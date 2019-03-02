package com.gotaxiride.passenger.home.submenu;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gotaxiride.passenger.GoTaxiApplication;
import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.api.ServiceGenerator;
import com.gotaxiride.passenger.api.service.UserService;
import com.gotaxiride.passenger.config.General;
import com.gotaxiride.passenger.home.MainActivity;
import com.gotaxiride.passenger.model.User;
import com.gotaxiride.passenger.model.json.user.TopupRequestJson;
import com.gotaxiride.passenger.model.json.user.TopupResponseJson;
import com.gotaxiride.passenger.utils.Log;
import com.gotaxiride.passenger.utils.Utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class TopUpActivity extends AppCompatActivity {

    private static final String TAG = TopUpActivity.class.getSimpleName();

    private static final int TAKE_PICTURE = 1;
    String bukti;
    TopUpActivity activity;
    @BindView(R.id.pemilikRekening)
    EditText name;
    @BindView(R.id.nomorRekening)
    EditText accountNumber;
    //    private Uri file;
    @BindView(R.id.nominalTransfer)
    EditText nominal;
    @BindView(R.id.spinBank)
    Spinner spinner;
    @BindView(R.id.butUploadBukti)
    TextView upload;
    @BindView(R.id.butTopup)
    TextView topup;
    @BindView(R.id.other_bank_layout)
    TextInputLayout otherBankLayout;
    @BindView(R.id.other_bank)
    EditText otherBank;
    private Uri imageUri;
    private String bankName = "";
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);
        ButterKnife.bind(this);

        if (General.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        realm = Realm.getDefaultInstance();
        activity = TopUpActivity.this;
        final User userLogin = GoTaxiApplication.getInstance(this).getLoginUser();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            upload.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

//                Toast.makeText(activity, "Index : "+i, Toast.LENGTH_SHORT).show();
                if (i != 3) {
                    bankName = spinner.getSelectedItem().toString();
                    otherBankLayout.setVisibility(GONE);
                } else {
                    otherBankLayout.setVisibility(VISIBLE);
                    otherBank.requestFocus();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        otherBank.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bankName = otherBank.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        nominal.addTextChangedListener(Utility.currencyTW(nominal));

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
//                take_photo();
            }
        });

        topup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (name.getText().toString().equals("")) {
                    Toast.makeText(TopUpActivity.this, "Please enter the account holder's name after the transfer!", Toast.LENGTH_LONG).show();
                } else {
                    if (accountNumber.getText().toString().equals("")) {
                        Toast.makeText(TopUpActivity.this, "Please enter your bank account number after transfer!", Toast.LENGTH_SHORT).show();
                    } else {
                        submitTopUp();
                    }
                }

            }
        });
    }


    private void submitTopUp() {
        final ProgressDialog pd = showLoading();

        User user = GoTaxiApplication.getInstance(this).getLoginUser();
        TopupRequestJson request = new TopupRequestJson();
        request.id = user.getId();
        request.atas_nama = name.getText().toString();
        request.no_rekening = accountNumber.getText().toString();
        request.jumlah = getNominal();
        request.bank = bankName;
        request.bukti = bukti;


        UserService service = ServiceGenerator.createService(UserService.class, user.getEmail(), user.getPassword());
        service.topUp(request).enqueue(new Callback<TopupResponseJson>() {
            @Override
            public void onResponse(Call<TopupResponseJson> call, Response<TopupResponseJson> response) {
                if (response.isSuccessful()) {
                    pd.dismiss();

                    if (response.body().message.equals("success")) {
                        topUp_success();
                    } else {
                        Toast.makeText(activity, "Problem verification ...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<TopupResponseJson> call, Throwable t) {
                t.printStackTrace();
                pd.dismiss();
                Toast.makeText(TopUpActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getNominal() {
        String originalString = nominal.getText().toString();

        Long longval;
        if (originalString.contains(".")) {
            originalString = originalString.replaceAll("[$.]", "");
        }
        if (originalString.contains(",")) {
            originalString = originalString.replaceAll(",", "");
        }
        if (originalString.contains("$ ")) {
            originalString = originalString.replaceAll("$ ", "");
        }
        if (originalString.contains("$")) {
            originalString = originalString.replaceAll("$", "");
        }
        if (originalString.contains("R")) {
            originalString = originalString.replaceAll("R", "");
        }
        if (originalString.contains("p")) {
            originalString = originalString.replaceAll("p", "");
        }
        if (originalString.contains(" ")) {
            originalString = originalString.replaceAll(" ", "");
        }

        return originalString;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                upload.setEnabled(true);
            }
        }
    }


    public void takePhoto() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Invoice_" + timeStamp;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), imageFileName);


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photo));
            imageUri = Uri.fromFile(photo);
        } else {
            File file = new File(photo.getPath());
            Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".fileProvider", file);
            imageUri = photoUri;
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        }

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {

                    activity.getContentResolver().notifyChange(imageUri, null);
                    ContentResolver cr = activity.getContentResolver();
                    Bitmap bitmap;
                    try {

                        bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, imageUri);
                        Log.d("after_comppres", String.valueOf(bitmap.getByteCount()));
                        bukti = compressJSON(bitmap);
                        if (!bukti.equals("")) {
                            ImageView centang = (ImageView) activity.findViewById(R.id.centang);
                            centang.setVisibility(VISIBLE);

                        }

                    } catch (Exception e) {
                        Toast.makeText(activity, "Failed to load", Toast.LENGTH_SHORT).show();
                        Log.e("Camera", e.toString());
                    }
                }
                break;
            default:
                break;
        }
    }


    private ProgressDialog showLoading() {
        ProgressDialog ad = ProgressDialog.show(activity, "", "Loading Data...", true);
        return ad;
    }

    public String compressJSON(Bitmap bmp) {
        byte[] imageBytes0;
        ByteArrayOutputStream baos0 = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos0);
        imageBytes0 = baos0.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes0, Base64.DEFAULT);
        return encodedImage;
    }


    public void topUp_success() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                TopUpActivity.this);
        alertDialog.setTitle("Top Up Wallet");
        alertDialog.setMessage("Thanks for doing Top Up wallet, Verification will be processed as soon as possible.");

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent home = new Intent(TopUpActivity.this, MainActivity.class);
                startActivity(home);
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }



}

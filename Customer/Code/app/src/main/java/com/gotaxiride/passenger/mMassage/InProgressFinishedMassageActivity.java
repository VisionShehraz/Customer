package com.gotaxiride.passenger.mMassage;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.gotaxiride.passenger.GoTaxiApplication;
import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.api.ServiceGenerator;
import com.gotaxiride.passenger.api.service.BookService;
import com.gotaxiride.passenger.config.General;
import com.gotaxiride.passenger.home.ChatActivity;
import com.gotaxiride.passenger.model.DetailTransaksiMassage;
import com.gotaxiride.passenger.model.ItemHistory;
import com.gotaxiride.passenger.model.User;
import com.gotaxiride.passenger.model.json.book.massage.DetailTransaksiRequest;
import com.gotaxiride.passenger.model.json.book.massage.DetailTransaksiResponse;
import com.gotaxiride.passenger.utils.Utils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Androgo on 12/31/2018.
 */

public class InProgressFinishedMassageActivity extends AppCompatActivity {

    public static final String IS_COMPLETED_ID = "IsCompleted";
    public static final String ITEM_HISTORY_ID = "ItemHistory";
    private static final int REQUEST_PERMISSION_CALL = 992;
    @BindView(R.id.inProgressFinishedMassage_cancel)
    Button cancelOrderButton;
    @BindView(R.id.inProgressFinishedMassage_driverImage)
    CircleImageView driverImage;
    @BindView(R.id.inProgressFinishedMassage_driverName)
    TextView driverName;
    @BindView(R.id.inProgressFinishedMassage_contactContainer)
    LinearLayout contactContainer;
    @BindView(R.id.inProgressFinishedMassage_phoneButton)
    ImageView phoneButton;
    @BindView(R.id.inProgressFinishedMassage_messageButton)
    ImageView messageButton;
    @BindView(R.id.inProgressFinishedMassage_dateTimeText)
    TextView dateTimeText;
    @BindView(R.id.inProgressFinishedMassage_locationText)
    TextView locationText;
    @BindView(R.id.inProgressFinishedMassage_massageTypeText)
    TextView massageType;
    @BindView(R.id.inProgressFinishedMassage_durationText)
    TextView durationText;
    @BindView(R.id.inProgressFinishedMassage_costText)
    TextView costText;
    @BindView(R.id.inProgressFinishedMassage_statusText)
    TextView statusText;
    private boolean isFinishedTransaction;
    private User loginUser;
    private BookService service;

    private ItemHistory itemHistory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inprogress_finished_massage);
        ButterKnife.bind(this);

        isFinishedTransaction = getIntent().getBooleanExtra(IS_COMPLETED_ID, false);
        itemHistory = (ItemHistory) getIntent().getSerializableExtra(ITEM_HISTORY_ID);

        if (isFinishedTransaction) setLayoutFinishedTransaction();
        else setLayoutInProgressTransaction();

        Realm realm = Realm.getDefaultInstance();

        loginUser = realm.copyFromRealm(GoTaxiApplication.getInstance(this).getLoginUser());
        service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());

        DetailTransaksiRequest param = new DetailTransaksiRequest();
        param.setIdTransaksi(itemHistory.id_transaksi);

        updateDriver(itemHistory);

        service.getDetailTransaksiMassage(param).enqueue(new Callback<DetailTransaksiResponse>() {
            @Override
            public void onResponse(Call<DetailTransaksiResponse> call, Response<DetailTransaksiResponse> response) {
                if (response.isSuccessful()) {
                    updateUI(response.body().getDataTransaksi().get(0));
                } else {
                    Toast.makeText(getApplicationContext(), "Please reload the detail screen.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<DetailTransaksiResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Please reload the detail screen.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatWithDriver();
            }
        });

        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callDriver();
            }
        });
    }


    private void callDriver() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Call driver");
        alertDialogBuilder.setMessage("Do you want to call " + itemHistory.no_telepon + "?");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (ActivityCompat.checkSelfPermission(InProgressFinishedMassageActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(InProgressFinishedMassageActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PERMISSION_CALL);
                            return;
                        }

                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + itemHistory.no_telepon));
                        startActivity(callIntent);
                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void chatWithDriver() {
        Toast.makeText(this, "Chat with driver", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("reg_id", itemHistory.reg_id);
        startActivity(intent);
    }

    private void updateDriver(ItemHistory item) {
        driverName.setText(itemHistory.nama_depan_driver + " " + itemHistory.nama_belakang_driver);
        Glide.with(this).load(itemHistory.foto).into(driverImage);
    }

    private void updateUI(DetailTransaksiMassage response) {
        SimpleDateFormat sdfFrom = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat sdfTo = new SimpleDateFormat("dd MM yyyy", Locale.US);

        String formattedDate;

        try {
            formattedDate = sdfTo.format(sdfFrom.parse(response.getTanggalPelayanan()));
        } catch (ParseException e) {
            e.printStackTrace();
            formattedDate = "";
        }

        String formattedDateTime = formattedDate + " " + response.getJamPelayanan();
        dateTimeText.setText(formattedDateTime);
        locationText.setText(response.getAlamatAsal());
        massageType.setText(Utils.toTitleCase(response.getMassageMenu()));
        durationText.setText(response.getLamaPelayanan() + " min");

        String formattedPrice = String.format(Locale.US, General.MONEY +" %s.00",
                NumberFormat.getNumberInstance(Locale.US).format(Integer.valueOf(response.getHarga())));
        costText.setText(formattedPrice);
        statusText.setText(Utils.toTitleCase(response.getStatusTransaksi()));

    }


    private void setLayoutFinishedTransaction() {
        contactContainer.setVisibility(View.GONE);
        cancelOrderButton.setVisibility(View.GONE);
    }

    private void setLayoutInProgressTransaction() {
        contactContainer.setVisibility(View.VISIBLE);
        cancelOrderButton.setVisibility(View.VISIBLE);
    }
}

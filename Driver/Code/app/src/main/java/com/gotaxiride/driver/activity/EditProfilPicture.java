package com.gotaxiride.driver.activity;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gotaxiride.driver.R;
import com.gotaxiride.driver.database.DBHandler;
import com.gotaxiride.driver.database.Queries;
import com.gotaxiride.driver.model.Driver;
import com.gotaxiride.driver.network.HTTPHelper;
import com.gotaxiride.driver.network.Log;
import com.gotaxiride.driver.network.NetworkActionResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

//import net.gumcode.drivermangjek.preference.UserPreference;

public class EditProfilPicture extends AppCompatActivity {

    private static int RESULT_LOAD_IMG = 1;
    int maxRetry = 4;
    private TextView pick_image;
    private ImageView image;
    private Bitmap bitmap;
    private Driver driver;
    private Button submit;
    private byte[] imageBytes0;
    private MaterialDialog md;
    private EditProfilPicture activity;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_image_profile);

        Queries que = new Queries(new DBHandler(this));
        driver = que.getDriver();
        que.closeDatabase();

        activity = EditProfilPicture.this;
        initView();
        loadImageFromStorage();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imgDecodableString = cursor.getString(columnIndex);

            bitmap = decodeFile(imgDecodableString, 200);
            Log.d("after_comppress", String.valueOf(bitmap.getByteCount()));
            image.setImageBitmap(bitmap);
            pick_image.setText("Image Selected");
        }
    }

    private Bitmap decodeFile(final String path, final int thumbnailSize) {

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, o);
        if ((o.outWidth == -1) || (o.outHeight == -1)) {
            bitmap = null;
        }

        int originalSize = (o.outHeight > o.outWidth) ? o.outHeight
                : o.outWidth;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = originalSize / thumbnailSize;
        bitmap = BitmapFactory.decodeFile(path, opts);
        return bitmap;
    }

    private boolean deleteImageFromStorage() {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("fotoDriver", Context.MODE_PRIVATE);
        File f = new File(directory, "profile.jpg");
        boolean del = f.delete();
//        Log.d("isDel", String.valueOf(del));
        return del;
    }

    public String compressJSON(Bitmap bmp) {
        ByteArrayOutputStream baos0 = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos0);
        imageBytes0 = baos0.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes0, Base64.DEFAULT);
        return encodedImage;
    }

    public void initView() {

        submit = (Button) findViewById(R.id.saveImage);
        pick_image = (TextView) findViewById(R.id.pick_image);
        image = (ImageView) findViewById(R.id.imageProfile);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pick_image.getText().toString().equals("")) {

                    String cop = compressJSON(bitmap);
                    updateFotoku(cop);
                } else {
                    Toast.makeText(getApplicationContext(), "Empty Image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        pick_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("pick", "OK");
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    submit.setEnabled(false);
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }

                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMG);
            }
        });
    }

    private boolean saveToInternalStorage(Bitmap bitmapImage) {

        deleteImageFromStorage();

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("fotoDriver", Context.MODE_PRIVATE);
        String nameFoto = "profile.jpg";
        File mypath = new File(directory, nameFoto);
//        Log.d("foto_name", nameFoto);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 50, fos);
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void loadImageFromStorage() {
        if (!driver.image.equals("")) {
            ContextWrapper cw = new ContextWrapper(EditProfilPicture.this);
            File directory = cw.getDir("fotoDriver", Context.MODE_PRIVATE);
            File f = new File(directory, "profile.jpg");
            Bitmap tryDec = decodeFile(f);
            image.setImageBitmap(tryDec);
        }
    }

    private MaterialDialog showLoading() {
        return new MaterialDialog.Builder(EditProfilPicture.this)
                .title("Loading")
                .content("Updating data")
                .widgetColorRes(R.color.blue)
                .progress(true, 0)
                .cancelable(false)
                .show();

    }

    private Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            final int REQUIRED_SIZE = 200;
            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    private void updateFotoku(final String foto) {
        final MaterialDialog md1 = showLoading();
        JSONObject jFoto = new JSONObject();
        try {
            jFoto.put("email", driver.email);
            jFoto.put("id", driver.id);
            jFoto.put("whatUpd", "foto");
            jFoto.put("value", foto);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.d("Update_fotoku", jFoto.toString());
        HTTPHelper.getInstance(activity).updateProfile(jFoto, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                try {
                    if (obj.getString("message").equals("success")) {
                        Toast.makeText(activity, "Photo saved successfully", Toast.LENGTH_SHORT).show();
                        saveToInternalStorage(bitmap);
                        finish();
                    } else {
                        Toast.makeText(activity, "Upload photo failed", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                md1.dismiss();
                maxRetry = 4;
            }

            @Override
            public void onFailure(String message) {
                md.dismiss();
            }

            @Override
            public void onError(String message) {

                if (maxRetry == 0) {
                    md1.dismiss();
                    Toast.makeText(activity, "Connection is problem", Toast.LENGTH_SHORT).show();
                    maxRetry = 4;
                } else {
                    updateFotoku(foto);
                    maxRetry--;
                    Log.d("Try_again_upload_photo", String.valueOf(maxRetry));
                    md1.dismiss();
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                submit.setEnabled(true);
            }
        }
    }


}

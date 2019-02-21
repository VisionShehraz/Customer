package com.gotaxiride.driver.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.gotaxiride.driver.service.MyConfig;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.gotaxiride.driver.BuildConfig;
import com.gotaxiride.driver.MainActivity;
import com.gotaxiride.driver.R;
import com.gotaxiride.driver.database.DBHandler;
import com.gotaxiride.driver.database.Queries;
import com.gotaxiride.driver.model.Driver;
import com.gotaxiride.driver.network.AppController;
import com.gotaxiride.driver.network.HTTPHelper;
import com.gotaxiride.driver.network.Log;
import com.gotaxiride.driver.network.NetworkActionResult;
import com.gotaxiride.driver.preference.SettingPreference;
import com.gotaxiride.driver.preference.UserPreference;
import com.gotaxiride.driver.service.MyFirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;


public class LoginActivity extends AppCompatActivity {

    Button login;
    Button forgot;
    EditText email, password;
    LoginActivity activity;
    Driver user;
    //    UserPreference userPreference;
    Intent locSev;
    String token = "";
    FirebaseInstanceId fireId;
    int maxRetry = 4;

    LinearLayout register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        activity = LoginActivity.this;
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        locSev = new Intent(this, MyFirebaseInstanceIdService.class);
        startService(locSev);

//        userPreference = new UserPreference(activity);
        login = (Button) findViewById(R.id.loginButton);
        register = (LinearLayout) findViewById(R.id.signUpButton);
        forgot = (Button) findViewById(R.id.forgot);

        new SettingPreference(this).insertSetting(new String[]{"OFF", "0", "OFF", String.valueOf(BuildConfig.VERSION_CODE)});


//        Log.d("NewToken", token);
//        Log.d("NewToken", userPreference.getDriver().gcm_id);

        ImageView clear = (ImageView) findViewById(R.id.clearEmail);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email.setText("");
            }
        });


            forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent reset = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(reset);
      }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fireId = FirebaseInstanceId.getInstance();
                if (fireId.getToken() != null)
                    token = fireId.getToken();

                if (token.equals("")) {
                    Toast.makeText(activity, "Waiting for connection..", Toast.LENGTH_SHORT).show();
//                    showWarning();
                } else {
                    if (email.getText().toString().equals("") || password.getText().toString().equals("")) {
                        Toast.makeText(activity, "Please complete the input form", Toast.LENGTH_SHORT).show();
                    } else {
//                        if(password.getText().toString().length() < 7){
//                            Toast.makeText(activity, "Minimum password 6 karakter", Toast.LENGTH_SHORT).show();
//                        }
                        signin(token);
                    }
                }

            }
        });

    register.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(MyConfig.URL_WEB + "index.php/c_utama/join")));
       // Intent register = new Intent(LoginActivity.this, BrowserActivity.class);
      //  startActivity(register);

        }
    });


    }


    private void signin(final String token) {
        final JSONObject logData = new JSONObject();
        try {
//            logData.put("email", email.getText().toString());
            logData.put("no_telepon", email.getText().toString());
            logData.put("password", password.getText().toString());
            logData.put("reg_id", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        Log.d("login_data", logData.toString());
        final ProgressDialog pd = showLoading();
        HTTPHelper.getInstance(activity).login(logData, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                Log.d("login_data", obj.toString());
                try {
                    if (obj.getString("message").equals("found")) {
                        pd.dismiss();
                        user = HTTPHelper.parseUserJSONData(activity, obj.toString());
                        user.gcm_id = token;
                        user.password = password.getText().toString();
                        Queries que = new Queries(new DBHandler(activity));
                        que.insertDriver(user);
                        que.closeDatabase();
                        new UserPreference(activity).insertDriver(user);
                        if (loadImageFromServer(user.image)) {
                            FirebaseMessaging.getInstance().subscribeToTopic("info");
                            Intent change = new Intent(activity, MainActivity.class);
                            startActivity(change);
                            finish();
                        }
                    } else if (obj.getString("message").equals("banned")) {
                        pd.dismiss();
                        showWarning("There is a problem with your account please contact CS");
                    } else {
                        pd.dismiss();
                        showWarning("Invalid mobile or password number");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                maxRetry = 4;
            }

            @Override
            public void onFailure(String message) {

            }

            @Override
            public void onError(String message) {
//                Log.d("Pesan_error", message);
                if (maxRetry == 0) {
                    pd.dismiss();
                    showWarning();
                    maxRetry = 4;
                } else {
                    signin(token);
                    maxRetry--;
                    Log.d("Try_again_login", String.valueOf(maxRetry));
                    pd.dismiss();
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(locSev);
    }

    private ProgressDialog showLoading() {
        ProgressDialog ad = ProgressDialog.show(activity, "", "Connected to Server...", true);
        return ad;
    }


    private MaterialDialog showWarning() {
        final MaterialDialog md = new MaterialDialog.Builder(activity)
                .title(R.string.Connection_problem)
                .content(R.string.try_again)
                .icon(new IconicsDrawable(activity)
                        .icon(FontAwesome.Icon.faw_exclamation_triangle)
                        .color(Color.YELLOW)
                        .sizeDp(24))
                .positiveText("Close")
                .positiveColor(Color.DKGRAY)
                .show();

        View positive = md.getActionButton(DialogAction.POSITIVE);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md.dismiss();
            }
        });
        return md;
    }

    private MaterialDialog showWarning(String content) {
        final MaterialDialog md = new MaterialDialog.Builder(activity)
                .title(R.string.Login_Failed)
                .content(content)
                .icon(new IconicsDrawable(activity)
                        .icon(FontAwesome.Icon.faw_exclamation_triangle)
                        .color(Color.RED)
                        .sizeDp(20))
                .positiveText(R.string.Close)
                .positiveColor(Color.DKGRAY)
                .show();

        View positive = md.getActionButton(DialogAction.POSITIVE);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md.dismiss();
            }
        });
        return md;
    }

    private boolean loadImageFromServer(String image) {
        final ProgressDialog pd = showLoading();
        ImageLoader imageLoader = AppController.getInstance(this).getImageLoader();
        imageLoader.get(image, new ImageLoader.ImageListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.d("Image Load Error: ", error.getMessage());
                pd.dismiss();
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                if (response.getBitmap() != null) {
                    Bitmap circleBitmap = response.getBitmap();
                    saveToInternalStorage(circleBitmap);
                }
                pd.dismiss();
            }
        });
        return true;
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("fotoDriver", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, "profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 50, fos);
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }
}

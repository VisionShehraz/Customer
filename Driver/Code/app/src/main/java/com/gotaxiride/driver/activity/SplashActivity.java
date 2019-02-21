package com.gotaxiride.driver.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.iconics.IconicsDrawable;
import com.gotaxiride.driver.MainActivity;
import com.gotaxiride.driver.network.HTTPHelper;
import com.gotaxiride.driver.network.Log;
import com.gotaxiride.driver.network.NetworkActionResult;
import com.gotaxiride.driver.preference.SettingPreference;
import com.gotaxiride.driver.preference.UserPreference;
import com.gotaxiride.driver.BuildConfig;
import com.gotaxiride.driver.R;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {

    UserPreference userPreference;
    TextView versionName1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        String versionName = BuildConfig.VERSION_NAME;
        versionName1 = (TextView) findViewById(R.id.versiName);
        versionName1.setText("DRIVER Version " + versionName);

        userPreference = new UserPreference(this);
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        Log.d("AppPackage", appPackageName);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SettingPreference sp = new SettingPreference(SplashActivity.this);
                sp.updateVersions(String.valueOf(BuildConfig.VERSION_CODE));
                if (new UserPreference(SplashActivity.this).getDriver().phone.equals("")) {
                    sp.insertSetting(new String[]{"OFF", "0", "OFF", String.valueOf(BuildConfig.VERSION_CODE)});
                    checkVersion(false);
                } else {
                    checkVersion(true);
                }
            }
        }, 1000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        stopService(locSev);
    }

    private void checkVersion(final boolean lanjut) {
        JSONObject jVer = new JSONObject();
        try {
            int versionCode = BuildConfig.VERSION_CODE;

            jVer.put("version", versionCode);
            jVer.put("application", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("Version", jVer.toString());
        HTTPHelper.getInstance(this).checkVersionApp(jVer, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                try {
                    if (obj.getString("new_version").equals("yes")) {
                        showPopupUpdate();
                    } else if (obj.getString("new_version").equals("hold")) {
                        showPopupHold(obj.getString("message"));
                    } else {
                        if (lanjut) {
                            Intent change = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(change);
                            finish();
                        } else {
                            Intent change = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(change);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onError(String message) {
                if (lanjut) {
                    Intent change = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(change);
                    finish();
                } else {
                    Intent change = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(change);
                    finish();
                }
            }
        });
    }

    private MaterialDialog showPopupUpdate() {
        final MaterialDialog md = new MaterialDialog.Builder(this)
                .title("Good news")
                .content("The latest version of app is available. Click 'Update' to update Apps!")
                .icon(new IconicsDrawable(this)
                        .color(Color.BLUE)
                        .sizeDp(24))
                .positiveText("Update")
                .negativeText("Cancel")
                .cancelable(false)
                .positiveColor(Color.BLUE)
                .negativeColor(Color.RED)
                .show();

        View positive = md.getActionButton(DialogAction.POSITIVE);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md.dismiss();
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                Log.d("AppPackage", appPackageName);
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                finish();
            }
        });

        View negative = md.getActionButton(DialogAction.NEGATIVE);
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md.dismiss();
                finish();

            }
        });


        return md;
    }

    private MaterialDialog showPopupHold(String message) {
        final MaterialDialog md = new MaterialDialog.Builder(this)
                .title("Notice")
                .content(message)
                .icon(new IconicsDrawable(this)
                        .color(Color.BLUE)
                        .sizeDp(24))
                .positiveText("Close")
                .cancelable(false)
                .positiveColor(Color.DKGRAY)
                .show();

        View positive = md.getActionButton(DialogAction.POSITIVE);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md.dismiss();
                Intent toMain = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(toMain);
                finish();
            }
        });
        return md;
    }
}

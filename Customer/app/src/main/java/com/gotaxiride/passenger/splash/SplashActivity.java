package com.gotaxiride.passenger.splash;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import com.gotaxiride.passenger.BuildConfig;
import com.gotaxiride.passenger.GoTaxiApplication;
import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.api.ServiceGenerator;
import com.gotaxiride.passenger.api.service.UserService;
import com.gotaxiride.passenger.home.MainActivity;
import com.gotaxiride.passenger.model.User;
import com.gotaxiride.passenger.model.json.menu.VersionRequestJson;
import com.gotaxiride.passenger.model.json.menu.VersionResponseJson;
import com.gotaxiride.passenger.signIn.SignInActivity;
import com.gotaxiride.passenger.utils.Log;

import java.net.URI;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SplashActivity extends AppCompatActivity {
    VideoView video;
    TextView VersionName;
    @BindView(R.id.progressBarSplash)
    ProgressBar progressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

      /*  video=(VideoView)findViewById(R.id.splash_video);
        String vid="android.resources://com.gotaxiride.passenger.splash"+R.raw.splash;
        Uri uri=Uri.parse(vid);
        video.setVideoURI(uri);
        video.start();
*/
        video = (VideoView)this.findViewById(R.id.splash_video);
        String path = "android.resource://" + getPackageName() + "/" + R.raw.splash;
        //MediaController mc = new MediaController(this);
        Uri uri=Uri.parse(path);

        //video.setMediaController(mc);

        video.setVideoURI(uri);

video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
    @Override
    public void onCompletion(MediaPlayer mp) {


        PackageInfo pInfo;
        VersionRequestJson request = new VersionRequestJson();
        VersionName = (TextView) findViewById(R.id.VersionName);
        String version = BuildConfig.VERSION_NAME;
        VersionName.setText("Version " + version);

        int versiterbaru = BuildConfig.VERSION_CODE;
        request.version = String.valueOf(versiterbaru);
        request.application = "0";
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            request.version = pInfo.versionCode + "";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }



        UserService service = ServiceGenerator.createService(UserService.class, null, null);
        service.checkVersion(request).enqueue(new Callback<VersionResponseJson>() {
            @Override
            public void onResponse(Call<VersionResponseJson> call, Response<VersionResponseJson> response) {
                if (response.isSuccessful()) {

                    if (response.body().new_version.equals("yes")) {
                        showPopupUpdate(response.body().message);
                    }else if (response.body().new_version.equals("no")) {
                        start();
                    }
                }else {

                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashActivity.this);
                    alertDialogBuilder.setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int arg1) {
                                    dialog.dismiss();
                                    start();
                                }
                            });

                    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            start();
                        }
                    });
                    alertDialogBuilder.setMessage(response.body().message);
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<VersionResponseJson> call, Throwable t) {
                t.printStackTrace();
                showPopupHold("Problems when loading apps");
                progressBar.setVisibility(View.GONE);
                Log.e("System error:", t.getLocalizedMessage());

            }
        });



    }}); //media file function close


        video.start();
    }


    private MaterialDialog showPopupUpdate(String message) {
        final MaterialDialog md = new MaterialDialog.Builder(this)
                .title("New Apps Available")
                .content(message)
                .icon(new IconicsDrawable(this)
                        .icon(FontAwesome.Icon.faw_google)
                        .color(Color.RED)
                        .sizeDp(24))
                .positiveText(R.string.update_now)
                .negativeText(R.string.text_cancel)
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
                .title("Notification")
                .content(message)
                .icon(new IconicsDrawable(this)
                        .icon(FontAwesome.Icon.faw_android)
                        .color(Color.BLUE)
                        .sizeDp(24))
                .positiveText("OK")
                .negativeText(R.string.no)
                .cancelable(false)
                .positiveColor(Color.BLUE)
                .negativeColor(Color.RED)
                .show();

        View positive = md.getActionButton(DialogAction.POSITIVE);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md.dismiss();
                start();
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


    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void start() {

        progressBar.setVisibility(View.GONE);
        User user = GoTaxiApplication.getInstance(this).getLoginUser();
        Intent intent;

        if (user != null) {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        }
         else {
            intent = new Intent(SplashActivity.this, SignInActivity.class);
              }
        startActivity(intent);




    }

}

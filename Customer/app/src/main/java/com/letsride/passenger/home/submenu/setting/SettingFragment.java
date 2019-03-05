package com.letsride.passenger.home.submenu.setting;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letsride.passenger.GoTaxiApplication;
import com.letsride.passenger.R;
import com.letsride.passenger.config.General;
import com.letsride.passenger.model.User;
import com.letsride.passenger.splash.SplashActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by Androgo on 10/30/2018.
 */

public class SettingFragment extends Fragment {

    @BindView(R.id.setting_name)
    TextView settingProfileName;

    @BindView(R.id.setting_email)
    TextView settingProfileEmail;

    @BindView(R.id.setting_phone)
    TextView settingProfilePhone;

    @BindView(R.id.setting_editProfile)
    Button settingEditProfile;

    @BindView(R.id.setting_changePassword)
    LinearLayout settingChangePassword;

    @BindView(R.id.setting_termOfService)
    LinearLayout settingTermOfService;

    @BindView(R.id.setting_privacyPolicy)
    LinearLayout settingPrivacyPolicy;

    @BindView(R.id.setting_faq)
    LinearLayout settingFaq;

    @BindView(R.id.setting_share)
    LinearLayout settingShare;

    @BindView(R.id.setting_sos)
    LinearLayout sos;


    @BindView(R.id.setting_rateThisApps)
    LinearLayout settingRateThisApps;

    @BindView(R.id.daftar_mitra)
    LinearLayout partnerRegister;

    @BindView(R.id.setting_logout)
    RelativeLayout settingLogout;
    private static final int REQUEST_PERMISSION_CALL = 992;

    private User loginUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);


        settingEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), UpdateProfileActivity.class));
            }
        });

        settingChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ChangePasswordActivity.class));
            }
        });

        settingTermOfService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TermOfServiceActivity.class));
            }
        });

        settingPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PrivacyPolicyActivity.class));
            }
        });

        settingRateThisApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = getActivity().getPackageName();

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        settingFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), FAQActivity.class));
            }
        });

        settingShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appPackageName = "com.gotaxiride.passenger";
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
                String message = "\nYou can download the GO-TAXI Apps at: \n" + Uri.parse("http://market.android.com/details?id=" + appPackageName) +"\n";

                i.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(i, "Choose One"));
            }
        });



        partnerRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appPackageName = "com.gotaxiride.driver";
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            }
        });

        settingLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm realm = GoTaxiApplication.getInstance(getContext()).getRealmInstance();
                realm.beginTransaction();
                realm.delete(User.class);
                realm.commitTransaction();
                GoTaxiApplication.getInstance(getContext()).setLoginUser(null);
                startActivity(new Intent(getContext(), SplashActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle(R.string.call_sos);
                alertDialogBuilder.setMessage(R.string.call_sos_now);
                alertDialogBuilder.setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PERMISSION_CALL);
                                    return;
                                }

                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + General.NUMBER_SOS));
                                startActivity(callIntent);
                            }
                        });

                alertDialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loginUser = GoTaxiApplication.getInstance(getActivity()).getLoginUser();
        settingProfileName.setText(String.format("%s %s", loginUser.getNamaDepan(), loginUser.getNamaBelakang()));
        settingProfileEmail.setText(loginUser.getEmail());
        settingProfilePhone.setText(loginUser.getNoTelepon());
    }
}

package com.gotaxiride.driver.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.gotaxiride.driver.MainActivity;
import com.gotaxiride.driver.R;
import com.gotaxiride.driver.activity.EditProfilPicture;
import com.gotaxiride.driver.activity.EditSettingActivity;
import com.gotaxiride.driver.activity.EditSettingVehicleActivity;
import com.gotaxiride.driver.activity.LoginActivity;
import com.gotaxiride.driver.database.DBHandler;
import com.gotaxiride.driver.database.Queries;
import com.gotaxiride.driver.model.Driver;
import com.gotaxiride.driver.network.HTTPHelper;
import com.gotaxiride.driver.network.Log;
import com.gotaxiride.driver.network.NetworkActionResult;
import com.gotaxiride.driver.preference.VehiclePreference;
import com.gotaxiride.driver.preference.SettingPreference;
import com.gotaxiride.driver.preference.UserPreference;
import com.gotaxiride.driver.service.LocationService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

//import net.gumcode.drivermangjek.preference.UserPreference;


public class SettingFragment extends Fragment {
    private static final String TAG = SettingFragment.class.getSimpleName();
    MainActivity activity;
    Driver driver;
    CircularImageView imageFoto;
    Queries que;
    int maxRetry = 4;
    private View rootView;

    public SettingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.content_setting, container, false);

        activity = (MainActivity) getActivity();
        que = new Queries(new DBHandler(activity));
        driver = que.getDriver();
        activity.getSupportActionBar().setTitle(R.string.profile);
        initView();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initView() {
        LinearLayout editFoto;
        LinearLayout editNoTelp, editNama, editEmail, editPassword, linKendaraan, linLogout, editRekening;
        TextView nama, email, password, textNumber;

        editFoto = (LinearLayout) rootView.findViewById(R.id.editFoto);
        editNoTelp = (LinearLayout) rootView.findViewById(R.id.editNomorTelepon);
        editNama = (LinearLayout) rootView.findViewById(R.id.editNama);
        editPassword = (LinearLayout) rootView.findViewById(R.id.editPassword);
        editEmail = (LinearLayout) rootView.findViewById(R.id.editEmail);
        editRekening = (LinearLayout) rootView.findViewById(R.id.editRekening);
        linLogout = (LinearLayout) rootView.findViewById(R.id.linLogout);
        linKendaraan = (LinearLayout) rootView.findViewById(R.id.editKendaraan);
        imageFoto = (CircularImageView) rootView.findViewById(R.id.imageProfile);

        nama = (TextView) rootView.findViewById(R.id.textNama);
        email = (TextView) rootView.findViewById(R.id.textEmail);
        password = (TextView) rootView.findViewById(R.id.textPassword);
        textNumber = (TextView) rootView.findViewById(R.id.textNumber);

        switch (driver.job) {
            case 3:
                linKendaraan.setVisibility(View.GONE);
                break;
        }


        nama.setText(driver.name);
        email.setText(driver.email);
        String a = "";
        for (int i = 0; i < driver.password.length() - 3; i++) {
            a += "*";
        }
        password.setText(driver.password.substring(0, 3) + a);
        textNumber.setText(driver.phone);

        loadImageFromStorage(imageFoto);
        imageFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toEditFoto = new Intent(activity, EditProfilPicture.class);
                startActivity(toEditFoto);
            }
        });
        editNoTelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent toEdit = new Intent(activity, EditSettingActivity.class);
//                toEdit.putExtra("edit", "nomor");
//                startActivity(toEdit);
            }
        });
        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toEdit = new Intent(activity, EditSettingActivity.class);
                toEdit.putExtra("edit", "email");
                startActivity(toEdit);
            }
        });
        editNama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(activity, "Editting nama..", Toast.LENGTH_SHORT).show();
            }
        });
        editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toEdit = new Intent(activity, EditSettingActivity.class);
                toEdit.putExtra("edit", "password");
                startActivity(toEdit);
            }
        });
        editRekening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toEdit = new Intent(activity, EditSettingActivity.class);
                toEdit.putExtra("edit", "rekening");
                startActivity(toEdit);
            }
        });
        linKendaraan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toEdit = new Intent(activity, EditSettingVehicleActivity.class);
                startActivity(toEdit);
            }
        });
        linLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWarnLogout();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (imageFoto != null)
            loadImageFromStorage(imageFoto);
    }

    public void changeFragment(Fragment frag, boolean addToBackStack) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container_body, frag);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    private ProgressDialog showLoading() {
        ProgressDialog ad = ProgressDialog.show(activity, "", "Loading...", true);
        return ad;
    }

    private MaterialDialog showWarnLogout() {
        final MaterialDialog md = new MaterialDialog.Builder(activity)
                .title(R.string.Warning)
                .content("Are you sure you want to Logout?")
                .icon(new IconicsDrawable(activity)
                        .icon(FontAwesome.Icon.faw_exclamation_triangle)
                        .color(Color.RED)
                        .sizeDp(24))
                .positiveText("Yes")
                .positiveColor(Color.BLUE)
                .negativeColor(Color.DKGRAY)
                .negativeText("No")
                .show();

        View positive = md.getActionButton(DialogAction.POSITIVE);
        View negative = md.getActionButton(DialogAction.NEGATIVE);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md.dismiss();
                doLogout();

            }
        });
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md.dismiss();
            }
        });

        return md;
    }

    private void doLogout() {
        JSONObject jLog = new JSONObject();
        try {
            jLog.put("id", driver.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog pd = showLoading();
        HTTPHelper.getInstance(activity).logout(jLog, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                try {

                    if (obj.getString("message").equals("success")) {
                        new UserPreference(activity).logout();
                        new VehiclePreference(activity).delete();
                        new SettingPreference(activity).logout();
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("info");
                        LocationService service = new LocationService();
                        service.stopLocationUpdates();
                        que.truncate(DBHandler.TABLE_DRIVER);
                        Intent serv = new Intent(activity, LocationService.class);
                        activity.stopService(serv);


                        Intent toLogin = new Intent(activity, LoginActivity.class);
                        startActivity(toLogin);
                        activity.finish();
                    } else {
                        Toast.makeText(activity, "Logout failed", Toast.LENGTH_SHORT).show();
                    }
                    pd.dismiss();
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
                if (maxRetry == 0) {
                    pd.dismiss();
                    Toast.makeText(activity, "Connection is problem", Toast.LENGTH_SHORT).show();
                    maxRetry = 4;
                } else {
                    doLogout();
                    maxRetry--;
                    Log.d("Try_ke_logout", String.valueOf(maxRetry));
                    pd.dismiss();
                }
            }
        });

    }

    private void loadImageFromStorage(CircularImageView civ) {
        if (!driver.image.equals("")) {
            ContextWrapper cw = new ContextWrapper(activity);
            File directory = cw.getDir("fotoDriver", Context.MODE_PRIVATE);
            File f = new File(directory, "profile.jpg");
            Bitmap circleBitmap = decodeFile(f);
            civ.setImageBitmap(circleBitmap);
        }
    }

    private Bitmap decodeFile(File f) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            final int REQUIRED_SIZE = 200;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        que.closeDatabase();
    }
}

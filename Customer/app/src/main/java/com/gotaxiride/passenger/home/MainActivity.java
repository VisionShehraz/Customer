package com.gotaxiride.passenger.home;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.gotaxiride.passenger.config.General;
import com.gotaxiride.passenger.utils.AppBarLayoutBehavior;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import com.gotaxiride.passenger.GoTaxiApplication;
import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.api.ServiceGenerator;
import com.gotaxiride.passenger.api.service.UserService;
import com.gotaxiride.passenger.home.submenu.help.HelpFragment;
import com.gotaxiride.passenger.home.submenu.history.HistoryFragment;
import com.gotaxiride.passenger.home.submenu.home.HomeFragment;
import com.gotaxiride.passenger.home.submenu.setting.SettingFragment;
import com.gotaxiride.passenger.model.DiskonMpay;
import com.gotaxiride.passenger.model.Fitur;
import com.gotaxiride.passenger.model.MfoodMitra;
import com.gotaxiride.passenger.model.User;
import com.gotaxiride.passenger.model.json.user.GetFiturResponseJson;
import com.gotaxiride.passenger.utils.GoTaxiTabProvider;
import com.gotaxiride.passenger.utils.MenuSelector;
import com.gotaxiride.passenger.utils.SnackbarController;
import com.gotaxiride.passenger.utils.view.CustomViewPager;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Androgo on 10/10/2018.
 */

public class MainActivity extends AppCompatActivity implements SnackbarController {

    @BindView(R.id.main_container)
    LinearLayout mainLayout;

    @BindView(R.id.main_tabLayout)
    SmartTabLayout mainTabLayout;

    @BindView(R.id.main_viewPager)
    CustomViewPager mainViewPager;
    boolean doubleBackToExitPressedOnce = false;
    private Snackbar snackBar;
    private MenuSelector selector;
    private SmartTabLayout.TabProvider tabProvider;
    private FragmentPagerItemAdapter adapter;
    private Locale locale;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupTabLayoutViewPager();

        if (General.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }


        // change language
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Configuration config = getBaseContext().getResources().getConfiguration();
        String lang = settings.getString("LANG", "");
        if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {
            locale = new Locale(lang);
            Locale.setDefault(locale);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }

        findViewById(R.id.languange).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                showChangeLangDialog();
            }
        });


    }


    public void showChangeLangDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.language_dialog, null);
        dialogBuilder.setView(dialogView);

        final Spinner spinner1 = (Spinner) dialogView.findViewById(R.id.spinner1);

        dialogBuilder.setTitle(getResources().getString(R.string.lang_dialog_title));
        dialogBuilder.setMessage(getResources().getString(R.string.lang_dialog_message));
        dialogBuilder.setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int langpos = spinner1.getSelectedItemPosition();
                changeLang(langpos);
            }
        });
        dialogBuilder.setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    void changeLang(int langpos){
        switch (langpos) {
            case 0: //Arabic
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "ar").commit();
                setLangRecreate("ar");

                return;
            case 1: //Spanish
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "es").commit();
                setLangRecreate("es");
                return;
            case 2: //French
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "fr").commit();
                setLangRecreate("fr");
                return;
            case 3: //Portuguese
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "pt").commit();
                setLangRecreate("pt");
                return;
            case 4: //Indonesian
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "in").commit();
                setLangRecreate("in");
                return;
            default: //By default set to english
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "en").commit();
                setLangRecreate("en");
                return;
        }
    }

    public void setLangRecreate(String langval) {
        Configuration config = getBaseContext().getResources().getConfiguration();
        locale = new Locale(langval);
        Locale.setDefault(locale);
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        recreate();
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.press_again_to_exit, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }


    private void setupTabLayoutViewPager() {
        tabProvider = new GoTaxiTabProvider(this);
        selector = (MenuSelector) tabProvider;
        mainTabLayout.setCustomTabView(tabProvider);

        adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(R.string.main_menuHome, HomeFragment.class)
                .add(R.string.main_menuHistory, HistoryFragment.class)
                .add(R.string.main_menuHelp, HelpFragment.class)
                .add(R.string.main_menuSetting, SettingFragment.class)
                .create());
        mainViewPager.setAdapter(adapter);
        mainTabLayout.setViewPager(mainViewPager);
        mainViewPager.setPagingEnabled(false);

        mainTabLayout.setOnTabClickListener(new SmartTabLayout.OnTabClickListener() {
            @Override
            public void onTabClicked(int position) {
                selector.selectMenu(position);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateFeature();
    }

    @Override
    public void showSnackbar(@StringRes int stringRes, int duration, @StringRes int actionResText, View.OnClickListener onClickListener) {
        snackBar = Snackbar.make(mainLayout, stringRes, duration);
        if (actionResText != -1 && onClickListener != null) {
            snackBar.setAction(actionResText, onClickListener)
                    .setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        snackBar.show();
    }

        private void updateFeature() {
        User loginUser = GoTaxiApplication.getInstance(this).getLoginUser();
        UserService userService = ServiceGenerator.createService(UserService.class,
                loginUser.getEmail(), loginUser.getPassword());
        userService.getFitur().enqueue(new Callback<GetFiturResponseJson>() {
            @Override
            public void onResponse(Call<GetFiturResponseJson> call, Response<GetFiturResponseJson> response) {
                if (response.isSuccessful()) {
                    Realm realm = GoTaxiApplication.getInstance(MainActivity.this).getRealmInstance();
                    realm.beginTransaction();
                    realm.delete(Fitur.class);
                    realm.copyToRealm(response.body().getData());
                    realm.commitTransaction();

                    DiskonMpay diskonMpay = response.body().getDiskonMpay();
                    realm.beginTransaction();
                    realm.delete(DiskonMpay.class);
                    realm.copyToRealm(response.body().getDiskonMpay());
                    realm.commitTransaction();
                    GoTaxiApplication.getInstance(MainActivity.this).setDiskonMpay(diskonMpay);

                    MfoodMitra mfoodMitra = response.body().getMfoodMitra();
                    realm.beginTransaction();
                    realm.delete(MfoodMitra.class);
                    realm.copyToRealm(response.body().getMfoodMitra());
                    realm.commitTransaction();
                    GoTaxiApplication.getInstance(MainActivity.this).setMfoodMitra(mfoodMitra);
                }
            }

            @Override
            public void onFailure(Call<GetFiturResponseJson> call, Throwable t) {

            }
        });
    }
}

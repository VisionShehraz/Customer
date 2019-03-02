package com.gotaxiride.passenger;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.google.firebase.iid.FirebaseInstanceId;

import com.gotaxiride.passenger.model.DiskonMpay;
import com.gotaxiride.passenger.model.FirebaseToken;
import com.gotaxiride.passenger.model.MfoodMitra;
import com.gotaxiride.passenger.model.User;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Androgo on 10/13/2018.
 */

public class GoTaxiApplication extends Application {

    private static final int SCHEMA_VERSION = 0;

    private static final String TAG = "GoTaxiApplication";

    private User loginUser;

    private Realm realmInstance;

    private DiskonMpay diskonMpay;

    private MfoodMitra mfoodMitra;

    public static GoTaxiApplication getInstance(Context context) {
        return (GoTaxiApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(SCHEMA_VERSION)
                .deleteRealmIfMigrationNeeded()
                .build();

        FirebaseToken token = new FirebaseToken(FirebaseInstanceId.getInstance().getToken());

        Realm.setDefaultConfiguration(config);

//       realmInstance = Realm.getInstance(config);
        realmInstance = Realm.getDefaultInstance();
        realmInstance.beginTransaction();
        realmInstance.delete(FirebaseToken.class);
        realmInstance.copyToRealm(token);
        realmInstance.commitTransaction();

        start();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public User getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(User loginUser) {
        this.loginUser = loginUser;
    }

    public final Realm getRealmInstance() {
        return realmInstance;
    }

    private void start() {
        Realm realm = getRealmInstance();
        User user = realm.where(User.class).findFirst();
        if (user != null) {
            setLoginUser(user);
        }
    }

    public DiskonMpay getDiskonMpay() {
        return diskonMpay;
    }

    public void setDiskonMpay(DiskonMpay diskonMpay) {
        this.diskonMpay = diskonMpay;
    }

    public MfoodMitra getMfoodMitra() {
        return mfoodMitra;
    }

    public void setMfoodMitra(MfoodMitra mfoodMitra) {
        this.mfoodMitra = mfoodMitra;
    }


}

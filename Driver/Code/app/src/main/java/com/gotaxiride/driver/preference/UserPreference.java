package com.gotaxiride.driver.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.gotaxiride.driver.model.Driver;
import com.gotaxiride.driver.network.Log;
import com.gotaxiride.driver.service.MyConfig;

/**
 * Created by GagahIB on 11/5/2016.
 */
public class UserPreference {

    private static String KEY_ID = "ID";
    private static String KEY_NAME = "NAME";
    private static String KEY_USERNAME = "USERNAME";
    private static String KEY_PHONE = "PHONE";
    private static String KEY_EMAIL = "EMAIL";
    private static String KEY_PASSWORD = "PASSWORD";
    private static String KEY_TYPE = "TYPE";
    private static String KEY_IMAGE = "IMAGE";
    private static String KEY_RATING = "RATING";
    private static String KEY_DEPOSIT = "DEPOSIT";
    private static String KEY_GCM_ID = "GCM_ID";
    private static String KEY_STATUS = "STATUS";
    private static String KEY_JOB = "JOB";
    private static String KEY_NAMA_BANK = "NAMA_BANK";
    private static String KEY_ATAS_NAMA = "ATAS_NAMA";
    private static String KEY_NO_REK = "NO_REK";
    private static String KEY_LATITUDE = "LATITUDE";
    private static String KEY_LONGITUDE = "LONGITUDE";

    private SharedPreferences pref;

    private SharedPreferences.Editor editor;

    public UserPreference(Context context) {
        pref = context.getSharedPreferences(MyConfig.USER_PREF, Context.MODE_PRIVATE);
    }

    public void insertDriver(Driver user) {
        editor = pref.edit();
        editor.putString(KEY_ID, user.id);
        editor.putString(KEY_NAME, user.name);
        editor.putString(KEY_PHONE, user.phone);
        editor.putString(KEY_EMAIL, user.email);
        editor.putString(KEY_PASSWORD, user.password);
        editor.putString(KEY_IMAGE, user.image);
        editor.putString(KEY_RATING, user.rating);
        editor.putInt(KEY_DEPOSIT, user.deposit);
        editor.putInt(KEY_STATUS, user.status);
        editor.putInt(KEY_JOB, user.job);
        editor.putString(KEY_NAMA_BANK, user.nama_bank);
        editor.putString(KEY_ATAS_NAMA, user.atas_nama);
        editor.putString(KEY_NO_REK, user.no_rek);
        editor.commit();
    }

    public void updateGCMID(String gcm) {
        editor = pref.edit();
        editor.putString(KEY_GCM_ID, gcm);
        editor.commit();
    }

    public void updatePassword(String pass) {
        editor = pref.edit();
        editor.putString(KEY_PASSWORD, pass);
        editor.commit();
    }

    public void updateTelepon(String tel) {
        editor = pref.edit();
        editor.putString(KEY_PHONE, tel);
        editor.commit();
    }

    public void updateEmail(String tel) {
        editor = pref.edit();
        editor.putString(KEY_EMAIL, tel);
        editor.commit();
    }

    public void updateStatus(int status) {
        editor = pref.edit();
        editor.putInt(KEY_STATUS, status);
        editor.commit();
        Log.d("Updating_status_to", String.valueOf(status));
    }

    public void updateDeposit(int deposit) {
        editor = pref.edit();
        editor.putInt(KEY_DEPOSIT, deposit);
        editor.commit();
    }

    public void updateRating(String rating) {
        editor = pref.edit();
        editor.putString(KEY_RATING, rating);
        editor.commit();
    }

    public void updateRekening(String[] rek) {
        editor = pref.edit();
        editor.putString(KEY_NAMA_BANK, rek[0]);
        editor.putString(KEY_NO_REK, rek[1]);
        editor.putString(KEY_ATAS_NAMA, rek[2]);
        editor.commit();
    }

    public void updateLokasi(Double lat, Double lon) {
        editor = pref.edit();
        editor.putString(KEY_LATITUDE, String.valueOf(lat));
        editor.putString(KEY_LONGITUDE, String.valueOf(lon));
        editor.commit();
        Log.d("Loc", "updated " + lat);
    }

    public Driver getDriver() {
        Driver instance = new Driver();
        instance.id = pref.getString(KEY_ID, "");
        instance.name = pref.getString(KEY_NAME, "");
        instance.phone = pref.getString(KEY_PHONE, "");
        instance.email = pref.getString(KEY_EMAIL, "");
        instance.password = pref.getString(KEY_PASSWORD, "");
        instance.image = pref.getString(KEY_IMAGE, "");
        instance.rating = pref.getString(KEY_RATING, "");
        instance.gcm_id = pref.getString(KEY_GCM_ID, "");
        instance.deposit = pref.getInt(KEY_DEPOSIT, -1);
        instance.status = pref.getInt(KEY_STATUS, -1);
        instance.job = pref.getInt(KEY_JOB, -1);
        instance.nama_bank = pref.getString(KEY_NAMA_BANK, "");
        instance.atas_nama = pref.getString(KEY_ATAS_NAMA, "");
        instance.no_rek = pref.getString(KEY_NO_REK, "");
        instance.latitude = pref.getString(KEY_LATITUDE, "");
        instance.longitude = pref.getString(KEY_LONGITUDE, "");
        return instance;
    }

    public void logout() {
        editor = pref.edit();
        editor.clear();
//        editor.putString(KEY_ID, "");
//        editor.putString(KEY_NAME, "");
//        editor.putString(KEY_USERNAME, "");
//        editor.putString(KEY_PHONE, "");
//        editor.putString(KEY_EMAIL, "");
//        editor.putString(KEY_PASSWORD, "");
//        editor.putString(KEY_TYPE, "");
//        editor.putString(KEY_IMAGE, "");
//        editor.putString(KEY_RATING,"");
//        editor.putString(KEY_GCM_ID,"");
//        editor.putInt(KEY_DEPOSIT, -1);
//        editor.putInt(KEY_STATUS, -1);
//        editor.putInt(KEY_JOB, -1);
//        editor.putString(KEY_NAMA_BANK, "");
//        editor.putString(KEY_ATAS_NAMA, "");
//        editor.putString(KEY_NO_REK, "");
//        editor.putString(KEY_LATITUDE, "");
//        editor.putString(KEY_LONGITUDE, "");
        editor.commit();
        if (pref.getString(KEY_ID, "").equals(""))
            Log.d("Logging_out", "yes");
        else
            Log.d("Logging_out", "fail " + pref.getString(KEY_ID, ""));
    }
}
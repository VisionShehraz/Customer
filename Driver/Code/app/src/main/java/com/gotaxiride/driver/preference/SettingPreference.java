package com.gotaxiride.driver.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.gotaxiride.driver.service.MyConfig;

/**
 * Created by GagahIB on 11/23/2016.
 */
public class SettingPreference {

    private static String KEY_AUTO_BID = "AUTO_BID";
    private static String KEY_MAKSIMAL_BELANJA = "MAKSIMAL_BELANJA";
    private static String KEY_KERJA = "KERJA";
    private static String KEY_VERSION = "VERSION";

    private SharedPreferences pref;

    private SharedPreferences.Editor editor;

    public SettingPreference(Context context) {
        pref = context.getSharedPreferences(MyConfig.SETTING_PREF, Context.MODE_PRIVATE);
    }

    public void insertSetting(String[] set) {
        editor = pref.edit();
        editor.putString(KEY_AUTO_BID, set[0]);
        editor.putString(KEY_MAKSIMAL_BELANJA, set[1]);
        editor.putString(KEY_KERJA, set[2]);
        editor.putString(KEY_VERSION, set[3]);
        editor.commit();
    }

    public void updateAutoBid(String autoBid) {
        editor = pref.edit();
        editor.putString(KEY_AUTO_BID, autoBid);
        editor.commit();
    }

    public void updateMaksimalBelanja(String max) {
        editor = pref.edit();
        editor.putString(KEY_MAKSIMAL_BELANJA, max);
        editor.commit();
    }

    public void updateKerja(String kerja) {
        editor = pref.edit();
        editor.putString(KEY_KERJA, kerja);
        editor.commit();
    }

    public void updateVersions(String version) {
        editor = pref.edit();
        editor.putString(KEY_VERSION, version);
        editor.commit();
    }

    public String[] getSetting() {

        String[] settingan = new String[4];
        settingan[0] = pref.getString(KEY_AUTO_BID, "");
        settingan[1] = pref.getString(KEY_MAKSIMAL_BELANJA, "");
        settingan[2] = pref.getString(KEY_KERJA, "");
        settingan[3] = pref.getString(KEY_VERSION, "");
        return settingan;
    }

    public void logout() {
        editor = pref.edit();
        editor.putString(KEY_AUTO_BID, "");
        editor.putString(KEY_MAKSIMAL_BELANJA, "");
        editor.putString(KEY_KERJA, "");
        editor.commit();
    }
}
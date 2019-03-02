package com.gotaxiride.passenger.utils;

import android.app.ProgressDialog;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Androgo on 10/14/2018.
 */

public class DialogActivity extends AppCompatActivity {

    private ProgressDialog dialog;

    public void showProgressDialog(@StringRes int stringRes) {
        String message = getResources().getString(stringRes);
        dialog = ProgressDialog.show(this, "", message, false, false);
    }

    public void showProgressDialog(String message) {
        dialog = ProgressDialog.show(this, "", message, false, false);
    }

    public void hideProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

}
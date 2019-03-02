package com.gotaxiride.passenger.utils;

import android.support.annotation.StringRes;
import android.view.View;

/**
 * Created by Androgo on 10/17/2018.
 */

public interface SnackbarController {
    void showSnackbar(@StringRes int stringRes, int duration, @StringRes int actionResText, View.OnClickListener onClickListener);
}

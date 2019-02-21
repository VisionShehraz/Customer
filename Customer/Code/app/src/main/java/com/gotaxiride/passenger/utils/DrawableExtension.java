package com.gotaxiride.passenger.utils;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageView;

/**
 * Created by Androgo on 10/29/2018.
 */

public class DrawableExtension {

    public static void setTintSelector(ImageView imageView, @ColorRes int colorId) {
        ColorStateList colors = ContextCompat.getColorStateList(imageView.getContext(), colorId);
        Drawable drawable = imageView.getDrawable();
        DrawableCompat.setTintList(drawable, colors);
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_ATOP);
        imageView.setImageDrawable(drawable);
    }

}

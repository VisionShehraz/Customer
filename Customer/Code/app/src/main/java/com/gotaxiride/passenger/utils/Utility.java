package com.gotaxiride.passenger.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.gotaxiride.passenger.config.General;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by haris on 12/2/16.
 */

public class Utility {
    public static TextWatcher currencyTW(final EditText editText) {

        return new TextWatcher() {
            private String current = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText.removeTextChangedListener(this);

                try {
                    String originalString = s.toString();

                    Long longval;
                    if (originalString.contains(".")) {
                        originalString = originalString.replaceAll("[$.]", "");
                    }
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    if (originalString.contains("$ ")) {
                        originalString = originalString.replaceAll("$ ", "");
                    }
                    if (originalString.contains("$")) {
                        originalString = originalString.replaceAll("$", "");
                    }
                    if (originalString.contains("R")) {
                        originalString = originalString.replaceAll("R", "");
                    }
                    if (originalString.contains("p")) {
                        originalString = originalString.replaceAll("p", "");
                    }
                    if (originalString.contains(" ")) {
                        originalString = originalString.replaceAll(" ", "");
                    }


                    longval = Long.parseLong(originalString);

                    DecimalFormat formatter = new DecimalFormat("#,###");
                    String formattedString = formatter.format(longval);
                    formattedString = formattedString.replaceAll("[$,]", ".");
                    //setting text after format to EditText
                    editText.setText(General.MONEY +" " + formattedString);
                    editText.setSelection(editText.getText().length());
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                editText.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        };
    }

//    public static double round(double value, int places) {
//        if (places < 0) throw new IllegalArgumentException();
//
//        long factor = (long) Math.pow(10, places);
//        value = value * factor;
//        long tmp = Math.round(value);
//        return (double) tmp / factor;
//    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}

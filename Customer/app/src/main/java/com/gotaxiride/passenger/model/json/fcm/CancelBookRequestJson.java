package com.gotaxiride.passenger.model.json.fcm;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by haris on 11/28/16.
 */

public class CancelBookRequestJson {
    @Expose
    @SerializedName("id")
    public String id;

    @Expose
    @SerializedName("id_transaksi")
    public String id_transaksi;

    @Expose
    @SerializedName("id_driver")
    public String id_driver;


}

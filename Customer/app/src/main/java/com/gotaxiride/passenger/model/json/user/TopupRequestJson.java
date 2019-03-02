package com.gotaxiride.passenger.model.json.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Androgo on 10/13/2018.
 */

public class TopupRequestJson {

    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("no_rekening")
    @Expose
    public String no_rekening;

    @SerializedName("atas_nama")
    @Expose
    public String atas_nama;

    @SerializedName("jumlah")
    @Expose
    public String jumlah;

    @SerializedName("bank")
    @Expose
    public String bank;

    @SerializedName("bukti")
    @Expose
    public String bukti;


}

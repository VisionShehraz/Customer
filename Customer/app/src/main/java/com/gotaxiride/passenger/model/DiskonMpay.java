package com.gotaxiride.passenger.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;

/**** Go - TAXI - On Demand All in One App Services Android
 * Created by Androgo on 12/20/2018.
 */

public class DiskonMpay extends RealmObject implements Serializable {

    @SerializedName("diskon")
    @Expose
    private String diskon;
    @SerializedName("biaya_akhir")
    @Expose
    private Double biayaAkhir;

    public String getDiskon() {
        return diskon;
    }

    public void setDiskon(String diskon) {
        this.diskon = diskon;
    }

    public Double getBiayaAkhir() {
        return biayaAkhir;
    }

    public void setBiayaAkhir(Double biayaAkhir) {
        this.biayaAkhir = biayaAkhir;
    }
}

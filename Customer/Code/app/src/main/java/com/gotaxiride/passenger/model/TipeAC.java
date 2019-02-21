package com.gotaxiride.passenger.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Androgo on 08/18/2018.
 */

public class TipeAC implements Serializable {

    @Expose
    @SerializedName("nomor")
    private int nomor;

    @Expose
    @SerializedName("ac_type")
    private String acType;

    @Expose
    @SerializedName("fare")
    private double fare;

    public int getNomor() {
        return nomor;
    }

    public void setNomor(int nomor) {
        this.nomor = nomor;
    }

    public String getAcType() {
        return acType;
    }

    public void setAcType(String acType) {
        this.acType = acType;
    }

    public double getFare() {
        return fare;
    }

    public void setFare(int fare) {
        this.fare = fare;
    }

}

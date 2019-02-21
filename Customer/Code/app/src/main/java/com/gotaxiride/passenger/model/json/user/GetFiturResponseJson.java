package com.gotaxiride.passenger.model.json.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.gotaxiride.passenger.model.DiskonMpay;
import com.gotaxiride.passenger.model.Fitur;
import com.gotaxiride.passenger.model.MfoodMitra;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Androgo on 10/17/2018.
 */

public class GetFiturResponseJson {

    @Expose
    @SerializedName("mfood_mitra")
    MfoodMitra mfoodMitra;
    @Expose
    @SerializedName("data")
    private List<Fitur> data = new ArrayList<>();
    @Expose
    @SerializedName("diskon_mpay")
    private DiskonMpay diskonMpay;

    public List<Fitur> getData() {
        return data;
    }

    public void setData(List<Fitur> data) {
        this.data = data;
    }

    public DiskonMpay getDiskonMpay() {
        return diskonMpay;
    }

    public void setDiskonMpay(DiskonMpay diskonMpay) {
        this.diskonMpay = diskonMpay;
    }

    public MfoodMitra getMfoodMitra() {
        return mfoodMitra;
    }

    public void setMfoodMitra(MfoodMitra mfoodMitra) {
        this.mfoodMitra = mfoodMitra;
    }
}

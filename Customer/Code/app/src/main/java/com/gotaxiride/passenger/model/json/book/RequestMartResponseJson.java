package com.gotaxiride.passenger.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.gotaxiride.passenger.model.Transaksi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Androgo on 12/7/2018.
 */

public class RequestMartResponseJson {

    @Expose
    @SerializedName("message")
    public String mesage;

    @Expose
    @SerializedName("data")
    public List<Transaksi> data = new ArrayList<>();

}

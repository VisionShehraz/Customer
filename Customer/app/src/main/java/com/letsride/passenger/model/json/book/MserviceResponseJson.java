package com.letsride.passenger.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.letsride.passenger.model.Transaksi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Androgo on 11/18/2018.
 */

public class MserviceResponseJson {

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("data")
    private List<Transaksi> data = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Transaksi> getData() {
        return data;
    }

    public void setData(List<Transaksi> data) {
        this.data = data;
    }
}

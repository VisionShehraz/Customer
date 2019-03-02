package com.gotaxiride.passenger.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.gotaxiride.passenger.model.Restoran;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Androgo on 12/28/2018.
 */

public class GetDataRestoByKategoriResponseJson {

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("data")
    private List<Restoran> restoranList = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Restoran> getRestoranList() {
        return restoranList;
    }

    public void setRestoranList(List<Restoran> restoranList) {
        this.restoranList = restoranList;
    }
}

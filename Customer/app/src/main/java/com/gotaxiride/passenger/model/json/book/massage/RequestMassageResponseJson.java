package com.gotaxiride.passenger.model.json.book.massage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.gotaxiride.passenger.model.DriverMassage;
import com.gotaxiride.passenger.model.TransaksiMassage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Androgo on 12/23/2018.
 */

public class RequestMassageResponseJson {

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("data")
    private List<TransaksiMassage> data = new ArrayList<>();

    @Expose
    @SerializedName("list_driver")
    private List<DriverMassage> listDriver = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<TransaksiMassage> getData() {
        return data;
    }

    public void setData(List<TransaksiMassage> data) {
        this.data = data;
    }

    public List<DriverMassage> getListDriver() {
        return listDriver;
    }

    public void setListDriver(List<DriverMassage> listDriver) {
        this.listDriver = listDriver;
    }
}

package com.gotaxiride.passenger.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.gotaxiride.passenger.model.Driver;
import com.gotaxiride.passenger.model.StatusTransaksi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fathony on 11/02/2017.
 */

public class CheckStatusTransaksiResponse {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("data")
    @Expose
    private List<StatusTransaksi> data = new ArrayList<>();
    @SerializedName("list_driver")
    @Expose
    private List<Driver> listDriver = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<StatusTransaksi> getData() {
        return data;
    }

    public void setData(List<StatusTransaksi> data) {
        this.data = data;
    }

    public List<Driver> getListDriver() {
        return listDriver;
    }

    public void setListDriver(List<Driver> listDriver) {
        this.listDriver = listDriver;
    }
}

package com.gotaxiride.passenger.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Androgo on 11/22/2018.
 */

public class DataMservice implements Serializable {

    @Expose
    @SerializedName("jenis_service")
    private List<JenisService> jenisServiceList = new ArrayList<>();

    @Expose
    @SerializedName("ac_type")
    private List<TipeAC> tipeACList = new ArrayList<>();

    public List<JenisService> getJenisServiceList() {
        return jenisServiceList;
    }

    public void setJenisServiceList(List<JenisService> jenisServiceList) {
        this.jenisServiceList = jenisServiceList;
    }

    public List<TipeAC> getTipeACList() {
        return tipeACList;
    }

    public void setTipeACList(List<TipeAC> tipeACList) {
        this.tipeACList = tipeACList;
    }

}

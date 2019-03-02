package com.gotaxiride.passenger.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Androgo on 11/22/2018.
 */

public class DataPulsa implements Serializable {

    @Expose
    @SerializedName("jenis_service")
    private List<KategoriPulsa> jenisServiceList = new ArrayList<>();

    @Expose
    @SerializedName("ac_type")
    private List<TipeAC> tipeACList = new ArrayList<>();

    public List<KategoriPulsa> getJenisServiceList() {
        return jenisServiceList;
    }

    public void setJenisServiceList(List<KategoriPulsa> jenisServiceList) {
        this.jenisServiceList = jenisServiceList;
    }

    public List<TipeAC> getTipeACList() {
        return tipeACList;
    }

    public void setTipeACList(List<TipeAC> tipeACList) {
        this.tipeACList = tipeACList;
    }

}

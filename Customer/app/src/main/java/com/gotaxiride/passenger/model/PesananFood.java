package com.gotaxiride.passenger.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Androgo on 1/5/2017.
 */

public class PesananFood extends RealmObject implements Serializable {

    @PrimaryKey
    @Expose
    @SerializedName("id_makanan")
    private int idMakanan;

    @Expose
    @SerializedName("total_harga")
    private long totalHarga;

    @Expose
    @SerializedName("qty")
    private int qty;

    @Expose
    @SerializedName("catatan")
    private String catatan = "";

    public int getIdMakanan() {
        return idMakanan;
    }

    public void setIdMakanan(int idMakanan) {
        this.idMakanan = idMakanan;
    }

    public long getTotalHarga() {
        return totalHarga;
    }

    public void setTotalHarga(long totalHarga) {
        this.totalHarga = totalHarga;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }
}

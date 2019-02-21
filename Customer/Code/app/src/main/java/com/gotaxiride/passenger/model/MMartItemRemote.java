package com.gotaxiride.passenger.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by fathony on 24/02/2017.
 */

public class MMartItemRemote {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("id_transaksi")
    @Expose
    private String idTransaksi;
    @SerializedName("nama_barang")
    @Expose
    private String namaBarang;
    @SerializedName("jumlah")
    @Expose
    private long jumlah;

    public String getId() {
        return id;
    }

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public long getJumlah() {
        return jumlah;
    }
}

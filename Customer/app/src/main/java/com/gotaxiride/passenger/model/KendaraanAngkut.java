package com.gotaxiride.passenger.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Androgo on 11/22/2018.
 */

public class KendaraanAngkut extends RealmObject implements Serializable {

    @PrimaryKey
    @Expose
    @SerializedName("id")
    private int idKendaraan;

    @Expose
    @SerializedName("kendaraan_angkut")
    private String kendaraanAngkut;

    @Expose
    @SerializedName("harga")
    private long harga;

    @Expose
    @SerializedName("deskripsi_kendaraan")
    private String deskripsiKendaraan;

    @Expose
    @SerializedName("foto_kendaraan")
    private String fotoKendaraan;

    @Expose
    @SerializedName("dimensi_kendaraan")
    private String dimensiKendaraan;

    @Expose
    @SerializedName("maxweight_kendaraan")
    private String maxweightKendaraan;


    public int getIdKendaraan() {
        return idKendaraan;
    }

    public void setIdKendaraan(int idKendaraan) {
        this.idKendaraan = idKendaraan;
    }

    public String getKendaraanAngkut() {
        return kendaraanAngkut;
    }

    public void setKendaraanAngkut(String kendaraanAngkut) {
        this.kendaraanAngkut = kendaraanAngkut;
    }

    public long getHarga() {
        return harga;
    }

    public void setHarga(long harga) {
        this.harga = harga;
    }

    public String getDeskripsiKendaraan() {
        return deskripsiKendaraan;
    }

    public void setDeskripsiKendaraan(String deskripsiKendaraan) {
        this.deskripsiKendaraan = deskripsiKendaraan;
    }

    public String getFotoKendaraan() {
        return fotoKendaraan;
    }

    public void setFotoKendaraan(String fotoKendaraan) {
        this.fotoKendaraan = fotoKendaraan;
    }

    public String getDimensiKendaraan() {
        return dimensiKendaraan;
    }

    public void setDimensiKendaraan(String dimensiKendaraan) {
        this.dimensiKendaraan = dimensiKendaraan;
    }

    public String getMaxweightKendaraan() {
        return maxweightKendaraan;
    }

    public void setMaxweightKendaraan(String maxweightKendaraan) {
        this.maxweightKendaraan = maxweightKendaraan;
    }

}

package com.gotaxiride.passenger.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by fathony on 1/25/2017.
 */

public class MfoodMitra extends RealmObject implements Serializable {

    @PrimaryKey
    @Expose
    @SerializedName("id_fitur")
    private int idFitur;

    @Expose
    @SerializedName("biaya")
    private long biaya;

    @Expose
    @SerializedName("biaya_minimum")
    private long biaya_minimum;

    @Expose
    @SerializedName("keterangan_biaya")
    private String keteranganBiaya;

    @SerializedName("diskon")
    @Expose
    private String diskon;

    @SerializedName("biaya_akhir")
    @Expose
    private Double biayaAkhir;

    public int getIdFitur() {
        return idFitur;
    }

    public void setIdFitur(int idFitur) {
        this.idFitur = idFitur;
    }

    public long getBiaya() {
        return biaya;
    }

    public void setBiaya(long biaya) {
        this.biaya = biaya;
    }

    public long getBiaya_minimum() {
        return biaya_minimum;
    }

    public void setBiaya_minimum(long biaya_minimum) {
        this.biaya_minimum = biaya_minimum;
    }

    public String getKeteranganBiaya() {
        return keteranganBiaya;
    }

    public void setKeteranganBiaya(String keteranganBiaya) {
        this.keteranganBiaya = keteranganBiaya;
    }

    public String getDiskon() {
        return diskon;
    }

    public void setDiskon(String diskon) {
        this.diskon = diskon;
    }

    public Double getBiayaAkhir() {
        return biayaAkhir;
    }

    public void setBiayaAkhir(Double biayaAkhir) {
        this.biayaAkhir = biayaAkhir;
    }
}

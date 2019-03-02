package com.gotaxiride.passenger.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Androgo on 12/28/2018.
 */

public class KategoriRestoran extends RealmObject implements Serializable {

    @Expose
    @SerializedName("foto_kategori")
    public String fotoKategori;
    @PrimaryKey
    @Expose
    @SerializedName("id_kategori")
    private int idKategori;
    @Expose
    @SerializedName("kategori")
    private String kategori;

    public int getIdKategori() {
        return idKategori;
    }

    public void setIdKategori(int idKategori) {
        this.idKategori = idKategori;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getFotoKategori() {
        return fotoKategori;
    }

    public void setFotoKategori(String fotoKategori) {
        this.fotoKategori = fotoKategori;
    }
}

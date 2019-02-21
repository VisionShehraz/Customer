package com.gotaxiride.passenger.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Androgo on 10/17/2018.
 */

public class DriverMassage implements Serializable {

    @Expose
    @SerializedName("nama_depan")
    private String namaDepan;

    @Expose
    @SerializedName("nama_belakang")
    private String namaBelakang;

    @Expose
    @SerializedName("no_telepon")
    private String noTelepon;

    @Expose
    @SerializedName("foto")
    private String foto;

    @Expose
    @SerializedName("rating")
    private String rating;

    @Expose
    @SerializedName("reg_id")
    private String regId;

    @Expose
    @SerializedName("pemijat")
    private String pemijat;

    @Expose
    @SerializedName("latitude")
    private double latitude;

    @Expose
    @SerializedName("longitude")
    private double longitude;

    public String getNamaDepan() {
        return namaDepan;
    }

    public void setNamaDepan(String namaDepan) {
        this.namaDepan = namaDepan;
    }

    public String getNamaBelakang() {
        return namaBelakang;
    }

    public void setNamaBelakang(String namaBelakang) {
        this.namaBelakang = namaBelakang;
    }

    public String getNoTelepon() {
        return noTelepon;
    }

    public void setNoTelepon(String noTelepon) {
        this.noTelepon = noTelepon;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getPemijat() {
        return pemijat;
    }

    public void setPemijat(String pemijat) {
        this.pemijat = pemijat;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}

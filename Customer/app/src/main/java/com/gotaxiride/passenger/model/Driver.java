package com.gotaxiride.passenger.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Androgo on 10/17/2018.
 */

public class Driver extends RealmObject implements Serializable {

    @Expose
    @SerializedName("id")
    private String id;

    @Expose
    @SerializedName("nama_depan")
    private String namaDepan;

    @Expose
    @SerializedName("nama_belakang")
    private String namaBelakang;

    @Expose
    @SerializedName("latitude")
    private double latitude;

    @Expose
    @SerializedName("longitude")
    private double longitude;

    @Expose
    @SerializedName("update_at")
    private Date updateAt;

    @Expose
    @SerializedName("no_telepon")
    private String noTelepon;

    @Expose
    @SerializedName("foto")
    private String foto;

    @Expose
    @SerializedName("reg_id")
    private String regId;

    @Expose
    @SerializedName("driver_job")
    private String driverJob;

    @Expose
    @SerializedName("distance")
    private String distance;

    @Expose
    @SerializedName("merek")
    private String merek;

    @Expose
    @SerializedName("nomor_kendaraan")
    private String nomor_kendaraan;

    @Expose
    @SerializedName("warna")
    private String warna;

    @Expose
    @SerializedName("tipe")
    private String tipe;


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

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
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

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getDriverJob() {
        return driverJob;
    }

    public void setDriverJob(String driverJob) {
        this.driverJob = driverJob;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getMerek() {
        return merek;
    }

    public void setMerek(String merek) {
        this.merek = merek;
    }

    public String getNomor_kendaraan() {
        return nomor_kendaraan;
    }

    public void setNomor_kendaraan(String nomor_kendaraan) {
        this.nomor_kendaraan = nomor_kendaraan;
    }

    public String getWarna() {
        return warna;
    }

    public void setWarna(String warna) {
        this.warna = warna;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}

package com.gotaxiride.passenger.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Androgo on 12/28/2018.
 */

public class Restoran extends RealmObject implements Serializable {

    @PrimaryKey
    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("nama_resto")
    private String namaResto;

    @Expose
    @SerializedName("alamat")
    private String alamat;

    @Expose
    @SerializedName("latitude")
    private double latitude;

    @Expose
    @SerializedName("longitude")
    private double longitude;

    @Expose
    @SerializedName("jam_buka")
    private String jamBuka;

    @Expose
    @SerializedName("jam_tutup")
    private String jamTutup;

    @Expose
    @SerializedName("deskripsi_resto")
    private String deskripsiResto;

    @Expose
    @SerializedName("kategori_resto")
    private int kategoriResto;

    @Expose
    @SerializedName("foto_resto")
    private String fotoResto;

    @Expose
    @SerializedName("kontak_telepon")
    private String kontakTelepon;

    @Expose
    @SerializedName("status")
    private int status;

    @Expose
    @SerializedName("is_open")
    private boolean isOpen;

    @Expose
    @SerializedName("is_partner")
    private boolean isPartner;

    @Expose
    @SerializedName("kategori_restoran")
    private String kategoriRestoran;

    @Expose
    @SerializedName("distance")
    private double distance;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamaResto() {
        return namaResto;
    }

    public void setNamaResto(String namaResto) {
        this.namaResto = namaResto;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
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

    public String getJamBuka() {
        return jamBuka;
    }

    public void setJamBuka(String jamBuka) {
        this.jamBuka = jamBuka;
    }

    public String getJamTutup() {
        return jamTutup;
    }

    public void setJamTutup(String jamTutup) {
        this.jamTutup = jamTutup;
    }

    public String getDeskripsiResto() {
        return deskripsiResto;
    }

    public void setDeskripsiResto(String deskripsiResto) {
        this.deskripsiResto = deskripsiResto;
    }

    public int getKategoriResto() {
        return kategoriResto;
    }

    public void setKategoriResto(int kategoriResto) {
        this.kategoriResto = kategoriResto;
    }

    public String getFotoResto() {
        return fotoResto;
    }

    public void setFotoResto(String fotoResto) {
        this.fotoResto = fotoResto;
    }

    public String getKontakTelepon() {
        return kontakTelepon;
    }

    public void setKontakTelepon(String kontakTelepon) {
        this.kontakTelepon = kontakTelepon;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getKategoriRestoran() {
        return kategoriRestoran;
    }

    public void setKategoriRestoran(String kategoriRestoran) {
        this.kategoriRestoran = kategoriRestoran;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isPartner() {
        return isPartner;
    }

    public void setPartner(boolean partner) {
        isPartner = partner;
    }
}

package com.gotaxiride.passenger.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Androgo on 12/23/2018.
 */

public class TransaksiMassage implements Serializable {

    @Expose
    @SerializedName("id_transaksi")
    private String idTransaksi;

    @Expose
    @SerializedName("harga")
    private long harga;

    @Expose
    @SerializedName("order_fitur")
    private String orderFitur;

    @Expose
    @SerializedName("alamat_asal")
    private String alamatAsal;

    @Expose
    @SerializedName("waktu_order")
    private Date waktuOrder;

    @Expose
    @SerializedName("kode_promo")
    private String kodePromo;

    @Expose
    @SerializedName("kredit_promo")
    private int kreditPromo;

    @Expose
    @SerializedName("id_pelanggan")
    private String idPelanggan;

    @Expose
    @SerializedName("start_latitude")
    private double startLatitude;

    @Expose
    @SerializedName("start_longitude")
    private double startLongitude;

    @Expose
    @SerializedName("pakai_mpay")
    private boolean pakaiMpay;

    @Expose
    @SerializedName("kota")
    private String kota;

    @Expose
    @SerializedName("tanggal_pelayanan")
    private String tanggalPelayanan;

    @Expose
    @SerializedName("massage_menu")
    private String massageMenu;

    @Expose
    @SerializedName("jam_pelayanan")
    private String jamPelayanan;

    @Expose
    @SerializedName("lama_pelayanan")
    private long lamaPelayanan;

    @Expose
    @SerializedName("prefer_gender")
    private String preferGender;

    @Expose
    @SerializedName("pelanggan_gender")
    private String pelangganGender;

    @Expose
    @SerializedName("status_transaksi")
    private String statusTransaksi;

    @Expose
    @SerializedName("catatan_tambahan")
    private String catatanTambahan;

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(String idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public long getHarga() {
        return harga;
    }

    public void setHarga(long harga) {
        this.harga = harga;
    }

    public String getOrderFitur() {
        return orderFitur;
    }

    public void setOrderFitur(String orderFitur) {
        this.orderFitur = orderFitur;
    }

    public String getAlamatAsal() {
        return alamatAsal;
    }

    public void setAlamatAsal(String alamatAsal) {
        this.alamatAsal = alamatAsal;
    }

    public Date getWaktuOrder() {
        return waktuOrder;
    }

    public void setWaktuOrder(Date waktuOrder) {
        this.waktuOrder = waktuOrder;
    }

    public String getKodePromo() {
        return kodePromo;
    }

    public void setKodePromo(String kodePromo) {
        this.kodePromo = kodePromo;
    }

    public int getKreditPromo() {
        return kreditPromo;
    }

    public void setKreditPromo(int kreditPromo) {
        this.kreditPromo = kreditPromo;
    }

    public String getIdPelanggan() {
        return idPelanggan;
    }

    public void setIdPelanggan(String idPelanggan) {
        this.idPelanggan = idPelanggan;
    }

    public double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public boolean isPakaiMpay() {
        return pakaiMpay;
    }

    public void setPakaiMpay(boolean pakaiMpay) {
        this.pakaiMpay = pakaiMpay;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public String getTanggalPelayanan() {
        return tanggalPelayanan;
    }

    public void setTanggalPelayanan(String tanggalPelayanan) {
        this.tanggalPelayanan = tanggalPelayanan;
    }

    public String getMassageMenu() {
        return massageMenu;
    }

    public void setMassageMenu(String massageMenu) {
        this.massageMenu = massageMenu;
    }

    public String getJamPelayanan() {
        return jamPelayanan;
    }

    public void setJamPelayanan(String jamPelayanan) {
        this.jamPelayanan = jamPelayanan;
    }

    public long getLamaPelayanan() {
        return lamaPelayanan;
    }

    public void setLamaPelayanan(long lamaPelayanan) {
        this.lamaPelayanan = lamaPelayanan;
    }

    public String getPreferGender() {
        return preferGender;
    }

    public void setPreferGender(String preferGender) {
        this.preferGender = preferGender;
    }

    public String getPelangganGender() {
        return pelangganGender;
    }

    public void setPelangganGender(String pelangganGender) {
        this.pelangganGender = pelangganGender;
    }

    public String getStatusTransaksi() {
        return statusTransaksi;
    }

    public void setStatusTransaksi(String statusTransaksi) {
        this.statusTransaksi = statusTransaksi;
    }

    public String getCatatanTambahan() {
        return catatanTambahan;
    }

    public void setCatatanTambahan(String catatanTambahan) {
        this.catatanTambahan = catatanTambahan;
    }
}
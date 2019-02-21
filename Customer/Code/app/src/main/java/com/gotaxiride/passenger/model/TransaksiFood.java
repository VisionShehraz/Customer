package com.gotaxiride.passenger.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by fathony on 1/26/2017.
 */

public class TransaksiFood {

    @Expose
    @SerializedName("id")
    private String id;
    @Expose
    @SerializedName("id_pelanggan")
    private String idPelanggan;
    @Expose
    @SerializedName("id_driver")
    private String idDriver;
    @Expose
    @SerializedName("order_fitur")
    private String orderFitur;
    @Expose
    @SerializedName("start_latitude")
    private double startLatitude;
    @Expose
    @SerializedName("start_longitude")
    private double startLongitude;
    @Expose
    @SerializedName("end_latitude")
    private double endLatitude;
    @Expose
    @SerializedName("end_longitude")
    private double endLongitude;
    @Expose
    @SerializedName("jarak")
    private double jarak;
    @Expose
    @SerializedName("harga")
    private long harga;
    @Expose
    @SerializedName("waktu_order")
    private Date waktuOrder;
    @Expose
    @SerializedName("waktu_selesai")
    private Date waktuSelesai;
    @Expose
    @SerializedName("alamat_asal")
    private String alamatAsal;
    @Expose
    @SerializedName("alamat_tujuan")
    private String alamatTujuan;
    @Expose
    @SerializedName("kode_promo")
    private String kodePromo;
    @Expose
    @SerializedName("kredit_promo")
    private long kreditPromo;
    @Expose
    @SerializedName("biaya_akhir")
    private long biayaAkhir;
    @Expose
    @SerializedName("pakai_mpay")
    private boolean pakaiMPay;
    @Expose
    @SerializedName("rate")
    private String rate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdPelanggan() {
        return idPelanggan;
    }

    public void setIdPelanggan(String idPelanggan) {
        this.idPelanggan = idPelanggan;
    }

    public String getIdDriver() {
        return idDriver;
    }

    public void setIdDriver(String idDriver) {
        this.idDriver = idDriver;
    }

    public String getOrderFitur() {
        return orderFitur;
    }

    public void setOrderFitur(String orderFitur) {
        this.orderFitur = orderFitur;
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

    public double getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(double endLatitude) {
        this.endLatitude = endLatitude;
    }

    public double getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(double endLongitude) {
        this.endLongitude = endLongitude;
    }

    public double getJarak() {
        return jarak;
    }

    public void setJarak(double jarak) {
        this.jarak = jarak;
    }

    public long getHarga() {
        return harga;
    }

    public void setHarga(long harga) {
        this.harga = harga;
    }

    public Date getWaktuOrder() {
        return waktuOrder;
    }

    public void setWaktuOrder(Date waktuOrder) {
        this.waktuOrder = waktuOrder;
    }

    public Date getWaktuSelesai() {
        return waktuSelesai;
    }

    public void setWaktuSelesai(Date waktuSelesai) {
        this.waktuSelesai = waktuSelesai;
    }

    public String getAlamatAsal() {
        return alamatAsal;
    }

    public void setAlamatAsal(String alamatAsal) {
        this.alamatAsal = alamatAsal;
    }

    public String getAlamatTujuan() {
        return alamatTujuan;
    }

    public void setAlamatTujuan(String alamatTujuan) {
        this.alamatTujuan = alamatTujuan;
    }

    public String getKodePromo() {
        return kodePromo;
    }

    public void setKodePromo(String kodePromo) {
        this.kodePromo = kodePromo;
    }

    public long getKreditPromo() {
        return kreditPromo;
    }

    public void setKreditPromo(long kreditPromo) {
        this.kreditPromo = kreditPromo;
    }

    public long getBiayaAkhir() {
        return biayaAkhir;
    }

    public void setBiayaAkhir(long biayaAkhir) {
        this.biayaAkhir = biayaAkhir;
    }

    public boolean isPakaiMPay() {
        return pakaiMPay;
    }

    public void setPakaiMPay(boolean pakaiMPay) {
        this.pakaiMPay = pakaiMPay;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}

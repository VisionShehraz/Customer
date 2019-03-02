package com.gotaxiride.passenger.model.json.book.massage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Androgo on 12/23/2018.
 */

public class RequestMassageRequestJson {

    @Expose
    @SerializedName("id_pelanggan")
    private String idPelanggan;

    @Expose
    @SerializedName("order_fitur")
    private String orderFitur;

    @Expose
    @SerializedName("alamat_asal")
    private String alamatAsal;

    @Expose
    @SerializedName("harga")
    private long harga;

    @Expose
    @SerializedName("start_latitude")
    private double startLatitude;

    @Expose
    @SerializedName("start_longitude")
    private double startLongitude;

    @Expose
    @SerializedName("pelanggan_gender")
    private String pelangganGender;

    @Expose
    @SerializedName("prefer_gender")
    private String preferGender;

    @Expose
    @SerializedName("kota")
    private String kota;

    @Expose
    @SerializedName("tanggal_pelayanan")
    private String tanggalPelayanan;

    @Expose
    @SerializedName("lama_pelayanan")
    private long lamaPelayanan;

    @Expose
    @SerializedName("massage_menu")
    private long massageMenu;

    @Expose
    @SerializedName("jam_pelayanan")
    private String jamPelayanan;

    @Expose
    @SerializedName("catatan_tambahan")
    private String catatanTambahan;

    @Expose
    @SerializedName("pakai_mpay")
    private boolean pakaiMPay;

    public String getIdPelanggan() {
        return idPelanggan;
    }

    public void setIdPelanggan(String idPelanggan) {
        this.idPelanggan = idPelanggan;
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

    public long getHarga() {
        return harga;
    }

    public void setHarga(long harga) {
        this.harga = harga;
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

    public String getPelangganGender() {
        return pelangganGender;
    }

    public void setPelangganGender(String pelangganGender) {
        this.pelangganGender = pelangganGender;
    }

    public String getPreferGender() {
        return preferGender;
    }

    public void setPreferGender(String preferGender) {
        this.preferGender = preferGender;
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

    public long getLamaPelayanan() {
        return lamaPelayanan;
    }

    public void setLamaPelayanan(long lamaPelayanan) {
        this.lamaPelayanan = lamaPelayanan;
    }

    public long getMassageMenu() {
        return massageMenu;
    }

    public void setMassageMenu(long massageMenu) {
        this.massageMenu = massageMenu;
    }

    public String getJamPelayanan() {
        return jamPelayanan;
    }

    public void setJamPelayanan(String jamPelayanan) {
        this.jamPelayanan = jamPelayanan;
    }

    public String getCatatanTambahan() {
        return catatanTambahan;
    }

    public void setCatatanTambahan(String catatanTambahan) {
        this.catatanTambahan = catatanTambahan;
    }

    public boolean isPakaiMPay() {
        return pakaiMPay;
    }

    public void setPakaiMPay(boolean pakaiMPay) {
        this.pakaiMPay = pakaiMPay;
    }
}

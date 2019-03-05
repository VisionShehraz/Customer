package com.letsride.passenger.model.json.fcm;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Androgo on 12/30/2018.
 */

public class MassageDriverRequest implements Serializable {

    @Expose
    @SerializedName("jam_pelayanan")
    private String jamPelayanan;

    @Expose
    @SerializedName("nama_pelanggan")
    private String namaPelanggan;

    @Expose
    @SerializedName("waktu_order")
    private Date waktuOrder;

    @Expose
    @SerializedName("start_longitude")
    private double startLongitude;

    @Expose
    @SerializedName("telepon")
    private String telepon;

    @Expose
    @SerializedName("tanggal_pelayanan")
    private String tanggalPelayanan;

    @Expose
    @SerializedName("alamat_asal")
    private String alamatAsal;

    @Expose
    @SerializedName("catatan_tambahan")
    private String catatanTambahan;

    @Expose
    @SerializedName("id_pelanggan")
    private String idPelanggan;

    @Expose
    @SerializedName("lama_pelayanan")
    private long lamaPelayanan;

    @Expose
    @SerializedName("prefer_gender")
    private String preferGender;

    @Expose
    @SerializedName("kota")
    private String kota;

    @Expose
    @SerializedName("type")
    private int type;

    @Expose
    @SerializedName("statusTransaksi")
    private String statusTransaksi;

    @Expose
    @SerializedName("harga")
    private long harga;

    @Expose
    @SerializedName("kredit_promo")
    private int kreditPromo;

    @Expose
    @SerializedName("time_accept")
    private long timeAccept;

    @Expose
    @SerializedName("reg_id_pelanggan")
    private String regIdPelanggan;

    @Expose
    @SerializedName("start_latitude")
    private double startLatitiude;

    @Expose
    @SerializedName("id_transaksi")
    private String idTransaksi;

    @Expose
    @SerializedName("pakai_mpay")
    private boolean pakaiMpay;

    @Expose
    @SerializedName("massage_menu")
    private String massageMenu;

    @Expose
    @SerializedName("pelanggan_gender")
    private String pelangganGender;

    @Expose
    @SerializedName("order_fitur")
    private String orderFitur;


    public String getJamPelayanan() {
        return jamPelayanan;
    }

    public void setJamPelayanan(String jamPelayanan) {
        this.jamPelayanan = jamPelayanan;
    }

    public String getNamaPelanggan() {
        return namaPelanggan;
    }

    public void setNamaPelanggan(String namaPelanggan) {
        this.namaPelanggan = namaPelanggan;
    }

    public Date getWaktuOrder() {
        return waktuOrder;
    }

    public void setWaktuOrder(Date waktuOrder) {
        this.waktuOrder = waktuOrder;
    }

    public double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public String getTanggalPelayanan() {
        return tanggalPelayanan;
    }

    public void setTanggalPelayanan(String tanggalPelayanan) {
        this.tanggalPelayanan = tanggalPelayanan;
    }

    public String getAlamatAsal() {
        return alamatAsal;
    }

    public void setAlamatAsal(String alamatAsal) {
        this.alamatAsal = alamatAsal;
    }

    public String getCatatanTambahan() {
        return catatanTambahan;
    }

    public void setCatatanTambahan(String catatanTambahan) {
        this.catatanTambahan = catatanTambahan;
    }

    public String getIdPelanggan() {
        return idPelanggan;
    }

    public void setIdPelanggan(String idPelanggan) {
        this.idPelanggan = idPelanggan;
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

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStatusTransaksi() {
        return statusTransaksi;
    }

    public void setStatusTransaksi(String statusTransaksi) {
        this.statusTransaksi = statusTransaksi;
    }

    public long getHarga() {
        return harga;
    }

    public void setHarga(long harga) {
        this.harga = harga;
    }

    public int getKreditPromo() {
        return kreditPromo;
    }

    public void setKreditPromo(int kreditPromo) {
        this.kreditPromo = kreditPromo;
    }

    public long getTimeAccept() {
        return timeAccept;
    }

    public void setTimeAccept(long timeAccept) {
        this.timeAccept = timeAccept;
    }

    public String getRegIdPelanggan() {
        return regIdPelanggan;
    }

    public void setRegIdPelanggan(String regIdPelanggan) {
        this.regIdPelanggan = regIdPelanggan;
    }

    public double getStartLatitiude() {
        return startLatitiude;
    }

    public void setStartLatitiude(double startLatitiude) {
        this.startLatitiude = startLatitiude;
    }

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(String idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public boolean isPakaiMpay() {
        return pakaiMpay;
    }

    public void setPakaiMpay(boolean pakaiMpay) {
        this.pakaiMpay = pakaiMpay;
    }

    public String getMassageMenu() {
        return massageMenu;
    }

    public void setMassageMenu(String massageMenu) {
        this.massageMenu = massageMenu;
    }

    public String getPelangganGender() {
        return pelangganGender;
    }

    public void setPelangganGender(String pelangganGender) {
        this.pelangganGender = pelangganGender;
    }

    public String getOrderFitur() {
        return orderFitur;
    }

    public void setOrderFitur(String orderFitur) {
        this.orderFitur = orderFitur;
    }
}
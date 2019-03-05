package com.letsride.passenger.model.json.fcm;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Androgo on 10/19/2018.
 */

public class DriverRequest implements Serializable {

    @Expose
    @SerializedName("nama_pengirim")
    public String namaPengirim;
    @Expose
    @SerializedName("telepon_pengirim")
    public String teleponPengirim;
    @Expose
    @SerializedName("nama_penerima")
    public String namaPenerima;
    @Expose
    @SerializedName("telepon_penerima")
    public String teleponPenerima;
    @Expose
    @SerializedName("nama_barang")
    public String namaBarang;
    @Expose
    @SerializedName("tanggal_pelayanan")
    public String tanggalPelayanan;
    @Expose
    @SerializedName("jam_pelayanan")
    public String jamPelayanan;
    @Expose
    @SerializedName("quantity")
    public int quantity;
    @Expose
    @SerializedName("residential_type")
    public String residentialType;
    @Expose
    @SerializedName("problem")
    public String problem;
    @Expose
    @SerializedName("id_jenis")
    public int idJenis;
    @Expose
    @SerializedName("jenis_service")
    public String jenisService;
    @Expose
    @SerializedName("ac_type")
    public String acType;
    @Expose
    @SerializedName("fare")
    public double fare;
    @Expose
    @SerializedName("type")
    private int type;
    @Expose
    @SerializedName("id_transaksi")
    private String idTransaksi;
    @Expose
    @SerializedName("id_pelanggan")
    private String idPelanggan;
    @Expose
    @SerializedName("reg_id_pelanggan")
    private String regIdPelanggan;
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
    @SerializedName("rate")
    private String rate;
    @Expose
    @SerializedName("kode_promo")
    private String kodePromo;
    @Expose
    @SerializedName("kredit_promo")
    private String kreditPromo;
    @Expose
    @SerializedName("pakai_mpay")
    private boolean pakaiMPay;
    @Expose
    @SerializedName("nama_pelanggan")
    private String namaPelanggan;
    @Expose
    @SerializedName("telepon")
    private String telepon;
    @Expose
    @SerializedName("time_accept")
    private String time_accept;

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(String idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public String getIdPelanggan() {
        return idPelanggan;
    }

    public void setIdPelanggan(String idPelanggan) {
        this.idPelanggan = idPelanggan;
    }

    public String getRegIdPelanggan() {
        return regIdPelanggan;
    }

    public void setRegIdPelanggan(String regIdPelanggan) {
        this.regIdPelanggan = regIdPelanggan;
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

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getKodePromo() {
        return kodePromo;
    }

    public void setKodePromo(String kodePromo) {
        this.kodePromo = kodePromo;
    }

    public String getKreditPromo() {
        return kreditPromo;
    }

    public void setKreditPromo(String kreditPromo) {
        this.kreditPromo = kreditPromo;
    }

    public boolean isPakaiMPay() {
        return pakaiMPay;
    }

    public void setPakaiMPay(boolean pakaiMPay) {
        this.pakaiMPay = pakaiMPay;
    }

    public String getNamaPelanggan() {
        return namaPelanggan;
    }

    public void setNamaPelanggan(String namaPelanggan) {
        this.namaPelanggan = namaPelanggan;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTime_accept() {
        return time_accept;
    }

    public void setTime_accept(String time_accept) {
        this.time_accept = time_accept;
    }

    public String getTanggalPelayanan() {
        return tanggalPelayanan;
    }

    public void setTanggalPelayanan(String tanggalPelayanan) {
        this.tanggalPelayanan = tanggalPelayanan;
    }

    public String getJamPelayanan() {
        return jamPelayanan;
    }

    public void setJamPelayanan(String jamPelayanan) {
        this.jamPelayanan = jamPelayanan;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getResidentialType() {
        return residentialType;
    }

    public void setResidentialType(String residentialType) {
        this.residentialType = residentialType;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public int getIdJenis() {
        return idJenis;
    }

    public void setIdJenis(int idJenis) {
        this.idJenis = idJenis;
    }

    public String getJenisService() {
        return jenisService;
    }

    public void setJenisService(String jenisService) {
        this.jenisService = jenisService;
    }

    public String getAcType() {
        return acType;
    }

    public void setAcType(String acType) {
        this.acType = acType;
    }

    public double getFare() {
        return fare;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }
}

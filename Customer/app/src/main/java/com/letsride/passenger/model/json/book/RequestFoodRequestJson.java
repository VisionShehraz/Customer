package com.letsride.passenger.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.letsride.passenger.model.PesananFood;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fathony on 1/26/2017.
 */

public class RequestFoodRequestJson implements Serializable {

    @Expose
    @SerializedName("id_pelanggan")
    private String idPelanggan;

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
    @SerializedName("toko_latitude")
    private double tokoLatitude;

    @Expose
    @SerializedName("toko_longitude")
    private double tokoLongitude;

    @Expose
    @SerializedName("alamat_asal")
    private String alamatAsal;

    @Expose
    @SerializedName("alamat_resto")
    private String alamatResto;

    @Expose
    @SerializedName("jarak")
    private double jarak;

    @Expose
    @SerializedName("harga")
    private long harga;

    @Expose
    @SerializedName("pakai_mpay")
    private boolean pakaiMPay;

    @Expose
    @SerializedName("id_resto")
    private String idResto;

    @Expose
    @SerializedName("total_biaya_belanja")
    private long totalBiayaBelanja;

    @Expose
    @SerializedName("catatan")
    private String catatan;

    @Expose
    @SerializedName("pesanan")
    private List<PesananFood> pesanan = new ArrayList<>();

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

    public double getTokoLatitude() {
        return tokoLatitude;
    }

    public void setTokoLatitude(double tokoLatitude) {
        this.tokoLatitude = tokoLatitude;
    }

    public double getTokoLongitude() {
        return tokoLongitude;
    }

    public void setTokoLongitude(double tokoLongitude) {
        this.tokoLongitude = tokoLongitude;
    }

    public String getAlamatAsal() {
        return alamatAsal;
    }

    public void setAlamatAsal(String alamatAsal) {
        this.alamatAsal = alamatAsal;
    }

    public String getAlamatResto() {
        return alamatResto;
    }

    public void setAlamatResto(String alamatResto) {
        this.alamatResto = alamatResto;
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

    public boolean isPakaiMPay() {
        return pakaiMPay;
    }

    public void setPakaiMPay(boolean pakaiMPay) {
        this.pakaiMPay = pakaiMPay;
    }

    public String getIdResto() {
        return idResto;
    }

    public void setIdResto(String idResto) {
        this.idResto = idResto;
    }

    public long getTotalBiayaBelanja() {
        return totalBiayaBelanja;
    }

    public void setTotalBiayaBelanja(long totalBiayaBelanja) {
        this.totalBiayaBelanja = totalBiayaBelanja;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public List<PesananFood> getPesanan() {
        return pesanan;
    }

    public void setPesanan(List<PesananFood> pesanan) {
        this.pesanan = pesanan;
    }
}

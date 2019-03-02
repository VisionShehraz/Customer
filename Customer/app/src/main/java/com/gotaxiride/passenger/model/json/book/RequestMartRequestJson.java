package com.gotaxiride.passenger.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.gotaxiride.passenger.model.Pesanan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Androgo on 12/7/2018.
 */

public class RequestMartRequestJson implements Serializable {

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
    @SerializedName("alamat_toko")
    private String alamatToko;

    @Expose
    @SerializedName("nama_toko")
    private String namaToko;

    @Expose
    @SerializedName("jarak")
    private double jarak;

    @Expose
    @SerializedName("harga")
    private long harga;

    @Expose
    @SerializedName("pakai_mpay")
    private boolean pakaiMpay;

    @Expose
    @SerializedName("estimasi_biaya")
    private long estimasiBiaya;

    @Expose
    @SerializedName("catatan")
    private String catatan;

    @Expose
    @SerializedName("pesanan")
    private List<Pesanan> pesanan = new ArrayList<>();

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

    public String getAlamatToko() {
        return alamatToko;
    }

    public void setAlamatToko(String alamatToko) {
        this.alamatToko = alamatToko;
    }

    public String getNamaToko() {
        return namaToko;
    }

    public void setNamaToko(String namaToko) {
        this.namaToko = namaToko;
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

    public boolean isPakaiMpay() {
        return pakaiMpay;
    }

    public void setPakaiMpay(boolean pakaiMpay) {
        this.pakaiMpay = pakaiMpay;
    }

    public long getEstimasiBiaya() {
        return estimasiBiaya;
    }

    public void setEstimasiBiaya(long estimasiBiaya) {
        this.estimasiBiaya = estimasiBiaya;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public List<Pesanan> getPesanan() {
        return pesanan;
    }

    public void setPesanan(List<Pesanan> pesanan) {
        this.pesanan = pesanan;
    }
}

package com.gotaxiride.passenger.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Androgo on 11/17/2018.
 */

public class MserviceRequestJson implements Serializable {

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
    @SerializedName("tanggal_pelayanan")
    private String tglService;

    @Expose
    @SerializedName("jam_pelayanan")
    private String jamService;

    @Expose
    @SerializedName("start_latitude")
    private double startLatitude;

    @Expose
    @SerializedName("start_longitude")
    private double startLongitude;

    @Expose
    @SerializedName("id_jenis")
    private int idJenis;

    @Expose
    @SerializedName("ac_type")
    private int acType;

    @Expose
    @SerializedName("quantity")
    private int quantity;

    @Expose
    @SerializedName("residential_type")
    private String residentialType;

    @Expose
    @SerializedName("problem")
    private String problem;

    @Expose
    @SerializedName("pakai_mpay")
    private int pakaiMpay;

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

    public String getTglService() {
        return tglService;
    }

    public void setTglService(String tglService) {
        this.tglService = tglService;
    }

    public String getJamService() {
        return jamService;
    }

    public void setJamService(String jamService) {
        this.jamService = jamService;
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

    public int getJenis() {

        return idJenis;
    }

    public void setJenis(int idJenis) {

        this.idJenis = idJenis;
    }

    public int getAcType() {

        return acType;
    }

    public void setAcType(int acType) {

        this.acType = acType;
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

    public void setResidentialType(String residentalType) {
        this.residentialType = residentalType;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }


    public int getPakaiMpay() {
        return pakaiMpay;
    }

    public void setPakaiMpay(int pakaiMpay) {
        this.pakaiMpay = pakaiMpay;
    }
}

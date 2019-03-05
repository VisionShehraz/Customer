package com.letsride.passenger.model.json.fcm;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Androgo on 12/7/2018.
 */

public class MartDriverRequest implements Serializable {

    @Expose
    @SerializedName("id_transaksi")
    private String idTransaksi;

    @Expose
    @SerializedName("kode_promo")
    private String kodePromo;

    @Expose
    @SerializedName("kredit_promo")
    private int KreditPromo;

    @Expose
    @SerializedName("pakai_mpay")
    private boolean pakaiMpay;

    @Expose
    @SerializedName("nama_pelanggan")
    private String namaPelanggan;

    @Expose
    @SerializedName("telepon")
    private String telepon;

    @Expose
    @SerializedName("time_accept")
    private long timeAccept;

    @Expose
    @SerializedName("reg_id_pelanggan")
    private String regIdPelanggan;

    @Expose
    @SerializedName("type")
    private int type;

    @Expose
    @SerializedName("order_fitur")
    private String orderFitur;

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(String idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public String getKodePromo() {
        return kodePromo;
    }

    public void setKodePromo(String kodePromo) {
        this.kodePromo = kodePromo;
    }

    public int getKreditPromo() {
        return KreditPromo;
    }

    public void setKreditPromo(int kreditPromo) {
        KreditPromo = kreditPromo;
    }

    public boolean isPakaiMpay() {
        return pakaiMpay;
    }

    public void setPakaiMpay(boolean pakaiMpay) {
        this.pakaiMpay = pakaiMpay;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getOrderFitur() {
        return orderFitur;
    }

    public void setOrderFitur(String orderFitur) {
        this.orderFitur = orderFitur;
    }
}

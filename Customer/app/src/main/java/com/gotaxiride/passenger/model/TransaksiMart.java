package com.gotaxiride.passenger.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Androgo on 12/7/2018.
 */

public class TransaksiMart {

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
}

package com.letsride.passenger.model.json.fcm;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Androgo on 12/8/2018.
 */

public class MartDriverResponse {

    public static final String ACCEPT = "1";
    public static final String REJECT = "0";

    @Expose
    @SerializedName("id_driver")
    private String idDriver;

    @Expose
    @SerializedName("response")
    private String response;

    @Expose
    @SerializedName("type")
    private String type;

    @Expose
    @SerializedName("id_transaksi")
    private String idTransaksi;

    public String getIdDriver() {
        return idDriver;
    }

    public void setIdDriver(String idDriver) {
        this.idDriver = idDriver;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(String idTransaksi) {
        this.idTransaksi = idTransaksi;
    }
}

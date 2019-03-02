package com.gotaxiride.passenger.model.json.book.detailTransaksi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.gotaxiride.passenger.model.DetailTransaksiMSend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fathony on 24/02/2017.
 */

public class GetDataTransaksiMSendResponse {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data_transaksi")
    @Expose
    private List<DetailTransaksiMSend> dataTransaksi = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public List<DetailTransaksiMSend> getDataTransaksi() {
        return dataTransaksi;
    }
}

package com.gotaxiride.passenger.model.json.book.detailTransaksi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.gotaxiride.passenger.model.MMartDetailTransaksi;
import com.gotaxiride.passenger.model.MMartItemRemote;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fathony on 24/02/2017.
 */

public class GetDataTransaksiMMartResponse {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data_transaksi")
    @Expose
    private List<MMartDetailTransaksi> dataTransaksi = new ArrayList<>();
    @SerializedName("list_barang")
    @Expose
    private List<MMartItemRemote> listBarang = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public List<MMartDetailTransaksi> getDataTransaksi() {
        return dataTransaksi;
    }

    public List<MMartItemRemote> getListBarang() {
        return listBarang;
    }
}

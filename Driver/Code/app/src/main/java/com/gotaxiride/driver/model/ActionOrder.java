package com.gotaxiride.driver.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by GagahIB on 19/10/2015.
 */
public class ActionOrder extends JSONObject {

    JSONObject aksi;

    public ActionOrder(String idDriver, String idTrans) {
        aksi = new JSONObject();

        try {
            aksi.put("id", idDriver);
            aksi.put("id_transaksi", idTrans);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getAksi() {
        return aksi;
    }
}

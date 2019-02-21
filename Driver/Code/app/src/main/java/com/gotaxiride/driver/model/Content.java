package com.gotaxiride.driver.model;

import android.location.Location;

import com.gotaxiride.driver.network.Log;
import com.gotaxiride.driver.service.MyConfig;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by GagahIB on 19/10/2015.
 */
public class Content implements Serializable {

    public List<String> registration_ids;
    public Map<String, String> data;

    public void addRegId(String regId) {
        if (registration_ids == null)
            registration_ids = new LinkedList<>();
        registration_ids.add(regId);
    }

    public void createDataOrder(String id, String idTrans, String response, String orderFitur) {
        if (data == null)
            data = new HashMap<>();

        data.put("type", MyConfig.orderType);
        data.put("id_driver", id);
        data.put("id_transaksi", idTrans);
        data.put("response", response);
        data.put("order_fitur", orderFitur);
    }

    public void createDataOrderFins(String id, String idTrans, String response, String orderFitur, String id_pelanggan, String foto_driver) {
        if (data == null)
            data = new HashMap<>();

        data.put("type", MyConfig.orderType);
        data.put("id_driver", id);
        data.put("id_transaksi", idTrans);
        data.put("response", response);
        data.put("order_fitur", orderFitur);
        data.put("id_pelanggan", id_pelanggan);
        data.put("driver_foto", foto_driver);
    }

    public void createDataChat(Chat chat) {
        if (data == null)
            data = new HashMap<>();
        data.put("type", MyConfig.chatType);
        data.put("nama_tujuan", chat.nama_tujuan);
        data.put("id_tujuan", chat.id_tujuan);
        data.put("isi_chat", chat.isi_chat);
        data.put("reg_id_tujuan", chat.reg_id_tujuan);
        data.put("waktu", chat.waktu);
        data.put("chat_from", String.valueOf(chat.chat_from));
    }

    public void createDataLocation(String idDriver, Location location) {
        if (data == null)
            data = new HashMap<>();
        data.put("type", MyConfig.locType);
        data.put("id_driver", idDriver);
        data.put("latitude", String.valueOf(location.getLatitude()));
        data.put("longitude", String.valueOf(location.getLongitude()));
    }

    public void createDataDummy(Map<String, String> dummy) {
        if (data == null)
            data = dummy;
    }

    public void toContentString() {
        Log.d("Content_data", data.toString());
    }
}

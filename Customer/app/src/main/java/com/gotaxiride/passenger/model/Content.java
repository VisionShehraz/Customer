package com.gotaxiride.passenger.model;

import com.gotaxiride.passenger.utils.Log;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.gotaxiride.passenger.model.FCMType.CHAT;
import static com.gotaxiride.passenger.model.FCMType.ORDER;

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

    public void createDataOrder(String id, String idTrans, String response) {
        if (data == null)
            data = new HashMap<>();

        data.put("type", ORDER + "");
        data.put("id_driver", id);
        data.put("id_transaksi", idTrans);
        data.put("response", response);
    }

    public void createDataChat(Chat chat) {
        if (data == null)
            data = new HashMap<>();
        Date date = new Date();
        String waktu = date.getHours() + ":" + date.getMinutes();
        data.put("type", CHAT + "");
        data.put("nama_tujuan", chat.nama_tujuan);
        data.put("id_tujuan", chat.id_tujuan);
        data.put("isi_chat", chat.isi_chat);
        data.put("reg_id_tujuan", chat.reg_id_tujuan);
        data.put("waktu", waktu);
        data.put("chat_from", String.valueOf(chat.chat_from));
    }

    public void createDataDummy(Map<String, String> dummy) {
        if (data == null)
            data = dummy;
    }

    public void toContentString() {
        Log.d("Content_data", data.toString());
    }
}

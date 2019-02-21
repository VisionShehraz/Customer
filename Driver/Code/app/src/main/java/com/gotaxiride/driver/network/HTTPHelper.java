package com.gotaxiride.driver.network;

import android.content.Context;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotaxiride.driver.database.DBHandler;
import com.gotaxiride.driver.database.Queries;
import com.gotaxiride.driver.model.ItemShopping;
import com.gotaxiride.driver.model.Content;
import com.gotaxiride.driver.model.DestinationGoBox;
import com.gotaxiride.driver.model.Driver;
import com.gotaxiride.driver.model.Feedback;
import com.gotaxiride.driver.model.Kendaraan;
import com.gotaxiride.driver.model.FoodShopping;
import com.gotaxiride.driver.model.TransactionHistory;
import com.gotaxiride.driver.model.Transaksi;
import com.gotaxiride.driver.preference.VehiclePreference;
import com.gotaxiride.driver.preference.SettingPreference;
import com.gotaxiride.driver.service.MyConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by GagahIB on 19/10/2016.
 */

public class HTTPHelper {

    private static HTTPHelper instance;
    Context context;
    Driver driver;

    public static HTTPHelper getInstance(Context context) {
        if (instance == null) {
            instance = new HTTPHelper();
        }
        instance.setContext(context);
        return instance;
    }

    //
    public static String getJSONFromUrl(String ur) {
        try {
            URL url = new URL(ur);
            URLConnection connection = url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static int sendToGCMServer(Content content) {
        try {
            URL url = new URL(MyConfig.URL_GCM_SERVER);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + MyConfig.GCM_KEY);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");

            ObjectMapper mapper = new ObjectMapper();
            Log.d("VALUE", mapper.writeValueAsString(content));

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            mapper.writeValue(wr, content);
            wr.flush();
            wr.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Log.d("RESPONSE", response.toString());
            JSONObject resObject = new JSONObject(response.toString());
            JSONArray jsonArray = resObject.getJSONArray("results");
            if (((JSONObject) jsonArray.get(0)).getString("message_id") != null) {
                return 1;
            } else {
                return 0;
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String sendBasicAuthenticationPOSTRequest(String ur, String data, String username, String password) {
        String credentials = (username + ":" + password);
        String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        try {
            Log.d("json_location", data);
            URL url = new URL(ur);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("Authorization", "Basic " + base64EncodedCredentials);
            connection.setDoOutput(true);
            OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());
            streamWriter.write(data);
            streamWriter.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
//            Log.d("Resp_lokasi", sb.toString());
            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Driver parseUserJSONData(Context context, String json) {
//        Log.d("String",json);
        Driver user = new Driver();
        if (json.equals("")) {
            user.name = "problem";
        } else {
            try {
                Log.d("JSON_login", json);
                JSONObject jObject = new JSONObject(json);
                if (jObject.getString("message").equals("found")) {
                    JSONArray jArray = jObject.getJSONArray("data_pelanggan");
                    JSONObject oneObject = jArray.getJSONObject(0);
                    user.id = oneObject.getString("id");
                    user.name = oneObject.getString("nama_depan") + " " + oneObject.getString("nama_belakang");
                    user.phone = oneObject.getString("no_telepon");
                    user.email = oneObject.getString("email");
                    user.password = oneObject.getString("password");
                    user.image = oneObject.getString("foto");
                    user.gcm_id = oneObject.getString("reg_id");
                    user.job = oneObject.getInt("job");

                    if (oneObject.getString("nama_bank").equals("null"))
                        user.nama_bank = "";
                    else
                        user.nama_bank = oneObject.getString("nama_bank");

                    if (oneObject.getString("atas_nama").equals("null"))
                        user.atas_nama = "";
                    else
                        user.atas_nama = oneObject.getString("atas_nama");

                    if (oneObject.getString("rekening_bank").equals("null"))
                        user.no_rek = "";
                    else
                        user.no_rek = oneObject.getString("rekening_bank");

                    user.status = 4;
                    if (oneObject.getString("rating").equals("null"))
                        user.rating = "0";
                    else
                        user.rating = oneObject.getString("rating");
                    if (oneObject.getInt("saldo") == 0)
                        user.deposit = 0;
                    else
                        user.deposit = oneObject.getInt("saldo");

                    JSONArray jArrKend = jObject.getJSONArray("data_kendaraan");
                    JSONObject kendObj = jArrKend.getJSONObject(0);
                    Kendaraan kendaraan = new Kendaraan();
                    kendaraan.id = kendObj.getString("id");
                    kendaraan.merek = kendObj.getString("merek");
                    kendaraan.warna = kendObj.getString("warna");
                    kendaraan.nopol = kendObj.getString("nomor_kendaraan");
                    kendaraan.jenisKendaraan = kendObj.getString("jenis_kendaraan");
                    kendaraan.tipe = kendObj.getString("tipe");

                    VehiclePreference kp = new VehiclePreference(context);
                    kp.insertKendaraan(kendaraan);

                } else {
                    user.name = "false";
                    user.password = jObject.getString("message");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    public static ArrayList<Feedback> parseFeedback(JSONObject jObject) {
        ArrayList<Feedback> arrFeed = new ArrayList<>();
        try {
//            Log.d("JSON_feedback", jObject.toString());
            if (jObject.getString("message").equals("success")) {
                JSONArray jArray = jObject.getJSONArray("data");
                for (int i = 0; i < jArray.length(); i++) {
                    Feedback feedback = new Feedback();
                    JSONObject oneObject = jArray.getJSONObject(i);
                    feedback.id = (i + 1) + "";
                    feedback.catatan = oneObject.getString("catatan");
                    feedback.waktu = oneObject.getLong("update_at");
                    arrFeed.add(feedback);
                }
            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrFeed;
    }

    public static ArrayList<TransactionHistory> parseRiwayatTransaksi(JSONObject jObject) {
        ArrayList<TransactionHistory> arrRiwayat = new ArrayList<>();
        try {
//            Log.d("JSON_riwayat_transaksi", jObject.toString());
            if (jObject.getString("message").equals("success")) {
                JSONArray jArray = jObject.getJSONArray("data");
                for (int i = 0; i < jArray.length(); i++) {
                    TransactionHistory riwayat = new TransactionHistory();
                    JSONObject oneObject = jArray.getJSONObject(i);
                    riwayat.id_transaksi = oneObject.getString("id_transaksi");
                    riwayat.waktu_riwayat = oneObject.getString("waktu_riwayat");
                    riwayat.nama_depan = oneObject.getString("nama_depan");
                    riwayat.nama_belakang = oneObject.getString("nama_belakang");
                    riwayat.jarak = oneObject.getString("jarak");
                    riwayat.debit = oneObject.getInt("debit");
                    riwayat.kredit = oneObject.getInt("kredit");
                    riwayat.saldo = oneObject.getInt("saldo");
                    riwayat.tipe_transaksi = oneObject.getString("tipe_transaksi");
                    riwayat.id_fitur = oneObject.getString("id_fitur");
                    riwayat.fitur = oneObject.getString("fitur");
                    riwayat.keterangan = oneObject.getString("keterangan");
                    arrRiwayat.add(riwayat);
                }
            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrRiwayat;
    }

    public static Driver parseUserSync(Context context, String json) {
        Driver user = new Driver();
        try {
            JSONObject jObject = new JSONObject(json);
            if (jObject.getString("message").equals("success")) {
                JSONArray jArray = jObject.getJSONArray("data_driver");
                JSONObject oneObject = jArray.getJSONObject(0);
                Log.d("data_sync_user", oneObject.toString());

                user.id = oneObject.getString("id");
                user.name = oneObject.getString("nama_depan") + " " + oneObject.getString("nama_belakang");
                user.phone = oneObject.getString("no_telepon");
                user.email = oneObject.getString("email");
                user.deposit = oneObject.getInt("saldo");
                //                    user.password = oneObject.getString("password");
                user.image = oneObject.getString("foto");
                user.gcm_id = oneObject.getString("reg_id");
                user.job = oneObject.getInt("job");
                user.status = jObject.getInt("driver_status");
                if (oneObject.getString("rating").equals("null"))
                    user.rating = "0";
                else
                    user.rating = oneObject.getString("rating");
                if (oneObject.getInt("saldo") == 0)
                    user.deposit = 0;
                else
                    user.deposit = oneObject.getInt("saldo");

                SettingPreference sp = new SettingPreference(context);
//                String swit = "";
//                if (oneObject.getInt("status_config") == 1)
//                    swit  = "ON";
//                else
//                    swit = "OFF";
//
//                sp.updateKerja(swit);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static Transaksi parseTransaksi(Context context, String json) {
        Transaksi transaksi = new Transaksi();
        try {
            JSONObject jObject = new JSONObject(json);
            if (jObject.getString("message").equals("success")) {
                JSONArray jArray = jObject.getJSONArray("data_transaksi");
                if (jArray.length() > 0) {
                    JSONObject oneObject = jArray.getJSONObject(0);
                    Log.d("data_sync_transaksi", oneObject.toString());
                    transaksi.id_transaksi = oneObject.getString("id_transaksi");
                    transaksi.id_pelanggan = oneObject.getString("id_pelanggan");
                    transaksi.reg_id_pelanggan = oneObject.getString("reg_id_pelanggan");
                    transaksi.order_fitur = oneObject.getString("order_fitur");
                    transaksi.start_latitude = oneObject.getString("start_latitude");
                    transaksi.start_longitude = oneObject.getString("start_longitude");
                    transaksi.end_latitude = oneObject.getString("end_latitude");
                    transaksi.end_longitude = oneObject.getString("end_longitude");
                    transaksi.jarak = oneObject.getString("jarak");
                    transaksi.harga = oneObject.getString("harga");
                    transaksi.waktu_order = oneObject.getString("waktu_order");
                    transaksi.waktu_selesai = oneObject.getString("waktu_selesai");
                    transaksi.alamat_asal = oneObject.getString("alamat_asal");
                    transaksi.alamat_tujuan = oneObject.getString("alamat_tujuan");
                    transaksi.rate = oneObject.getString("rate");
                    transaksi.kredit_promo = oneObject.getString("kredit_promo");
                    transaksi.kode_promo = oneObject.getString("kode_promo");
                    transaksi.biaya_akhir = oneObject.getString("biaya_akhir");
                    transaksi.pakai_mpay = oneObject.getString("pakai_mpay");
                    transaksi.nama_pelanggan = oneObject.getString("nama_depan") + " " + oneObject.getString("nama_belakang");
                    transaksi.telepon_pelanggan = oneObject.getString("telepon");
                } else {
                    transaksi.id_transaksi = "0";
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return transaksi;
    }

    public static Transaksi parseDataMmart(Transaksi transaksi, JSONObject jObject) {
        Log.d("JSON_data_mmart", jObject.toString());
        try {
            if (jObject.getString("message").equals("success")) {
                JSONArray jArray = jObject.getJSONArray("data_transaksi");
                JSONObject oneObject = jArray.getJSONObject(0);
                transaksi.id_transaksi = oneObject.getString("id_transaksi");
                transaksi.id_pelanggan = oneObject.getString("id_pelanggan");
                transaksi.order_fitur = oneObject.getString("order_fitur");
                transaksi.start_latitude = oneObject.getString("start_latitude");
                transaksi.start_longitude = oneObject.getString("start_longitude");
                transaksi.end_latitude = oneObject.getString("end_latitude");
                transaksi.end_longitude = oneObject.getString("end_longitude");
                transaksi.jarak = oneObject.getString("jarak");
                transaksi.harga = oneObject.getString("harga");
                transaksi.waktu_order = oneObject.getString("waktu_order");
                transaksi.waktu_selesai = oneObject.getString("waktu_selesai");
                transaksi.alamat_asal = oneObject.getString("alamat_asal");
                transaksi.alamat_tujuan = oneObject.getString("alamat_tujuan");
                transaksi.kode_promo = oneObject.getString("kode_promo");
                transaksi.kredit_promo = oneObject.getString("kredit_promo");
                transaksi.biaya_akhir = oneObject.getString("biaya_akhir");
                transaksi.pakai_mpay = oneObject.getString("pakai_mpay");
                transaksi.nama_toko = oneObject.getString("nama_toko");
                transaksi.estimasi_biaya = oneObject.getInt("estimasi_biaya");
                transaksi.harga_akhir = oneObject.getString("harga_akhir");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return transaksi;
    }

    public static Transaksi parseDataMfood(Transaksi transaksi, JSONObject jObject) {
        Log.d("JSON_data_mfood", jObject.toString());
        try {
            if (jObject.getString("message").equals("success")) {
                JSONArray jArray = jObject.getJSONArray("data_transaksi");
                JSONObject oneObject = jArray.getJSONObject(0);
                transaksi.id_transaksi = oneObject.getString("id_transaksi");
                transaksi.id_pelanggan = oneObject.getString("id_pelanggan");
                transaksi.order_fitur = oneObject.getString("order_fitur");
                transaksi.start_latitude = oneObject.getString("start_latitude");
                transaksi.start_longitude = oneObject.getString("start_longitude");
                transaksi.end_latitude = oneObject.getString("end_latitude");
                transaksi.end_longitude = oneObject.getString("end_longitude");
                transaksi.jarak = oneObject.getString("jarak");
                transaksi.harga = oneObject.getString("harga");
                transaksi.waktu_order = oneObject.getString("waktu_order");
                transaksi.waktu_selesai = oneObject.getString("waktu_selesai");
                transaksi.alamat_asal = oneObject.getString("alamat_asal");
                transaksi.alamat_tujuan = oneObject.getString("alamat_tujuan");
                transaksi.kode_promo = oneObject.getString("kode_promo");
                transaksi.kredit_promo = oneObject.getString("kredit_promo");
                transaksi.biaya_akhir = oneObject.getString("biaya_akhir");
                transaksi.pakai_mpay = oneObject.getString("pakai_mpay");
//                transaksi.nama_toko = oneObject.getString("nama_toko");
//                transaksi.estimasi_biaya = oneObject.getInt("estimasi_biaya");
                transaksi.harga_akhir = oneObject.getString("harga_akhir");
                transaksi.nama_resto = oneObject.getString("nama_resto");
                transaksi.total_biaya = oneObject.getInt("total_biaya");
                transaksi.telepon_resto = oneObject.getString("kontak_telepon");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return transaksi;
    }

    public static Transaksi parseDataMbox(Transaksi transaksi, JSONObject jObject) {
        Log.d("json_sync_mbox", jObject.toString());
        try {
            if (jObject.getString("message").equals("success")) {
                JSONArray jArray = jObject.getJSONArray("data_transaksi");
                JSONObject oneObject = jArray.getJSONObject(0);
                transaksi.id_transaksi = oneObject.getString("id_transaksi");
                transaksi.id_pelanggan = oneObject.getString("id_pelanggan");
                transaksi.order_fitur = oneObject.getString("order_fitur");
                transaksi.start_latitude = oneObject.getString("start_latitude");
                transaksi.start_longitude = oneObject.getString("start_longitude");
                transaksi.end_latitude = oneObject.getString("end_latitude");
                transaksi.end_longitude = oneObject.getString("end_longitude");
                transaksi.jarak = oneObject.getString("jarak");
                transaksi.harga = oneObject.getString("harga");
                transaksi.waktu_order = oneObject.getString("waktu_order");
                transaksi.waktu_selesai = oneObject.getString("waktu_selesai");
                transaksi.alamat_asal = oneObject.getString("alamat_asal");
                transaksi.alamat_tujuan = oneObject.getString("alamat_tujuan");
                transaksi.kode_promo = oneObject.getString("kode_promo");
                transaksi.kredit_promo = oneObject.getString("kredit_promo");
                transaksi.biaya_akhir = oneObject.getString("biaya_akhir");
                transaksi.pakai_mpay = oneObject.getString("pakai_mpay");
                transaksi.shipper = oneObject.getInt("shipper");
                transaksi.nama_barang = oneObject.getString("nama_barang");
//                transaksi.estimasi_biaya = oneObject.getString("estimasi_biaya");
//                transaksi.harga_akhir = oneObject.getString("harga_akhir");
                transaksi.waktu_pelayanan = oneObject.getString("waktu_pelayanan");
                transaksi.kendaraan_angkut = oneObject.getString("kendaraan_angkut");
                transaksi.jam_pelayanan = oneObject.getString("jam_pelayanan");
                transaksi.tanggal_pelayanan = oneObject.getString("tanggal_pelayanan");
                transaksi.asuransi = oneObject.getInt("asuransi");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return transaksi;
    }

    public static Transaksi parseDataMsend(Transaksi transaksi, JSONObject jObject) {
        try {
            if (jObject.getString("message").equals("success")) {
                JSONArray jArray = jObject.getJSONArray("data_transaksi");
                JSONObject oneObject = jArray.getJSONObject(0);
                transaksi.id_transaksi = oneObject.getString("id_transaksi");
                transaksi.id_pelanggan = oneObject.getString("id_pelanggan");
                transaksi.order_fitur = oneObject.getString("order_fitur");
                transaksi.start_latitude = oneObject.getString("start_latitude");
                transaksi.start_longitude = oneObject.getString("start_longitude");
                transaksi.end_latitude = oneObject.getString("end_latitude");
                transaksi.end_longitude = oneObject.getString("end_longitude");
                transaksi.jarak = oneObject.getString("jarak");
                transaksi.harga = oneObject.getString("harga");
                transaksi.waktu_order = oneObject.getString("waktu_order");
                transaksi.waktu_selesai = oneObject.getString("waktu_selesai");
                transaksi.alamat_asal = oneObject.getString("alamat_asal");
                transaksi.alamat_tujuan = oneObject.getString("alamat_tujuan");
                transaksi.kode_promo = oneObject.getString("kode_promo");
                transaksi.kredit_promo = oneObject.getString("kredit_promo");
                transaksi.biaya_akhir = oneObject.getString("biaya_akhir");
                transaksi.pakai_mpay = oneObject.getString("pakai_mpay");
                transaksi.nama_barang = oneObject.getString("nama_barang");
                transaksi.nama_pengirim = oneObject.getString("nama_pengirim");
                transaksi.telepon_pengirim = oneObject.getString("telepon_pengirim");
                transaksi.nama_penerima = oneObject.getString("nama_penerima");
                transaksi.telepon_penerima = oneObject.getString("telepon_penerima");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return transaksi;
    }

    public static Transaksi parseDataMservice(Transaksi transaksi, JSONObject jObject) {
        try {
            if (jObject.getString("message").equals("success")) {
                JSONArray jArray = jObject.getJSONArray("data_transaksi");
                JSONObject oneObject = jArray.getJSONObject(0);
                transaksi.id_transaksi = oneObject.getString("id_transaksi");
                transaksi.id_pelanggan = oneObject.getString("id_pelanggan");
                transaksi.order_fitur = oneObject.getString("order_fitur");
                transaksi.start_latitude = oneObject.getString("start_latitude");
                transaksi.start_longitude = oneObject.getString("start_longitude");
                transaksi.end_latitude = oneObject.getString("end_latitude");
                transaksi.end_longitude = oneObject.getString("end_longitude");
                transaksi.jarak = oneObject.getString("jarak");
                transaksi.harga = oneObject.getString("harga");
                transaksi.waktu_order = oneObject.getString("waktu_order");
                transaksi.waktu_selesai = oneObject.getString("waktu_selesai");
                transaksi.alamat_asal = oneObject.getString("alamat_asal");
                transaksi.alamat_tujuan = oneObject.getString("alamat_tujuan");
                transaksi.kode_promo = oneObject.getString("kode_promo");
                transaksi.kredit_promo = oneObject.getString("kredit_promo");
                transaksi.biaya_akhir = oneObject.getString("biaya_akhir");
                transaksi.pakai_mpay = oneObject.getString("pakai_mpay");
                transaksi.jenis_service = oneObject.getString("jenis_service");
                transaksi.ac_type = oneObject.getString("ac_type");
                transaksi.tanggal_pelayanan = oneObject.getString("tanggal_pelayanan");
                transaksi.jam_pelayanan = oneObject.getString("jam_pelayanan");
                transaksi.quantity = oneObject.getString("quantity");
                transaksi.residential_type = oneObject.getString("residential_type");
                transaksi.problem = oneObject.getString("problem");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return transaksi;
    }

    public static Transaksi parseDataMmassage(Transaksi transaksi, JSONObject jObject) {
        try {
            if (jObject.getString("message").equals("success")) {
                JSONArray jArray = jObject.getJSONArray("data_transaksi");
                JSONObject oneObject = jArray.getJSONObject(0);
                transaksi.id_transaksi = oneObject.getString("id_transaksi");
                transaksi.id_pelanggan = oneObject.getString("id_pelanggan");
                transaksi.order_fitur = oneObject.getString("order_fitur");
                transaksi.start_latitude = oneObject.getString("start_latitude");
                transaksi.start_longitude = oneObject.getString("start_longitude");
                transaksi.harga = oneObject.getString("harga");
                transaksi.waktu_order = oneObject.getString("waktu_order");
                transaksi.alamat_asal = oneObject.getString("alamat_asal");
                transaksi.kode_promo = oneObject.getString("kode_promo");
                transaksi.kredit_promo = oneObject.getString("kredit_promo");
                transaksi.biaya_akhir = oneObject.getString("biaya_akhir");
                transaksi.pakai_mpay = oneObject.getString("pakai_mpay");
                transaksi.kota = oneObject.getString("kota");
                transaksi.tanggal_pelayanan = oneObject.getString("tanggal_pelayanan");
                transaksi.massage_menu = oneObject.getString("massage_menu");
                transaksi.jam_pelayanan = oneObject.getString("jam_pelayanan");
                transaksi.lama_pelayanan = oneObject.getString("lama_pelayanan");
                transaksi.pelanggan_gender = oneObject.getString("pelanggan_gender");
                transaksi.prefer_gender = oneObject.getString("prefer_gender");
                transaksi.catatan_tambahan = oneObject.getString("catatan_tambahan");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return transaksi;
    }

    public static ArrayList<ItemShopping> parseBarangBelanja(JSONObject jObject) {
        Log.d("JSON_riwayat_transaksi", jObject.toString());
        ArrayList<ItemShopping> arrBarang = new ArrayList<>();
        try {
            if (jObject.getString("message").equals("success")) {
                JSONArray jArray = jObject.getJSONArray("list_barang");
                for (int i = 0; i < jArray.length(); i++) {
                    ItemShopping barang_belanja = new ItemShopping();
                    JSONObject oneObject = jArray.getJSONObject(i);
                    barang_belanja.id_barang = i;
                    barang_belanja.nama_barang = oneObject.getString("nama_barang");
                    barang_belanja.jumlah_barang = oneObject.getString("jumlah");
                    barang_belanja.isChecked = 0;
                    arrBarang.add(barang_belanja);
                }
            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrBarang;
    }

    public static ArrayList<FoodShopping> parseMakananBelanja(JSONObject jObject) {
        Log.d("JSON_makanan_belanja", jObject.toString());
        ArrayList<FoodShopping> arrBarang = new ArrayList<>();
        try {
            if (jObject.getString("message").equals("success")) {
                JSONArray jArray = jObject.getJSONArray("list_barang");
                for (int i = 0; i < jArray.length(); i++) {
                    FoodShopping barang_belanja = new FoodShopping();
                    JSONObject oneObject = jArray.getJSONObject(i);
                    barang_belanja.id_makanan = oneObject.getInt("id_makanan");
                    barang_belanja.nama_makanan = oneObject.getString("nama_menu");
                    barang_belanja.jumlah_makanan = oneObject.getInt("jumlah");
                    barang_belanja.harga_makanan = oneObject.getInt("harga");
                    barang_belanja.catatan_makanan = oneObject.getString("catatan");
                    arrBarang.add(barang_belanja);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrBarang;
    }

    public static ArrayList<DestinationGoBox> parseDestinasiMbox(JSONObject jObject) {
        Log.d("JSON_destinasi_mbox", jObject.toString());
        ArrayList<DestinationGoBox> arrDestinasi = new ArrayList<>();
        try {
            if (jObject.getString("message").equals("success")) {
                JSONArray jArray = jObject.getJSONArray("list_destinasi");
                for (int i = 0; i < jArray.length(); i++) {
                    DestinationGoBox destinationGoBox = new DestinationGoBox();
                    JSONObject oneObject = jArray.getJSONObject(i);
                    destinationGoBox.id = oneObject.getInt("id");
                    destinationGoBox.id_transaksi = oneObject.getInt("id_transaksi");
                    destinationGoBox.lokasi = oneObject.getString("lokasi");
                    destinationGoBox.detail_lokasi = oneObject.getString("detail_lokasi");
                    destinationGoBox.latitude = oneObject.getString("latitude");
                    destinationGoBox.longitude = oneObject.getString("longitude");
                    destinationGoBox.nama_penerima = oneObject.getString("nama_penerima");
                    destinationGoBox.telepon_penerima = oneObject.getString("telepon_penerima");
                    destinationGoBox.instruksi = oneObject.getString("instruksi");
                    arrDestinasi.add(destinationGoBox);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrDestinasi;
    }

    private void setContext(Context context) {
        this.context = context;
        Queries que = new Queries(new DBHandler(context));
        driver = que.getDriver();
        que.closeDatabase();
    }

    public void login(JSONObject dataLogin, final NetworkActionResult actionResult) {

        Log.d("Jalankan_login", "Ya");
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_DRIVER_LOGIN, dataLogin, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.d(MyConfig.TAG_response+" JSON", response.toString());
                        actionResult.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.d(MyConfig.TAG_response+" JSON", error.toString());
                        actionResult.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json; charset=UTF-8");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsObjRequest);
    }



    public void logout(JSONObject dataLogin, final NetworkActionResult actionResult) {

        Log.d("Jalankan_login", "Ya");
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_DRIVER_LOGOUT, dataLogin, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.d(MyConfig.TAG_response+" JSON", response.toString());
                        actionResult.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.d(MyConfig.TAG_response+" JSON", error.toString());
                        actionResult.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json; charset=UTF-8");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsObjRequest);
    }


    public void tesConnection(JSONObject dataOrder, final NetworkActionResult actionResult) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_TEST_SERVER, dataOrder, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.d(MyConfig.TAG_response+" JSON", response.toString());
                        actionResult.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.d(MyConfig.TAG_response+" JSON", error.toString());
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                Log.d("Auth", base64EncodedCredentials);
                return headers;
            }

//            @Override
//            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
//
//                try {
//                    String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
//                    actionResult.onSuccess(new JSONObject(jsonString));
//                    Log.d("Res_Test", jsonString);
//
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                return super.parseNetworkResponse(response);
//            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsObjRequest);
    }

    public void acceptOrder(JSONObject dataOrder, final NetworkActionResult actionResult) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_DRIVER_ACCEPT, dataOrder, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.d(MyConfig.TAG_response+" JSON", response.toString());
                        actionResult.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.d(MyConfig.TAG_response+" JSON", error.toString());
                        actionResult.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                Log.d("Auth", base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsObjRequest);
    }

    public void startOrder(JSONObject dataOrder, final NetworkActionResult actionResult) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_DRIVER_START, dataOrder, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.d(MyConfig.TAG_response+" JSON", response.toString());
                        actionResult.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.d(MyConfig.TAG_response+" JSON", error.toString());
                        actionResult.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                Log.d("Auth", base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsObjRequest);
    }

    public void cancelOrder(JSONObject dataOrder, final NetworkActionResult actionResult) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_DRIVER_CANCEL, dataOrder, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(MyConfig.TAG_response + " JSON", response.toString());
                        actionResult.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(MyConfig.TAG_response + " JSON", error.toString());
                        actionResult.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                Log.d("Auth", base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsObjRequest);
    }

    public void finishOrder(JSONObject dataOrder, final NetworkActionResult actionResult) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_DRIVER_FINISH, dataOrder, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.d(MyConfig.TAG_response+" JSON", response.toString());
                        actionResult.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.d(MyConfig.TAG_response+" JSON", error.toString());
                        actionResult.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                Log.d("Auth", base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsObjRequest);
    }

    public void rejectOrder(JSONObject dataOrder, final NetworkActionResult actionResult) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_DRIVER_REJECT, dataOrder, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.d(MyConfig.TAG_response+" JSON", response.toString());
                        actionResult.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.d(MyConfig.TAG_response+" JSON", error.toString());
                        actionResult.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                Log.d("Auth", base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsObjRequest);
    }

    public void rateUser(JSONObject dataOrder, final NetworkActionResult actionResult) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_DRIVER_RATE_USER, dataOrder, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.d(MyConfig.TAG_response+" JSON", response.toString());
                        actionResult.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(MyConfig.TAG_response + " JSON", error.toString());
                        actionResult.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                Log.d("Auth", base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsObjRequest);
    }

    public void updateProfile(JSONObject dataOrder, final NetworkActionResult actionResult) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_DRIVER_UPDATE_PROFILE, dataOrder, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.d(MyConfig.TAG_response+" JSON", response.toString());
                        actionResult.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(MyConfig.TAG_response + " JSON", error.toString());
                        actionResult.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                Log.d("Auth", base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsObjRequest);
    }

    public void updateRekening(JSONObject dataOrder, final NetworkActionResult actionResult) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_DRIVER_UPDATE_REKENING, dataOrder, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.d(MyConfig.TAG_response+" JSON", response.toString());
                        actionResult.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(MyConfig.TAG_response + " JSON", error.toString());
                        actionResult.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                Log.d("Auth", base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsObjRequest);
    }

    public void updateKendaraan(JSONObject dataOrder, final NetworkActionResult actionResult) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_DRIVER_UPDATE_KENDARAAN, dataOrder, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.d(MyConfig.TAG_response+" JSON", response.toString());
                        actionResult.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(MyConfig.TAG_response + " JSON", error.toString());
                        actionResult.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                Log.d("Auth", base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsObjRequest);
    }

    public void checkVersionApp(JSONObject dataOrder, final NetworkActionResult actionResult) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_DRIVER_CHECK_VERSION, dataOrder, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.d(MyConfig.TAG_response+" JSON", response.toString());
                        actionResult.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(MyConfig.TAG_response + " JSON", error.toString());
                        actionResult.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                Log.d("Auth", base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsObjRequest);
    }

    public void checkStatusTransaksi(JSONObject dataOrder, final NetworkActionResult actionResult) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_DRIVER_CHECK_STATUS_TRANSAKSI, dataOrder, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.d(MyConfig.TAG_response+" JSON", response.toString());
                        actionResult.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(MyConfig.TAG_response + " JSON", error.toString());
                        actionResult.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                Log.d("Auth", base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsObjRequest);
    }

    public void getFeedback(JSONObject dataOrder, final NetworkActionResult actionResult) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_DRIVER_GET_FEEDBACK, dataOrder, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.d(MyConfig.TAG_response+" JSON", response.toString());
                        actionResult.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(MyConfig.TAG_response + " JSON", error.toString());
                        actionResult.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                Log.d("Auth", base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsObjRequest);
    }

    public void getRiwayatTransaksi(JSONObject dataOrder, final NetworkActionResult actionResult) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_DRIVER_GET_RIWAYAT_TRANSAKSI, dataOrder, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.d(MyConfig.TAG_response+" JSON", response.toString());
                        actionResult.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.d(MyConfig.TAG_response+" JSON", error.toString());
                        actionResult.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                Log.d("Auth", base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsObjRequest);
    }

    public void verifikasiTopUp(JSONObject dataOrder, final NetworkActionResult actionResult) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_DRIVER_VERIVY_TOPUP, dataOrder, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.d(MyConfig.TAG_response+" JSON", response.toString());
                        actionResult.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(MyConfig.TAG_response+" JSON", error.toString());
                        actionResult.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                Log.d("Auth", base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsObjRequest);
    }

    public void settingUangBelanja(JSONObject dataOrder, final NetworkActionResult actionResult) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_DRIVER_SETTING_UANG_BELANJA, dataOrder, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.d(MyConfig.TAG_response+" JSON", response.toString());
                        actionResult.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.d(MyConfig.TAG_response+" JSON", error.toString());
                        actionResult.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                Log.d("Auth", base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsObjRequest);
    }

    public void withdrawal(JSONObject dataOrder, final NetworkActionResult actionResult) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_DRIVER_WITHDRAWAL, dataOrder, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.d(MyConfig.TAG_response+" JSON", response.toString());
                        actionResult.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(MyConfig.TAG_response + " JSON", error.toString());
                        actionResult.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                Log.d("Auth", base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsObjRequest);
    }

    public void turningOn(JSONObject dataOrder, final NetworkActionResult actionResult) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_DRIVER_TURNING_ON, dataOrder, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.d(MyConfig.TAG_response+" JSON", response.toString());
                        actionResult.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.d(MyConfig.TAG_response+" JSON", error.toString());
                        actionResult.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                Log.d("Auth", base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
//        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(jsObjRequest);
    }

    public void syncAccount(JSONObject dataOrder, final NetworkActionResult actionResult) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_DRIVER_SYNC_ACCOUNT, dataOrder, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(MyConfig.TAG_response + " JSON_sync", response.toString());
                        actionResult.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.d(MyConfig.TAG_response+" JSON", error.toString());
                        actionResult.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                Log.d("Auth", base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsObjRequest);
    }

    public void getTransaksiMmart(JSONObject dataOrder, final NetworkActionResult actionResult) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_DRIVER_GET_TRANSAKSI_MMART, dataOrder, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(MyConfig.TAG_response + " JSON", response.toString());
                        actionResult.onSuccess(response);
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(MyConfig.TAG_response + " JSON", error.toString());
                        actionResult.onError(error.toString());
                    }

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 4, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(jsObjRequest);
    }

    public void getTransaksiMfood(JSONObject dataOrder, final NetworkActionResult actionResult) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_DRIVER_GET_TRANSAKSI_MFOOD, dataOrder, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(MyConfig.TAG_response + " JSON", response.toString());
                        actionResult.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(MyConfig.TAG_response + " JSON", error.toString());
                        actionResult.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsObjRequest);
    }

    public void getTransaksiMbox(JSONObject dataOrder, final NetworkActionResult actionResult) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_DRIVER_GET_TRANSAKSI_MBOX, dataOrder, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(MyConfig.TAG_response + " JSON", response.toString());
                        actionResult.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(MyConfig.TAG_response + " JSON", error.toString());
                        actionResult.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsObjRequest);
    }

    public void getTransaksiMsend(JSONObject dataOrder, final NetworkActionResult actionResult) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_DRIVER_GET_TRANSAKSI_MSEND, dataOrder, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(MyConfig.TAG_response + " JSON", response.toString());
                        actionResult.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(MyConfig.TAG_response + " JSON", error.toString());
                        actionResult.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsObjRequest);
    }

    public void getTransaksiMservice(JSONObject dataOrder, final NetworkActionResult actionResult) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_DRIVER_GET_TRANSAKSI_MSERVICE, dataOrder, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(MyConfig.TAG_response + " JSON", response.toString());
                        actionResult.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(MyConfig.TAG_response + " JSON", error.toString());
                        actionResult.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsObjRequest);
    }

    public void getTransaksiMmassage(JSONObject dataOrder, final NetworkActionResult actionResult) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_DRIVER_GET_TRANSAKSI_MMASSAGE, dataOrder, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(MyConfig.TAG_response + " JSON", response.toString());
                        actionResult.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(MyConfig.TAG_response + " JSON", error.toString());
                        actionResult.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsObjRequest);
    }

    public void driverFinishMamrt(JSONObject dataOrder, final NetworkActionResult actionResult) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_DRIVER_FINISH_MMART, dataOrder, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(MyConfig.TAG_response + " JSON", response.toString());
                        actionResult.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(MyConfig.TAG_response + " JSON", error.toString());
                        actionResult.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsObjRequest);
    }

    public void driverFinishMfood(JSONObject dataOrder, final NetworkActionResult actionResult) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, MyConfig.URL_DRIVER_FINISH_MFOOD, dataOrder, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(MyConfig.TAG_response + " JSON", response.toString());
                        actionResult.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(MyConfig.TAG_response + " JSON", error.toString());
                        actionResult.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = (driver.email + ":" + driver.password);
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(jsObjRequest);
    }


}

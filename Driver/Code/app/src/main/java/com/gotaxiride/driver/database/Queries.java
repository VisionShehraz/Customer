package com.gotaxiride.driver.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gotaxiride.driver.model.ItemShopping;
import com.gotaxiride.driver.model.Chat;
import com.gotaxiride.driver.model.DestinationGoBox;
import com.gotaxiride.driver.model.Driver;
import com.gotaxiride.driver.model.Feedback;
import com.gotaxiride.driver.model.FoodShopping;
import com.gotaxiride.driver.model.TransactionHistory;
import com.gotaxiride.driver.model.Transaksi;
import com.gotaxiride.driver.network.Log;

import java.util.ArrayList;

/**
 * Created by GagahIB on 2/6/2016.
 */

public class Queries {

    private SQLiteDatabase writableDatabase;
    private SQLiteDatabase readableDatabase;
    private DBHandler dbHandler;

    public Queries(DBHandler dbHandler) {
        this.dbHandler = dbHandler;
        initilizationDB();
    }

    public void initilizationDB() {
        this.writableDatabase = dbHandler.getWritableDatabase();
        this.readableDatabase = dbHandler.getReadableDatabase();
    }

    public void truncate(String table) {
        writableDatabase.execSQL("DELETE FROM " + table);
    }

    public void closeDatabase() {
        writableDatabase.close();
        readableDatabase.close();
    }

    public void insertChat(Chat chat) {
        ContentValues values = new ContentValues();
        values.put(DBHandler.COLUMN_ID_TUJUAN, chat.id_tujuan);
        values.put(DBHandler.COLUMN_NAMA_TUJUAN, chat.nama_tujuan);
        values.put(DBHandler.COLUMN_REG_ID_TUJUAN, chat.reg_id_tujuan);
        values.put(DBHandler.COLUMN_ISI_CHAT, chat.isi_chat);
        values.put(DBHandler.COLUMN_WAKTU, chat.waktu);
        values.put(DBHandler.COLUMN_STATUS, chat.status);
        values.put(DBHandler.COLUMN_CHAT_FROM, chat.chat_from);
        writableDatabase.replace(DBHandler.TABLE_CHAT, null, values);
    }

    public void insertInProgressTransaksi(Transaksi transaksi) {
        ContentValues values = new ContentValues();
        values.put(DBHandler.COLUMN_ID_TRANSAKSI, transaksi.id_transaksi);
        values.put(DBHandler.COLUMN_ID_PELANGGAN, transaksi.id_pelanggan);
        values.put(DBHandler.COLUMN_REG_ID_PELANGGAN, transaksi.reg_id_pelanggan);
        values.put(DBHandler.COLUMN_ORDER_FITUR, transaksi.order_fitur);
        values.put(DBHandler.COLUMN_START_LATITUDE, transaksi.start_latitude);
        values.put(DBHandler.COLUMN_START_LONGITUDE, transaksi.start_longitude);
        values.put(DBHandler.COLUMN_END_LATITUDE, transaksi.end_latitude);
        values.put(DBHandler.COLUMN_END_LONGITUDE, transaksi.end_longitude);
        values.put(DBHandler.COLUMN_JARAK, transaksi.jarak);
        values.put(DBHandler.COLUMN_HARGA, transaksi.harga);
        values.put(DBHandler.COLUMN_WAKTU_ORDER, transaksi.waktu_order);
        values.put(DBHandler.COLUMN_WAKTU_SELESAI, transaksi.waktu_selesai);
        values.put(DBHandler.COLUMN_ALAMAT_ASAL, transaksi.alamat_asal);
        values.put(DBHandler.COLUMN_ALAMAT_TUJUAN, transaksi.alamat_tujuan);
        values.put(DBHandler.COLUMN_RATE, transaksi.rate);
        values.put(DBHandler.COLUMN_KODE_PROMO, transaksi.kode_promo);
        values.put(DBHandler.COLUMN_KREDIT_PROMO, transaksi.kredit_promo);
        values.put(DBHandler.COLUMN_PAKAI_MPAY, transaksi.pakai_mpay);
        values.put(DBHandler.COLUMN_NAMA_PELANGGAN, transaksi.nama_pelanggan);
        values.put(DBHandler.COLUMN_TELEPON_PELANGGAN, transaksi.telepon_pelanggan);
        values.put(DBHandler.COLUMN_NAMA_BARANG, transaksi.nama_barang);
        values.put(DBHandler.COLUMN_NAMA_TOKO, transaksi.nama_toko);
        values.put(DBHandler.COLUMN_TANGGAL_PELAYANAN, transaksi.tanggal_pelayanan);
        values.put(DBHandler.COLUMN_NAMA_PENGIRIM, transaksi.nama_pengirim);
        values.put(DBHandler.COLUMN_NAMA_PENERIMA, transaksi.nama_penerima);
        values.put(DBHandler.COLUMN_TELEPON_PENGIRIM, transaksi.telepon_pengirim);
        values.put(DBHandler.COLUMN_TELEPON_PENERIMA, transaksi.telepon_penerima);
        values.put(DBHandler.COLUMN_JAM_PELAYANAN, transaksi.jam_pelayanan);
        values.put(DBHandler.COLUMN_LAMA_PELAYANAN, transaksi.lama_pelayanan);
        values.put(DBHandler.COLUMN_WAKTU_PELAYANAN, transaksi.waktu_pelayanan);
        values.put(DBHandler.COLUMN_KENDARAAN_ANGKUT, transaksi.kendaraan_angkut);
        values.put(DBHandler.COLUMN_QUANTITY, transaksi.quantity);
        values.put(DBHandler.COLUMN_RESIDENTIAL_TYPE, transaksi.residential_type);
        values.put(DBHandler.COLUMN_PROBLEM, transaksi.problem);
        values.put(DBHandler.COLUMN_JENIS_SERVICE, transaksi.jenis_service);
        values.put(DBHandler.COLUMN_AC_TYPE, transaksi.ac_type);
        values.put(DBHandler.COLUMN_ASURANSI, transaksi.asuransi);
        values.put(DBHandler.COLUMN_SHIPPER, transaksi.shipper);
        values.put(DBHandler.COLUMN_ESTIMASI_BIAYA, transaksi.estimasi_biaya);
        values.put(DBHandler.COLUMN_KOTA, transaksi.kota);
        values.put(DBHandler.COLUMN_MASSAGE_MENU, transaksi.massage_menu);
        values.put(DBHandler.COLUMN_PELANGGAN_GENDER, transaksi.pelanggan_gender);
        values.put(DBHandler.COLUMN_PREFER_GENDER, transaksi.prefer_gender);
        values.put(DBHandler.COLUMN_CATATAN_TAMBAHAN, transaksi.catatan_tambahan);
        values.put(DBHandler.COLUMN_NAMA_RESTO, transaksi.nama_resto);
        values.put(DBHandler.COLUMN_TELEPON_RESTO, transaksi.telepon_resto);
        values.put(DBHandler.COLUMN_TOTAL_BIAYA, transaksi.total_biaya);
        values.put(DBHandler.COLUMN_BIAYA_AKHIR, transaksi.biaya_akhir);
        writableDatabase.replace(DBHandler.TABLE_IN_PROGRESS_TRANSAKSI, null, values);
    }

    public ArrayList<Chat> getAllChat() {
        ArrayList<Chat> list = new ArrayList<>();
        Cursor mCursor = readableDatabase.rawQuery("SELECT * FROM " + DBHandler.TABLE_CHAT, null);
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                Chat entry = new Chat();
                entry.id_tujuan = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_ID_TUJUAN));
                entry.nama_tujuan = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_NAMA_TUJUAN));
                entry.reg_id_tujuan = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_REG_ID_TUJUAN));
                entry.isi_chat = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_ISI_CHAT));
                entry.waktu = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_WAKTU));
                entry.status = mCursor.getInt(mCursor.getColumnIndex(DBHandler.COLUMN_STATUS));
                entry.chat_from = mCursor.getInt(mCursor.getColumnIndex(DBHandler.COLUMN_CHAT_FROM));
                list.add(entry);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return list;
    }

    public void insertDriver(Driver driver) {
        ContentValues values = new ContentValues();
        values.put(DBHandler.COLUMN_ID, "1");
        values.put(DBHandler.COLUMN_ID_DRIVER, driver.id);
        values.put(DBHandler.COLUMN_NAME, driver.name);
        values.put(DBHandler.COLUMN_PHONE, driver.phone);
        values.put(DBHandler.COLUMN_EMAIL, driver.email);
        values.put(DBHandler.COLUMN_PASSWORD, driver.password);
        values.put(DBHandler.COLUMN_IMAGE, driver.image);
        values.put(DBHandler.COLUMN_DEPOSIT, driver.deposit);
        values.put(DBHandler.COLUMN_RATING, driver.rating);
        values.put(DBHandler.COLUMN_REG_ID, driver.gcm_id);
        values.put(DBHandler.COLUMN_STATUS, driver.status);
        values.put(DBHandler.COLUMN_JOB, driver.job);
        values.put(DBHandler.COLUMN_NAMA_BANK, driver.nama_bank);
        values.put(DBHandler.COLUMN_ATAS_NAMA, driver.atas_nama);
        values.put(DBHandler.COLUMN_NOREK, driver.no_rek);
        values.put(DBHandler.COLUMN_LATITUDE, driver.latitude);
        values.put(DBHandler.COLUMN_LONGITUDE, driver.longitude);
        writableDatabase.insert(DBHandler.TABLE_DRIVER, null, values);
    }

    public void insertFeedback(ArrayList<Feedback> feedbacks) {
        for (Feedback instance : feedbacks) {
            ContentValues values = new ContentValues();
            values.put(DBHandler.COLUMN_ID, instance.id);
            values.put(DBHandler.COLUMN_CATATAN, instance.catatan);
            values.put(DBHandler.COLUMN_WAKTU, instance.waktu);
            writableDatabase.insert(DBHandler.TABLE_FEEDBACK, null, values);
        }
    }

    public void insertRiwayatTransaksi(ArrayList<TransactionHistory> transactionHistories) {
        for (TransactionHistory instance : transactionHistories) {
            ContentValues values = new ContentValues();
            values.put(DBHandler.COLUMN_ID_TRANSAKSI, instance.id_transaksi);
            values.put(DBHandler.COLUMN_WAKTU_RIWAYAT, instance.waktu_riwayat);
            values.put(DBHandler.COLUMN_DEBIT, instance.debit);
            values.put(DBHandler.COLUMN_KREDIT, instance.kredit);
            values.put(DBHandler.COLUMN_SALDO, instance.saldo);
            values.put(DBHandler.COLUMN_FITUR, instance.fitur);
            values.put(DBHandler.COLUMN_ID_FITUR, instance.id_fitur);
            values.put(DBHandler.COLUMN_NAMA_DEPAN, instance.nama_depan);
            values.put(DBHandler.COLUMN_NAMA_BELAKANG, instance.nama_belakang);
            values.put(DBHandler.COLUMN_KETERANGAN, instance.keterangan);
            values.put(DBHandler.COLUMN_JARAK, instance.jarak);
            values.put(DBHandler.COLUMN_TIPE_TRANSAKSI, instance.tipe_transaksi);
            writableDatabase.insert(DBHandler.TABLE_RIWAYAT_TRANSAKSI, null, values);
        }
    }

    public Driver getDriver() {
        Cursor mCursor = readableDatabase.rawQuery("SELECT * FROM " + DBHandler.TABLE_DRIVER, null);
        mCursor.moveToFirst();
        Driver entry = new Driver();
        if (mCursor.getCount() > 0) {
            entry.id = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_ID_DRIVER));
            entry.name = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_NAME));
            entry.phone = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_PHONE));
            entry.email = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_EMAIL));
            entry.password = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_PASSWORD));
            entry.image = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_IMAGE));
            entry.deposit = mCursor.getInt(mCursor.getColumnIndex(DBHandler.COLUMN_DEPOSIT));
            entry.rating = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_RATING));
            entry.gcm_id = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_REG_ID));
            entry.status = mCursor.getInt(mCursor.getColumnIndex(DBHandler.COLUMN_STATUS));
            entry.job = mCursor.getInt(mCursor.getColumnIndex(DBHandler.COLUMN_JOB));
            entry.nama_bank = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_NAMA_BANK));
            entry.atas_nama = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_ATAS_NAMA));
            entry.no_rek = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_NOREK));
            entry.latitude = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_LATITUDE));
            entry.longitude = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_LONGITUDE));
        }
        mCursor.close();
        return entry;
    }

    public ArrayList<Feedback> getAllFeedback() {
        ArrayList<Feedback> list = new ArrayList<>();
        Cursor mCursor = readableDatabase.rawQuery("SELECT * FROM " + DBHandler.TABLE_FEEDBACK, null);
        mCursor.moveToFirst();
        if (mCursor.getCount() > 0) {
            if (!mCursor.isAfterLast()) {
                do {
                    Feedback entry = new Feedback();
                    entry.id = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_ID));
                    entry.waktu = mCursor.getLong(mCursor.getColumnIndex(DBHandler.COLUMN_WAKTU));
                    entry.catatan = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_CATATAN));
                    list.add(entry);
                } while (mCursor.moveToNext());
            }
        }
        mCursor.close();
        return list;
    }

    public ArrayList<TransactionHistory> getAllRiwayatTransaksi() {
        ArrayList<TransactionHistory> list = new ArrayList<>();
        Cursor mCursor = readableDatabase.rawQuery("SELECT * FROM " + DBHandler.TABLE_RIWAYAT_TRANSAKSI + " ORDER BY " + DBHandler.COLUMN_WAKTU_RIWAYAT + " DESC", null);
        mCursor.moveToFirst();
        if (mCursor.getCount() > 0) {
            if (!mCursor.isAfterLast()) {
                do {
                    TransactionHistory entry = new TransactionHistory();
                    entry.id_transaksi = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_ID_TRANSAKSI));
                    entry.waktu_riwayat = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_WAKTU_RIWAYAT));
                    entry.nama_depan = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_NAMA_DEPAN));
                    entry.nama_belakang = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_NAMA_BELAKANG));
                    entry.debit = mCursor.getInt(mCursor.getColumnIndex(DBHandler.COLUMN_DEBIT));
                    entry.kredit = mCursor.getInt(mCursor.getColumnIndex(DBHandler.COLUMN_KREDIT));
                    entry.saldo = mCursor.getInt(mCursor.getColumnIndex(DBHandler.COLUMN_SALDO));
                    entry.jarak = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_JARAK));
                    entry.fitur = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_FITUR));
                    entry.id_fitur = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_ID_FITUR));
                    entry.keterangan = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_KETERANGAN));
                    entry.tipe_transaksi = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_TIPE_TRANSAKSI));
                    list.add(entry);
                } while (mCursor.moveToNext());
            }
        }
        mCursor.close();
        return list;
    }

    public Transaksi getInProgressTransaksi() {
        Cursor mCursor = readableDatabase.rawQuery("SELECT * FROM " + DBHandler.TABLE_IN_PROGRESS_TRANSAKSI, null);
        mCursor.moveToFirst();

        Transaksi entry = new Transaksi();
        if (mCursor.getCount() > 0) {
            entry.id_transaksi = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_ID_TRANSAKSI));
            entry.id_pelanggan = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_ID_PELANGGAN));
            entry.reg_id_pelanggan = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_REG_ID_PELANGGAN));
            entry.order_fitur = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_ORDER_FITUR));
            entry.start_latitude = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_START_LATITUDE));
            entry.start_longitude = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_START_LONGITUDE));
            entry.end_latitude = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_END_LATITUDE));
            entry.end_longitude = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_END_LONGITUDE));
            entry.jarak = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_JARAK));
            entry.harga = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_HARGA));
            entry.waktu_order = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_WAKTU_ORDER));
            entry.waktu_selesai = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_WAKTU_SELESAI));
            entry.alamat_asal = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_ALAMAT_ASAL));
            entry.alamat_tujuan = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_ALAMAT_TUJUAN));
            entry.rate = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_RATE));
            entry.kode_promo = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_KODE_PROMO));
            entry.kredit_promo = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_KREDIT_PROMO));
            entry.pakai_mpay = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_PAKAI_MPAY));
            entry.nama_pelanggan = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_NAMA_PELANGGAN));
            entry.telepon_pelanggan = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_TELEPON_PELANGGAN));
            entry.nama_barang = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_NAMA_BARANG));
            entry.nama_toko = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_NAMA_TOKO));
            entry.tanggal_pelayanan = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_TANGGAL_PELAYANAN));
            entry.nama_pengirim = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_NAMA_PENGIRIM));
            entry.nama_penerima = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_NAMA_PENERIMA));
            entry.telepon_pengirim = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_TELEPON_PENGIRIM));
            entry.telepon_penerima = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_TELEPON_PENERIMA));
            entry.jam_pelayanan = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_JAM_PELAYANAN));
            entry.lama_pelayanan = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_LAMA_PELAYANAN));
            entry.waktu_pelayanan = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_WAKTU_PELAYANAN));
            entry.kendaraan_angkut = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_KENDARAAN_ANGKUT));
            entry.quantity = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_QUANTITY));
            entry.residential_type = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_RESIDENTIAL_TYPE));
            entry.problem = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_PROBLEM));
            entry.jenis_service = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_JENIS_SERVICE));
            entry.ac_type = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_AC_TYPE));
            entry.asuransi = mCursor.getInt(mCursor.getColumnIndex(DBHandler.COLUMN_ASURANSI));
            entry.shipper = mCursor.getInt(mCursor.getColumnIndex(DBHandler.COLUMN_SHIPPER));
            entry.estimasi_biaya = mCursor.getInt(mCursor.getColumnIndex(DBHandler.COLUMN_ESTIMASI_BIAYA));
            entry.kota = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_KOTA));
            entry.massage_menu = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_MASSAGE_MENU));
            entry.pelanggan_gender = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_PELANGGAN_GENDER));
            entry.prefer_gender = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_PREFER_GENDER));
            entry.catatan_tambahan = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_CATATAN_TAMBAHAN));
            entry.nama_resto = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_NAMA_RESTO));
            entry.telepon_resto = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_TELEPON_RESTO));
            entry.total_biaya = mCursor.getInt(mCursor.getColumnIndex(DBHandler.COLUMN_TOTAL_BIAYA));
            entry.biaya_akhir = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_BIAYA_AKHIR));
        }

        mCursor.close();
        return entry;
    }

    public void insertBarangBelanja(ArrayList<ItemShopping> barang_belanjas) {
        for (ItemShopping instance : barang_belanjas) {
            ContentValues values = new ContentValues();
            values.put(DBHandler.COLUMN_ID_BARANG, instance.id_barang);
            values.put(DBHandler.COLUMN_NAMA_BARANG, instance.nama_barang);
            values.put(DBHandler.COLUMN_JUMLAH_BARANG, instance.jumlah_barang);
            values.put(DBHandler.COLUMN_HARGA_BARANG, instance.harga_barang);
            values.put(DBHandler.COLUMN_FOTO_BARANG, instance.foto_barang);
            values.put(DBHandler.COLUMN_KETERANGAN_BARANG, instance.keterangan_barang);
            values.put(DBHandler.COLUMN_IS_CHECKED, instance.isChecked);
            writableDatabase.insert(DBHandler.TABLE_BARANG_BELANJA, null, values);
        }
    }

    public void insertMakananBelanja(ArrayList<FoodShopping> foodShoppings) {
        for (FoodShopping instance : foodShoppings) {
            ContentValues values = new ContentValues();
            values.put(DBHandler.COLUMN_ID_MAKANAN, instance.id_makanan);
            values.put(DBHandler.COLUMN_NAMA_MAKANAN, instance.nama_makanan);
            values.put(DBHandler.COLUMN_JUMLAH_MAKANAN, instance.jumlah_makanan);
            values.put(DBHandler.COLUMN_HARGA_MAKANAN, instance.harga_makanan);
            values.put(DBHandler.COLUMN_CATATAN_MAKANAN, instance.catatan_makanan);
            writableDatabase.insert(DBHandler.TABLE_MAKANAN_BELANJA, null, values);
        }
    }

    public void insertDestinasiMbox(ArrayList<DestinationGoBox> destinationGoBoxes) {
        for (DestinationGoBox instance : destinationGoBoxes) {
            ContentValues values = new ContentValues();
            values.put(DBHandler.COLUMN_ID, instance.id);
            values.put(DBHandler.COLUMN_ID_TRANSAKSI, instance.id_transaksi);
            values.put(DBHandler.COLUMN_NAMA_PENERIMA, instance.nama_penerima);
            values.put(DBHandler.COLUMN_TELEPON_PENERIMA, instance.telepon_penerima);
            values.put(DBHandler.COLUMN_LOKASI, instance.lokasi);
            values.put(DBHandler.COLUMN_DETAIL_LOKASI, instance.detail_lokasi);
            values.put(DBHandler.COLUMN_LATITUDE, instance.latitude);
            values.put(DBHandler.COLUMN_LONGITUDE, instance.longitude);
            values.put(DBHandler.COLUMN_INSTRUKSI, instance.instruksi);
            long status = writableDatabase.insert(DBHandler.TABLE_DESTINASI_MBOX, null, values);
            Log.d("status_insert_mbox", status + "");
        }
    }

    public ArrayList<ItemShopping> getAllBarangBelanja() {
        ArrayList<ItemShopping> list = new ArrayList<>();
        Cursor mCursor = readableDatabase.rawQuery("SELECT * FROM " + DBHandler.TABLE_BARANG_BELANJA, null);
        mCursor.moveToFirst();
        if (mCursor.getCount() > 0) {
            if (!mCursor.isAfterLast()) {
                do {
                    ItemShopping entry = new ItemShopping();
                    entry.id_barang = mCursor.getInt(mCursor.getColumnIndex(DBHandler.COLUMN_ID_BARANG));
                    entry.nama_barang = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_NAMA_BARANG));
                    entry.jumlah_barang = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_JUMLAH_BARANG));
                    entry.harga_barang = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_HARGA_BARANG));
                    entry.foto_barang = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_FOTO_BARANG));
                    entry.keterangan_barang = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_KETERANGAN_BARANG));
                    entry.isChecked = mCursor.getInt(mCursor.getColumnIndex(DBHandler.COLUMN_IS_CHECKED));
                    list.add(entry);
                } while (mCursor.moveToNext());
            }
        }
        mCursor.close();
        return list;
    }

    public ArrayList<FoodShopping> getAllMakananBelanja() {
        ArrayList<FoodShopping> list = new ArrayList<>();
        Cursor mCursor = readableDatabase.rawQuery("SELECT * FROM " + DBHandler.TABLE_MAKANAN_BELANJA, null);
        mCursor.moveToFirst();
        if (mCursor.getCount() > 0) {
            if (!mCursor.isAfterLast()) {
                do {
                    FoodShopping entry = new FoodShopping();
                    entry.id_makanan = mCursor.getInt(mCursor.getColumnIndex(DBHandler.COLUMN_ID_MAKANAN));
                    entry.nama_makanan = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_NAMA_MAKANAN));
                    entry.jumlah_makanan = mCursor.getInt(mCursor.getColumnIndex(DBHandler.COLUMN_JUMLAH_MAKANAN));
                    entry.harga_makanan = mCursor.getInt(mCursor.getColumnIndex(DBHandler.COLUMN_HARGA_MAKANAN));
                    entry.catatan_makanan = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_CATATAN_MAKANAN));
                    list.add(entry);
                } while (mCursor.moveToNext());
            }
        }
        mCursor.close();
        return list;
    }

    public ArrayList<DestinationGoBox> getAllDestinasiMbox() {
        ArrayList<DestinationGoBox> list = new ArrayList<>();
        Cursor mCursor = readableDatabase.rawQuery("SELECT * FROM " + DBHandler.TABLE_DESTINASI_MBOX, null);
        mCursor.moveToFirst();
        if (mCursor.getCount() > 0) {
            if (!mCursor.isAfterLast()) {
                do {
                    DestinationGoBox entry = new DestinationGoBox();
                    entry.id = mCursor.getInt(mCursor.getColumnIndex(DBHandler.COLUMN_ID));
                    entry.id_transaksi = mCursor.getInt(mCursor.getColumnIndex(DBHandler.COLUMN_ID_TRANSAKSI));
                    entry.nama_penerima = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_NAMA_PENERIMA));
                    entry.telepon_penerima = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_TELEPON_PENERIMA));
                    entry.latitude = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_LATITUDE));
                    entry.longitude = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_LONGITUDE));
                    entry.lokasi = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_LOKASI));
                    entry.detail_lokasi = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_DETAIL_LOKASI));
                    entry.instruksi = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_INSTRUKSI));
                    list.add(entry);
                } while (mCursor.moveToNext());
            }
        }
        mCursor.close();
        return list;
    }

    public void checkedBarang(int pos, int status) {
        ContentValues values = new ContentValues();
        String[] args = {Integer.toString(pos)};
        values.put(DBHandler.COLUMN_IS_CHECKED, status);
        int hasil = writableDatabase.update(DBHandler.TABLE_BARANG_BELANJA, values, DBHandler.COLUMN_ID_BARANG + " = ?", args);
        Log.d("isChecked", Integer.toString(hasil));
    }

    public void updateStatus(int status) {
        ContentValues values = new ContentValues();
        String[] args = {Integer.toString(1)};
        values.put(DBHandler.COLUMN_STATUS, status);
        int hasil = writableDatabase.update(DBHandler.TABLE_DRIVER, values, DBHandler.COLUMN_ID + " = ?", args);
        Log.d("updating_status", Integer.toString(hasil));
        Log.d("updating_status_to", Integer.toString(status));
    }

    public void updateEmail(String email) {
        ContentValues values = new ContentValues();
        String[] args = {Integer.toString(1)};
        values.put(DBHandler.COLUMN_EMAIL, email);
        int hasil = writableDatabase.update(DBHandler.TABLE_DRIVER, values, DBHandler.COLUMN_ID + " = ?", args);
        Log.d("updating_email", Integer.toString(hasil));
    }

    public void updateTelepon(String nomor) {
        ContentValues values = new ContentValues();
        String[] args = {Integer.toString(1)};
        values.put(DBHandler.COLUMN_PHONE, nomor);
        int hasil = writableDatabase.update(DBHandler.TABLE_DRIVER, values, DBHandler.COLUMN_ID + " = ?", args);
        Log.d("updating_nomor", Integer.toString(hasil));
    }

    public void updatePassword(String password) {
        ContentValues values = new ContentValues();
        String[] args = {Integer.toString(1)};
        values.put(DBHandler.COLUMN_PASSWORD, password);
        int hasil = writableDatabase.update(DBHandler.TABLE_DRIVER, values, DBHandler.COLUMN_ID + " = ?", args);
        Log.d("updating_password", Integer.toString(hasil));
    }

    public void updateRekening(String[] rekening) {
        ContentValues values = new ContentValues();
        String[] args = {Integer.toString(1)};
        values.put(DBHandler.COLUMN_NAMA_BANK, rekening[0]);
        values.put(DBHandler.COLUMN_NOREK, rekening[1]);
        values.put(DBHandler.COLUMN_ATAS_NAMA, rekening[2]);
        int hasil = writableDatabase.update(DBHandler.TABLE_DRIVER, values, DBHandler.COLUMN_ID + " = ?", args);
        Log.d("updating_password", Integer.toString(hasil));
    }

    public void updateDeposit(int deposit) {
        ContentValues values = new ContentValues();
        String[] args = {Integer.toString(1)};
        values.put(DBHandler.COLUMN_DEPOSIT, deposit);
        int hasil = writableDatabase.update(DBHandler.TABLE_DRIVER, values, DBHandler.COLUMN_ID + " = ?", args);
        Log.d("updating_deposit", Integer.toString(hasil));
    }

    public void updateRating(String rating) {
        ContentValues values = new ContentValues();
        String[] args = {Integer.toString(1)};
        values.put(DBHandler.COLUMN_RATING, rating);
        int hasil = writableDatabase.update(DBHandler.TABLE_DRIVER, values, DBHandler.COLUMN_ID + " = ?", args);
        Log.d("updating_rating", Integer.toString(hasil));
    }

    public void updateLokasi(String[] lokasis) {
        ContentValues values = new ContentValues();
        String[] args = {Integer.toString(1)};
        values.put(DBHandler.COLUMN_LATITUDE, lokasis[0]);
        values.put(DBHandler.COLUMN_LONGITUDE, lokasis[1]);
        int hasil = writableDatabase.update(DBHandler.TABLE_DRIVER, values, DBHandler.COLUMN_ID + " = ?", args);
        Log.d("updating_lokasi", Integer.toString(hasil));
    }

    //    public void updateStatusChat(int status) {
//        ContentValues values = new ContentValues();
//        String[] args = { Integer.toString(msg.idBooking) };
//        values.put(DBHandler.COLUMN_STATUS, status);
//        int hasil = writableDatabase.update(DBHandler.TABLE_CHAT, values, DBHandler.COLUMN_ID+ " = ?", args);
//    }


}

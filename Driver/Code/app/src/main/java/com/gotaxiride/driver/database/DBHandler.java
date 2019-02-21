package com.gotaxiride.driver.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by GagahIB on 27/11/2016.
 */
public class DBHandler extends SQLiteOpenHelper {

    public static String TABLE_CHAT = "CHAT";
    public static String TABLE_FEEDBACK = "FEEDBACK";
    public static String TABLE_RIWAYAT_TRANSAKSI = "RIWAYAT_TRANSAKSI";
    public static String TABLE_IN_PROGRESS_TRANSAKSI = "IN_PROGRESS_TRANSAKSI";
    public static String TABLE_BARANG_BELANJA = "BARANG_BELANJA";
    public static String TABLE_DESTINASI_MBOX = "DESTINASI_MBOX";
    public static String TABLE_DRIVER = "DRIVER";
    public static String TABLE_MAKANAN_BELANJA = "MAKANAN_BELANJA";
    public static String COLUMN_ID_TUJUAN = "ID_TUJUAN";
    public static String COLUMN_NAMA_TUJUAN = "NAMA_TUJUAN";
    public static String COLUMN_REG_ID_TUJUAN = "REG_ID_TUJUAN";
    public static String COLUMN_ISI_CHAT = "ISI_CHAT";
    public static String COLUMN_WAKTU = "WAKTU";
    public static String COLUMN_STATUS = "STATUS";
    public static String COLUMN_CHAT_FROM = "CHAT_FROM";
    public static String COLUMN_ID = "ID";
    public static String COLUMN_CATATAN = "CATATAN";
    public static String COLUMN_ID_TRANSAKSI = "ID_TRANSAKSI";
    public static String COLUMN_WAKTU_RIWAYAT = "WAKTU_RIWAYAT";
    public static String COLUMN_NAMA_DEPAN = "NAMA_DEPAN";
    public static String COLUMN_NAMA_BELAKANG = "NAMA_BELAKANG";
    public static String COLUMN_JARAK = "JARAK";
    public static String COLUMN_DEBIT = "DEBIT";
    public static String COLUMN_KREDIT = "KREDIT";
    public static String COLUMN_SALDO = "SALDO";
    public static String COLUMN_TIPE_TRANSAKSI = "TIPE_TRANSAKSI";
    public static String COLUMN_ID_FITUR = "ID_FITUR";
    public static String COLUMN_FITUR = "FITUR";
    public static String COLUMN_KETERANGAN = "KETERANGAN";
    public static String COLUMN_ID_PELANGGAN = "ID_PELANGGAN";
    public static String COLUMN_REG_ID_PELANGGAN = "REG_ID_PELANGGAN";
    public static String COLUMN_ORDER_FITUR = "ORDER_FITUR";
    public static String COLUMN_START_LATITUDE = "START_LATITUDE";
    public static String COLUMN_START_LONGITUDE = "START_LONGITUDE";
    public static String COLUMN_END_LATITUDE = "END_LATITUDE";
    public static String COLUMN_END_LONGITUDE = "END_LONGITUDE";
    public static String COLUMN_HARGA = "HARGA";
    public static String COLUMN_WAKTU_ORDER = "WAKTU_ORDER";
    public static String COLUMN_WAKTU_SELESAI = "WAKTU_SELESAI";
    public static String COLUMN_ALAMAT_ASAL = "ALAMAT_ASAL";
    public static String COLUMN_ALAMAT_TUJUAN = "ALAMAT_TUJUAN";
    public static String COLUMN_RATE = "RATE";
    public static String COLUMN_KODE_PROMO = "KODE_PROMO";
    public static String COLUMN_PAKAI_MPAY = "PAKAI_MPAY";
    public static String COLUMN_KREDIT_PROMO = "KREDIT_PROMO";
    public static String COLUMN_BIAYA_AKHIR = "BIAYA_AKHIR";
    public static String COLUMN_NAMA_PELANGGAN = "NAMA_PELANGGAN";
    public static String COLUMN_TELEPON_PELANGGAN = "TELEPON_PELANGGAN";
    public static String COLUMN_NAMA_BARANG = "NAMA_BARANG";
    public static String COLUMN_TANGGAL_PELAYANAN = "TANGGAL_PELAYANAN";
    public static String COLUMN_JAM_PELAYANAN = "JAM_PELAYANAN";
    public static String COLUMN_LAMA_PELAYANAN = "LAMA_PELAYANAN";
    public static String COLUMN_KENDARAAN_ANGKUT = "KENDARAAN_ANGKUT";
    public static String COLUMN_WAKTU_PELAYANAN = "WAKTU_PELAYANAN";
    public static String COLUMN_ID_BARANG = "ID_BARANG";
    public static String COLUMN_ESTIMASI_BIAYA = "ESTIMASI_BIAYA";
    public static String COLUMN_JUMLAH_BARANG = "JUMLAH_BARANG";
    public static String COLUMN_HARGA_BARANG = "HARGA_BARANG";
    public static String COLUMN_FOTO_BARANG = "FOTO_BARANG";
    public static String COLUMN_KETERANGAN_BARANG = "KETERANGAN_BARANG";
    public static String COLUMN_IS_CHECKED = "IS_CHECKED";
    public static String COLUMN_NAMA_TOKO = "NAMA_TOKO";
    public static String COLUMN_NAMA_PENERIMA = "NAMA_PENERIMA";
    public static String COLUMN_NAMA_PENGIRIM = "NAMA_PENGIRIM";
    public static String COLUMN_TELEPON_PENERIMA = "TELEPON_PENERIMA";
    public static String COLUMN_TELEPON_PENGIRIM = "TELEPON_PENGIRIM";
    public static String COLUMN_LOKASI = "LOKASI";
    public static String COLUMN_LATITUDE = "LATITUDE";
    public static String COLUMN_LONGITUDE = "LONGITUDE";
    public static String COLUMN_DETAIL_LOKASI = "DETAIL_LOKASI";
    public static String COLUMN_INSTRUKSI = "INSTRUKSI";
    public static String COLUMN_QUANTITY = "QUANTITY";
    public static String COLUMN_RESIDENTIAL_TYPE = "RESIDENTIAL_TYPE";
    public static String COLUMN_PROBLEM = "PROBLEM";
    public static String COLUMN_JENIS_SERVICE = "JENIS_SERVICE";
    public static String COLUMN_AC_TYPE = "AC_TYPE";
    public static String COLUMN_ASURANSI = "ASURANSI";
    public static String COLUMN_SHIPPER = "SHIPPER";
    public static String COLUMN_ID_DRIVER = "ID_DRIVER";
    public static String COLUMN_NAME = "NAME";
    public static String COLUMN_PHONE = "PHONE";
    public static String COLUMN_EMAIL = "EMAIL";
    public static String COLUMN_PASSWORD = "PASSWORD";
    public static String COLUMN_TYPE = "TYPE";
    public static String COLUMN_IMAGE = "AMGE";
    public static String COLUMN_DEPOSIT = "DEPOSIT";
    public static String COLUMN_RATING = "RATING";
    public static String COLUMN_REG_ID = "REG_ID";
    public static String COLUMN_JOB = "JOB";
    public static String COLUMN_NAMA_BANK = "NAMA_BANK";
    public static String COLUMN_ATAS_NAMA = "ATAS_NAMA";
    public static String COLUMN_NOREK = "NOREK";
    public static String COLUMN_KOTA = "KOTA";
    public static String COLUMN_MASSAGE_MENU = "MASSAGE_MENU";
    public static String COLUMN_PELANGGAN_GENDER = "PELANGGAN_GENDER";
    public static String COLUMN_PREFER_GENDER = "PREFER_GENDER";
    public static String COLUMN_CATATAN_TAMBAHAN = "CATATAN_TAMBAHAN";
    public static String COLUMN_NAMA_RESTO = "NAMA_RESTO";
    public static String COLUMN_TOTAL_BIAYA = "TOTAL_BIAYA";
    public static String COLUMN_TELEPON_RESTO = "TELEPON_RESTO";
    public static String COLUMN_ID_MAKANAN = "ID_MAKANAN";
    public static String COLUMN_NAMA_MAKANAN = "NAMA_MAKANAN";
    public static String COLUMN_HARGA_MAKANAN = "HARGA_MAKANAN";
    public static String COLUMN_JUMLAH_MAKANAN = "JUMLAH_MAKANAN";
    public static String COLUMN_CATATAN_MAKANAN = "CATATAN_MAKANAN";
    private static String DATABASE_NAME = "DRIVERMJEK";
    private static int SCHEMA_VERSION = 18;

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CHAT + " ("
                + COLUMN_ID_TUJUAN + " VARCHAR NOT NULL, "
                + COLUMN_NAMA_TUJUAN + " VARCHAR NOT NULL, "
                + COLUMN_REG_ID_TUJUAN + " VARCHAR NOT NULL, "
                + COLUMN_ISI_CHAT + " VARCHAR NOT NULL, "
                + COLUMN_WAKTU + " VARCHAR NOT NULL, "
                + COLUMN_CHAT_FROM + " INTEGER NOT NULL, "
                + COLUMN_STATUS + " INTEGER NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_FEEDBACK + " ("
                + COLUMN_ID + " VARCHAR NOT NULL, "
                + COLUMN_CATATAN + " VARCHAR NOT NULL, "
                + COLUMN_WAKTU + " BIGINT NOT NULL)");


        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_RIWAYAT_TRANSAKSI + " ("
                + COLUMN_ID_TRANSAKSI + " VARCHAR NOT NULL, "
                + COLUMN_WAKTU_RIWAYAT + " VARCHAR NOT NULL, "
                + COLUMN_NAMA_DEPAN + " VARCHAR NOT NULL, "
                + COLUMN_NAMA_BELAKANG + " VARCHAR NOT NULL, "
                + COLUMN_JARAK + " VARCHAR NOT NULL, "
                + COLUMN_DEBIT + " INTEGER NOT NULL, "
                + COLUMN_KREDIT + " INTEGER NOT NULL, "
                + COLUMN_SALDO + " INTEGER NOT NULL, "
                + COLUMN_TIPE_TRANSAKSI + " VARCHAR NOT NULL, "
                + COLUMN_ID_FITUR + " VARCHAR NOT NULL, "
                + COLUMN_FITUR + " VARCHAR NOT NULL, "
                + COLUMN_KETERANGAN + " VARCHAR NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_IN_PROGRESS_TRANSAKSI + " ("
                + COLUMN_ID_TRANSAKSI + " VARCHAR NOT NULL, "
                + COLUMN_ID_PELANGGAN + " VARCHAR NOT NULL, "
                + COLUMN_REG_ID_PELANGGAN + " VARCHAR NOT NULL, "
                + COLUMN_ORDER_FITUR + " VARCHAR NOT NULL, "
                + COLUMN_START_LATITUDE + " VARCHAR, "
                + COLUMN_START_LONGITUDE + " VARCHAR, "
                + COLUMN_END_LATITUDE + " VARCHAR, "
                + COLUMN_END_LONGITUDE + " VARCHAR, "
                + COLUMN_JARAK + " VARCHAR, "
                + COLUMN_HARGA + " VARCHAR NOT NULL, "
                + COLUMN_WAKTU_ORDER + " VARCHAR, "
                + COLUMN_WAKTU_SELESAI + " VARCHAR, "
                + COLUMN_ALAMAT_ASAL + " VARCHAR, "
                + COLUMN_ALAMAT_TUJUAN + " VARCHAR, "
                + COLUMN_RATE + " VARCHAR, "
                + COLUMN_KODE_PROMO + " VARCHAR, "
                + COLUMN_PAKAI_MPAY + " VARCHAR, "
                + COLUMN_KREDIT_PROMO + " VARCHAR, "
                + COLUMN_BIAYA_AKHIR + " VARCHAR, "
                + COLUMN_NAMA_PELANGGAN + " VARCHAR, "
                + COLUMN_TELEPON_PELANGGAN + " VARCHAR, "
                + COLUMN_NAMA_BARANG + " VARCHAR, "
                + COLUMN_NAMA_TOKO + " VARCHAR, "
                + COLUMN_TANGGAL_PELAYANAN + " VARCHAR, "
                + COLUMN_NAMA_PENGIRIM + " VARCHAR, "
                + COLUMN_NAMA_PENERIMA + " VARCHAR, "
                + COLUMN_TELEPON_PENGIRIM + " VARCHAR, "
                + COLUMN_TELEPON_PENERIMA + " VARCHAR, "
                + COLUMN_JAM_PELAYANAN + " VARCHAR, "
                + COLUMN_KENDARAAN_ANGKUT + " VARCHAR, "
                + COLUMN_WAKTU_PELAYANAN + " VARCHAR, "
                + COLUMN_QUANTITY + " VARCHAR, "
                + COLUMN_RESIDENTIAL_TYPE + " VARCHAR, "
                + COLUMN_PROBLEM + " VARCHAR, "
                + COLUMN_JENIS_SERVICE + " VARCHAR, "
                + COLUMN_AC_TYPE + " VARCHAR, "
                + COLUMN_ASURANSI + " VARCHAR, "
                + COLUMN_SHIPPER + " VARCHAR, "
                + COLUMN_ESTIMASI_BIAYA + " INTEGER, "
                + COLUMN_KOTA + " VARCHAR, "
                + COLUMN_MASSAGE_MENU + " VARCHAR, "
                + COLUMN_PELANGGAN_GENDER + " VARCHAR, "
                + COLUMN_PREFER_GENDER + " VARCHAR, "
                + COLUMN_CATATAN_TAMBAHAN + " VARCHAR, "
                + COLUMN_NAMA_RESTO + " VARCHAR, "
                + COLUMN_TELEPON_RESTO + " VARCHAR, "
                + COLUMN_TOTAL_BIAYA + " INTEGER, "
                + COLUMN_LAMA_PELAYANAN + " VARCHAR)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_BARANG_BELANJA + " ("
                + COLUMN_ID_BARANG + " VARCHAR, "
                + COLUMN_NAMA_BARANG + " VARCHAR NOT NULL, "
                + COLUMN_JUMLAH_BARANG + " VARCHAR NOT NULL, "
                + COLUMN_HARGA_BARANG + " VARCHAR, "
                + COLUMN_FOTO_BARANG + " VARCHAR, "
                + COLUMN_IS_CHECKED + " INTEGER, "
                + COLUMN_KETERANGAN_BARANG + " VARCHAR)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_MAKANAN_BELANJA + " ("
                + COLUMN_ID_MAKANAN + " INTEGER, "
                + COLUMN_NAMA_MAKANAN + " VARCHAR NOT NULL, "
                + COLUMN_JUMLAH_MAKANAN + " VARCHAR NOT NULL, "
                + COLUMN_HARGA_MAKANAN + " INTEGER, "
                + COLUMN_CATATAN_MAKANAN + " VARCHAR)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_DESTINASI_MBOX + " ("
                + COLUMN_ID + " INTEGER, "
                + COLUMN_ID_TRANSAKSI + " INTEGER, "
                + COLUMN_NAMA_PENERIMA + " VARCHAR, "
                + COLUMN_TELEPON_PENERIMA + " VARCHAR, "
                + COLUMN_LATITUDE + " VARCHAR, "
                + COLUMN_LONGITUDE + " VARCHAR, "
                + COLUMN_LOKASI + " VARCHAR, "
                + COLUMN_DETAIL_LOKASI + " VARCHAR, "
                + COLUMN_INSTRUKSI + " VARCHAR)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_DRIVER + " ("
                + COLUMN_ID + " INTEGER NOT NULL, "
                + COLUMN_ID_DRIVER + " VARCHAR NOT NULL, "
                + COLUMN_NAME + " VARCHAR, "
                + COLUMN_PHONE + " VARCHAR, "
                + COLUMN_EMAIL + " VARCHAR, "
                + COLUMN_PASSWORD + " VARCHAR, "
                + COLUMN_IMAGE + " VARCHAR, "
                + COLUMN_DEPOSIT + " INTEGER, "
                + COLUMN_RATING + " DOUBLE, "
                + COLUMN_REG_ID + " VARCHAR, "
                + COLUMN_STATUS + " VARCHAR, "
                + COLUMN_JOB + " VARCHAR, "
                + COLUMN_NAMA_BANK + " VARCHAR, "
                + COLUMN_ATAS_NAMA + " VARCHAR, "
                + COLUMN_NOREK + " VARCHAR, "
                + COLUMN_LATITUDE + " VARCHAR, "
                + COLUMN_LONGITUDE + " VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEEDBACK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RIWAYAT_TRANSAKSI);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IN_PROGRESS_TRANSAKSI);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BARANG_BELANJA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DESTINASI_MBOX);

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CHAT + " ("
                + COLUMN_ID_TUJUAN + " VARCHAR NOT NULL, "
                + COLUMN_NAMA_TUJUAN + " VARCHAR NOT NULL, "
                + COLUMN_REG_ID_TUJUAN + " VARCHAR NOT NULL, "
                + COLUMN_ISI_CHAT + " VARCHAR NOT NULL, "
                + COLUMN_WAKTU + " VARCHAR NOT NULL, "
                + COLUMN_CHAT_FROM + " INTEGER NOT NULL, "
                + COLUMN_STATUS + " INTEGER NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_FEEDBACK + " ("
                + COLUMN_ID + " VARCHAR NOT NULL, "
                + COLUMN_CATATAN + " VARCHAR NOT NULL, "
                + COLUMN_WAKTU + " BIGINT NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_RIWAYAT_TRANSAKSI + " ("
                + COLUMN_ID_TRANSAKSI + " VARCHAR NOT NULL, "
                + COLUMN_WAKTU_RIWAYAT + " VARCHAR NOT NULL, "
                + COLUMN_NAMA_DEPAN + " VARCHAR NOT NULL, "
                + COLUMN_NAMA_BELAKANG + " VARCHAR NOT NULL, "
                + COLUMN_JARAK + " VARCHAR NOT NULL, "
                + COLUMN_DEBIT + " INTEGER NOT NULL, "
                + COLUMN_KREDIT + " INTEGER NOT NULL, "
                + COLUMN_SALDO + " INTEGER NOT NULL, "
                + COLUMN_TIPE_TRANSAKSI + " VARCHAR NOT NULL, "
                + COLUMN_ID_FITUR + " VARCHAR NOT NULL, "
                + COLUMN_FITUR + " VARCHAR NOT NULL, "
                + COLUMN_KETERANGAN + " VARCHAR NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_IN_PROGRESS_TRANSAKSI + " ("
                + COLUMN_ID_TRANSAKSI + " VARCHAR NOT NULL, "
                + COLUMN_ID_PELANGGAN + " VARCHAR NOT NULL, "
                + COLUMN_REG_ID_PELANGGAN + " VARCHAR NOT NULL, "
                + COLUMN_ORDER_FITUR + " VARCHAR NOT NULL, "
                + COLUMN_START_LATITUDE + " VARCHAR, "
                + COLUMN_START_LONGITUDE + " VARCHAR, "
                + COLUMN_END_LATITUDE + " VARCHAR, "
                + COLUMN_END_LONGITUDE + " VARCHAR, "
                + COLUMN_JARAK + " VARCHAR, "
                + COLUMN_HARGA + " VARCHAR NOT NULL, "
                + COLUMN_WAKTU_ORDER + " VARCHAR, "
                + COLUMN_WAKTU_SELESAI + " VARCHAR, "
                + COLUMN_ALAMAT_ASAL + " VARCHAR, "
                + COLUMN_ALAMAT_TUJUAN + " VARCHAR, "
                + COLUMN_RATE + " VARCHAR, "
                + COLUMN_KODE_PROMO + " VARCHAR, "
                + COLUMN_KREDIT_PROMO + " VARCHAR, "
                + COLUMN_BIAYA_AKHIR + " VARCHAR, "
                + COLUMN_PAKAI_MPAY + " VARCHAR, "
                + COLUMN_NAMA_PELANGGAN + " VARCHAR, "
                + COLUMN_TELEPON_PELANGGAN + " VARCHAR, "
                + COLUMN_NAMA_BARANG + " VARCHAR, "
                + COLUMN_NAMA_TOKO + " VARCHAR, "
                + COLUMN_TANGGAL_PELAYANAN + " VARCHAR, "
                + COLUMN_NAMA_PENGIRIM + " VARCHAR, "
                + COLUMN_NAMA_PENERIMA + " VARCHAR, "
                + COLUMN_TELEPON_PENGIRIM + " VARCHAR, "
                + COLUMN_TELEPON_PENERIMA + " VARCHAR, "
                + COLUMN_JAM_PELAYANAN + " VARCHAR, "
                + COLUMN_KENDARAAN_ANGKUT + " VARCHAR, "
                + COLUMN_WAKTU_PELAYANAN + " VARCHAR, "
                + COLUMN_QUANTITY + " VARCHAR, "
                + COLUMN_RESIDENTIAL_TYPE + " VARCHAR, "
                + COLUMN_PROBLEM + " VARCHAR, "
                + COLUMN_JENIS_SERVICE + " VARCHAR, "
                + COLUMN_ASURANSI + " VARCHAR, "
                + COLUMN_SHIPPER + " VARCHAR, "
                + COLUMN_AC_TYPE + " VARCHAR, "
                + COLUMN_ESTIMASI_BIAYA + " INTEGER, "
                + COLUMN_KOTA + " VARCHAR, "
                + COLUMN_MASSAGE_MENU + " VARCHAR, "
                + COLUMN_PELANGGAN_GENDER + " VARCHAR, "
                + COLUMN_PREFER_GENDER + " VARCHAR, "
                + COLUMN_CATATAN_TAMBAHAN + " VARCHAR, "
                + COLUMN_NAMA_RESTO + " VARCHAR, "
                + COLUMN_TELEPON_RESTO + " VARCHAR, "
                + COLUMN_TOTAL_BIAYA + " INTEGER, "
                + COLUMN_LAMA_PELAYANAN + " VARCHAR)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_BARANG_BELANJA + " ("
                + COLUMN_ID_BARANG + " VARCHAR, "
                + COLUMN_NAMA_BARANG + " VARCHAR NOT NULL, "
                + COLUMN_JUMLAH_BARANG + " VARCHAR NOT NULL, "
                + COLUMN_HARGA_BARANG + " VARCHAR, "
                + COLUMN_FOTO_BARANG + " VARCHAR, "
                + COLUMN_IS_CHECKED + " INTEGER, "
                + COLUMN_KETERANGAN_BARANG + " VARCHAR)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_MAKANAN_BELANJA + " ("
                + COLUMN_ID_MAKANAN + " INTEGER, "
                + COLUMN_NAMA_MAKANAN + " VARCHAR NOT NULL, "
                + COLUMN_JUMLAH_MAKANAN + " VARCHAR NOT NULL, "
                + COLUMN_HARGA_MAKANAN + " INTEGER, "
                + COLUMN_CATATAN_MAKANAN + " VARCHAR)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_DESTINASI_MBOX + " ("
                + COLUMN_ID + " INTEGER, "
                + COLUMN_ID_TRANSAKSI + " INTEGER, "
                + COLUMN_NAMA_PENERIMA + " VARCHAR, "
                + COLUMN_TELEPON_PENERIMA + " VARCHAR, "
                + COLUMN_LATITUDE + " VARCHAR, "
                + COLUMN_LONGITUDE + " VARCHAR, "
                + COLUMN_LOKASI + " VARCHAR, "
                + COLUMN_DETAIL_LOKASI + " VARCHAR, "
                + COLUMN_INSTRUKSI + " VARCHAR)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_DRIVER + " ("
                + COLUMN_ID + " INTEGER NOT NULL, "
                + COLUMN_ID_DRIVER + " VARCHAR NOT NULL, "
                + COLUMN_NAME + " VARCHAR, "
                + COLUMN_PHONE + " VARCHAR, "
                + COLUMN_EMAIL + " VARCHAR, "
                + COLUMN_PASSWORD + " VARCHAR, "
                + COLUMN_IMAGE + " VARCHAR, "
                + COLUMN_DEPOSIT + " INTEGER, "
                + COLUMN_RATING + " DOUBLE, "
                + COLUMN_REG_ID + " VARCHAR, "
                + COLUMN_STATUS + " VARCHAR, "
                + COLUMN_JOB + " VARCHAR, "
                + COLUMN_NAMA_BANK + " VARCHAR, "
                + COLUMN_ATAS_NAMA + " VARCHAR, "
                + COLUMN_NOREK + " VARCHAR, "
                + COLUMN_LATITUDE + " VARCHAR, "
                + COLUMN_LONGITUDE + " VARCHAR)");
    }
}

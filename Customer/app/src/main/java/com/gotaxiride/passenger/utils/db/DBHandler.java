package com.gotaxiride.passenger.utils.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by GagahIB on 6/6/2018.
 */
public class DBHandler extends SQLiteOpenHelper {

    public static String TABLE_CHAT = "CHAT";
    public static String COLUMN_ID_TUJUAN = "ID_TUJUAN";
    public static String COLUMN_NAMA_TUJUAN = "NAMA_TUJUAN";
    public static String COLUMN_REG_ID_TUJUAN = "REG_ID_TUJUAN";
    public static String COLUMN_ISI_CHAT = "ISI_CHAT";
    public static String COLUMN_WAKTU = "WAKTU";
    public static String COLUMN_STATUS = "STATUS";
    public static String COLUMN_CHAT_FROM = "CHAT_FROM";
    public static String COLUMN_REG_ID_FROM = "REG_ID_FROM";
    private static String DATABASE_NAME = "DRIVERMJEK";
    private static int SCHEMA_VERSION = 6;


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
                + COLUMN_STATUS + " INTEGER NOT NULL, "
                + COLUMN_REG_ID_FROM + " VARCHAR )");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT);

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CHAT + " ("
                + COLUMN_ID_TUJUAN + " VARCHAR NOT NULL, "
                + COLUMN_NAMA_TUJUAN + " VARCHAR NOT NULL, "
                + COLUMN_REG_ID_TUJUAN + " VARCHAR NOT NULL, "
                + COLUMN_ISI_CHAT + " VARCHAR NOT NULL, "
                + COLUMN_WAKTU + " VARCHAR NOT NULL, "
                + COLUMN_CHAT_FROM + " INTEGER NOT NULL, "
                + COLUMN_STATUS + " INTEGER NOT NULL, "
                + COLUMN_REG_ID_FROM + " VARCHAR )");

    }
}

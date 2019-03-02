package com.gotaxiride.passenger.utils.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gotaxiride.passenger.model.Chat;

import java.util.ArrayList;

/**
 * Created by GagahIB on 2/6/2018.
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
        values.put(DBHandler.COLUMN_REG_ID_FROM, chat.reg_id_from);
        writableDatabase.replace(DBHandler.TABLE_CHAT, null, values);
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
                entry.reg_id_from = mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_REG_ID_FROM));
                list.add(entry);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return list;
    }
}

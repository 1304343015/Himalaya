package com.example.himalaya.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.himalaya.utils.Constants;

public class SubscriptHelper extends SQLiteOpenHelper {
    public SubscriptHelper(@Nullable Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="create table "+Constants.SUB_TABLE_NAME+"("+
                Constants.SUB_ID+" integer primary key autoincrement,"+
                Constants.SUB_COVER_URL+" text,"+
                Constants.SUB_TITLE+" varchar,"+
                Constants.SUB_DESCRIPTION+" text,"+
                Constants.SUB_TRACKS_COUNT+" integer,"+
                Constants.SUB_PLAY_COUNT+" integer,"+
                Constants.SUB_AUTHOR_NAME+" varchar,"+
                Constants.SUB_ALBUM_ID+" integer)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

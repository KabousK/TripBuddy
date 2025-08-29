package com.example.formative_eduv4834254.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "tripbuddy.db";
    public static final int DB_VERSION = 1;

    public static final String T_MEMORIES = "memories";
    public static final String C_ID = "id";
    public static final String C_CAPTION = "caption";
    public static final String C_PHOTO = "photo_uri";
    public static final String C_AUDIO = "audio_uri";
    public static final String C_CREATED = "created_at";
    public static final String C_LOCATION = "location";
    public static final String C_MOOD = "mood";

    public DbHelper(Context c) { super(c, DB_NAME, null, DB_VERSION); }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + T_MEMORIES + " (" +
                C_ID + " INTEGER PRIMARY KEY, " +
                C_CAPTION + " TEXT, " +
                C_PHOTO + " TEXT NOT NULL, " +
                C_AUDIO + " TEXT, " +
                C_CREATED + " INTEGER, " +
                C_LOCATION + " TEXT, " +
                C_MOOD + " TEXT" +
                ")");
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        // simple path for prototype: drop and recreate
        db.execSQL("DROP TABLE IF EXISTS " + T_MEMORIES);
        onCreate(db);
    }
}

package com.example.formative_eduv4834254.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class MemoryDao {
    private final DbHelper helper;
    public MemoryDao(Context c) { helper = new DbHelper(c); }

    public long insert(Memory m) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.C_ID, m.id);
        cv.put(DbHelper.C_CAPTION, m.caption);
        cv.put(DbHelper.C_PHOTO, m.photoUri);
        cv.put(DbHelper.C_AUDIO, m.audioUri);
        cv.put(DbHelper.C_CREATED, m.createdAt);
        cv.put(DbHelper.C_LOCATION, m.location);
        cv.put(DbHelper.C_MOOD, m.mood);
        return db.insertWithOnConflict(DbHelper.T_MEMORIES, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void updateBasic(long id, String caption, String mood) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.C_CAPTION, caption);
        cv.put(DbHelper.C_MOOD, mood);
        db.update(DbHelper.T_MEMORIES, cv, DbHelper.C_ID + "=?", new String[]{ String.valueOf(id)});
    }

    public List<Memory> getAll() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DbHelper.T_MEMORIES, null, null, null, null, null, DbHelper.C_CREATED + " DESC");
        List<Memory> list = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                Memory m = new Memory();
                m.id = c.getLong(c.getColumnIndexOrThrow(DbHelper.C_ID));
                m.caption = c.getString(c.getColumnIndexOrThrow(DbHelper.C_CAPTION));
                m.photoUri = c.getString(c.getColumnIndexOrThrow(DbHelper.C_PHOTO));
                m.audioUri = c.getString(c.getColumnIndexOrThrow(DbHelper.C_AUDIO));
                m.createdAt = c.getLong(c.getColumnIndexOrThrow(DbHelper.C_CREATED));
                m.location = c.getString(c.getColumnIndexOrThrow(DbHelper.C_LOCATION));
                m.mood = c.getString(c.getColumnIndexOrThrow(DbHelper.C_MOOD));
                list.add(m);
            }
        } finally { c.close(); }
        return list;
    }
}

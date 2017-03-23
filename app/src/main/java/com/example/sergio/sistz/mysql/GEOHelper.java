package com.example.sergio.sistz.mysql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sergio on 2/22/2016.
 */
public class GEOHelper extends SQLiteOpenHelper{

    public static final String TABLE_ID = "_idnote";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String TABLE = "notes";

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "sisdb/sisdb.sqlite";


    public GEOHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void close() {
        this.close();
    }

    public void addNote(String title, String content ) {
        ContentValues valores = new ContentValues();
        valores.put(TITLE,title);
        valores.put(CONTENT, content);
        //this.getWritableDatabase().insert(TABLE, null, valores);
    }

    //public Cursor getNote(){
    public Cursor getNote() {
        String columnas[]={TABLE_ID, TITLE,CONTENT};
        Cursor c = this.getReadableDatabase().query(TABLE, columnas, null,null,null, null,null);
        return c;
    }

}

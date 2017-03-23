package com.example.sergio.sistz.mysql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;

/**
 * Created by jlgarcia on 20/03/2017.
 */

public class DBlogfunctions extends SQLiteOpenHelper{

    String sqlCreate_table_logFunction = "CREATE TABLE IF NOT EXISTS\"logfunctions\" (\"id\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , \"startlogl\"  TEXT, \"locationlog\" TEXT, \"finishlog\" TEXT, \"flag\" INTEGER DEFAULT 1)";

    public DBlogfunctions(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (db.isReadOnly()){
            db = getWritableDatabase();
        }
        db.execSQL(sqlCreate_table_logFunction);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

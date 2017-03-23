package com.example.sergio.sistz.mysql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.example.sergio.sistz.data.GEO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergio on 2/22/2016.
 */
public class dbGEO {
    private static final String STATICS_ROOT = Environment.getExternalStorageDirectory() + File.separator + "sisdb";
    private static final int DB_SCHEME_VERSION=4;
    private static final String DB_NAME = STATICS_ROOT + File.separator + "sisdb.sqlite";

    private DBHelper conn;
    private Context context;

    public dbGEO(Context ctx) {
        context = ctx;
        conn = new DBHelper(ctx);
    }

    public List<GEO> getGEO() {
        List<GEO> data = new ArrayList<GEO>();
        SQLiteDatabase db = conn.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT g1, g2, g3, g4, name1, name2, name3, name4 FROM set_geo_codes",null);
        if (c != null) {
            data = GEO.getGEOCursor(c);
        }
        return data;
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_SCHEME_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}

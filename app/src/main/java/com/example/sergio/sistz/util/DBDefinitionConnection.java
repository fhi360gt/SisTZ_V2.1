package com.example.sergio.sistz.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;

/**
 * Created by jlgarcia on 21/03/2018.
 */

public class DBDefinitionConnection {
    private static final String STATICS_ROOT = Environment.getExternalStorageDirectory() + File.separator + "sisdb";
    private static final String DB_NAME = "sisdb.sqlite";
    private static final int DB_VER = 4;
    private DBDefinitionHelper conn;
    private SQLiteDatabase db;
    private Context context;

    public DBDefinitionConnection(Context context) {
        this.context = context;
        conn = new DBDefinitionHelper(context);
      //Conexion cnSET = new Conexion(this,STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
    }
    public String testLoadEMISCode() {
        String getemiscode="";
        String sql_ = "SELECT emis FROM ms_0";
        if (conn==null){conn = new DBDefinitionHelper(context);}
        db = conn.getWritableDatabase();
        Cursor c = db.rawQuery(sql_,null);
        c.moveToFirst();
        if (c.getCount() > 0) {getemiscode = c.getString(0);}
        c.close();
        db.close();
        return  getemiscode;
    }
    public class DBDefinitionHelper extends SQLiteOpenHelper {

//        public DBDefinitionHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
//                                  int version) {
//            super(context, name, factory, version);
//        }

        public DBDefinitionHelper(Context context1) {
            super(context, STATICS_ROOT + File.separator + DB_NAME, null, DB_VER);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

//    Conexion cnSET = new Conexion(this,STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
//    SQLiteDatabase dbSET = cnSET.getReadableDatabase();



    /*
    Conexion cnSET = new Conexion(this,STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        //Cursor cur_data = dbSET.rawQuery(" SELECT a.tc, t.surname, t.givenname FROM _ta a  INNER JOIN teacher t ON t._id=a.tc and year_ta= " + school_year + " GROUP BY a.tc, t.surname, t.surname ORDER BY t.surname", null);
        Cursor cur_data = dbSET.rawQuery("SELECT a.tc, t.surname, t.givenname FROM _ta a  INNER JOIN teacher t ON t._id=a.tc  GROUP BY a.tc, t.surname, t.surname ORDER BY t.surname", null);
        cur_data.moveToFirst();
     */

}

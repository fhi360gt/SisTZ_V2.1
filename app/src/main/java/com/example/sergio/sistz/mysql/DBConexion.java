package com.example.sergio.sistz.mysql;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

/**
 * Created by Sergio on 2/22/2016.
 */
public class DBConexion extends SQLiteOpenHelper {
    private static String  DB_PATH = "sisdb";
    static final String DB_NAME = "sisdb.sqlite";
    public SQLiteDatabase myDatabase;
    private final Context myContext;

    public DBConexion(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    public void openDataBase() throws SQLException {
        String myPaht = DB_PATH + File.separator + DB_NAME;
        myDatabase = SQLiteDatabase.openDatabase(myPaht, null, SQLiteDatabase.OPEN_READWRITE);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

package com.example.sergio.sistz.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.example.sergio.sistz.R;
import com.example.sergio.sistz.mysql.Conexion;

import java.io.File;

/**
 * Created by jlgarcia on 20/03/2017.
 */

public class toolsfncs extends Activity{
    public static final String STATICS_ROOT = Environment.getExternalStorageDirectory() + File.separator + "sisdb.sqlite";

    public static void logFunctions(Context contexto, String startLog, String locationLog, String finishLog) {
        Conexion cnSET = new Conexion(contexto,STATICS_ROOT, null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();

        ContentValues logfcns = new ContentValues();
        logfcns.put("startlog", startLog);
        logfcns.put("locationlog", locationLog);
        logfcns.put("finishlog", finishLog);
        dbSET.insert("logfunctions", null, logfcns);

    }

    // *********** Control Alerts ************************
    public static void dialogAlertConfirm(Context contexto, Resources resources, int v){
        //Toast.makeText(getContext(),String.valueOf(v) ,Toast.LENGTH_SHORT).show();
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(contexto);
        dialogo1.setTitle(resources.getString(R.string.str_bl_msj1)); // Importante
        if (v == 1){dialogo1.setMessage(resources.getString(R.string.str_g_msj1));}
        if (v == 2){dialogo1.setMessage(resources.getString(R.string.str_g_msj2));} // Your application is up to date.
        if (v == 3){dialogo1.setMessage(resources.getString(R.string.str_g_msj3));} // You have made a Local backup.
        if (v == 4){dialogo1.setMessage(resources.getString(R.string.str_g_msj4));} // You have made a Local restore.
        if (v == 5){dialogo1.setMessage(resources.getString(R.string.str_g_msj5));} // You have made a WEB backup.
        if (v == 6){dialogo1.setMessage(resources.getString(R.string.str_g_msj6));} // You have made a WEB restore.
        if (v == 7){dialogo1.setMessage(resources.getString(R.string.synchronizing));} // You have made a WEB restore.
        if (v == 8){dialogo1.setMessage(resources.getString(R.string.not_connected_internet));} // You have made a WEB restore.
        if (v == 9){dialogo1.setMessage(resources.getString(R.string.str_bl_msj5));} // The information has been update...
        if (v == 10){dialogo1.setMessage(resources.getString(R.string.str_g_therearenot_student));} // The information has been update...

        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton(resources.getString(R.string.str_g_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                confirm();
            }
//        }).setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialogo1, int id) {
//                aceptar();
//            }
        });

        dialogo1.show();
    }

    private static void confirm() {
    }


}

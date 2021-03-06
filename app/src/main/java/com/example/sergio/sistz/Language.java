package com.example.sergio.sistz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sergio.sistz.mysql.Conexion;

import java.io.File;
import java.util.Locale;

/**
 * Created by Sergio on 2/26/2016.
 */
public class Language extends Activity implements View.OnClickListener {
    public static final String STATICS_ROOT = Environment.getExternalStorageDirectory() + File.separator + "sisdb";
    int fl_location = 1; // *********** Control change page
    LinearLayout ll_wizard;
    FrameLayout fl_part0; // ************ FrameLayout ***************
    //ImageButton btn_back, btn_next, btn_save;
    TextView btn_back, btn_next, btn_save, tv_english, tv_kiswahili;
    //CheckBox _col1, _col2, _col3, _col4, _col5, _col6, _col7, _col8, _col9;
    //EditText et_emis, et_school_name;
    String _IU="U", languageToLoadWizard="";
    private Locale locale;
    private Configuration config = new Configuration();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language);

        tv_english = (TextView) findViewById(R.id.tv_english);
        tv_kiswahili = (TextView) findViewById(R.id.tv_kiswahili);



        //  ************************ Objects assing *********************
        ll_wizard = (LinearLayout) findViewById(R.id.ll_wizard);
        fl_part0 = (FrameLayout) findViewById(R.id.fl_part0);

        //  ************************ Objects Buttoms *********************
        btn_back = (TextView) findViewById(R.id.btn_back);
        btn_next = (TextView) findViewById(R.id.btn_next);
        btn_save = (TextView) findViewById(R.id.btn_save);


        //************* Start FrameLayout **************************
        fl_part0.setVisibility(View.VISIBLE);


        // **************** CLICK ON BUTTONS ********************
        tv_english.setOnClickListener(this);
        tv_kiswahili.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        //btn_save.setOnClickListener(this);
        btn_back.setVisibility(View.INVISIBLE);
        btn_next.setVisibility(View.INVISIBLE);
        tv_english.setVisibility(View.VISIBLE);
        tv_kiswahili.setVisibility(View.VISIBLE);


        // ***************** LOCAD INFORMATION *************************
        //loadRecord();
    }


    // **************** Load DATA *************************
//    public void loadRecord() {
//        et_emis.setText("");
//        Conexion cnSET = new Conexion(getApplicationContext(),STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
//        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
//        Cursor cur_data = dbSET.rawQuery("SELECT _id, m_pp, m_p, m_s, a_pp, a_p, a_s, e_pp, e_p, e_s, emis, flag FROM ms_0 WHERE _id=1",null);
//        cur_data.moveToFirst();
//        if (cur_data.getCount() > 0) {
//            if(cur_data.getInt(1)==1) {_col1.setChecked(true);}
//            if(cur_data.getInt(2)==1) {_col2.setChecked(true);}
//            if(cur_data.getInt(3)==1) {_col3.setChecked(true);}
//            if(cur_data.getInt(4)==1) {_col4.setChecked(true);}
//            if(cur_data.getInt(5)==1) {_col5.setChecked(true);}
//            if(cur_data.getInt(6)==1) {_col6.setChecked(true);}
//            if(cur_data.getInt(7)==1) {_col7.setChecked(true);}
//            if(cur_data.getInt(8)==1) {_col8.setChecked(true);}
//            if(cur_data.getInt(9)==1) {_col9.setChecked(true);}
//            et_emis.setText(cur_data.getString(10));
//            et_school_name.setText(cur_data.getString(11));
//            _IU = "U";
//        } else {_IU = "I";}
//        cur_data.close();
//        dbSET.close();
//        cnSET.close();
//    }

//    public void updateRecord () {
//        Conexion cnSET = new Conexion(getApplicationContext(),STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
//        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
//        String sql = "", delimit=";", s1;
//        // ***************** CONTENT TO RECORD-SET **************************
//        ContentValues reg = new ContentValues();
//        //if (!et_emis.getText().toString().isEmpty()  ) {  //        if (!et_emis.getText().toString().isEmpty()) {  //
//        if ((!et_emis.getText().toString().isEmpty()) && (!et_emis.getText().toString().equals("0"))) {
//            if (_IU == "I" ) {reg.put("_id",1);}
//            //reg.put("flag", _IU); sql = sql  + "ms_0;" + getEMIS_code() + delimit;
//            reg.put("flag", et_school_name.getText().toString());
//            reg.put("emis", et_emis.getText().toString());
//            //reg.put("lang", MainActivity.languageToLoad);
//            //reg.put("lang", languageToLoadWizard);
//            sql = sql  + "ms_0;" + et_emis.getText().toString() + delimit;
//
//            if (_col1.isChecked() == true) {reg.put("m_pp", 1); sql = sql + "1" + delimit;} else {reg.put("m_pp",0); sql = sql + "0" + delimit;}
//            if (_col2.isChecked() == true) {reg.put("m_p", 1); sql = sql + "1" + delimit;} else {reg.put("m_p",0); sql = sql + "0" + delimit;}
//            if (_col3.isChecked() == true) {reg.put("m_s", 1); sql = sql + "1" + delimit;} else {reg.put("m_s",0); sql = sql + "0" + delimit;}
//            if (_col4.isChecked() == true) {reg.put("a_pp", 1); sql = sql + "1" + delimit;} else {reg.put("a_pp",0); sql = sql + "0" + delimit;}
//            if (_col5.isChecked() == true) {reg.put("a_p", 1); sql = sql + "1" + delimit;} else {reg.put("a_p",0); sql = sql + "0" + delimit;}
//            if (_col6.isChecked() == true) {reg.put("a_s", 1); sql = sql + "1" + delimit;} else {reg.put("a_s",0); sql = sql + "0" + delimit;}
//            if (_col7.isChecked() == true) {reg.put("e_pp", 1); sql = sql + "1" + delimit;} else {reg.put("e_pp",0); sql = sql + "0" + delimit;}
//            if (_col8.isChecked() == true) {reg.put("e_p", 1); sql = sql + "1" + delimit;} else {reg.put("e_p",0); sql = sql + "0" + delimit;}
//            if (_col9.isChecked() == true) {reg.put("e_s", 1); sql = sql + "1" + delimit;} else {reg.put("e_s",0); sql = sql + "0" + delimit;}
//            sql = sql + _IU;
//            try {
//                // ****************** Fill Bitacora
////                ContentValues Bitacora = new ContentValues();
////                Bitacora.put("sis_sql",sql);
////                dbSET.insert("sisupdate",null,Bitacora);
//                // ********************* Fill TABLE d
//                if (_IU=="I") {
//                    dbSET.insert("ms_0", null, reg); _IU="U";
//                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.str_bl_msj5), Toast.LENGTH_SHORT).show(); // "The information has been updated!!!"
//                } else {
//                    if (et_emis.getText().toString().equals(getEMIS_code_form_a())) {
//                        dbSET.update("ms_0", reg, "_id=1", null);
//                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.str_bl_msj5), Toast.LENGTH_SHORT).show(); // "The information has been updated!!!"
//                    } else {Toast.makeText(getApplicationContext(), "Ya ingreso información con ese código..." +et_emis.getText().toString() + " = "+ getEMIS_code_form_a(), Toast.LENGTH_SHORT).show();}
//                }
//                finish();
//
//            }catch (Exception e) {
//                Toast.makeText(getApplicationContext(), getResources().getString(R.string.str_bl2_msj1),Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(getApplicationContext(), getResources().getString(R.string.str_w_a),Toast.LENGTH_SHORT).show();
//        }
//
//        dbSET.close();
//        cnSET.close();
//    }

    // **************** CLICK ON BUTTONS ********************
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_next:
//                fl_location++;
//                change_fl(fl_location);
                //Toast.makeText(this, "Lenguaje Wizard --> "+languageToLoadWizard, Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_back:
//                fl_location--;
//                change_fl(fl_location);
                break;
            case R.id.btn_save:
                //dialogAlert(1);
                //updateRecord();
                break;
            case R.id.tv_english:
                saveLang("en");
                showLanguage("en");
                //languageToLoadWizard="en";
                //MainActivity.languageToLoad="en";
                break;
            case R.id.tv_kiswahili:
                saveLang("sw");
                showLanguage("sw");
                //languageToLoadWizard="sw";
                //MainActivity.languageToLoad="sw";
                break;
        }

    }

    // *********** Control change page
//    public void change_fl(Integer fl) {
//        if (fl > 6) {fl_location=6;}
//        if (fl < 1) {fl_location=1;}
//        if (fl > 5) {btn_next.setVisibility(View.INVISIBLE);} else {btn_next.setVisibility(View.VISIBLE);}
//        if (fl < 2) {btn_back.setVisibility(View.INVISIBLE);} else {btn_back.setVisibility(View.VISIBLE);}
//        fl_part0.setVisibility(View.GONE);
//
//
//        switch (fl_location){
//
//            case 1: ll_wizard.setBackgroundResource(R.drawable.w_step0); fl_part0.setVisibility(View.VISIBLE); break;
//
//        }
//    }

    // *********** Control Alerts ************************
//    public void dialogAlert(int v){
//        Toast.makeText(getApplicationContext(),String.valueOf(v) ,Toast.LENGTH_SHORT).show();
//        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
//        dialogo1.setTitle(getResources().getString(R.string.str_bl_msj1)); // Importante
//        if (v == 1){dialogo1.setMessage("Save and Exit !!!");}
//        if (v == 2){dialogo1.setMessage("Are you sure to quit?");}
//        dialogo1.setCancelable(false);
//        dialogo1.setPositiveButton(getResources().getString(R.string.str_g_cancel), new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialogo1, int id) {
//                aceptar();
//            }
//        }); // Cancel
//        dialogo1.setNegativeButton(getResources().getString(R.string.str_g_confirm), new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialogo1, int id) {
//                cancelar();
//            }
//        }); // Confirm
//        dialogo1.show();
//    }
//    public void aceptar() {   }
//    public void cancelar() {finish(); }

    // *********** END Control Alerts ************************

    public String getEMIS_code(){
        String getemiscode="";
        Conexion cnSET = new Conexion(this,STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        Cursor cur_data = dbSET.rawQuery("SELECT a1 FROM a", null);
        cur_data.moveToFirst();
        if (cur_data.getCount() > 0) {getemiscode = cur_data.getString(0);} else {getemiscode = "";}
        return getemiscode;
    }

    public String getEMIS_code_form_a(){
        String getemiscode="";
        Conexion cnSET = new Conexion(this, STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        try {
            Cursor cur_data = dbSET.rawQuery("SELECT a1 FROM a", null);
            cur_data.moveToFirst();
            if (cur_data.getCount() > 0) {getemiscode = cur_data.getString(0);} else {getemiscode = "";}
        }catch (Exception e) {}

        return getemiscode;
    }

    /**
     * Muestra una ventana de dialogo para elegir el nuevo idioma de la aplicacion
     * Cuando se hace clic en uno de los idiomas, se cambia el idioma de la aplicacion
     * y se recarga la actividad para ver los cambios
     * */
    public void showLanguage(String which){
        switch (which) {
            case "en":
                locale = new Locale("en");
                config.locale = locale;
                //MainActivity.language = 0;
                languageToLoadWizard="en";
                break;
            case "sw":
                locale = new Locale("sw");
                config.locale = locale;
                //MainActivity.language = 1;
                languageToLoadWizard="sw";
                break;
            case "es":
                locale = new Locale("es");
                config.locale = locale;
                languageToLoadWizard="es";
                break;
        }
        getResources().updateConfiguration(config, null);
        Intent refresh = new Intent(Language.this, Wizard.class);
        startActivity(refresh);
        finish();
    }

    private void saveLang(String lang){
        Conexion cnSET = new Conexion(this, STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        dbSET.execSQL("UPDATE lang SET lang='" + lang + "'");
    }
}

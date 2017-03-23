package com.example.sergio.sistz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sergio.sistz.mysql.Conexion;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Sergio on 3/13/2016.
 */
public class SettingsMenuStaff extends Activity implements View.OnClickListener{
    public static final String STATICS_ROOT = Environment.getExternalStorageDirectory() + File.separator + "sisdb";
    public static String EMIS_code = "";
    public static String TS_code = "", code="";
    ArrayList<String> list_1 = new ArrayList<>();
    ArrayList<String> list_code = new ArrayList<>();
    int fl_location = 1; // *********** Control change page
    FrameLayout fl_part1, fl_part2; // ************ FrameLayout ***************
    ListView lv_list;
    FloatingActionButton add_reg, find_reg, erase_reg;
    EditText et_find_reg;
    ImageButton ib_find_reg;
    //int code;



    @Override
    protected void onRestart() {
        super.onRestart();
        loadListTeacher();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadListTeacher();
        TS_code="";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_menu_staff);

        // ********************** Global vars ******************
        lv_list = (ListView) findViewById(R.id.lv_list);
        et_find_reg = (EditText) findViewById(R.id.et_find_reg);
        ib_find_reg = (ImageButton) findViewById(R.id.ib_find_reg);



        //  ************************ Objects assing *********************
        fl_part1 = (FrameLayout) findViewById(R.id.fl_part1);

        //  ************************ Objects Buttoms *********************

        add_reg = (FloatingActionButton) findViewById(R.id.add_reg);
        find_reg = (FloatingActionButton) findViewById(R.id.find_reg);

        //************* Start FrameLayout **************************
        fl_part1.setVisibility(View.VISIBLE);


        // **************** CLICK ON BUTTONS ********************
        add_reg.setOnClickListener(this);
        add_reg.setVisibility(View.VISIBLE);
        find_reg.setOnClickListener(this);
        ib_find_reg.setOnClickListener(this);
        // ***************** LOCAD INFORMATION *************************
        loadListTeacher();
        //loadRecord();


    }

    // ***************** Load Teacher/Staff LIST *************************
    public void loadListTeacher() {
        //Toast.makeText(getApplicationContext(), "Ahora CARGA LISTA DE TEACHER... !!! ", Toast.LENGTH_SHORT).show();
        Conexion cnSET = new Conexion(getApplicationContext(),STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        Cursor cur_data = dbSET.rawQuery("SELECT DISTINCT(_id), t_s, surname, givenname, sex, yearob, checkno, cp1, cp2, cp3, cp4, cp5, cp6, lt1 , lt2 , lt3 , lt4 , lt5 , prof_q, acad_q,  sub_t1, sub_t2, sub_t3, sub_t4, sub_t5, year_pos, salary, phone, email, addrs FROM teacher ORDER BY givenname, surname", null);
        String col_id, col_g1="";
        cur_data.moveToFirst();
        //TS_code = 0;
        list_1.clear();
        list_code.clear();
        String surName="", givenName="";
        if (cur_data.moveToFirst()) {
            do {
                //col_g1 = cur_data.getString(0) + " - " + cur_data.getString(2) + ", " + cur_data.getString(3);
                //if(cur_data.isEmpty()) {col_g1 = cur_data.getString(2).toString();} else {col_g1 = cur_data.getString(2).toString() + ", "+ cur_data.getString(3);}
                if (cur_data.getString(2) != null) {surName = cur_data.getString(2);} else {surName="";}
                if (cur_data.getString(3) != null) {givenName = cur_data.getString(3) + ", " ;} else {givenName="";}
                //col_g1 = cur_data.getString(3) + ", " + cur_data.getString(2);
                col_g1 = givenName + surName;
                        col_id = cur_data.getString(0);
                list_1.add(col_g1);
                list_code.add(col_id);
            } while (cur_data.moveToNext());

            ArrayAdapter adap_list = new ArrayAdapter(this, R.layout.row_menu_select, list_1);
            lv_list.setAdapter(adap_list);

            lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int posicion, long id) {
                    //Toast.makeText(getApplicationContext(), "Selected: " + String.valueOf(posicion) + ", Code: " + list_code.get(posicion) , Toast.LENGTH_SHORT).show();
                    ReportTS.TS_report_enable="";
                    code = list_code.get(posicion);
                    TS_code = list_code.get(posicion);
                    Intent intent1 = new Intent(SettingsMenuStaff.this, SettingsMenuStaff_menu.class);
                    startActivity(intent1);
                    loadListTeacher();
                }
            });
        }
        lv_list.refreshDrawableState();
    }


    // **************** CLICK ON BUTTONS ********************
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_reg:
//                Toast.makeText(getApplicationContext(),"Abre informacion del Teacher/Staff... !!! ",Toast.LENGTH_SHORT).show();
                TS_code="";
                Intent intent1 = new Intent(SettingsMenuStaff.this, SettingsMenuStaff_menu.class);
                startActivity(intent1);
                loadListTeacher();

                break;
            case R.id.find_reg:
                et_find_reg.setVisibility(View.VISIBLE);
                ib_find_reg.setVisibility(View.VISIBLE);

                break;
            case R.id.ib_find_reg:
                et_find_reg.setVisibility(View.INVISIBLE);
                ib_find_reg.setVisibility(View.INVISIBLE);
                if (findRecord(et_find_reg.getText().toString()).equals("1")){
                    TS_code = et_find_reg.getText().toString();

                    Intent intent2 = new Intent(SettingsMenuStaff.this, SettingsMenuStaff_menu.class);
                    startActivity(intent2);
                } else {
                    Toast.makeText(getApplicationContext(), "Not exist Teacher/Staff... !!! ", Toast.LENGTH_SHORT).show();
                }
                et_find_reg.setText("");
        }

    }

    // *********** Control Alerts ************************
    public void dialogAlert(int v){
        //Toast.makeText(getApplicationContext(),String.valueOf(v) ,Toast.LENGTH_SHORT).show();
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Important");
        if (v == 1){dialogo1.setMessage("Save and Exit !!!");}
        if (v == 2){dialogo1.setMessage("Are you sure to quit?");}
        if (v == 3){dialogo1.setMessage("Are you sure to delete record?");}

        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                cancelar();
            }
        });
        dialogo1.setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                aceptar();
            }
        });
        dialogo1.show();
    }
    public void aceptar() {
        Conexion cnSET = new Conexion(getApplicationContext(),STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        dbSET.delete("_t","_id=" + code,null);
        dbSET.close();
        cnSET.close();
        //initialize_upgrade();
        loadListTeacher();
    }

    public void cancelar() {
        //finish();
    }

    // *********** END Control Alerts ************************

    public String getEMIS_code(){
        String getemiscode="";
        Conexion cnSET = new Conexion(getApplicationContext(),STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        Cursor cur_data = dbSET.rawQuery("SELECT a1 FROM a", null);
        cur_data.moveToFirst();
        if (cur_data.getCount() > 0) {getemiscode = cur_data.getString(0);} else {getemiscode = "";}
        return getemiscode;
    }

    public String findRecord(String string){
        String getFindRecord="0";
        try {
            Conexion cnSET = new Conexion(getApplicationContext(),STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
            SQLiteDatabase dbSET = cnSET.getReadableDatabase();
            Cursor cur_data = dbSET.rawQuery("SELECT * FROM teacher WHERE _id=" + string, null);
            cur_data.moveToFirst();
            if (cur_data.getCount() > 0) {getFindRecord = "1";} else {getFindRecord = "0";}
        } catch (Exception e) {}
        return getFindRecord;
    }
}

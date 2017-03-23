package com.example.sergio.sistz;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.ResultReceiver;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sergio.sistz.mysql.Conexion;
import com.example.sergio.sistz.mysql.DBlogfunctions;
import com.example.sergio.sistz.util.BKDBWebUtility;
import com.example.sergio.sistz.util.BkDbUtility;
import com.example.sergio.sistz.util.CopyAssetDBUtility;
import com.example.sergio.sistz.util.toolsfncs;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends Activity implements View.OnClickListener {
    public static final String STATICS_ROOT = Environment.getExternalStorageDirectory() + File.separator + "sisdb";
    public static final String STATICS_ROOT_BK = Environment.getExternalStorageDirectory() + File.separator + "sisdbBK";
    private final static String DB_INDICATORS_NAME = "sisdb.sqlite";
    public String _EMISCODE="", _PhoneNumber="";
    FrameLayout fl2, fl3, fl4, fl5, fl6;
    LinearLayout ll_baseline, ll_dailyClassroom,ll_finance, ll_reports;
    ImageButton btn_base, btn_infra, btn_teacher, btn_student, btn_daily, btn_attendance, btn_evaluation, btn_behavior, btn_report, btn_setting,status, btn_number, btn_finance;
    ImageButton btn_ts, btn_st, btn_a, btn_f;
    //private String sistz_vrs=" 1.2";
    ArrayList<String> list_num = new ArrayList<>();
    SampleResultReceiver resultReceiever;
    String defaultUrl = "http://developer.android.com/assets/images/dac_logo.png";
    ProgressBar pd;
    public Locale locale;
    public Configuration config = new Configuration();
    TextView tv_baseline, tv_dailyclassroom, tv_finance, tv_reports;
    public static Integer language=1;
    //String languageToLoad="en";
    //public Integer language;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS=124;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private GoogleApiClient client;
    Calendar calendar = Calendar.getInstance();
    public int school_year = calendar.get(Calendar.YEAR);

    ProgressDialog progress;
    //String delimit="%";
    private int totalProgressTime = 0;
    private Handler handler = new Handler();

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            status.setBackgroundResource(R.drawable.internet_signal);
        } else {
            status.setBackgroundResource(R.drawable.cell_signal);
        }
        server_number();
        //showLanguage();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // ************* Muestra el lenguaje que se ha selecconado ******************
        //Toast.makeText(getBaseContext(),"Lenguage: " + language,Toast.LENGTH_SHORT).show();
        //language=getLang();
        // ****************** Este muetra el lenguaje *********************
        showLanguage();
//        languageToLoad = getLang();
//        //String languageToLoad  = "en"; // your language
//        Locale locale = new Locale(languageToLoad);
//        Locale.setDefault(locale);
//        Configuration config = new Configuration();
//        config.locale = locale;
//        getBaseContext().getResources().updateConfiguration(config,
//                getBaseContext().getResources().getDisplayMetrics());

        //Toast.makeText(this, "antes de salir... "+getEMIS_code(), Toast.LENGTH_SHORT).show();
        if (getEMIS_code() == "") {finish();}
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            status.setBackgroundResource(R.drawable.internet_signal);
        } else {
            status.setBackgroundResource(R.drawable.cell_signal);
        }
        server_number();
        //showLanguage(Wizard.language);
        _PhoneNumber = getPhone_number();
        // ************ Alerta que no hay numero de telefono (no eliminar) ***********
        // if (_PhoneNumber.isEmpty()) {dialogAlert(1);}
    }


    @Override
    protected void onResume() {
        super.onResume();
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            status.setBackgroundResource(R.drawable.internet_signal);
        } else {
            status.setBackgroundResource(R.drawable.cell_signal);
        }
        server_number();
        //showLanguage();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            }
        }



        CopyAssetDBUtility.copyDB(this, DB_INDICATORS_NAME);

        // ************** FUNCIONA PARA CREAR LOG *************************************
//        DBlogfunctions cnlogFuncion = new DBlogfunctions(this, STATICS_ROOT , null, 4);
//        SQLiteDatabase db = cnlogFuncion.getWritableDatabase();
//        db.close();
        String sqlCreate_table_logFunction = "CREATE TABLE IF NOT EXISTS\"logfunctions\" (\"id\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , \"startlog\"  TEXT, \"locationlog\" TEXT, \"finishlog\" TEXT, \"flag\" INTEGER DEFAULT 1)";
        Conexion cnSETlogFunction = new Conexion(this, STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSETlogFunction = cnSETlogFunction.getReadableDatabase();
        dbSETlogFunction.execSQL(sqlCreate_table_logFunction);
        dbSETlogFunction.close();
        cnSETlogFunction.close();

        //new_set_geo_codes();

        // **************** LOAD Default Language ****************
        //showLanguage();
        // ****************** languageToLoad = getLang();
        //Toast.makeText(getBaseContext(),"Lenguage: " + languageToLoad,Toast.LENGTH_SHORT).show();
        //String languageToLoad  = "en"; // your language

        Locale locale = new Locale(getLang());
        //Locale locale = new Locale("es");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        this.setContentView(R.layout.activity_main);
        // ********************* END Default languaje **************

//        TextView tv_sistz_vrs = (TextView) findViewById(R.id.tv_sistz_vrs);
//        tv_sistz_vrs.setText(sistz_vrs + " 1.2");
        resultReceiever = new SampleResultReceiver(new Handler());
        pd = (ProgressBar) findViewById(R.id.downloadPD);

        status = (ImageButton)findViewById(R.id.btn_status);
        btn_number=(ImageButton) findViewById(R.id.btn_cell_number);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            status.setBackgroundResource(R.drawable.internet_signal);
        } else {
            status.setBackgroundResource(R.drawable.cell_signal);
        }

        server_number();
        // ********************** Global vars ******************
        fl2 = (FrameLayout) findViewById(R.id.fl2);
        fl3 = (FrameLayout) findViewById(R.id.fl3);
        fl4 = (FrameLayout) findViewById(R.id.fl4);
        fl5 = (FrameLayout) findViewById(R.id.fl5);
        fl6 = (FrameLayout) findViewById(R.id.fl6);
        ll_baseline = (LinearLayout) findViewById(R.id.ll_baseline);
        ll_dailyClassroom = (LinearLayout) findViewById(R.id.ll_dailyClassroom);
        ll_finance = (LinearLayout) findViewById(R.id.ll_finance);
        ll_reports = (LinearLayout) findViewById(R.id.ll_reports);

        //  ************************ Objects Buttoms *********************
        btn_base = (ImageButton) findViewById(R.id.btn_base);
        btn_infra = (ImageButton) findViewById(R.id.btn_infra);
        btn_teacher = (ImageButton) findViewById(R.id.btn_teacher);
        btn_student = (ImageButton) findViewById(R.id.btn_student);
        btn_daily = (ImageButton) findViewById(R.id.btn_daily);
        btn_attendance = (ImageButton) findViewById(R.id.btn_attendance);
        btn_evaluation = (ImageButton) findViewById(R.id.btn_evaluation);
        btn_behavior = (ImageButton) findViewById(R.id.btn_behavior);
        btn_finance = (ImageButton) findViewById(R.id.btn_finance);
        btn_report = (ImageButton) findViewById(R.id.btn_reports);
        btn_setting = (ImageButton) findViewById(R.id.btn_setting);
        btn_ts = (ImageButton) findViewById(R.id.btn_ts);
        btn_st = (ImageButton) findViewById(R.id.btn_st);
        btn_a = (ImageButton) findViewById(R.id.btn_a);
        btn_f = (ImageButton) findViewById(R.id.btn_f);
        tv_baseline = (TextView) findViewById(R.id.tv_baseline);
        tv_dailyclassroom = (TextView) findViewById(R.id.tv_dailyclassroom);
        tv_finance = (TextView) findViewById(R.id.tv_finance);
        tv_reports = (TextView) findViewById(R.id.tv_reports);


        //************* Start FrameLayout **************************

        fl2.setVisibility(View.VISIBLE);
        fl3.setVisibility(View.GONE);
        fl4.setVisibility(View.GONE);
        fl5.setVisibility(View.GONE);
        fl6.setVisibility(View.GONE);

        _EMISCODE = getEMIS_code();
        //Toast.makeText(this, _EMISCODE, Toast.LENGTH_SHORT).show();

        _PhoneNumber = getPhone_number();
        // *********** Indica que no hay numero de telefo definido (no elimnar) ***********
        //if (_PhoneNumber.isEmpty()) {dialogAlert(1);}

        // **************** CLICK ON BUTTONS ********************

        start_menu();
        //Toast.makeText(getBaseContext(),"Lenguage: " + getLang(),Toast.LENGTH_SHORT).show();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        doBindService();

    }


    @SuppressLint("ParcelCreator")
    private class SampleResultReceiver extends ResultReceiver {

        public SampleResultReceiver(Handler handler) {
            super(handler);
            // TODO Auto-generated constructor stub
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            // TODO Auto-generated method stub
            switch (resultCode) {
                case DownloadService.DOWNLOAD_ERROR:
                    Toast.makeText(getApplicationContext(), "error in download",
                            Toast.LENGTH_SHORT).show();
                    pd.setVisibility(View.INVISIBLE);
                    break;

                case DownloadService.DOWNLOAD_SUCCESS:



                    Toast.makeText(getApplicationContext(),
                            "image download via IntentService is done",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    File file = new File("/sdcard/Sis_tz.apk");
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    startActivity(intent);
                    pd.setIndeterminate(false);
                    pd.setVisibility(View.INVISIBLE);

                    break;
            }
            super.onReceiveResult(resultCode, resultData);
        }

    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    void doBindService(){
        bindService(new Intent(this, service_sms.class), mConnection, Context.BIND_AUTO_CREATE);
    }
    private void start_menu () {

        //*****tesst***//
//        String one="1";
//        toolsfncs.logFunctions(this, one,one,one);

        // ************** AUTOMATIC UPDATE... ***************
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            if (downloadText().equals("0")) {

            } else {
                if (downloadText().equals(String.valueOf(getVersionCode(this)))) {

                }
                else{
                    dialogAlert1();
                }
            }
        }

        if (getFlagDatabase().equals("0") || getFlagDatabase().equals("1") ) {
           // initializeDatabase();
        }

        if (getFlagbkTable().equals("0")) {
            backup();
        }

        //dialogAlert(7);
        new_set_geo_codes();

        if (!_EMISCODE.toString().equals("")) {
            _buttons();
            //addLang();
            //showLanguage(language);
        } else {
            //Intent intent0 = new Intent(MainActivity.this, SettingsMenu_0.class);
            //showDialog();
            //addLang();
            Intent intent0 = new Intent(MainActivity.this, Language.class);
            //Intent intent0 = new Intent(MainActivity.this, Wizard.class);
            startActivity(intent0);
            _buttons();
            //showLanguage(Wizard.language);
        }
    }

    private void _buttons () {
        btn_setting.setOnClickListener(this);
        btn_base.setOnClickListener(this);
        btn_infra.setOnClickListener(this);
        btn_teacher.setOnClickListener(this);
        btn_student.setOnClickListener(this);
        btn_daily.setOnClickListener(this);
        btn_attendance.setOnClickListener(this);
        btn_evaluation.setOnClickListener(this);
        btn_behavior.setOnClickListener(this);
        btn_finance.setOnClickListener(this);
        btn_report.setOnClickListener(this);
        //btn_base.setImageDrawable(getResources().getDrawable(R.drawable.icon4_));
        btn_ts.setOnClickListener(this);
        btn_st.setOnClickListener(this);
        btn_a.setOnClickListener(this);
        btn_f.setOnClickListener(this);

        ll_baseline.setOnClickListener(this);
        ll_dailyClassroom.setOnClickListener(this);
        ll_finance.setOnClickListener(this);
        ll_reports.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_baseline:
                fl2.setVisibility(View.VISIBLE);
                fl3.setVisibility(View.GONE);
                fl4.setVisibility(View.GONE);
                fl6.setVisibility(View.GONE);
                ll_baseline.setBackgroundColor(Color.parseColor("#F47321"));
                tv_baseline.setTextColor(Color.parseColor("#ffffff"));
                ll_dailyClassroom.setBackgroundColor(Color.parseColor("#343434"));
                tv_dailyclassroom.setTextColor(Color.parseColor("#F47321"));
                ll_finance.setBackgroundColor(Color.parseColor("#343434"));
                tv_finance.setTextColor(Color.parseColor("#F47321"));
                ll_reports.setBackgroundColor(Color.parseColor("#343434"));
                tv_reports.setTextColor(Color.parseColor("#F47321"));
                btn_base.setImageDrawable(getResources().getDrawable(R.drawable.icon4_));
                btn_daily.setImageDrawable(getResources().getDrawable(R.drawable.icon5));
                btn_finance.setImageDrawable(getResources().getDrawable(R.drawable.iconf));
                btn_report.setImageDrawable(getResources().getDrawable(R.drawable.icon6));
                break;
            case R.id.ll_dailyClassroom:
                fl2.setVisibility(View.GONE);
                fl3.setVisibility(View.VISIBLE);
                fl4.setVisibility(View.GONE);
                fl6.setVisibility(View.GONE);
                ll_baseline.setBackgroundColor(Color.parseColor("#343434"));
                tv_baseline.setTextColor(Color.parseColor("#F47321"));
                ll_dailyClassroom.setBackgroundColor(Color.parseColor("#F47321"));
                tv_dailyclassroom.setTextColor(Color.parseColor("#ffffff"));
                ll_finance.setBackgroundColor(Color.parseColor("#343434"));
                tv_finance.setTextColor(Color.parseColor("#F47321"));
                ll_reports.setBackgroundColor(Color.parseColor("#343434"));
                tv_reports.setTextColor(Color.parseColor("#F47321"));
                btn_base.setImageDrawable(getResources().getDrawable(R.drawable.icon4));
                btn_daily.setImageDrawable(getResources().getDrawable(R.drawable.icon5_));
                btn_finance.setImageDrawable(getResources().getDrawable(R.drawable.iconf));
                btn_report.setImageDrawable(getResources().getDrawable(R.drawable.icon6));
                break;
            case R.id.ll_finance:
                fl2.setVisibility(View.GONE);
                fl3.setVisibility(View.GONE);
                fl4.setVisibility(View.GONE);
                fl6.setVisibility(View.VISIBLE);
                ll_baseline.setBackgroundColor(Color.parseColor("#343434"));
                tv_baseline.setTextColor(Color.parseColor("#F47321"));
                ll_dailyClassroom.setBackgroundColor(Color.parseColor("#343434"));
                tv_dailyclassroom.setTextColor(Color.parseColor("#F47321"));
                ll_finance.setBackgroundColor(Color.parseColor("#F47321"));
                tv_finance.setTextColor(Color.parseColor("#ffffff"));
                ll_reports.setBackgroundColor(Color.parseColor("#343434"));
                tv_reports.setTextColor(Color.parseColor("#F47321"));
                btn_base.setImageDrawable(getResources().getDrawable(R.drawable.icon4));
                btn_daily.setImageDrawable(getResources().getDrawable(R.drawable.icon5));
                btn_finance.setImageDrawable(getResources().getDrawable(R.drawable.iconf_));
                btn_report.setImageDrawable(getResources().getDrawable(R.drawable.icon6));
                break;
            case R.id.ll_reports:
                fl2.setVisibility(View.GONE);
                fl3.setVisibility(View.GONE);
                fl4.setVisibility(View.VISIBLE);
                fl6.setVisibility(View.GONE);
                ll_baseline.setBackgroundColor(Color.parseColor("#343434"));
                tv_baseline.setTextColor(Color.parseColor("#F47321"));
                ll_dailyClassroom.setBackgroundColor(Color.parseColor("#343434"));
                tv_dailyclassroom.setTextColor(Color.parseColor("#F47321"));
                ll_finance.setBackgroundColor(Color.parseColor("#343434"));
                tv_finance.setTextColor(Color.parseColor("#F47321"));
                ll_reports.setBackgroundColor(Color.parseColor("#F47321"));
                tv_reports.setTextColor(Color.parseColor("#ffffff"));
                btn_base.setImageDrawable(getResources().getDrawable(R.drawable.icon4));
                btn_daily.setImageDrawable(getResources().getDrawable(R.drawable.icon5));
                btn_finance.setImageDrawable(getResources().getDrawable(R.drawable.iconf));
                btn_report.setImageDrawable(getResources().getDrawable(R.drawable.icon6_));
                break;
            case R.id.btn_base:
                fl2.setVisibility(View.VISIBLE);
                fl3.setVisibility(View.GONE);
                fl4.setVisibility(View.GONE);
                fl6.setVisibility(View.GONE);
                ll_baseline.setBackgroundColor(Color.parseColor("#F47321"));
                tv_baseline.setTextColor(Color.parseColor("#ffffff"));
                ll_dailyClassroom.setBackgroundColor(Color.parseColor("#343434"));
                tv_dailyclassroom.setTextColor(Color.parseColor("#F47321"));
                ll_finance.setBackgroundColor(Color.parseColor("#343434"));
                tv_finance.setTextColor(Color.parseColor("#F47321"));
                ll_reports.setBackgroundColor(Color.parseColor("#343434"));
                tv_reports.setTextColor(Color.parseColor("#F47321"));
                btn_base.setImageDrawable(getResources().getDrawable(R.drawable.icon4_));
                btn_daily.setImageDrawable(getResources().getDrawable(R.drawable.icon5));
                btn_finance.setImageDrawable(getResources().getDrawable(R.drawable.iconf));
                btn_report.setImageDrawable(getResources().getDrawable(R.drawable.icon6));
//                fl2.setVisibility(View.VISIBLE);
//                fl3.setVisibility(View.GONE);
//                fl4.setVisibility(View.GONE);
//                fl5.setVisibility(View.GONE);
//                btn_base.setImageDrawable(getResources().getDrawable(R.drawable.icon4_));
//                btn_daily.setImageDrawable(getResources().getDrawable(R.drawable.icon5));
//                btn_report.setImageDrawable(getResources().getDrawable(R.drawable.icon6));
                break;
            case R.id.btn_daily:
                fl2.setVisibility(View.GONE);
                fl3.setVisibility(View.VISIBLE);
                fl4.setVisibility(View.GONE);
                fl6.setVisibility(View.GONE);
                ll_baseline.setBackgroundColor(Color.parseColor("#343434"));
                tv_baseline.setTextColor(Color.parseColor("#F47321"));
                ll_dailyClassroom.setBackgroundColor(Color.parseColor("#F47321"));
                tv_dailyclassroom.setTextColor(Color.parseColor("#ffffff"));
                ll_finance.setBackgroundColor(Color.parseColor("#343434"));
                tv_finance.setTextColor(Color.parseColor("#F47321"));
                ll_reports.setBackgroundColor(Color.parseColor("#343434"));
                tv_reports.setTextColor(Color.parseColor("#F47321"));
                btn_base.setImageDrawable(getResources().getDrawable(R.drawable.icon4));
                btn_daily.setImageDrawable(getResources().getDrawable(R.drawable.icon5_));
                btn_finance.setImageDrawable(getResources().getDrawable(R.drawable.iconf));
                btn_report.setImageDrawable(getResources().getDrawable(R.drawable.icon6));
//                fl2.setVisibility(View.GONE);
//                fl3.setVisibility(View.VISIBLE);
//                fl4.setVisibility(View.GONE);
//                fl5.setVisibility(View.GONE);
//                btn_base.setImageDrawable(getResources().getDrawable(R.drawable.icon4));
//                btn_daily.setImageDrawable(getResources().getDrawable(R.drawable.icon5_));
//                btn_report.setImageDrawable(getResources().getDrawable(R.drawable.icon6));
                break;
            case R.id.btn_finance:
                fl2.setVisibility(View.GONE);
                fl3.setVisibility(View.GONE);
                fl4.setVisibility(View.GONE);
                fl6.setVisibility(View.VISIBLE);
                ll_baseline.setBackgroundColor(Color.parseColor("#343434"));
                tv_baseline.setTextColor(Color.parseColor("#F47321"));
                ll_dailyClassroom.setBackgroundColor(Color.parseColor("#343434"));
                tv_dailyclassroom.setTextColor(Color.parseColor("#F47321"));
                ll_finance.setBackgroundColor(Color.parseColor("#F47321"));
                tv_finance.setTextColor(Color.parseColor("#ffffff"));
                ll_reports.setBackgroundColor(Color.parseColor("#343434"));
                tv_reports.setTextColor(Color.parseColor("#F47321"));
                btn_base.setImageDrawable(getResources().getDrawable(R.drawable.icon4));
                btn_daily.setImageDrawable(getResources().getDrawable(R.drawable.icon5));
                btn_finance.setImageDrawable(getResources().getDrawable(R.drawable.icon6_));
                btn_report.setImageDrawable(getResources().getDrawable(R.drawable.icon6));
                break;
            case R.id.btn_reports:
                fl2.setVisibility(View.GONE);
                fl3.setVisibility(View.GONE);
                fl4.setVisibility(View.VISIBLE);
                fl6.setVisibility(View.GONE);
                ll_baseline.setBackgroundColor(Color.parseColor("#343434"));
                tv_baseline.setTextColor(Color.parseColor("#F47321"));
                ll_dailyClassroom.setBackgroundColor(Color.parseColor("#343434"));
                tv_dailyclassroom.setTextColor(Color.parseColor("#F47321"));
                ll_finance.setBackgroundColor(Color.parseColor("#343434"));
                tv_finance.setTextColor(Color.parseColor("#F47321"));
                ll_reports.setBackgroundColor(Color.parseColor("#F47321"));
                tv_reports.setTextColor(Color.parseColor("#ffffff"));
                btn_base.setImageDrawable(getResources().getDrawable(R.drawable.icon4));
                btn_daily.setImageDrawable(getResources().getDrawable(R.drawable.icon5));
                btn_finance.setImageDrawable(getResources().getDrawable(R.drawable.iconf));
                btn_report.setImageDrawable(getResources().getDrawable(R.drawable.icon6_));
//                //dialogAlert(1);
//                fl2.setVisibility(View.GONE);
//                fl3.setVisibility(View.GONE);
//                fl4.setVisibility(View.VISIBLE);
//                fl5.setVisibility(View.GONE);
//                btn_base.setImageDrawable(getResources().getDrawable(R.drawable.icon4));
//                btn_daily.setImageDrawable(getResources().getDrawable(R.drawable.icon5));
//                btn_report.setImageDrawable(getResources().getDrawable(R.drawable.icon6_));
                break;
            case R.id.btn_infra:
                Intent intent1 = new Intent(MainActivity.this, SettingsMenuInfra.class);
                startActivity(intent1);
                break;
            case R.id.btn_teacher:
                //dialogAlert(2);
                Intent intent2 = new Intent(MainActivity.this, SettingsMenuStaff.class);
                startActivity(intent2);
                break;
            case R.id.btn_student:
                //dialogAlert(2);
                Intent intent3 = new Intent(MainActivity.this, SettingsMenuStudent.class);
                startActivity(intent3);
                break;
            case R.id.btn_attendance:
                //dialogAlert(2);
                Intent intent4 = new Intent(MainActivity.this, DailyClassroomAttendance.class);
                startActivity(intent4);
                break;
            case R.id.btn_evaluation:
                //dialogAlert(2);
                Intent intent5 = new Intent(MainActivity.this, DailyClassroomEvaluation.class);
                startActivity(intent5);
                break;
            case R.id.btn_behavior:
                //dialogAlert(2);
                Intent intent6 = new Intent(MainActivity.this, DailyClassroomBehaviour.class);
                startActivity(intent6);
                break;
            case R.id.btn_ts:
                //dialogAlert(2);
                //Intent intent7 = new Intent(MainActivity.this, ReportTeacherStaff.class);
                Intent intent7 = new Intent(MainActivity.this, ReportTS.class);
                startActivity(intent7);
                break;
            case R.id.btn_st:
                //dialogAlert(2);
                //Intent intent8 = new Intent(MainActivity.this, ReportStudent.class);
                Intent intent8 = new Intent(MainActivity.this, ReportS.class);
                startActivity(intent8);
                break;
            case R.id.btn_a:
                Intent intent9 = new Intent(MainActivity.this, ReportA.class);
                startActivity(intent9);
                break;
            case R.id.btn_f:
                //Toast.makeText(MainActivity.this, "Formulario financiero... : ", Toast.LENGTH_SHORT).show();
                Intent intent10 = new Intent(MainActivity.this, FinanceForm.class);
                startActivity(intent10);
                break;
            case R.id.btn_setting:
                popupMenu();
                break;
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.sergio.sistz/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.sergio.sistz/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    /** Popup Menu */
    private void popupMenu()
    {
        //Crea instancia a PopupMenu
        PopupMenu popup = new PopupMenu(this, btn_setting );
        popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());
        //registra los eventos click para cada item del menu
        final Context contexto = this;
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_settings1) {
                    // Toast.makeText(MainActivity.this, "Ejecutar : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(MainActivity.this, SettingsMenu_0.class);
                    //Intent intent1 = new Intent(MainActivity.this, Wizard.class);
                    startActivity(intent1);
                } else if (item.getItemId() == R.id.action_settings2) {
                    Intent intent1 = new Intent(MainActivity.this, login.class);
                    startActivity(intent1);
//                } else if (item.getItemId() == R.id.action_settings3) {
//                    Toast.makeText(MainActivity.this,
//                            "Ejecutar : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == R.id.action_settings4) {

                    if (isInternetPresent) {
                        if (downloadText().equals("0")) {
                            dialogAlert(8);
                        } else {
                            if (downloadText().equals(String.valueOf(getVersionCode(contexto)))) {
                                dialogAlert(2);
                            }
                            else{
                                dialogAlert1();
                            }
                        }
                    }
                    else{
                        dialogAlert(8);
                    }

//                    if (downloadText().equals(String.valueOf(getVersionCode(contexto)))) {
//                        dialogAlert(2);
//                    } else {
//                        dialogAlert1();
//
//                    }
                } else if (item.getItemId() == R.id.action_settings5) {
                    //Toast.makeText(MainActivity.this,"Ejecutar : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(MainActivity.this, About.class);
                    startActivity(intent1);
                } else if (item.getItemId() == R.id.action_settings6) {
                    dialogAlert2(1); // Backup Local and WEB
                } else if (item.getItemId() == R.id.action_settings8) {
                    dialogAlert2(2);
                } else if (item.getItemId() == R.id.action_settings9) {
                    showDialog();
                } else if (item.getItemId() == R.id.action_settings10) {
//                    if (getFlagDatabase().equals("0") || getFlagDatabase().equals("1")) {
//                        dialogAlert3(3);
////                        initializeDatabase();
////                        Toast.makeText(getApplicationContext(), "Initialize database", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.str_g_msj12), Toast.LENGTH_LONG).show();
//                    }
                    dialogAlert3(4);
                    //aut_promotion();
                }

                return true;
            }
        });
        popup.show();
    }



    //modificacion PY



    public static int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException ex) {}
        return 0;
    }

    private String downloadText() {
        int BUFFER_SIZE = 2000;
        InputStream in = null;
        try {
            in = openHttpConnection();
        } catch (IOException e1) {
            return "";
        }

        String str = "";
        if (in != null) {
            InputStreamReader isr = new InputStreamReader(in);
            int charRead;
            char[] inputBuffer = new char[BUFFER_SIZE];
            try {
                while ((charRead = isr.read(inputBuffer)) > 0) {
                    // ---convert the chars to a String---
                    String readString = String.copyValueOf(inputBuffer, 0, charRead);
                    str += readString;
                    inputBuffer = new char[BUFFER_SIZE];
                }
                in.close();
            } catch (IOException e) {
                return "";
            }
        }
        if (str.length()<=4){
        }
        else{
                str = "0";
        }
        return str;
    }


    private InputStream openHttpConnection() throws IOException {
        InputStream in = null;
        int response = -1;

        URL url = new URL("http://fhi360bi.org/user/tz_app/version.txt");
        //URL url = new URL(getURL());  // ya está listo para leer el URL de la base de datos.
        //Toast.makeText(this, getURL(), Toast.LENGTH_SHORT).show();
        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection)) {
            throw new IOException("Not an HTTP connection");
        }

        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        } catch (Exception ex) {
            throw new IOException("Error connecting");
        }
        return in;
    }

    //MOdificacion py fin

    public String getEMIS_code(){
        String getemiscode="";
        Conexion cnSET = new Conexion(this, STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        try {
            Cursor cur_data = dbSET.rawQuery("SELECT emis FROM ms_0", null);
            cur_data.moveToFirst();
            if (cur_data.getCount() > 0) {getemiscode = cur_data.getString(0);} else {getemiscode = "";}
        }catch (Exception e) {}

        return getemiscode;
    }

    public String getPhone_number(){
        String getPhoneNumber="";
        Conexion cnSET = new Conexion(this, STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        try {
            Cursor cur_data = dbSET.rawQuery("SELECT mobil_server from login", null);
            cur_data.moveToFirst();
            if (cur_data.getCount() > 0) {getPhoneNumber = cur_data.getString(0);} else {getPhoneNumber = "";
            }
        } catch (Exception e) {}

        return getPhoneNumber;
    }

    public String getURL(){
        String getUrl="";
        Conexion cnSET = new Conexion(this, STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        try {
            Cursor cur_data = dbSET.rawQuery("SELECT server_ip from login", null);
            cur_data.moveToFirst();
            if (cur_data.getCount() > 0) {getUrl = cur_data.getString(0);} else {getUrl = "";
            }
        } catch (Exception e) {}

        return getUrl;
    }

//    public String getLang(){
//        String getLang="en";
//        Conexion cnSET = new Conexion(this, STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
//        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
//        try {
//            Cursor cur_data = dbSET.rawQuery("SELECT lang from lang", null);
//            cur_data.moveToFirst();
//            if (cur_data.getCount() > 0 & !cur_data.getString(0).isEmpty()) {getLang = cur_data.getString(0);}
//                //if (getLang.isEmpty()) {saveLang("en");}
//            else {saveLang("en");}
//        } catch (Exception e) {addLang();} // saveLang("es");}
//        return getLang;
//    }

    public String getLang(){
        String getLang="";
        Conexion cnSET = new Conexion(this, STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        Cursor cur_data = dbSET.rawQuery("SELECT lang from lang", null);
        cur_data.moveToFirst();
        getLang = cur_data.getString(0);
        return getLang;
    }

    public void  addLang(){
        try {
            Conexion cnSET = new Conexion(this, STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
            SQLiteDatabase dbSET = cnSET.getReadableDatabase();
            dbSET.execSQL("ALTER TABLE ms_0  ADD lang TEXT");
            //dbSET.execSQL("UPDATE TABLE ms_0  SET lang='en'");
        } catch (Exception e) {}
    }

    private void server_number(){
        Conexion cnGEO3 = new Conexion(getApplicationContext(), STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbGEO3 = cnGEO3.getWritableDatabase();
        Cursor cur_num = dbGEO3.rawQuery("SELECT mobil_server from login ", null);
        String col_num;
        cur_num.moveToFirst();
        if (cur_num.moveToFirst()) {
            do {
                col_num = cur_num.getString(0);
                list_num.add(col_num);
            } while (cur_num.moveToNext());
        }
        cur_num.close();
        if(list_num.isEmpty()){
            btn_number.setVisibility(View.VISIBLE);
            //Toast.makeText(MainActivity.this, "Server number not defined. Go to Settings->Administrator", Toast.LENGTH_SHORT).show();
            //dialogAlert(1);
        }
        else{
            btn_number.setVisibility(View.INVISIBLE);
        }
    }

    // *********** Control Alerts ************************
    public void dialogAlert(int v){
        //Toast.makeText(getContext(),String.valueOf(v) ,Toast.LENGTH_SHORT).show();
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle(getResources().getString(R.string.str_bl_msj1)); // Importante
        if (v == 1){dialogo1.setMessage(getResources().getString(R.string.str_g_msj1));}
        if (v == 2){dialogo1.setMessage(getResources().getString(R.string.str_g_msj2));} // Your application is up to date.
        if (v == 3){dialogo1.setMessage(getResources().getString(R.string.str_g_msj3));} // You have made a Local backup.
        if (v == 4){dialogo1.setMessage(getResources().getString(R.string.str_g_msj4));} // You have made a Local restore.
        if (v == 5){dialogo1.setMessage(getResources().getString(R.string.str_g_msj5));} // You have made a WEB backup.
        if (v == 6){dialogo1.setMessage(getResources().getString(R.string.str_g_msj6));} // You have made a WEB restore.
        if (v == 7){dialogo1.setMessage(getResources().getString(R.string.synchronizing));} // You have made a WEB restore.
        if (v == 8){dialogo1.setMessage(getResources().getString(R.string.not_connected_internet));} // You have made a WEB restore.

        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton(getResources().getString(R.string.str_g_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                cancelar();
            }
        });
//        dialogo1.setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialogo1, int id) {
//                aceptar();
//            }
//        });

        dialogo1.show();
    }

    public void dialogAlert1(){
        //Toast.makeText(getContext(),String.valueOf(v) ,Toast.LENGTH_SHORT).show();
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle(getResources().getString(R.string.str_bl_msj1)); // Importante
        dialogo1.setMessage(getResources().getString(R.string.str_g_msj7)); // "There is a new version available. Update?"

        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton(getResources().getString(R.string.str_g_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) { // OK
//                Intent updateIntent = new Intent(Intent.ACTION_VIEW,
//                        Uri.parse("http://fhi360bi.org/user/tz_app/SIS_tz.apk"));
//                startActivity(updateIntent);
                Intent startIntent = new Intent(MainActivity.this,
                        DownloadService.class);
                startIntent.putExtra("receiver", resultReceiever);
                startIntent.putExtra("url","http://www.fhi360bi.org/user/tz_app/SIS_tz.apk");
                startService(startIntent);

                pd.setVisibility(View.VISIBLE);
                pd.setIndeterminate(true);
            }
        });
//        dialogo1.setNegativeButton(getResources().getString(R.string.str_g_cancel), new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialogo1, int id) { // Cancel
//                //aceptar();
//                cancelar();
//            }
//        });
        dialogo1.show();
    }
    public void aceptar() {}
    public void cancelar() {} //finish();}

    public void dialogAlert2(int v){
        final int v_BR = v;
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle(getResources().getString(R.string.str_bl_msj1)); // Importante
        if (v == 1){dialogo1.setMessage(getResources().getString(R.string.str_g_msj8));} // You are about to make a backup ...
        if (v == 2) {dialogo1.setMessage(getResources().getString(R.string.str_g_msj9));} // You are about to make a restore ...
        dialogo1.setCancelable(true);
        dialogo1.setNegativeButton(getResources().getString(R.string.str_g_local), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) { // Local
                if (v_BR == 1) {BkDbUtility.copyFile(STATICS_ROOT, DB_INDICATORS_NAME, STATICS_ROOT_BK); dialogAlert(3);}
                if (v_BR == 2) {
                    Intent intent1 = new Intent(MainActivity.this, FileListBKLocal.class);
                    startActivity(intent1); dialogAlert(4);
                }
            }
        });
        dialogo1.setNeutralButton(getResources().getString(R.string.str_g_web), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) { // WEB
                if (v_BR == 1) {BKDBWebUtility.doFileUpload(_EMISCODE); dialogAlert(5);}
                if (v_BR == 2) {
//                    Intent intent1 = new Intent(MainActivity.this, FileListBKLocal.class);
//                    startActivity(intent1); dialogAlert(4);
                    Intent intent1 = new Intent(MainActivity.this, FileListBKWEB.class);
                    intent1.putExtra("emis", _EMISCODE);
                    startActivity(intent1);
                }
            }
        });
        dialogo1.setPositiveButton(getResources().getString(R.string.str_g_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) { // Cancel
                //aceptar();
            }
        });
        dialogo1.show();
    }

    public void dialogAlert3(int v){
        final int v_BR = v;
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle(getResources().getString(R.string.str_bl_msj1)); // Importante
        if (v == 1){dialogo1.setMessage(getResources().getString(R.string.str_g_msj8));} // You are about to make a backup ...
        if (v == 2) {dialogo1.setMessage(getResources().getString(R.string.str_g_msj9));} // You are about to make a restore ...
        if (v == 3) {dialogo1.setMessage(getResources().getString(R.string.str_g_msj11));} // Initialize database ...
        if (v == 4) {dialogo1.setMessage(getResources().getString(R.string.str_g_msj13));} // Automatic promotion ...
        dialogo1.setCancelable(true);
        dialogo1.setNegativeButton(getResources().getString(R.string.str_g_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) { // Local
                cancelar();
            }
        });
        dialogo1.setPositiveButton(getResources().getString(R.string.str_g_confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) { // Cancel
                if (v_BR == 3) {initializeDatabase();}
                if (v_BR == 4) {aut_promotion();}
            }
        });
        dialogo1.show();
    }
    // *********** END Control Alerts ************************

    /**
     * Muestra una ventana de dialogo para elegir el nuevo idioma de la aplicacion
     * Cuando se hace clic en uno de los idiomas, se cambia el idioma de la aplicacion
     * y se recarga la actividad para ver los cambios
     * */
    public void showDialog(){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        //b.setTitle(getResources().getString(R.string.str_button));
        //obtiene los idiomas del array de strings.xml
        String[] types = getResources().getStringArray(R.array.languages);
        b.setItems(types, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                //showLanguage(which);
                switch (which) {
                    case 0:
                        locale = new Locale("en");
                        config.locale = locale;
                        saveLang("en");
                        break;
                    case 1:
                        locale = new Locale("sw");
                        config.locale = locale;
                        saveLang("sw");
                        break;
                    case 2:
                        locale = new Locale("es");
                        config.locale = locale;
                        saveLang("es");
                        break;
//                    case 2:
//                        locale = new Locale("pt");
//                        config.locale = locale;
//                        break;
                }
                getResources().updateConfiguration(config, null);
                Intent refresh = new Intent(MainActivity.this, MainActivity.class);
                startActivity(refresh);
                finish();
            }

        });

        b.show();
    }

//    public void showLanguage(int which){
//        switch (which) {
//            case 0:
//                locale = new Locale("en");
//                config.locale = locale;
//                saveLang("en");
//                break;
//            case 1:
//                locale = new Locale("sw");
//                config.locale = locale;
//                saveLang("sw");
//                break;
//            case 2:
//                locale = new Locale("es");
//                config.locale = locale;
//                saveLang("es");
//                break;
//        }
//        getResources().updateConfiguration(config, null);
//        Intent refresh = new Intent(MainActivity.this, MainActivity.class);
//        startActivity(refresh);
//        finish();
//    }

    public void showLanguage(){
        // ***************** languageToLoad = getLang();
        //Toast.makeText(this, "Lenguaje -> " + languageToLoad, Toast.LENGTH_SHORT).show();
        //String languageToLoad  = "en"; // your language
        Locale locale = new Locale(getLang());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        //this.setContentView(R.layout.activity_main);
    }


    private void saveLang(String lang){
        Conexion cnSET = new Conexion(this, STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        dbSET.execSQL("UPDATE lang SET lang='" + lang + "'");
    }


    private void initializeDatabase(){
        Conexion cnSET = new Conexion(this, STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        dbSET.execSQL("UPDATE lang SET flag='2'"); // se usa flag = 1 para inicializar datos en la capacitación y flag=2 para iniciar el proceso de recolección verdadero
        dbSET.execSQL("DELETE FROM a");
        dbSET.execSQL("DELETE FROM d");
        dbSET.execSQL("DELETE FROM f");
        dbSET.execSQL("DELETE FROM g");
        dbSET.execSQL("DELETE FROM i");
        dbSET.execSQL("DELETE FROM if1");
        dbSET.execSQL("DELETE FROM if2");
        dbSET.execSQL("DELETE FROM if3");
        dbSET.execSQL("DELETE FROM if4");
        dbSET.execSQL("DELETE FROM if5");
        dbSET.execSQL("DELETE FROM if6");
        dbSET.execSQL("DELETE FROM ii");
        dbSET.execSQL("DELETE FROM j");
        dbSET.execSQL("DELETE FROM q");
        dbSET.execSQL("DELETE FROM r");
        dbSET.execSQL("DELETE FROM s");
        dbSET.execSQL("DELETE FROM login");
        dbSET.execSQL("DELETE FROM ms_0");
        dbSET.execSQL("DELETE FROM sisupdate");
        dbSET.execSQL("DELETE FROM student");
        dbSET.execSQL("DELETE FROM _sa");
        dbSET.execSQL("DELETE FROM teacher");
        dbSET.execSQL("DELETE FROM _ta");
        dbSET.execSQL("DELETE FROM attendance");
        dbSET.execSQL("DELETE FROM behaviour");
        dbSET.execSQL("DELETE FROM evaluation");
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public String getFlagDatabase(){
        String getflagbdd="";
        Conexion cnSET = new Conexion(this, STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        Cursor cur_data = dbSET.rawQuery("SELECT flag FROM lang", null);
        cur_data.moveToFirst();
        getflagbdd = cur_data.getString(0);
        return getflagbdd;
    }


    public void dialogAlert_update_geo(int v){
        //Toast.makeText(getApplicationContext(),String.valueOf(v) ,Toast.LENGTH_SHORT).show();
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Important");
        if (v == 1){dialogo1.setMessage("Save and Exit !!!");}
        if (v == 2){dialogo1.setMessage("Are you sure to quit?");}
        if (v == 3){dialogo1.setMessage("Are you sure to delete record?");}
        if (v == 6){dialogo1.setMessage("Are you sure you want to send?");}
        if (v == 7){dialogo1.setMessage(getResources().getString(R.string.synchronizing));}

        dialogo1.setCancelable(false);
//        dialogo1.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialogo1, int id) {
//                cancelar();
//            }
//        });
        dialogo1.setNegativeButton(getResources().getString(R.string.str_g_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                aceptar_update_geo();
            }
        });
        dialogo1.show();
    }
    public void aceptar_update_geo() {
        //sendEnrollment();
        progressBar_update_geo();
    }

    public void progressBar_update_geo(){
        progress=new ProgressDialog(this);
        progress.setMessage("Synchronizing ...");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(false);
        progress.setProgress(0);
        progress.show();

        final int totalProgressTime = 100;

        final Thread t = new Thread() {
            @Override
            public void run() {
                int jumpTime = 0;
                insert_new_geo_code();
                while(jumpTime < totalProgressTime) {
                    try {
                        sleep(200);
                        jumpTime += 20;
                        progress.setProgress(jumpTime);
                    }
                    catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (jumpTime==100){
                        progress.dismiss();
                    }
                }
            }
        };
        t.start();

    }

    public void new_set_geo_codes() {
        Conexion cnSET = new Conexion(this, STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        Cursor cur_data = dbSET.rawQuery("SELECT COUNT(*) FROM set_geo_codes", null);
        cur_data.moveToFirst();
        if (!cur_data.getString(0).equals("1118")) {
            dialogAlert_update_geo(7);
        }
        dbSET.execSQL("UPDATE set_geo_codes SET g2=2 WHERE name2=\"KASULU DC\"");
        dbSET.execSQL("UPDATE set_geo_codes SET g2=5 WHERE name2=\"UVINZA\"");
        cnSET.close();
        dbSET.close();
        cur_data.close();
    }


    public  void insert_new_geo_code() {
        Conexion cnSET = new Conexion(this, STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        dbSET.execSQL("DELETE FROM set_geo_codes");
        //dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(0,0,0,'REGION','DISTRICT','WARD')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,6,31,'DODOMA','BAHI','BABAYU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,6,71,'DODOMA','BAHI','BAHI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,6,141,'DODOMA','BAHI','CHALI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,6,181,'DODOMA','BAHI','CHIBELELA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,6,61,'DODOMA','BAHI','CHIFUTUKA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,6,121,'DODOMA','BAHI','CHIKOLA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,6,131,'DODOMA','BAHI','CHIPANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,6,91,'DODOMA','BAHI','IBIHWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,6,171,'DODOMA','BAHI','IBUGULE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,6,101,'DODOMA','BAHI','ILINDI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,6,111,'DODOMA','BAHI','KIGWE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,6,21,'DODOMA','BAHI','LAMAITI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,6,11,'DODOMA','BAHI','MAKANDA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,6,161,'DODOMA','BAHI','MPALANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,6,81,'DODOMA','BAHI','MPAMANTWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,6,71,'DODOMA','BAHI','MPINGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,6,51,'DODOMA','BAHI','MSISI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,6,201,'DODOMA','BAHI','MTITAA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,6,61,'DODOMA','BAHI','MUNDEMU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,6,191,'DODOMA','BAHI','MWITIKIRA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,6,151,'DODOMA','BAHI','NONDWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,6,41,'DODOMA','BAHI','ZANKA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,83,'DODOMA','CHAMWINO','BUIGIRI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,32,'DODOMA','CHAMWINO','CHAMWINO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,301,'DODOMA','CHAMWINO','CHIBOLI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,71,'DODOMA','CHAMWINO','CHILONWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,261,'DODOMA','CHAMWINO','CHINUGULU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,41,'DODOMA','CHAMWINO','DABALO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,221,'DODOMA','CHAMWINO','FUFU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,161,'DODOMA','CHAMWINO','HANDALI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,11,'DODOMA','CHAMWINO','HANETI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,281,'DODOMA','CHAMWINO','HUZI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,181,'DODOMA','CHAMWINO','IDIFU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,131,'DODOMA','CHAMWINO','IGANDU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,131,'DODOMA','CHAMWINO','IKOMBOLINGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,111,'DODOMA','CHAMWINO','IKOWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,171,'DODOMA','CHAMWINO','IRINGA MVUMI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,31,'DODOMA','CHAMWINO','ITISO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,291,'DODOMA','CHAMWINO','LOJE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,91,'DODOMA','CHAMWINO','MAJELEKO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,191,'DODOMA','CHAMWINO','MAKANG'' WA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,101,'DODOMA','CHAMWINO','MANCHALI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,271,'DODOMA','CHAMWINO','MANDA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,211,'DODOMA','CHAMWINO','MANZASE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,51,'DODOMA','CHAMWINO','MEMBE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,41,'DODOMA','CHAMWINO','MLOWA BARABARANI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,231,'DODOMA','CHAMWINO','MLOWA BWAWANI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,241,'DODOMA','CHAMWINO','MPWAYUNGU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,121,'DODOMA','CHAMWINO','MSAMALO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,61,'DODOMA','CHAMWINO','MSANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,41,'DODOMA','CHAMWINO','MUUNGANO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,151,'DODOMA','CHAMWINO','MVUMI MAKULU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,151,'DODOMA','CHAMWINO','MVUMI MISHENI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,101,'DODOMA','CHAMWINO','NGHAHELEZI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,251,'DODOMA','CHAMWINO','NGHAMBAKU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,311,'DODOMA','CHAMWINO','NHINHI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,21,'DODOMA','CHAMWINO','SEGALA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,4,321,'DODOMA','CHAMWINO','ZAJILWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,7,31,'DODOMA','CHEMBA','BABAYU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,7,111,'DODOMA','CHEMBA','CHANDAMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,7,131,'DODOMA','CHEMBA','CHEMBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,7,51,'DODOMA','CHEMBA','CHURUKU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,7,81,'DODOMA','CHEMBA','DALAI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,7,161,'DODOMA','CHEMBA','FARKWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,7,121,'DODOMA','CHEMBA','GOIMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,7,151,'DODOMA','CHEMBA','GWANDI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,7,91,'DODOMA','CHEMBA','JANGALO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,7,191,'DODOMA','CHEMBA','KIDOKA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,7,41,'DODOMA','CHEMBA','KIMAHA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,7,121,'DODOMA','CHEMBA','KINYAMSINDO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,7,191,'DODOMA','CHEMBA','KWAMTORO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,7,111,'DODOMA','CHEMBA','LAHODA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,7,201,'DODOMA','CHEMBA','LALTA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,7,11,'DODOMA','CHEMBA','MAKORONGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,7,51,'DODOMA','CHEMBA','MONDO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,7,171,'DODOMA','CHEMBA','MPENDO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,7,103,'DODOMA','CHEMBA','MRIJO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,7,31,'DODOMA','CHEMBA','MSAADA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,7,21,'DODOMA','CHEMBA','OVADA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,7,141,'DODOMA','CHEMBA','PARANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,7,181,'DODOMA','CHEMBA','SANZAWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,7,51,'DODOMA','CHEMBA','SONGOLO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,7,33,'DODOMA','CHEMBA','SOYA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,7,51,'DODOMA','CHEMBA','TUMBAKOSE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,5,32,'DODOMA','DODOMA URBAN','CHAMWINO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,5,91,'DODOMA','DODOMA URBAN','CHIHANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,5,41,'DODOMA','DODOMA URBAN','DODOMA-MAKULU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,5,252,'DODOMA','DODOMA URBAN','HAZINA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,5,101,'DODOMA','DODOMA URBAN','HOMBOLO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,5,111,'DODOMA','DODOMA URBAN','IPALA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,5,151,'DODOMA','DODOMA URBAN','KIKOMBO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,5,111,'DODOMA','DODOMA URBAN','KIKUYU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,5,42,'DODOMA','DODOMA URBAN','KIWANJA CHA NDEGE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,5,282,'DODOMA','DODOMA URBAN','KIZOTA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,5,52,'DODOMA','DODOMA URBAN','MAKOLE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,5,81,'DODOMA','DODOMA URBAN','MAKUTUPORA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,5,231,'DODOMA','DODOMA URBAN','MBABALA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,5,301,'DODOMA','DODOMA URBAN','MBALAWALA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,5,62,'DODOMA','DODOMA URBAN','MIYUJI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,5,221,'DODOMA','DODOMA URBAN','MKONZE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,5,171,'DODOMA','DODOMA URBAN','MPUNGUZI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,5,71,'DODOMA','DODOMA URBAN','MSALATO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,5,141,'DODOMA','DODOMA URBAN','MTUMBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,5,291,'DODOMA','DODOMA URBAN','NALA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,5,161,'DODOMA','DODOMA URBAN','NG'' HONGHONHA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,5,121,'DODOMA','DODOMA URBAN','NZUGUNI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,5,52,'DODOMA','DODOMA URBAN','TAMBUKARELI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,5,22,'DODOMA','DODOMA URBAN','UHURU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,5,241,'DODOMA','DODOMA URBAN','ZUZU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,91,'DODOMA','KONDOA','BEREKO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,161,'DODOMA','KONDOA','BOLISA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,11,'DODOMA','KONDOA','BUMBUTA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,31,'DODOMA','KONDOA','BUSI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,251,'DODOMA','KONDOA','CHANGAA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,42,'DODOMA','KONDOA','CHEMCHEM')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,41,'DODOMA','KONDOA','HAUBI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,151,'DODOMA','KONDOA','HONDOMAIRO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,201,'DODOMA','KONDOA','ITASWI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,191,'DODOMA','KONDOA','ITOLOLO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,51,'DODOMA','KONDOA','KALAMBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,41,'DODOMA','KONDOA','KEIKEI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,81,'DODOMA','KONDOA','KIKILO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,111,'DODOMA','KONDOA','KIKORE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,192,'DODOMA','KONDOA','KILIMANI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,221,'DODOMA','KONDOA','KINGALE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,171,'DODOMA','KONDOA','KINYASI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,101,'DODOMA','KONDOA','KISESE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,241,'DODOMA','KONDOA','KOLO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,232,'DODOMA','KONDOA','KONDOA MJINI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,61,'DODOMA','KONDOA','KWADELO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,71,'DODOMA','KONDOA','MASANGE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,271,'DODOMA','KONDOA','MNENIA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,21,'DODOMA','KONDOA','PAHI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,181,'DODOMA','KONDOA','SALANKA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,121,'DODOMA','KONDOA','SERYA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,281,'DODOMA','KONDOA','SOERA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,211,'DODOMA','KONDOA','SURUKE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,261,'DODOMA','KONDOA','THAWI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,3,141,'DODOMA','KONGWA','CHAMKOROMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,3,161,'DODOMA','KONGWA','CHITEGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,3,201,'DODOMA','KONGWA','CHIWE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,3,31,'DODOMA','KONGWA','HOGORO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,3,101,'DODOMA','KONGWA','IDUO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,3,123,'DODOMA','KONGWA','KIBAIGWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,3,13,'DODOMA','KONGWA','KONGWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,3,211,'DODOMA','KONGWA','LENJULU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,3,151,'DODOMA','KONGWA','MAKAWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,3,171,'DODOMA','KONGWA','MATONGORO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,3,131,'DODOMA','KONGWA','MKOKA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,3,91,'DODOMA','KONGWA','MLALI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,3,71,'DODOMA','KONGWA','MTANANA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,3,221,'DODOMA','KONGWA','NGHUMBI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,3,181,'DODOMA','KONGWA','NGOMAI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,3,61,'DODOMA','KONGWA','NJOGE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,3,81,'DODOMA','KONGWA','PANDAMBILI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,3,111,'DODOMA','KONGWA','SAGARA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,3,21,'DODOMA','KONGWA','SEJELI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,3,51,'DODOMA','KONGWA','SONGAMBELE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,3,133,'DODOMA','KONGWA','UGOGONI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,3,41,'DODOMA','KONGWA','ZOISSA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,141,'DODOMA','MPWAPWA','BEREGE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,291,'DODOMA','MPWAPWA','CHIPOGORO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,221,'DODOMA','MPWAPWA','CHITEMO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,141,'DODOMA','MPWAPWA','CHUNYU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,271,'DODOMA','MPWAPWA','GALIGALI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,171,'DODOMA','MPWAPWA','GODEGODE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,201,'DODOMA','MPWAPWA','GULWE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,211,'DODOMA','MPWAPWA','IGOVU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,91,'DODOMA','MPWAPWA','IPERA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,231,'DODOMA','MPWAPWA','IWONDO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,53,'DODOMA','MPWAPWA','KIBAKWE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,41,'DODOMA','MPWAPWA','KIMAGAI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,241,'DODOMA','MPWAPWA','KINGITI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,251,'DODOMA','MPWAPWA','LUFU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,71,'DODOMA','MPWAPWA','LUHUNDWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,61,'DODOMA','MPWAPWA','LUMUMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,191,'DODOMA','MPWAPWA','LUPETA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,91,'DODOMA','MPWAPWA','LWIHOMELO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,132,'DODOMA','MPWAPWA','MALOLO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,191,'DODOMA','MPWAPWA','MANG'' ALIZA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,81,'DODOMA','MPWAPWA','MASSA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,31,'DODOMA','MPWAPWA','MATOMONDO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,11,'DODOMA','MPWAPWA','MAZAE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,161,'DODOMA','MPWAPWA','MBUGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,131,'DODOMA','MPWAPWA','MIMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,91,'DODOMA','MPWAPWA','MLEMBULE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,111,'DODOMA','MPWAPWA','MLUNDUZI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,183,'DODOMA','MPWAPWA','MPWAPWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,281,'DODOMA','MPWAPWA','MTERA DAM')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,211,'DODOMA','MPWAPWA','NGHAMBI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,261,'DODOMA','MPWAPWA','PWAGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,101,'DODOMA','MPWAPWA','RUDI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,91,'DODOMA','MPWAPWA','VINGHAWE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(1,2,121,'DODOMA','MPWAPWA','WOTTA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,6,21,'KIGOMA','BUHIGWE','BIHARU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,6,81,'KIGOMA','BUHIGWE','BUHIGWE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,6,211,'KIGOMA','BUHIGWE','BUKUBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,6,101,'KIGOMA','BUHIGWE','JANDA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,6,41,'KIGOMA','BUHIGWE','KAJANA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,6,91,'KIGOMA','BUHIGWE','KIBANDE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,6,111,'KIGOMA','BUHIGWE','KIBWIGWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,6,61,'KIGOMA','BUHIGWE','KILELEMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,6,201,'KIGOMA','BUHIGWE','KINAZI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,6,61,'KIGOMA','BUHIGWE','MKATANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,6,261,'KIGOMA','BUHIGWE','MUBANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,6,51,'KIGOMA','BUHIGWE','MUGERA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,6,131,'KIGOMA','BUHIGWE','MUHINDA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,6,141,'KIGOMA','BUHIGWE','MUNANILA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,6,71,'KIGOMA','BUHIGWE','MUNYEGERA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,6,111,'KIGOMA','BUHIGWE','MUNZEZE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,6,31,'KIGOMA','BUHIGWE','MUYAMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,6,151,'KIGOMA','BUHIGWE','MWAYAYA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,6,11,'KIGOMA','BUHIGWE','NYAMUGALI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,6,121,'KIGOMA','BUHIGWE','RUSABA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,7,151,'KIGOMA','KAKONKO','GWANUMPU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,7,151,'KIGOMA','KAKONKO','GWARAMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,7,53,'KIGOMA','KAKONKO','KAKONKO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,7,12,'KIGOMA','KAKONKO','KANYONZA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,7,81,'KIGOMA','KAKONKO','KASANDA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,7,41,'KIGOMA','KAKONKO','KASUNGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,7,101,'KIGOMA','KAKONKO','KATANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,7,61,'KIGOMA','KAKONKO','KIZIGUZIGU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,7,111,'KIGOMA','KAKONKO','MUGUNZU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,7,31,'KIGOMA','KAKONKO','MUHANGE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,7,11,'KIGOMA','KAKONKO','NYABIBUYE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,7,21,'KIGOMA','KAKONKO','NYAMTUKUZA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,7,71,'KIGOMA','KAKONKO','RUGENGE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,2,121,'KIGOMA','KASULU DC','ASANTE NYERERE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,2,31,'KIGOMA','KASULU DC','BUGAGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,2,21,'KIGOMA','KASULU DC','BUHORO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,2,252,'KIGOMA','KASULU DC','HERU SHINGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,2,62,'KIGOMA','KASULU DC','KAGERANKANDA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,2,51,'KIGOMA','KASULU DC','KALELA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,2,171,'KIGOMA','KASULU DC','KIGEMBE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,2,181,'KIGOMA','KASULU DC','KITAGATA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,2,11,'KIGOMA','KASULU DC','KITANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,2,101,'KIGOMA','KASULU DC','KURUGONGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,2,61,'KIGOMA','KASULU DC','KWAGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,2,151,'KIGOMA','KASULU DC','MAKERE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,2,151,'KIGOMA','KASULU DC','MUZYE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,2,51,'KIGOMA','KASULU DC','NYACHENDA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,2,81,'KIGOMA','KASULU DC','NYAKITONTO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,2,141,'KIGOMA','KASULU DC','NYAMIDAHO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,2,71,'KIGOMA','KASULU DC','NYAMNYUSI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,2,151,'KIGOMA','KASULU DC','NYENGE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,2,111,'KIGOMA','KASULU DC','RUNGWE MPYA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,2,121,'KIGOMA','KASULU DC','RUSESA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,2,141,'KIGOMA','KASULU DC','SHUNGULIBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,2,131,'KIGOMA','KASULU DC','TITYE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,8,252,'KIGOMA','KASULU TOWNSHIP AUTHORITY','HERU JUU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,8,91,'KIGOMA','KASULU TOWNSHIP AUTHORITY','KABANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,8,21,'KIGOMA','KASULU TOWNSHIP AUTHORITY','KIGONDO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,8,81,'KIGOMA','KASULU TOWNSHIP AUTHORITY','KUMSENGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,8,241,'KIGOMA','KASULU TOWNSHIP AUTHORITY','MARUMBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,8,31,'KIGOMA','KASULU TOWNSHIP AUTHORITY','MSAMBARA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,8,11,'KIGOMA','KASULU TOWNSHIP AUTHORITY','MUGANZA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,8,81,'KIGOMA','KASULU TOWNSHIP AUTHORITY','MURUBONA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,8,61,'KIGOMA','KASULU TOWNSHIP AUTHORITY','MURUFITI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,8,41,'KIGOMA','KASULU TOWNSHIP AUTHORITY','MURUSI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,8,71,'KIGOMA','KASULU TOWNSHIP AUTHORITY','NYANSHA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,8,51,'KIGOMA','KASULU TOWNSHIP AUTHORITY','NYUMBIGWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,8,61,'KIGOMA','KASULU TOWNSHIP AUTHORITY','RUHITA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,1,21,'KIGOMA','KIBONDO','BITARE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,1,21,'KIGOMA','KIBONDO','BITURANA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,1,111,'KIGOMA','KIBONDO','BUNYAMBO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,1,51,'KIGOMA','KIBONDO','BUSAGARA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,1,71,'KIGOMA','KIBONDO','BUSUNZU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,1,121,'KIGOMA','KIBONDO','ITABA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,1,62,'KIGOMA','KIBONDO','KAGEZI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,1,33,'KIGOMA','KIBONDO','KIBONDO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,1,131,'KIGOMA','KIBONDO','KITAHANA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,1,91,'KIGOMA','KIBONDO','KIZAZI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,1,81,'KIGOMA','KIBONDO','KUMSENGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,1,81,'KIGOMA','KIBONDO','KUMWAMBU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,1,103,'KIGOMA','KIBONDO','MABAMBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,1,11,'KIGOMA','KIBONDO','MISEZERO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,1,91,'KIGOMA','KIBONDO','MUKABUYE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,1,41,'KIGOMA','KIBONDO','MURUNGU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,1,111,'KIGOMA','KIBONDO','NYARUYOBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,1,61,'KIGOMA','KIBONDO','RUGONGWE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,1,132,'KIGOMA','KIBONDO','RUSOHOKO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,4,172,'KIGOMA','KIGOMA MUNICIPAL-UJIJI','BANGWE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,4,32,'KIGOMA','KIGOMA MUNICIPAL-UJIJI','BUHANDA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,4,42,'KIGOMA','KIGOMA MUNICIPAL-UJIJI','BUSINDE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,4,142,'KIGOMA','KIGOMA MUNICIPAL-UJIJI','BUZEBAZEBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,4,12,'KIGOMA','KIGOMA MUNICIPAL-UJIJI','GUNGU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,4,62,'KIGOMA','KIGOMA MUNICIPAL-UJIJI','KAGERA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,4,72,'KIGOMA','KIGOMA MUNICIPAL-UJIJI','KASIMBU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,4,72,'KIGOMA','KIGOMA MUNICIPAL-UJIJI','KASINGILIMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,4,192,'KIGOMA','KIGOMA MUNICIPAL-UJIJI','KATUBUKA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,4,22,'KIGOMA','KIGOMA MUNICIPAL-UJIJI','KIBIRIZI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,4,162,'KIGOMA','KIGOMA MUNICIPAL-UJIJI','KIGOMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,4,122,'KIGOMA','KIGOMA MUNICIPAL-UJIJI','KIPAMPA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,4,112,'KIGOMA','KIGOMA MUNICIPAL-UJIJI','KITONGONI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,4,52,'KIGOMA','KIGOMA MUNICIPAL-UJIJI','MACHINJIONI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,4,163,'KIGOMA','KIGOMA MUNICIPAL-UJIJI','MAJENGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,4,182,'KIGOMA','KIGOMA MUNICIPAL-UJIJI','MWANGA KASKAZINI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,4,152,'KIGOMA','KIGOMA MUNICIPAL-UJIJI','MWANGA KUSINI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,4,82,'KIGOMA','KIGOMA MUNICIPAL-UJIJI','RUBUGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,4,132,'KIGOMA','KIGOMA MUNICIPAL-UJIJI','RUSIMBI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,3,51,'KIGOMA','KIGOMA RURAL','BITALE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,3,101,'KIGOMA','KIGOMA RURAL','KAGONGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,3,11,'KIGOMA','KIGOMA RURAL','KAGUNGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,3,41,'KIGOMA','KIGOMA RURAL','KALINZI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,3,191,'KIGOMA','KIGOMA RURAL','KIDAHWE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,3,71,'KIGOMA','KIGOMA RURAL','MAHEMBE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,3,81,'KIGOMA','KIGOMA RURAL','MATENDO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,3,61,'KIGOMA','KIGOMA RURAL','MKONGORO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,3,12,'KIGOMA','KIGOMA RURAL','MUKIGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,3,91,'KIGOMA','KIGOMA RURAL','MUNGONYA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,3,31,'KIGOMA','KIGOMA RURAL','MWAMGONGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,3,113,'KIGOMA','KIGOMA RURAL','MWANDIGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,3,33,'KIGOMA','KIGOMA RURAL','NKUNGWE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,3,111,'KIGOMA','KIGOMA RURAL','NYARUBANDA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,3,181,'KIGOMA','KIGOMA RURAL','ZIWANI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,5,61,'KIGOMA','UVINZA','BASANZA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,5,21,'KIGOMA','UVINZA','BUHINGU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,5,252,'KIGOMA','UVINZA','HEREMBE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,5,31,'KIGOMA','UVINZA','IGALULA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,5,61,'KIGOMA','UVINZA','ILAGALA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,5,141,'KIGOMA','UVINZA','ITEBULA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,5,11,'KIGOMA','UVINZA','KALYA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,5,81,'KIGOMA','UVINZA','KANDAGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,5,91,'KIGOMA','UVINZA','KAZURAMIMBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,5,111,'KIGOMA','UVINZA','MGANZA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,5,121,'KIGOMA','UVINZA','MTEGO WA NOTI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,5,71,'KIGOMA','UVINZA','MWAKIZEGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,5,133,'KIGOMA','UVINZA','NGURUKA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,5,41,'KIGOMA','UVINZA','SIGUNGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,5,71,'KIGOMA','UVINZA','SIMBO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,5,51,'KIGOMA','UVINZA','SUNUKA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(16,5,103,'KIGOMA','UVINZA','UVINZA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,1,51,'LINDI','KILWA','CHUMO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,1,71,'LINDI','KILWA','KANDAWALE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,1,211,'LINDI','KILWA','KIBATA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,1,171,'LINDI','KILWA','KIKOLE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,1,41,'LINDI','KILWA','KINJUMBI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,1,61,'LINDI','KILWA','KIPATIMU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,1,91,'LINDI','KILWA','KIRANJERANJE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,1,71,'LINDI','KILWA','KIVINJE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,1,151,'LINDI','KILWA','LIHIMALYAO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,1,111,'LINDI','KILWA','LIKAWAGE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,1,121,'LINDI','KILWA','MANDAWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,1,202,'LINDI','KILWA','MASOKO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,1,101,'LINDI','KILWA','MIGURUWE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,1,31,'LINDI','KILWA','MINGUMBI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,1,21,'LINDI','KILWA','MITEJA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,1,91,'LINDI','KILWA','MITOLE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,1,251,'LINDI','KILWA','NAMAYUNI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,1,121,'LINDI','KILWA','NANJIRINJI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,1,81,'LINDI','KILWA','NJINJO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,1,161,'LINDI','KILWA','PANDE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,1,73,'LINDI','KILWA','SOMANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,1,191,'LINDI','KILWA','SONGOSONGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,1,11,'LINDI','KILWA','TINGI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,201,'LINDI','LINDI RURAL','CHIPONDA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,51,'LINDI','LINDI RURAL','KILANGALA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,41,'LINDI','LINDI RURAL','KILOLAMBWANI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,21,'LINDI','LINDI RURAL','KITOMANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,63,'LINDI','LINDI RURAL','KIWALALA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,251,'LINDI','LINDI RURAL','KIWAWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,221,'LINDI','LINDI RURAL','LONGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,163,'LINDI','LINDI RURAL','MAJENGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,181,'LINDI','LINDI RURAL','MANDWANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,271,'LINDI','LINDI RURAL','MATIMBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,31,'LINDI','LINDI RURAL','MCHINGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,241,'LINDI','LINDI RURAL','MILOLA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,11,'LINDI','LINDI RURAL','MIPINGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,191,'LINDI','LINDI RURAL','MNARA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,81,'LINDI','LINDI RURAL','MNOLELA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,113,'LINDI','LINDI RURAL','MTAMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,211,'LINDI','LINDI RURAL','MTUA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,261,'LINDI','LINDI RURAL','MTUMBYA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,151,'LINDI','LINDI RURAL','MVULENI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,101,'LINDI','LINDI RURAL','NACHUNYU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,161,'LINDI','LINDI RURAL','NAHUKAHUKA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,301,'LINDI','LINDI RURAL','NAMANGALE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,131,'LINDI','LINDI RURAL','NAMUPA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,281,'LINDI','LINDI RURAL','NANGARU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,71,'LINDI','LINDI RURAL','NAVANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,171,'LINDI','LINDI RURAL','NYANGAMARA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,123,'LINDI','LINDI RURAL','NYANGAO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,141,'LINDI','LINDI RURAL','NYENGEDI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,211,'LINDI','LINDI RURAL','PANGATENA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,231,'LINDI','LINDI RURAL','RUTAMBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,2,91,'LINDI','LINDI RURAL','SUDI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,6,172,'LINDI','LINDI URBAN','CHIKONJI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,6,112,'LINDI','LINDI URBAN','KITUMBIKWELA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,6,142,'LINDI','LINDI URBAN','MINGOYO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,6,191,'LINDI','LINDI URBAN','MNAZIMMOJA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,6,132,'LINDI','LINDI URBAN','MSINJAHILI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,6,152,'LINDI','LINDI URBAN','NG'' APA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,6,52,'LINDI','LINDI URBAN','RAHALEO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,6,102,'LINDI','LINDI URBAN','RASBURA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,6,162,'LINDI','LINDI URBAN','TANDANGONGORO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,6,82,'LINDI','LINDI URBAN','WAILES')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,4,61,'LINDI','LIWALE','BARIKIWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,4,101,'LINDI','LIWALE','KIANGARA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,4,111,'LINDI','LIWALE','KIBUTUKA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,4,191,'LINDI','LIWALE','KICHONDA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,4,91,'LINDI','LIWALE','KIMAMBI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,4,111,'LINDI','LIWALE','LIKONGOWELE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,4,201,'LINDI','LIWALE','LILOMBE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,4,151,'LINDI','LIWALE','LIWALE '' B'' ')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,4,13,'LINDI','LIWALE','LIWALE MJINI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,4,51,'LINDI','LIWALE','MAKATA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,4,161,'LINDI','LIWALE','MANGIRIKITI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,4,81,'LINDI','LIWALE','MBAYA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,4,21,'LINDI','LIWALE','MIHUMO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,4,141,'LINDI','LIWALE','MIRUI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,4,71,'LINDI','LIWALE','MKUTANO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,4,41,'LINDI','LIWALE','MLEMBWE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,4,131,'LINDI','LIWALE','MPIGAMITI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,4,61,'LINDI','LIWALE','NANGANDO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,4,121,'LINDI','LIWALE','NANGANO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,4,31,'LINDI','LIWALE','NGONGOWELE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,141,'LINDI','NACHINGWEA','CHIOLA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,121,'LINDI','NACHINGWEA','KIEGEI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,101,'LINDI','NACHINGWEA','KILIMA RONDO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,23,'LINDI','NACHINGWEA','KILIMANIHEWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,61,'LINDI','NACHINGWEA','KIPARA MNERO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,61,'LINDI','NACHINGWEA','KIPARA MTUA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,71,'LINDI','NACHINGWEA','LIONJA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,241,'LINDI','NACHINGWEA','MARAMBO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,231,'LINDI','NACHINGWEA','MATEKWE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,111,'LINDI','NACHINGWEA','MBONDO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,31,'LINDI','NACHINGWEA','MCHONDA ')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,41,'LINDI','NACHINGWEA','MINERO MIEMBENI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,221,'LINDI','NACHINGWEA','MINERONGONGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,91,'LINDI','NACHINGWEA','MITUMBATI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,131,'LINDI','NACHINGWEA','MKOKA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,171,'LINDI','NACHINGWEA','MKOTOKUYANA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,151,'LINDI','NACHINGWEA','MPIRUKA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,211,'LINDI','NACHINGWEA','MTUA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,163,'LINDI','NACHINGWEA','NACHINGWEA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,183,'LINDI','NACHINGWEA','NAIPANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,201,'LINDI','NACHINGWEA','NAIPINGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,51,'LINDI','NACHINGWEA','NAMAPWIA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,251,'LINDI','NACHINGWEA','NAMATULA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,12,'LINDI','NACHINGWEA','NAMBAMBO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,81,'LINDI','NACHINGWEA','NAMIKANGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,281,'LINDI','NACHINGWEA','NANGONDO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,161,'LINDI','NACHINGWEA','NANGOWE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,91,'LINDI','NACHINGWEA','NDITI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,261,'LINDI','NACHINGWEA','NDOMONI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,103,'LINDI','NACHINGWEA','NGUNICHILE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,31,'LINDI','NACHINGWEA','RUPONDA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,191,'LINDI','NACHINGWEA','STESHENI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,3,101,'LINDI','NACHINGWEA','UGAWAJI ')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,5,181,'LINDI','RUANGWA','CHIBULA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,5,61,'LINDI','RUANGWA','CHIENJELE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,5,211,'LINDI','RUANGWA','CHINONGWE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,5,141,'LINDI','RUANGWA','CHUNYU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,5,101,'LINDI','RUANGWA','LIKUNJA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,5,51,'LINDI','RUANGWA','LUCHELEGWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,5,91,'LINDI','RUANGWA','MAKANJIRO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,5,132,'LINDI','RUANGWA','MALOLO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,5,151,'LINDI','RUANGWA','MANDARAWE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,5,121,'LINDI','RUANGWA','MANDAWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,5,171,'LINDI','RUANGWA','MATAMBARALE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,5,23,'LINDI','RUANGWA','MBEKENYERA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,5,81,'LINDI','RUANGWA','MBWEMKULU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,5,111,'LINDI','RUANGWA','MNACHO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,5,163,'LINDI','RUANGWA','NACHINGWEA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,5,131,'LINDI','RUANGWA','NAMBILANJE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,5,71,'LINDI','RUANGWA','NAMICHIGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,5,191,'LINDI','RUANGWA','NANDAGALA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,5,201,'LINDI','RUANGWA','NANGANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,5,81,'LINDI','RUANGWA','NARUNG'' OMBE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,5,33,'LINDI','RUANGWA','NKOWE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(8,5,13,'LINDI','RUANGWA','RUANGWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,243,'MARA','BUNDA DC','BALILI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,93,'MARA','BUNDA DC','BUNDA MJINI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,253,'MARA','BUNDA DC','BUNDA STOO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,111,'MARA','BUNDA DC','BUTIMBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,221,'MARA','BUNDA DC','CHITENGULE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,101,'MARA','BUNDA DC','GUTA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,51,'MARA','BUNDA DC','HUNYARI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,161,'MARA','BUNDA DC','IGUNDU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,171,'MARA','BUNDA DC','IRAMBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,201,'MARA','BUNDA DC','KABASA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,231,'MARA','BUNDA DC','KASUGUTI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,281,'MARA','BUNDA DC','KETARE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,133,'MARA','BUNDA DC','KIBARA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,151,'MARA','BUNDA DC','KISORYA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,81,'MARA','BUNDA DC','KUNZUGU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,61,'MARA','BUNDA DC','MCHARO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,31,'MARA','BUNDA DC','MIHINGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,41,'MARA','BUNDA DC','MUGETA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,131,'MARA','BUNDA DC','NAMHULA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,211,'MARA','BUNDA DC','NAMPINDI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,141,'MARA','BUNDA DC','NANSIMO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,121,'MARA','BUNDA DC','NERUMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,271,'MARA','BUNDA DC','NYAMANG'' UTA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,141,'MARA','BUNDA DC','NYAMIHYOLO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,13,'MARA','BUNDA DC','NYAMUSWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,263,'MARA','BUNDA DC','NYASURA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,21,'MARA','BUNDA DC','SALAMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,71,'MARA','BUNDA DC','SAZIRA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,4,191,'MARA','BUNDA DC','WARIKU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,7,171,'MARA','BUTIAMA','BISUMWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,7,51,'MARA','BUTIAMA','BUHEMBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,7,181,'MARA','BUTIAMA','BUKABWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,7,121,'MARA','BUTIAMA','BURUMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,7,201,'MARA','BUTIAMA','BUSEGWE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,7,21,'MARA','BUTIAMA','BUSWAHILI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,7,83,'MARA','BUTIAMA','BUTIAMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,7,191,'MARA','BUTIAMA','BUTUGURI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,7,11,'MARA','BUTIAMA','BWIREGI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,7,81,'MARA','BUTIAMA','KAMUGEGI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,7,113,'MARA','BUTIAMA','KUKIRANGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,7,101,'MARA','BUTIAMA','KYANYARI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,7,91,'MARA','BUTIAMA','MASABA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,7,61,'MARA','BUTIAMA','MIRWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,7,71,'MARA','BUTIAMA','MURIAZA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,7,173,'MARA','BUTIAMA','NYAKANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,7,31,'MARA','BUTIAMA','NYAMIMANGE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,7,41,'MARA','BUTIAMA','SIRORISIMBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,23,'MARA','MUSOMA DC','BUGOJI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,71,'MARA','MUSOMA DC','BUGWEMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,51,'MARA','MUSOMA DC','BUKIMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,11,'MARA','MUSOMA DC','BUKUMI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,111,'MARA','MUSOMA DC','BULINGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,131,'MARA','MUSOMA DC','BUSAMBARA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,31,'MARA','MUSOMA DC','BWASI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,141,'MARA','MUSOMA DC','ETARO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,241,'MARA','MUSOMA DC','IFULIFU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,121,'MARA','MUSOMA DC','KIRIBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,21,'MARA','MUSOMA DC','MAKOJO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,143,'MARA','MUSOMA DC','MUGANGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,61,'MARA','MUSOMA DC','MURANGI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,41,'MARA','MUSOMA DC','MUSANJA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,131,'MARA','MUSOMA DC','NYAKATENDE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,91,'MARA','MUSOMA DC','NYAMBONO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,201,'MARA','MUSOMA DC','NYAMURANDIRIRA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,151,'MARA','MUSOMA DC','NYEGINA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,132,'MARA','MUSOMA DC','RUSOLI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,101,'MARA','MUSOMA DC','SUGUTI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,111,'MARA','MUSOMA DC','TEGERUKA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,122,'MARA','MUSOMA MC','BUHARE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,62,'MARA','MUSOMA MC','BWERI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,32,'MARA','MUSOMA MC','IRINGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,92,'MARA','MUSOMA MC','KAMUNYONGE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,82,'MARA','MUSOMA MC','KIGERA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,42,'MARA','MUSOMA MC','KITAJI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,191,'MARA','MUSOMA MC','KWANGWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,132,'MARA','MUSOMA MC','MAKOKO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,61,'MARA','MUSOMA MC','MSHIKAMANO ')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,12,'MARA','MUSOMA MC','MUKENDO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,22,'MARA','MUSOMA MC','MWIGOBERO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,112,'MARA','MUSOMA MC','MWISENGE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,72,'MARA','MUSOMA MC','NYAKATO ')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,102,'MARA','MUSOMA MC','NYAMATARE ')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,52,'MARA','MUSOMA MC','NYASHO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,231,'MARA','MUSOMA MC','RWAMLIMI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,6,172,'MARA','RORYA','BARAKI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,6,81,'MARA','RORYA','BUKURA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,6,151,'MARA','RORYA','BUKWE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,6,121,'MARA','RORYA','GORIBE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,6,131,'MARA','RORYA','IKOMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,6,11,'MARA','RORYA','KIGUNGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,6,171,'MARA','RORYA','KINYENCHE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,6,21,'MARA','RORYA','KIROGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,6,181,'MARA','RORYA','KISUMWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,6,101,'MARA','RORYA','KITEMBE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,6,191,'MARA','RORYA','KOMUGE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,6,143,'MARA','RORYA','KORYO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,6,171,'MARA','RORYA','KYANGASAGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,6,211,'MARA','RORYA','KYANG'' OMBE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,6,111,'MARA','RORYA','MIRARE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,6,63,'MARA','RORYA','MKOMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,6,201,'MARA','RORYA','NYABURONGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,6,51,'MARA','RORYA','NYAHONGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,6,41,'MARA','RORYA','NYAMAGARO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,6,31,'MARA','RORYA','NYAMTINGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,6,201,'MARA','RORYA','NYAMUNGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,6,161,'MARA','RORYA','NYATHOROGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,6,171,'MARA','RORYA','RABOUR')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,6,52,'MARA','RORYA','RARANYA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,6,91,'MARA','RORYA','ROCHE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,6,71,'MARA','RORYA','TAI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,21,'MARA','SERENGETI','BUSAWE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,261,'MARA','SERENGETI','GEITASAMO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,131,'MARA','SERENGETI','IKOMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,101,'MARA','SERENGETI','ISENYE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,91,'MARA','SERENGETI','KEBANCHA BANCHA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,11,'MARA','SERENGETI','KENYAMONTA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,31,'MARA','SERENGETI','KISAKA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,81,'MARA','SERENGETI','KISANGURA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,171,'MARA','SERENGETI','KYAMBAHI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,71,'MARA','SERENGETI','MACHOCHWE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,201,'MARA','SERENGETI','MAGANGE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,191,'MARA','SERENGETI','MAJIMOTO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,161,'MARA','SERENGETI','MANCHIRA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,171,'MARA','SERENGETI','MATARE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,241,'MARA','SERENGETI','MBALIBALI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,273,'MARA','SERENGETI','MOROTONGA      ')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,221,'MARA','SERENGETI','MOSONGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,92,'MARA','SERENGETI','MUGUMU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,101,'MARA','SERENGETI','NAGUSI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,111,'MARA','SERENGETI','NATTA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,102,'MARA','SERENGETI','NYAMATARE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,141,'MARA','SERENGETI','NYAMBURETI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,151,'MARA','SERENGETI','NYAMOKO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,211,'MARA','SERENGETI','NYANSURURA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,131,'MARA','SERENGETI','RIGICHA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,51,'MARA','SERENGETI','RING'' WANI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,61,'MARA','SERENGETI','RUNG'' ABURE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,231,'MARA','SERENGETI','SEDECO ')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,252,'MARA','SERENGETI','STENDI KUU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,2,281,'MARA','SERENGETI','UWANJA WA NDEGE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,141,'MARA','TARIME TC','BINAGI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,162,'MARA','TARIME TC','BOMANI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,11,'MARA','TARIME TC','BUMERA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,141,'MARA','TARIME TC','GANYANGE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,101,'MARA','TARIME TC','GORONG'' A')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,151,'MARA','TARIME TC','GWITIRYO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,301,'MARA','TARIME TC','ITIRYO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,121,'MARA','TARIME TC','KEMAMBO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,251,'MARA','TARIME TC','KENYAMANYORI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,281,'MARA','TARIME TC','KETARE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,131,'MARA','TARIME TC','KIBASUKA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,221,'MARA','TARIME TC','KIORE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,211,'MARA','TARIME TC','KOMASWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,191,'MARA','TARIME TC','KWIHANCHA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,191,'MARA','TARIME TC','MANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,201,'MARA','TARIME TC','MATONGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,271,'MARA','TARIME TC','MBOGI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,81,'MARA','TARIME TC','MURIBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,21,'MARA','TARIME TC','MWEMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,61,'MARA','TARIME TC','NKENDE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,51,'MARA','TARIME TC','NYAKONGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,242,'MARA','TARIME TC','NYAMISANGURA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,71,'MARA','TARIME TC','NYAMWAGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,171,'MARA','TARIME TC','NYANDOTO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,291,'MARA','TARIME TC','NYANSINCHA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,91,'MARA','TARIME TC','NYANUNGU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,61,'MARA','TARIME TC','NYARERO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,111,'MARA','TARIME TC','NYAROKOBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,41,'MARA','TARIME TC','PEMBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,102,'MARA','TARIME TC','REGICHERI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,232,'MARA','TARIME TC','SABASABA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,33,'MARA','TARIME TC','SIRARI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,11,'MARA','TARIME TC','SUSUNI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(20,1,153,'MARA','TARIME TC','TURWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,5,171,'SHINYANGA','KAHAMA TOWNSHIP AUTHORITY','BUSOKA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,5,41,'SHINYANGA','KAHAMA TOWNSHIP AUTHORITY','ISAGEHE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,5,191,'SHINYANGA','KAHAMA TOWNSHIP AUTHORITY','IYENZE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,5,63,'SHINYANGA','KAHAMA TOWNSHIP AUTHORITY','KAGONGWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,5,152,'SHINYANGA','KAHAMA TOWNSHIP AUTHORITY','KAHAMA MJINI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,5,181,'SHINYANGA','KAHAMA TOWNSHIP AUTHORITY','KILAGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,5,31,'SHINYANGA','KAHAMA TOWNSHIP AUTHORITY','KINAGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,5,163,'SHINYANGA','KAHAMA TOWNSHIP AUTHORITY','MAJENGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,5,83,'SHINYANGA','KAHAMA TOWNSHIP AUTHORITY','MALUNGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,5,93,'SHINYANGA','KAHAMA TOWNSHIP AUTHORITY','MHONGOLO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,5,123,'SHINYANGA','KAHAMA TOWNSHIP AUTHORITY','MHUNGULA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,5,51,'SHINYANGA','KAHAMA TOWNSHIP AUTHORITY','MONDO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,5,103,'SHINYANGA','KAHAMA TOWNSHIP AUTHORITY','MWENDAKULIMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,5,11,'SHINYANGA','KAHAMA TOWNSHIP AUTHORITY','NGOGWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,5,73,'SHINYANGA','KAHAMA TOWNSHIP AUTHORITY','NYAHANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,5,201,'SHINYANGA','KAHAMA TOWNSHIP AUTHORITY','NYANDEKWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,5,143,'SHINYANGA','KAHAMA TOWNSHIP AUTHORITY','NYASUBI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,5,132,'SHINYANGA','KAHAMA TOWNSHIP AUTHORITY','NYIHOGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,5,21,'SHINYANGA','KAHAMA TOWNSHIP AUTHORITY','WENDELE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,5,111,'SHINYANGA','KAHAMA TOWNSHIP AUTHORITY','ZONGOMERA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,21,'SHINYANGA','KISHAPU','BUBIKI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,11,'SHINYANGA','KISHAPU','BUNAMBIYU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,81,'SHINYANGA','KISHAPU','BUSANGWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,181,'SHINYANGA','KISHAPU','IDUKILO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,201,'SHINYANGA','KISHAPU','ITILIMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,41,'SHINYANGA','KISHAPU','KILOLELI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,83,'SHINYANGA','KISHAPU','KISHAPU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,141,'SHINYANGA','KISHAPU','LAGANA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,201,'SHINYANGA','KISHAPU','MAGANZO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,131,'SHINYANGA','KISHAPU','MASANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,51,'SHINYANGA','KISHAPU','MONDO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,261,'SHINYANGA','KISHAPU','MWADUI LOHUMBO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,91,'SHINYANGA','KISHAPU','MWAKIPOYA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,121,'SHINYANGA','KISHAPU','MWAMALASA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,151,'SHINYANGA','KISHAPU','MWAMASHELE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,51,'SHINYANGA','KISHAPU','MWATAGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,101,'SHINYANGA','KISHAPU','MWAWEJA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,161,'SHINYANGA','KISHAPU','NGOFILA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,41,'SHINYANGA','KISHAPU','SEKE BUGORO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,101,'SHINYANGA','KISHAPU','SHAGIHILU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,111,'SHINYANGA','KISHAPU','SOMAGEDI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,33,'SHINYANGA','KISHAPU','SONGWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,191,'SHINYANGA','KISHAPU','TALAGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,71,'SHINYANGA','KISHAPU','UCHUNGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,181,'SHINYANGA','KISHAPU','UKENYENGE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,23,'SHINYANGA','MSALALA','BUGARAMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,111,'SHINYANGA','MSALALA','BULIGE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,13,'SHINYANGA','MSALALA','BULYANHULU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,81,'SHINYANGA','MSALALA','BUSANGI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,71,'SHINYANGA','MSALALA','CHELA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,171,'SHINYANGA','MSALALA','IKINDA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,163,'SHINYANGA','MSALALA','ISAKA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,151,'SHINYANGA','MSALALA','JANA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,121,'SHINYANGA','MSALALA','KASHISHI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,31,'SHINYANGA','MSALALA','LUNGUYA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,61,'SHINYANGA','MSALALA','MEGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,41,'SHINYANGA','MSALALA','MWAKATA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,141,'SHINYANGA','MSALALA','MWALUGULU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,131,'SHINYANGA','MSALALA','MWANASE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,101,'SHINYANGA','MSALALA','NGAYA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,91,'SHINYANGA','MSALALA','NTOBO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,53,'SHINYANGA','MSALALA','SEGESE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,41,'SHINYANGA','MSALALA','SHILELA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,62,'SHINYANGA','SHINYANGA MC','CHAMAGUHA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,133,'SHINYANGA','SHINYANGA MC','CHIBE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,43,'SHINYANGA','SHINYANGA MC','IBADAKULI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,72,'SHINYANGA','SHINYANGA MC','IBINZAMATA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,122,'SHINYANGA','SHINYANGA MC','KAMBARAGE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,82,'SHINYANGA','SHINYANGA MC','KITANGILI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,91,'SHINYANGA','SHINYANGA MC','KIZUMBI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,21,'SHINYANGA','SHINYANGA MC','KOLANDOTO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,142,'SHINYANGA','SHINYANGA MC','LUBAGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,162,'SHINYANGA','SHINYANGA MC','MASEKELO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,61,'SHINYANGA','SHINYANGA MC','MJINI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,11,'SHINYANGA','SHINYANGA MC','MWAMALILI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,101,'SHINYANGA','SHINYANGA MC','MWAWAZA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,112,'SHINYANGA','SHINYANGA MC','NDALA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,152,'SHINYANGA','SHINYANGA MC','NDEMBEZI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,32,'SHINYANGA','SHINYANGA MC','NGOKOLO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,2,173,'SHINYANGA','SHINYANGA MC','OLD SHINYANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,3,201,'SHINYANGA','SHINYANGA RURAL','BUKENE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,3,41,'SHINYANGA','SHINYANGA RURAL','DIDIA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,3,31,'SHINYANGA','SHINYANGA RURAL','ILOLA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,3,11,'SHINYANGA','SHINYANGA RURAL','IMESELA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,3,101,'SHINYANGA','SHINYANGA RURAL','ISELAMAGAZI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,3,51,'SHINYANGA','SHINYANGA RURAL','ITWANGI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,3,111,'SHINYANGA','SHINYANGA RURAL','LYABUKANDE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,3,231,'SHINYANGA','SHINYANGA RURAL','LYABUSALU'' A'' ')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,3,231,'SHINYANGA','SHINYANGA RURAL','LYABUSALU'' B'' ')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,3,261,'SHINYANGA','SHINYANGA RURAL','LYAMIDATI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,3,211,'SHINYANGA','SHINYANGA RURAL','MASENGWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,3,231,'SHINYANGA','SHINYANGA RURAL','MWAKITOLYO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,3,241,'SHINYANGA','SHINYANGA RURAL','MWALUKWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,3,141,'SHINYANGA','SHINYANGA RURAL','MWAMALA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,3,121,'SHINYANGA','SHINYANGA RURAL','MWANTINI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,3,221,'SHINYANGA','SHINYANGA RURAL','MWENGE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,3,191,'SHINYANGA','SHINYANGA RURAL','NSALALA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,3,251,'SHINYANGA','SHINYANGA RURAL','NYAMALOGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,3,181,'SHINYANGA','SHINYANGA RURAL','NYIDA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,3,131,'SHINYANGA','SHINYANGA RURAL','PANDAGICHIZA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,3,171,'SHINYANGA','SHINYANGA RURAL','PUNI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,3,81,'SHINYANGA','SHINYANGA RURAL','SALAWE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,3,151,'SHINYANGA','SHINYANGA RURAL','SAMUYE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,3,91,'SHINYANGA','SHINYANGA RURAL','SOLWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,3,63,'SHINYANGA','SHINYANGA RURAL','TINDE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,3,161,'SHINYANGA','SHINYANGA RURAL','USANDA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,3,21,'SHINYANGA','SHINYANGA RURAL','USULE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,4,211,'SHINYANGA','USHETU','BUKOMELA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,4,291,'SHINYANGA','USHETU','BULUNGWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,4,181,'SHINYANGA','USHETU','CHAMBO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,4,171,'SHINYANGA','USHETU','CHONA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,4,281,'SHINYANGA','USHETU','IDAHINA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,4,231,'SHINYANGA','USHETU','IGUNDA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,4,251,'SHINYANGA','USHETU','IGWAMANONI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,4,241,'SHINYANGA','USHETU','KINAMAPULA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,4,191,'SHINYANGA','USHETU','KISUKE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,4,201,'SHINYANGA','USHETU','MAPAMBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,4,261,'SHINYANGA','USHETU','MPUNZE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,4,141,'SHINYANGA','USHETU','NYAMILANGANO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,4,301,'SHINYANGA','USHETU','NYANKENDE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,4,271,'SHINYANGA','USHETU','SABASABINI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,4,11,'SHINYANGA','USHETU','UBAGWE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,4,221,'SHINYANGA','USHETU','UKUNE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,4,311,'SHINYANGA','USHETU','ULEWE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,4,343,'SHINYANGA','USHETU','ULOWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,4,321,'SHINYANGA','USHETU','USHETU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(17,4,331,'SHINYANGA','USHETU','UYOGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,243,'SIMIYU','BARIADI DC','BANEMHI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,183,'SIMIYU','BARIADI DC','DUTWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,141,'SIMIYU','BARIADI DC','GAMBOSI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,281,'SIMIYU','BARIADI DC','GIBISHI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,211,'SIMIYU','BARIADI DC','GILYA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,101,'SIMIYU','BARIADI DC','IHUSI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,161,'SIMIYU','BARIADI DC','IKUNGULYABASHASHI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,161,'SIMIYU','BARIADI DC','ITUBUKILO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,131,'SIMIYU','BARIADI DC','KASOLI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,121,'SIMIYU','BARIADI DC','KILALO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,211,'SIMIYU','BARIADI DC','MASEWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,201,'SIMIYU','BARIADI DC','MATONGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,111,'SIMIYU','BARIADI DC','MWADOBANA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,171,'SIMIYU','BARIADI DC','MWASUBUYA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,21,'SIMIYU','BARIADI DC','MWAUBINGI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,41,'SIMIYU','BARIADI DC','MWAUMATONDO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,103,'SIMIYU','BARIADI DC','NGULYATI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,11,'SIMIYU','BARIADI DC','NKINDWABIYE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,33,'SIMIYU','BARIADI DC','NKOLOLO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,91,'SIMIYU','BARIADI DC','SAKWE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,191,'SIMIYU','BARIADI DC','SAPIWI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,172,'SIMIYU','BARIADI TC','BARADI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,233,'SIMIYU','BARIADI TC','BARIADI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,81,'SIMIYU','BARIADI TC','BUNAMHALA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,31,'SIMIYU','BARIADI TC','GUDUW')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,151,'SIMIYU','BARIADI TC','GUDUWI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,141,'SIMIYU','BARIADI TC','ISANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,243,'SIMIYU','BARIADI TC','MALAMBO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,111,'SIMIYU','BARIADI TC','MHANGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,173,'SIMIYU','BARIADI TC','NYAKABINDI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,61,'SIMIYU','BARIADI TC','NYANGOKOLWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,151,'SIMIYU','BARIADI TC','SANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,253,'SIMIYU','BARIADI TC','SIMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,1,73,'SIMIYU','BARIADI TC','SOMANDA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,5,21,'SIMIYU','BUSEGA','BADUGU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,5,131,'SIMIYU','BUSEGA','IGALUKILO '' A'' ')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,5,131,'SIMIYU','BUSEGA','IGALUKILO '' B'' ')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,5,131,'SIMIYU','BUSEGA','IGALUKILO''  B'' ')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,5,141,'SIMIYU','BUSEGA','IMALAMATE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,5,63,'SIMIYU','BUSEGA','KABITA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,5,73,'SIMIYU','BUSEGA','KALEMELA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,5,41,'SIMIYU','BUSEGA','KILOLELI '' A'' ')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,5,41,'SIMIYU','BUSEGA','KILOLELI '' B'' ')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,5,83,'SIMIYU','BUSEGA','LAMADI '' A'' ')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,5,83,'SIMIYU','BUSEGA','LAMADI '' B'' ')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,5,91,'SIMIYU','BUSEGA','LUTUBIGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,5,121,'SIMIYU','BUSEGA','MALILI '' A'' ')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,5,121,'SIMIYU','BUSEGA','MALILI '' B'' ')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,5,101,'SIMIYU','BUSEGA','MKULA '' A'' ')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,5,101,'SIMIYU','BUSEGA','MKULA '' B'' ')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,5,51,'SIMIYU','BUSEGA','MWAMANYILI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,5,111,'SIMIYU','BUSEGA','NGASAMO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,5,31,'SIMIYU','BUSEGA','NYALUHANDE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,5,111,'SIMIYU','BUSEGA','NYASHIMO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,5,11,'SIMIYU','BUSEGA','SHIGALA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,2,121,'SIMIYU','ITILIMA','BUDALABUJIGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,2,11,'SIMIYU','ITILIMA','BUMERA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,2,91,'SIMIYU','ITILIMA','CHINAMILI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,2,171,'SIMIYU','ITILIMA','IKINDILO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,2,31,'SIMIYU','ITILIMA','KINAG'' WELI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,2,111,'SIMIYU','ITILIMA','LAGANGABILILI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,2,173,'SIMIYU','ITILIMA','LUGURU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,2,211,'SIMIYU','ITILIMA','MBITA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,2,71,'SIMIYU','ITILIMA','MHUNZE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,2,81,'SIMIYU','ITILIMA','MIGATO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,2,153,'SIMIYU','ITILIMA','MWAMAPALALA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,2,31,'SIMIYU','ITILIMA','MWAMTANI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,2,51,'SIMIYU','ITILIMA','MWASWALE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,2,91,'SIMIYU','ITILIMA','NDOLELEJI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,2,71,'SIMIYU','ITILIMA','NGWALUSHU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,2,181,'SIMIYU','ITILIMA','NHOBORA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,2,21,'SIMIYU','ITILIMA','NKOMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,2,61,'SIMIYU','ITILIMA','NKUYU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,2,161,'SIMIYU','ITILIMA','NYAMALAPA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,2,41,'SIMIYU','ITILIMA','SAGATA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,2,221,'SIMIYU','ITILIMA','SAWIDA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,2,191,'SIMIYU','ITILIMA','ZAGAYU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,241,'SIMIYU','MASWA','BADI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,263,'SIMIYU','MASWA','BINZA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,171,'SIMIYU','MASWA','BUCHAMBI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,101,'SIMIYU','MASWA','BUDEKWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,23,'SIMIYU','MASWA','BUGARAMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,81,'SIMIYU','MASWA','BUSANGI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,111,'SIMIYU','MASWA','BUSILILI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,81,'SIMIYU','MASWA','DAKAMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,31,'SIMIYU','MASWA','IPILILO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,141,'SIMIYU','MASWA','ISANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,91,'SIMIYU','MASWA','JIJA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,181,'SIMIYU','MASWA','KADOTO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,221,'SIMIYU','MASWA','KULIMI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,93,'SIMIYU','MASWA','LALAGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,233,'SIMIYU','MASWA','MALAMPAKA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,131,'SIMIYU','MASWA','MASELA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,81,'SIMIYU','MASWA','MATABA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,182,'SIMIYU','MASWA','MBARAGANE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,71,'SIMIYU','MASWA','MPINDO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,173,'SIMIYU','MASWA','MWABARATURU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,173,'SIMIYU','MASWA','MWABAYANDA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,51,'SIMIYU','MASWA','MWAMANENGE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,161,'SIMIYU','MASWA','MWAMASHIMBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,152,'SIMIYU','MASWA','MWANGHONOLI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,21,'SIMIYU','MASWA','NGULIGULI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,11,'SIMIYU','MASWA','NG'' WIGWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,201,'SIMIYU','MASWA','NYABUBINZA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,253,'SIMIYU','MASWA','NYALIKUNGU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,151,'SIMIYU','MASWA','SANGAMWALUGESHA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,41,'SIMIYU','MASWA','SENANI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,121,'SIMIYU','MASWA','SENG'' WA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,101,'SIMIYU','MASWA','SHANWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,191,'SIMIYU','MASWA','SHISHIYU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,281,'SIMIYU','MASWA','SOLA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,61,'SIMIYU','MASWA','SUKUMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,4,151,'SIMIYU','MASWA','ZANZUI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,3,191,'SIMIYU','MEATU','BUKUNDI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,3,101,'SIMIYU','MEATU','IMALASEKO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,3,51,'SIMIYU','MEATU','ITINJE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,3,31,'SIMIYU','MEATU','KIMALI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,3,61,'SIMIYU','MEATU','KISESA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,3,81,'SIMIYU','MEATU','LINGEKA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,3,161,'SIMIYU','MEATU','LUBIGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,3,141,'SIMIYU','MEATU','MWABUMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,3,151,'SIMIYU','MEATU','MWABUSALU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,3,111,'SIMIYU','MEATU','MWABUZO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,3,231,'SIMIYU','MEATU','MWAKISANDU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,3,121,'SIMIYU','MEATU','MWAMALOLE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,3,211,'SIMIYU','MEATU','MWAMANIMBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,3,171,'SIMIYU','MEATU','MWAMANONGU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,3,41,'SIMIYU','MEATU','MWAMISHALI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,3,71,'SIMIYU','MEATU','MWANDOYA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,3,231,'SIMIYU','MEATU','MWANGUDO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,3,13,'SIMIYU','MEATU','MWANHUZI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,3,131,'SIMIYU','MEATU','MWANJOLO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,3,251,'SIMIYU','MEATU','MWANYAHINA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,3,201,'SIMIYU','MEATU','MWASENGELA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,3,181,'SIMIYU','MEATU','NGHOBOKO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,3,21,'SIMIYU','MEATU','NKOMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,3,91,'SIMIYU','MEATU','SAKASAKA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(24,3,221,'SIMIYU','MEATU','TINDABULIGI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,31,'TABORA','IGUNGA DC','BUKOKO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,181,'TABORA','IGUNGA DC','CHAMBUTWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,163,'TABORA','IGUNGA DC','CHOMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,101,'TABORA','IGUNGA DC','IBOROGERO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,211,'TABORA','IGUNGA DC','IGOWEKO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,13,'TABORA','IGUNGA DC','IGUNGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,103,'TABORA','IGUNGA DC','IGURUBI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,41,'TABORA','IGUNGA DC','ISAKAMALIWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,21,'TABORA','IGUNGA DC','ITUMBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,141,'TABORA','IGUNGA DC','ITUNDURU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,241,'TABORA','IGUNGA DC','KININGINILA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,121,'TABORA','IGUNGA DC','KINUNGU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,82,'TABORA','IGUNGA DC','KITANGILI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,251,'TABORA','IGUNGA DC','LUGUBU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,81,'TABORA','IGUNGA DC','MBUTU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,261,'TABORA','IGUNGA DC','MTUNGULU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,141,'TABORA','IGUNGA DC','MWAMAKONA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,141,'TABORA','IGUNGA DC','MWAMALA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,151,'TABORA','IGUNGA DC','MWAMASHIGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,161,'TABORA','IGUNGA DC','MWAMASHIMBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,171,'TABORA','IGUNGA DC','MWASHIKU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,241,'TABORA','IGUNGA DC','MWISI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,61,'TABORA','IGUNGA DC','NANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,152,'TABORA','IGUNGA DC','NDEMBEZI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,211,'TABORA','IGUNGA DC','NGULU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,71,'TABORA','IGUNGA DC','NGUVUMOJA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,201,'TABORA','IGUNGA DC','NKINGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,91,'TABORA','IGUNGA DC','NTOBO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,201,'TABORA','IGUNGA DC','NYANDEKWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,71,'TABORA','IGUNGA DC','SIMBO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,261,'TABORA','IGUNGA DC','SUNGWIZI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,191,'TABORA','IGUNGA DC','TAMBALALE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,81,'TABORA','IGUNGA DC','UGAKA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,171,'TABORA','IGUNGA DC','USWAYA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,2,181,'TABORA','IGUNGA DC','ZIBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,161,'TABORA','KALIUA DC','ICHEMBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,71,'TABORA','KALIUA DC','IGALALA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,211,'TABORA','KALIUA DC','IGOMBE MKULU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,101,'TABORA','KALIUA DC','IGWISI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,61,'TABORA','KALIUA DC','ILEGE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,101,'TABORA','KALIUA DC','IMALAUPINA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,53,'TABORA','KALIUA DC','KALIUA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,81,'TABORA','KALIUA DC','KAMSEKWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,191,'TABORA','KALIUA DC','KANINDO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,181,'TABORA','KALIUA DC','KANOGE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,121,'TABORA','KALIUA DC','KASHISHI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,91,'TABORA','KALIUA DC','KAZAROHO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,191,'TABORA','KALIUA DC','KONANNE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,151,'TABORA','KALIUA DC','MAKINGI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,201,'TABORA','KALIUA DC','MILAMBO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,171,'TABORA','KALIUA DC','MWONGOZO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,71,'TABORA','KALIUA DC','NG'' WANDE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,141,'TABORA','KALIUA DC','SASU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,151,'TABORA','KALIUA DC','SELELI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,121,'TABORA','KALIUA DC','SILAMBO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,341,'TABORA','KALIUA DC','UFUKUTWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,41,'TABORA','KALIUA DC','UGUNGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,11,'TABORA','KALIUA DC','UKUMBI SIGANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,161,'TABORA','KALIUA DC','USENYE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,31,'TABORA','KALIUA DC','USHOKOLA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,31,'TABORA','KALIUA DC','USIMBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,61,'TABORA','KALIUA DC','USINGE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,111,'TABORA','KALIUA DC','UYOWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,7,21,'TABORA','KALIUA DC','ZUGIMLOLE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,31,'TABORA','NZEGA DC','BUDUSHI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,201,'TABORA','NZEGA DC','BUKENE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,261,'TABORA','NZEGA DC','IGUSULE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,371,'TABORA','NZEGA DC','IKINDWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,361,'TABORA','NZEGA DC','ISAGENHE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,211,'TABORA','NZEGA DC','ISANZU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,221,'TABORA','NZEGA DC','ITOBO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,11,'TABORA','NZEGA DC','KAMANHALANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,291,'TABORA','NZEGA DC','KARITU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,281,'TABORA','NZEGA DC','KASELA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,191,'TABORA','NZEGA DC','LUSU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,81,'TABORA','NZEGA DC','MAGENGATI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,321,'TABORA','NZEGA DC','MAMBALI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,231,'TABORA','NZEGA DC','MBAGWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,81,'TABORA','NZEGA DC','MBUTU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,71,'TABORA','NZEGA DC','MILAMBO ITOBO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,61,'TABORA','NZEGA DC','MIZIBAZIBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,311,'TABORA','NZEGA DC','MOGWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,151,'TABORA','NZEGA DC','MUHUGI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,41,'TABORA','NZEGA DC','MWAKASHANHALA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,141,'TABORA','NZEGA DC','MWAMALA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,31,'TABORA','NZEGA DC','MWAMTUNDU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,231,'TABORA','NZEGA DC','MWANGOYE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,251,'TABORA','NZEGA DC','MWASALA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,201,'TABORA','NZEGA DC','NATA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,112,'TABORA','NZEGA DC','NDALA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,21,'TABORA','NZEGA DC','NKINIZIWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,11,'TABORA','NZEGA DC','PUGE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,351,'TABORA','NZEGA DC','SEMEMBELA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,271,'TABORA','NZEGA DC','SHIGAMBA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,241,'TABORA','NZEGA DC','SIGILI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,51,'TABORA','NZEGA DC','TONGI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,341,'TABORA','NZEGA DC','UDUKA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,101,'TABORA','NZEGA DC','UGEMBE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,161,'TABORA','NZEGA DC','UTWIGU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,111,'TABORA','NZEGA DC','WELA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,171,'TABORA','NZEGA TOWN','IJANIJA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,141,'TABORA','NZEGA TOWN','ITILO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,82,'TABORA','NZEGA TOWN','KITANGILI ')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,121,'TABORA','NZEGA TOWN','MBOGWE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,131,'TABORA','NZEGA TOWN','MIGUWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,251,'TABORA','NZEGA TOWN','MWANZOLI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,51,'TABORA','NZEGA TOWN','NZEGA MAGHARIBI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,51,'TABORA','NZEGA TOWN','NZEGA MASHARIKI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,102,'TABORA','NZEGA TOWN','NZEGA NDOGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,1,11,'TABORA','NZEGA TOWN','UCHAMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,21,'TABORA','SIKONGE DC','CHABUTWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,61,'TABORA','SIKONGE DC','IGIGWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,111,'TABORA','SIKONGE DC','IPOLE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,41,'TABORA','SIKONGE DC','KILOLELI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,81,'TABORA','SIKONGE DC','KILOLI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,81,'TABORA','SIKONGE DC','KILUMBI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,41,'TABORA','SIKONGE DC','KIPANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,91,'TABORA','SIKONGE DC','KIPILI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,131,'TABORA','SIKONGE DC','KISANGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,71,'TABORA','SIKONGE DC','KITUNDA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,141,'TABORA','SIKONGE DC','MISHENI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,131,'TABORA','SIKONGE DC','MKOLYE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,151,'TABORA','SIKONGE DC','MOLE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,151,'TABORA','SIKONGE DC','MPOMBWE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,121,'TABORA','SIKONGE DC','NGOYWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,51,'TABORA','SIKONGE DC','NYAHUA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,101,'TABORA','SIKONGE DC','PANGALE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,53,'TABORA','SIKONGE DC','SIKONGE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,191,'TABORA','SIKONGE DC','TUMBILI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,11,'TABORA','SIKONGE DC','TUTUO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,171,'TABORA','SIKONGE DC','USUNGA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,42,'TABORA','TABORA MUNICIPAL','CHEMCHEM')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,102,'TABORA','TABORA MUNICIPAL','CHEYO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,22,'TABORA','TABORA MUNICIPAL','GONGONI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,241,'TABORA','TABORA MUNICIPAL','IFUCHA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,231,'TABORA','TABORA MUNICIPAL','IKOMWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,92,'TABORA','TABORA MUNICIPAL','IPULI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,82,'TABORA','TABORA MUNICIPAL','ISEVYA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,181,'TABORA','TABORA MUNICIPAL','ITETEMIA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,161,'TABORA','TABORA MUNICIPAL','ITONJANDA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,91,'TABORA','TABORA MUNICIPAL','K/CHEKUNDU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,221,'TABORA','TABORA MUNICIPAL','KABILA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,141,'TABORA','TABORA MUNICIPAL','KAKOLA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,201,'TABORA','TABORA MUNICIPAL','KALUNDE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,12,'TABORA','TABORA MUNICIPAL','KANYENYE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,62,'TABORA','TABORA MUNICIPAL','KILOLENI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,112,'TABORA','TABORA MUNICIPAL','KITETE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,132,'TABORA','TABORA MUNICIPAL','MALOLO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,201,'TABORA','TABORA MUNICIPAL','MAPAMBANO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,32,'TABORA','TABORA MUNICIPAL','MBUGANI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,211,'TABORA','TABORA MUNICIPAL','MISHA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,171,'TABORA','TABORA MUNICIPAL','MPERA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,72,'TABORA','TABORA MUNICIPAL','MTENDENI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,22,'TABORA','TABORA MUNICIPAL','MWINYI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,171,'TABORA','TABORA MUNICIPAL','NDEVELWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,121,'TABORA','TABORA MUNICIPAL','NG'' AMBO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,251,'TABORA','TABORA MUNICIPAL','NTALIKWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,11,'TABORA','TABORA MUNICIPAL','T/RELI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,191,'TABORA','TABORA MUNICIPAL','TUMBI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,5,151,'TABORA','TABORA MUNICIPAL','UYUI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,4,141,'TABORA','URAMBO DC','IMALAMAKOYE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,4,21,'TABORA','URAMBO DC','ITUNDU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,4,31,'TABORA','URAMBO DC','KALOLENI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,4,11,'TABORA','URAMBO DC','KAPILULA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,4,131,'TABORA','URAMBO DC','KASISI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,4,251,'TABORA','URAMBO DC','KIYUNGI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,4,61,'TABORA','URAMBO DC','MCHIKICHINI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,4,41,'TABORA','URAMBO DC','MUUNGANO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,4,151,'TABORA','URAMBO DC','NSENDA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,4,51,'TABORA','URAMBO DC','SONGAMBELE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,4,101,'TABORA','URAMBO DC','UGALLA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,4,161,'TABORA','URAMBO DC','UKONDAMOYO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,4,23,'TABORA','URAMBO DC','URAMBO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,4,111,'TABORA','URAMBO DC','USISYA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,4,81,'TABORA','URAMBO DC','USSOKE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,4,331,'TABORA','URAMBO DC','UYOGO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,4,91,'TABORA','URAMBO DC','UYUMBU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,4,31,'TABORA','URAMBO DC','VUMILIA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,111,'TABORA','UYUI DC','BUKUMBI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,31,'TABORA','UYUI DC','GOWEKO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,241,'TABORA','UYUI DC','IBELAMILUNDI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,101,'TABORA','UYUI DC','IBIRI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,31,'TABORA','UYUI DC','IGALULA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,231,'TABORA','UYUI DC','IGULUNGU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,121,'TABORA','UYUI DC','IKONGOLO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,151,'TABORA','UYUI DC','ISIKIZYA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,151,'TABORA','UYUI DC','ISILA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,53,'TABORA','UYUI DC','KALOLA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,211,'TABORA','UYUI DC','KIGWA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,21,'TABORA','UYUI DC','KIZENGI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,291,'TABORA','UYUI DC','LOLANGULU')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,171,'TABORA','UYUI DC','LOYA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,11,'TABORA','UYUI DC','LUTENDE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,63,'TABORA','UYUI DC','MABAMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,141,'TABORA','UYUI DC','MAGIRI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,151,'TABORA','UYUI DC','MAKAZI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,181,'TABORA','UYUI DC','MISWAKI')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,221,'TABORA','UYUI DC','MIYENZE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,111,'TABORA','UYUI DC','MMALE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,71,'TABORA','UYUI DC','NDONO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,231,'TABORA','UYUI DC','NSIMBO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,201,'TABORA','UYUI DC','NSOLOLO')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,181,'TABORA','UYUI DC','NZUBUKA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,161,'TABORA','UYUI DC','SHITAGE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,191,'TABORA','UYUI DC','TURA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,81,'TABORA','UYUI DC','UFULUMA')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,131,'TABORA','UYUI DC','UPUGE')");
        dbSET.execSQL("INSERT INTO set_geo_codes(g1, g2, g3, name1, name2, name3) VALUES(14,3,91,'TABORA','UYUI DC','USAGARI')");

        cnSET.close();
        dbSET.close();
    }

    private void bkTable(){
        Conexion cnSET = new Conexion(this, STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        try {
            //dbSET.execSQL("CREATE TABLE IF NOT EXISTS finance (_id INTEGER, _date DATE, emis INTEGER,f1 INTEGER,f2 INTEGER,f3 INTEGER,f4 INTEGER,f5 INTEGER,f6 INTEGER,f7 INTEGER,f8 INTEGER,f9 INTEGER, f10 INTEGER,f11 INTEGER,f12 INTEGER,f13 INTEGER,f14 INTEGER,flag TEXT)");
            dbSET.execSQL("CREATE TABLE IF NOT EXISTS backup ( _date DATE, flag INTEGER)");
        }catch (Exception e) {}
    }

    public String getFlagbkTable(){
        bkTable();
        String getflagbktable="";
        Conexion cnSET = new Conexion(this, STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        Cursor cur_data = dbSET.rawQuery("SELECT flag FROM backup", null);

        if (cur_data.moveToFirst()){
            getflagbktable = cur_data.getString(0);
        }
        else{

            getflagbktable = "0";
        }

        return getflagbktable;
    }

    private void updatebkTable(){
        Conexion cnSET = new Conexion(this, STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        ContentValues Bitacora = new ContentValues();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());
        Bitacora.put("_date", formattedDate);
        Bitacora.put("flag", "1");
        dbSET.insert("backup", null, Bitacora);
    }

    private void backup(){
        BkDbUtility.copyFile(STATICS_ROOT, DB_INDICATORS_NAME, STATICS_ROOT_BK);
        updatebkTable();
    }

    private void aut_promotion() {
        Conexion cnSETpromotion = new Conexion(this, STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSETpromotion = cnSETpromotion.getReadableDatabase();
        try {
            //dbSET.execSQL("CREATE TABLE IF NOT EXISTS finance (_id INTEGER, _date DATE, emis INTEGER,f1 INTEGER,f2 INTEGER,f3 INTEGER,f4 INTEGER,f5 INTEGER,f6 INTEGER,f7 INTEGER,f8 INTEGER,f9 INTEGER, f10 INTEGER,f11 INTEGER,f12 INTEGER,f13 INTEGER,f14 INTEGER,flag TEXT)");
            dbSETpromotion.execSQL("INSERT INTO _sa (emis, sc, shift, level, grade, section, subject_assigned, year_ta)\n" +
                    "SELECT emis, sc, shift, level, grade+1, section, subject_assigned, year_ta+1 FROM _sa\n" +
                    "where grade<7  and  sc not  in (Select sc from _sa where year_ta ="+ school_year +")");
            dbSETpromotion.execSQL("update _sa set level = 1 and grade = 1 where level=3 and grade = 3 and year_ta ="+ school_year +")");

        }catch (Exception e) {}
        Toast.makeText(getApplication(), getResources().getString(R.string.automatic_promotion), Toast.LENGTH_SHORT).show();
        dbSETpromotion.close();
        cnSETpromotion.close();
    }
}

package com.example.sergio.sistz;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.sergio.sistz.data.AttendanceList;
import com.example.sergio.sistz.mysql.Conexion;
import com.example.sergio.sistz.util.DBDefinitionConnection;
import com.example.sergio.sistz.util.toolsfncs;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Sergio on 3/21/2016.
 */
public class DailyClassroomAttendance extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    public static final String STATICS_ROOT = Environment.getExternalStorageDirectory() + File.separator + "sisdb";
    String[] _shift = {"Morning","Afternoon","Evening"};
    String[] _level = {"Primary","Secondary","Pre-Primary"};
    private String[] _grade = {"G1","G2","G3","G4","G5","G6","G7","G8"};
    private String[] _section = {"A","B","C","D","E","F","G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    //private String[] _subject_p = {"Mathematics","English","Kiswahili","French","Science","Geography","Civics","History","Vocational skills","Personality and Sports","ICT","Other"};
    private String[] _subject_p = {"All","English","Kiswahili","French","Science","Geography","Civics","History","Vocational skills","Personality and Sports","ICT","Other"};
    ArrayList<String> list_teacher = new ArrayList<>();
    ArrayList<String> list_code = new ArrayList<>();
    Spinner sp_teacher, sp_reason;
    ListView lv_attendance, lv_subject_attendance;
    ScrollView lista;
    public static String EMIS_code = "";
    public static String code="";
    CharSequence texto;
    FloatingActionButton save_reg,btn_confirm;
    CheckBox cb_absence;
    RadioButton _col1a, _col1b, _col2a, _col2b;
    TextView date_record, tv_subject;
    String _IU="U", teacher_selected="", shift_selected="", level_selected="", grade_selected="", section_selected="", subject_selected="", sqlcondition, dateAttendance="";
    FrameLayout fl_part1, fl_part2;
    LinearLayout ll_subject, ll_in_charge, ll_head_list, ll_question_3, ll_question_4;
    DatePicker dp_attandance;
    int present, absence, ts_present=0, ts_absence=0, show_list=0;

    long date = System.currentTimeMillis();  // sistem date
//    SimpleDateFormat sdf = new SimpleDateFormat("MMMM-dd-yyyy");
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy  h:mm a");
    SimpleDateFormat formatDatabase = new SimpleDateFormat("yyyy-MM-dd");

    //Calendar calendar = Calendar.getInstance();
    public int school_year = calendar.get(Calendar.YEAR);

    private List<AttendanceList> attendance;

    DBDefinitionConnection dbConn = new DBDefinitionConnection(DailyClassroomAttendance.this);

    //String test1 = dbConn.testLoadEMISCode();




//    Date today;
    //String currentDateandTime = new SimpleDateFormat("MM-dd-yyyy").format();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daily_classroom_attendance);


        lista = (ScrollView) findViewById(R.id.sv_1);
        this.sp_teacher = (Spinner) findViewById(R.id.sp_teacher);
        this.sp_reason = (Spinner) findViewById(R.id.sp_reason);
        lv_subject_attendance = (ListView) findViewById(R.id.lv_subject_attendance);
        lv_attendance = (ListView) findViewById(R.id.lv_attendance);
        save_reg = (FloatingActionButton) findViewById(R.id.save_reg);
        date_record = (TextView) findViewById(R.id.date_record);
        btn_confirm = (FloatingActionButton) findViewById(R.id.btn_confirm);
        fl_part1 = (FrameLayout) findViewById(R.id.fl_part1);
        fl_part2 = (FrameLayout) findViewById(R.id.fl_part2);
        ll_subject = (LinearLayout) findViewById(R.id.ll_subject);
        ll_in_charge = (LinearLayout) findViewById(R.id.ll_in_charge);
        ll_head_list = (LinearLayout) findViewById(R.id.ll_head_list);
        ll_question_3 = (LinearLayout) findViewById(R.id.ll_queston_3);
        ll_question_4 = (LinearLayout) findViewById(R.id.ll_question_4);
        tv_subject = (TextView) findViewById(R.id.tv_subject);
        _col1a = (RadioButton) findViewById(R.id._col1a);
        _col1b = (RadioButton) findViewById(R.id._col1b);
        _col2a = (RadioButton) findViewById(R.id._col2a);
        _col2b = (RadioButton) findViewById(R.id._col2b);
        dp_attandance = (DatePicker) findViewById(R.id.dp_atteandance);
//        String newDate = "1840-09-05";
//        Toast.makeText(this, newDate.substring(0, 4), Toast.LENGTH_SHORT).show();
                //dp_attandance.updateDate(1996,05,25);


        fl_part1.setVisibility(View.GONE);
        fl_part2.setVisibility(View.VISIBLE);
        save_reg.setVisibility(View.GONE);
        btn_confirm.setVisibility(View.GONE);
        ll_in_charge.setVisibility(View.GONE);
        ll_head_list.setVisibility(View.GONE);
        lista.setVisibility(View.GONE);
        ll_question_3.setVisibility(View.GONE);
        ll_question_4.setVisibility(View.GONE);

        //date_record.setText(dateString);
        btn_confirm.setOnClickListener(this);
        save_reg.setOnClickListener(this);
        tv_subject.setOnClickListener(this);
        _col1a.setOnClickListener(this);
        _col1b.setOnClickListener(this);
        _col2a.setOnClickListener(this);
        _col2b.setOnClickListener(this);

        start_array();
        loadSpinner_teacher();

        attendance = new ArrayList<AttendanceList>();

        //Toast.makeText(this,"getLoadEMISCode -> " + test1,Toast.LENGTH_SHORT).show();
    }

    private void start_array() {
        _shift[0] = getResources().getString(R.string.str_g_morning);
        _shift[1] = getResources().getString(R.string.str_g_afternoon);
        _shift[2] = getResources().getString(R.string.str_g_evening);
        _level[0] = getResources().getString(R.string.p);
        _level[1] = getResources().getString(R.string.s);
        _level[2] = getResources().getString(R.string.pp);

        // ***************** Load Subject  if read DATABASE recordSet ****************************
        Conexion cnSET = new Conexion(this, STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        dbSET.execSQL("UPDATE grade SET grade='"+getResources().getString(R.string.str_g_std1)+"' WHERE level=1 and id=1");
        dbSET.execSQL("UPDATE grade SET grade='"+getResources().getString(R.string.str_g_std2)+"' WHERE level=1 and id=2");
        dbSET.execSQL("UPDATE grade SET grade='"+getResources().getString(R.string.str_g_std3)+"' WHERE level=1 and id=3");
        dbSET.execSQL("UPDATE grade SET grade='"+getResources().getString(R.string.str_g_std4)+"' WHERE level=1 and id=4");
        dbSET.execSQL("UPDATE grade SET grade='"+getResources().getString(R.string.str_g_std5)+"' WHERE level=1 and id=5");
        dbSET.execSQL("UPDATE grade SET grade='"+getResources().getString(R.string.str_g_std6)+"' WHERE level=1 and id=6");
        dbSET.execSQL("UPDATE grade SET grade='"+getResources().getString(R.string.str_g_std7)+"' WHERE level=1 and id=7");
        dbSET.execSQL("UPDATE grade SET grade='"+getResources().getString(R.string.str_g_yeari)+"' WHERE level=3 and id=1");
        dbSET.execSQL("UPDATE grade SET grade='" + getResources().getString(R.string.str_g_yearii) + "' WHERE level=3 and id=3");

        dbSET.execSQL("UPDATE subject SET subject='" + getResources().getString(R.string.str_g_reading) + "' WHERE level=1 and id=1");
        dbSET.execSQL("UPDATE subject SET subject='" + getResources().getString(R.string.str_g_writing) + "' WHERE level=1 and id=2");
        dbSET.execSQL("UPDATE subject SET subject='" + getResources().getString(R.string.str_g_arithmetic) + "' WHERE level=1 and id=3");
        dbSET.execSQL("UPDATE subject SET subject='" + getResources().getString(R.string.str_g_healt) + "' WHERE level=1 and id=4");
        dbSET.execSQL("UPDATE subject SET subject='" + getResources().getString(R.string.str_g_games) + "' WHERE level=1 and id=5");
        dbSET.execSQL("UPDATE subject SET subject='" + getResources().getString(R.string.str_g_religion) + "' WHERE level=1 and id=6");
        dbSET.execSQL("UPDATE subject SET subject='" + getResources().getString(R.string.str_g_mathematics) + "' WHERE level=1 and id=7");
        dbSET.execSQL("UPDATE subject SET subject='" + getResources().getString(R.string.str_g_english) + "' WHERE level=1 and id=8");
        dbSET.execSQL("UPDATE subject SET subject='" + getResources().getString(R.string.str_g_science) + "' WHERE level=1 and id=9");
        dbSET.execSQL("UPDATE subject SET subject='" + getResources().getString(R.string.str_g_history) + "' WHERE level=1 and id=10");
        dbSET.execSQL("UPDATE subject SET subject='" + getResources().getString(R.string.str_g_geography) + "' WHERE level=1 and id=11");

        dbSET.execSQL("UPDATE subject SET subject='" + getResources().getString(R.string.str_g_all) + "' WHERE level=3 and id=1");

        dbSET.execSQL("UPDATE subject SET subject='" + getResources().getString(R.string.str_g_kiswahili) + "' WHERE level=1 and id=12");
        dbSET.execSQL("UPDATE subject SET subject='" + getResources().getString(R.string.str_g_civics) + "' WHERE level=1 and id=13");
        dbSET.execSQL("UPDATE subject SET subject='" + getResources().getString(R.string.str_g_vocational_skills) + "' WHERE level=1 and id=14");
        dbSET.execSQL("UPDATE subject SET subject='" + getResources().getString(R.string.str_g_ict) + "' WHERE level=1 and id=15");
        dbSET.execSQL("UPDATE subject SET subject='" + getResources().getString(R.string.str_g_personality) + "' WHERE level=1 and id=16");

        dbSET.execSQL("UPDATE subject SET subject='"+getResources().getString(R.string.str_g_social_studies)+"' WHERE level=1 and id=17");
        dbSET.execSQL("UPDATE subject SET subject='"+getResources().getString(R.string.str_g_civics_and_moral)+"' WHERE level=1 and id=18");
        dbSET.execSQL("UPDATE subject SET subject='"+getResources().getString(R.string.str_g_cience_and_technology)+"' WHERE level=1 and id=19");
    }

    private void setDateAttendance() {
        int day = dp_attandance.getDayOfMonth();
        int month = dp_attandance.getMonth();
        int year = dp_attandance.getYear();
        calendar.set(year, month, day);
        String strDate = format.format(calendar.getTime());
        String strDate2 = formatDatabase.format(calendar.getTime());

//        Toast.makeText(this,String.valueOf(year) + "-" + String.valueOf(month)+ "-" + String.valueOf(day),Toast.LENGTH_SHORT).show();
//        Date d = new Date(year, month, day);
//
//        String formatDate = sdf.format(new Date(month,day,year));
//        Toast.makeText(this,strDate,Toast.LENGTH_SHORT).show();
        date_record.setText(strDate);
        dateAttendance = strDate2;
        // date_record.setText(String.valueOf(year)+"-"+ String.valueOf(month)+"-"+String.valueOf(day));
    }

    private void loadSpinner_teacher(){
        Conexion cnSET = new Conexion(this,STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        //Cursor cur_data = dbSET.rawQuery(" SELECT a.tc, t.surname, t.givenname FROM _ta a  INNER JOIN teacher t ON t._id=a.tc and year_ta= " + school_year + " GROUP BY a.tc, t.surname, t.surname ORDER BY t.surname", null);
        Cursor cur_data = dbSET.rawQuery("SELECT a.tc, t.surname, t.givenname FROM _ta a  INNER JOIN teacher t ON t._id=a.tc  GROUP BY a.tc, t.surname, t.surname ORDER BY t.surname", null);
        cur_data.moveToFirst();
        String col_id="0", col_g1=getResources().getString(R.string.str_g_selectone);
        list_teacher.add(col_g1);
        list_code.add(col_id);
        //list_1.clear();
        //list_code.clear();
        if (cur_data.moveToFirst()) {
            do {
                col_g1 = cur_data.getString(2) + ", " + cur_data.getString(1);
                col_id = cur_data.getString(0);
                list_teacher.add(col_g1);
                list_code.add(col_id);
            } while (cur_data.moveToNext());
        }

        ArrayAdapter adap_list = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list_teacher);
        sp_teacher.setAdapter(adap_list);

        sp_teacher.setOnItemSelectedListener(this);
        sp_teacher.setSelected(false);
        _col1a.setChecked(true);
        _col1b.setChecked(false);
    }


    public void loadSubjectAssigned(String code){

        ll_question_3.setVisibility(View.GONE);
        ll_question_4.setVisibility(View.GONE);
        //_col1a.setChecked(false);
        //_col1b.setChecked(false);
        save_reg.setVisibility(View.GONE);
        if (code != "") {
            final String deleteCode = code;
            ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map = new HashMap<String, String>();
            Conexion cnSET = new Conexion(this, STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
            SQLiteDatabase dbSET = cnSET.getReadableDatabase();
            Cursor cur_data = dbSET.rawQuery("SELECT a.tc, a.shift, a.level, a.grade, a.section, a.subject, t.t_s FROM _ta a  INNER JOIN teacher t ON t._id=a.tc WHERE a.tc="+code, null);
            //if (code == "") {code = SettingsMenuStaff.TS_code;}
            if (cur_data.moveToFirst()&& code != "") {
                do {
                    map = new HashMap<String, String>();
                    map.put("shift", String.valueOf(_shift[cur_data.getInt(1) - 1]));
                    map.put("level", String.valueOf(_level[cur_data.getInt(2) - 1]));
                    //map.put("grade", String.valueOf(_grade[cur_data.getInt(3) - 1]));
                    //Toast.makeText(getApplicationContext(), getSubject(cur_data.getString(2), cur_data.getString(3)), Toast.LENGTH_SHORT).show();
                    map.put("grade", getGrade(cur_data.getString(2), cur_data.getString(3)));
                    map.put("section", String.valueOf(_section[cur_data.getInt(4) - 1]));
                    map.put("subject", getSubject(cur_data.getString(2), cur_data.getString(5)));
                    //map.put("subject", getSubject(String.valueOf(cur_data.getInt(2)), String.valueOf(cur_data.getInt(5))));
                    //if (cur_data.getInt(5)>0) {map.put("subject", String.valueOf(_subject_p[cur_data.getInt(5) - 1]));}
                    mylist.add(map);
                    show_list=cur_data.getInt(6); // 1=Class Teacher and 2=Subject Teacher
                }while (cur_data.moveToNext());

                SimpleAdapter mSchedule = new SimpleAdapter(this, mylist, R.layout.row_list_assing,
                        new String[]{"shift", "level", "grade", "section", "subject"}, new int[]{R.id.txt1, R.id.txt2, R.id.txt3, R.id.txt4, R.id.txt5});
                lv_subject_attendance.setAdapter(mSchedule);

                lv_subject_attendance.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TextView text1 = (TextView) view.findViewById(R.id.txt1);
                        TextView text2 = (TextView) view.findViewById(R.id.txt2);
                        TextView text3 = (TextView) view.findViewById(R.id.txt3);
                        TextView text4 = (TextView) view.findViewById(R.id.txt4);
                        TextView text5 = (TextView) view.findViewById(R.id.txt5);

                        texto = " tc=" + String.valueOf(deleteCode) + " AND shift=" +
                                getIndexArray(_shift, text1.getText().toString()) + " AND level=" +
                                getIndexArray(_level, text2.getText().toString()) + " AND  grade=" +
                                getIndexArray(_grade, text3.getText().toString()) + " AND  section=" +
                                getIndexArray(_section, text4.getText().toString()) + " AND subject=" +
                                getIndexArray(_subject_p, text5.getText().toString());

                        //Toast.makeText(getApplicationContext(), texto , Toast.LENGTH_SHORT).show();
                        //dialogAlert(3);
                        loadStudentAssigned(String.valueOf(deleteCode),
                                String.valueOf(getIndexArray(_shift, text1.getText().toString())),
                                String.valueOf(getIndexArray(_level, text2.getText().toString())),
                                //String.valueOf(getIndexArray(_grade, text3.getText().toString())),
                                getGradeId(String.valueOf(getIndexArray(_level, text2.getText().toString())), text3.getText().toString()),
                                //getGradeId(String.valueOf(getIndexArray(_level, text2.getText().toString())),text3.getText().toString())),
                                String.valueOf(getIndexArray(_section, text4.getText().toString())),
                                getSubjectId(String.valueOf(getIndexArray(_level, text2.getText().toString())), text5.getText().toString()));
                        //String.valueOf(getIndexArray(_subject_p, text5.getText().toString())));

                        teacher_selected = deleteCode;

                        tv_subject.setText(text1.getText().toString() + " - " + text2.getText().toString() + " - " + text3.getText().toString() + " - " + text4.getText().toString() + " - " + text5.getText().toString());
                        // parent.getChildAt(position).setBackgroundColor(Color.YELLOW);
                        ll_question_3.setVisibility(View.VISIBLE);
                        save_reg.setVisibility(View.VISIBLE);
                        if (show_list==1 && _col1a.isChecked()) {ll_head_list.setVisibility(View.VISIBLE); lista.setVisibility(View.VISIBLE);}
//                        _col1a.setChecked(false);
//                        _col1b.setChecked(false);
                    }
                });
            }
            else {
//                mylist.clear();
//                SimpleAdapter mSchedule = new SimpleAdapter(getContext(), mylist, R.layout.row_list_assing,
//                        new String[]{"shift", "level", "grade", "section", "subject"}, new int[]{R.id.txt1, R.id.txt2, R.id.txt3, R.id.txt4, R.id.txt5});
//                lv_subject_teacher.setAdapter(mSchedule);
            }
        }
    }


    @SuppressLint("WrongViewCast")
    private void loadStudentAssigned(String code, String shift, String level, String grade, String section, String subject){
        setDateAttendance();
        ll_subject.setVisibility(View.GONE);
        tv_subject.setVisibility(View.VISIBLE);
        //lista.setVisibility(View.VISIBLE);
        shift_selected = shift;
        level_selected = level;
        grade_selected = grade;
        section_selected = section;
        subject_selected = subject;
        Conexion cnSET_Attendance = new Conexion(this,STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET_Attendance = cnSET_Attendance.getReadableDatabase();
        //Cursor cur_data_attendance = dbSET_Attendance.rawQuery("SELECT emis, t_id, subject, s_id, absence, reason, date FROM attendance  WHERE emis="+getEMIS_code()+" AND t_id="+code+"  AND shift="+shift+"  AND level="+level+"  AND grade="+grade+"  AND section="+section+"  AND subject="+subject+" AND Date(date)='"+dateAttendance+"'" + " AND date=" + school_year, null);
        //Cursor cur_data_attendance = dbSET_Attendance.rawQuery("SELECT emis, t_id, subject, s_id, absence, reason, date FROM attendance  WHERE  t_id="+code+"  AND shift="+shift+"  AND level="+level+"  AND grade="+grade+"  AND section="+section+"  AND subject="+subject+" AND Date(date)='"+dateAttendance+"'" + " AND date=" + school_year, null);
        //String sql= "SELECT at.emis, at.t_id, at.subject, at.s_id, (ifnull(st.family,\"\") || ', ' || st.surname) AS fullname, at.absence, at.reason, at.date FROM attendance  at " +
        String sql= "SELECT at.s_id, (ifnull(st.family,\"\") || ', ' || st.surname) AS fullname, at.absence, at.reason FROM attendance  at " +
                "INNER JOIN student st ON (st._id=at.s_id) " +
                "WHERE  at.t_id="+code+"  AND at.shift="+shift+"  AND at.level="+level+"  AND at.grade="+grade+"  AND at.section="+section+"  AND at.subject="+subject+" AND Date(at.date)='"+dateAttendance+"'";
        Cursor cur_data_attendance = dbSET_Attendance.rawQuery(sql, null);
        //ts_present = cur_data_attendance.getCount();
        if (cur_data_attendance.getCount()>0) {_IU = "U";
           //Toast.makeText(this,  "  List already exists!!!! " + cur_data_attendance.getCount() , Toast.LENGTH_LONG).show();
        } else {ts_present = cur_data_attendance.getCount(); _IU = "I";
            sql= "SELECT  DISTINCT(s.sc), (ifnull(st.family,\"\") || ', ' || st.surname) AS fullname, 1 AS absence, 0 AS reason FROM _sa s \n" +
                    "  INNER JOIN student st ON st._id=s.sc\n" +
                    "  INNER JOIN  _ta t ON t.shift=s.shift AND t.level=s.level AND t.grade=s.grade and t.section=s.section \n" +
                    "  INNER JOIN subject s2 ON  s2.level=t.level AND s2.id=t.subject\n" +
                    "  WHERE t.tc=" + code + " and s.year_ta=" + school_year + " \n" +
                    " AND t.shift="+shift+" AND t.level="+level+"  AND t.grade="+grade+"   AND t.section="+section+" AND t.subject="+subject+" ORDER BY st.family, st.surname";
            cur_data_attendance = dbSET_Attendance.rawQuery(sql, null);
        }
        //sqlcondition = " emis="+getEMIS_code()+" AND t_id="+code+"  AND shift="+shift+"  AND level="+level+"  AND grade="+grade+"  AND section="+section+"  AND subject="+subject+" AND Date(date)='"+dateAttendance +"'" + " AND date=" + school_year;
        //sqlcondition = " t_id="+code+"  AND shift="+shift+"  AND level="+level+"  AND grade="+grade+"  AND section="+section+"  AND subject="+subject+" AND Date(date)='"+dateAttendance +"'" + " AND date=" + school_year+"'";
        sqlcondition = " t_id="+code+"  AND shift="+shift+"  AND level="+level+"  AND grade="+grade+"  AND section="+section+"  AND subject="+subject+" AND Date(date)='"+dateAttendance +"'";

        if (code != "") {
            //final String deleteCode = code;
            ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map = new HashMap<String, String>();
            ArrayList<HashMap<String, String>> mylist2 = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map2 = new HashMap<String, String>();
            //Conexion cnSET = new Conexion(this, STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
            //SQLiteDatabase dbSET = cnSET.getReadableDatabase();
            //Cursor cur_data = dbSET.rawQuery("SELECT  s.sc, (st.givenname || ', ' || st.surname) AS fullname, s2.subject FROM _sa s \n" +
//            Cursor cur_data = dbSET.rawQuery("SELECT  DISTINCT(s.sc), (ifnull(st.family,\"\") || ', ' || st.surname) AS fullname, s2.subject FROM _sa s \n" +
//                    "  INNER JOIN student st ON st._id=s.sc\n" +
//                    "  INNER JOIN  _ta t ON t.shift=s.shift AND t.level=s.level AND t.grade=s.grade and t.section=s.section \n" +
//                    "  INNER JOIN subject s2 ON  s2.level=t.level AND s2.id=t.subject\n" +
//                    "  WHERE t.tc=" + code + " and s.year_ta=" + school_year + " \n" +
//                    " AND t.shift="+shift+" AND t.level="+level+"  AND t.grade="+grade+"   AND t.section="+section+" AND t.subject="+subject+" ORDER BY st.family, st.surname", null);
            cur_data_attendance.moveToFirst();
            //if (cur_data.moveToFirst()&& code != "") {
            if (cur_data_attendance.getCount() > 0) {
                do {
                    map = new HashMap<String, String>();
                    map.put("code", String.valueOf(cur_data_attendance.getInt(0)));
                    map.put("fullname", cur_data_attendance.getString(1));
//                    map.put("absence", cur_data_attendance.getString(2));
//                    map.put("reason", cur_data_attendance.getString(3));
                    //map.put("subject", cur_data.getString(2));
                    mylist.add(map);
                }
                while (cur_data_attendance.moveToNext());


                //ArrayAdapter Attendance = new ArrayAdapter(AttendanceList)
                SimpleAdapter mSchedule = new SimpleAdapter(this, mylist, R.layout.row_list_assing3,
                        //new String[]{"code", "fullname","absence","reason"}, new int[]{R.id.txt1, R.id.txt2, R.id.cb_absence, R.id.sp_reason});
                        new String[]{"code", "fullname"}, new int[]{R.id.txt1, R.id.txt2});
                lv_attendance.setAdapter(mSchedule);

                int tmp_hg = lv_attendance.getCount() * 95;
                ViewGroup.LayoutParams params = lv_attendance.getLayoutParams();
                params.height = tmp_hg;
                lv_attendance.setLayoutParams(params);

                //save_reg.setVisibility(View.VISIBLE);
            }
            else {
                mylist.clear();
                SimpleAdapter mSchedule = new SimpleAdapter(this, mylist, R.layout.row_list_assing,
                        new String[]{"code", "fullname"}, new int[]{R.id.txt1, R.id.txt2});
                lv_attendance.setAdapter(mSchedule);
                toolsfncs.dialogAlertConfirm(this,getResources(),10);
                //Toast.makeText(getApplicationContext(), "NO hay alumnos asignados o el maestro no es CLASS TEACHER.", Toast.LENGTH_SHORT).show();
                save_reg.setVisibility(View.GONE);
            }
        }

    }

    public void saveAttendanceTeacher () {
        setDateAttendance();
        int t_present = 0, charge=0;
        Conexion cnSET = new Conexion(this,STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        String sql = "", delimit="%", tableName="attendance";
        if (_col1a.isChecked()) {t_present=1;} else if (_col1b.isChecked()) {t_present = 0; } else {t_present = 0; }
        //if (_col2a.isChecked()) {charge=1;} else if (_col2b.isChecked()) {charge = 2; } else {charge = 0; }
        sql = sql + tableName + delimit + dateAttendance + delimit + getEMIS_code() + delimit;
        sql = sql + teacher_selected + delimit + t_present + delimit + charge + delimit;
        sql = sql + shift_selected + delimit;
        sql = sql + level_selected + delimit;
        sql = sql + grade_selected + delimit;
        sql = sql + section_selected + delimit;
        sql = sql + subject_selected + delimit;
        ContentValues reg = new ContentValues();
        reg.put("emis",getEMIS_code());
        reg.put("t_present", t_present);
        reg.put("charge", charge);
        reg.put("t_id", teacher_selected);
        reg.put("shift", shift_selected);
        reg.put("level", level_selected);
        reg.put("grade", grade_selected);
        reg.put("section", section_selected);
        reg.put("subject", subject_selected);
        reg.put("s_id", 0);
        reg.put("absence",-1);
        reg.put("reason", -1);
        reg.put("date", dateAttendance);
        dbSET.insert("attendance", null, reg);

        toolsfncs.dialogAlertConfirm(this,getResources(),9);
        //Toast.makeText(this, "The information has been updated!!!", Toast.LENGTH_SHORT).show();
        sql = sql + ts_present + delimit + ts_absence + delimit + _IU;
        _IU = "U";
        try {
            // ****************** Fill Bitacora
            ContentValues Bitacora = new ContentValues();
            Bitacora.put("sis_sql", sql);
            dbSET.insert("sisupdate", null, Bitacora);  sql = "";
        } catch (Exception e) {
        }
        //ts_present=0; ts_absence=0;
    }

    public void saveAttendance () {
        setDateAttendance();
        //ts_present=0; ts_absence=0;
        int t_present = 0, charge=0;
        Conexion cnSET = new Conexion(this,STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        String sql = "", delimit="%", tableName="attendance";
        //sql = sql + dateAttendance + delimit + getEMIS_code() + delimit;
        if (_col1a.isChecked()) {t_present=1;} else if (_col1b.isChecked()) {t_present = 2; } else {t_present = 0; }
        if (_col2a.isChecked()) {charge=1;} else if (_col2b.isChecked()) {charge = 2; } else {charge = 0; }
        sql = sql + tableName + delimit + dateAttendance + delimit + getEMIS_code() + delimit;
        sql = sql + teacher_selected + delimit + t_present + delimit + charge + delimit;
        sql = sql + shift_selected + delimit;
        sql = sql + level_selected + delimit;
        sql = sql + grade_selected + delimit;
        sql = sql + section_selected + delimit;
        sql = sql + subject_selected + delimit;
        ContentValues reg = new ContentValues();
        for (int i=0; i < lv_attendance.getAdapter().getCount(); i++) {
            String absence="", reason="";
            reg.put("emis",getEMIS_code());
            reg.put("t_present", t_present);
            reg.put("charge", charge);
            reg.put("t_id", teacher_selected);
            reg.put("shift", shift_selected);
            reg.put("level", level_selected);
            reg.put("grade", grade_selected);
            reg.put("section", section_selected);
            reg.put("subject", subject_selected);
            HashMap<String,String> item = (HashMap<String, String>) lv_attendance.getItemAtPosition(i);
            String reason_val = String.valueOf(((TextView)((Spinner)lv_attendance.getChildAt(i).findViewById(R.id.sp_reason)).getSelectedView()).getText().toString());
            if (reason_val.toString().equals(getResources().getString(R.string.str_g_sick)) || reason_val.toString().equals(getResources().getString(R.string.str_g_excused)) || reason_val.toString().equals(getResources().getString(R.string.str_g_unexcused)) || reason_val.toString().equals("Total absence") ) {
                absence="";
                // ******************* CONTROL DE RAZON DE AUSENCIA *****************************
                reason_val = String.valueOf(((TextView)((Spinner)lv_attendance.getChildAt(i).findViewById(R.id.sp_reason)).getSelectedView()).getText().toString());
                ts_absence++;
                switch (reason_val){
                    case "Sick": reason="1"; break;
                    case "Excused": reason="2"; break;
                    case "Unexcused": reason="3"; break;
                    case "Kuumwa": reason="1"; break;
                    case "Ruhusiwa": reason="2"; break;
                    case "Kutoruhusiwa": reason="3"; break;
                    //case "Total absence": reason="4"; break;
                }
            } else {
                absence="1";
                ts_present++;
            }
//            if (((CheckBox)lv_attendance.getChildAt(i).findViewById(R.id.cb_absence)).isChecked()) {
//                //Toast.makeText(this, item.get("code") + "  Present", Toast.LENGTH_LONG).show();
//                    absence="1";
//                    ts_present++;
//            }else {
//                //Toast.makeText(this, item.get("code") + "  Absence", Toast.LENGTH_LONG).show();
//                absence="";
//                // ******************* CONTROL DE RAZON DE AUSENCIA *****************************
//                reason_val = String.valueOf(((TextView)((Spinner)lv_attendance.getChildAt(i).findViewById(R.id.sp_reason)).getSelectedView()).getText().toString());
//                ts_absence++;
//                switch (reason_val){
//                    case "Sick": reason="1"; break;
//                    case "Excused": reason="2"; break;
//                    case "Unexcused": reason="3"; break;
//                    case "Total absence": reason="4"; break;
//                }
//                //Toast.makeText(this, reason_val, Toast.LENGTH_LONG).show();
//            }
            reg.put("s_id", item.get("code"));  //sql = sql + item.get("code") + delimit;
            reg.put("absence", absence);        //sql = sql + absence + delimit;
            reg.put("reason", reason);          //sql = sql + reason + delimit;
            reg.put("date", dateAttendance);
            //Toast.makeText(this, getEMIS_code() + " - " + teacher_selected  + " - " + subject_selected + " - " + item.get("code") + " - " +   absence + " - " + reason, Toast.LENGTH_SHORT).show();
            //if (_IU == "I") {
                dbSET.insert("attendance", null, reg);
            //} else {} //dbSET.update("_sa", reg, sqlcondition, null);}
        }
        toolsfncs.dialogAlertConfirm(this,getResources(),9);
        //Toast.makeText(this, "The information has been updated!!!", Toast.LENGTH_SHORT).show();
        sql = sql + ts_present + delimit + ts_absence + delimit + _IU;
        _IU = "U";
        try {
            // ****************** Fill Bitacora
            ContentValues Bitacora = new ContentValues();
            Bitacora.put("sis_sql", sql);
            dbSET.insert("sisupdate", null, Bitacora);  sql = "";
        } catch (Exception e) {
        }
        ts_present=0; ts_absence=0;
        //loadStudentAssigned(teacher_selected,shift_selected,level_selected,grade_selected,section_selected,subject_selected);
    }


    public void deleteRecord(String string){
        //Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
        Conexion cnSET = new Conexion(this,STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        dbSET.delete("attendance",string, null);
        dbSET.close();
        cnSET.close();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.sp_teacher:
                if (sp_teacher.getSelectedItem().toString().equals("Select one")) {} else {
                    loadSubjectAssigned(list_code.get(position));
                }
                ll_subject.setVisibility(View.VISIBLE);
                tv_subject.setVisibility(View.GONE);
                ll_head_list.setVisibility(View.GONE);
                lista.setVisibility(View.GONE);
                save_reg.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public int getIndexArray(String[] myArray , String myString){
        int index = 0;
        for (int i=0;i<myArray.length;i++){
            if (myArray[i].equals(myString)){
                index = i+1;
            }
        }
        return index;
    }

    // *********** Control Alerts ************************
    public void dialogAlert(int v){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        //dialogo1.setTitle("Important");
        dialogo1.setTitle(getResources().getString(R.string.str_bl_msj1)); // Importante
        if (v == 1){dialogo1.setMessage(getResources().getString(R.string.str_bl_msj2));}
        if (v == 2){dialogo1.setMessage("Are you sure to quit?");}
        if (v == 3){dialogo1.setMessage("Are you sure to delete record?");}

        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton(getResources().getString(R.string.str_g_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                cancelar();
            }
        });
        dialogo1.setNegativeButton(getResources().getString(R.string.str_g_confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                aceptar();
                //load_lv_subject_assing(SettingsMenuStaff.TS_code);
            }
        });
        dialogo1.show();
    }

    public void aceptar() {
        deleteRecord(sqlcondition);
        if (show_list==1 && _col1a.isChecked()) {saveAttendance();}
        else {saveAttendanceTeacher();}
        save_reg.setVisibility(View.GONE);
        ll_head_list.setVisibility(View.GONE);
        lista.setVisibility(View.GONE);
        ll_question_3.setVisibility(View.GONE);
        ll_question_4.setVisibility(View.GONE);
        tv_subject.setVisibility(View.GONE);
        ll_subject.setVisibility(View.VISIBLE);
        //_col1a.setChecked(false);

        //_col1b.setChecked(false);
    }
    public void cancelar() {
        //finish();
    }
    // *********** END Control Alerts ************************

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_reg:
                //setDateAttendance();
                dialogAlert(1);
                //saveAttendance();
//                setDateAttendance();\
                // ************* esto es lo ultimo
//                save_reg.setVisibility(View.GONE);
//                ll_head_list.setVisibility(View.GONE);
//                lista.setVisibility(View.GONE);
//                _col1a.setChecked(false);
//                _col1b.setChecked(false);
                // ************* esto es lo ultimo
//                fl_part2.setVisibility(View.GONE);
//                fl_part1.setVisibility(View.VISIBLE);

                break;
            case R.id.btn_confirm:
                setDateAttendance();
                fl_part1.setVisibility(View.GONE);
                fl_part2.setVisibility(View.VISIBLE);
                btn_confirm.setVisibility(View.GONE);
                break;
            case R.id.tv_subject:
                ll_subject.setVisibility(View.VISIBLE);
                tv_subject.setVisibility(View.GONE);
                lista.setVisibility(View.GONE);
                save_reg.setVisibility(View.GONE);
                break;
            case R.id._col1a:
                //ll_in_charge.setVisibility(View.GONE);
                //btn_confirm.setVisibility(View.VISIBLE);
                save_reg.setVisibility(View.VISIBLE);
                ll_question_4.setVisibility(View.GONE);
                //if (show_list == 1) {Toast.makeText(getApplication(), "Solamente guarda la asistencia del profesor!!!", Toast.LENGTH_SHORT).show();}
                //else {ll_head_list.setVisibility(View.VISIBLE); lista.setVisibility(View.VISIBLE);}
                //if (show_list == 1 && _col1a.isChecked()) {ll_head_list.setVisibility(View.VISIBLE); lista.setVisibility(View.VISIBLE);}
                if (show_list == 1) {ll_head_list.setVisibility(View.VISIBLE); lista.setVisibility(View.VISIBLE);}
                break;
            case R.id._col1b:
//                ll_in_charge.setVisibility(View.VISIBLE);
//                btn_confirm.setVisibility(View.GONE);
                ll_head_list.setVisibility(View.GONE);
                lista.setVisibility(View.GONE);
                ll_question_4.setVisibility(View.VISIBLE);
                //save_reg.setVisibility(View.VISIBLE);
                break;
            case R.id._col2a:
                save_reg.setVisibility(View.VISIBLE);
                break;
            case R.id._col2b:
                save_reg.setVisibility(View.VISIBLE);
                break;
        }
    }

    public String getEMIS_code(){
        String getemiscode="";
        Conexion cnSET = new Conexion(this,STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        Cursor cur_data = dbSET.rawQuery("SELECT emis FROM ms_0", null);
        cur_data.moveToFirst();
        if (cur_data.getCount() > 0) {getemiscode = cur_data.getString(0);} else {getemiscode = "";}
        return getemiscode;
    }

    public String getGrade(String level, String id){
        String getSubject="";
        Conexion cnSET = new Conexion(this,STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        Cursor cur_data = dbSET.rawQuery("SELECT level, id, grade FROM grade WHERE level='"+level+"' AND id='"+id+"' ORDER BY level, id", null);
        cur_data.moveToFirst();
        if (cur_data.getCount() > 0) {getSubject = cur_data.getString(2);} else {getSubject = "";}
        return getSubject;
    }

    public String getGradeId(String level, String grade){
        String getSubject="";
        Conexion cnSET = new Conexion(this,STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        Cursor cur_data = dbSET.rawQuery("SELECT level, id, grade FROM grade WHERE level='"+level+"' AND grade='"+grade+"' ORDER BY level, id", null);
        cur_data.moveToFirst();
        if (cur_data.getCount() > 0) {getSubject = cur_data.getString(1);} else {getSubject = "";}
        return getSubject;
    }

    public String getSubject(String level, String subject){
        String getSubject="";
        Conexion cnSET = new Conexion(this,STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        Cursor cur_data = dbSET.rawQuery("SELECT level, id, subject FROM subject WHERE level='"+level+"' AND id='"+subject+"' ORDER BY level, id", null);
        cur_data.moveToFirst();
        if (cur_data.getCount() > 0) {getSubject = cur_data.getString(2);} else {getSubject = "";}
        return getSubject;
    }

    public String getSubjectId(String level, String subject){
        String getSubject="";
        Conexion cnSET = new Conexion(this,STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        SQLiteDatabase dbSET = cnSET.getReadableDatabase();
        Cursor cur_data = dbSET.rawQuery("SELECT level, id, subject FROM subject WHERE level='"+level+"' AND subject='"+subject+"' ORDER BY level, id", null);
        cur_data.moveToFirst();
        if (cur_data.getCount() > 0) {getSubject = cur_data.getString(1);} else {getSubject = "";}
        return getSubject;
    }
}

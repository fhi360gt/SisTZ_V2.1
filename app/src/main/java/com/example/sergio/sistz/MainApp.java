package com.example.sergio.sistz;

import android.app.Application;
import android.os.Environment;

import com.example.sergio.sistz.util.toolsfncs;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

import java.io.File;

/**
 * Created by pjyac on 20/03/2017.
 */
@ReportsCrashes(
        httpMethod = HttpSender.Method.PUT,
        reportType = HttpSender.Type.JSON,
        formUri = "http://fhi360bi.org:5984/acra-sis/_design/acra-storage/_update/report",
        formUriBasicAuthLogin = "root",
        formUriBasicAuthPassword = "piolin2010"
)

public class MainApp extends Application{
    File dbFile = new File (Environment.getExternalStorageDirectory() + File.separator + "sisdb" + File.separator + "sisdb.sqlite");
    @Override
    public void onCreate() {
        super.onCreate();

        ACRA.init(this);
        if (!dbFile.exists()) {}
        else {
            String emis = toolsfncs.getEMIS_code(this);
            ACRA.getErrorReporter().putCustomData("EMIS", emis);
        }
    }

}

package com.example.sergio.sistz.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Sergio on 2/22/2016.
 */
public class CopyAssetDBUtility {

    public static void copyDB (Context context,String dbName) {
        File dbFile = new File (Environment.getExternalStorageDirectory() + File.separator + "sisdb" + File.separator + dbName);
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "sisdb");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (!dbFile.exists()) {
            InputStream is = null;
            try {
                is = context.getAssets().open(dbName);
                OutputStream os = new FileOutputStream(dbFile);

                byte[] buffer = new byte[1024];
                while (is.read(buffer) > 0) {
                    os.write(buffer);
                }

                os.flush();
                os.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();

            }

        }

    }
}

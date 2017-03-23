package com.example.sergio.sistz;

/**
 * Created by Sergio on 4/14/2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.example.sergio.sistz.mysql.Conexion;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class APIUtility {
    //Datos Miembro
    private String data;
    @SuppressWarnings("unused")
    private boolean error;
    private HttpContext httpContext;
    private DefaultHttpClient httpClient;
    private HttpPost post;
    private HttpGet get;
    private boolean isPost;
    public static final String STATICS_ROOT = Environment.getExternalStorageDirectory() + File.separator + "sisdb";

    SQLiteDatabase dbSET;
    Conexion cnSET;

    public void saveData(String datos, Context context) {
        cnSET = new Conexion(context,STATICS_ROOT + File.separator + "sisdb.sqlite", null, 4);
        dbSET = cnSET.getReadableDatabase();
        ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
        data.add(new BasicNameValuePair("str", datos));
        //sendData("http://fhi360bi.org/user/tz_school/JSONaINSERT_v2.php", data, false, datos);
        //sendData("http://54.209.225.96/tz_school/JSONaINSERT_v3.php", data, false, datos);
        sendData("http://54.209.225.96/tz_school/JSONaINSERT_v4.php", data, false, datos);

    }



    private void sendData(String url,ArrayList<NameValuePair> params,boolean isPOST, String sms) {
        this.isPost = isPOST;
        httpContext  = new BasicHttpContext();
        httpClient = new DefaultHttpClient();
        //Log.d("DEBUG","Entro a sendData");
        if (isPost) {
            post = new HttpPost(url);
            //Log.e("URL DEBUG POST",url);
            try {
                post.setEntity(new UrlEncodedFormEntity(params));
                //      Log.d("DEBUG",params.toString());

            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            String query = URLEncodedUtils.format(params, "utf-8");
            //Log.e("DEBUG INFO", "params size : " + params);
            if (params.size()>0) {
                get = new HttpGet(url+"?"+query);
                //  Log.d("URL DEBUG GET", url+"?"+query );
            } else {
                get = new HttpGet(url);
                //Log.d("URL DEBUG GET", url);
            }
        }
        data=null;
        BasicHttpResponse response;
        InputStream is;
        try {
            if (isPost) {
                response = (BasicHttpResponse) httpClient.execute(post,httpContext);
            } else {
                response =  (BasicHttpResponse)httpClient.execute(get, httpContext);
            }
            is = response.getEntity().getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder respuesta = new StringBuilder();
            String linea;
            while ((linea = br.readLine()) != null) {
                respuesta.append(linea);
            }
            br.close();
            is.close();
            if (linea!="") {

                data= respuesta.toString();
                Log.d("JSONDATA", data);

                ContentValues sql = new ContentValues();
                sql.put("flag","0");
                dbSET.update("sisupdate", sql, "sis_sql='" + sms+"'", null);

            }
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            error = true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            error = true;

        }
    }

    public String getData() {

        return data;
    }
}

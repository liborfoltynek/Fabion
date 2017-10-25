package com.fotolibb.fabion;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

/**
 * Created by Libb on 19.10.2017.
 */

public class LoadDataLoginAsyncTask
        extends AsyncTask<String, String, String> {

    private String login;
    private String password;
    private TextView textView;
    private LoginActivity loginActivity;

    public FabionUser fabionUser;

    public LoadDataLoginAsyncTask(LoginActivity loginActivity, TextView textView, String login, String passwordHash) {
        this.login = login;
        this.password = passwordHash;
        this.textView = textView;
        this.loginActivity = loginActivity;
    }

    protected String doInBackground(String... arg0) {
        String response = null;
        String sJSON = null;
        InputStream in = null;

        try {
            String mainUrl = loginActivity.getResources().getString(R.string.url_fabion_service) + "trylogin.php?l=%s&p=%s";
            URL url = new URL(String.format(mainUrl, login, password));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String radek = null;

            while ((radek = reader.readLine()) != null) {
                sb.append(radek + "\n");
            }
            sJSON = sb.toString();
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
            //} catch (ProtocolException e) {
            //    Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        } finally {
            try {
                if (in != null) in.close();
            } catch (Exception e) {
                Log.e(TAG, "Exception: ");
                String s = e.getMessage();
                Log.e(TAG, "Exception: " + s);
            }
        }

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(sJSON);
            fabionUser = new FabionUser(
                    jsonObject.getString("login"),
                    jsonObject.getString("name"),
                    jsonObject.getString("phone"),
                    jsonObject.getString("email"),
                    jsonObject.getInt("freehours"),
                    jsonObject.getString("admin"),
                    jsonObject.getInt("ok"));
            loginActivity.setFabionUser(fabionUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sJSON;
    }
}



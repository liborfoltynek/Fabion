package com.fotolibb.fabion;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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

import static android.content.ContentValues.TAG;

/**
 * Created by Libb on 27.10.2017.
 */

public class DeleteEventAsyncTask
        extends AsyncTask<String, String, String> {

    public FabionUser fabionUser;
    private String login;
    private String password;
    private String eventId;
    private String servicesUrl;
    private OneDayEventsViewActivity callingActivity;

    public DeleteEventAsyncTask(String login, String passwordHash, String servicesUrl, String eventId, OneDayEventsViewActivity callingActivity) {
        this.login = login;
        this.password = passwordHash;
        this.eventId = eventId;
        this.servicesUrl = servicesUrl;
        this.callingActivity = callingActivity;
    }

    protected String doInBackground(String... arg0) {
        String sJSON = null;
        InputStream in = null;

        try {
            String mainUrl = servicesUrl + "event.php?action=d&l=%s&p=%s&id=%s";
            URL url = new URL(String.format(mainUrl, login, password, eventId));
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
            String result = jsonObject.getString("result");
            callingActivity.ProcessData(result, eventId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sJSON;
    }
}





package com.fotolibb.fabion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
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
import java.util.Calendar;

import static android.content.ContentValues.TAG;

/**
 * Created by Libb on 28.10.2017.
 */

public class UpdateEventAsyncTask
        extends AsyncTask<String, String, String> {

    public FabionEvent fabionEvent;
    private String login;
    private String password;
    private String servicesUrl;
    private EventDetailActivity callingActivity;

    public UpdateEventAsyncTask(String login, String passwordHash, String servicesUrl, FabionEvent fabionEvent, EventDetailActivity callingActivity) {
        this.login = login;
        this.password = passwordHash;

        this.servicesUrl = servicesUrl;
        this.callingActivity = callingActivity;
        this.fabionEvent = fabionEvent;
    }

    protected String doInBackground(String... arg0) {
        String sJSON = null;
        InputStream in = null;
        int initId = fabionEvent.getId();

        try {
            String mainUrl = servicesUrl + "event.php?l=%s&p=%s&id=%d&tf=%s&tt=%s&s=%s&n=%s&d=%d&m=%d&y=%d&action=";
            mainUrl += initId == 0 ? "n" : "u";
            URL url = new URL(String.format(mainUrl, login, password,
                    fabionEvent.getId(),
                    fabionEvent.getTimeFrom(),
                    fabionEvent.getTimeTo(),
                    fabionEvent.getSubject(),
                    fabionEvent.getNote(),
                    fabionEvent.getDay(),
                    fabionEvent.getMonth(),
                    fabionEvent.getYear()
            ));
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
            callingActivity.ProcessData(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (initId == 0)
        {
            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(callingActivity);
            String calendarName = mPrefs.getString("PREFS_KEY_CALENDAR_NAME", "");
            Log.i("CAL", calendarName);

            Calendar cal = Tools.getDateTime(fabionEvent);
            Calendar c2 = Tools.getTime(fabionEvent.getTimeTo());
            c2.set(fabionEvent.getYear(), fabionEvent.getMonth()-1,fabionEvent.getDay());

            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra("beginTime", cal.getTimeInMillis());
            intent.putExtra("allDay", false);
            intent.putExtra("endTime", c2.getTimeInMillis());
            intent.putExtra("title", fabionEvent.getNote());
            intent.putExtra("calendar_id",2);
            callingActivity.startActivity(intent);
        }

        return sJSON;
    }
}



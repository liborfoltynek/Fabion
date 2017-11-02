package com.fotolibb.fabion;

import android.os.AsyncTask;
import android.util.Log;

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

import static android.content.ContentValues.TAG;

/**
 * Created by Libb on 19.10.2017.
 */

public class LoadDataByMonthsAsyncTask
        extends AsyncTask<Object, Object, ArrayList> {

    private int mMonth;
    private int mYear;
    private String sURL;
    private ArrayList events;
    private IEventsConsumer callingActivity;

    public LoadDataByMonthsAsyncTask( int mMonth, int mYear, String sURL, FabionUser fabionUser, IEventsConsumer activity) {
        this.mMonth = mMonth;
        this.mYear = mYear;
        this.sURL = sURL;
        FabionUser fabionUser1 = fabionUser;
        events = new ArrayList();
        this.callingActivity = activity;
    }

    protected ArrayList doInBackground(Object... arg0) {
        String sJSON = null;
        InputStream in = null;

        try {
            URL url = new URL(String.format(sURL, mMonth + 1, mYear));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new
                    InputStreamReader(in, "UTF-8"), 8);
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
            JSONArray eventsJSONObject = jsonObject.getJSONArray("events");
            for (int i = 0; i < eventsJSONObject.length(); i++) {
                {
                    JSONObject jsonEventData = eventsJSONObject.getJSONObject(i);
                    FabionEvent fe = new FabionEvent(jsonEventData);
                    events.add(fe);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return events;
    }

    @Override
    protected void onPostExecute(ArrayList result) {
        callingActivity.ProcessData(events);
    }
}


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
 * Created by Libb on 02.11.2017.
 */

public class LoadUserMonthTimeAsyncTask
        extends AsyncTask<String, String, String> {

    private int mMonth;
    private int mYear;
    private String sURL;

    private IStringConsumer callingActivity;
    FabionUser fabionUser;

    public LoadUserMonthTimeAsyncTask( int mMonth, int mYear, String sURL, FabionUser fabionUser, IStringConsumer activity) {
        this.mMonth = mMonth;
        this.mYear = mYear;
        this.sURL = sURL;
        this.fabionUser = fabionUser;
        this.callingActivity = activity;
    }

    protected String doInBackground(String... arg0) {
        String sJSON = null;
        InputStream in = null;

        try {
            URL url = new URL(String.format(sURL, mMonth + 1, mYear, fabionUser.Login, fabionUser.PasswordHash));
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
        String result = null;
        try {
            jsonObject = new JSONObject(sJSON);
            result = jsonObject.getString("sum");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return result == null ? null : result.replace(".0","");//.replace(".5","½");
    }

    @Override
    protected void onPostExecute(String result) {
        callingActivity.ProcessData(result);
    }
}



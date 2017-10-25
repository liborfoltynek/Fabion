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


public class LoadDataByDaysAsyncTask
        extends AsyncTask<Object, Object, Object[]> {

    //private int delta = 0;
    //private int nStav = 0;
  //  private ViewFlipper flipper;
    private int mDay;
    private int mMonth;
    private int mYear;
    private String sURL;
    private ArrayList<FabionEvent> events;
    private FabionUser fabionUser;
    //private CollapsingToolbarLayout layout;
    private EventsByDaysScrollActivity callingActivity;

    public LoadDataByDaysAsyncTask(int mDay, int mMonth, int mYear, /*int delta, int nStav,*/ String sURL,/* ViewFlipper flipper, CollapsingToolbarLayout layout,*/ FabionUser fabionUser, EventsByDaysScrollActivity eventsByDaysScrollActivity) {
        //this.delta = delta;
//        this.flipper = flipper;
      //  this.layout = layout;
        this.mDay = mDay;
        this.mMonth = mMonth;
        this.mYear = mYear;
        this.sURL = sURL;
      //  this.nStav = nStav;
        this.fabionUser = fabionUser;
        this.callingActivity = eventsByDaysScrollActivity;
        events = new ArrayList();
    }

    protected Object[] doInBackground(Object... arg0) {
        String response = null;
        String sJSON = null;
        InputStream in = null;

        try {
            URL url = new URL(String.format(sURL, mDay, mMonth, mYear));
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
            events = new ArrayList<FabionEvent>();
            for (int i = 0; i < eventsJSONObject.length(); i++) {
                {
                    JSONObject event = eventsJSONObject.getJSONObject(i);
                    FabionEvent fe = new FabionEvent();
                    fe.Day = event.getInt("day");
                    fe.Month = event.getInt("month");
                    fe.Year = event.getInt("year");
                    fe.Login = event.getString("login");
                    fe.TimeFrom = event.getString("timefrom");
                    fe.TimeTo = event.getString("timeto");
                    fe.Subject = event.getString("subject");
                    fe.Note = event.getString("note");

                    events.add(fe);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return events.toArray();
    }

    protected void onPostExecute(Object[] result) {

        callingActivity.GenerateDayReservationsInfo(events);

       /* StringBuilder sb = new StringBuilder();
        for (int i = 0; i < events.toArray().length; i++) {
            sb.append(((FabionEvent) events.toArray()[i]).getString(fabionUser));
        }
        prepniLayout(delta, layout, String.format("%d.%d.%d", mDay, mMonth, mYear), sb.toString());
    }

    public void prepniLayout(int d, CollapsingToolbarLayout l, String title, String text) {
        // nStav = switchTo;
        l.setTitle(title);

        if (d == 1) {
            flipper.setInAnimation(animZprava());
            flipper.setOutAnimation(animZlava());
        } else {
            flipper.setInAnimation(animZprava1());
            flipper.setOutAnimation(animZlava1());
        }

        LinearLayout linearLayout;
        if (nStav == 0) {
            //  ((TextView) flipper.findViewById(R.id.textView1)).setText(text);
            linearLayout = (LinearLayout) callingActivity.findViewById(R.id.linearLayout1);
        } else {
            //    ((TextView) flipper.findViewById(R.id.textView2)).setText(text);
            linearLayout = (LinearLayout) callingActivity.findViewById(R.id.linearLayout2);
        }

        linearLayout.removeAllViews();

        TextView t = new TextView(callingActivity);
        t.setText(text);
        linearLayout.addView(t);

        flipper.showNext();
    }

    private Animation animZprava() {
        Animation zprava = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        zprava.setDuration(250);
        zprava.setInterpolator(new LinearInterpolator());
        return zprava;
    }

    private Animation animZlava() {
        Animation zlava = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        zlava.setDuration(250);
        zlava.setInterpolator(new LinearInterpolator());
        return zlava;
    }

    private Animation animZprava1() {
        Animation zprava = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        zprava.setDuration(250);
        zprava.setInterpolator(new LinearInterpolator());
        return zprava;
    }

    private Animation animZlava1() {
        Animation zlava = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        zlava.setDuration(250);
        zlava.setInterpolator(new LinearInterpolator());
        return zlava;
        */
    }

}


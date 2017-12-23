package com.fotolibb.fabion;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.util.Log;

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
import java.net.URLEncoder;
import java.util.TimeZone;

import static android.content.ContentValues.TAG;
import static com.fotolibb.fabion.SettingsActivity.PREFS_KEY_CALENDAR_ID;
import static com.fotolibb.fabion.SettingsActivity.PREFS_KEY_CALENDAR_NAME;

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
            if (initId == 0) { // new event
                fabionEvent.setCalendarEventId(addEvent(fabionEvent));
            } else {
                updateEvent(fabionEvent);
            }

            String mainUrl = servicesUrl + "event.php?l=%s&p=%s&id=%d&tf=%s&tt=%s&s=%s&n=%s&d=%d&m=%d&y=%d&cid=%d&action=";
            mainUrl += initId == 0 ? "n" : "u";
            URL url = new URL(String.format(mainUrl, login, password,
                    fabionEvent.getId(),
                    fabionEvent.getTimeFrom(),
                    fabionEvent.getTimeTo(),
                    URLEncoder.encode(fabionEvent.getSubject(), "utf-8"),
                    URLEncoder.encode(fabionEvent.getNote(), "utf-8"),
                    fabionEvent.getDay(),
                    fabionEvent.getMonth(),
                    fabionEvent.getYear(),
                    fabionEvent.getCalendarEventId()
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

        return sJSON;
    }

    private int updateEvent(FabionEvent fe) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(callingActivity);
        String calendarName = mPrefs.getString(PREFS_KEY_CALENDAR_NAME, "");
        long calendarId = mPrefs.getLong(PREFS_KEY_CALENDAR_ID, -1);

        ContentValues l_event = new ContentValues();
        //l_event.put("calendar_id", calendarId);
        if (fe.getNote().length() > 0) {
            l_event.put("title", "\uD83D\uDCF7 " + fe.getNote());
        } else {
            l_event.put("title", "\uD83D\uDCF7 " + fe.getSubject());
        }
        //l_event.put("description", fe.getNote());

        l_event.put("dtstart", Tools.getDateTimeFrom(fe).getTimeInMillis());
        l_event.put("dtend", Tools.getDateTimeTo(fe).getTimeInMillis());
        l_event.put("eventTimezone", TimeZone.getTimeZone("Europe/Prague").getID());
        l_event.put("allDay", 0);
        //status: 0~ tentative; 1~ confirmed; 2~ canceled
        Uri l_eventUri;
        if (Build.VERSION.SDK_INT >= 8) {
            l_eventUri = Uri.parse("content://com.android.calendar/events/" + Integer.toString(fe.getCalendarEventId()));
        } else {
            l_eventUri = Uri.parse("content://calendar/events/" + Integer.toString(fe.getCalendarEventId()));
        }
        l_eventUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, fe.getCalendarEventId());

        int i = callingActivity.getContentResolver().update(l_eventUri, l_event, null, null);
        return i;
    }

    private int addEvent(FabionEvent fe) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(callingActivity);
        String calendarName = mPrefs.getString(PREFS_KEY_CALENDAR_NAME, "");
        long calendarId = mPrefs.getLong(PREFS_KEY_CALENDAR_ID, -1);
        Log.i("CAL", calendarName);
        ContentValues l_event = new ContentValues();
        l_event.put("calendar_id", calendarId);
        if (fe.getNote().length() > 0) {
            l_event.put("title", "\uD83D\uDCF7 " + fe.getNote());
        } else {
            l_event.put("title", "\uD83D\uDCF7 " + fe.getSubject());
        }
        //l_event.put("description", fe.getNote());
        l_event.put("eventLocation", "Pohankova 8, Brno");
        l_event.put("dtstart", Tools.getDateTimeFrom(fe).getTimeInMillis());
        l_event.put("dtend", Tools.getDateTimeTo(fe).getTimeInMillis());
        l_event.put("eventTimezone", TimeZone.getTimeZone("Europe/Prague").getID());
        l_event.put("allDay", 0);
        //status: 0~ tentative; 1~ confirmed; 2~ canceled
        l_event.put("eventStatus", 1);
        l_event.put("hasAlarm", 1);
        Uri l_eventUri;
        if (Build.VERSION.SDK_INT >= 8) {
            l_eventUri = Uri.parse("content://com.android.calendar/events");
        } else {
            l_eventUri = Uri.parse("content://calendar/events");
        }
        Uri l_uri = callingActivity.getContentResolver().insert(l_eventUri, l_event);
        Log.i("CAL", l_uri.toString());

        return Integer.parseInt(l_uri.getLastPathSegment());
    }
}



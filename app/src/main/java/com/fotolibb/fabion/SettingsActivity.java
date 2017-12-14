package com.fotolibb.fabion;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    public static final String PREFS_KEY_CALENDAR_ID = "PREFS_KEY_CALENDAR_ID";
    public static final String PREFS_KEY_CALENDAR_NAME = "PREFS_KEY_CALENDAR_NAME";

    public static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 1

    };
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ((Button) findViewById(R.id.btSettingsOK)).setOnClickListener(new View.OnClickListener() {
                                                                          @Override
                                                                          public void onClick(View view) {
                                                                              onBtPressed(view);
                                                                          }
                                                                      }
                                                                    );
        run();
    }

    private void run() {
        Cursor cur = null;
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;

        try {
            @SuppressLint("MissingPermission") Cursor cursor = cur = cr.query(uri, EVENT_PROJECTION, null, null, null);

            RadioGroup rg = (RadioGroup) findViewById(R.id.settingsCalendarsGroup);

            while (cur.moveToNext()) {
                long calID = 0;
                String displayName = null;

                // Get the field values
                displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
                Log.i("CAL", String.format("[%d] : %s", cur.getInt(PROJECTION_ID_INDEX), displayName));

                RadioButton rb = new RadioButton(getApplicationContext());
                rb.setText(displayName);
                rg.addView(rb);
            }
        } catch (Exception ex) {
            Log.e("EX", ex.getMessage());
        }
    }

    private void onBtPressed(View view) {

        RadioGroup radioButtonGroup = (RadioGroup) findViewById(R.id.settingsCalendarsGroup);
        int radioButtonID = radioButtonGroup.getCheckedRadioButtonId();
        if (radioButtonID != -1) {
            if (radioButtonID != R.id.rbNoEvent) {
                View radioButton = radioButtonGroup.findViewById(radioButtonID);
                int idx = radioButtonGroup.indexOfChild(radioButton);
                RadioButton r = (RadioButton) radioButtonGroup.getChildAt(idx);
                String selectedtext = r.getText().toString();
                SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                prefsEditor.putString(PREFS_KEY_CALENDAR_NAME, selectedtext);
                prefsEditor.apply();
            } else {
                SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                prefsEditor.remove(PREFS_KEY_CALENDAR_NAME);
                prefsEditor.apply();
            }
        }
    }
}

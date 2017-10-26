package com.fotolibb.fabion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Calendar;

public class OneDayEventsView extends AppCompatActivity implements IEventsConsumer {
    private int mDay;
    private int mMonth;
    private int mYear;
    private FabionUser fabionUser;
    private String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Calendar c = Calendar.getInstance();
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mMonth = c.get(Calendar.MONTH) + 1;
        mYear = c.get(Calendar.YEAR);

        URL = getResources().getString(R.string.url_fabion_service) + "getday.php?d=%d&m=%d&y=%d";
        Intent i = getIntent();
        fabionUser = i.getExtras().getParcelable("FUser");
        int d = i.getExtras().getInt("Day");
        int m = i.getExtras().getInt("Month");
        int y = i.getExtras().getInt("Year");
        if ((d != 0) && (m != 0) && (y != 0)) {
            mDay = d;
            mMonth = m;
            mYear = y;
        }

        new LoadDataByDaysAsyncTask(mDay, mMonth, mYear, URL, fabionUser, this).execute();
    }


    @Override
    public void ProcessData(ArrayList<FabionEvent> events) {

    }
}


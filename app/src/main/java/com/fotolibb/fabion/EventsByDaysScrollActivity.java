package com.fotolibb.fabion;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.Calendar;

import static com.fotolibb.fabion.R.menu.main;

public class EventsByDaysScrollActivity extends AppCompatActivity {

    EventsByDaysScrollActivity mainView;
    String URL;
    FabionUser fabionUser;
    GestureDetector gestDetector;
    ViewFlipper flipper;
    private int mDay;
    private int mMonth;
    private int mYear;
    private int nStav = 1;
    private int delta = 1;
    private LinearLayout linearLayout1, linearLayout2;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_by_days_scroll);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainView = this;

        initiateDates();
        flipper = (ViewFlipper) findViewById(R.id.view_flipper);
        linearLayout1 = (LinearLayout) flipper.findViewById(R.id.linearLayout1);
        linearLayout2 = (LinearLayout) flipper.findViewById(R.id.linearLayout2);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle("---");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mDay = c.get(Calendar.DAY_OF_MONTH);
                mMonth = c.get(Calendar.MONTH) + 1;
                mYear = c.get(Calendar.YEAR);
                new LoadDataByDaysAsyncTask(mDay, mMonth, mYear, URL, fabionUser, mainView).execute();
            }
        });


        fabionUser = new FabionUser();
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

        gestDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2,
                                           float rychlostX, float rychlostY) {
                        //Toast.makeText(mainView, String.format("%f : %f",rychlostX ,rychlostY), Toast.LENGTH_SHORT).show();
                        if (rychlostY < 3000 && rychlostY > -3000) {

                            if (rychlostX < -10.0f) {
                                mDay++;
                                delta = 1;
                                new LoadDataByDaysAsyncTask(mDay, mMonth, mYear, URL, fabionUser, mainView).execute();
                                nStav = nStav == 0 ? 1 : 0;
                            }

                            if (rychlostX > 10.0f) {
                                mDay--;
                                delta = -1;
                                new LoadDataByDaysAsyncTask(mDay, mMonth, mYear, URL, fabionUser, mainView).execute();
                                nStav = nStav == 0 ? 1 : 0;
                            }
                        }
                        return true;
                    }
                });
        new LoadDataByDaysAsyncTask(mDay, mMonth, mYear, URL, fabionUser, this).execute();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        return gestDetector.onTouchEvent(ev);
    }

    protected void initiateDates() {
        final Calendar c = Calendar.getInstance();
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mMonth = c.get(Calendar.MONTH) + 1;
        mYear = c.get(Calendar.YEAR);
    }


    public void GenerateDayReservationsInfo(ArrayList<FabionEvent> events) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < events.toArray().length; i++) {
            sb.append(((FabionEvent) events.toArray()[i]).getString(fabionUser));
        }
        String title = String.format("%d.%d.%d", mDay, mMonth, mYear);
        collapsingToolbarLayout.setTitle(title);

        LinearLayout linearLayout;
        if (nStav == 0) {
            linearLayout = linearLayout1;
        } else {
            linearLayout = linearLayout2;
        }

        if (delta == 1) {
            flipper.setInAnimation(animZprava());
            flipper.setOutAnimation(animZlava());
        } else {
            flipper.setInAnimation(animZprava1());
            flipper.setOutAnimation(animZlava1());
        }

        linearLayout.removeAllViews();

        TextView t = new TextView(this);
        t.setText(sb.toString());
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
    }

}

package com.fotolibb.fabion;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.Calendar;

public class EventsByMonthsScrollingActivity extends AppCompatActivity implements IEventsConsumer {


    private int month;
    private int year;
    private FabionUser fabionUser;
    private GestureDetector gestDetector;
    private TableLayout tableLayout1, tableLayout2;
    private EventsByMonthsScrollingActivity mainActivity;
    private ArrayList<FabionEvent> events;
    private ViewFlipper flipper;
    private CollapsingToolbarLayout toolbarLayout;
    private int nStav = 1;
    private int delta = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_by_months_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainActivity = this;
        flipper = (ViewFlipper) findViewById(R.id.view_flipperMonth);
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabMonth);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });

        Intent i = getIntent();
        fabionUser = i.getExtras().getParcelable("FUser");

        Calendar c = Calendar.getInstance();
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);

        tableLayout1 = flipper.findViewById(R.id.calendarTableScroll1);
        tableLayout2 = flipper.findViewById(R.id.calendarTableScroll2);
        toolbarLayout.setTitle(String.format("%d/%d [%s]", month + 1, year, fabionUser.isLogged() ? fabionUser.Login : ""));

        gestDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2,
                                           float rychlostX, float rychlostY) {
                        //Toast.makeText(mainView, String.format("%f : %f",rychlostX ,rychlostY), Toast.LENGTH_SHORT).show();
                        if (rychlostY < 3000 && rychlostY > -3000) {

                            if (rychlostX < -10.0f) {
                                delta = 1;
                                month++;
                                if (month > 11) {
                                    month = 0;
                                    year++;
                                }
                                //new LoadDataByDaysAsyncTask(mDay, mMonth, mYear, 1, nStav, URL, flipper, lay, fabionUser).execute();
                                new LoadDataByMonthsAsyncTask(month, year, getResources().getString(R.string.url_fabion_service) + "getday.php?m=%d&y=%d", fabionUser, mainActivity).execute();
                                nStav = nStav == 0 ? 1 : 0;
                            }

                            if (rychlostX > 10.0f) {
                                //mDay--;
                                //new LoadDataByDaysAsyncTask(mDay, mMonth, mYear, -1, nStav, URL, flipper, lay, fabionUser).execute();
                                month--;
                                delta = -1;
                                if (month < 0) {
                                    month = 11;
                                    year--;
                                }
                                new LoadDataByMonthsAsyncTask(month, year, getResources().getString(R.string.url_fabion_service) + "getday.php?m=%d&y=%d", fabionUser, mainActivity).execute();
                                nStav = nStav == 0 ? 1 : 0;
                            }
                        }
                        return true;
                    }
                });

        new LoadDataByMonthsAsyncTask(month, year, getResources().getString(R.string.url_fabion_service) + "getday.php?m=%d&y=%d", fabionUser, this).execute();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        return gestDetector.onTouchEvent(ev);
    }

    public void cellOnClick(View view) {
        try {
            LinearLayout b = (LinearLayout) view;
            TextView aa = (TextView) b.getChildAt(0);
            Integer day = Integer.parseInt(aa.getText().toString());

            Intent intent = new Intent(getApplicationContext(), OneDayEventsViewActivity.class);
            intent.putExtra("FUser", fabionUser);
            intent.putExtra("Day", day);
            intent.putExtra("Month", month + 1);
            intent.putExtra("Year", year);
            startActivity(intent);

        } catch (Exception ex) {
            Log.e("EX", ex.getMessage());
        }
    }

    @Override
    public void ProcessData(ArrayList<FabionEvent> events) {
        this.events = events;

        toolbarLayout.setTitle(String.format("%d/%d %s", month + 1, year, fabionUser.isLogged() ? String.format("[%s]", fabionUser.Login) : ""));

        TableLayout tableLayout;
        if (nStav == 0)
            tableLayout = tableLayout1;
        else
            tableLayout = tableLayout2;

        try {
            tableLayout.removeAllViews();
        } catch (Exception ex) {
            Log.e("EX", ex.getMessage());
        }

        Calendar c = Calendar.getInstance();
        int today = c.get(Calendar.DAY_OF_MONTH);
        int tomonth = c.get(Calendar.MONTH);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.YEAR, year);
        int day = c.get(Calendar.DAY_OF_WEEK);

        if (day != 1) {
            day = -day + 3;
        } else {
            day = -5;
        }

        TableRow tr = new TableRow(this);
        InitDaysHeader(tr, tableLayout);

        for (int w = 0; w < 6; w++) {
            tr = new TableRow(this);
            TableRow.LayoutParams tp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            tr.setLayoutParams(tp);

            for (int d = 0; d < 7; d++) {
                LinearLayout l = new LinearLayout(this);
                TableRow.LayoutParams llp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                l.setOrientation(LinearLayout.VERTICAL);
                l.setPadding(4, 0, 0, 2);
                l.setLayoutParams(llp);

                TextView tvDate = new TextView(this);

                if ((day > 0) && (day <= c.getActualMaximum(Calendar.DAY_OF_MONTH))) {
                    l.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.cell, null));

                    l.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            cellOnClick(view);
                        }
                    });

                    tvDate.setTextSize(24);
                    tvDate.setGravity(Gravity.TOP | Gravity.RIGHT);
                    tvDate.setText(Integer.toString(day));
                    tvDate.setPadding(0, 2, 12, 0);
                    if (d == 6) {
                        tvDate.setTextColor(Color.RED);
                    }
                    if ((month == tomonth) && (day == today)) {
                        tvDate.setTypeface(null, Typeface.BOLD);
                    }
                    l.addView(tvDate);

                    TextView tvReservations = new TextView(this);
                    tvReservations.setText(getDayText(day));
                    tvReservations.setTextSize(10);
                    tvReservations.setPadding(2, 0, 0, 2);
                    tvReservations.setGravity(Gravity.LEFT);
                    l.addView(tvReservations);
                }

                day++;
                tr.addView(l);
            }
            tableLayout.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        }

        if (delta == 1) {
            flipper.setInAnimation(Animations.animZprava());
            flipper.setOutAnimation(Animations.animZlava());
        } else {
            flipper.setInAnimation(Animations.animZprava1());
            flipper.setOutAnimation(Animations.animZlava1());
        }
        flipper.showNext();
    }

    private void InitDaysHeader(TableRow tr, TableLayout tableLayout) {
        tr.addView(getDayInWeekHeaderCell("Po"));
        tr.addView(getDayInWeekHeaderCell("Út"));
        tr.addView(getDayInWeekHeaderCell("St"));
        tr.addView(getDayInWeekHeaderCell("Čt"));
        tr.addView(getDayInWeekHeaderCell("Pá"));
        tr.addView(getDayInWeekHeaderCell("So"));
        tr.addView(getDayInWeekHeaderCell("Ne"));
        tableLayout.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
    }

    private TextView getDayInWeekHeaderCell(String day) {
        TextView t = new TextView(this);
        t.setText(day);
        t.setGravity(Gravity.CENTER);
        if (day.equals("Ne")) {
            t.setTypeface(null, Typeface.BOLD);
            t.setTextColor(Color.RED);
        }
        return t;
    }

    private String getDayText(int d) {
        StringBuilder sb = new StringBuilder();
        boolean b = false;
        for (int i = 0; i < events.toArray().length; i++) {
            if (((FabionEvent) events.toArray()[i]).getDay() == d) {
                if (b)
                    sb.append("\n");
                if (!b) {
                    b = true;
                }
                if (fabionUser.isLogged()) {
                    sb.append(((FabionEvent) events.toArray()[i]).getLogin());
                } else {
                    sb.append("***");
                }
            }
        }
        return sb.toString();
    }


}

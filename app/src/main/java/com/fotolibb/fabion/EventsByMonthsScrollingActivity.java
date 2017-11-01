package com.fotolibb.fabion;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.Calendar;

import static com.fotolibb.fabion.Constants.FAB_USER;

public class EventsByMonthsScrollingActivity extends AppCompatActivity implements IEventsConsumer {
    private int month;
    private int year;
    private FabionUser fabionUser;
    private GestureDetector gestDetector;
    private TableLayout tableLayout1, tableLayout2;
    private EventsByMonthsScrollingActivity mainActivity;
    private ArrayList<FabionEvent> events;
    private ViewFlipper flipper;
    ProgressBar progressBar;
    private int nStav = 1;
    private int delta = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_by_months_scrolling);

        mainActivity = this;
        flipper = (ViewFlipper) findViewById(R.id.view_flipperMonth);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabMonth);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                PrepareNewEvent();

            }
        });

        Intent i = getIntent();
        fabionUser = i.getExtras().getParcelable("FUser");

        Calendar c = Calendar.getInstance();
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);

        tableLayout1 = flipper.findViewById(R.id.calendarTableScroll1);
        tableLayout2 = flipper.findViewById(R.id.calendarTableScroll2);
        this.setTitle(String.format("%d/%d [%s]", month + 1, year, fabionUser.isLogged() ? fabionUser.Login : ""));

        gestDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2,
                                           float rychlostX, float rychlostY) {
                        if (rychlostY < 3000 && rychlostY > -3000) {

                            if (rychlostX < -10.0f) {
                                delta = 1;
                                month++;
                                if (month > 11) {
                                    month = 0;
                                    year++;
                                }
                                progressBar.setVisibility(View.VISIBLE);
                                nStav = nStav == 0 ? 1 : 0;
                                new LoadDataByMonthsAsyncTask(month, year, Constants.getUrlService() + "getday.php?m=%d&y=%d", fabionUser, mainActivity).execute();
                            }

                            if (rychlostX > 10.0f) {
                                month--;
                                delta = -1;
                                if (month < 0) {
                                    month = 11;
                                    year--;
                                }
                                nStav = nStav == 0 ? 1 : 0;
                                progressBar.setVisibility(View.VISIBLE);
                                new LoadDataByMonthsAsyncTask(month, year, Constants.getUrlService() + "getday.php?m=%d&y=%d", fabionUser, mainActivity).execute();
                            }
                        }
                        return true;
                    }
                });

        nStav = 0;
        delta = 0;
        progressBar.setVisibility(View.VISIBLE);
        new LoadDataByMonthsAsyncTask(month, year, Constants.getUrlService() + "getday.php?m=%d&y=%d", fabionUser, this).execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (fabionUser != null) {
            outState.putParcelable(FAB_USER, fabionUser);
        }
        outState.putInt("MONTH", month);
        outState.putInt("YEAR", year);
        outState.putInt("NSTAV", nStav);
        outState.putInt("DELTA", delta);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        FabionUser f = savedInstanceState.getParcelable(FAB_USER);
        if (f != null) {
            fabionUser = f;
        }

        month = savedInstanceState.getInt("MONTH");
        year = savedInstanceState.getInt("YEAR");
        delta = savedInstanceState.getInt("DELTA");
        nStav = savedInstanceState.getInt("NSTAV");
        delta = 0;
        progressBar.setVisibility(View.VISIBLE);
        new LoadDataByMonthsAsyncTask(month, year, Constants.getUrlService() + "getday.php?m=%d&y=%d", fabionUser, this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_events_by_months_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.menuLogin) {
            Login();
        } else if (id == R.id.menuLogout) {
            Logout();
        }

        return super.onOptionsItemSelected(item);
    }

    private void Login() {
        Login(true);
    }

    private void Login(Boolean showAlreadyLoggedMessage) {
        if (fabionUser.isLogged() && showAlreadyLoggedMessage) {
            Toast.makeText(this, R.string.action_sign_alreadylogged, Toast.LENGTH_SHORT).show();
        } else {
            try {
                Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(intent2, Constants.RO_LOGIN);
            } catch (Exception ex) {
                Log.e("EX", ex.getMessage());
            }
        }
    }

    private void Logout() {
        fabionUser = new FabionUser();
        Login();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        return gestDetector.onTouchEvent(ev);
    }

    public void cellOnClick(View view) {
        try {

            if (fabionUser.isLogged()) {

                LinearLayout b = (LinearLayout) view;
                TextView aa = (TextView) b.getChildAt(0);
                Integer day = Integer.parseInt(aa.getText().toString());

                Intent intent = new Intent(getApplicationContext(), OneDayEventsViewActivity.class);
                intent.putExtra("FUser", fabionUser);
                intent.putExtra("Day", day);
                intent.putExtra("Month", month + 1);
                intent.putExtra("Year", year);
                startActivityForResult(intent, Constants.RC_EVENT_UPDATE);
            } else {
                Toast.makeText(getApplicationContext(), "Pro zobrazení detailů se musíte přihlásit", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {
            Log.e("EX", ex.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == Constants.RC_EVENT_UPDATE || requestCode == Constants.RC_EVENT_NEW) && resultCode == RESULT_OK) {
            delta = 0;
            new LoadDataByMonthsAsyncTask(month, year, Constants.getUrlService() + "getday.php?m=%d&y=%d", fabionUser, this).execute();
        }

        if (requestCode == Constants.RO_LOGIN) {
            if (resultCode == RESULT_OK) {
                fabionUser = data.getExtras().getParcelable("FUser");
                delta = 0;
                new LoadDataByMonthsAsyncTask(month, year, Constants.getUrlService() + "getday.php?m=%d&y=%d", fabionUser, this).execute();
            } else if (resultCode == RESULT_FIRST_USER + 1) {
                fabionUser = new FabionUser();
                Intent i = new Intent();
                i.putExtra("FUser", new FabionUser());
                i.putExtra("MONTH", month);
                i.putExtra("YEAR", year);
                setResult(RESULT_OK, i);
                finish();
            }
        }
    }

    @Override
    public void ProcessData(ArrayList<FabionEvent> events) {
        this.events = events;
        progressBar.setVisibility(View.GONE);
        this.setTitle(String.format("%d/%d %s", month + 1, year, fabionUser.isLogged() ? String.format("[%s]", fabionUser.Login) : ""));

        TableLayout tableLayout;
        //  if (nStav == 0)
        tableLayout = tableLayout1;
        //  else
        //     tableLayout = tableLayout2;

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

        renderCalendar(tableLayout, c, today, tomonth, day);

        if (delta == 1) {
            flipper.setInAnimation(Animations.animZprava());
            flipper.setOutAnimation(Animations.animZlava());
        } else {
            flipper.setInAnimation(Animations.animZprava1());
            flipper.setOutAnimation(Animations.animZlava1());
        }

        // if (delta != 0) {
        //     flipper.showNext();
        // }
    }

    private void renderCalendar(TableLayout tableLayout, Calendar c, int today, int tomonth, int day) {
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


                    LinearLayout lDay = new LinearLayout(this);
                    lDay.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    lDay.setOrientation(LinearLayout.VERTICAL);
                    lDay.setPadding(0, 0, 0, 0);
                    lDay.setLayoutParams(llp);

                    ArrayList<View> texts = getDayText(day);
                    for (int i = 0; i < texts.size(); i++) {
                        lDay.addView(texts.get(i));
                    }
                    l.addView(lDay);
                }

                day++;
                tr.addView(l);
            }
            tableLayout.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        }
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

    private ArrayList<View> getDayText(int d) {
        ArrayList<View> views = new ArrayList<View>();
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getDay() == d) {
                TextView t = new TextView(getApplicationContext());
                if (fabionUser.isLogged()) {
                    FabionEvent fe = events.get(i);
                    t.setTextSize(11);
                    t.setText(fe.getLogin());
                    t.setPadding(3, 0, 0, 0);
                    t.setGravity(Gravity.LEFT);
                    if (fe.getLogin().equalsIgnoreCase(fabionUser.Login)) {
                        t.setTextColor(Color.argb(255, 99, 99, 255));
                    }
                    views.add(t);

                    TextView tt = new TextView(getApplicationContext());
                    tt.setText(String.format("%s-%s", fe.getTimeFrom(), fe.getTimeTo()));
                    tt.setTextSize(9);
                    TextView ttt = new TextView(getApplicationContext());
                    ttt.setText("  ");
                    ttt.setGravity(Gravity.CENTER);
                    ttt.setTextSize(9);
                    ttt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    LinearLayout sp = new LinearLayout(getApplicationContext());
                    sp.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                    sp.setPadding(50, 0, 50, 0);
                    sp.setBackgroundColor(Color.BLACK);
                    sp.setGravity(Gravity.CENTER);
                    sp.addView(ttt);

                    views.add(tt);
                    views.add(sp);
                } else {
                    t.setText("***");
                    views.add(t);
                }
            }
        }
        return views;
    }

    private void PrepareNewEvent() {
        Intent intent = new Intent(getApplicationContext(), EventDetailActivity.class);
        intent.putExtra("FUser", fabionUser);
        intent.putExtra("FEvent", FabionEvent.CreateNew(fabionUser));
        startActivityForResult(intent, Constants.RC_EVENT_NEW);
    }
}

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
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.Calendar;

import static com.fotolibb.fabion.Constants.FAB_USER;
import static com.fotolibb.fabion.Constants.PAR_FEVENT;
import static com.fotolibb.fabion.Constants.PAR_FUSER;

public class EventsByMonthsScrollingActivity extends AppCompatActivity implements IEventsConsumer, IStringConsumer {
    ProgressBar progressBar;
    private int month;
    private int year;
    private FabionUser fabionUser;
    private GestureDetector gestDetector;
    private TableLayout tableLayout1, tableLayout2;
    private EventsByMonthsScrollingActivity mainActivity;
    private ArrayList<FabionEvent> events;
    private ViewFlipper flipper;
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
                PrepareNewEvent();
            }
        });

        Intent i = getIntent();
        fabionUser = i.getExtras().getParcelable(PAR_FUSER);

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
                                           float deltaX, float deltaY) {
                        if (deltaY < 3000 && deltaY > -3000) {

                            if (deltaX < -10.0f) {
                                delta = 1;
                                month++;
                                if (month > 11) {
                                    month = 0;
                                    year++;
                                }
                                LoadData(mainActivity);
                            }

                            if (deltaX > 10.0f) {
                                month--;
                                delta = -1;
                                if (month < 0) {
                                    month = 11;
                                    year--;
                                }
                                LoadData(mainActivity);
                            }
                        }
                        return true;
                    }
                });

        delta = 0;
        LoadData(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (fabionUser != null) {
            outState.putParcelable(FAB_USER, fabionUser);
        }
        outState.putInt("MONTH", month);
        outState.putInt("YEAR", year);
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
        delta = 0;
        LoadData(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //TODO: finish menu
        getMenuInflater().inflate(R.menu.menu_events_by_months_scrolling, menu);
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId())
        {
            case R.id.menuLogout:
                Logout();
                break;
            case R.id.action_switchlogin:
                Logout();
                break;
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
                Log.e(getString(R.string.TAG_EX), ex.getMessage());
            }
        }
    }

    private void Logout() {
        fabionUser = new FabionUser();
        Login();
    }

    protected void LoadData(EventsByMonthsScrollingActivity activity) {
        progressBar.setVisibility(View.VISIBLE);
        new LoadDataByMonthsAsyncTask(month, year, Constants.getUrlService() + "getday.php?m=%d&y=%d&l=%s&p=%s", fabionUser, activity).execute();
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
                intent.putExtra(PAR_FUSER, fabionUser);
                intent.putExtra("Day", day);
                intent.putExtra("Month", month + 1);
                intent.putExtra("Year", year);
                startActivityForResult(intent, Constants.RC_EVENT_UPDATE);
            } else {
                Toast.makeText(getApplicationContext(), R.string.MUST_LOGIN_FOR_DETAILS, Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {
            Log.e(getString(R.string.TAG_EX), ex.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == Constants.RC_EVENT_UPDATE || requestCode == Constants.RC_EVENT_NEW) && resultCode == RESULT_OK) {
            delta = 0;
            LoadData(this);
        }

        if (requestCode == Constants.RO_LOGIN) {
            if (resultCode == RESULT_OK) {
                fabionUser = data.getExtras().getParcelable(PAR_FUSER);
                delta = 0;
                LoadData(this);
            } else if (resultCode == RESULT_FIRST_USER + 1) {
                fabionUser = new FabionUser();
                Intent i = new Intent();
                i.putExtra(PAR_FUSER, new FabionUser());
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

        TableLayout tableLayout = tableLayout1;
        if (delta != 0) {
            ScrollView sv = (ScrollView) flipper.getCurrentView();
            if (sv.getChildAt(0).getId() == R.id.calendarTableScroll1) {
                tableLayout = tableLayout2;
            } else {
                tableLayout = tableLayout1;
            }

            if (delta == 1) {
                flipper.setInAnimation(Animations.animZprava());
                flipper.setOutAnimation(Animations.animZlava());
            } else {
                flipper.setInAnimation(Animations.animZprava1());
                flipper.setOutAnimation(Animations.animZlava1());
            }
        }

        tableLayout.removeAllViews();
        renderCalendar(tableLayout);

        if (delta != 0) {
            flipper.showNext();
        }

        new LoadUserMonthTimeAsyncTask(month, year, Constants.getUrlService() + "usermonthtime.php?m=%d&y=%d&l=%s&p=%s", fabionUser, mainActivity).execute();
    }

    @Override
    public void ProcessData(String result) {
        //Toast.makeText(getApplicationContext(), String.format("Hodin tento mesic: %s", result), Toast.LENGTH_SHORT).show();
        //this.setTitle(this.getTitle() + String.format(" (%s)", result));
        String[] mesice = getResources().getStringArray(R.array.mesice);

        String hours = result != null ? String.format(", %sh.", result) : "";
        this.setTitle(String.format("%s %d %s", mesice[month], year, fabionUser.isLogged() ? String.format("[%s%s]", fabionUser.Login, hours) : ""));
    }

    private void renderCalendar(TableLayout tableLayout) {
        InitDaysHeader(tableLayout);
        RenderMonth(tableLayout);
    }

    private void RenderMonth(TableLayout tableLayout) {
        Calendar c = Calendar.getInstance();
        int thisDay = c.get(Calendar.DAY_OF_MONTH);
        int thisMonth = c.get(Calendar.MONTH);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.YEAR, year);
        int day = c.get(Calendar.DAY_OF_WEEK);

        if (day != 1) {
            day = -day + 3;
        } else {
            day = -5;
        }

        for (int w = 0; w < 6; w++) {
            TableRow tr = new TableRow(this);
            TableRow.LayoutParams tp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            tr.setLayoutParams(tp);

            for (int d = 0; d < 7; d++) {
                LinearLayout l = new LinearLayout(this);
                TableRow.LayoutParams llp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                l.setOrientation(LinearLayout.VERTICAL);
                l.setPadding(4, 0, 0, 2);
                l.setLayoutParams(llp);

                if ((day > 0) && (day <= c.getActualMaximum(Calendar.DAY_OF_MONTH))) {
                    renderDay(thisDay, thisMonth, day, d, l, llp);
                }

                day++;
                tr.addView(l);
            }
            tableLayout.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        }
    }

    private void renderDay(int today, int tomonth, int day, int d, LinearLayout l, TableRow.LayoutParams llp) {
        TextView tvDayNumber = new TextView(this);
        l.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.cell, null));

        if ((month == tomonth) && (day == today)) {
            l.setBackgroundColor(Color.argb(125, 153, 218, 234));
        }
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cellOnClick(view);
            }
        });

        tvDayNumber.setTextSize(24);
        tvDayNumber.setGravity(Gravity.TOP | Gravity.RIGHT);
        tvDayNumber.setText(Integer.toString(day));
        tvDayNumber.setPadding(0, 2, 12, 0);
        tvDayNumber.setTextColor(d == 6 ? Color.RED : Color.BLACK);

        if ((month == tomonth) && (day == today)) {
            tvDayNumber.setTypeface(null, Typeface.BOLD);
        }
        l.addView(tvDayNumber);

        LinearLayout lDay = new LinearLayout(this);
        lDay.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        //Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        //int rotation = display.getRotation();
        //lDay.setOrientation(rotation == 0 ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL );

        lDay.setOrientation(LinearLayout.VERTICAL );
        lDay.setPadding(0, 0, 0, 0);
        lDay.setLayoutParams(llp);

        ArrayList<View> texts = getDayText(day);
        for (View text : texts) {
            lDay.addView(text);
        }
        l.addView(lDay);
    }

    private void InitDaysHeader(TableLayout tableLayout) {
        String[] daysInWeek = getResources().getStringArray(R.array.daysInWeek);
        TableRow tr = new TableRow(this);
        for (String dayInWeek : daysInWeek) {
            tr.addView(getDayInWeekHeaderCell(dayInWeek));
        }
        tableLayout.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
    }

    private TextView getDayInWeekHeaderCell(String day) {
        TextView t = new TextView(this);
        t.setText(day);
        t.setTextColor(Color.BLACK);
        t.setGravity(Gravity.CENTER);
        if (day.equals("Ne")) {
            t.setTypeface(null, Typeface.BOLD);
            t.setTextColor(Color.RED);
        }
        return t;
    }

    private ArrayList<View> getDayText(int d) {
        ArrayList<View> views = new ArrayList<View>();
        for (FabionEvent fe : events) {
            if (fe.getDay() == d) {
                TextView tLogin = new TextView(getApplicationContext());
                if (fabionUser.isLogged()) {
                    tLogin.setTextSize(9);
                    tLogin.setText(fe.getLogin());
                    tLogin.setTextColor(Color.BLACK);
                    tLogin.setPadding(3, 0, 0, 0);
                    tLogin.setGravity(Gravity.LEFT);
                    if (fe.getLogin().equalsIgnoreCase(fabionUser.Login)) {
                        tLogin.setTextColor(Color.argb(255, 99, 99, 255));
                    }


                    TextView tTime = new TextView(getApplicationContext());
                    tTime.setText(String.format("%s-%s", fe.getTimeFrom(), fe.getTimeTo()));
                    tTime.setBackgroundColor(Color.argb(150,222,222,222));
                    tTime.setTextSize(11);
                    tTime.setTextColor(Color.BLACK);

                    TextView tPlaceHolder = new TextView(getApplicationContext());
                    tPlaceHolder.setText("  ");
                    tPlaceHolder.setGravity(Gravity.CENTER);
                    tPlaceHolder.setTextSize(9);
                    tPlaceHolder.setTextColor(Color.BLACK);
                    tPlaceHolder.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    LinearLayout sp = new LinearLayout(getApplicationContext());
                    sp.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                    sp.setPadding(50, 0, 50, 0);
                    sp.setBackgroundColor(Color.BLACK);
                    sp.setGravity(Gravity.CENTER);
                    sp.addView(tPlaceHolder);

                    views.add(tTime);
                    views.add(tLogin);
                    views.add(sp);
                } else {
                    tLogin.setText("***");
                    views.add(tLogin);
                }
            }
        }
        return views;
    }

    private void PrepareNewEvent() {
        Intent intent = new Intent(getApplicationContext(), EventDetailActivity.class);
        intent.putExtra(PAR_FUSER, fabionUser);
        intent.putExtra(PAR_FEVENT, FabionEvent.CreateNew(fabionUser));
        startActivityForResult(intent, Constants.RC_EVENT_NEW);
    }
}

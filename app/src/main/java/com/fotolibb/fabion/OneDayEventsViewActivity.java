package com.fotolibb.fabion;

import android.Manifest;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import static com.fotolibb.fabion.Constants.PAR_FEVENT;
import static com.fotolibb.fabion.Constants.PAR_FUSER;
import static com.fotolibb.fabion.SettingsActivity.PREFS_KEY_CALENDAR_ID;

public class OneDayEventsViewActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, IEventsConsumer, IStringConsumer {

    private static final int ITEM_ID_DELETE = Menu.FIRST + 1;
    private static final int ITEM_ID_BACK = Menu.FIRST + 2;
    ArrayList<FabionEvent> fabionEvents;
    private int mDay;
    private int mMonth;
    private int mYear;
    private FabionUser fabionUser;
    private String URL;

    private OneDayEventsViewActivity thisActivity;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_day_events_view2);
        fabionEvents = new ArrayList<FabionEvent>();
        thisActivity = this;

        final Calendar c = Calendar.getInstance();
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mMonth = c.get(Calendar.MONTH) + 1;
        mYear = c.get(Calendar.YEAR);

        setResult(RESULT_CANCELED);

        URL = Constants.getUrlService() + "getday.php?d=%d&m=%d&y=%d&l=%s&p=%s";
        Intent i = getIntent();
        fabionUser = i.getExtras().getParcelable(PAR_FUSER);
        int d = i.getExtras().getInt("Day");
        int m = i.getExtras().getInt("Month");
        int y = i.getExtras().getInt("Year");
        if ((d != 0) && (m != 0) && (y != 0)) {
            mDay = d;
            mMonth = m;
            mYear = y;
        }

        this.setTitle(String.format("%d. %s %d", d, getResources().getStringArray(R.array.mesice)[m - 1], y));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabOneDay);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrepareNewEvent();
            }
        });

        loadData();
    }

    private void PrepareNewEvent() {
        Intent intent = new Intent(getApplicationContext(), EventDetailActivity.class);
        intent.putExtra(PAR_FUSER, fabionUser);
        FabionEvent fe = FabionEvent.CreateNew(fabionUser);
        fe.setDay(mDay);
        fe.setMonth(mMonth);
        fe.setYear(mYear);
        intent.putExtra(PAR_FEVENT, fe);
        startActivityForResult(intent, Constants.RC_EVENT_NEW);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (view.getId() == listView.getId()) {
            ListView lv = (ListView) view;

            FabionEvent fEvent = (FabionEvent) lv.getItemAtPosition(((AdapterView.AdapterContextMenuInfo) menuInfo).position);

            menu.setHeaderTitle("Funkce");
            if (fEvent.getLogin().equalsIgnoreCase(fabionUser.Login)) {
                menu.add(Menu.NONE, ITEM_ID_DELETE, Menu.NONE, "Smazat");
            }
            menu.add(Menu.NONE, ITEM_ID_BACK, Menu.NONE, "Zpět");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ITEM_ID_DELETE:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                final FabionEvent fEvent = (FabionEvent) listView.getItemAtPosition(info.position);
                deleteEvent(fEvent);
                return true;
            case ITEM_ID_BACK:
                finish();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void deleteEvent(final FabionEvent fEvent) {
        deleteEvent(fEvent, null);
    }

    private void deleteEvent(final FabionEvent fEvent, final ImageView imView) {

        if (!validateEvent(fEvent)) {
            if (imView != null) {
                imView.setImageResource(R.drawable.delete_bw);
            }
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Skutečne smazat?\n" + fEvent.getSubject());
        builder.setCancelable(false);
        builder.setPositiveButton("Ano",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteEventOnServer(fEvent);
                        deleteCalendarEvent(fEvent);
                    }
                });
        builder.setNegativeButton("Ne",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (imView != null) {
                            imView.setImageResource(R.drawable.delete_bw);
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteEventOnServer(FabionEvent fe) {
        new DeleteEventAsyncTask(fabionUser.Login, fabionUser.PasswordHash, Constants.getUrlService(), Integer.toString(fe.getId()), thisActivity).execute();
    }

    private void deleteCalendarEvent(FabionEvent fEvent) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        long calendarId = mPrefs.getLong(PREFS_KEY_CALENDAR_ID, -1);
        if (calendarId == -1) {
            return;
        }

        int eventId = Tools.getCalendarEventId(fEvent, getContentResolver());
        if (eventId > 0) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
                int rows = getContentResolver().delete(deleteUri, null, null);
            }
        }
    }

    private Boolean validateEvent(FabionEvent fe) {
        StringBuilder sb = new StringBuilder();
        Boolean issue = false;

        Calendar now = Calendar.getInstance();
        Calendar ev = Calendar.getInstance();
        ev.set(Calendar.DAY_OF_MONTH, fe.getDay());
        ev.set(Calendar.MONTH, fe.getMonth() - 1);
        ev.set(Calendar.YEAR, fe.getYear());

        Integer h = Integer.parseInt(fe.getTimeFrom().substring(0, 2));
        Integer m = Integer.parseInt(fe.getTimeFrom().substring(3, 5));
        ev.set(Calendar.HOUR_OF_DAY, h);
        ev.set(Calendar.MINUTE, m);
        ev.set(Calendar.SECOND, 0);

        if (ev.before(now)) {
            if (issue) {
                sb.append("\n");
            }
            sb.append("Nelze mazat rezervace v minulosti");
            issue = true;
        }
        if (issue) {
            Toast.makeText(getApplicationContext(), sb.toString(), Toast.LENGTH_SHORT).show();
        }

        return !issue;
    }

    private void loadData() {
        new LoadDataByDaysAsyncTask(mDay, mMonth, mYear, URL, fabionUser, this).execute();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == Constants.RC_EVENT_UPDATE || requestCode == Constants.RC_EVENT_NEW) && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            loadData();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getApplicationContext(), EventDetailActivity.class);
        FabionEvent fe = fabionEvents.get(position);
        intent.putExtra(PAR_FUSER, fabionUser);
        intent.putExtra(PAR_FEVENT, fe);
        startActivityForResult(intent, Constants.RC_EVENT_UPDATE);
    }

    public void onDeleteButtonClick(View v) {
        ((ImageView) v).setImageResource(R.drawable.delete);
        for (int i = 0; i < fabionEvents.size(); i++) {
            if (Integer.toString(fabionEvents.get(i).getId()).equalsIgnoreCase((String) v.getContentDescription())) {
                deleteEvent(fabionEvents.get(i), (ImageView) v);
                break;
            }
        }
    }

    @Override
    public void ProcessData(ArrayList<FabionEvent> events) {
        fabionEvents = events;
        listView = (ListView) findViewById(R.id.list);
        String url = Constants.getUrlService();
        FabionEventBaseAdapter adapter = new FabionEventBaseAdapter(this, events, fabionUser, url);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void ProcessData(final String result) {
        try {
            if (result.equalsIgnoreCase("ok")) {
                setResult(RESULT_OK);
                loadData();
            } else {
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (Exception ex) {
            Log.e(getString(R.string.TAG_EX), ex.getMessage());
        }
    }
}



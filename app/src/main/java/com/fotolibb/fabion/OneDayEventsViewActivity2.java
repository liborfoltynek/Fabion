package com.fotolibb.fabion;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

public class OneDayEventsViewActivity2 extends AppCompatActivity implements AdapterView.OnItemClickListener, IEventsConsumer, IStringConsumer {

    private static final int ITEM_ID_DELETE = Menu.FIRST + 1;
    private static final int ITEM_ID_BACK = Menu.FIRST + 2;
    ArrayList<FabionEvent> fabionEvents;
    private int mDay;
    private int mMonth;
    private int mYear;
    private FabionUser fabionUser;
    private String URL;

    private OneDayEventsViewActivity2 thisActivity;
    private ListView listView;

    private int RC_UPDATE = 443;

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

        loadData();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Skutečne smazat?\n" + fEvent.getSubject());
        builder.setCancelable(false);
        builder.setPositiveButton("Ano",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteEvent(Integer.toString(fEvent.getId()));
                    }
                });
        builder.setNegativeButton("Ne",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        imView.setImageResource(R.drawable.delete_bw);
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteEvent(String eventId) {
        new DeleteEventAsyncTask(fabionUser.Login, fabionUser.PasswordHash, getResources().getString(R.string.url_fabion_service), eventId, thisActivity).execute();
    }

    private void loadData() {
        new LoadDataByDaysAsyncTask(mDay, mMonth, mYear, URL, fabionUser, this).execute();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == RC_UPDATE) && (resultCode == RESULT_OK)) {
            setResult(RESULT_OK);
            loadData();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getApplicationContext(), EventDetailActivity.class);
        FabionEvent fe = (FabionEvent) fabionEvents.toArray()[position];
        intent.putExtra("FUser", fabionUser);
        intent.putExtra("FEvent", fe);
        startActivityForResult(intent, RC_UPDATE);
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
        String url = getResources().getString(R.string.url_fabion_service);
        FabionEventBaseAdapter adapter = new FabionEventBaseAdapter(this, events, fabionUser, url);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        registerForContextMenu(listView);
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
            Log.e("EX", ex.getMessage());
        }
    }
}



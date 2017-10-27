package com.fotolibb.fabion;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OneDayEventsViewActivity extends ListActivity implements IEventsConsumer {
    private static final int ITEM_ID_FUNKCE1 = Menu.FIRST + 1;
    private int mDay;
    private int mMonth;
    private int mYear;
    private FabionUser fabionUser;
    private String URL;
    private ArrayList<FabionEvent> fabionEvents;
    private OneDayEventsViewActivity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisActivity = this;
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

        this.fabionEvents = events;
        setListAdapter(getListAdapter(events));
        registerForContextMenu(getListView());

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(getApplicationContext(), EventDetailActivity.class);
                FabionEvent fe = (FabionEvent) fabionEvents.toArray()[position];
                intent.putExtra("FUser", fabionUser);
                intent.putExtra("FEvent", fe);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (view.getId() == this.getListView().getId()) {

            ListView lv = (ListView) view;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Object obj = lv.getItemAtPosition(acmi.position);

            menu.setHeaderTitle("Funkce");
            menu.add(Menu.NONE, ITEM_ID_FUNKCE1, Menu.NONE, "Smazat");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ITEM_ID_FUNKCE1:

                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                ListView lv = getListView();
                HashMap hashMap = (HashMap) lv.getItemAtPosition(info.position);
                final String eventId = (String) hashMap.get("id");
                final String subj = (String) hashMap.get("subject");

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Skutečne smazat?\n" + subj);
                builder.setCancelable(false);
                builder.setPositiveButton("Ano",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteEvent(eventId);
                            }
                        });
                builder.setNegativeButton("Ne",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void deleteEvent(String eventId) {
        new DeleteEventAsyncTask(fabionUser.Login, fabionUser.PasswordHash, getResources().getString(R.string.url_fabion_service), eventId, thisActivity).execute();
    }

    private ListAdapter getListAdapter(List<FabionEvent> fabionEvents) {
        String[] nazvyAtributu = {"login", "subject", "timefrom", "timeto"};
        int[] idAtributu = {R.id.eventLogin, R.id.eventSubject, R.id.eventTime};
        SimpleAdapter adapter = new SimpleAdapter
                (this, getListAdapterData(fabionEvents),
                        R.layout.event_list_item, nazvyAtributu, idAtributu);
        return adapter;
    }

    private List<Map<String, ?>> getListAdapterData(List<FabionEvent> fabionEvents) {
        List<Map<String, ?>> list = new ArrayList<Map<String, ?>>(fabionEvents.size());
        for (FabionEvent fEvent : fabionEvents) {
            Map<String, String> polozkyMap = new HashMap<String, String>();
            polozkyMap.put("id", Integer.toString(fEvent.getId()));
            polozkyMap.put("login", fEvent.getLogin());
            polozkyMap.put("subject", fEvent.getSubject());
            polozkyMap.put("timefrom", fEvent.getTimeFrom());
            polozkyMap.put("timeto", fEvent.getTimeTo());
            list.add(polozkyMap);
        }
        return list;
    }

    public void ProcessData(final String result, String deletedId) {
        try {
            if (result.equalsIgnoreCase("ok")) {
                new LoadDataByDaysAsyncTask(mDay, mMonth, mYear, URL, fabionUser, this).execute();
            }
            else {
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


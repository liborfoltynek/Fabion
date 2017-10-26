package com.fotolibb.fabion;

import android.app.ListActivity;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.process;

public class OneDayEventsViewActivity extends ListActivity implements IEventsConsumer {
    private static final int ITEM_ID_FUNKCE1 = Menu.FIRST + 1;
    private static final int ITEM_ID_FUNKCE2 = Menu.FIRST + 2;
    private static final int ITEM_ID_FUNKCE3 = Menu.FIRST + 3;
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
            menu.setHeaderTitle("Funkce");
            menu.add(Menu.NONE, ITEM_ID_FUNKCE1, Menu.NONE, "Funkce1");
            menu.add(Menu.NONE, ITEM_ID_FUNKCE2, Menu.NONE, "Funkce2");
            menu.add(Menu.NONE, ITEM_ID_FUNKCE3, Menu.NONE, "Funkce3");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ITEM_ID_FUNKCE1:
                Toast.makeText(getApplicationContext(), "Funkce1",
                        Toast.LENGTH_SHORT).show();
                return true;
            case ITEM_ID_FUNKCE2:
                Toast.makeText(getApplicationContext(), "Funkce2",
                        Toast.LENGTH_SHORT).show();
                return true;
            case ITEM_ID_FUNKCE3:
                Toast.makeText(getApplicationContext(), "Funkce3",
                        Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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
}


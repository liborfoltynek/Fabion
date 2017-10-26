package com.fotolibb.fabion;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class EventDetailActivity extends AppCompatActivity {

    private FabionEvent fabionEvent;
    private FabionUser fabionUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Intent i = getIntent();
        fabionEvent = i.getExtras().getParcelable("FEvent");
        fabionUser = i.getExtras().getParcelable("FUser");

        ((EditText)findViewById(R.id.eventDetailLogin)).setText(fabionEvent.getLogin());
        ((EditText)findViewById(R.id.eventDetailSubject)).setText(fabionEvent.getSubject());
        ((EditText)findViewById(R.id.eventDetailTimeFrom)).setText(fabionEvent.getTimeFrom());
        ((EditText)findViewById(R.id.eventDetailTimeTo)).setText(fabionEvent.getTimeTo());
    }
}

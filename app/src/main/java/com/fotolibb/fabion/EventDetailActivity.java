package com.fotolibb.fabion;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
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

                Boolean isEnabled = !findViewById(R.id.eventDetailSubject).isEnabled();

                findViewById(R.id.eventDetailOK).setEnabled(isEnabled);
                findViewById(R.id.eventDetailOK).setVisibility(isEnabled ? View.VISIBLE : View.GONE);

                //findViewById(R.id.eventDetailLogin).setEnabled(isEnabled);
                findViewById(R.id.eventDetailSubject).setEnabled(isEnabled);
                findViewById(R.id.eventDetailNote).setEnabled(isEnabled);
                findViewById(R.id.eventDetailTimeFrom).setEnabled(isEnabled);
                findViewById(R.id.eventDetailTimeTo).setEnabled(isEnabled);
            }
        });

        Intent i = getIntent();
        fabionEvent = i.getExtras().getParcelable("FEvent");
        fabionUser = i.getExtras().getParcelable("FUser");

        ((EditText) findViewById(R.id.eventDetailLogin)).setText(fabionEvent.getLogin());
        ((EditText) findViewById(R.id.eventDetailSubject)).setText(fabionEvent.getSubject());
        ((EditText) findViewById(R.id.eventDetailTimeFrom)).setText(fabionEvent.getTimeFrom());
        ((EditText) findViewById(R.id.eventDetailTimeTo)).setText(fabionEvent.getTimeTo());
    }
}
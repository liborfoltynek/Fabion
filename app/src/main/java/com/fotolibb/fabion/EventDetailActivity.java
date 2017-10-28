package com.fotolibb.fabion;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EventDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private FabionEvent fabionEvent;
    private FabionUser fabionUser;

    private TextView eventDateText;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialogFrom, timePickerDialogTo;
    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;
    private EditText eventDetailDateTextView;
    private Button eventDetailButtonOK;
    private EditText eventTimeFromTextView, eventTimeToTextView;

    private int day, month, year;

    private int TIME_PICKER_THEME = 0;
    private int TIME_FROM = 0;
    private int TIME_TO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_event_detail);
            setResult(RESULT_CANCELED);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            dateFormatter = new SimpleDateFormat("dd.MM.yyyy"); //, Locale.forLanguageTag("CS"));
            timeFormatter = new SimpleDateFormat("HH:mm"); //, Locale.forLanguageTag("CS"));

            Intent i = getIntent();
            fabionEvent = i.getExtras().getParcelable("FEvent");
            fabionUser = i.getExtras().getParcelable("FUser");

            day = fabionEvent.getDay();
            month = fabionEvent.getMonth();
            year = fabionEvent.getYear();

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

            if (fabionEvent.getLogin().equalsIgnoreCase(fabionUser.Login)) {
                fab.setVisibility(View.VISIBLE);
            } else {
                fab.setVisibility(View.GONE);
            }

            findViewsById();
            setDateTimeField();

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Boolean isEnabled = !findViewById(R.id.eventDetailSubject).isEnabled();
                    if (!fabionEvent.getLogin().equalsIgnoreCase(fabionUser.Login)) {
                        Toast.makeText(getApplicationContext(), "Nelze editovat, co neni tvoje", Toast.LENGTH_LONG).show();
                    } else {

                        findViewById(R.id.eventDetailOK).setEnabled(isEnabled);
                        findViewById(R.id.eventDetailOK).setVisibility(isEnabled ? View.VISIBLE : View.GONE);

                        //findViewById(R.id.eventDetailLogin).setEnabled(isEnabled);
                        findViewById(R.id.eventDetailSubject).setEnabled(isEnabled);
                        findViewById(R.id.eventDetailEditNote).setEnabled(isEnabled);
                        findViewById(R.id.eventDetailTimeFrom).setEnabled(isEnabled);
                        findViewById(R.id.eventDetailTimeTo).setEnabled(isEnabled);
                        findViewById(R.id.eventDetailDate).setEnabled(isEnabled);
                    }
                }
            });

            ((EditText) findViewById(R.id.eventDetailLogin)).setText(fabionEvent.getLogin());
            ((EditText) findViewById(R.id.eventDetailSubject)).setText(fabionEvent.getSubject());
            ((EditText) findViewById(R.id.eventDetailTimeFrom)).setText(fabionEvent.getTimeFrom());
            ((EditText) findViewById(R.id.eventDetailTimeTo)).setText(fabionEvent.getTimeTo());
            ((EditText) findViewById(R.id.eventDetailEditNote)).setText(fabionEvent.getNote());
            ((EditText) findViewById(R.id.eventDetailDate)).setText(String.format("%d.%d.%d", fabionEvent.getDay(), fabionEvent.getMonth(), fabionEvent.getYear()));
        } catch (Exception ex) {
            Log.e("EX", ex.getLocalizedMessage());
        }
    }

    // OK, update
    public void onEventDetailButtonClick(View v) {
        FabionEvent f = new FabionEvent(
                fabionEvent.getId(),
                ((EditText) findViewById(R.id.eventDetailLogin)).getText().toString(),
                ((EditText) findViewById(R.id.eventDetailSubject)).getText().toString(),
                ((EditText) findViewById(R.id.eventDetailEditNote)).getText().toString(),
                ((EditText) findViewById(R.id.eventDetailTimeFrom)).getText().toString(),
                ((EditText) findViewById(R.id.eventDetailTimeTo)).getText().toString(),
                day,
                month,
                year);

        new UpdateEventAsyncTask(fabionUser.Login, fabionUser.PasswordHash, getResources().getString(R.string.url_fabion_service), f, this).execute();
    }

    private void findViewsById() {
        eventDetailDateTextView = (EditText) findViewById(R.id.eventDetailDate);
        eventTimeFromTextView = (EditText) findViewById(R.id.eventDetailTimeFrom);
        eventTimeToTextView = (EditText) findViewById(R.id.eventDetailTimeTo);
        eventDetailDateTextView.setInputType(InputType.TYPE_NULL);

        eventDetailButtonOK = (Button) findViewById(R.id.eventDetailOK);

        eventTimeFromTextView.setInputType(InputType.TYPE_NULL);
        eventTimeToTextView.setInputType(InputType.TYPE_NULL);
        eventDetailDateTextView.requestFocus();
    }

    private void setDateTimeField() {
        eventDetailDateTextView.setOnClickListener(this);
        eventTimeFromTextView.setOnClickListener(this);
        eventTimeToTextView.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int yearInput, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(yearInput, monthOfYear, dayOfMonth);
                month = monthOfYear+1;
                year = yearInput;
                day = dayOfMonth;
                eventDetailDateTextView.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        timePickerDialogFrom = new TimePickerDialog(this, TIME_PICKER_THEME, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hour, int min) {
                Calendar newC = Calendar.getInstance();
                newC.set(Calendar.HOUR_OF_DAY, hour);
                newC.set(Calendar.MINUTE, min);
                newC.set(Calendar.SECOND, 0);
                roundMinutes(newC);

                eventTimeFromTextView.setText(timeFormatter.format(newC.getTime()));
            }
        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);

        timePickerDialogTo = new TimePickerDialog(this, TIME_PICKER_THEME, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hour, int min) {
                Calendar newC = Calendar.getInstance();
                newC.set(Calendar.HOUR_OF_DAY, hour);
                newC.set(Calendar.MINUTE, min);
                newC.set(Calendar.SECOND, 0);
                roundMinutes(newC);
                eventTimeToTextView.setText(timeFormatter.format(newC.getTime()));
            }
        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);
    }

    @Override
    public void onClick(View view) {
        // datum
        if (view == eventDetailDateTextView) {
            datePickerDialog.updateDate(fabionEvent.getYear(), fabionEvent.getMonth() - 1, fabionEvent.getDay());
            datePickerDialog.show();
        }
        // time from
        if (view == eventTimeFromTextView) {
            Calendar c = getTimeFromFabionEvent(fabionEvent, TIME_FROM);
            timePickerDialogFrom.updateTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
            timePickerDialogFrom.show();
        }
        // time to
        if (view == eventTimeToTextView) {
            Calendar c = getTimeFromFabionEvent(fabionEvent, TIME_TO);
            timePickerDialogTo.updateTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
            timePickerDialogTo.show();
        }
        // OK (update)
        if (view == eventDetailButtonOK)
        {
            onEventDetailButtonClick(view);
        }
    }

    private Calendar getTimeFromFabionEvent(FabionEvent f, int seq) {
        Calendar c = Calendar.getInstance();
        String t = seq == 0 ? f.getTimeFrom() : f.getTimeTo();
        Integer h = Integer.parseInt(t.substring(0, 2));
        Integer m = Integer.parseInt(t.substring(3, 5));
        c.set(Calendar.HOUR_OF_DAY, h);
        c.set(Calendar.MINUTE, m);
        c.set(Calendar.SECOND, 0);
        return c;
    }

    private void roundMinutes(Calendar c) {
        int m = c.get(Calendar.MINUTE);

        if (m == 0 || m == 30)
            return;

        if (m < 15 || m > 45) {
            c.set(Calendar.MINUTE, 0);
        } else {
            c.set(Calendar.MINUTE, 30);
        }
        Toast.makeText(getApplicationContext(), R.string.MSG_WARN_TIME_MUST_BE_OF_HALF_HOUR, Toast.LENGTH_SHORT).show();
    }

    public void ProcessData(final String result) {
        if (result.equalsIgnoreCase("ok")) {
            setResult(RESULT_OK);
            finish();
        } else {

            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
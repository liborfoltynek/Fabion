package com.fotolibb.fabion;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.fotolibb.fabion.Constants.PAR_FEVENT;
import static com.fotolibb.fabion.Constants.PAR_FEVENT_EDIT;
import static com.fotolibb.fabion.Constants.PAR_FUSER;

public class EventDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private FabionEvent fabionEvent;
    private FabionUser fabionUser;
    private FloatingActionButton fab;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialogFrom, timePickerDialogTo;
    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;
    private EditText eventDetailDateTextView;
    private Button eventDetailButtonOK;
    private EditText eventTimeFromTextView, eventTimeToTextView;

    private int day, month, year; //, calendarEventId;

    private int TIME_PICKER_THEME = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_event_detail);
            setResult(RESULT_CANCELED);
            dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
            timeFormatter = new SimpleDateFormat("HH:mm");

            Intent i = getIntent();
            fabionEvent = i.getExtras().getParcelable(PAR_FEVENT);
            fabionUser = i.getExtras().getParcelable(PAR_FUSER);

            boolean edit = false;

            if (i.hasExtra(PAR_FEVENT_EDIT))
            {
                edit = i.getExtras().getBoolean(PAR_FEVENT_EDIT);
            }

            day = fabionEvent.getDay();
            month = fabionEvent.getMonth();
            year = fabionEvent.getYear();
            //calendarEventId = fabionEvent.getCalendarEventId();

            fab = (FloatingActionButton) findViewById(R.id.fab);

            if (fabionEvent.getLogin().equalsIgnoreCase(fabionUser.Login)) {
                fab.setVisibility(View.VISIBLE);
            } else {
                fab.setVisibility(GONE);
            }

            Calendar cEvent = Calendar.getInstance();
            Calendar cNow = Calendar.getInstance();
            cEvent.set(Calendar.YEAR, year);
            cEvent.set(Calendar.MONTH, month - 1);
            cEvent.set(Calendar.DAY_OF_MONTH, day);
            if (cEvent.before(cNow)) {
                fab.setVisibility(GONE);
            }

            findViewsById();
            setDateTimeField();

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SwitchEditMode();
                    fab.setVisibility(GONE);
                }
            });

            ((EditText) findViewById(R.id.eventDetailTimeFrom)).setText(fabionEvent.getTimeFrom());
            ((EditText) findViewById(R.id.eventDetailTimeTo)).setText(fabionEvent.getTimeTo());
            if (fabionEvent.getSubject().length() > 0) {
                ((EditText) findViewById(R.id.eventDetailSubject)).setText(fabionEvent.getSubject());
            }
            ((EditText) findViewById(R.id.eventDetailSubject)).setHint(String.format("%s (%s)", fabionUser.Name, fabionUser.Login));

            if (fabionEvent.getLogin().equalsIgnoreCase(fabionUser.Login) || fabionUser.Login.equalsIgnoreCase("libb")) {
                ((EditText) findViewById(R.id.eventDetailEditNote)).setText(fabionEvent.getNote());
            } else {
                findViewById(R.id.eventDetailEditNote).setVisibility(GONE);
                findViewById(R.id.eventDetailEditNoteLabel).setVisibility(GONE);
            }

            ((EditText) findViewById(R.id.eventDetailDate)).setText(String.format("%02d.%02d.%d", fabionEvent.getDay(), fabionEvent.getMonth(), fabionEvent.getYear()));

            if (edit || fabionEvent.getId() == 0) {
                SwitchEditMode(true);
                fab.setVisibility(GONE);
                findViewById(R.id.eventDetailEditNote).requestFocus();
            }

        } catch (Exception ex) {
            Log.e(getString(R.string.TAG_EX), ex.getLocalizedMessage());
        }
    }

    private void SwitchEditMode() {
        SwitchEditMode(!findViewById(R.id.eventDetailSubject).isEnabled());
    }

    @Override
    public void onBackPressed() {
        if (findViewById(R.id.eventDetailSubject).isEnabled() && fabionEvent.getId() != 0) {
            findViewById(R.id.eventDetailOK).setVisibility(GONE);
            SwitchEditMode(false);
            fab.setVisibility(VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void SwitchEditMode(Boolean isEnabled) {

        if (!fabionEvent.getLogin().equalsIgnoreCase(fabionUser.Login)) {
            Toast.makeText(getApplicationContext(), R.string.CANNOT_EDIT_SOMEBODYS_ELSE_EVENT, Toast.LENGTH_LONG).show();
        } else {

            findViewById(R.id.eventDetailOK).setEnabled(isEnabled);
            findViewById(R.id.eventDetailOK).setVisibility(isEnabled ? View.VISIBLE : GONE);
            findViewById(R.id.eventDetailSubject).setEnabled(isEnabled);
            findViewById(R.id.eventDetailEditNote).setEnabled(isEnabled);
            findViewById(R.id.eventDetailTimeFrom).setEnabled(isEnabled);
            findViewById(R.id.eventDetailTimeTo).setEnabled(isEnabled);
            findViewById(R.id.eventDetailDate).setEnabled(isEnabled);
        }
    }

    // OK, update
    public void onEventDetailButtonClick(View v) {
        String subject = ((EditText) findViewById(R.id.eventDetailSubject)).getText().toString();
        if (subject.length() == 0) {
            subject = ((EditText) findViewById(R.id.eventDetailSubject)).getHint().toString();
        }
        FabionEvent updatedEvent = new FabionEvent(
                fabionEvent.getId(),
                fabionUser.Login,
                subject,
                ((EditText) findViewById(R.id.eventDetailEditNote)).getText().toString(),
                ((EditText) findViewById(R.id.eventDetailTimeFrom)).getText().toString(),
                ((EditText) findViewById(R.id.eventDetailTimeTo)).getText().toString(),
                day,
                month,
                year,
                0);

        if (validateEvent(updatedEvent)) {
            new UpdateEventAsyncTask(fabionUser.Login, fabionUser.PasswordHash, Constants.getUrlService(), updatedEvent, this).execute();
        }
    }

    private Boolean validateEvent(FabionEvent fe) {
        StringBuilder sb = new StringBuilder();
        Boolean issue = false;

        if (fe.getLogin().isEmpty()) {
            sb.append(getString(R.string.LOGIN_NOT_SET));
            issue = true;
        }

        if (fe.getSubject().isEmpty()) {
            if (issue) {
                sb.append("\n");
            }
            fe.setSubject(String.format("%s (%s)", fabionUser.Name, fabionUser.Login));
        }
        Calendar now = Calendar.getInstance();
        Calendar evFrom = Tools.getTime(fe.getTimeFrom());
        Calendar evTo = Tools.getTime(fe.getTimeTo());
        evFrom.set(Calendar.DAY_OF_MONTH, fe.getDay());
        evFrom.set(Calendar.MONTH, fe.getMonth() - 1);
        evFrom.set(Calendar.YEAR, fe.getYear());
        evTo.set(Calendar.DAY_OF_MONTH, fe.getDay());
        evTo.set(Calendar.MONTH, fe.getMonth() - 1);
        evTo.set(Calendar.YEAR, fe.getYear());

        if (evFrom.before(now)) {
            if (issue) {
                sb.append("\n");
            }
            sb.append(getString(R.string.UNABLE_TO_SET_DATE_IN_PAST));
            issue = true;
        }

        if (!evFrom.before(evTo)) {
            if (issue) {
                sb.append("\n");
            }
            sb.append(getString(R.string.EVENT_FROM_MUST_BE_BEFORE_TO));
            issue = true;
        }

        if (issue) {
            Toast.makeText(getApplicationContext(), sb.toString(), Toast.LENGTH_SHORT).show();
        }

        return !issue;
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
                month = monthOfYear + 1;
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
            String dateStr = ((TextView) findViewById(R.id.eventDetailDate)).getText().toString();
            int day = Integer.parseInt(dateStr.substring(0, 2));
            int month = Integer.parseInt(dateStr.substring(3, 5));
            int year = Integer.parseInt(dateStr.substring(6, 10));
            datePickerDialog.updateDate(year, month - 1, day);
            datePickerDialog.show();
        }
        // time from
        if (view == eventTimeFromTextView) {
            String timeStr = ((TextView) findViewById(R.id.eventDetailTimeFrom)).getText().toString();
            Calendar c = Tools.getTime(timeStr);
            timePickerDialogFrom.updateTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
            timePickerDialogFrom.show();
        }
        // time to
        if (view == eventTimeToTextView) {
            String timeStr = ((TextView) findViewById(R.id.eventDetailTimeTo)).getText().toString();
            Calendar c = Tools.getTime(timeStr);
            timePickerDialogTo.updateTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
            timePickerDialogTo.show();
        }
        // OK (update)
        if (view == eventDetailButtonOK) {
            onEventDetailButtonClick(view);
        }
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
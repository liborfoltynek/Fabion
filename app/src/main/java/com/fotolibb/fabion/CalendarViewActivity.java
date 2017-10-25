package com.fotolibb.fabion;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import static android.R.attr.y;

public class CalendarViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);

        TableLayout tl = (TableLayout) findViewById(R.id.calendarTable);

        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        c.set(Calendar.MONTH, 4);
        c.set(Calendar.DAY_OF_MONTH, 1);
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
                TableRow.LayoutParams llp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                l.setOrientation(LinearLayout.VERTICAL);
                l.setPadding(4, 0, 0, 2);
                l.setLayoutParams(llp);

                TextView tvLeft = new TextView(this);

                if ((day > 0) && (day <= c.getActualMaximum(Calendar.DAY_OF_MONTH))) {
                    l.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.cell, null));
                    l.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            cellOnClick(view);
                        }
                    });
                    tvLeft.setTextSize(24);
                    tvLeft.setGravity(Gravity.TOP | Gravity.RIGHT);
                    tvLeft.setText(Integer.toString(day));
                    tvLeft.setPadding(0, 2, 12, 0);
                    if (d == 6) {
                        tvLeft.setTextColor(Color.RED);
                    }
                    l.addView(tvLeft);

                    TextView tv = new TextView(this);
                    tv.setText("Libb\n3stan\n...");
                    tv.setTextSize(10);
                    tvLeft.setPadding(2, 0, 0, 2);
                    tv.setGravity(Gravity.LEFT);
                    l.addView(tv);
                }

                day++;
                tr.addView(l);
            }
            tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        }
    }

    public void cellOnClick(View view) {
        try {
            LinearLayout b = (LinearLayout) view;
            TextView aa = (TextView) b.getChildAt(0);
            Toast.makeText(this, aa.getText(), Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Log.e("EX", ex.getMessage());
        }
    }
}



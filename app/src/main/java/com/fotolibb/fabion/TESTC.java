package com.fotolibb.fabion;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TESTC extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testc);


        GridLayout g = (GridLayout) findViewById(R.id.testcal);
        int day = 1;

        for (int d = 0; d < 7; d++) {
            LinearLayout l = new LinearLayout(this);
            TableRow.LayoutParams llp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            l.setOrientation(LinearLayout.VERTICAL);
            l.setPadding(4, 0, 0, 2);
            l.setLayoutParams(llp);

            TextView tvDate = new TextView(this);

            //if ((day > 0) && (day <= c.getActualMaximum(Calendar.DAY_OF_MONTH))) {
            l.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.cell, null));
            tvDate.setTextSize(24);
            tvDate.setGravity(Gravity.TOP | Gravity.RIGHT);
            tvDate.setText(Integer.toString(day));
            tvDate.setPadding(0, 2, 12, 0);
            if (d == 6) {
                tvDate.setTextColor(Color.RED);
            }
            l.addView(tvDate);

            TextView tvReservations = new TextView(this);
            tvReservations.setText(Integer.toString(day));
            tvReservations.setTextSize(10);
            tvReservations.setPadding(2, 0, 0, 2);
            tvReservations.setGravity(Gravity.LEFT);
            l.addView(tvReservations);
            //}

            day++;
            g.addView(l);

        }


    }
}

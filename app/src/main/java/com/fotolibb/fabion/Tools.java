package com.fotolibb.fabion;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import java.util.Calendar;

/**
 * Created by Libb on 30.10.2017.
 */

public class Tools {

    public static Calendar getTime(String strTime) {
        Calendar c = Calendar.getInstance();
        getTime(strTime, c);
        return c;
    }

    public static Calendar getDateTimeFrom(String strDate) {
        Calendar c = Calendar.getInstance();
        getDateTimeFrom(strDate, c);
        return c;
    }

    private static void getTime(String strTime, Calendar c) {
        Integer h = Integer.parseInt(strTime.substring(0, 2));
        Integer m = Integer.parseInt(strTime.substring(3, 5));

        c.set(Calendar.HOUR_OF_DAY, h);
        c.set(Calendar.MINUTE, m);
        c.set(Calendar.SECOND, 0);
    }

    private static void getDateTimeFrom(String strDate, Calendar c) {
        int day = Integer.parseInt(strDate.substring(0, 2));
        int month = Integer.parseInt(strDate.substring(3, 5));
        int year = Integer.parseInt(strDate.substring(6, 10));
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, day);
    }

    public static Calendar getDateTimeFrom(String strDate, String strTime) {
        Calendar c = Calendar.getInstance();
        getTime(strTime, c);
        getDateTimeFrom(strDate, c);
        return c;
    }

    public static Calendar getDateTimeFrom(FabionEvent fe) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, fe.getYear());
        c.set(Calendar.MONTH, fe.getMonth() - 1);
        c.set(Calendar.DAY_OF_MONTH, fe.getDay());
        getTime(fe.getTimeFrom(), c);
        return c;
    }

    public static Calendar getDateTimeTo(FabionEvent fe) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, fe.getYear());
        c.set(Calendar.MONTH, fe.getMonth() - 1);
        c.set(Calendar.DAY_OF_MONTH, fe.getDay());
        getTime(fe.getTimeTo(), c);
        return c;
    }

    public static boolean isFabionEventFromFuture(FabionEvent fe) {
        Calendar cFE = Tools.getDateTimeFrom(fe);
        Calendar cNow = Calendar.getInstance();
        return cNow.before(cFE);
    }

    public static boolean allPermissionsGranted(int[] grantResults) {
        if (grantResults.length == 0) {
            return false;
        }
        for (int res : grantResults)
            if (res != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        return true;
    }

    public static int getCalendarEventId(FabionEvent fe, ContentResolver contentResolver) {
        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(eventsUriBuilder, Long.MIN_VALUE);
        ContentUris.appendId(eventsUriBuilder, Long.MAX_VALUE);

        Uri eventsUri = eventsUriBuilder.build();
        Cursor cursor = contentResolver.query(
                eventsUri,
                new String[]{CalendarContract.Instances.CALENDAR_ID, CalendarContract.Instances.EVENT_ID},
                CalendarContract.Instances.DESCRIPTION +  " like '%###FABION ID="+ fe.getId() + "%'",
                null,
                CalendarContract.Instances.BEGIN + " ASC");

        if (cursor.moveToFirst()) {
            return cursor.getInt(1);
        }
        return -1;
    }
}

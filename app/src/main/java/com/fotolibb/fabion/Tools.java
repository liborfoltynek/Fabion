package com.fotolibb.fabion;

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
        c.set(Calendar.MONTH, fe.getMonth()-1);
        c.set(Calendar.DAY_OF_MONTH, fe.getDay());
        getTime(fe.getTimeFrom(), c);
        return c;
    }

    public static Calendar getDateTimeTo(FabionEvent fe) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, fe.getYear());
        c.set(Calendar.MONTH, fe.getMonth()-1);
        c.set(Calendar.DAY_OF_MONTH, fe.getDay());
        getTime(fe.getTimeTo(), c);
        return c;
    }

    public static boolean isFabionEventFromFuture(FabionEvent fe) {
        Calendar cFE = Tools.getDateTimeFrom(fe);
        Calendar cNow = Calendar.getInstance();
        return cNow.before(cFE);
    }
}

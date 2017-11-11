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

    private static void getTime(String strTime, Calendar c) {
        Integer h = Integer.parseInt(strTime.substring(0, 2));
        Integer m = Integer.parseInt(strTime.substring(3, 5));

        c.set(Calendar.HOUR_OF_DAY, h);
        c.set(Calendar.MINUTE, m);
        c.set(Calendar.SECOND, 0);
    }

    public static Calendar getDate(String strDate) {
        Calendar c = Calendar.getInstance();
        getDate(strDate, c);
        return c;
    }

    private static void getDate(String strDate, Calendar c) {
        int day = Integer.parseInt(strDate.substring(0, 2));
        int month = Integer.parseInt(strDate.substring(3, 5));
        int year = Integer.parseInt(strDate.substring(6, 10));
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, day);
    }

    public static Calendar getDateTime(String strDate, String strTime) {
        Calendar c = Calendar.getInstance();
        getTime(strTime, c);
        getDate(strDate, c);
        return c;
    }

    public static Calendar getDate(FabionEvent fe) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, fe.getYear());
        c.set(Calendar.MONTH, fe.getMonth()-1);
        c.set(Calendar.DAY_OF_MONTH, fe.getDay());
        return c;
    }


}

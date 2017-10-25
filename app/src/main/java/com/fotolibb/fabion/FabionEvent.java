package com.fotolibb.fabion;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Libb on 19.10.2017.
 */

public class FabionEvent implements Parcelable {
    public static final Creator<FabionEvent> CREATOR = new Creator<FabionEvent>() {
        @Override
        public FabionEvent createFromParcel(Parcel in) {
            return new FabionEvent(in);
        }

        @Override
        public FabionEvent[] newArray(int size) {
            return new FabionEvent[size];
        }
    };
    public String Login;
    public String TimeFrom;
    public String TimeTo;
    public int Day;
    public int Month;
    public int Year;
    public String Subject;
    public String Note;

    public FabionEvent() {

    }

    protected FabionEvent(Parcel in) {
        Login = in.readString();
        TimeFrom = in.readString();
        TimeTo = in.readString();
        Day = in.readInt();
        Month = in.readInt();
        Year = in.readInt();
        Subject = in.readString();
        Note = in.readString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Login: %s\n", Login));
        sb.append(String.format("Datum: %d.%d.%d\n", Day, Month, Year));
        sb.append(String.format("Čas: %s - %s\n", TimeFrom, TimeTo));
        sb.append(String.format("Subject: %s\n\n", Subject));
        sb.append(String.format("Poznámka: %s\n\n", Note));
        return sb.toString();
    }

    public String getString(FabionUser f) {
        StringBuilder sb = new StringBuilder();
        Boolean isLogged = f.isLogged();
        String hidden = "***";

        sb.append(String.format("Login: %s\n", isLogged ? Login : hidden));
        //       sb.append(String.format("Datum: %d.%d.%d\n", Day, Month, Year));
        sb.append(String.format("Čas: %s - %s\n", TimeFrom, TimeTo));
        sb.append(String.format("Subject: %s\n", isLogged ? Subject : hidden));

        if (isLogged && (f.Login.equals(Login) || f.Login.equals("libb") || f.Login.equals("Libb"))) {
            sb.append(String.format("Poznámka: %s\n", Note));
        }
        sb.append("\n");
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Login);
        parcel.writeString(TimeFrom);
        parcel.writeString(TimeTo);
        parcel.writeInt(Day);
        parcel.writeInt(Month);
        parcel.writeInt(Year);
        parcel.writeString(Subject);
        parcel.writeString(Note);
    }
}
package com.fotolibb.fabion;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

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

    private int Id;
    private String Login;
    private String TimeFrom;
    private String TimeTo;
    private int Day;
    private int Month;

    private int Year;
    private String Subject;
    private String Note;

    protected FabionEvent(Parcel in) {
        Id = in.readInt();
        Login = in.readString();
        TimeFrom = in.readString();
        TimeTo = in.readString();
        Day = in.readInt();
        Month = in.readInt();
        Year = in.readInt();
        Subject = in.readString();
        Note = in.readString();
    }

    public FabionEvent(int id, String login, String subject, String note, String tFrom, String tTo, int day, int month, int year) {
        Id = id;
        Login = login;
        TimeFrom = tFrom;
        TimeTo = tTo;
        Day = day;
        Month = month;
        Year = year;
        Subject = subject;
        Note = note;
    }

    public FabionEvent(JSONObject jsonEventData) throws JSONException {
        Id = jsonEventData.getInt("id");
        Day = jsonEventData.getInt("day");
        Month = jsonEventData.getInt("month");
        Year = jsonEventData.getInt("year");
        Login = jsonEventData.getString("login");
        TimeFrom = jsonEventData.getString("timefrom");
        TimeTo = jsonEventData.getString("timeto");
        Subject = jsonEventData.getString("subject");
        Note = jsonEventData.getString("note");
    }

    public static FabionEvent CreateNew(FabionUser fu) {
        Calendar c = Calendar.getInstance();
        int Day = c.get(Calendar.DAY_OF_MONTH);
        int Month = c.get(Calendar.MONTH) + 1;
        int Year = c.get(Calendar.YEAR);
        //String s = String.format("%s (%s)", fu.Name, fu.Login);
        return new FabionEvent(
                0, fu.Login, "", "", "18:00", "20:00", Day, Month, Year);
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getLogin() {
        return Login;
    }

    public void setLogin(String login) {
        Login = login;
    }

    public String getTimeFrom() {
        return TimeFrom;
    }

    public void setTimeFrom(String timeFrom) {
        TimeFrom = timeFrom;
    }

    public String getTimeTo() {
        return TimeTo;
    }

    public void setTimeTo(String timeTo) {
        TimeTo = timeTo;
    }

    public int getDay() {
        return Day;
    }

    public void setDay(int day) {
        Day = day;
    }

    public int getMonth() {
        return Month;
    }

    public void setMonth(int month) {
        Month = month;
    }

    public int getYear() {
        return Year;
    }

    public void setYear(int year) {
        Year = year;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getNote() {
        return Note;
    }

    public String getNote(String userLogin) {
        if (this.Login.equals(userLogin) || "libb".equals(userLogin))
            return Note;
        else
            return "";
    }

    public void setNote(String note) {
        Note = note;
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
        parcel.writeInt(Id);
        parcel.writeString(Login);
        parcel.writeString(TimeFrom);
        parcel.writeString(TimeTo);
        parcel.writeInt(Day);
        parcel.writeInt(Month);
        parcel.writeInt(Year);
        parcel.writeString(Subject);
        parcel.writeString(Note);
    }

    public int getImage() {
        return R.drawable.photographer;
    }
}
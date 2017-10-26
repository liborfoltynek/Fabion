package com.fotolibb.fabion;

/**
 * Created by Libb on 26.10.2017.
 */

public class FabionEventListItem {
    public String getLogin() {
        return Login;
    }

    public String getSubject() {
        return Subject;
    }

    public String getTime() {
        return Time;
    }

    private String Login;
    private String Subject;
    private String Time;
    public FabionEventListItem(String login, String time, String subject) {
        this.Subject = subject;
        this.Time = time;
        this.Login = login;
    }

}

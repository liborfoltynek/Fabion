package com.fotolibb.fabion;

import android.os.Parcel;
import android.os.Parcelable;

import static com.fotolibb.fabion.R.id.login;

/**
 * Created by Libb on 19.10.2017.
 */

public class FabionUser implements Parcelable {
    public String Login;
    public String Name;
    public String Phone;
    public String Email;
    public int FreeHours;
    public boolean Admin;
    private int logged = 0;

    public FabionUser() {
        this(null, null, null, null, 0, "N", 0);
    }

    public FabionUser(String login,
                      String name,
                      String phone,
                      String email,
                      int freeHours,
                      String admin,
                      int logged) {
        this.Login = login;
        this.logged = logged;
        this.Name = name;
        this.Phone = phone;
        this.Email = email;
        this.FreeHours = freeHours;
        this.Admin = admin.equals("Y");
    }

    public FabionUser(Parcel in) {
        readFromParcel(in);
    }

    public boolean isLogged() {
        return logged == 1;
    }

    public static final Parcelable.Creator<FabionUser> CREATOR
            = new Parcelable.Creator<FabionUser>() {
        public FabionUser createFromParcel(Parcel in) {
            return new FabionUser(in);
        }

        public FabionUser[] newArray(int size) {
            return new FabionUser[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(Login);
        dest.writeString(Name);
        dest.writeString(Phone);
        dest.writeString(Email);
        dest.writeInt(FreeHours);
        dest.writeInt(logged);
        dest.writeString(Admin ? "Y" : "N:");
    }

    private void readFromParcel(Parcel in) {
        Login = in.readString();
        Name = in.readString();
        Phone = in.readString();
        Email = in.readString();
        FreeHours = in.readInt();
        logged = in.readInt();
        Admin = in.readString().equals("Y");
    }

    @Override
    public String toString() {
        if (isLogged()) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Login: %s\n", Login));
            sb.append(String.format("Jméno: %s\n", Name));
            sb.append(String.format("E-mail: %s\n", Email));
            sb.append(String.format("Telefon: %s\n", Phone));
            sb.append(String.format("Volné hodiny: %s\n", FreeHours));
            if (Admin) {
                sb.append(String.format("Admin: Ano\n", Phone));
            }

            return sb.toString();
        } else {
            return "-";
        }
    }
}

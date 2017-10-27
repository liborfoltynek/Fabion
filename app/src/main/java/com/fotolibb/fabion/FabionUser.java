package com.fotolibb.fabion;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Libb on 19.10.2017.
 */

public class FabionUser implements Parcelable {
    public static final Parcelable.Creator<FabionUser> CREATOR
            = new Parcelable.Creator<FabionUser>() {
        public FabionUser createFromParcel(Parcel in) {
            return new FabionUser(in);
        }

        public FabionUser[] newArray(int size) {
            return new FabionUser[size];
        }
    };
    public String Login;
    public String PasswordHash;
    public String Name;
    public String Phone;
    public String Email;
    public int FreeHours;
    public boolean Admin;

    public int getId() {
        return Id;
    }

    private int Id;

    public FabionUser() {
        this(-1, null, null, null, null, null, 0, "N");
    }

    public FabionUser(
            int id,
            String login,
            String passwordHash,
            String name,
            String phone,
            String email,
            int freeHours,
            String admin
    ) {
        this.Id = id;
        this.Login = login;
        this.PasswordHash = passwordHash;
        this.Name = name;
        this.Phone = phone;
        this.Email = email;
        this.FreeHours = freeHours;
        this.Admin = admin.equals("Y");
    }

    public FabionUser(Parcel in) {
        Id = in.readInt();
        Login = in.readString();
        PasswordHash = in.readString();
        Name = in.readString();
        Phone = in.readString();
        Email = in.readString();
        FreeHours = in.readInt();
        Admin = in.readString().equals("Y");
    }

    public boolean isLogged() {
        return Id > 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeString(Login);
        dest.writeString(PasswordHash);
        dest.writeString(Name);
        dest.writeString(Phone);
        dest.writeString(Email);
        dest.writeInt(FreeHours);
        dest.writeString(Admin ? "Y" : "N:");
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
            return "";
        }
    }
}

package com.fotolibb.fabion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Objects;

import static android.support.constraint.R.id.parent;
import static com.fotolibb.fabion.Constants.PAR_FUSER;

public class LoginActivity extends AppCompatActivity {

    public static final String PREFS_KEY_EMAIL = "login_email";
    public static final String PREFS_KEY_PASSWORD = "login_password";
    private SharedPreferences mPrefs;

    public void setFabionUser(FabionUser fu) {
        try {
            Intent ii = new Intent(); //getApplicationContext(), MainActivity.class);
            ii.putExtra(PAR_FUSER, fu);

            if (fu.isLogged()) {
                setResult(RESULT_OK, ii);
                finish();
            } else {
                setResult(RESULT_FIRST_USER+1, ii);
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.action_sign_unableLogin, Toast.LENGTH_SHORT).show();
                    }
                });
                finish();

            }
        } catch (Exception ex) {
            Log.e(getString(R.string.TAG_EX), ex.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        setFabionUser(new FabionUser());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Boolean autoLogin = getIntent().hasExtra("TryAutoLogin") ? getIntent().getExtras().getBoolean("TryAutoLogin") : false;

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String emailFromPrefs = mPrefs.getString(PREFS_KEY_EMAIL, "");
        String passFromPrefs = mPrefs.getString(PREFS_KEY_PASSWORD, "");
        if (!emailFromPrefs.equals("") && !passFromPrefs.equals("")) {
            ((TextView) findViewById(R.id.email)).setText(emailFromPrefs);
            ((EditText) findViewById(R.id.password)).setText(passFromPrefs);
            ((CheckBox) findViewById(R.id.storeLogin)).setChecked(true);
            if (autoLogin) {
                tryLogin();
            }
        }
    }

    public void TryLogin(View view) {
        tryLogin();
    }

    private void tryLogin() {
        try {
            TextView t = (TextView) findViewById(R.id.userText);

            String l = ((EditText) findViewById(R.id.email)).getText().toString();
            String p = ((TextView) findViewById(R.id.password)).getText().toString();
            String pp = DoSHA1.SHA1(p);

            CheckBox chb = (CheckBox) findViewById(R.id.storeLogin);
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            if (chb.isChecked()) {
                String email = ((TextView) findViewById(R.id.email)).getText().toString();
                String password = ((EditText) findViewById(R.id.password)).getText().toString();
                prefsEditor.putString(PREFS_KEY_EMAIL, email);
                prefsEditor.putString(PREFS_KEY_PASSWORD, password);
            } else {

                prefsEditor.remove(PREFS_KEY_EMAIL);
                prefsEditor.remove(PREFS_KEY_PASSWORD);
            }
            prefsEditor.apply();

            new LoadDataLoginAsyncTask(this, t, l, pp).execute();

        } catch (NoSuchAlgorithmException e) {
            System.err.println("I'm sorry, but SHA-1 is not a valid message digest algorithm");
            setResult(RESULT_CANCELED);
        } catch (UnsupportedEncodingException e) {
            System.err.println("I'm sorry, but SHA-1 is not a valid message digest algorithm");
            setResult(RESULT_CANCELED);
        } catch (Exception ex) {
            Log.e(getString(R.string.TAG_EX), ex.getMessage());
        }
    }
}
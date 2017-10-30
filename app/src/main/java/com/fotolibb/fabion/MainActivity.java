package com.fotolibb.fabion;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import static android.R.attr.data;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FabionUser fabionUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, R.string.not_implemented, Snackbar.LENGTH_LONG).setAction("Action", null).show();

                Intent ii = new Intent(getApplicationContext(), TESTC.class);
                startActivity(ii);
            }
        });

        Constants.setUrlService(getResources().getString(R.string.url_fabion_service_production));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fabionUser = new FabionUser();
        setFabionUserInfoText();

        Login(false);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection Simplifiabl  eIfStatement
        if (id == R.id.action_settings) {
            ShowNotImplemented(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void ShowNotImplemented(MainActivity mainActivity) {
        Toast.makeText(mainActivity, R.string.not_implemented, Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.monthView) {
            MonthView(this);
        } else if (id == R.id.menuLogin) {
            Login();
        } else if (id == R.id.menuLogout) {
            Logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void MonthView(MainActivity mainActivity) {
        if (fabionUser.isLogged()) {
            Intent intent = new Intent(getApplicationContext(), EventsByMonthsScrollingActivity.class);
            intent.putExtra("FUser", fabionUser);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Pro zobrazení detailů se musíte přihlásit", Toast.LENGTH_SHORT).show();
        }
    }
    private void Login()
    {
        Login(true);
    }
    private void Login(Boolean showAlreadyLoggedMessage) {
        if (fabionUser.isLogged() && showAlreadyLoggedMessage) {
            Toast.makeText(this, R.string.action_sign_alreadylogged, Toast.LENGTH_SHORT).show();
        } else {
            try {
                Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(intent2, Constants.RO_LOGIN);
            } catch (Exception ex) {
                Log.e("EX", ex.getMessage());
            }
        }
    }

    private void Logout() {
        ((TextView) findViewById(R.id.userText)).setText("-");
        fabionUser = new FabionUser();
        setFabionUserInfoText();
        Login();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.RO_LOGIN) {
            if (resultCode == RESULT_OK) {
                if (data.hasExtra("FUser")) {
                    fabionUser = data.getParcelableExtra("FUser");
                    setFabionUserInfoText();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.openDrawer(Gravity.LEFT);
                }
            } else if (resultCode == RESULT_CANCELED) {
                fabionUser = new FabionUser();
                setFabionUserInfoText();
            }
        }
    }

    private void setFabionUserInfoText() {
        setFabionUserInfoText(fabionUser);
    }

    private void setFabionUserInfoText(FabionUser fu) {
        ((TextView) findViewById(R.id.userText)).setText(fu.toString());
    }
}

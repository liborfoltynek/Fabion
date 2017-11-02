package com.fotolibb.fabion;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import static com.fotolibb.fabion.Constants.FAB_USER;
import static com.fotolibb.fabion.Constants.PAR_FUSER;
import static com.fotolibb.fabion.Constants.RO_MONTHVIEW;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FabionUser fabionUser;
    private MainActivity mainActivity;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        Constants.setUrlService(getResources().getString(R.string.url_fabion_service_stage));
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MonthView(mainActivity);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
    protected void onResume() {
        super.onResume();

        if (fabionUser == null) {
            fabionUser = new FabionUser();
            Login(false);
        }
        setFabionUserInfoText();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (fabionUser != null) {
            outState.putParcelable(FAB_USER, fabionUser);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        FabionUser f = savedInstanceState.getParcelable(FAB_USER);
        if (f != null) {
            fabionUser = f;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection Simplifiabl  eIfStatement
        if (id == R.id.action_switchlogin) {
            if (fabionUser.isLogged()) {
                Logout();
            } else {
                Login();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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
            intent.putExtra(PAR_FUSER, fabionUser);
            startActivityForResult(intent, RO_MONTHVIEW);
        } else {
            Toast.makeText(getApplicationContext(), "Pro zobrazení detailů se musíte přihlásit", Toast.LENGTH_SHORT).show();
        }
    }

    private void Login() {
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
                Log.e(getString(R.string.TAG_EX), ex.getMessage());
            }
        }
    }

    private void Logout() {
        if (fabionUser.isLogged()) {
            ((TextView) findViewById(R.id.userText)).setText("-");
            fabionUser = new FabionUser();
            setFabionUserInfoText();
            Login();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.RO_LOGIN) {
            if (resultCode == RESULT_OK) {
                if (data.hasExtra(PAR_FUSER)) {
                    fabionUser = data.getParcelableExtra(PAR_FUSER);
                    setFabionUserInfoText();
                    MonthView(this);
                }
            } else if (resultCode == RESULT_CANCELED) {
                fabionUser = new FabionUser();
                setFabionUserInfoText();
            }

            if (menu != null) {
                MenuItem mi = (MenuItem) menu.findItem(R.id.action_switchlogin);
                mi.setTitle(fabionUser.isLogged() ? getString(R.string.action_logout) : getString(R.string.action_login));
            }

        } else if (requestCode == RO_MONTHVIEW) {
            if (resultCode == RESULT_OK)
                if (data != null) {
                    fabionUser = data.getParcelableExtra(PAR_FUSER);
                }
        }
    }

    private void setFabionUserInfoText() {
        setFabionUserInfoText(fabionUser);
    }

    private void setFabionUserInfoText(FabionUser fu) {
        if (fabionUser != null) {
            ((TextView) findViewById(R.id.userText)).setText(fu.toString());
        }
    }
}

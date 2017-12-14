package com.fotolibb.fabion;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import static com.fotolibb.fabion.Constants.FAB_USER;
import static com.fotolibb.fabion.Constants.PAR_FUSER;
import static com.fotolibb.fabion.Constants.RO_MONTHVIEW;

public class MainActivity extends AppCompatActivity implements IImageOwner {
    private FabionUser fabionUser;
    private MainActivity mainActivity;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;

        askAll();

        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        Constants.setUrlService(getResources().getString(R.string.url_fabion_service_stage));
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MonthView(mainActivity);
            }
        });
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
                intent2.putExtra("TryAutoLogin", !showAlreadyLoggedMessage);
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
        if (fu != null && fu.isLogged()) {
            //((TextView) findViewById(R.id.userText)).setText(fu.toString());
            TableLayout tl = (TableLayout) findViewById(R.id.tableLayout);
            tl.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.userText)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.userDetailEmail)).setText(fu.Email);
            ((TextView) findViewById(R.id.userDetailPhone)).setText(fu.Phone);
            ((TextView) findViewById(R.id.userDetailName)).setText(fu.Name);
            ((TextView) findViewById(R.id.userDetailFreeHours)).setText(Integer.toString(fu.FreeHours));
            ((TextView) findViewById(R.id.userDetailLogin)).setText(fu.Login);
            ((ImageView) findViewById(R.id.imageViewUser)).setVisibility(View.VISIBLE);

            String url = Constants.getUrlService() + "userimage.php?login=%s";
            String u = String.format(url, fabionUser.Login);
            new DownloadImageAsyncTask(getApplicationContext(), this).execute(new String[]{u});

        } else {
            TableLayout tl = (TableLayout) findViewById(R.id.tableLayout);
            tl.setVisibility(View.GONE);

            ((TextView) findViewById(R.id.userText)).setText(fu.toString());
            ((TextView) findViewById(R.id.userText)).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.imageViewUser)).setVisibility(View.GONE);
        }
    }

    @Override
    public void setUserImage(Bitmap bmp) {
        ImageView iv = (ImageView) findViewById(R.id.imageViewUser);
        iv.setImageBitmap(bmp);
    }

    private void askAll() {
        askRights(Manifest.permission.READ_CALENDAR);
        askRights(Manifest.permission.WRITE_CALENDAR);
    }

    private void askRights(String r) {
        if (ContextCompat.checkSelfPermission(this, r) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, r)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{r}, 5554);
            }
        }
    }
}
package com.fotolibb.fabion;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import static android.util.Base64.DEFAULT;
import static android.view.View.GONE;

/**
 * Created by Libb on 28.10.2017.
 */

public class FabionEventBaseAdapter
        extends BaseAdapter {

    Context context;
    List<FabionEvent> rowItems;
    FabionUser fabionUser;
    String serviceUrl;

    public FabionEventBaseAdapter(Context context, List<FabionEvent> items, FabionUser fabionUser, String serviceUrl) {
        this.context = context;
        this.rowItems = items;
        this.fabionUser = fabionUser;
        this.serviceUrl = serviceUrl + "userimage.php?login=%s";
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder hld = null;
        LayoutInflater lInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = lInflater.inflate(R.layout.list_item, null);
            hld = new ViewHolder();
            hld.txtTime = (TextView) convertView.findViewById(R.id.evDetailTime);
            hld.txtSubject = (TextView) convertView.findViewById(R.id.evDetailSubject);
            hld.imageUser = (ImageView) convertView.findViewById(R.id.obrazek);
            hld.imageDelete = (ImageView) convertView.findViewById(R.id.imDelete);
            hld.txtLogin = (TextView) convertView.findViewById(R.id.evDetailLogin);
            hld.txtNote = (TextView) convertView.findViewById(R.id.eventNote);

            convertView.setTag(hld);
        } else {
            hld = (ViewHolder) convertView.getTag();
        }
        FabionEvent polozka = (FabionEvent) getItem(position);
        hld.txtTime.setText(polozka.getTimeFrom() + " - " + polozka.getTimeTo());
        hld.txtSubject.setText(polozka.getSubject());
        hld.txtLogin.setText(polozka.getLogin());

        if (polozka.getLogin().equals(fabionUser.Login) || "libb".equals(fabionUser.Login))
        {
            hld.txtNote.setText(polozka.getNote(fabionUser.Login));
            hld.txtNote.setVisibility(View.VISIBLE);
        }
        else {
            hld.txtNote.setVisibility(GONE);
        }



        String u = String.format(serviceUrl, polozka.getLogin());
        //hld.imageUser.setImageResource(R.drawable.photographer);
        new DownloadImageAsyncTask(context, hld).execute(new String[]{u});

        if (polozka.getLogin().equals(fabionUser.Login)) {
            hld.imageDelete.setVisibility(View.VISIBLE);
            hld.imageDelete.setContentDescription(Integer.toString(polozka.getId()));
        } else {
            hld.imageDelete.setVisibility(GONE);
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }

    public class ViewHolder implements IImageOwner {
        ImageView imageUser;
        ImageView imageDelete;
        TextView txtSubject;
        TextView txtLogin;
        TextView txtTime;
        TextView txtNote;

        private Boolean imageUserSet = false;

        public void setUserImage(Bitmap bmp) {
            if (!imageUserSet) {
                imageUser.setImageBitmap(bmp);
                //   imageUserSet = true;
            }
        }
    }
}



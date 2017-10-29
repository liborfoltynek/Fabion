package com.fotolibb.fabion;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
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
            convertView.setTag(hld);
        } else {
            hld = (ViewHolder) convertView.getTag();
        }
        FabionEvent polozka = (FabionEvent) getItem(position);
        hld.txtTime.setText(polozka.getTimeFrom() + " - " + polozka.getTimeTo());
        hld.txtSubject.setText(polozka.getSubject());
        hld.txtLogin.setText(polozka.getLogin());

        String u = String.format(serviceUrl, polozka.getLogin());
        //hld.imageUser.setImageResource(R.drawable.photographer);
        new DownloadImageAsyncTask(hld).execute(new String[]{u});

        if (polozka.getLogin().equals(fabionUser.Login)) {
            hld.imageDelete.setVisibility(View.VISIBLE);
            hld.imageDelete.setContentDescription(Integer.toString(polozka.getId()));
        } else {
            hld.imageDelete.setVisibility(View.GONE);
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

    private class ViewHolder {
        ImageView imageUser;
        ImageView imageDelete;
        TextView txtSubject;
        TextView txtLogin;
        TextView txtTime;

        private Boolean imageUserSet = false;

        public void setUserImage(Bitmap bmp) {
            if (!imageUserSet) {
                imageUser.setImageBitmap(bmp);
             //   imageUserSet = true;
            }
        }
    }

    private class DownloadImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

        ViewHolder viewHolder;
        private SharedPreferences mPrefs;

        public DownloadImageAsyncTask(ViewHolder vh) {
            viewHolder = vh;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap bmp = null;
            for (String url : urls) {
                bmp = LoadBmpFromSharedPrefs(url);

                if (bmp == null) {
                    bmp = nactiBmp(url);
                }
            }
            return bmp;
        }

        private void SaveBmpToSharedPrefs(String url, Bitmap bmp) {
            mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            String encoded = Base64.encodeToString(b, DEFAULT);
            prefsEditor.putString(url, encoded);
            prefsEditor.apply();
        }

        private Bitmap LoadBmpFromSharedPrefs(String url) {
            Bitmap bmp;
            mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            String encoded = mPrefs.getString(url, "");
            if (encoded.isEmpty())
                return null;

            byte[] imageAsBytes = Base64.decode(encoded.getBytes(), DEFAULT);
            bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            viewHolder.setUserImage(result);
        }

        private Bitmap nactiBmp(String url) {
            Bitmap bmp = null;
            InputStream stream = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;
            try {
                stream = vytvorStream(url);
                bmp = BitmapFactory.decodeStream(stream, null, bmOptions);
                stream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            SaveBmpToSharedPrefs(url, bmp);
            return bmp;
        }

        private InputStream vytvorStream(String urlString) throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            try {
                HttpURLConnection httpCon = (HttpURLConnection) connection;
                httpCon.setRequestMethod("GET");
                httpCon.connect();
                if (httpCon.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpCon.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }
    }


}
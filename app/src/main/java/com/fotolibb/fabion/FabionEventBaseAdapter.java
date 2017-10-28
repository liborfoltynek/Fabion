package com.fotolibb.fabion;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        hld.imageUser.setImageResource(R.drawable.photographer);
        new WebImgDownloader(hld.imageUser).execute(new String[]{u});

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
        public ImageView imageUser;
        ImageView imageDelete;
        TextView txtSubject;
        TextView txtLogin;
        TextView txtTime;
    }

    private class WebImgDownloader extends AsyncTask<String, Void, Bitmap> {

        ImageView imageView;

        public WebImgDownloader(ImageView iv) {
            imageView = iv;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap bmp = null;
            for (String url : urls) {
                bmp = nactiBmp(url);
            }
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
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
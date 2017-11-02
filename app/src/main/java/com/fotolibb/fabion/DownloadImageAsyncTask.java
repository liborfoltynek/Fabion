package com.fotolibb.fabion;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import static android.util.Base64.DEFAULT;

public class DownloadImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

    IImageOwner viewHolder;
    private SharedPreferences mPrefs;
    private Context context;

    public DownloadImageAsyncTask(Context context, IImageOwner vh) {
        viewHolder = vh;
        this.context = context;
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

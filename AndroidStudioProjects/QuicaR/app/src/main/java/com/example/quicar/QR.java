package com.example.quicar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class QR extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewWeakReference;

    public QR(ImageView imageView){
        imageViewWeakReference = new WeakReference<>(imageView);
    }
    @Override
    protected Bitmap doInBackground(String... strings) {
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap){
        if (isCancelled()){
            bitmap = null;
        }
        ImageView imageView = imageViewWeakReference.get();
        if (imageView != null){
            if (bitmap != null){
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    private Bitmap getBitmap(String url){
        HttpURLConnection urlConnection = null;
        try{
            URL myUrl = new URL(url);
            urlConnection = (HttpURLConnection) myUrl.openConnection();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK){
                return null;
            }
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null){
                return BitmapFactory.decodeStream(inputStream);
            }
        } catch (Exception e) {
            urlConnection.disconnect();
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
        }
        return null;
    }
}

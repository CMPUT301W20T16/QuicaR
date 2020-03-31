package com.example.util;

import android.os.AsyncTask;
import android.util.Log;

import com.example.datahelper.DatabaseHelper;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class PopUpNotification {
    private String title = "empty title";
    private String msg = "empty body";

    public PopUpNotification() {}

    public PopUpNotification(String title, String msg) {
        this.title = title;
        this.msg = msg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void build() {
        sendPopUpNotification();
    }

    /**
     * This method send a pop up notification to this device
     */
    private void sendPopUpNotification() {
        new Notify().execute(title, msg);
    }



    /**
     * This is the class that generate a notification
     */
    private static class Notify extends AsyncTask<String, Void, Void> {
        // https://www.youtube.com/watch?v=v29x4dKNBJw
        // https://stackoverflow.com/questions/9671546/asynctask-android-example
        @Override
        protected Void doInBackground(String... data) {
            try {

                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestMethod("POST");


                conn.setRequestProperty("Authorization", "key=AIzaSyDKHMO4xM-b9y2-TMGR5KvPTvlT9jqGbYs");
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject json = new JSONObject();

                json.put("to", DatabaseHelper.getInstance().getToken());

                String title = data[0];
                String body = data[1];

                JSONObject info = new JSONObject();
                info.put("title", title);   // Notification title
                info.put("body", body); // Notification body

                json.put("notification", info);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(json.toString());
                wr.flush();
                conn.getInputStream();

            } catch (Exception e) {
                Log.d("Error",""+e);
            }
            return null;
        }
    }
}

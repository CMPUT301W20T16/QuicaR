package com.example.quicar;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.example.util.ConnectivityReceiver;

public class MyApplication extends Application {
    private static MyApplication mInstance;
    public static IntentFilter filter;
    public static ConnectivityReceiver receiver;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new ConnectivityReceiver();
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}


//https://www.androidhive.info/2012/07/android-detect-internet-connection-status/
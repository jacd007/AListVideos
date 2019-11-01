package com.zippyttech.alist.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ServiceStartOnBoot extends Service {

    private static final String TAG = "ServiceStartOnBoot";
    private boolean FINISH_SERVICES=false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FINISH_SERVICES=true;
        Log.i(TAG,"Create services.");
        // here you can add whatever you want this service to do
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Start services...");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Servicio iniciado", Toast.LENGTH_SHORT).show();
                }
            }, 500);


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"Destroy services.");
    }
}
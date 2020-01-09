package com.zippyttech.alist.services;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.zippyttech.alist.common.Utils;

public class SyncService extends Service {

    //CONSTANTS FIELDS
    public static final String SHARED_PREFERENCES_KEY ="SharedPreferences_data";
    private static final String TAG="SyncService";
    private final int TIME_SYNC = 5000;

    //FIELDS
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    public Hilo hilo;
    private boolean LOGGED;

    //PRIVATE FIELDS
    private ProgressDialog dialog;

    public SyncService() {
    }

    @Override
    public void onCreate() {
        settings = getSharedPreferences(SHARED_PREFERENCES_KEY, 0);
        editor = settings.edit();
        LOGGED = settings.getBoolean("logged",false);
        Log.i(TAG,"Servicio creado...");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (LOGGED) {
            Log.i(TAG, "Servicio iniciado...");
            boolean available = Utils.isNetworkAvailable(this);
            boolean conectionAvailable = Utils.isNetworkConnectionAvailable(this);

            Log.w("netHabilitada", Boolean.toString(available));
            Log.w("accInternet", Boolean.toString(conectionAvailable));

//            if (available) {
//                if (conectionAvailable) {
//                    hilo = new Hilo(this, true, TIME_SYNC);
//                    hilo.execute();
//                }else {
//                    Log.w(TAG,"No hay conexion a internet");
//                }
//            }else {
//                Log.w(TAG,"Conexion a la red no disponible");
//            }
        }else {
            Log.w(TAG,"No esta loggeado");
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        hilo.cancel(true);
        Log.w(TAG,"Servicio terminado...");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }
}

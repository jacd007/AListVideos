package com.zippyttech.alist.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.zippyttech.alist.R;
import com.zippyttech.alist.data.AListDB;
import com.zippyttech.alist.model.VideoModel;
import com.zippyttech.datelib.DateUtils.UtilsDate;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;
import static java.lang.Thread.sleep;

public class MyService extends Service {

    private static final String TAG = "Service";
    private AListDB aListDB;
    private List<VideoModel> list;
    private String DAY;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Servicio iniciado...");
        DAY = UtilsDate.dateTodayFormat("EEEE");
        aListDB = new AListDB(MyService.this);
        Toast.makeText(MyService.this, "Sync's Service start!", Toast.LENGTH_SHORT).show();
//        VideoModel vm = aListDB.getVideosByDay(DAY);
            list = new ArrayList<>();
            list.addAll(aListDB.getVideo());

        for (VideoModel vms: list){
            String lisDay = vms.getmDay().replaceAll("á","a")
                    .toLowerCase().replaceAll("é","e").toLowerCase().replaceAll("í","i")
                    .toLowerCase().replaceAll("ó","o").toLowerCase().replaceAll("ú","u");

           if (lisDay.equals(DAY)) Log.w(TAG, "Fecha: "+vms.getmDay()+" = "+DAY);
        }


//        try{
//            hilo hil = new hilo(this);
//            hil.execute();
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}

class hilo extends AsyncTask<String,String,String> {
    private Context context;
    private boolean val;
    private AListDB aListDB;
    private List<VideoModel> list;

    public hilo(Context context){
        this.context = context;
        if (context!=null)
            aListDB = new AListDB(context);
        val=true;

    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            while (val){
                VideoModel vm = new VideoModel();
                if (context!=null){
                  vm = aListDB.getVideosByDay(UtilsDate.dateTodayFormat("EEEE"));
                  list = new ArrayList<>();
                  list.add(vm);
                }
                for (VideoModel vms: list){
                    Toast.makeText(context, "Nombre: "+vms.getTitle(), Toast.LENGTH_SHORT).show();
                    sleep(5000);
                }
//                sleep(2880000);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private void sendBroadCast(Context context) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("es.androcode.android.BootBroadcast");
        broadcastIntent.putExtra("parameter", "Nueva notificacion.");
        context.sendBroadcast(broadcastIntent);
    }

    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        sendBroadCast(context);
    }

    private void createNotificationChannel(String CHANNEL_ID, String name, String description) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}


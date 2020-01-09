package com.zippyttech.alist.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.zippyttech.alist.MainActivity;
import com.zippyttech.alist.R;
import com.zippyttech.alist.common.Utils;
import com.zippyttech.alist.data.AListDB;
import com.zippyttech.alist.model.VideoModel;
import com.zippyttech.alist.view.SelectListActivity;
import com.zippyttech.datelib.DateUtils.UtilsDate;

import static android.content.Context.NOTIFICATION_SERVICE;
import static java.lang.Thread.sleep;

/**
 * Created by zippyttech on 06/10/18.
 */

public class Hilo extends AsyncTask<String,String,String>{
    Context context ;
    private boolean inProgress;
    private int TIME_SYNC;
    private static final String TAG="Hilo";
    private  String DAY;
    private AListDB aListDB;
    private VideoModel vm;

    public Hilo(Context context, Boolean inProgress, int timeSeg){
        this.context = context;
        this.inProgress = inProgress;
        this.TIME_SYNC = timeSeg * 1000;
        aListDB = new AListDB(context);

    }
    @Override
    protected String doInBackground(String... strings) {
        try {
            DAY = UtilsDate.dateTodayFormat("EEEE").toLowerCase();
            String hora = UtilsDate.dateTodayFormat("HH:mm:ss");
            int ehora = UtilsDate.Epoch(hora,"HH:mm:ss");
//            vm = aListDB.getitemByDateUp(DAY);

            while(inProgress){
                sleep(TIME_SYNC);
                    Log.w(TAG, " hilo en progreso... ("+ehora+")<==("+hora+")");
                    if (hora.equals("08:00") || hora.equals("14:00")||hora.equals("20:00"))
                        throudNotificacion(vm.getId(),vm.getTitle(),""+vm.getCap(),vm.getmColor());

            }
        }
        catch (Exception e){

        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        // throudNotificacion(V);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
//        consulta(context);
        // throudNotificacion(V);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void throudNotificacion(int cont,String title , String contentText, String ticker){

        Intent intent = new Intent(context, SyncNotification.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.logox)
                .setContentIntent(contentIntent)
                .setTicker(ticker)
                .setContentInfo(""+1)
                .setSound(sonido)
                .build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(cont, noti);
    }

}

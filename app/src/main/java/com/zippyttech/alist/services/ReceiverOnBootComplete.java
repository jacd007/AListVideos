package com.zippyttech.alist.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiverOnBootComplete extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Intent serviceIntent = new Intent(context, ServiceStartOnBoot.class);
//            context.startService(serviceIntent);
        }
    }
}
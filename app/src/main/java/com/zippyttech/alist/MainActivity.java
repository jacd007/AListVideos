package com.zippyttech.alist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import com.zippyttech.alist.services.MyService;
import com.zippyttech.alist.view.SelectListActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Handler handler = new Handler();
        if (!isMyServiceRunning(MyService.class)){ //método que determina si el servicio ya está corriendo o no
            Intent serv = new Intent(this, MyService.class); //serv de tipo Intent
            this.startService(serv); //ctx de tipo Context
            Log.d("App", "Service started");
        } else {
            Log.d("App", "Service already running");
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goIntroScreen();
//              startServices();
            }
        }, 1000);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private boolean isMyServiceRunning(Class<MyService> myServiceClass) {
        return false;
    }

    private void goIntroScreen() {
        intent = new Intent(this, SelectListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}

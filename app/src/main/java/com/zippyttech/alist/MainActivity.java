package com.zippyttech.alist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.zippyttech.alist.services.ServiceStartOnBoot;
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
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goIntroScreen();
//              startServices();
            }
        }, 2000);

    }

    private void goIntroScreen() {
        intent = new Intent(this, SelectListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void startServices(){
        Intent serviceIntent = new Intent(this, ServiceStartOnBoot.class);
        this.startService(serviceIntent);
    }
}

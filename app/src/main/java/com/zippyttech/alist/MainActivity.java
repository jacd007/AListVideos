package com.zippyttech.alist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.zippyttech.alist.view.SelectListActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        goIntroScreen();

    }

    private void goIntroScreen() {
        intent = new Intent(this, SelectListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

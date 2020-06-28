package com.sanjaysgangwar.clock;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    TextView textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Full Screen App
        View decorView = getWindow().getDecorView();
        int uiOptions = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        decorView.setSystemUiVisibility(uiOptions);
        textview = findViewById(R.id.time);
        updateTime();

    }

    private void updateTime() {
        Date date = new Date();
        int HH = date.getHours();
        if (HH > 12) {
            HH = HH - 12;
        }
        int MM = date.getMinutes();
        String Time = HH + " : " + MM;
        textview.setText(Time);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateTime();
            }
        }, 1000);
    }


}
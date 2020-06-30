package com.sanjaysgangwar.clock;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    TextView timeTV, dayTV, dateTV;
    String DAY = null;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeTV = findViewById(R.id.TIMETV);
        dayTV = findViewById(R.id.DAYTV);
        dateTV = findViewById(R.id.DATETV);

        //Full Screen App
        View decorView = getWindow().getDecorView();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        int uiOptions;

        uiOptions = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);


        decorView.setSystemUiVisibility(uiOptions);

        updateTime();

    }

    private void updateTime() {
        Date date = new Date();
        int day = date.getDay();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        switch (day) {
            case 1:
                DAY = "MON";
                break;
            case 2:
                DAY = "TUES";
                break;
            case 3:
                DAY = "WED";
                break;
            case 4:
                DAY = "THURS";
                break;
            case 5:
                DAY = "FRI";
                break;
            case 6:
                DAY = "SAT";
                break;
            case 7:
                DAY = "SUN";
                break;


        }

        int HH = date.getHours();
        if (HH > 12) {
            HH = HH - 12;
        }
        int MM = date.getMinutes();
        String Time = HH + " : " + MM;
        timeTV.setText(Time);
        dateTV.setText(formatter.format(date));
        dayTV.setText(DAY);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateTime();
            }
        }, 1000);
    }


}
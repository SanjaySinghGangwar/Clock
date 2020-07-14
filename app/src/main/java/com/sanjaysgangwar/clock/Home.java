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

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

public class Home extends AppCompatActivity {
    TextView timeTV, dayTV, dateTV;
    String DAY = null;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        timeTV = findViewById(R.id.TIMETV);
        dayTV = findViewById(R.id.DAYTV);
        dateTV = findViewById(R.id.DATETV);


        updateTime();
        updateUI();

    }

    private void updateUI() {
        //Full Screen App
        View decorView = getWindow().getDecorView();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        int uiOptions;

        uiOptions = (
                SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | SYSTEM_UI_FLAG_FULLSCREEN);

        decorView.setSystemUiVisibility(uiOptions);
    }

    private void updateTime() {
        Date date = new Date();
        int day = date.getDay();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy");
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
                DAY = "THUR";
                break;
            case 5:
                DAY = "FRI";
                break;
            case 6:
                DAY = "SAT";
                break;
            case 0:
                DAY = "SUN";
                break;


        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormatter = new SimpleDateFormat("hh : mm");

        timeTV.setText(timeFormatter.format(date));
        dateTV.setText(formatter.format(date));
        dayTV.setText(DAY);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateTime();
                updateUI();
            }
        }, 5000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopClock();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopClock();

    }

    void stopClock() {
        System.exit(0);
    }
}
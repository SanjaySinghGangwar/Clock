package com.sanjaysgangwar.clock;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    TextView timeTV, dayTV, dateTV;
    String DAY = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeTV = findViewById(R.id.TIMETV);
        dayTV = findViewById(R.id.DAYTV);
        dateTV = findViewById(R.id.DATETV);

        //Full Screen App
        View decorView = getWindow().getDecorView();
        int uiOptions = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        decorView.setSystemUiVisibility(uiOptions);

        updateTime();

    }

    private void updateTime() {
        Date date = new Date();
        int day = date.getDay();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

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
package com.sanjaysgangwar.clock.Java;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sanjaysgangwar.clock.R;
import com.sanjaysgangwar.clock.api.ApiInterface;
import com.sanjaysgangwar.clock.modelClass.wheatherModel;
import com.sanjaysgangwar.clock.utils.NetworkUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

public class Home extends AppCompatActivity implements View.OnClickListener {
    TextView timeTV, dayTV, dateTV, tempTextView;
    String DAY = null;
    int temp;
    FusedLocationProviderClient fusedLocationProviderClient;
    double currentLongitude;
    double currentLatitude;
    String weatherKey = "2e3e87023a31a19d056c76e35a48a178";
    ImageView Setting;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String APP_SHARED_PREFS;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        timeTV = findViewById(R.id.TIMETV);
        dayTV = findViewById(R.id.DAYTV);
        dateTV = findViewById(R.id.DATETV);
        Setting = findViewById(R.id.Setting);
        tempTextView = findViewById(R.id.tempTextView);


        Setting.setOnClickListener(this);

        sharedPreferences = Home.this.getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        APP_SHARED_PREFS = "DeskClock";

        //location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //onCrate
        updateTime();
        updateUI();
        weatherUpdate();


    }

    private void weatherUpdate() {
        if ((sharedPreferences.getString("weather", "").equals("on"))) {
            if (NetworkUtil.isOnline(this)) {
                getLocation();
            } else {
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
            }
        } else {
            tempTextView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        timeAndDateHandler();
        whetherHandler();
    }

    private void timeAndDateHandler() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateTime();
                updateUI();
                timeAndDateHandler();
            }
        }, 5000);
    }

    private void whetherHandler() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                weatherUpdate();
                whetherHandler();
                //SET TEMP TO LAYOUT

            }
        }, 3600000);

    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentLatitude = location.getLatitude();
                            currentLongitude = location.getLongitude();
                            Log.e("lastCurrentLatitude", String.valueOf(currentLatitude));
                            Log.e("lastCurrentLongitude", String.valueOf(currentLongitude));
                            wheatherApi(currentLatitude, currentLongitude);

                        } else {
                            Toast.makeText(Home.this, "Error getting location", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void wheatherApi(Double currentLatitude, Double currentLongitude) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<wheatherModel> call = apiInterface.getWeather(currentLatitude, currentLongitude, weatherKey);
        call.enqueue(new Callback<wheatherModel>() {
            @Override
            public void onResponse(Call<wheatherModel> call, Response<wheatherModel> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        tempTextView.setVisibility(View.VISIBLE);
                        temp = (int) (response.body().getMain().getTemp() - 273.15);
                        tempTextView.setText(temp + "°C");
                    }
                }

            }

            @Override
            public void onFailure(Call<wheatherModel> call, Throwable t) {
                Log.e("Weather API ", "onFailure: " + t.getLocalizedMessage());
            }
        });
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
        // System.exit(0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Setting:
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                View v = getLayoutInflater().inflate(R.layout.setting_dialog, null);
                builder.setView(v);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                Switch weatherSwitch = v.findViewById(R.id.weatherSwitch);

                if ((sharedPreferences.getString("weather", "").equals("on"))) {
                    weatherSwitch.setChecked(true);
                } else {
                    weatherSwitch.setChecked(false);
                }

                weatherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
                                alertDialog.dismiss();
                            } else {
                                editor.clear();
                                editor.putString("weather", "on");
                                editor.commit();
                                getLocation();
                                alertDialog.dismiss();
                            }


                        } else {
                            tempTextView.setVisibility(View.INVISIBLE);
                            editor.clear();
                            editor.putString("weather", "off");
                            editor.commit();
                            alertDialog.dismiss();
                        }
                    }
                });

                break;

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10:
                editor.clear();
                editor.putString("weather", "on");
                editor.commit();
                getLocation();
                break;
        }
    }
}
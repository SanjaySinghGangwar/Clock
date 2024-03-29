package com.sanjaysgangwar.clock.mUi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.sanjaysgangwar.clock.R;
import com.sanjaysgangwar.clock.databinding.HomeBinding;
import com.sanjaysgangwar.clock.mRestrofit.ApiClient;
import com.sanjaysgangwar.clock.mRestrofit.ApiInterface;
import com.sanjaysgangwar.clock.modelClass.AppSharePreference;
import com.sanjaysgangwar.clock.modelClass.wheatherModel;
import com.sanjaysgangwar.clock.utils.NetworkUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

public class Home extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {


    String DAY = null;
    int temp;
    FusedLocationProviderClient fusedLocationProviderClient;
    String weatherKey = "Add Api key here";
    SwitchCompat weatherSwitch, SecondsSwitch, am_pm_switch, Ads;
    AppSharePreference appSharePreference;
    LinearLayoutCompat donate, OtherApps, Share;
    Intent intent;
    AlertDialog.Builder builder;
    View v;
    AlertDialog alertDialog;
    ApiInterface apiInterface;
    private HomeBinding bind;
    AdRequest adRequest;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = HomeBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        initialiseALL();
        updateTime();
        updateUI();

        weatherUpdate();

        UpdateUIForSwitches();
        if (appSharePreference.getAds()) {
            checkForAds();
        }

    }

    private void checkForAds() {
        bind.adView.loadAd(adRequest);
    }


    private void initialiseALL() {

        //Dialog Setting
        builder = new AlertDialog.Builder(Home.this);
        v = getLayoutInflater().inflate(R.layout.setting_dialog, null);
        builder.setView(v);
        alertDialog = builder.create();


        //dialog ids
        weatherSwitch = v.findViewById(R.id.weatherSwitch);
        donate = v.findViewById(R.id.donate);
        am_pm_switch = v.findViewById(R.id.am_pm_switch);
        SecondsSwitch = v.findViewById(R.id.SecondsSwitch);
        Ads = v.findViewById(R.id.ads);
        OtherApps = v.findViewById(R.id.otherApps);
        Share = v.findViewById(R.id.share);


        //Listener
        bind.setting.setOnClickListener(this);
        weatherSwitch.setOnCheckedChangeListener(this);
        am_pm_switch.setOnCheckedChangeListener(this);
        SecondsSwitch.setOnCheckedChangeListener(this);
        Ads.setOnCheckedChangeListener(this);
        donate.setOnClickListener(this);
        OtherApps.setOnClickListener(this);
        Share.setOnClickListener(this);

        //sharedPreference
        appSharePreference = new AppSharePreference(this);

        //location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        apiInterface = ApiClient.getClient(Home.this).create(ApiInterface.class);

        //ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adRequest = new AdRequest.Builder().build();

    }


    private void weatherUpdate() {
        if (appSharePreference.getWeather()) {
            getLocation();
        } else {
            bind.temperature.setVisibility(View.GONE);
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
        handler.postDelayed(() -> {
            updateTime();
            updateUI();
            timeAndDateHandler();
        }, 1000);
    }

    private void whetherHandler() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            weatherUpdate();
            whetherHandler();
        }, 3600000);

    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
        } else {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            weatherApi(location.getLatitude(), location.getLongitude());
                        } else {
                            bind.temperature.setVisibility(View.GONE);
                            Toast.makeText(Home.this, "Error getting location 💤", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> {
                Log.e("Weather API ", "onFailure: " + e.getLocalizedMessage());
            });
        }
    }

    private void weatherApi(Double currentLatitude, Double currentLongitude) {
        if (NetworkUtil.isOnline(this)) {
            Call<wheatherModel> call = apiInterface.getWeather(currentLatitude, currentLongitude, weatherKey);
            call.enqueue(new Callback<wheatherModel>() {
                @Override
                public void onResponse(Call<wheatherModel> call, Response<wheatherModel> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            bind.temperature.setVisibility(View.VISIBLE);
                            temp = (int) (response.body().getMain().getTemp() - 273.15);
                            bind.temperature.setText(temp + "°C");
                        }
                    }

                }

                @Override
                public void onFailure(Call<wheatherModel> call, Throwable t) {
                    Log.e("Weather API ", "onFailure: " + t.getLocalizedMessage());
                }
            });
        } else {
            Toast.makeText(this, "No internet connection 📶", Toast.LENGTH_LONG).show();
        }
    }


    private void updateUI() {
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
        @SuppressLint("SimpleDateFormat") SimpleDateFormat hrs = new SimpleDateFormat("hh");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat min = new SimpleDateFormat("mm");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sec = new SimpleDateFormat("ss");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat am_pm = new SimpleDateFormat("aa");


        bind.date.setText(formatter.format(date));
        bind.day.setText(DAY);
        bind.hours.setText(hrs.format(date) + " : ");
        bind.minutes.setText(min.format(date));
        bind.seconds.setText(" : " + sec.format(date));
        bind.amPm.setText("  " + am_pm.format(date));


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting:
                switches();
                alertDialog.setCancelable(true);
                alertDialog.show();
                break;
            case R.id.share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Desk Clock");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey, Just found really awesome game on play store,\n\nhttps://play.google.com/store/apps/details?id=" + view.getContext().getPackageName());
                startActivity(Intent.createChooser(shareIntent, "Share with"));
                DismissDialog();
                break;
            case R.id.otherApps:
                DismissDialog();
                Intent i = new Intent(this, AllApps.class);
                startActivity(i);
                break;
            case R.id.donate:
                intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.buymeacoffee.com/TheAverageGuy"));
                intent = Intent.createChooser(intent, "Donate us ♥");
                startActivity(intent);
                DismissDialog();
                break;


        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                appSharePreference.setWeather(true);
                getLocation();
            }
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.weatherSwitch:
                if (isChecked) {
                    if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
                    } else {
                        appSharePreference.setWeather(true);
                        getLocation();
                    }
                } else {
                    bind.temperature.setVisibility(View.GONE);
                    appSharePreference.setWeather(false);
                }
                break;
            case R.id.am_pm_switch:
                if (isChecked) {
                    appSharePreference.setAmPm(true);
                    bind.amPm.setVisibility(View.VISIBLE);
                } else {
                    appSharePreference.setAmPm(false);
                    bind.amPm.setVisibility(View.GONE);
                }
                break;
            case R.id.SecondsSwitch:
                if (isChecked) {
                    bind.seconds.setVisibility(View.VISIBLE);
                    appSharePreference.setSeconds(true);
                } else {
                    bind.seconds.setVisibility(View.GONE);
                    appSharePreference.setSeconds(false);
                }
                break;
            case R.id.ads:
                if (isChecked) {
                    bind.adView.setVisibility(View.VISIBLE);
                    checkForAds();
                    appSharePreference.setAds(true);
                } else {
                    bind.adView.setVisibility(View.GONE);
                    appSharePreference.setAds(false);
                }
                break;
        }
    }

    public void switches() {
        weatherSwitch.setChecked(appSharePreference.getWeather());
        am_pm_switch.setChecked(appSharePreference.getAmPm());
        SecondsSwitch.setChecked(appSharePreference.getSeconds());
        Ads.setChecked(appSharePreference.getAds());
    }

    private void UpdateUIForSwitches() {
        if (appSharePreference.getAmPm()) {
            bind.amPm.setVisibility(View.VISIBLE);
        }
        if (appSharePreference.getSeconds()) {
            bind.seconds.setVisibility(View.VISIBLE);
        }
        if (appSharePreference.getWeather()) {
            bind.temperature.setVisibility(View.VISIBLE);
        }
    }

    private void DismissDialog() {
        if (alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        UpdateUIForSwitches();

    }

}

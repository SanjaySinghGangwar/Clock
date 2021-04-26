package com.sanjaysgangwar.clock.modelClass;


import android.content.Context;
import android.content.SharedPreferences;

public class AppSharePreference {
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private String APP_SHARED_PREFS;

    public AppSharePreference(Context mContext) {
        sharedPreferences = mContext.getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        APP_SHARED_PREFS = "DeskClock";
    }

    public void clearPreferences() {
        editor.clear();
        editor.commit();
    }

    public Boolean getWeather() {
        return sharedPreferences.getBoolean("Weather", false);
    }

    public void setWeather(Boolean weather) {
        editor.putBoolean("Weather", weather);
        editor.commit();
    }

    public Boolean getAmPm() {
        return sharedPreferences.getBoolean("Am_Pm", false);
    }

    public void setAmPm(Boolean AmPm) {
        editor.putBoolean("Am_Pm", AmPm);
        editor.commit();
    }

    public Boolean getSeconds() {
        return sharedPreferences.getBoolean("Sec", false);
    }

    public void setSeconds(Boolean Seconds) {
        editor.putBoolean("Sec", Seconds);
        editor.commit();
    }

    public Boolean getAds() {
        return sharedPreferences.getBoolean("ads", false);
    }

    public void setAds(Boolean Seconds) {
        editor.putBoolean("ads", Seconds);
        editor.commit();
    }


}

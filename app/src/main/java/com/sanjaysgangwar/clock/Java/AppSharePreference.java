package com.sanjaysgangwar.clock.Java;


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

    public String getWeather() {
        return sharedPreferences.getString("weather", "");
    }

    public void setWeather(String weather) {
        editor.putString("weather", weather);
        editor.commit();
    }

    public String getAmPm() {
        return sharedPreferences.getString("AmPm", "");
    }

    public void setAmPm(String AmPm) {
        editor.putString("AmPm", AmPm);
        editor.commit();
    }

    public String getSeconds() {
        return sharedPreferences.getString("Seconds", "");
    }

    public void setSeconds(String Seconds) {
        editor.putString("Seconds", Seconds);
        editor.commit();
    }


}

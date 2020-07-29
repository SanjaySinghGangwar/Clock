package com.sanjaysgangwar.clock.api;

import com.sanjaysgangwar.clock.modelClass.wheatherModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("data/2.5/weather/")
    Call<wheatherModel> getWeather(@Query("lat") Double lat,
                                   @Query("lon") Double lon,
                                   @Query("appid") String appid);
}


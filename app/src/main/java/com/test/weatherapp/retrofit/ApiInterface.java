package com.test.weatherapp.retrofit;

import com.test.weatherapp.data.model.currentweather.CurrentWeatherResponse;
import com.test.weatherapp.data.model.currentweather.FiveDaysWeather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.Url;


public interface ApiInterface {


    //@Headers({"Content-Type: application/json","companycode: 0001","username: R35","password: R35","employeecode: R35"})
    //@Headers({"Content-Type: application/json","mobile: 9928100718","password: 123456"})

    @Headers({"CONNECT_TIMEOUT:30000", "READ_TIMEOUT:30000", "WRITE_TIMEOUT:30000"})
    @GET
    Call<CurrentWeatherResponse> getWeatherData(@Url String url,@Query("q") String q, @Query("units") String units, @Query("lang") String lang, @Query("appid") String appId);

    @Headers({"CONNECT_TIMEOUT:30000", "READ_TIMEOUT:30000", "WRITE_TIMEOUT:30000"})
    @GET
    Call<CurrentWeatherResponse> getCurrentWeatherData(@Url String url,@Query("lat") String lat,@Query("lon") String lon, @Query("units") String units, @Query("lang") String lang, @Query("appid") String appId);

    @Headers({"CONNECT_TIMEOUT:30000", "READ_TIMEOUT:30000", "WRITE_TIMEOUT:30000"})
    @GET
    Call<FiveDaysWeather> getFiveDayWeatherData(@Url String url, @Query("lat") String lat, @Query("lon") String lon, @Query("units") String units, @Query("lang") String lang, @Query("appid") String appId);

}


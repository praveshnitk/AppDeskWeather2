package com.test.weatherapp.ui.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.test.weatherapp.R;
import com.test.weatherapp.data.model.currentweather.CurrentWeatherResponse;
import com.test.weatherapp.data.model.currentweather.FiveDaysWeather;
import com.test.weatherapp.data.repository.MainRepository;
import com.test.weatherapp.retrofit.ApiClient;
import com.test.weatherapp.retrofit.ApiInterface;
import com.test.weatherapp.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;

public class MainViewModel extends ViewModel {

    private MainRepository mainRepository;
    Context context;
    private MutableLiveData<CurrentWeatherResponse> mutableLiveData = new MutableLiveData<>();
    private MutableLiveData<FiveDaysWeather> fiveDaysWeatherMutableLiveData = new MutableLiveData<>();

    public MainViewModel(MainRepository mainRepository, Context context) {
        this.mainRepository = mainRepository;
        this.context=context;
    }

    public LiveData<CurrentWeatherResponse> getWeatherData() {
        return mutableLiveData;
    }
    public LiveData<FiveDaysWeather> getFiveDaysWeatherData() {
        return fiveDaysWeatherMutableLiveData;
    }


    public void weatherApi(String cityName){
        try
        {
            String apiKey = context.getResources().getString(R.string.open_weather_map_api);
            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            Call<CurrentWeatherResponse> call = apiService.getWeatherData("https://api.openweathermap.org/data/2.5/weather?",cityName, Constants.UNITS,Constants.LANGUAGE,apiKey);
            call.enqueue(new Callback<CurrentWeatherResponse>() {
                @Override
                public void onResponse(Call<CurrentWeatherResponse> call, retrofit2.Response<CurrentWeatherResponse> response) {
                    mutableLiveData.setValue(response.body());
                }

                @Override
                public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {
                    Log.i("tag", t.toString());
                }
            });
        }catch (Exception ex){
            Log.i("tag",ex.toString());
        }
    }

    public void weather5Api(String cityName){
        try
        {
            String apiKey = context.getResources().getString(R.string.open_weather_map_api);
            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            Call<CurrentWeatherResponse> call = apiService.getWeatherData("https://api.openweathermap.org/data/2.5/forecast?",cityName, Constants.UNITS,Constants.LANGUAGE,apiKey);
            call.enqueue(new Callback<CurrentWeatherResponse>() {
                @Override
                public void onResponse(Call<CurrentWeatherResponse> call, retrofit2.Response<CurrentWeatherResponse> response) {
                    mutableLiveData.setValue(response.body());
                }

                @Override
                public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {
                    Log.i("tag", t.toString());
                }
            });
        }catch (Exception ex){
            Log.i("tag",ex.toString());
        }
    }

    public void currentWeatherApi(String lat, String lon){
        try
        {
            String apiKey = context.getResources().getString(R.string.open_weather_map_api);
            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            Call<CurrentWeatherResponse> call = apiService.getCurrentWeatherData("https://api.openweathermap.org/data/2.5/weather?",lat,lon, Constants.UNITS,Constants.LANGUAGE,apiKey);
            call.enqueue(new Callback<CurrentWeatherResponse>() {
                @Override
                public void onResponse(Call<CurrentWeatherResponse> call, retrofit2.Response<CurrentWeatherResponse> response) {
                    mutableLiveData.setValue(response.body());
                }

                @Override
                public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {
                    Log.i("tag", t.toString());
                }
            });
        }catch (Exception ex){
            Log.i("tag",ex.toString());
        }
    }

    public void fiveDaysWeatherApi(String lat, String lon){
        try
        {
            String apiKey = context.getResources().getString(R.string.open_weather_map_api);
            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            Call<FiveDaysWeather> call = apiService.getFiveDayWeatherData("https://api.openweathermap.org/data/2.5/forecast?",lat,lon, Constants.UNITS,Constants.LANGUAGE,apiKey);
            call.enqueue(new Callback<FiveDaysWeather>() {
                @Override
                public void onResponse(Call<FiveDaysWeather> call, retrofit2.Response<FiveDaysWeather> response) {
                    fiveDaysWeatherMutableLiveData.setValue(response.body());
                }

                @Override
                public void onFailure(Call<FiveDaysWeather> call, Throwable t) {
                    Log.i("tag", t.toString());
                }
            });
        }catch (Exception ex){
            Log.i("tag",ex.toString());
        }
    }

}

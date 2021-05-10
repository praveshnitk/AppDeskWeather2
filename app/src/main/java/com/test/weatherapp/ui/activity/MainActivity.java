package com.test.weatherapp.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.test.weatherapp.R;
import com.test.weatherapp.data.model.CityInfo;
import com.test.weatherapp.data.model.currentweather.CurrentWeatherResponse;
import com.test.weatherapp.data.model.currentweather.FiveDaysWeather;
import com.test.weatherapp.databinding.ActivityMainBinding;
import com.test.weatherapp.helper.DataBaseHelper;
import com.test.weatherapp.helper.PreferenceHelper;
import com.test.weatherapp.interfaces.SpinnerSelectionListener;
import com.test.weatherapp.ui.dialog.WeatherCityList;
import com.test.weatherapp.ui.dialog.WeatherForcastList;
import com.test.weatherapp.ui.factory.MainModelFactory;
import com.test.weatherapp.ui.viewmodel.MainViewModel;
import com.test.weatherapp.utils.AppUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SpinnerSelectionListener, LocationListener {

    private MainViewModel ViewModel;

    ActivityMainBinding binding;
    private MaterialSearchView searchView;
    private TextView cityName;
    private DataBaseHelper dbHelper;
    private CityInfo cityInfo = null;
    private PreferenceHelper pHelper;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    String currentCity = "";
    String lat = "28.7041";
    String lon = "77.1025";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbHelper = new DataBaseHelper(MainActivity.this);
        pHelper = new PreferenceHelper(this);
        searchView = findViewById(R.id.search_view);
        cityName = findViewById(R.id.city_name_text_view);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        initSearchView();
        ViewModel = new ViewModelProvider(this, new MainModelFactory(MainActivity.this)).get(MainViewModel.class);

        ViewModel.getWeatherData().observe(this, new Observer<CurrentWeatherResponse>() {
            @Override
            public void onChanged(@Nullable CurrentWeatherResponse currentWeatherResponse) {
                Log.i("tag", "");
                if (currentWeatherResponse!=null){
                    showCurrentWeather(currentWeatherResponse);
                    binding.swipeContainer.setRefreshing(false);
                    cityInfo = new CityInfo();
                    cityInfo.setCountry(currentWeatherResponse.getSys().getCountry());
                    cityInfo.setId(currentWeatherResponse.getId());
                    cityInfo.setName(currentWeatherResponse.getName());
                    cityName.setText(currentWeatherResponse.getName()+ " , "+currentWeatherResponse.getSys().getCountry());

                    pHelper.setString("cityName",currentWeatherResponse.getName());
                    pHelper.setString("cityCountry",currentWeatherResponse.getSys().getCountry());
                    pHelper.setString("description",currentWeatherResponse.getWeather().get(0).getDescription());
                    pHelper.setDouble("temprature",currentWeatherResponse.getMain().getTemp());
                    pHelper.setInt("humidity",currentWeatherResponse.getMain().getHumidity());
                    pHelper.setDouble("speed",currentWeatherResponse.getWind().getSpeed());
                }else {
                    Toast.makeText(MainActivity.this, "This location weather not found.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        ViewModel.getFiveDaysWeatherData().observe(this, new Observer<FiveDaysWeather>() {
            @Override
            public void onChanged(FiveDaysWeather fiveDaysWeather) {
                Log.i("tag","");
                ArrayList<CurrentWeatherResponse> currentWeatherResponses = (ArrayList<CurrentWeatherResponse>) fiveDaysWeather.getList();
                new WeatherForcastList().weatherData(MainActivity.this,currentWeatherResponses);
            }
        });

        binding.btnAddtoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cityInfo!=null && !cityInfo.getName().isEmpty()){
                    if (dbHelper.cityExists(cityInfo.getName(),cityInfo.getCountry())){
                        Toast.makeText(MainActivity.this, "Same city already exists", Toast.LENGTH_SHORT).show();
                    }else {
                        dbHelper.insertCityListData(cityInfo.getName(),cityInfo.getCountry());
                        Toast.makeText(MainActivity.this, "com.test.weatherapp.data.model.currentweather.City added successfully", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(MainActivity.this, "Same city already exists", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.btnMyList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<CityInfo> cityInfoArrayList = dbHelper.cityData();
                new WeatherCityList().cityList(MainActivity.this,cityInfoArrayList);
            }
        });

        binding.btnCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewModel.currentWeatherApi(lat,lon);
            }
        });

        binding.btnSevenDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewModel.fiveDaysWeatherApi(lat,lon);
            }
        });

        requestWeather(pHelper.getString("cityName",currentCity), false);

        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(pHelper.getString("cityName",""), false);
            }
        });
    }

    private void initSearchView() {
        searchView.setVoiceSearch(false);
        searchView.setHint(getString(R.string.search_label));
        searchView.setCursorDrawable(R.drawable.custom_curosr);
        searchView.setEllipsize(true);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                requestWeather(query, true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.showSearch();
            }
        });

    }

    private void requestWeather(String cityName, boolean isSearch) {
        if (AppUtil.isNetworkConnected()){
            ViewModel.weatherApi(cityName);
        }else {
            Toast.makeText(this, "Please check internet connection", Toast.LENGTH_SHORT).show();
            setDefaultWeather();
        }
    }

    private void setDefaultWeather() {
        cityName.setText(pHelper.getString("cityName","")+ " , "+pHelper.getString("cityCountry",""));
        binding.tempTextView.setText(String.format(Locale.getDefault(), "%.0f°C", pHelper.getDouble("temprature",0)));
        binding.descriptionTextView.setText(pHelper.getString("description",""));
        binding.humidityTextView.setText(String.format(Locale.getDefault(), "%d%%", pHelper.getInt("humidity",0)));
        binding.windTextView.setText(String.format(Locale.getDefault(), getResources().getString(R.string.wind_unit_label), pHelper.getDouble("speed",0)));
    }

    private void showCurrentWeather(CurrentWeatherResponse currentWeatherResponse) {

        Log.i("tag","Temp: "+currentWeatherResponse.getMain().getTemp()+" Desc: "+currentWeatherResponse.getWeather().get(0).getDescription()+" Humidity : "+currentWeatherResponse.getMain().getHumidity()+" Wind : "+currentWeatherResponse.getWind().getSpeed());
        binding.tempTextView.setText(String.format(Locale.getDefault(), "%.0f°C", currentWeatherResponse.getMain().getTemp()));
        binding.descriptionTextView.setText(currentWeatherResponse.getWeather().get(0).getDescription());
        binding.humidityTextView.setText(String.format(Locale.getDefault(), "%d%%", currentWeatherResponse.getMain().getHumidity()));
        binding.windTextView.setText(String.format(Locale.getDefault(), getResources().getString(R.string.wind_unit_label), currentWeatherResponse.getWind().getSpeed()));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    public void onSectionChanged(String city, int Position) {
        requestWeather(city, false);
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.i("tag","Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());
        Geocoder geocoder = null;
        List<Address> addresses = null;
        try {
            geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (addresses.get(0).getLocality()!=""){
                currentCity = addresses.get(0).getAdminArea();
            }
            Log.i("tag",currentCity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
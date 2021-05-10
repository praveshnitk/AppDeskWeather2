package com.test.weatherapp.ui.activity.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.test.weatherapp.R;
import com.test.weatherapp.data.model.CityInfo;
import com.test.weatherapp.data.model.currentweather.CurrentWeatherResponse;
import com.test.weatherapp.helper.DataBaseHelper;
import com.test.weatherapp.helper.PreferenceHelper;
import com.test.weatherapp.interfaces.SpinnerSelectionListener;
import com.test.weatherapp.ui.activity.MainActivity;
import com.test.weatherapp.ui.activity.ui.home.HomeViewModel;
import com.test.weatherapp.ui.dialog.WeatherCityList;
import com.test.weatherapp.ui.factory.MainModelFactory;
import com.test.weatherapp.ui.viewmodel.MainViewModel;
import com.test.weatherapp.utils.AppUtil;

import java.util.ArrayList;
import java.util.Locale;

public class DashboardFragment extends Fragment implements SpinnerSelectionListener {

    private DashboardViewModel dashboardViewModel;
    private ViewDataBinding binding;
    private Button btnAddtoList,btnMyList;
    private TextView cityName;
    private DataBaseHelper dbHelper;
    private CityInfo cityInfo=null;
    private PreferenceHelper pHelper;
    private MaterialSearchView searchView;
    private TextView tempTextView,descriptionTextView,humidityTextView,windTextView;
    private SwipeRefreshLayout swipeContainer;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        btnAddtoList = root.findViewById(R.id.btnAddtoList);
        btnMyList = root.findViewById(R.id.btnMyList);
        dbHelper = new DataBaseHelper(getContext());
        pHelper = new PreferenceHelper(getContext());
        searchView = root.findViewById(R.id.search_view);
        cityName = root.findViewById(R.id.city_name_text_view);
        tempTextView = root.findViewById(R.id.temp_text_view);
        descriptionTextView = root.findViewById(R.id.description_text_view);
        humidityTextView = root.findViewById(R.id.humidity_text_view);
        windTextView = root.findViewById(R.id.wind_text_view);
        swipeContainer = root.findViewById(R.id.swipe_container);

        initSearchView();
        //dashboardViewModel = new ViewModelProvider(this, new MainModelFactory(getActivity())).get(MainViewModel.class);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        dashboardViewModel.getWeatherData().observe(getActivity(), new Observer<CurrentWeatherResponse>() {
            @Override
            public void onChanged(@Nullable CurrentWeatherResponse currentWeatherResponse) {
                Log.i("tag", "");
                showCurrentWeather(currentWeatherResponse);
                swipeContainer.setRefreshing(false);
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
            }
        });

        btnAddtoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cityInfo!=null && !cityInfo.getName().isEmpty()){
                    if (dbHelper.cityExists(cityInfo.getName(),cityInfo.getCountry())){
                        Toast.makeText(getContext(), "Same city already exists", Toast.LENGTH_SHORT).show();
                    }else {
                        dbHelper.insertCityListData(cityInfo.getName(),cityInfo.getCountry());
                        Toast.makeText(getContext(), "com.test.weatherapp.data.model.currentweather.City added successfully", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getContext(), "Same city already exists", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnMyList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<CityInfo> cityInfoArrayList = dbHelper.cityData();
                new WeatherCityList().cityList(getContext(),cityInfoArrayList);
            }
        });
        requestWeather(pHelper.getString("cityName","Delhi"), false);

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(pHelper.getString("cityName",""), false);
            }
        });
        return root;
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
            dashboardViewModel.weatherApi(cityName);
        }else {
            Toast.makeText(getContext(), "Please check internet connection", Toast.LENGTH_SHORT).show();
            setDefaultWeather();
        }
    }

    private void setDefaultWeather() {
        cityName.setText(pHelper.getString("cityName","")+ " , "+pHelper.getString("cityCountry",""));
        tempTextView.setText(String.format(Locale.getDefault(), "%.0f°C", pHelper.getDouble("temprature",0)));
        descriptionTextView.setText(pHelper.getString("description",""));
        humidityTextView.setText(String.format(Locale.getDefault(), "%d%%", pHelper.getInt("humidity",0)));
        windTextView.setText(String.format(Locale.getDefault(), getResources().getString(R.string.wind_unit_label), pHelper.getDouble("speed",0)));
    }

    private void showCurrentWeather(CurrentWeatherResponse currentWeatherResponse) {

        Log.i("tag","Temp: "+currentWeatherResponse.getMain().getTemp()+" Desc: "+currentWeatherResponse.getWeather().get(0).getDescription()+" Humidity : "+currentWeatherResponse.getMain().getHumidity()+" Wind : "+currentWeatherResponse.getWind().getSpeed());
        tempTextView.setText(String.format(Locale.getDefault(), "%.0f°C", currentWeatherResponse.getMain().getTemp()));
        descriptionTextView.setText(currentWeatherResponse.getWeather().get(0).getDescription());
        humidityTextView.setText(String.format(Locale.getDefault(), "%d%%", currentWeatherResponse.getMain().getHumidity()));
        windTextView.setText(String.format(Locale.getDefault(), getResources().getString(R.string.wind_unit_label), currentWeatherResponse.getWind().getSpeed()));

    }

    @Override
    public void onSectionChanged(String city, int Position) {
        requestWeather(city, false);
    }
}
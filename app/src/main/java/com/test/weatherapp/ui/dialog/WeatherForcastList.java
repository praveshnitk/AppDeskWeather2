package com.test.weatherapp.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.test.weatherapp.R;
import com.test.weatherapp.data.model.CityInfo;
import com.test.weatherapp.data.model.currentweather.CurrentWeatherResponse;
import com.test.weatherapp.ui.adapter.ForcastAdapter;
import com.test.weatherapp.ui.adapter.SimpleSpinnerAdapter;

import java.util.ArrayList;

public class WeatherForcastList {
    Context context;
    private Dialog dialog;

    public void weatherData(Context context, ArrayList<CurrentWeatherResponse> weatherResponses){
        try {
            this.context = context;

            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.weather_forcast_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(true);

            RecyclerView mRecyclerView = (RecyclerView) dialog.findViewById(R.id.data_list);
            mRecyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
            mRecyclerView.setLayoutManager(mLayoutManager);
            TextView textClose = (TextView) dialog.findViewById(R.id.textClose);
            TextView txtNoRecord = (TextView) dialog.findViewById(R.id.txtNoRecord);
            if (weatherResponses.size() > 0) {
                mRecyclerView.setVisibility(View.VISIBLE);
                txtNoRecord.setVisibility(View.GONE);
                final ForcastAdapter forcastAdapter = new ForcastAdapter(context, weatherResponses);
                mRecyclerView.setAdapter(forcastAdapter);
                forcastAdapter.notifyDataSetChanged();
            } else {
                mRecyclerView.setVisibility(View.GONE);
                txtNoRecord.setVisibility(View.VISIBLE);
            }

            textClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }catch (Exception ex){
            Log.i("tag",ex.toString());
        }
    }
}

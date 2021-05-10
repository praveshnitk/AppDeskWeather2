package com.test.weatherapp.ui.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.test.weatherapp.R;
import com.test.weatherapp.data.model.CityInfo;
import com.test.weatherapp.data.model.currentweather.CurrentWeatherResponse;
import com.test.weatherapp.interfaces.SpinnerSelectionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ForcastAdapter extends RecyclerView.Adapter<ForcastAdapter.MyRecyclerViewHolder> {

    private final Context mActivity;
    private final List<CurrentWeatherResponse> mList;
    public ForcastAdapter(Context mActivity, ArrayList<CurrentWeatherResponse> mList) {
        this.mList = mList;
        this.mActivity=mActivity;
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewHolder contactViewHolder, int position) {
        contactViewHolder.tempTextView.setText(String.format(Locale.getDefault(), "%.0f°C", mList.get(position).getMain().getTemp()));
        contactViewHolder.descriptionTextView.setText(mList.get(position).getWeather().get(0).getDescription());
        contactViewHolder.tvDate.setText(mList.get(position).getDt_txt());
        contactViewHolder.humidityTextView.setText(String.format(Locale.getDefault(), "%d%%", mList.get(position).getMain().getHumidity()));
        contactViewHolder.windTextView.setText(String.format(Locale.getDefault(), mActivity.getResources().getString(R.string.wind_unit_label), mList.get(position).getWind().getSpeed()));
        contactViewHolder.tvTempMin.setText(String.format(Locale.getDefault(), "%.0f°C", mList.get(position).getMain().getTempMin()));
        contactViewHolder.tvTempMax.setText(String.format(Locale.getDefault(), "%.0f°C", mList.get(position).getMain().getTempMax()));
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @NonNull
    @Override
    public MyRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.forcast_items, parent, false);
        return new MyRecyclerViewHolder(view);
    }
    public class MyRecyclerViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public TextView tempTextView,descriptionTextView,humidityTextView,windTextView,tvDate,tvTempMin,tvTempMax;
        public MyRecyclerViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tempTextView = (TextView) itemView.findViewById(R.id.temp_text_view);
            descriptionTextView = (TextView) itemView.findViewById(R.id.description_text_view);
            humidityTextView = (TextView) itemView.findViewById(R.id.humidity_text_view);
            windTextView = (TextView) itemView.findViewById(R.id.wind_text_view);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvTempMin = (TextView) itemView.findViewById(R.id.tvTempMin);
            tvTempMax = (TextView) itemView.findViewById(R.id.tvTempMax);
        }
    }
}

package com.test.weatherapp.ui.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.test.weatherapp.R;
import com.test.weatherapp.data.model.CityInfo;
import com.test.weatherapp.interfaces.SpinnerSelectionListener;

import java.util.ArrayList;
import java.util.List;


public class SimpleSpinnerAdapter extends RecyclerView.Adapter<SimpleSpinnerAdapter.MyRecyclerViewHolder> {

    private final Context mActivity;
    private final List<CityInfo> mList;
    private Dialog mDialog;
    private Integer mClickPositions;
    private LinearLayout linearLayoutSubjectGrade,linearLayoutDmOption;
    public SimpleSpinnerAdapter(Context mActivity, ArrayList<CityInfo> mList, Dialog mDialog) {
        this.mList = mList;
        this.mActivity=mActivity;
        this.mDialog=mDialog;
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewHolder contactViewHolder, int position) {
        contactViewHolder.spinnerText.setTag(position);
        contactViewHolder.spinnerText.setText(mList.get(position).getName()+" - "+mList.get(position).getCountry());
        contactViewHolder.spinnerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickPositions = (Integer) view.getTag();
                String Value = mList.get(mClickPositions).getName();

                ((SpinnerSelectionListener)mActivity).onSectionChanged(Value,mClickPositions);

                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                    mDialog = null;
                }
            }
        });
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
        View view = LayoutInflater.from(mActivity).inflate(R.layout.simple_spinner_items, parent, false);
        return new MyRecyclerViewHolder(view);
    }
    public class MyRecyclerViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public TextView spinnerText;
        public MyRecyclerViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            spinnerText = (TextView) itemView.findViewById(R.id.spinnerText);
        }
    }
}

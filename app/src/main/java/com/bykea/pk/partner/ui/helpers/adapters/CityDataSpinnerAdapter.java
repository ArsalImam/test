package com.bykea.pk.partner.ui.helpers.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.CitiesData;
import com.bykea.pk.partner.widgets.AutoFitFontTextView;

import java.util.ArrayList;

public class CityDataSpinnerAdapter extends BaseAdapter {
    private ArrayList<CitiesData> mDataList;
    private LayoutInflater inflter;

    public CityDataSpinnerAdapter(Context applicationContext, ArrayList<CitiesData> list) {
        mDataList = list;
        inflter = (LayoutInflater.from(applicationContext));
    }


    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int i) {
        return mDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.air_ticket_spinner_item, null);
        AutoFitFontTextView name = (AutoFitFontTextView) view.findViewById(R.id.tv_item);
        name.setText(mDataList.get(i).getName());
        if (i == mDataList.size() - 1) {
            view.findViewById(R.id.singleViewLine).setVisibility(View.INVISIBLE);
        } else {
            view.findViewById(R.id.singleViewLine).setVisibility(View.VISIBLE);
        }
        return view;
    }
}

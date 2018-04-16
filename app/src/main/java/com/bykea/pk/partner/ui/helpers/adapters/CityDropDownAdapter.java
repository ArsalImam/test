package com.bykea.pk.partner.ui.helpers.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.SignUpCity;
import com.bykea.pk.partner.models.data.SignUpSettingsResponse;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class CityDropDownAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<SignUpCity> mDataList;
    private LayoutInflater inflter;

    public CityDropDownAdapter(Context applicationContext, ArrayList<SignUpCity> list) {
        this.context = applicationContext;
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
        view = inflter.inflate(R.layout.city_drop_down_item, null);
        FontTextView name = (FontTextView) view.findViewById(R.id.titleName);
        FontTextView urduName = (FontTextView) view.findViewById(R.id.titleUrduName);
        name.setText(mDataList.get(i).getName());
        if (StringUtils.isNotBlank(mDataList.get(i).getUrduName())) {
            urduName.setVisibility(View.VISIBLE);
            urduName.setTextColor(ContextCompat.getColor(context, R.color.textColorPrimary));
            urduName.setText(mDataList.get(i).getUrduName());
        } else {
            urduName.setVisibility(View.INVISIBLE);
        }
        if (i == mDataList.size() - 1) {
            view.findViewById(R.id.singleViewLine).setVisibility(View.INVISIBLE);
        } else {
            view.findViewById(R.id.singleViewLine).setVisibility(View.VISIBLE);
        }
        return view;
    }
}
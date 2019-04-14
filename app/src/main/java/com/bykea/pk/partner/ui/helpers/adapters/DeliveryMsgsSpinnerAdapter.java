package com.bykea.pk.partner.ui.helpers.adapters;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.widgets.FontTextView;

import java.util.ArrayList;

public class DeliveryMsgsSpinnerAdapter extends BaseAdapter {
    private String[] mDataList;
    private LayoutInflater inflter;
    private Context mContext;

    public DeliveryMsgsSpinnerAdapter(Context applicationContext, String[] list) {
        mDataList = list;
        mContext = applicationContext;
        inflter = (LayoutInflater.from(applicationContext));
    }


    @Override
    public int getCount() {
        return mDataList.length;
    }

    @Override
    public Object getItem(int i) {
        return mDataList[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.simple_text_view, null);
        FontTextView name = (FontTextView) view.findViewById(R.id.tvItem);
        name.setText(mDataList[i]);
        name.setTextColor(ContextCompat.getColor(mContext, i == 0 ? R.color.blue_dark : R.color.color_error));
        return view;
    }
}

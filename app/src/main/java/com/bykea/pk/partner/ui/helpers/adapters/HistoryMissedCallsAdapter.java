package com.bykea.pk.partner.ui.helpers.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.TripHistoryData;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.DashedLine;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class HistoryMissedCallsAdapter extends RecyclerView.Adapter<HistoryMissedCallsAdapter.ItemHolder> {

    private static ArrayList<TripHistoryData> mHistoryList;
    private final static String REQUIRED_DATE_FORMAT = "dd MMM, hh:mm a";
    private final static String CURRENT_DATE_FORMAT = "HH:mm:ss MM-dd-yyyy";//7:48:40 1-6-2017
    private final static String CURRENT_DATE_FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public HistoryMissedCallsAdapter(Context context, ArrayList<TripHistoryData> list) {
        mHistoryList = list;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.list_item_trip_history_missed_calls,
                parent, false);

        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {

        TripHistoryData data = mHistoryList.get(position);
        if (data.getStatus().equalsIgnoreCase("missed")) {
            holder.tripNoTv.setText(data.getTrip_id() != null && StringUtils.isNotBlank(data.getTrip_id().getTrip_no())
                    ? data.getTrip_id().getTrip_no() : StringUtils.EMPTY);
            holder.dateTv.setText(Utils.getFormattedDateUTC(data.getCreated_at(), CURRENT_DATE_FORMAT_ISO,
                    REQUIRED_DATE_FORMAT));
        }

    }

    @Override
    public int getItemCount() {
        return mHistoryList.size();
    }


    static class ItemHolder extends RecyclerView.ViewHolder {


        FontTextView tripNoTv;
        FontTextView dateTv;


        ItemHolder(final View itemView) {
            super(itemView);

            tripNoTv = (FontTextView) itemView.findViewById(R.id.tripNoTv);
            dateTv = (FontTextView) itemView.findViewById(R.id.dateTv);
        }
    }


}
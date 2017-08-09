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

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ItemHolder> {

    private Context mContext;
    private static ArrayList<TripHistoryData> mHistoryList;
    private static MyOnItemClickListener myOnItemClickListener;
    private final static String REQUIRED_DATE_FORMAT = "dd MMM, hh:mm a";
    private final static String CURRENT_DATE_FORMAT = "HH:mm:ss MM-dd-yyyy";//7:48:40 1-6-2017
    private final static String CURRENT_DATE_FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public HistoryAdapter(Context context, ArrayList<TripHistoryData> list) {
        mHistoryList = list;
        this.mContext = context;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.list_item_trip_history,
                parent, false);
        ItemHolder holder = new ItemHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {

        TripHistoryData data = mHistoryList.get(position);
        Utils.redLog("Trip No", data.getTripNo());
        if (data.getStatus().equalsIgnoreCase("cancelled")
                || data.getStatus().equalsIgnoreCase("missed")) {
            holder.detailsImage.setVisibility(View.INVISIBLE);
            holder.dotted_line.setVisibility(View.GONE);
            holder.ic_pin.setVisibility(View.GONE);
            holder.endAddressTv.setVisibility(View.GONE);
        } else {
            holder.detailsImage.setVisibility(View.VISIBLE);
            holder.dotted_line.setVisibility(View.VISIBLE);
            holder.ic_pin.setVisibility(View.VISIBLE);
            holder.endAddressTv.setVisibility(View.VISIBLE);
        }

        if (null != data.getPassRating() && StringUtils.isNotBlank(data.getPassRating().getRate())
                && !data.getPassRating().getRate().equalsIgnoreCase("0")) {
            holder.ratingValueTv.setText("Rating " + Utils.formatDecimalPlaces(data.getPassRating().getRate()));
        } else {
            holder.ratingValueTv.setText("Rating -");
        }


        String time[] = null;
        if (data.getStatus().equalsIgnoreCase("missed")) {
            holder.tripNoTv.setText(data.getTrip_id().getTrip_no());
        } else {
            holder.tripNoTv.setText(data.getTripNo());
        }
        if (data.getStatus().equalsIgnoreCase("completed")) {
            if (data.getInvoice() != null && StringUtils.isNotBlank(data.getInvoice().getTotal())) {
                holder.totalAmountTv.setText("Rs. " + data.getInvoice().getTotal());
            }
            holder.status.setText("Completed");
            holder.status.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            holder.totalAmountTv.setVisibility(View.VISIBLE);
            holder.dateTv.setText(Utils.getFormattedDate(StringUtils.isNotBlank(data.getFinishTime())
                            ? data.getFinishTime() : data.getAcceptTime(), CURRENT_DATE_FORMAT,
                    REQUIRED_DATE_FORMAT));
        } else if (data.getStatus().equalsIgnoreCase("cancelled")) {
            holder.status.setText(StringUtils.isNotBlank(data.getCancel_by()) ? data.getCancel_by() + " Cancelled" : "Admin Cancelled");
            holder.status.setTextColor(ContextCompat.getColor(mContext, R.color.color_error));
            holder.totalAmountTv.setVisibility(View.GONE);
            if (StringUtils.isNotBlank(data.getCancelTime())) {
                holder.dateTv.setText(Utils.getFormattedDate(data.getCancelTime(), CURRENT_DATE_FORMAT,
                        REQUIRED_DATE_FORMAT));
            } else {
                holder.dateTv.setText(Utils.getFormattedDate(data.getCreated_at(), CURRENT_DATE_FORMAT,
                        REQUIRED_DATE_FORMAT));
            }
        } else if (data.getStatus().equalsIgnoreCase("missed")) {
            holder.status.setText("Missed");
            holder.status.setTextColor(ContextCompat.getColor(mContext, R.color.color_error));
            holder.totalAmountTv.setVisibility(View.GONE);
            holder.dateTv.setText(Utils.getFormattedDate(data.getCreated_at(), CURRENT_DATE_FORMAT_ISO,
                    REQUIRED_DATE_FORMAT));
        } else if (data.getStatus().equalsIgnoreCase("feedback")) {
            if (data.getInvoice() != null && StringUtils.isNotBlank(data.getInvoice().getTotal())) {
                holder.totalAmountTv.setText("Rs. " + data.getInvoice().getTotal());
            }
            holder.status.setText("Completed");
            holder.status.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            holder.totalAmountTv.setVisibility(View.VISIBLE);
            holder.dateTv.setText(Utils.getFormattedDate(data.getFinishTime(), CURRENT_DATE_FORMAT,
                    REQUIRED_DATE_FORMAT));

        }

        if (data.is_verified()) {
            holder.verifiedTv.setText("Verified");
            holder.verifiedTv.setTextColor(ContextCompat.getColor(mContext, R.color.color_success));
        } else {
            holder.verifiedTv.setTextColor(ContextCompat.getColor(mContext, R.color.color_error));
            holder.verifiedTv.setText("Unverified");
        }
        if (StringUtils.isBlank(data.getStartAddress())) {
            holder.startAddressTv.setVisibility(View.GONE);
            holder.green_dot.setVisibility(View.GONE);
        } else {
            holder.startAddressTv.setVisibility(View.VISIBLE);
            holder.green_dot.setVisibility(View.VISIBLE);
            holder.startAddressTv.setText(data.getStartAddress());
        }
        if (StringUtils.isBlank(data.getEndAddress())) {
            holder.endAddressTv.setText("Passenger Guided.");
        } else {
            holder.endAddressTv.setText(data.getEndAddress());
        }
    }

    @Override
    public int getItemCount() {
        return mHistoryList.size();
    }


    public void setMyOnItemClickListener(MyOnItemClickListener itemClickListener) {
        myOnItemClickListener = itemClickListener;
    }


    public interface MyOnItemClickListener {
        void onItemClickListener(int position, View view, TripHistoryData recentJob);
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {


        FontTextView tripNoTv;
        FontTextView ratingValueTv;
        FontTextView startAddressTv;
        FontTextView endAddressTv;
        FontTextView totalAmountTv;
        FontTextView status;
        FontTextView timeTv;
        FontTextView dateTv;
        FontTextView verifiedTv;
        ImageView detailsImage;
        ImageView ic_pin;
        DashedLine dotted_line;
        ImageView green_dot;
       /* @Bind(R.id.callerRb)
        RatingBar ratingBar;*/


        public ItemHolder(final View itemView) {
            super(itemView);

            detailsImage = (ImageView) itemView.findViewById(R.id.ivRight);
            tripNoTv = (FontTextView) itemView.findViewById(R.id.tripNoTv);
            ratingValueTv = (FontTextView) itemView.findViewById(R.id.ratingValueTv);
            startAddressTv = (FontTextView) itemView.findViewById(R.id.startAddressTv);
            endAddressTv = (FontTextView) itemView.findViewById(R.id.endAddressTv);
            totalAmountTv = (FontTextView) itemView.findViewById(R.id.totalAmountTv);
            status = (FontTextView) itemView.findViewById(R.id.status);
            timeTv = (FontTextView) itemView.findViewById(R.id.timeTv);
            dateTv = (FontTextView) itemView.findViewById(R.id.dateTv);
            verifiedTv = (FontTextView) itemView.findViewById(R.id.verifiedTv);
            ic_pin = (ImageView) itemView.findViewById(R.id.ic_pin);
            dotted_line = (DashedLine) itemView.findViewById(R.id.dotted_line);
            green_dot = (ImageView) itemView.findViewById(R.id.green_dot);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    myOnItemClickListener.onItemClickListener(getLayoutPosition(), v
                            , mHistoryList.get(getLayoutPosition()));
                }
            });
        }
    }


}
package com.bykea.pk.partner.ui.helpers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.TripHistoryData;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Utils;
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

        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {

        TripHistoryData data = mHistoryList.get(position);
        if (data.getStatus().equalsIgnoreCase("missed")) {
            holder.tripNoTv.setText(data.getTrip_id().getTrip_no());
        } else {
            holder.tripNoTv.setText(data.getTripNo());
        }
        if (data.isDd() || data.is_verified()) {
            holder.ivDriverDestination.setVisibility(View.VISIBLE);
            Utils.loadImgPicasso(mContext, holder.ivDriverDestination, Constants.S3_DD_ICON_URL);
        } else if (data.getTrip_status_code() != null
                && data.getTrip_status_code().equalsIgnoreCase(String.valueOf(Constants.ServiceCode.OFFLINE_RIDE))) {
            holder.ivDriverDestination.setVisibility(View.VISIBLE);
            Utils.loadImgPicasso(mContext, holder.ivDriverDestination, Constants.S3_OFFLINE_RIDE_ICON_URL);
        }else if (data.getTrip_status_code() != null
                && data.getTrip_status_code().equalsIgnoreCase(String.valueOf(Constants.ServiceCode.OFFLINE_DELIVERY))) {
            holder.ivDriverDestination.setVisibility(View.VISIBLE);
            Utils.loadImgPicasso(mContext, holder.ivDriverDestination, Constants.S3_OFFLINE_RIDE_ICON_URL);
        } else {
            holder.ivDriverDestination.setVisibility(View.GONE);
        }
        if (data.getStatus().equalsIgnoreCase("completed")) {
            if (data.getInvoice() != null && StringUtils.isNotBlank(data.getInvoice().getTotal())) {
                holder.totalAmountTv.setText("Rs. " + data.getInvoice().getTotal());
            } else {
                holder.totalAmountTv.setText("Rs. N/A");
            }

            holder.status.setText("Completed");
            holder.status.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            holder.totalAmountTv.setVisibility(View.VISIBLE);
            holder.dateTv.setText(Utils.getFormattedDate(StringUtils.isNotBlank(data.getFinishTime())
                            ? data.getFinishTime() : data.getAcceptTime(), CURRENT_DATE_FORMAT,
                    REQUIRED_DATE_FORMAT));
        } else if (data.getStatus().equalsIgnoreCase("cancelled")) {
            holder.status.setText(data.getCancel_by() + " Cancelled");
            holder.status.setTextColor(ContextCompat.getColor(mContext, R.color.color_error));
            holder.totalAmountTv.setVisibility(View.VISIBLE);
//            if (StringUtils.isNotBlank(data.getCancel_feeNoCheck())) {
            holder.totalAmountTv.setText("Rs. " + data.getCancel_fee());
//            } else {
//                holder.totalAmountTv.setText("N/A");
//            }
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
            } else {
                holder.totalAmountTv.setText("Rs. N/A");
            }
            holder.status.setText("Completed");
            holder.status.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            holder.totalAmountTv.setVisibility(View.VISIBLE);
            if (StringUtils.isNotBlank(data.getFinishTime())) {
                holder.dateTv.setText(Utils.getFormattedDate(data.getFinishTime(), CURRENT_DATE_FORMAT,
                        REQUIRED_DATE_FORMAT));
            } else {
                holder.dateTv.setText(Utils.getFormattedDate(data.getFinishTime(), CURRENT_DATE_FORMAT,
                        REQUIRED_DATE_FORMAT));
            }
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

    static class ItemHolder extends RecyclerView.ViewHolder {

        FontTextView tripNoTv;
        FontTextView totalAmountTv;
        FontTextView status;
        FontTextView dateTv;
        ImageView ivDriverDestination;

        ItemHolder(final View itemView) {
            super(itemView);

            dateTv = (FontTextView) itemView.findViewById(R.id.dateTv);
            totalAmountTv = (FontTextView) itemView.findViewById(R.id.totalAmountTv);
            tripNoTv = (FontTextView) itemView.findViewById(R.id.tripNoTv);
            status = (FontTextView) itemView.findViewById(R.id.status);
            ivDriverDestination = (ImageView) itemView.findViewById(R.id.ivDriverDestination);
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
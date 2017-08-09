package com.bykea.pk.partner.ui.helpers.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.WalletData;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WalletHistoryAdapter extends RecyclerView.Adapter<WalletHistoryAdapter.ItemHolder> {

    private Context mContext;
    private ArrayList<WalletData> mHistoryList;
    private final static String REQUIRED_DATE_FORMAT = "dd MMM, hh:mm a";
    private final static String CURRENT_DATE_FORMAT = "HH:mm:ss MM-dd-yyyy";//7:48:53 1-6-2017

    public WalletHistoryAdapter(Context context, ArrayList<WalletData> list) {
        mHistoryList = list;
        this.mContext = context;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_list_item,
                parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        if (position % 2 == 0) {
            holder.rlWalletItem.setBackground(ContextCompat.getDrawable(mContext, R.color.white));
        } else {
            holder.rlWalletItem.setBackground(ContextCompat.getDrawable(mContext, R.color.secondaryColorLight));
        }
        WalletData data = mHistoryList.get(position);
        if (StringUtils.isNotBlank(data.getTitle())) {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setText(data.getTitle());
        } else {
            holder.tvStatus.setVisibility(View.INVISIBLE);
        }

        if (StringUtils.isNotBlank(data.getTransfer())) {
            holder.tvTotalAmount.setVisibility(View.VISIBLE);
            holder.tvTotalAmount.setText(data.getTransfer());
        } else {
            holder.tvTotalAmount.setVisibility(View.INVISIBLE);
        }

        if (StringUtils.isNotBlank(data.getTrip_no())) {
            holder.tvTripId.setText(data.getTrip_no());
            holder.tvTripId.setVisibility(View.VISIBLE);
        } else {
            holder.tvTripId.setVisibility(View.INVISIBLE);
        }
        if (StringUtils.isNotBlank(data.getCreated_at())) {
            holder.tvDate.setVisibility(View.VISIBLE);
            holder.tvDate.setText(Utils.getFormattedDate(data.getCreated_at(), CURRENT_DATE_FORMAT, REQUIRED_DATE_FORMAT));
        } else {
            holder.tvDate.setVisibility(View.INVISIBLE);
        }

        if (StringUtils.isNotBlank(data.getComments())) {
            holder.tvTripStatus.setText("" + data.getComments());
            holder.tvTripStatus.setVisibility(View.VISIBLE);
        } else {
            holder.tvTripStatus.setVisibility(View.INVISIBLE);
        }

        if (StringUtils.isNotBlank(data.getTotal())) {
            holder.tvTotalTripAmount.setText(data.getTotal());
            holder.tvTotalTripAmount.setVisibility(View.VISIBLE);
        } else {
            holder.tvTotalTripAmount.setVisibility(View.INVISIBLE);
        }
        if (StringUtils.isNotBlank(data.getBalance())) {
            if (data.getBalance().equalsIgnoreCase("NaN")) {
                holder.tvBalance.setText("0");
            } else {
                holder.tvBalance.setText(data.getBalance());
            }
            holder.tvBalance.setVisibility(View.VISIBLE);
        } else {
            holder.tvBalance.setVisibility(View.INVISIBLE);
        }

        if (StringUtils.isNotBlank(data.getBalance())) {
            if (data.getBalance().equalsIgnoreCase("NaN")) {
                holder.tvBalance.setText("0");
            } else {
                holder.tvBalance.setText(data.getBalance());
            }
            holder.tvBalance.setVisibility(View.VISIBLE);
        } else {
            holder.tvBalance.setVisibility(View.INVISIBLE);
        }


    }


    @Override
    public int getItemCount() {
        return mHistoryList.size();
    }

/*
    public void addAll(ArrayList<WalletData> list) {
        if(mHistoryList.size() > 0){
            mHistoryList.clear();
        }
        mHistoryList.addAll(list);
    }*/

    public static class ItemHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tvStatus)
        FontTextView tvStatus;
        @Bind(R.id.tvTripId)
        FontTextView tvTripId;
        @Bind(R.id.tvDate)
        FontTextView tvDate;
        /*@Bind(R.id.tvTime)
        FontTextView tvTime;*/
        @Bind(R.id.tvTripStatus)
        FontTextView tvTripStatus;
        @Bind(R.id.tvTotalAmount)
        FontTextView tvTotalAmount;
        @Bind(R.id.tvTotalTripAmount)
        FontTextView tvTotalTripAmount;
        @Bind(R.id.tvBalance)
        FontTextView tvBalance;
        @Bind(R.id.rlWalletItem)
        RelativeLayout rlWalletItem;


        public ItemHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}

package com.bykea.pk.partner.ui.helpers.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.LoadBoardListingData;
import com.bykea.pk.partner.models.data.ZoneData;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ActiveHomeLoadBoardListAdapter extends RecyclerView.Adapter<ActiveHomeLoadBoardListAdapter.ViewHolder> {

    private ArrayList<LoadBoardListingData> mItems;
    private ItemClickListener itemClickListener;
    private Context mContext;

    public ActiveHomeLoadBoardListAdapter(Context context, ArrayList<LoadBoardListingData> items, ItemClickListener itemClickListener) {
        this.mContext = context;
        this.mItems = items;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_active_home_loadboard,
                parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final LoadBoardListingData item = mItems.get(position);

        if(item != null){
            holder.li_LoadboardPickUpTV.setText(item.getPickupZone().getUrduName());
            holder.li_LoadboardDropOffTV.setText(item.getDropoffZone().getUrduName());
            holder.li_LoadboardBookingIdTV.setText(item.getOrderNo());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onClick(item);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.li_LoadboardPickUpTV)
        FontTextView li_LoadboardPickUpTV;
        @BindView(R.id.li_LoadboardDropOffTV)
        FontTextView li_LoadboardDropOffTV;
        @BindView(R.id.li_LoadboardBookingIdTV)
        FontTextView li_LoadboardBookingIdTV;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    itemClickListener.onClick(mItems.get(getAdapterPosition()));
//                }
//            });
        }
    }

    public interface ItemClickListener {
        void onClick(LoadBoardListingData item);
    }
}


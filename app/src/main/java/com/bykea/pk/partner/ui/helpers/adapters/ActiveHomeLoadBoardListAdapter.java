package com.bykea.pk.partner.ui.helpers.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.LoadBoardListingData;
import com.bykea.pk.partner.widgets.FontTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Home screen's bottom sheet loadboard jobs listing adapter
 */
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
            if(item.getPickupZone() != null){
                holder.li_LoadboardPickUpTV.setText(mContext.getString(R.string.pick_drop_name_ur,item.getPickupZone().getUrduName()));
            } else {
                holder.li_LoadboardPickUpTV.setText(mContext.getString(R.string.not_selected_ur));
            }

            if (item.getDropoffZone() != null) {
                holder.li_LoadboardDropOffTV.setText(mContext.getString(R.string.pick_drop_name_ur,item.getDropoffZone().getUrduName()));
            } else {
                holder.li_LoadboardDropOffTV.setText(mContext.getString(R.string.not_selected_ur));
            }
            holder.li_LoadboardBookingIdTV.setText(item.getOrderNo());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onClick(item);
                }
            });
            if(position % 2 == 0){
                holder.itemView.setBackgroundColor(ContextCompat.getColor(DriverApp.getContext(),R.color.white));
            } else {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(DriverApp.getContext(),R.color.color_grayf8f8f8));
            }
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


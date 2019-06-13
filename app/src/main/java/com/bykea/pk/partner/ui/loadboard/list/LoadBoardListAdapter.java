package com.bykea.pk.partner.ui.loadboard.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.dal.Booking;
import com.bykea.pk.partner.widgets.AutoFitFontTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Home screen's bottom sheet loadboard jobs listing adapter
 */
public class LoadBoardListAdapter extends RecyclerView.Adapter<LoadBoardListAdapter.ViewHolder> {

    private ArrayList<Booking> mItems;
    private ItemClickListener itemClickListener;
    private Context mContext;

    public LoadBoardListAdapter(Context context, ArrayList<Booking> items, ItemClickListener itemClickListener) {
        this.mContext = context;
        this.mItems = items;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.loadboard_list_item,
                parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        final Booking item = mItems.get(position);
        holder.li_LoadboardDropOffTV.setText(mContext.getResources().getString(R.string.not_selected_ur));
        holder.tvFare.setText(mContext.getResources().getString(R.string.dash));
        /*if(item != null){
            switch (item.getTripType()){
                case Constants.TripTypes.RIDE_TYPE:
                    holder.ivServiceIcon.setImageResource(R.drawable.ride);
                    break;
                case Constants.TripTypes.PURCHASE_TYPE:
                    holder.ivServiceIcon.setImageResource(R.drawable.lay_ao);
                    break;
                case Constants.TripTypes.DELIVERY_TYPE:
                default:
                    holder.ivServiceIcon.setImageResource(R.drawable.bhejdo);
                    break;
            }
            if (item.getDropoffZone() != null) {
                holder.li_LoadboardDropOffTV.setText(mContext.getString(R.string.pick_drop_name_ur,item.getDropoffZone().getUrduName()));
            } else {
                holder.li_LoadboardDropOffTV.setText(mContext.getString(R.string.not_selected_ur));
            }
            if (item.getFare() != null) {
                holder.tvFare.setText(mContext.getString(R.string.seleted_amount_rs, item.getFare()));
            } else {
                holder.tvFare.setText(R.string.dash);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onClick(item);
                }
            });
        }*/
    }

    @Override
    public int getItemCount() {
        return 10;//mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivServiceIcon)
        AppCompatImageView ivServiceIcon;
        @BindView(R.id.li_LoadboardDropOffTV)
        AutoFitFontTextView li_LoadboardDropOffTV;
        @BindView(R.id.tv_fare)
        AutoFitFontTextView tvFare;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(view -> itemClickListener.onClick(getAdapterPosition()));//mItems.get(getAdapterPosition())));
        }
    }

    public interface ItemClickListener {
        void onClick(int item);
//        void onClick(Booking item);
    }
}


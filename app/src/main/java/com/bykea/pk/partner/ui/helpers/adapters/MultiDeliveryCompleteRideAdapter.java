package com.bykea.pk.partner.ui.helpers.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.DirectionDropOffData;

import java.util.List;

public class MultiDeliveryCompleteRideAdapter extends
        RecyclerView.Adapter<MultiDeliveryCompleteRideAdapter.ViewHolder> {

    List<DirectionDropOffData> list;

    /***
     * Constructor.
     *
     * Construct MultiDeliveryCompleteRideAdapter
     *
     * @param list a collection of DirectionDropOffData.
     */
    public MultiDeliveryCompleteRideAdapter(List<DirectionDropOffData> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(
                R.layout.multidelivery_ride_complete_row,
                parent,
                false
        );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DirectionDropOffData data = list.get(position);
        if (data == null) return;

        holder.areaTv.setText(data.getmArea());
        holder.driverNameTv.setText(data.getDriverName());
        holder.numberTv.setText(data.getDropOffNumberText());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /***
     * View Holder class Pattern for caches fields
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView areaTv;
        TextView driverNameTv;
        TextView numberTv;
        AppCompatImageView completeBtn;

        /***
         * Constructor.
         *
         * @param itemView an item row view.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            areaTv = itemView.findViewById(R.id.areaTv);
            driverNameTv = itemView.findViewById(R.id.driverNameTv);
            numberTv = itemView.findViewById(R.id.numberTv);
            completeBtn = itemView.findViewById(R.id.completeBtn);
        }
    }
}

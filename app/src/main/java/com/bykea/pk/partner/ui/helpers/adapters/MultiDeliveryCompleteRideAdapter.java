package com.bykea.pk.partner.ui.helpers.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.DirectionDropOffData;

import java.util.List;

public class MultiDeliveryCompleteRideAdapter extends
        RecyclerView.Adapter<MultiDeliveryCompleteRideAdapter.ViewHolder> {

    private List<DirectionDropOffData> list;
    private MultiDeliveryCompleteRideListener listener;

    private Context context;

    /**
     * Interface definition for a callback to be invoked when multi delivery ride has been completed
     */
    public interface MultiDeliveryCompleteRideListener {
        /**
         * Called when complete ride item view has been clicked.
         *
         * @param position The postion of the view that has been clicked.
         */
        void onMultiDeliveryCompleteRide(int position, String whichDelivery);
    }

    /***
     * Constructor.
     *
     * Construct MultiDeliveryCompleteRideAdapter
     *
     * @param list a collection of DirectionDropOffData.
     * @param listener Callback to be invoked when complete multi delivery ride has been clicked.
     */
    public MultiDeliveryCompleteRideAdapter(List<DirectionDropOffData> list,
                                            MultiDeliveryCompleteRideListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
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
        holder.driverNameTv.setText(data.getPassengerName());
        holder.numberTv.setText(data.getDropOffNumberText());

        if (data.isCompleted()) {
            holder.completeBtn.setVisibility(View.VISIBLE);
            holder.dropOffMarker.setColorFilter(ContextCompat.getColor(
                            DriverApp.getContext(),
                            R.color.multi_delivery_dropoff_completed
                    ),
                    android.graphics.PorterDuff.Mode.SRC_IN
            );
        } else {
            holder.completeBtn.setVisibility(View.INVISIBLE);
            holder.dropOffMarker.setColorFilter(ContextCompat.getColor(
                    DriverApp.getContext(),
                    R.color.red_dropoff
                    ),
                    android.graphics.PorterDuff.Mode.SRC_IN
            );
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /***
     * View Holder class Pattern for caches fields
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView areaTv;
        TextView driverNameTv;
        TextView numberTv;
        AppCompatImageView completeBtn;
        AppCompatImageView dropOffMarker;

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
            dropOffMarker = itemView.findViewById(R.id.dropOffMarker);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            //added individual ride number to show on complete confirmation dialog.
            listener.onMultiDeliveryCompleteRide(getAdapterPosition(),list.get(getAdapterPosition()).getDropOffNumberText());
        }
    }
}

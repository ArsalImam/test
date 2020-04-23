package com.bykea.pk.partner.ui.helpers.adapters;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.MultiDeliveryCallData;
import com.bykea.pk.partner.models.data.MultiDeliveryDropOff;
import com.bykea.pk.partner.utils.Constants;

/***
 * Multi Delivery Call Adapter an adaptet class is used to collect child view to represent a parent view.
 */
public class MultiDeliveryCallAdapter extends RecyclerView.Adapter<MultiDeliveryCallAdapter.ViewHolder> {

    private MultiDeliveryCallData data;
    private CallClickListener callClickListener;
    private int TYPE_HEADER = 0;
    private int TYPE_ITEM = 1;

    /**
     * Interface Definition for callback to be invoked when call button view has been clicked.
     */
    public interface CallClickListener {
        /**
         * Called when call button view has been clicked.
         *
         * @param position The position of the view that has been clicked.
         */
        void onCallClick(int position);
    }

    /***
     * Constructor.
     *
     * @param data MultiDeliveryCallData object.
     */
    public MultiDeliveryCallAdapter(MultiDeliveryCallData data,
                                    CallClickListener callClickListener) {
        this.data = data;
        this.callClickListener = callClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        //Inflate the header view if the viewType is TYPE_HEADER
        // otherwise inflate itemview

        int layout = viewType == TYPE_HEADER ? R.layout.call_pickup_header :
                R.layout.call_dropoff_item_row;

        View view = inflater.inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        if (data == null) return;
        try {
            if (getItemViewType(position) == TYPE_ITEM) {

                //Position - 1 is used because we use position = 0 for inflating header view
                // i.e pickup view when counter come in this condition position increased by one so
                // avoid this scenerio we have decrease position by one to get all the item
                // otherwise it will skip one item.

                MultiDeliveryDropOff dropOff = data.getDropOffList().get(position - 1);
                holder.numberTv.setText(dropOff.getDropOffNumberText());
                holder.areaTv.setText(dropOff.getmArea());
                holder.streetAddressTv.setText(dropOff.getStreetAddress());

                //Enable/Disable drop off location's call option
                if(data.getBatchStatus().equals(Constants.BATCH_STARTED) || data.getBatchStatus().equals(Constants.BATCH_ARRIVED)){
                    //disable making call of individual's completed delivery
                    if(dropOff.getRideStatus().equals(Constants.RIDE_FEEDBACK)){
                        holder.callIv.setOnClickListener(null);
                        holder.callIv.setImageResource(R.drawable.ic_call);
                    } else {
                        holder.callIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                callClickListener.onCallClick(position);
                            }
                        });
                        holder.callIv.setImageResource(R.drawable.contact_call_icon);
                    }
                } else {
                    holder.callIv.setOnClickListener(null);
                    holder.callIv.setVisibility(View.GONE);
                    holder.callIv.setImageResource(R.drawable.ic_call);
                }
            } else {

                //Set header data i.e pickup data

                holder.areaTv.setText(data.getPickupData().getArea());
                holder.streetAddressTv.setText(data.getPickupData().getStreetAddress());
                holder.feederTv.setText(data.getPickupData().getFeederName());
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return data.getDropOffList().size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return TYPE_HEADER;
        return TYPE_ITEM;
    }

    /***
     * View Holder class Pattern for caches fields
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView areaTv;
        TextView streetAddressTv;
        TextView numberTv;
        AppCompatImageView callIv;

        //pickup header layout fields
        TextView feederTv;

        /***
         * Constructor.
         *
         * @param itemView an item row view.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            areaTv = itemView.findViewById(R.id.areaTv);
            feederTv = itemView.findViewById(R.id.feaderTv);
            streetAddressTv = itemView.findViewById(R.id.streetAddress);
            numberTv = itemView.findViewById(R.id.numberTv);
            callIv = itemView.findViewById(R.id.callBtn);
            callIv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            callClickListener.onCallClick(getAdapterPosition());
        }
    }
}

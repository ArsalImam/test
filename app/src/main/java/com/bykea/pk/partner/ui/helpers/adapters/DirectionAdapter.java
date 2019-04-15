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
import com.bykea.pk.partner.models.data.MultiDeliveryDirectionDetails;

public class DirectionAdapter extends RecyclerView.Adapter<DirectionAdapter.ViewHolder> {

    private MultiDeliveryDirectionDetails data;
    private DirectionClickListener directionClickListener;

    private int TYPE_HEADER = 0;
    private int TYPE_ITEM = 1;

    /**
     * Interface definition for a callback to be invoked when direction button has been clicked.
     */
    public interface DirectionClickListener {
        /**
         * Called when direction button has been clicked.
         *
         * @param position The position of the view that has been clicked.
         */
        void onDirectionClick(int position);
    }

    /***
     * Constructor.
     * Generate or construct DirectionAdapter.
     *
     * @param data MultiDeliveryDirectionDetails response to map on the UI.
     * @param listener The direction click listener {@link DirectionClickListener}
     */
    public DirectionAdapter(MultiDeliveryDirectionDetails data, DirectionClickListener listener) {
        this.data = data;
        directionClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        //Inflate the header view if the viewType is TYPE_HEADER
        // otherwise inflate itemview

        int layout = viewType == TYPE_HEADER ? R.layout.direction_details_fragment_header :
                R.layout.direction_details_item_row;

        View view = inflater.inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (data == null) return;
        try {
            if (getItemViewType(position) == TYPE_ITEM) {

                //Position - 1 is used because we use position = 0 for inflating header view
                // i.e pickup view when counter come in this condition position increased by one so
                // avoid this scenerio we have decrease position by one to get all the item
                // otherwise it will skip one item.

                DirectionDropOffData dropOff = data.getDropOffList().get(position - 1);
                holder.numberTv.setText(dropOff.getDropOffNumberText());
                holder.areaTv.setText(dropOff.getmArea());
                holder.streetAddressTv.setText(dropOff.getStreetAddress());
                holder.tripNumberTv.setText(dropOff.getTripNumber());
                holder.driverNameTv.setText(dropOff.getPassengerName());
                if(dropOff.getCodValue() != -1){
                    holder.codValueTv.setVisibility(View.VISIBLE);
                    holder.codValueTv.setText(holder.codValueTv.getContext().
                            getString(R.string.code_value, dropOff.getCodValue()));
                } else {
                    holder.codValueTv.setVisibility(View.GONE);
                }
            } else {

                //Set header data i.e pickup data

                holder.areaTv.setText(data.getPickupData().getArea());
                holder.streetAddressTv.setText(data.getPickupData().getStreetAddress());
                holder.feederTv.setText(data.getPickupData().getFeederName());
                holder.directionBtn.setVisibility(View.INVISIBLE);
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        //In position zero we are inflating header so
        // avoid skipping first item return list size + 1
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
        TextView tripNumberTv;
        TextView driverNameTv;
        TextView codValueTv;
        AppCompatImageView directionBtn;

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
            directionBtn = itemView.findViewById(R.id.directionBtn);
            tripNumberTv = itemView.findViewById(R.id.tripNoTv);
            driverNameTv = itemView.findViewById(R.id.driverNameTv);
            codValueTv = itemView.findViewById(R.id.codTv);
            directionBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            directionClickListener.onDirectionClick(getAdapterPosition());
        }
    }
}

package com.bykea.pk.partner.ui.helpers.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.loadboard.LoadboardBookingOrderData;
import com.bykea.pk.partner.widgets.FontTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Selected booking's ordered items adapter that displays in loadboard booking detail screen
 */
public class LoadBoardOrdersAdapter extends RecyclerView.Adapter<LoadBoardOrdersAdapter.ViewHolder> {

    private ArrayList<LoadboardBookingOrderData> mItems;

    /**
     * Constructor call with booking's items list
     * @param items orders list items
     */
    public LoadBoardOrdersAdapter( ArrayList<LoadboardBookingOrderData> items) {
        this.mItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_loadboard_orders,
                parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final LoadboardBookingOrderData item = mItems.get(position);

        if(item != null){
            holder.orderNameTV.setText(item.getName());
            holder.orderQtyTV.setText("X "+item.getQty());
            holder.orderPriceTV.setText("Rs. "+(item.getQty()*item.getPrice()));
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * Viewholder to display individual items in list in loadboard details screen
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.orderNameTV)
        FontTextView orderNameTV;
        @BindView(R.id.orderPriceTV)
        FontTextView orderPriceTV;
        @BindView(R.id.orderQtyTV)
        FontTextView orderQtyTV;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}


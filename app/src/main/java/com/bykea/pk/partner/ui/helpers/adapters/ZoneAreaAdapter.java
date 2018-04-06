package com.bykea.pk.partner.ui.helpers.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.ZoneAreaData;
import com.bykea.pk.partner.widgets.FontTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ZoneAreaAdapter extends RecyclerView.Adapter<ZoneAreaAdapter.ViewHolder> {

    private ArrayList<ZoneAreaData> mItems;
    private ItemClickListener itemClickListener;
    private Context mContext;

    public ZoneAreaAdapter(Context context, ArrayList<ZoneAreaData> items, ItemClickListener itemClickListener) {
        this.mContext = context;
        this.mItems = items;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_area,
                parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ZoneAreaData item = mItems.get(position);
        holder.tvEnglishItem.setText(item.getName());
        holder.tvUrduItem.setText(item.getUrdu());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvEnglishItem)
        FontTextView tvEnglishItem;

        @BindView(R.id.tvUrduItem)
        FontTextView tvUrduItem;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onClick(mItems.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface ItemClickListener {
        void onClick(ZoneAreaData item);
    }
}


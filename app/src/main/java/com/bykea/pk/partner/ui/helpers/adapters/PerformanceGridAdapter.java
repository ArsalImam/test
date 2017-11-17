package com.bykea.pk.partner.ui.helpers.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.Performance;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PerformanceGridAdapter extends RecyclerView.Adapter<PerformanceGridAdapter.ItemHolder> {
    private ArrayList<Performance> mPerformanceList;
    private Context mContext;
    private OnItemClickListener onItemClickListener;

    public PerformanceGridAdapter(Context context, ArrayList<Performance> list, OnItemClickListener onItemClickListener) {
        mPerformanceList = new ArrayList<>();
        mPerformanceList.addAll(list);
        mContext = context;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.performance_item,
                parent, false);
        return new ItemHolder(view);
    }


    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        Performance data = mPerformanceList.get(position);
        holder.tvKey.setText(data.getKey());
        holder.tvValue.setText(data.getVal());
    }

    @Override
    public int getItemCount() {
        Utils.redLog("PerformanceAdapter",mPerformanceList.size()+"");
        return mPerformanceList.size();
    }



    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.tvKey)
        FontTextView tvKey;
        @Bind(R.id.tvValue)
        FontTextView tvValue;

        ItemHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClickListener(mPerformanceList.get(getAdapterPosition()));
        }
    }

    public interface OnItemClickListener {
        void onItemClickListener(Performance data);
    }
}

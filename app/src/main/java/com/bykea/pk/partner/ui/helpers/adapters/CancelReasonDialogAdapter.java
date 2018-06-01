package com.bykea.pk.partner.ui.helpers.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.bykea.pk.partner.R;
import com.bykea.pk.partner.widgets.FontTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CancelReasonDialogAdapter extends RecyclerView.Adapter<CancelReasonDialogAdapter.ViewHolder> {
    private ArrayList<String> mList;
    private Context mCurrentActivity;
    private int selectedIndex;


    public CancelReasonDialogAdapter(Context context, ArrayList<String> list) {
        this.mList = list;
        this.mCurrentActivity = context;
        selectedIndex = 999;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cancel_reason_single_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        String data = mList.get(i);
        holder.tv_item.setText(" " + data + " ");
        if (selectedIndex == i) {
            holder.singleViewLine.setVisibility(View.VISIBLE);
            holder.tv_item.setTextColor(ContextCompat.getColor(mCurrentActivity, R.color.white));
            holder.tv_item.setBackgroundColor(ContextCompat.getColor(mCurrentActivity, R.color.colorAccent));
        } else if (i == mList.size() - 1) {
            holder.singleViewLine.setVisibility(View.INVISIBLE);
            holder.tv_item.setTextColor(ContextCompat.getColor(mCurrentActivity, R.color.black));
            holder.tv_item.setBackgroundColor(ContextCompat.getColor(mCurrentActivity, R.color.last_item_color));
        } else {
            holder.singleViewLine.setVisibility(View.VISIBLE);
            holder.tv_item.setTextColor(ContextCompat.getColor(mCurrentActivity, R.color.black));
            holder.tv_item.setBackgroundColor(ContextCompat.getColor(mCurrentActivity, R.color.white));
        }
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_item)
        FontTextView tv_item;

        @BindView(R.id.singleViewLine)
        View singleViewLine;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            setSelectedIndex(getLayoutPosition());
            notifyDataSetChanged();
        }
    }
}
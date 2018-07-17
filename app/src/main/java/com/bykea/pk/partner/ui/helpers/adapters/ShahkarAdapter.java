package com.bykea.pk.partner.ui.helpers.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.ShahkarData;

import java.text.DecimalFormat;
import java.util.List;

public class ShahkarAdapter extends RecyclerView.Adapter<ShahkarAdapter.ViewHolder> {

    List<ShahkarData> list;

    public ShahkarAdapter(List<ShahkarData> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.shahkar_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (list == null || list.size()==0) return;

        holder.numberTv.setText(String.valueOf(list.get(position).getNumber()));
        holder.nameTv.setText(list.get(position).getName());
        holder.bookingTv.setText(String.valueOf(list.get(position).getBooking()));

        DecimalFormat formatter = new DecimalFormat("#,###,###");

        holder.kamaiTv.setText(formatter.format(list.get(position).getEarning()));
        holder.rattingTv.setText(String.valueOf(list.get(position).getScore()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView numberTv;
        TextView nameTv;
        TextView bookingTv;
        TextView kamaiTv;
        TextView rattingTv;

        public ViewHolder(View itemView) {
            super(itemView);
            numberTv = itemView.findViewById(R.id.numberTv);
            nameTv = itemView.findViewById(R.id.nameTv);
            bookingTv = itemView.findViewById(R.id.bookingTv);
            kamaiTv = itemView.findViewById(R.id.kamaiTv);
            rattingTv = itemView.findViewById(R.id.scoreTv);
        }
    }
}

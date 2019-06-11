package com.bykea.pk.partner.ui.helpers.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.HaftaBookingBonusModel;

import java.util.List;

public class RankingWeeklyStatsAdapter extends RecyclerView.Adapter<RankingWeeklyStatsAdapter.ViewHolder> {

    List<HaftaBookingBonusModel> list;

    public RankingWeeklyStatsAdapter(List<HaftaBookingBonusModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.weeklystats_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (list == null || list.size()==0) return;

        holder.bonusTv.setText(list.get(position).getBonus());
        holder.bookingTv.setText(list.get(position).getBooking());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView bookingTv;
        TextView bonusTv;

        public ViewHolder(View itemView) {
            super(itemView);
            bookingTv = itemView.findViewById(R.id.bookingTv);
            bonusTv = itemView.findViewById(R.id.bonusTv);
        }
    }
}

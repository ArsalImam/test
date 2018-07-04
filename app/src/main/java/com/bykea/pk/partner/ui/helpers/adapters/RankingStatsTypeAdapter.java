package com.bykea.pk.partner.ui.helpers.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.RankingStatsTypeModel;

import java.util.List;

public class RankingStatsTypeAdapter extends RecyclerView.Adapter<RankingStatsTypeAdapter.ViewHolder> {

    List<RankingStatsTypeModel> list;

    public RankingStatsTypeAdapter(List<RankingStatsTypeModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.driver_ranking_stats_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (list == null || list.size()==0) return;

        holder.statsTv_driver1.setText(list.get(position).getStatsDriver1());
        holder.statsTv_driver2.setText(list.get(position).getStatsDriver2());
        holder.statsTv_driver3.setText(list.get(position).getStatsDriver3());
        holder.statsTypeTv.setText(list.get(position).getStatsType());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView statsTv_driver1;
        TextView statsTv_driver2;
        TextView statsTv_driver3;
        TextView statsTypeTv;

        public ViewHolder(View itemView) {
            super(itemView);
            statsTv_driver1 = itemView.findViewById(R.id.statsTv_driver1);
            statsTv_driver2 = itemView.findViewById(R.id.statsTv_driver2);
            statsTv_driver3 = itemView.findViewById(R.id.statsTv_driver3);
            statsTypeTv = itemView.findViewById(R.id.statsTypeTv);
        }
    }
}

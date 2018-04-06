package com.bykea.pk.partner.ui.helpers.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.ModelVideoDemo;
import com.bykea.pk.partner.widgets.FontTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HowItWorksVideoAdapter extends RecyclerView.Adapter<HowItWorksVideoAdapter.ItemHolder> {

    private ArrayList<ModelVideoDemo> data = new ArrayList<>();
    int i = 0;
    private OnItemClickListener onItemClickListener;

    public HowItWorksVideoAdapter(Context a, ArrayList<ModelVideoDemo> d, OnItemClickListener onItemClickListener) {
        data = d;
        this.onItemClickListener = onItemClickListener;
    }


    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvVideoNo, tvVideoName;
        ImageView imgPlay, imgVideoName;

        ItemHolder(final View itemView) {
            super(itemView);
            tvVideoName = (TextView) itemView.findViewById(R.id.tvVideoName);
            tvVideoNo = (TextView) itemView.findViewById(R.id.tvVideoNo);
            imgPlay = (ImageView) itemView.findViewById(R.id.imgPlay);
            imgVideoName = (ImageView) itemView.findViewById(R.id.imgVideoName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClickListener(data.get(getAdapterPosition()));
        }
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.how_it_works_videos_single_item,
                parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {

        if (data.size() > 0) {
            holder.tvVideoNo.setText(data.get(position).getVideoNumber());
            holder.tvVideoName.setText(data.get(position).getVideoName());
            holder.imgVideoName.setImageResource(data.get(position).getImageName());
        } else {
            holder.tvVideoNo.setText("No Data");
        }
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (data.size() <= 0)
            return 1;
        return data.size();
    }


    public interface OnItemClickListener {
        void onItemClickListener(ModelVideoDemo data);
    }


}

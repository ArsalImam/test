package com.bykea.pk.partner.ui.helpers.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.DileveryScheduleModel;
import com.instabug.library.core.ui.BaseContract;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeliveryScheduleAdapter extends RecyclerView.Adapter<DeliveryScheduleAdapter.ViewHoder> {

    private List<DileveryScheduleModel> list;

    private onClickListener onClickListener;

    public DeliveryScheduleAdapter(List<DileveryScheduleModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.dilevery_schedule_item, parent, false);
        return new ViewHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoder holder, int position) {
        if (list == null || list.size() == 0) return;

        holder.addressTv.setText(list.get(position).getAddress());
        holder.distanceTv.setText(list.get(position).getDistance());
        holder.durationTv.setText(list.get(position).getDuration());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHoder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.addressTv)
        TextView addressTv;

        @BindView(R.id.durationTv)
        TextView durationTv;

        @BindView(R.id.distanceTv)
        TextView distanceTv;

        @BindView(R.id.directionBtn)
        ImageView directionBtn;

        public ViewHoder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            directionBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.directionBtn:
                    onClickListener.directionClick(getAdapterPosition());
                    break;
                case R.id.callBtn:
                    onClickListener.callClick(getAdapterPosition());
                    break;
                case R.id.assignBtn:
                    onClickListener.confirmClick(getAdapterPosition());
                    break;
            }
        }
    }

    public void setOnClickListener(DeliveryScheduleAdapter.onClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface onClickListener {
        void directionClick(int pos);

        void callClick(int pos);

        void confirmClick(int pos);
    }
}

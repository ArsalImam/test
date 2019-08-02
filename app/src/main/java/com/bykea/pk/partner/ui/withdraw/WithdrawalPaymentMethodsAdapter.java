package com.bykea.pk.partner.ui.withdraw;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.dal.source.remote.data.WithdrawPaymentMethod;

import java.util.ArrayList;
import java.util.List;

public class WithdrawalPaymentMethodsAdapter extends RecyclerView.Adapter<WithdrawalPaymentMethodsAdapter.ViewHolder> {
    private final ArrayList<WithdrawPaymentMethod> withdrawPaymentMethods;
    private final WithdrawalViewModel viewModel;
    private int lastSelectedPosition;

    public WithdrawalPaymentMethodsAdapter(ArrayList<WithdrawPaymentMethod> withdrawPaymentMethods,
                                           WithdrawalViewModel viewModel) {
        this.withdrawPaymentMethods = withdrawPaymentMethods;
        this.viewModel = viewModel;
        this.lastSelectedPosition = 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        return new ViewHolder(layoutInflater.inflate(R.layout.adapter_withdraw_methdods, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mCnicTextView.setText(viewModel.getDriverCnicNumber());
        holder.mFeesTextView.setText(withdrawPaymentMethods.get(position).getDescription());
        holder.mCheckImageView.setVisibility(withdrawPaymentMethods.get(position).isSelected() ?
                View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return withdrawPaymentMethods.size();
    }

    public void notifyMethodsChanged(List<WithdrawPaymentMethod> it) {
        withdrawPaymentMethods.clear();
        withdrawPaymentMethods.addAll(it);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView mCnicTextView;
        private final TextView mFeesTextView;
        private final ImageView mCheckImageView;

        public ViewHolder(View v) {
            super(v);
            mCnicTextView = v.findViewById(R.id.nic_val_textview);
            mFeesTextView = v.findViewById(R.id.fees_val_textview);
            mCheckImageView = v.findViewById(R.id.checkView);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            WithdrawPaymentMethod object = withdrawPaymentMethods.get(position);
            if (!object.isSelected()) {
                object.setSelected(true);
                viewModel.setSelectedPaymentMethod(object);
                withdrawPaymentMethods.get(lastSelectedPosition).setSelected(false);
                lastSelectedPosition = position;
                notifyDataSetChanged();
            }
        }
    }
}
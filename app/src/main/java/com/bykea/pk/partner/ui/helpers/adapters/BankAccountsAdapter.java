package com.bykea.pk.partner.ui.helpers.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.AccountsData;
import com.bykea.pk.partner.widgets.FontTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BankAccountsAdapter extends RecyclerView.Adapter<BankAccountsAdapter.ItemHolder> {

    private static ArrayList<AccountsData> mAccountList;

    public BankAccountsAdapter(Context context, ArrayList<AccountsData> list) {
        mAccountList = list;
        Context mContext = context;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_bank_accounts,
                parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        AccountsData data = mAccountList.get(position);
        holder.bankName.setText(data.getBankName());
        holder.accountTitle.setText(data.getAccountTitle());
        holder.accountNumber.setText(data.getAccountNumber());

    }


    @Override
    public int getItemCount() {
        return mAccountList.size();
    }

/*
    public void addAll(ArrayList<WalletData> list) {
        if(mAccountList.size() > 0){
            mAccountList.clear();
        }
        mAccountList.addAll(list);
    }*/

    public static class ItemHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.bankName)
        FontTextView bankName;
        @Bind(R.id.accountTitle)
        FontTextView accountTitle;
        @Bind(R.id.accountNumber)
        FontTextView accountNumber;


        public ItemHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }


}

package com.bykea.pk.partner.ui.helpers.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.BankData;
import com.bykea.pk.partner.ui.activities.BaseActivity;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import org.jcodec.common.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BankAccountsAdapter extends
        RecyclerView.Adapter<BankAccountsAdapter.ItemHolder> {

    private static ArrayList<BankData> mAccountList;
    private BaseActivity mContext;
    private static MyOnItemClickListener myOnItemClickListener;

    public BankAccountsAdapter(BaseActivity context, ArrayList<BankData> list) {
        mAccountList = list;
        mContext = context;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_bank,
                        parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        BankData data = mAccountList.get(position);
        holder.tvMsg.setText(data.getMsg());
        Utils.loadImgPicasso(mContext, holder.ivBank, Utils.getCloudinaryLink(data.getImg()));
    }


    @Override
    public int getItemCount() {
        return mAccountList.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivBank)
        ImageView ivBank;

        @BindView(R.id.tvMsg)
        FontTextView tvMsg;


        ItemHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (myOnItemClickListener != null) {
                        myOnItemClickListener.onItemClickListener(getLayoutPosition(), v
                                , mAccountList.get(getLayoutPosition()));
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(MyOnItemClickListener itemClickListener) {
        myOnItemClickListener = itemClickListener;
    }

    public interface MyOnItemClickListener {
        void onItemClickListener(int position, View view, BankData data);
    }

}
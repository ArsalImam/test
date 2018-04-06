package com.bykea.pk.partner.ui.helpers.adapters;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.BankData;
import com.bykea.pk.partner.models.data.BankDetailsData;
import com.bykea.pk.partner.models.data.BankData.BankAgentData;
import com.bykea.pk.partner.ui.activities.BaseActivity;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BankDetailsAdapter extends
        RecyclerView.Adapter<BankDetailsAdapter.ItemHolder> {

    private static ArrayList<BankData.BankAgentData> mAccountList;
    private BaseActivity mContext;

    public BankDetailsAdapter(BaseActivity context,
                              ArrayList<BankData.BankAgentData> list) {
        mAccountList = list;
        mContext = context;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_bank_accounts,
                        parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        BankData.BankAgentData data = mAccountList.get(position);
        String distance = Utils.calculateDistanceInKm(data.getLoc().get(0),
                data.getLoc().get(1), AppPreferences.getLatitude(), AppPreferences.getLongitude()) + " KM";
        if (StringUtils.isNotBlank(data.getAgentName())) {
            holder.rlAgentName.setVisibility(View.VISIBLE);
            holder.rlDirection1.setVisibility(View.GONE);
            holder.tvAgentName.setText(data.getAgentName());
            holder.tvDistance.setText(distance);
        } else {
            holder.rlAgentName.setVisibility(View.GONE);
            holder.rlDirection1.setVisibility(View.VISIBLE);
            holder.tvDistance1.setText(distance);
        }

        if (StringUtils.isNotBlank(data.getContactPerson())) {
            holder.llContactPerson.setVisibility(View.VISIBLE);
            holder.tvContactPerson.setText(data.getContactPerson());
        } else {
            holder.llContactPerson.setVisibility(View.GONE);
        }

        holder.tvAddress.setText(data.getAddress());
        holder.tvPhone.setText(data.getPhone());
    }


    @Override
    public int getItemCount() {
        return mAccountList.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvAgentName)
        FontTextView tvAgentName;
        @BindView(R.id.tvAddress)
        FontTextView tvAddress;
        @BindView(R.id.tvContactPerson)
        FontTextView tvContactPerson;
        @BindView(R.id.tvPhone)
        FontTextView tvPhone;

        @BindView(R.id.tvDistance)
        FontTextView tvDistance;

        @BindView(R.id.tvDistance1)
        FontTextView tvDistance1;

        @BindView(R.id.rlDirection1)
        RelativeLayout rlDirection1;

        @BindView(R.id.rlAgentName)
        RelativeLayout rlAgentName;

        @BindView(R.id.llContactPerson)
        LinearLayout llContactPerson;


        ItemHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.llPhone, R.id.rlDirection, R.id.rlDirection1})
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.rlDirection:
                case R.id.rlDirection1:
                    try {
                        String end = mAccountList.get(getLayoutPosition()).getLoc().get(0) + "," + mAccountList.get(getLayoutPosition()).getLoc().get(1);
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + end + "&mode=d");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        mContext.startActivity(mapIntent);
                    } catch (Exception ex) {
                        Utils.appToast(mContext, "Please install Google Maps");
                    }
                    break;
                case R.id.llPhone:
                    Utils.callingIntent(mContext, mAccountList.get(getLayoutPosition()).getPhone());
                    break;
            }
        }
    }
}
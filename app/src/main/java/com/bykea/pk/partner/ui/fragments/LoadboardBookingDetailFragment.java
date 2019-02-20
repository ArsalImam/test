package com.bykea.pk.partner.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.loadboard.LoadboardBookingDetailData;
import com.bykea.pk.partner.ui.activities.LoadboardBookingDetailActivity;
import com.bykea.pk.partner.ui.helpers.adapters.LoadBoardOrdersAdapter;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoadboardBookingDetailFragment extends Fragment {

    private LoadboardBookingDetailActivity mCurrentActivity;
    private LoadboardBookingDetailData data;

    @BindView(R.id.bd_FareTV)
    FontTextView bd_FareTV;

    @BindView(R.id.bd_directionIV)
    AppCompatImageView bd_directionIV;

    @BindView(R.id.bd_pickUpZoneTV)
    FontTextView bd_pickUpZoneTV;
    @BindView(R.id.bd_pickUpNameTV)
    FontTextView bd_pickUpNameTV;
    @BindView(R.id.bd_pickUpAddressTV)
    FontTextView bd_pickUpAddressTV;
    @BindView(R.id.bd_pickUpTimeTV)
    FontTextView bd_pickUpTimeTV;
    @BindView(R.id.bd_estimatedTimeTV)
    FontTextView bd_estimatedTimeTV;
    @BindView(R.id.bd_pickUpPhoneIV)
    AppCompatImageView bd_pickUpPhoneIV;


    @BindView(R.id.bd_dropOffZoneTV)
    FontTextView bd_dropOffZoneTV;
    @BindView(R.id.bd_dropOffAddressTV)
    FontTextView bd_dropOffAddressTV;
    @BindView(R.id.bd_estimatedDistanceTV)
    FontTextView bd_estimatedDistanceTV;
    @BindView(R.id.bd_dropOffPhoneIV)
    AppCompatImageView bd_dropOffPhoneIV;

    @BindView(R.id.bd_OrdersRV)
    RecyclerView bd_OrdersRV;



    public static LoadboardBookingDetailFragment newInstance(LoadboardBookingDetailData data){
        LoadboardBookingDetailFragment fragment = new LoadboardBookingDetailFragment();
        fragment.data = data;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_loadboard_booking_detail, container, false);
        ButterKnife.bind(this, v);

        mCurrentActivity = (LoadboardBookingDetailActivity) getActivity();

        /*new UserRepository().acceptLoadboardBooking(mCurrentActivity, ""*//*item.getId()*//*, new UserDataHandler(){
            @Override
            public void onAcceptLoadboardBookingResponse(AcceptLoadboardBookingResponse response) {
                Utils.appToast(mCurrentActivity,"RESsss");
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                Utils.appToast(mCurrentActivity,errorMessage);

            }
        });*/

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    private void initViews(){
        bd_FareTV.setText("Rs. "+data.getFareEstimation());

        bd_pickUpNameTV.setText(data.getPickupName());
        bd_pickUpAddressTV.setText(data.getPickupAddress());
        bd_pickUpTimeTV.setText(data.getDeliveryTimings());
        bd_pickUpZoneTV.setText(data.getPickupZone().getUrduName());
        int etaInMinute = data.getPickupEta() / 60;
        bd_estimatedTimeTV.setText(String.valueOf(etaInMinute));

        bd_dropOffAddressTV.setText(data.getDropoffAddress());
        bd_dropOffZoneTV.setText(data.getDropoffZone().getUrduName());
        float estimatedDistance = data.getDropoffDistance() / 1000;
        bd_estimatedDistanceTV.setText(String.format("%.1f", estimatedDistance));

        bd_pickUpPhoneIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.callingIntent(mCurrentActivity, data.getPickupPhone());
            }
        });
        bd_dropOffPhoneIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.callingIntent(mCurrentActivity, data.getReceiverPhone());
            }
        });
        bd_directionIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGoogleDirectionsApp();
            }
        });

        bd_OrdersRV.setLayoutManager(new LinearLayoutManager(mCurrentActivity));
        bd_OrdersRV.setHasFixedSize(true);
        bd_OrdersRV.setAdapter(new LoadBoardOrdersAdapter(data.getOrders()));
    }


    private void startGoogleDirectionsApp() {
        try {
            if (data != null) {
                String start = data.getPickupLoc().getLatitude()+","+data.getPickupLoc().getLongitude();
                String destination =data.getEndLoc().getLatitude()+","+data.getEndLoc().getLongitude();

                try {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + start + "&daddr=" + destination + "&mode=b");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                } catch (Exception ex) {
                    Utils.appToast(mCurrentActivity, "Please install Google Maps");
                }

            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}

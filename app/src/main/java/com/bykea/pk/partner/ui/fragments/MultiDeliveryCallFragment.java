package com.bykea.pk.partner.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.MultiDeliveryCallData;
import com.bykea.pk.partner.models.data.MultiDeliveryDropOff;
import com.bykea.pk.partner.models.data.MultiDeliveryPickup;
import com.bykea.pk.partner.models.data.MultiDeliveryCallDriverData;
import com.bykea.pk.partner.models.response.MultipleDeliveryBookingResponse;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.adapters.MultiDeliveryCallAdapter;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Multi Delivery Call Fragment
 */
public class MultiDeliveryCallFragment extends Fragment {

    @BindView(R.id.call_recycler_view)
    RecyclerView mRecyclerView;

    private Unbinder unbinder;
    private LinearLayoutManager mLayoutManager;
    private MultiDeliveryCallDriverData callDriverData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.multi_delivery_call_fragment,
                container,
                false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        callDriverData = AppPreferences.getMultiDeliveryCallDriverData();
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Todo 1: Mock Data for testing need to be change when backend team provide an API

        MultiDeliveryPickup pickup = new MultiDeliveryPickup("University Road",
                callDriverData.getPickup().getFeederName(),
                callDriverData.getPickup().getPickupAddress(),
                callDriverData.getPickup().getContactNumer());

        final List<MultiDeliveryDropOff> list = new ArrayList<>();
        addDropOffData(list);

        final MultiDeliveryCallData data = new MultiDeliveryCallData(pickup, list, callDriverData.getBatchStatus());

        MultiDeliveryCallAdapter adapter = new MultiDeliveryCallAdapter(
                data,
                new MultiDeliveryCallAdapter.CallClickListener() {
                    @Override
                    public void onCallClick(int position) {
                        try {
                            if (position != 0) { //0 position of the list have the pickup
                                if (callDriverData.getBatchStatus()
                                        .equalsIgnoreCase(TripStatus.ON_ACCEPT_CALL)) return;
                                position--;
                                Utils.callingIntent(getActivity(),
                                        Utils.phoneNumberToShow(
                                                list.get(position).getContactNumer()
                                        ));
                            } else {
                                Utils.callingIntent(getActivity(),
                                        Utils.phoneNumberToShow(
                                                data.getPickupData().getContactNumber()
                                        ));
                            }

                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                });
        mRecyclerView.setAdapter(adapter);


    }

    /**
     * Fetch the collection of drop off data.
     *
     * @param list The collection that will return
     *
     *  f(n) = 2 + 5n
     *  g(n) = n (tightest coupled)
     *  By ignoring the constant. Time Complexity OR Rate of a growth of a function = O(n)
     *
     * @return The collection of data.
     */
    private void addDropOffData(List<MultiDeliveryDropOff> list) {
        int length = callDriverData.getBookings().size();
        for (int i = 0; i < length; i++) {
            MultipleDeliveryBookingResponse response = callDriverData.getBookings().get(i);
            MultiDeliveryDropOff dropOff = new MultiDeliveryDropOff(
                    response.getPassenger().getName(),
                    response.getDropOff().getPickupAddress(),
                    String.valueOf(i+1), response.getPassenger().getPhone());
            //set individual's delivery status
            dropOff.setRideStatus(response.getTrip().getStatus());
            list.add(dropOff);
        }
    }

    public static MultiDeliveryCallFragment newInstance() {
        return new MultiDeliveryCallFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

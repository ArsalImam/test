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
import com.bykea.pk.partner.models.data.DirectionDropOffData;
import com.bykea.pk.partner.models.data.MultiDeliveryDirectionDetails;
import com.bykea.pk.partner.models.data.MultiDeliveryPickup;
import com.bykea.pk.partner.models.data.MultiDeliveryCallDriverData;
import com.bykea.pk.partner.models.response.MultipleDeliveryDropOff;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.adapters.DirectionAdapter;
import com.bykea.pk.partner.utils.Utils;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Multi Delivery Direction Fragment
 */
public class MultiDeliveryDirectionFragment extends Fragment {

    @BindView(R.id.direction_recycler_view)
    RecyclerView mRecyclerView;

    private Unbinder unbinder;
    private LinearLayoutManager mLayoutManager;
    private MultiDeliveryCallDriverData callData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.multideliver_direction_fragment,
                container,
                false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        callData = AppPreferences.getMultiDeliveryCallDriverData();
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        MultiDeliveryPickup pickup = new MultiDeliveryPickup("University Road",
                callData.getPickup().getFeederName(),
                callData.getPickup().getPickupAddress());

        List<DirectionDropOffData> list = new ArrayList<>();
        for (int i = 0; i < callData.getBookings().size(); i++) {
            DirectionDropOffData dropOff = new DirectionDropOffData(
                    "University Road",
                    callData.getBookings().get(i).getTrip().getTripNo(),
                    callData.getBookings().get(i).getPassenger().getName(),
                    500,
                    callData.getBookings().get(i).getDropOff().getPickupAddress(),
                    String.valueOf(i + 1));

            list.add(dropOff);
        }

        MultiDeliveryDirectionDetails data = new MultiDeliveryDirectionDetails(pickup, list);

        DirectionAdapter adapter = new DirectionAdapter(
                data,
                new DirectionAdapter.DirectionClickListener() {
                    @Override
                    public void onDirectionClick(int position) {
                        try {
                            position--;
                            MultipleDeliveryDropOff dropOff = callData
                                    .getBookings()
                                    .get(position)
                                    .getDropOff();
                            LatLng dropOffLatLng = new LatLng(dropOff.getLat(), dropOff.getLng());
                            Utils.navigateDropDownToGoogleMap(getActivity(), dropOffLatLng);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        mRecyclerView.setAdapter(adapter);


    }

    /**
     * Fetch the instance of this fragment.
     *
     * @return The instance of this fragment.
     */
    public static MultiDeliveryDirectionFragment newInstance() {
        return new MultiDeliveryDirectionFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

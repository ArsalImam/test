package com.bykea.pk.partner.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.DirectionDropOffData;
import com.bykea.pk.partner.models.data.MultiDeliveryCallDriverData;
import com.bykea.pk.partner.models.response.MultiDeliveryCompleteRideResponse;
import com.bykea.pk.partner.models.response.MultipleDeliveryBookingResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.adapters.MultiDeliveryCompleteRideAdapter;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Keys;

import org.greenrobot.eventbus.EventBus;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Multi Delivery Ride Complete Fragment
 */
public class MultiDeliveryRideCompleteFragment extends Fragment {

    @BindView(R.id.ride_recycler_view)
    RecyclerView mRecyclerView;

    private Unbinder unbinder;
    private LinearLayoutManager mLayoutManager;
    private MultiDeliveryCallDriverData callDriverData;
    private UserRepository repository;
    private List<DirectionDropOffData> list = new ArrayList<>();
    private List<String> tripIDList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.multidelivery_mukamal_fragment,
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
        mRecyclerView.setLayoutManager(mLayoutManager);
        repository = new UserRepository();

        callDriverData = AppPreferences.getMultiDeliveryCallDriverData();


        populateCompleteRideData(list);

        MultiDeliveryCompleteRideAdapter adapter = new MultiDeliveryCompleteRideAdapter(
                list,
                new MultiDeliveryCompleteRideAdapter.MultiDeliveryCompleteRideListener() {
                    @Override
                    public void onMultiDeliveryCompleteRide(int position) {
                        requestRideFinish(position);
                    }
                });
        mRecyclerView.setAdapter(adapter);
    }

    /**
     * Show confirm dialog for ride finish.
     *
     * @param position The position of the view that has been clicked.
     */
    private void requestRideFinish(int position) {
        Dialogs.INSTANCE.showLoader(getActivity());
        final DirectionDropOffData data = list.get(position);
        Dialogs.INSTANCE.showRideStatusDialog(getActivity(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialogs.INSTANCE.dismissDialog();
                requestMultiDeliveryTripFinished(data);

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialogs.INSTANCE.dismissDialog();
            }
        }, " مکمل؟");
    }

    /**
     * Invoked this method when multi delivery trip is going to finish
     *
     * @param data The data id trip that is going to be finished.
     */
    private void requestMultiDeliveryTripFinished(DirectionDropOffData data) {
        Dialogs.INSTANCE.showLoader(getActivity());
        repository.requestMultiDeliveryDriverFinishRide(
                data, handler);
    }

    /**
     * Populate the colection of complete ride data.
     * <p>
     * By ignoring the constants. Time Complexity or Rate of growth of a function is: O(n)
     *
     * @param list The collection of multi delivery complete ride data.
     */
    private void populateCompleteRideData(List<DirectionDropOffData> list) {
        List<MultipleDeliveryBookingResponse> bookingResponses = callDriverData.getBookings();
        if (AppPreferences.getMultiDeliveryTrip() != null)
            tripIDList = AppPreferences.getMultiDeliveryTrip();
        int n = bookingResponses.size();
        for (int i = 0; i < n; i++) {
            MultipleDeliveryBookingResponse multipleDeliveryBookingResponse =
                    bookingResponses.get(i);
            DirectionDropOffData dropOff = new DirectionDropOffData.Builder()
                    .setmArea("University Road")
                    .setPassengerName(multipleDeliveryBookingResponse
                            .getPassenger()
                            .getName())
                    .setTripID(multipleDeliveryBookingResponse.getTrip().getId())
                    .setDropOffNumberText(String.valueOf(i + 1))
                    .build();

            setCompletedData(dropOff);
            list.add(dropOff);
        }
    }

    /**
     * Set Complete data by comparing the completed trip id and mark as completed.
     * <p>
     * By ignoring the constants the time complexity or rate of growth of a function is
     * f(n) = O(n)^2 because it is already executed in a loop.
     *
     * @param dropOff The list item data.
     */
    private void setCompletedData(DirectionDropOffData dropOff) {
        for (int i = 0; i < tripIDList.size(); i++) {
            if (dropOff.getTripID()
                    .equalsIgnoreCase(tripIDList.get(i))) {
                dropOff.setCompleted(true);
            }
        }
    }

    /**
     * Fetch the instance of this fragment.
     *
     * @return The instance of multi delivery ride complete fragment.
     */
    public static MultiDeliveryRideCompleteFragment newInstance() {
        return new MultiDeliveryRideCompleteFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * Callback that will be invoked when multi delivery ride completed event emitted.
     */
    private UserDataHandler handler = new UserDataHandler() {

        @Override
        public void onMultiDeliveryDriverRideFinish(final MultiDeliveryCompleteRideResponse
                                                            response, final DirectionDropOffData data) {
            Log.d("MultiDeliveryComplete", response.getCode() + "");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Dialogs.INSTANCE.dismissDialog();
                    onMultiDeliveryRideFinished(data, response);
                }
            });

        }

        @Override
        public void onError(int errorCode, String errorMessage) {
            Dialogs.INSTANCE.dismissDialog();
            if (errorCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                EventBus.getDefault().post(Keys.UNAUTHORIZED_BROADCAST);
            } else {
                EventBus.getDefault().post(Keys.MULTIDELIVERY_ERROR_BORADCAST);
            }
        }
    };

    /**
     * On Multi Delivery Ride Finished State.
     * <p>
     * By Ignoring the constants the time complexity or rate of a growth of a function is O(n)
     *
     * @param data     The multi delivery list item data.
     * @param response The {@linkplain MultiDeliveryCompleteRideResponse} object.
     */
    private void onMultiDeliveryRideFinished(DirectionDropOffData data,
                                             MultiDeliveryCompleteRideResponse response) {

        for (DirectionDropOffData item : list) {
            if (data.getTripID().equalsIgnoreCase(item.getTripID())) {
                item.setCompleted(true);
                tripIDList.add(item.getTripID());
            }
        }
        AppPreferences.setMultiDeliveryTrips(tripIDList);
        int count = AppPreferences.getMultiDeliveryCompletedTripCounts();
        AppPreferences.saveMultiDeliveryCompletedTripCounts(++count);
        if (response.getCode() == HttpURLConnection.HTTP_OK) {
            ActivityStackManager.getInstance()
                    .startMultiDeliveryFeedbackActivity(getActivity(), response);
            getActivity().finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        list = null;
    }
}

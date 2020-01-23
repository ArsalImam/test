package com.bykea.pk.partner.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.DirectionDropOffData;
import com.bykea.pk.partner.models.data.MultiDeliveryCallDriverData;
import com.bykea.pk.partner.models.data.MultiDeliveryCompleteRideData;
import com.bykea.pk.partner.models.data.MultiDeliveryRideCompleteTripInfo;
import com.bykea.pk.partner.models.response.MultiDeliveryCompleteRideResponse;
import com.bykea.pk.partner.models.response.MultipleDeliveryBookingResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.repositories.places.IPlacesDataHandler;
import com.bykea.pk.partner.repositories.places.PlacesDataHandler;
import com.bykea.pk.partner.ui.activities.BookingActivity;
import com.bykea.pk.partner.ui.activities.MapDetailsActivity;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.adapters.MultiDeliveryCompleteRideAdapter;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.GeocodeStrategyManager;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;
import com.google.gson.Gson;

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

    private static final String TAG = MultiDeliveryRideCompleteFragment.class.getSimpleName();
    @BindView(R.id.ride_recycler_view)
    RecyclerView mRecyclerView;

    private Unbinder unbinder;
    private LinearLayoutManager mLayoutManager;
    private MultiDeliveryCallDriverData callDriverData;
    private UserRepository repository;
    private List<DirectionDropOffData> list = new ArrayList<>();
    private List<String> tripIDList = new ArrayList<>();
    private GeocodeStrategyManager geocodeStrategyManager;
    private MapDetailsActivity mCurrentActivity;
    private DirectionDropOffData tripFinishedData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.multidelivery_mukamal_fragment,
                container,
                false);
        unbinder = ButterKnife.bind(this, view);
        mCurrentActivity = (MapDetailsActivity) getActivity();
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
                    public void onMultiDeliveryCompleteRide(int position, String whichDelivery) {
                        if (!list.get(position).isCompleted())
                            requestRideFinish(position, whichDelivery);
                    }
                });
        mRecyclerView.setAdapter(adapter);

        geocodeStrategyManager = new GeocodeStrategyManager(mCurrentActivity, placesDataHandler, Constants.NEAR_LBL);
    }

    /**
     * Show confirm dialog for ride finish.
     *
     * @param position The position of the view that has been clicked.
     */
    private void requestRideFinish(int position, String whichDelivery) {
        String title =
                getContext().getString(R.string.multidelivery_complete_msg_one) +
                        " " + whichDelivery + " " +
                        getContext().getString(R.string.multidelivery_complete_msg_two);
        Dialogs.INSTANCE.showLoader(getActivity());
        Dialogs.INSTANCE.showRideStatusDialog(getActivity(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tripFinishedData = list.get(position);
                geocodeStrategyManager.fetchLocation(AppPreferences.getLatitude(), AppPreferences.getLongitude(), true);
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialogs.INSTANCE.dismissDialog();
            }
        }, title);
    }

    private IPlacesDataHandler placesDataHandler = new PlacesDataHandler() {
        @Override
        public void onPlacesResponse(String response) {
            super.onPlacesResponse(response);
            Dialogs.INSTANCE.dismissDialog();
            requestMultiDeliveryTripFinished(tripFinishedData, response);
        }
    };

    /**
     * Invoked this method when multi delivery trip is going to finish
     *
     * @param data                  The data id trip that is going to be finished.
     * @param reverseGeoCodeAddress
     */
    private void requestMultiDeliveryTripFinished(DirectionDropOffData data, String reverseGeoCodeAddress) {
        Dialogs.INSTANCE.showLoader(getActivity());
        repository.requestMultiDeliveryDriverFinishRide(
                data, handler, reverseGeoCodeAddress);
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

        int n = bookingResponses.size();
        for (int i = 0; i < n; i++) {
            MultipleDeliveryBookingResponse bookingResponse = bookingResponses.get(i);
            MultipleDeliveryBookingResponse multipleDeliveryBookingResponse =
                    bookingResponses.get(i);
            DirectionDropOffData dropOff = new DirectionDropOffData.Builder()
                    .setmArea(bookingResponse.getDropOff().getPickupAddress())
                    .setPassengerName(multipleDeliveryBookingResponse
                            .getPassenger()
                            .getName())
                    .setTripID(multipleDeliveryBookingResponse.getTrip().getId())
                    .setDropOffNumberText(String.valueOf(i + 1))
                    .build();
            if (multipleDeliveryBookingResponse.getTrip().
                    getStatus().equalsIgnoreCase(TripStatus.ON_COMPLETED_TRIP) ||
                    multipleDeliveryBookingResponse.getTrip().
                            getStatus().equalsIgnoreCase(TripStatus.ON_FEEDBACK_TRIP)) {
                dropOff.setCompleted(true);
            }

            list.add(dropOff);
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

        MultiDeliveryCompleteRideData multiDeliveryCompleteRideData = response.getData();
        MultiDeliveryRideCompleteTripInfo tripInfo = multiDeliveryCompleteRideData.getTripInfo();

        MultipleDeliveryBookingResponse bookingResponse = callDriverData.
                getTripById(tripInfo.getTripID());

        bookingResponse.setInvoice(multiDeliveryCompleteRideData.getInvoice());

        bookingResponse.getTrip().setTripDistance(tripInfo.getTripDistance());
        bookingResponse.getTrip().setTripDuration(tripInfo.getTripDuration());
//        bookingResponse.getTrip().setStartAddress(tripInfo.getStartAddress());
        bookingResponse.getTrip().setEndAddress(tripInfo.getEndAddress());

        AppPreferences.setMultiDeliveryCallDriverData(callDriverData);
        Utils.redLog(TAG, new Gson().toJson(AppPreferences.getMultiDeliveryCallDriverData()));

        if (response.getCode() == HttpURLConnection.HTTP_OK) {
            ActivityStackManager.getInstance()
                    .startMultiDeliveryFeedbackActivity(getActivity(),
                            multiDeliveryCompleteRideData.getTripInfo().getTripID(), true);
            getActivity().finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        list = null;
    }
}

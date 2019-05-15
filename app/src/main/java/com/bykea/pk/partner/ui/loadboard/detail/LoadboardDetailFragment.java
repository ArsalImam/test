package com.bykea.pk.partner.ui.loadboard.detail;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.loadboard.LoadboardBookingDetailData;
import com.bykea.pk.partner.models.response.AcceptLoadboardBookingResponse;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.fragments.HomeFragment;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.adapters.LoadBoardOrdersAdapter;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Permissions;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Loadboard booking fragment which contain all detail information related to current booking
 */
public class LoadboardDetailFragment extends Fragment implements View.OnClickListener {

    private LoadboardDetailActivity mCurrentActivity;
    private LoadboardBookingDetailData data;

    @BindView(R.id.loadBoardMapFragment)
    MapView mapView;
    private GoogleMap mGoogleMap;

    @BindView(R.id.tVEstimatedTime)
    FontTextView tVEstimatedTime;
    @BindView(R.id.tVAddressPickUp)
    FontTextView tVAddressPickUp;

    @BindView(R.id.tVZoneDropOff)
    FontTextView tVZoneDropOff;
    @BindView(R.id.tVEstimatedDistanceDropOff)
    FontTextView tVEstimatedDistanceDropOff;
    @BindView(R.id.tVKilometre)
    FontTextView tVKilometre;
    @BindView(R.id.tVAddressDropOff)
    FontTextView tVAddressDropOff;

    @BindView(R.id.acceptBookingBtn)
    FontTextView acceptBookingBtn;

    @BindView(R.id.imgViewDirectionPickUp)
    AppCompatImageView imgViewDirectionPickUp;
    @BindView(R.id.imgViewDirectionDropOff)
    AppCompatImageView imgViewDirectionDropOff;
    @BindView(R.id.imgViewAudioPlay)
    AppCompatImageView imgViewAudioPlay;

    /**
     * fragment instance that accept booking detail data to be displayed
     *
     * @param data booking detail data
     * @return current fragment
     */
    public static LoadboardDetailFragment newInstance(LoadboardBookingDetailData data) {
        LoadboardDetailFragment fragment = new LoadboardDetailFragment();
        fragment.data = data;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_loadboard_detail, container, false);
        ButterKnife.bind(this, v);

        mCurrentActivity = (LoadboardDetailActivity) getActivity();

        try {
            mapView.onCreate(savedInstanceState);
            MapsInitializer.initialize(mCurrentActivity.getApplicationContext());
        } catch (Exception e) {
            Utils.redLog("HomeScreenException", e.getMessage());
            e.printStackTrace();
        }
        mapView.getMapAsync(mapReadyCallback);

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

    private OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            //check if fragment is replaced before map is ready
            if (mCurrentActivity == null || getView() == null) {
                return;
            }
            mGoogleMap = googleMap;

            Utils.formatMap(mGoogleMap);
            //mGoogleMap.clear();
            if (mCurrentActivity != null && !Permissions.hasLocationPermissions(mCurrentActivity)) {
                Permissions.getLocationPermissions(getActivity());
            } else {
                if (enableLocation()) return;
            }

            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(24.8669095,67.0803487)
                    , 12.0f));
        }
    };

    private boolean enableLocation() {
        if (ActivityCompat.checkSelfPermission(mCurrentActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return true;
        }
        if (mGoogleMap != null) {
            mGoogleMap.setMyLocationEnabled(true);
        }
        return false;
    }

    private void setMarkersForPickUpAndDropOff(GoogleMap mMap, LoadboardBookingDetailData data) {
        LatLng mLatLngPickUp = new LatLng(data.getPickupLoc().getLatitude(), data.getPickupLoc().getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLng(mLatLngPickUp));
        mMap.addMarker(new MarkerOptions().position(mLatLngPickUp));

        LatLng mLatLngDropOff = new LatLng(data.getEndLoc().getLatitude(), data.getEndLoc().getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLng(mLatLngDropOff));
        mMap.addMarker(new MarkerOptions().position(mLatLngDropOff));
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        setListeners();
    }

    /**
     * initialize views and set datails data and attach click listener
     */
    private void initViews() {
        if (data != null) {
            //bd_FareTV.setText(getString(R.string.seleted_amount_rs, data.getFareEstimation()));
            //bd_pickUpNameTV.setText(data.getPickupName());
            tVAddressPickUp.setText(data.getPickupAddress());
            //bd_pickUpTimeTV.setText(data.getDeliveryTimings());
//            if (data.getPickupZone() != null)
//                bd_pickUpZoneTV.setText(getString(R.string.pick_drop_name_ur, data.getPickupZone().getUrduName()));
//            else
//                bd_pickUpZoneTV.setText(getString(R.string.not_selected_ur));

            int etaInMinute = data.getPickupEta() / Constants.MINUTE_DIVISIBLE_VALUE;
            tVEstimatedTime.setText(String.valueOf(etaInMinute));
            tVAddressDropOff.setText(data.getDropoffAddress());

            if (data.getDropoffZone() != null)
                tVZoneDropOff.setText(getString(R.string.pick_drop_name_ur, data.getDropoffZone().getUrduName()));
            else
                tVZoneDropOff.setText(getString(R.string.not_selected_ur));

            float estimatedDistance = data.getDropoffDistance() / Constants.KILOMETER_DIVISIBLE_VALUE;
            tVEstimatedDistanceDropOff.setText(String.format("%.1f", estimatedDistance));
        }
        acceptBookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setListeners() {
        imgViewDirectionPickUp.setOnClickListener(this);
        imgViewDirectionDropOff.setOnClickListener(this);
        imgViewAudioPlay.setOnClickListener(this);
    }

    /**
     * open Google's default Map application to draw route and enable direction call
     */
    private void startGoogleDirectionsApp() {
        try {
            if (data != null) {
                String start = data.getPickupLoc().getLatitude() + "," + data.getPickupLoc().getLongitude();
                String destination = data.getEndLoc().getLatitude() + "," + data.getEndLoc().getLongitude();

                try {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + start + "&daddr=" + destination + "&mode=motorcycle");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                } catch (Exception ex) {
                    Utils.appToast(mCurrentActivity, "Please install Google Maps");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * logging accepted response for tracking data
     *
     * @param callData   response data
     * @param isOnAccept trip state
     */
    private void logMixpanelEvent(NormalCallData callData, boolean isOnAccept) {
        try {

            JSONObject data = new JSONObject();
            data.put("PassengerID", callData.getPassId());
            data.put("DriverID", AppPreferences.getPilotData().getId());
            data.put("TripID", callData.getTripId());
            data.put("TripNo", callData.getTripNo());
            data.put("PickUpLocation", callData.getStartLat() + "," + callData.getStartLng());
            data.put("timestamp", Utils.getIsoDate());
            if (StringUtils.isNotBlank(callData.getEndLat()) && StringUtils.isNotBlank(callData.getEndLng())) {
                data.put("DropOffLocation", callData.getEndLat() + "," + callData.getEndLng());
            }
            data.put("ETA", Utils.formatETA(callData.getArivalTime()));
            data.put("EstimatedDistance", AppPreferences.getEstimatedDistance());
            data.put("CurrentLocation", Utils.getCurrentLocation());
            data.put("PassengerName", callData.getPassName());
            data.put("DriverName", AppPreferences.getPilotData().getFullName());
            data.put("type", callData.getCallType());
            data.put("SignUpCity", AppPreferences.getPilotData().getCity().getName());

            if (isOnAccept) {
                data.put("AcceptSeconds", "0");
                Utils.logEvent(mCurrentActivity, callData.getPassId(), Constants.AnalyticsEvents.ON_ACCEPT.replace(
                        Constants.AnalyticsEvents.REPLACE, callData.getCallType()), data);
            } else {
                Utils.logEvent(mCurrentActivity, callData.getPassId(), Constants.AnalyticsEvents.ON_RECEIVE_NEW_JOB.replace(
                        Constants.AnalyticsEvents.REPLACE, callData.getCallType()), data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgViewDirectionPickUp:
                Utils.appToast(getContext(), "imgViewDirectionPickUp");
                break;

            case R.id.imgViewDirectionDropOff:
                Utils.appToast(getContext(), "imgViewDirectionDropOff");
                break;

            case R.id.imgViewAudioPlay:
                Utils.appToast(getContext(), "imgViewAudioPlay");
                break;
            //region
            case R.id.acceptBookingBtn:
                if (Connectivity.isConnectedFast(mCurrentActivity)) {
                    Dialogs.INSTANCE.showLoader(mCurrentActivity);
                    new UserRepository().acceptLoadboardBooking(mCurrentActivity, data.getId(), new UserDataHandler() {
                        @Override
                        public void onAcceptLoadboardBookingResponse(AcceptLoadboardBookingResponse response) {
                            Dialogs.INSTANCE.dismissDialog();
                            if (response != null) {
                                if (response.isSuccess()) {
                                    AppPreferences.clearTripDistanceData();
                                    AppPreferences.setTripStatus(TripStatus.ON_ACCEPT_CALL);

                                    NormalCallData callData = response.getData();
                                    callData.setStatus(TripStatus.ON_ACCEPT_CALL);
                                    AppPreferences.setCallData(callData);
                                    logMixpanelEvent(callData, true);

                                    AppPreferences.addLocCoordinateInTrip(AppPreferences.getLatitude(), AppPreferences.getLongitude());

                                    AppPreferences.setIsOnTrip(true);
                                    ActivityStackManager.getInstance().startJobActivity(mCurrentActivity);
                                    mCurrentActivity.finish();
                                } else if (response.getSubCode() == Constants.ApiError.LOADBOARD_BOOKING_ALREADY_TAKEN) {
                                    Utils.appToast(mCurrentActivity, response.getMessage());
                                    ActivityStackManager.getInstance().startHomeActivity(mCurrentActivity);
                                } else if (response.getSubCode() == Constants.ApiError.LOADBOARD_ALREADY_IN_TRIP) {
                                    Utils.appToast(mCurrentActivity, response.getMessage());
                                } else {
                                    Utils.setCallIncomingState();
                                    Dialogs.INSTANCE.showToast(mCurrentActivity, response.getMessage());
                                }
                            } else {
                                Dialogs.INSTANCE.showTempToast(mCurrentActivity, "Response is null");
                            }
                        }

                        @Override
                        public void onError(int errorCode, String errorMessage) {
                            Dialogs.INSTANCE.dismissDialog();
                            if (errorCode == HTTPStatus.UNAUTHORIZED) {
                                Utils.onUnauthorized(mCurrentActivity);
                            } else {
                                Dialogs.INSTANCE.showTempToast(mCurrentActivity, errorMessage);
                            }
                        }
                    });
                } else {
                    Utils.appToast(mCurrentActivity, getString(R.string.internet_error));
                }
                break;
            //endregion
        }

//        bd_pickUpPhoneIV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Utils.callingIntent(mCurrentActivity, data.getPickupPhone());
//            }
//        });
//        bd_dropOffPhoneIV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Utils.callingIntent(mCurrentActivity, data.getReceiverPhone());
//            }
//        });
//        bd_directionIV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startGoogleDirectionsApp();
//            }
//        });
    }
}

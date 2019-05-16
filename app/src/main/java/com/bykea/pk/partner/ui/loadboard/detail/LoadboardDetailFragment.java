package com.bykea.pk.partner.ui.loadboard.detail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.loadboard.LoadboardBookingDetailData;
import com.bykea.pk.partner.models.response.AcceptLoadboardBookingResponse;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Permissions;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Loadboard booking fragment which contain all detail information related to current booking
 */
public class LoadboardDetailFragment extends Fragment implements View.OnClickListener {

    private LoadboardDetailActivity mCurrentActivity;
    private LoadboardBookingDetailData data;
    private static final double EARTHRADIUS = 6366198;
    private Location mCurrentLocation;

    private SupportMapFragment mapView;

    private GoogleMap mGoogleMap;
    private ArrayList<Marker> mMarkerList = new ArrayList<>();

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

        mapView = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.loadBoardMapFragment);

        mCurrentActivity = (LoadboardDetailActivity) getActivity();
        try {
            mapView.onCreate(savedInstanceState);
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            Utils.redLog("HomeScreenException", e.getMessage());
            e.printStackTrace();
        }
        mCurrentLocation = new Location(StringUtils.EMPTY);
        setCurrentLocation();
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
        public void onMapReady(final GoogleMap googleMap) {
            //check if fragment is replaced before map is ready
            if (mCurrentActivity == null) {
                return;
            }
            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    if (mCurrentActivity == null || googleMap == null) {
                        return;
                    }
                    mGoogleMap = googleMap;
                    mGoogleMap.clear();
                    Utils.formatMap(mGoogleMap);
                    mGoogleMap.getUiSettings().setScrollGesturesEnabled(false);

                    com.google.maps.model.LatLng driverLatLng = new com.google.maps.model.LatLng(
                            AppPreferences.getLatitude(),
                            AppPreferences.getLongitude()
                    );
                    getDriverRoadPosition(driverLatLng);

                    if (mCurrentActivity != null && !Permissions.hasLocationPermissions(mCurrentActivity)) {
                        Permissions.getLocationPermissions(getActivity());
                    }
                    setMarkersForPickUpAndDropOff(mGoogleMap, data);
                }
            });

        }
    };

    private void setCurrentLocation() {
        mCurrentLocation.setLatitude(AppPreferences.getLatitude());
        mCurrentLocation.setLongitude(AppPreferences.getLongitude());
    }

    private void setMarkersForPickUpAndDropOff(GoogleMap mMap, LoadboardBookingDetailData data) {
        LatLng mLatLngPickUp = new LatLng(data.getPickupLoc().getLatitude(), data.getPickupLoc().getLongitude());
        SetMarker(mMap, mLatLngPickUp);

        LatLng mLatLngDropOff = new LatLng(data.getEndLoc().getLatitude(), data.getEndLoc().getLongitude());
        SetMarker(mMap, mLatLngDropOff);

        setPickupBounds(mMap);
    }

    private void SetMarker(GoogleMap mMap, LatLng mLatLngPickUp) {
        Marker mMarker = mMap.addMarker(new MarkerOptions().position(mLatLngPickUp));
        mMarkerList.add(mMarker);
    }

    private void setPickupBounds(GoogleMap mMap) {
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(getCurrentLatLngBounds(), 30);
        int padding = (int) mCurrentActivity.getResources().getDimension(R.dimen._70sdp);
        mMap.setPadding(0, padding, 0, 0);
        mMap.moveCamera(cu);
    }

    private LatLngBounds getCurrentLatLngBounds() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : mMarkerList) {
            builder.include(marker.getPosition());
        }
        LatLngBounds tmpBounds = builder.build();
        /* Add 2 points 1000m northEast and southWest of the center.
         * They increase the bounds only, if they are not already larger
         * than this.
         * 1000m on the diagonal translates into about 709m to each direction. */
        LatLng center = tmpBounds.getCenter();
        LatLng northEast = move(center, 709, 709);
        LatLng southWest = move(center, -709, -709);

        builder.include(southWest);
        builder.include(northEast);
        return builder.build();
    }

    private static LatLng move(LatLng startLL, double toNorth, double toEast) {
        double lonDiff = meterToLongitude(toEast, startLL.latitude);
        double latDiff = meterToLatitude(toNorth);
        return new LatLng(startLL.latitude + latDiff, startLL.longitude
                + lonDiff);
    }

    private static double meterToLongitude(double meterToEast, double latitude) {
        double latArc = Math.toRadians(latitude);
        double radius = Math.cos(latArc) * EARTHRADIUS;
        double rad = meterToEast / radius;
        return Math.toDegrees(rad);
    }

    private static double meterToLatitude(double meterToNorth) {
        double rad = meterToNorth / EARTHRADIUS;
        return Math.toDegrees(rad);
    }


    public synchronized void getDriverRoadPosition(com.google.maps.model.LatLng normalLocation) {
        if (normalLocation != null && normalLocation.lat != 0.0 && normalLocation.lng != 0.0) {
            onGetLocation(normalLocation.lat, normalLocation.lng);
        }
    }

    private void onGetLocation(double lat, double lng) {
        if (mCurrentLocation != null/* && callDriverData != null*/) {
            mCurrentLocation.setLatitude(lat);
            mCurrentLocation.setLongitude(lng);
            updateDriverMarker(
                    String.valueOf(mCurrentLocation.getLatitude()),
                    String.valueOf(mCurrentLocation.getLongitude())
            );
        } else {
            mCurrentLocation = new Location(LocationManager.GPS_PROVIDER);
            mCurrentLocation.setLatitude(lat);
            mCurrentLocation.setLongitude(lng);
        }
    }

    private void updateDriverMarker(String snappedLatitude, String snappedLongitude) {
        if (null != mGoogleMap) {
            //if driver marker is null add driver marker on google map
            if (null == driverMarker) {
                Bitmap driverIcon = Utils.getBitmap(mCurrentActivity, R.drawable.ic_delivery_bike);
                driverMarker = mGoogleMap.addMarker(new MarkerOptions().
                        icon(
                                BitmapDescriptorFactory.fromBitmap(
                                        driverIcon
                                )
                        )
                        .position(
                                new LatLng(
                                        Double.parseDouble(snappedLatitude),
                                        Double.parseDouble(snappedLongitude)
                                )
                        ));
                mMarkerList.add(driverMarker);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        setListeners();
    }

    private boolean animationStart = false;
    private Marker driverMarker;

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
        Intent mapIntent = null;
        String sourceAddress, destinationAddress;
        switch (view.getId()) {
            case R.id.imgViewDirectionPickUp:
                if (data != null) {
                    sourceAddress = mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude();
                    destinationAddress = data.getPickupLoc().getLatitude() + "," + data.getPickupLoc().getLongitude();
                    openGoogleMap(sourceAddress,destinationAddress);
                }
                break;

            case R.id.imgViewDirectionDropOff:
                if (data != null) {
                    sourceAddress =  data.getPickupLoc().getLatitude() + "," +  data.getPickupLoc().getLongitude();
                    destinationAddress = data.getEndLoc().getLatitude() + "," + data.getEndLoc().getLongitude();
                    openGoogleMap(sourceAddress,destinationAddress);
                }
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

    private void openGoogleMap(String sourceAddress, String destinationAddress) {
        String uri = Constants.GoogleMap.GOOGLE_NAVIGATE_ENDPOINT + sourceAddress +
                Constants.GoogleMap.GOOGLE_DESTINATION_ENDPOINT + destinationAddress;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName(Constants.GoogleMap.GOOGLE_MAP_PACKAGE,
                Constants.GoogleMap.GOOGLE_MAP_ACTIVITY);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}

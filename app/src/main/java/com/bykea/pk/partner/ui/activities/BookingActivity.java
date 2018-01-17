package com.bykea.pk.partner.ui.activities;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bykea.pk.partner.Notifications;
import com.bykea.pk.partner.models.response.CheckDriverStatusResponse;
import com.bykea.pk.partner.models.response.UpdateDropOffResponse;
import com.bykea.pk.partner.ui.helpers.adapters.PlaceAutocompleteAdapter;
import com.bykea.pk.partner.utils.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.communication.socket.WebIORequestHandler;
import com.bykea.pk.partner.models.response.ArrivedResponse;
import com.bykea.pk.partner.models.response.BeginRideResponse;
import com.bykea.pk.partner.models.response.CancelRideResponse;
import com.bykea.pk.partner.models.response.EndRideResponse;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.tracking.AbstractRouting;
import com.bykea.pk.partner.tracking.RoadLocationListener;
import com.bykea.pk.partner.tracking.Route;
import com.bykea.pk.partner.tracking.RouteException;
import com.bykea.pk.partner.tracking.Routing;
import com.bykea.pk.partner.tracking.RoutingListener;
import com.bykea.pk.partner.ui.helpers.StringCallBack;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.NetworkChangeListener;
import com.bykea.pk.partner.utils.Permissions;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;
import com.google.maps.android.PolyUtil;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BookingActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener,
        RoutingListener {

    @Bind(R.id.startAddressTv)
    FontTextView startAddressTv;
    @Bind(R.id.tvTripId)
    FontTextView tvTripId;
    @Bind(R.id.tvCodAmount)
    FontTextView tvCodAmount;
    @Bind(R.id.tvPWalletAmount)
    FontTextView tvPWalletAmount;
    @Bind(R.id.llTopRight)
    LinearLayout llTopRight;
    @Bind(R.id.callbtn)
    ImageView callbtn;
    @Bind(R.id.cancelBtn)
    FontTextView cancelBtn;
    @Bind(R.id.chatBtn)
    ImageView chatBtn;
    /*    @Bind(R.id.callerIv)
        CircularImageView callerIv;*/
    @Bind(R.id.callerNameTv)
    FontTextView callerNameTv;
    @Bind(R.id.TimeTv)
    FontTextView timeTv;
    @Bind(R.id.distanceTv)
    FontTextView distanceTv;
    @Bind(R.id.endAddressTv)
    FontTextView endAddressTv;
    @Bind(R.id.tvEstimation)
    FontTextView tvEstimation;
    @Bind(R.id.jobBtn)
    FontTextView jobBtn;
    @Bind(R.id.currentLocationIv)
    ImageView currentLocationIv;
    @Bind(R.id.currentLocIv)
    ImageView currentLocIv;

    private String canceOption = "Didn't show up";

    //GOOGLE NEAR BY PLACE SEARCH VIEW
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView mAutocompleteView;
    private Place place = null;

    public static boolean isJobActivityLive = false;
    //    private List<com.google.maps.model.LatLng> mCapturedLocations;


    private BookingActivity mCurrentActivity;
    private NormalCallData callData;
    private UserRepository dataRepository;
    private String cancelReason = StringUtils.EMPTY;
    public static boolean isWazeCheck = true;
    public static boolean isGoogleCheck;
    public static int cancelRequest = 0;
    private String mGoogleSrcLatLng = "";
    private String mGoogleDesLatLng = "";
    private int distanceToPickup = 0;

    private Marker driverMarker, dropOffMarker, pickUpMarker/*, passCurrentLocMarker*/;
    private MarkerOptions driverMarkerOptions;
    private Polyline mapPolylines;
    private List<LatLng> mRouteLatLng;
    private PolylineOptions mPolylineOptions;

    private List<Circle> mArriveTraceCircleList;
    private List<Circle> mStartTraceCircleList;
    //    private LatLng dropOff = new LatLng(33.438706, 72.970290);

    //LOCATION CHANGE UPDATE DATA MEMBERS
    private Location mCurrentLocation;
    private Location mPreviousLocation;
    private String mLocBearing = "0.0";
    private String mpreLocBearing = "0";
    private boolean isDriverUpdated;
    private boolean animationStart = false, isFirstTime = true;

    //HANDLING RESUME CASE.
//    private NormalCallData resumeTripData;
    private boolean isResume = false;


    private GoogleMap mGoogleMap;
    private MapView mapView;
    private ProgressDialog progressDialogJobActivity;
    private LatLng lastPolyLineLatLng, lastApiCallLatLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ButterKnife.bind(this);
        mCurrentActivity = this;
        dataRepository = new UserRepository();
        ButterKnife.bind(this);
        AppPreferences.setStatsApiCallRequired(true);
        Utils.keepScreenOn(mCurrentActivity);
        Notifications.removeAllNotifications(mCurrentActivity);
        mGoogleApiClient = new GoogleApiClient.Builder(mCurrentActivity)
                .enableAutoManage(mCurrentActivity, 0 /* clientId */, mCurrentActivity)
                .addApi(Places.GEO_DATA_API)
                .build();


        mapView = (MapView) findViewById(R.id.jobMapFragment);
        final Bundle mapViewSavedInstanceState = savedInstanceState != null ? savedInstanceState.getBundle("mapViewSaveState") : null;
        mapView.onCreate(mapViewSavedInstanceState);
        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(mapReadyCallback);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            Dialogs.INSTANCE.showLocationSettings(mCurrentActivity, Permissions.LOCATION_PERMISSION);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        //This MUST be done before saving any of your own or your base class's     variables
        final Bundle mapViewSaveState = new Bundle(outState);
        mapView.onSaveInstanceState(mapViewSaveState);
        outState.putBundle("mapViewSaveState", mapViewSaveState);
        //Add any other variables here.
        super.onSaveInstanceState(outState);
    }

    private OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(final GoogleMap googleMap) {
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
                    mGoogleMap.setPadding(0, 0, 0, (int) mCurrentActivity.getResources().getDimension(R.dimen.map_padding_bottom));
                    getDriverRoadPosition(mCurrentActivity,
                            new com.google.maps.model.LatLng(AppPreferences.getLatitude(),
                                    AppPreferences.getLongitude()));
                    if (callData != null && StringUtils.isNotBlank(callData.getStartLat())
                            && StringUtils.isNotBlank(callData.getStartLng())) {
                        updatePickupMarker(callData.getStartLat(), callData.getStartLng());

//                        TrackingMap.addMarker(mCurrentActivity, mLocBearing, mGoogleMap, Double.parseDouble(callData.getStartLat())
//                                , Double.parseDouble(callData.getStartLng()), R.drawable.ic_destination_temp);
                        LatLngBounds bounds;
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(new LatLng(AppPreferences.getLatitude(),
                                AppPreferences.getLongitude()));
                        builder.include(new LatLng(Double.parseDouble(callData.getStartLat())
                                , Double.parseDouble(callData.getStartLng())));
                        bounds = builder.build();
                        zoomFirstTime(new LatLng(AppPreferences.getLatitude(),
                                AppPreferences.getLongitude()));
                        setPickupBounds();


                        if (isResume) {
                            if (StringUtils.isNotBlank(callData.getStartLat()) && StringUtils.isNotBlank(callData.getStartLng())
                                    && StringUtils.isNotBlank(callData.getEndLat()) && StringUtils.isNotBlank(callData.getEndLng())) {
                                boundRoute(new LatLng(AppPreferences.getLatitude(),
                                                AppPreferences.getLongitude())
                                        , new LatLng(Double.parseDouble(callData.getStartLat()),
                                                Double.parseDouble(callData.getStartLng())),
                                        new LatLng(Double.parseDouble(callData.getEndLat()),
                                                Double.parseDouble(callData.getEndLng())));
                            }
                        }
                    }

                }
            });
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 49)//CHECK FOR DROP OFF PLACE RESULT
        {
            if (StringUtils.isNotBlank(AppPreferences.getDropOffAddress(mCurrentActivity))) {
                callData.setEndLat(AppPreferences.getDropOffLat());
                callData.setEndLng(AppPreferences.getDropOffLng(mCurrentActivity));
                callData.setEndAddress(AppPreferences.getDropOffAddress(mCurrentActivity));
                AppPreferences.setCallData(callData);
                updateDropOffToServer();

            }

        }
        if (requestCode == Permissions.LOCATION_PERMISSION) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                Dialogs.INSTANCE.showLocationSettings(mCurrentActivity, Permissions.LOCATION_PERMISSION);
            else {
                ActivityStackManager.getInstance().startLocationService(mCurrentActivity);
            }
        }
    }

    private void updateDropOffToServer() {
        Dialogs.INSTANCE.showLoader(mCurrentActivity);
        updateDropOff();
        dataRepository.updateDropOff(driversDataHandler, mCurrentActivity, callData.getTripId(),
                callData.getEndAddress(), callData.getEndLat() + "", callData.getEndLng() + "");
    }

    private void updateDropOff() {
        if (StringUtils.isNotBlank(callData.getEndAddress())
                && StringUtils.isNotBlank(callData.getEndLat())
                && StringUtils.isNotBlank(callData.getEndLng())) {
            endAddressTv.setText(callData.getEndAddress());
            if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_START_TRIP)) {
                lastApiCallLatLng = null;
                AppPreferences.setLastDirectionsApiCallTime(0);
                if (mRouteLatLng != null && mRouteLatLng.size() > 0) {
                    mRouteLatLng.clear();
                }
                drawRouteToDropOff();
                mGoogleDesLatLng = callData.getEndLat() + "," + callData.getEndLng();
                updatePickupMarker(callData.getEndLat(), callData.getEndLng());
            }
        }
    }

    private void hideButtonOnArrived() {
//        callbtn.setVisibility(View.GONE);
//        chatBtn.setVisibility(View.GONE);
        cancelBtn.setVisibility(View.GONE);
    }


    @OnClick({R.id.callbtn, R.id.cancelBtn, R.id.chatBtn, R.id.jobBtn, R.id.currentLocIv, R.id.currentLocationIv, R.id.endAddressTv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chatBtn:
                if (!callData.isDispatcher()) {
                    ActivityStackManager.getInstance()
                            .startChatActivity(callData.getPassName(), "", true, mCurrentActivity);
                } else {
                    Utils.sendSms(mCurrentActivity, callData.getPhoneNo());
                }
                break;
            case R.id.endAddressTv:
                startActivityForResult(new Intent(mCurrentActivity, PlacesActivity.class), 49);
                break;
            case R.id.callbtn:
                Utils.callingIntent(mCurrentActivity, callData.getPhoneNo());
                break;
            case R.id.cancelBtn:
                if (Utils.isCancelAfter5Min()) {
                    Dialogs.INSTANCE.showAlertDialog(mCurrentActivity, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Dialogs.INSTANCE.dismissDialog();
                            cancelReasonDialog();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Dialogs.INSTANCE.dismissDialog();
                        }
                    }, "Cancel Trip", "If you are cancelling after " + AppPreferences.getSettings().getSettings().getCancel_time() + " minutes of booking, you may be charged a small cancellation fee.");
                } else {
                    cancelReasonDialog();
                }
                break;
            case R.id.jobBtn:
                if (Connectivity.isConnectedFast(mCurrentActivity)) {
                    Dialogs.INSTANCE.showLoader(mCurrentActivity);
                    if (jobBtn.getText().toString().equalsIgnoreCase(getString(R.string.button_text_arrived))) {
                        int distance = (int) Utils.calculateDistance(AppPreferences.getLatitude(), AppPreferences.getLongitude(),
                                Double.parseDouble(callData.getStartLat()), Double.parseDouble(callData.getStartLng()));
                        if (distance > 200) {
                            boolean showTickBtn = distance < AppPreferences.getSettings().getSettings().getArrived_min_dist();
                            Dialogs.INSTANCE.showConfirmArrivalDialog(mCurrentActivity, showTickBtn, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Dialogs.INSTANCE.dismissDialog();
                                    requestArrived();
                                }
                            });
                        } else {
                            Dialogs.INSTANCE.showRideStatusDialog(mCurrentActivity, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Dialogs.INSTANCE.dismissDialog();
                                    requestArrived();
                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Dialogs.INSTANCE.dismissDialog();
                                }
                            }, "ARRIVED?");
                        }
                    }
                    //CHECK FOR BEGIN TRIP BUTTON CLICK
                    else if (jobBtn.getText().toString().equalsIgnoreCase(getString(R.string.button_text_start))) {
                        Dialogs.INSTANCE.showRideStatusDialog(mCurrentActivity, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Dialogs.INSTANCE.dismissDialog();
                                Dialogs.INSTANCE.showLoader(mCurrentActivity);
                                AppPreferences.clearTripDistanceData();
                                dataRepository.requestBeginRide(mCurrentActivity, driversDataHandler,
                                        callData.getEndLat(), callData.getEndLng(), callData.getEndAddress());
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Dialogs.INSTANCE.dismissDialog();
                            }
                        }, "START?");
                    } else if (jobBtn.getText().toString().equalsIgnoreCase(getString(R.string.button_text_finish))) {
                        Dialogs.INSTANCE.showRideStatusDialog(mCurrentActivity, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Dialogs.INSTANCE.dismissDialog();
                                Dialogs.INSTANCE.showLoader(mCurrentActivity);
                                logMixPanelEvent();
                                dataRepository.requestEndRide(mCurrentActivity, driversDataHandler);
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Dialogs.INSTANCE.dismissDialog();
                            }
                        }, "FINISH?");
                    }
                } else {
                    Dialogs.INSTANCE.showError(mCurrentActivity, jobBtn, getString(R.string.error_internet_connectivity));
                }

                break;
            case R.id.currentLocationIv:

                //CHECK FOR THE SRC AND DEST SHOULD NOT BE EMPTY FOR GOOGLE TRACKING..
                if (StringUtils.isNotBlank(mGoogleSrcLatLng) && StringUtils.isNotBlank(mGoogleDesLatLng)) {
                    try {
                        String uri = "";
                        Intent intent;
                        if (!endAddressTv.getText().toString().equalsIgnoreCase(getString(R.string.destination_not_selected_msg))) {
                            uri = "http://maps.google.com/maps?saddr=" + mGoogleSrcLatLng +
                                    "&daddr=" + mGoogleDesLatLng;
                            intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://maps.google.com/maps?saddr=" + mGoogleSrcLatLng +
                                            "&daddr=" + mGoogleDesLatLng));
                        } else {
                            uri = "http://maps.google.com/maps?saddr=&daddr=";
                            intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://maps.google.com/maps?saddr=&daddr="));
                        }
                        Utils.redLog("Google Route Link ", uri);

                        startActivity(intent);
                    } catch (ActivityNotFoundException ex) {
                        Toast.makeText(mCurrentActivity, "Please install google play services", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.currentLocIv:
                setDriverLocation();
                break;
        }
    }

    private void logMixPanelEvent() {
        JSONObject properties = new JSONObject();
        try {
            properties.put("TripNo", callData.getTripNo());
            properties.put("PassengerID", callData.getPassId());
            properties.put("DriverID", AppPreferences.getPilotData().getId());
            properties.put("timestamp", Utils.getIsoDate());
            properties.put("City", AppPreferences.getPilotData().getCity().getName());
            properties.put("PassengerName", callData.getPassName());
            properties.put("DriverName", AppPreferences.getPilotData().getFullName());
            properties.put("TripID", callData.getTripId());
            properties.put("type", callData.getCallType());
            if (StringUtils.isNotBlank(Utils.getCurrentLocation())) {
                properties.put("endDropOff", Utils.getCurrentLocation());
            }

            Utils.logEvent(mCurrentActivity, callData.getPassId(), Constants.RIDE_COMPLETE.replace("_R_", callData.getCallType()), properties);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void requestArrived() {
        Dialogs.INSTANCE.showLoader(mCurrentActivity);
        dataRepository.requestArrived(mCurrentActivity, driversDataHandler);
    }


    private void setDriverLocation() {
        if (null != mGoogleMap) {
            Utils.formatMap(mGoogleMap);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(AppPreferences.getLatitude()
                            , AppPreferences.getLongitude())
                    , 16f));
        }
    }


    private void cancelReasonDialog() {
        Dialogs.INSTANCE.showCancelDialog(mCurrentActivity, new StringCallBack() {
            @Override
            public void onCallBack(String msg) {
                Dialogs.INSTANCE.showLoader(mCurrentActivity);
                cancelReason = msg;
                dataRepository.requestCancelRide(mCurrentActivity, driversDataHandler,
                        msg);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(Keys.BROADCAST_CANCEL_RIDE);
//        intentFilter.addAction(Keys.BROADCAST_COMPLETE_BY_ADMIN);
//        intentFilter.addAction(Keys.BROADCAST_DROP_OFF_UPDATED);
//        registerReceiver(cancelRideReceiver, intentFilter);
        registerReceiver(locationReceiver, new IntentFilter(Keys.LOCATION_UPDATE_BROADCAST));
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        setInitialData();
//        LocationService.setContext(BookingActivity.this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.location.PROVIDERS_CHANGED");
        intentFilter.addCategory("android.intent.category.DEFAULT");
        registerReceiver(networkChangeListener, intentFilter);
        checkGps();
        checkConnectivity(mCurrentActivity);
         /*SETTING SERVICE CONTEXT WITH ACTIVITY TO SEND BROADCASTS*/
//        LocationService.setContext(BookingActivity.this);
//        WebIORequestHandler.getInstance().setContext(mCurrentActivity);
        WebIORequestHandler.getInstance().registerChatListener();
        isJobActivityLive = true;
        AppPreferences.setJobActivityOnForeground(true);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppPreferences.setJobActivityOnForeground(false);
        mGoogleApiClient.disconnect();

    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }


    @Override
    protected void onDestroy() {
        Utils.flushMixPanelEvent(mCurrentActivity);
        progressDialogJobActivity.dismiss();
        AppPreferences.setJobActivityOnForeground(false);
        AppPreferences.setLastDirectionsApiCallTime(0);
        // Unregister here due to some reasons.
        unregisterReceiver(locationReceiver);
//        unregisterReceiver(cancelRideReceiver);
        unregisterReceiver(networkChangeListener);
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 13:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Utils.callingIntent(mCurrentActivity, callData.getPhoneNo());
                } else {
                    Dialogs.INSTANCE.showError(mCurrentActivity,
                            jobBtn, "Call permission is denied to call passenger.");
                }
                break;
        }

    }

    /*******************************************************************************************
     * METHODS FOR SETTING CALL DATA ACCORDING TO THE TRIP STATE
     ****************************************************************************************/
    private void setInitialData() {
        if (progressDialogJobActivity == null) {
            progressDialogJobActivity = new ProgressDialog(mCurrentActivity);
            progressDialogJobActivity.setCancelable(false);
            progressDialogJobActivity.setIndeterminate(true);
            progressDialogJobActivity.setMessage(getString(R.string.internet_error));
        }
        AppPreferences.setIsOnTrip(true);
        mLocBearing = AppPreferences.getBearing() + "";
        mCurrentLocation = new Location("");
        mCurrentLocation.setLongitude(AppPreferences.getLongitude());
        mCurrentLocation.setLatitude(AppPreferences.getLatitude());
        mPreviousLocation = new Location("");
        mArriveTraceCircleList = new ArrayList<>();
        mStartTraceCircleList = new ArrayList<>();

        callData = AppPreferences.getCallData();
        isResume = true;
        if (callData != null) {
            AppPreferences.setTripStatus(callData.getStatus());
//        setCallData();
            tvTripId.setText(callData.getTripNo());
            if (StringUtils.isNotBlank(callData.getCodAmount())) {
                String codAmount = "Rs. " + callData.getCodAmount();
                llTopRight.setVisibility(View.VISIBLE);
                tvCodAmount.setVisibility(View.VISIBLE);
                tvTripId.setGravity(Gravity.CENTER | Gravity.START);
                tvCodAmount.setGravity(Gravity.CENTER | Gravity.END);
                tvCodAmount.setText(callData.isCod() ? "COD   " + codAmount : codAmount);
            } else {
                llTopRight.setVisibility(View.GONE);
                tvCodAmount.setVisibility(View.GONE);
                tvTripId.setGravity(Gravity.CENTER);
            }
            if (StringUtils.isBlank(callData.getStatus())) {
                setAcceptedState();
            } else {
                if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_ACCEPT_CALL)) {
                    Utils.redLog("RESUME TRIP", "ACCEPTED STATE RESUME");
                    setAcceptedState();
                } else if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_ARRIVED_TRIP)) {
                    Utils.redLog("RESUME TRIP", "ARRIVED STATE RESUME");
                    setArrivedState();
                } else if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_START_TRIP)) {
                    Utils.redLog("RESUME TRIP", "STARTED STATE RESUME");
                    setStartedState();
                }
            }

            if (StringUtils.isNotBlank(callData.getDistance()))
                distanceToPickup = (int) Double.parseDouble(callData.getDistance()) * 1000;

        }
    }

    private void setTimeDistance(String time, String distance) {
        timeTv.setText(time);
        distanceTv.setText(distance);
        AppPreferences.setEta(time);
        AppPreferences.setEstimatedDistance(distance);
    }

    private void setAcceptedState() {

        callerNameTv.setText(callData.getPassName());
        setTimeDistance(Utils.formatETA(callData.getArivalTime()),
                callData.getDistance());
        startAddressTv.setText(callData.getStartAddress());
      /*  if (StringUtils.isNotBlank(callData.getEndAddress())) {
            destTv.setText(callData.getEndAddress());
        }*/
       /* if (StringUtils.isNotBlank(callData.getPassImage())) {
            Picasso.with(mCurrentActivity).load(Utils.getImageLink(callData.getPassImage()))
                    .into(callerIv);
        }*/

        mGoogleSrcLatLng = AppPreferences.getLatitude() + "," + AppPreferences.getLongitude();
        mGoogleDesLatLng = callData.getStartLat() + "," + callData.getStartLng();

        String mLocLat = AppPreferences.getLatitude() + "";
        String mLocLng = AppPreferences.getLongitude() + "";

        drawRouteToPickup();
    }

    private void setArrivedState() {
        jobBtn.setText(getString(R.string.button_text_start));
        endAddressTv.setVisibility(View.VISIBLE);
        currentLocationIv.setVisibility(View.INVISIBLE);
        setOnArrivedData();
    }

    private void setStartedState() {
        endAddressTv.setVisibility(View.VISIBLE);
        currentLocationIv.setVisibility(View.VISIBLE);
        jobBtn.setText(getString(R.string.button_text_finish));
        setOnStartData();
        mGoogleSrcLatLng = callData.getStartLat() + "," + callData.getStartLng();
        mGoogleDesLatLng = callData.getEndLat() + "," + callData.getEndLng();

    }

    private void showWalletAmount() {
        tvPWalletAmount.setVisibility(View.VISIBLE);
        llTopRight.setVisibility(View.VISIBLE);
        if (StringUtils.isNotBlank(callData.getCodAmount())) {
            tvCodAmount.setVisibility(View.VISIBLE);
            tvCodAmount.setGravity(Gravity.BOTTOM | Gravity.END);
            tvPWalletAmount.setGravity(Gravity.TOP | Gravity.END);
        } else {
            tvCodAmount.setVisibility(View.GONE);
            tvPWalletAmount.setGravity(Gravity.CENTER | Gravity.END);
        }
        tvTripId.setGravity(Gravity.CENTER | Gravity.START);
        tvPWalletAmount.setText("Wallet   " + callData.getPassWallet());
    }

    private void setOnArrivedData() {
        showWalletAmount();
        if (mapPolylines != null) {
            mapPolylines.remove();
        }
        if (isResume) {
            callerNameTv.setText(callData.getPassName());
        }

        //CHECK FOR DESTINATION IF USER NOT SENT DESTINATION THEN ENABLE SECOND VIEW....
        if (StringUtils.isNotBlank(callData.getEndLat()) &&
                StringUtils.isNotBlank(callData.getEndLng()) &&
                StringUtils.isNotBlank(callData.getEndAddress())) {
            endAddressTv.setText(callData.getEndAddress());
            startAddressTv.setText(callData.getStartAddress());
            mGoogleSrcLatLng = callData.getStartLat() + "," + callData.getStartLng();
        } else {
            updateEtaAndCallData("0", "0");
            startAddressTv.setText(callData.getStartAddress());
            mGoogleSrcLatLng = callData.getStartLat() + "," + callData.getStartLng();
        }

        jobBtn.setText(getString(R.string.button_text_start));
        currentLocationIv.setVisibility(View.INVISIBLE);
        AppPreferences.setTripStatus(TripStatus.ON_ARRIVED_TRIP);

    }

    private void updateEtaAndCallData(String time, String distance) {
        callData.setArivalTime(time);
        callData.setDistance(distance);
        setTimeDistance(callData.getArivalTime(), callData.getDistance());
        AppPreferences.setCallData(callData);
    }

    private void setOnStartData() {
        showWalletAmount();
        hideButtonOnArrived();
        lastApiCallLatLng = null;
        AppPreferences.setLastDirectionsApiCallTime(0);
        if (mRouteLatLng != null && mRouteLatLng.size() > 0) {
            mRouteLatLng.clear();
        }
        if (isResume) {
            drawRouteToDropOff();
            callerNameTv.setText(callData.getPassName());
        }

        if (AppPreferences.getTripStatus().equalsIgnoreCase(TripStatus.ON_START_TRIP)) {
            startAddressTv.setText(callData.getStartAddress());
            endAddressTv.setText(callData.getEndAddress());
        }

        if (StringUtils.isBlank(callData.getEndAddress()))
            endAddressTv.setText(getString(R.string.destination_not_selected_msg));
        else endAddressTv.setText(callData.getEndAddress());

        if (StringUtils.isNotBlank(callData.getEndLat()) && StringUtils.isNotBlank(callData.getEndLng())) {
            updatePickupMarker(callData.getEndLat(), callData.getEndLng());
        } else if (pickUpMarker != null) {
            pickUpMarker.remove();
            pickUpMarker = null;
        }


    }


    /******************************************************************************************
     * REAL TIME TRACKING METHODS ARE IMPLEMENT HERE
     ******************************************************************************************/
    private synchronized void zoomFirstTime(LatLng location) {
        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(location)
                        .zoom(16f)
                        .build();

        mGoogleMap.moveCamera(
                CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void updateDriverMarker(String snappedLatitude, String snappedLongitude) {
        if (null != mGoogleMap) {

            //IF DRIVER MARKER IN NULL THEN ADD MARKER TO MAP.
            if (null == driverMarker) {
                driverMarker = mGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(
                        Utils.getMapIcon(callData.getCallType())))
                    /*.anchor(0.5f, 0.5f)*/.position(new LatLng(Double.parseDouble(snappedLatitude),
                                Double.parseDouble(snappedLongitude)))/*.flat(true).rotation(Float.parseFloat(mLocBearing))*/);
            }

            //THIS CHECK IS FOR ANIMATION WHEN ACTIVITY IS CREATED FIRST TIME AND MAP LOADED THEN
            // THIS ANIMATION SHOULD NOT RUN.
            if (animationStart) {
                // UPDATE CAMERA ROTATION ACCORDING TO THE MARKER DIRECTION.
                if (null != mCurrentLocation) {
                    if (StringUtils.isBlank(mLocBearing)) {
                        mLocBearing = "0.0";
                    }
                    updateCamera(mLocBearing);
                    // ANIMATE DRIVER MARKER TO THE TARGET LOCATION.
                    if (isLastAnimationComplete) {
                        if (Utils.calculateDistance(driverMarker.getPosition().latitude,
                                driverMarker.getPosition().longitude, mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()) >= 10) {
                            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                            animateMarker(latLng, mPreviousLocation.bearingTo(mCurrentLocation));
                        }
                    }
                }
            }
            animationStart = true;
        }
    }

    private void changeDriverMarker() {
        if (mGoogleMap == null) {
            return;
        }
        if (null != driverMarker) driverMarker.remove();
        driverMarker = mGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(
                Utils.getMapIcon(callData.getCallType())))
                .position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())));
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });
    }

    private void updateDropOffMarker(String latitude, String longitude) {
        if (null == mGoogleMap) return;
        if (null != dropOffMarker)
            dropOffMarker.setPosition(new LatLng(Double.parseDouble(latitude),
                    Double.parseDouble(longitude)));
        else
            dropOffMarker = mGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory
                    .fromResource(R.drawable.ic_destination_temp))
                    .position(new LatLng(Double.parseDouble(latitude),
                            Double.parseDouble(longitude))));
    }

    private void updatePickupMarker(String latitude, String longitude) {
        if (null == mGoogleMap) return;
        if (null != pickUpMarker) pickUpMarker.setPosition(new LatLng(Double.parseDouble(latitude),
                Double.parseDouble(longitude)));
        else {
            pickUpMarker = mGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_temp))
                    .position(new LatLng(Double.parseDouble(latitude),
                            Double.parseDouble(longitude))));
            /*
            * setting bounds for pickup marker*/
//            setPickupBounds();
        }
        /*if (!AppPreferences.getTripStatus(mCurrentActivity).equalsIgnoreCase(TripStatus.ON_START_TRIP)) {
            if (StringUtils.isNotBlank(callData.getPassLat()) && StringUtils.isNotBlank(callData.getPassLng())) {
                if (passCurrentLocMarker == null) {
                    passCurrentLocMarker = mGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pass_current_marker))
                            .position(new LatLng(Double.parseDouble(callData.getPassLat()),
                                    Double.parseDouble(callData.getPassLng()))));
                } else {
                    passCurrentLocMarker.setPosition(new LatLng(Double.parseDouble(callData.getPassLat()),
                            Double.parseDouble(callData.getPassLng())));
                }
            }
        } else if (passCurrentLocMarker != null) {
            passCurrentLocMarker.remove();
            passCurrentLocMarker = null;
        }*/
    }


    private boolean isLastAnimationComplete = true;

    private synchronized void animateMarker(final LatLng target, final float toRotation) {
        try {
            isLastAnimationComplete = false;
            final float currentRotationAngle = driverMarker.getRotation();
            final float duration = 4850;
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            Projection proj = mGoogleMap.getProjection();

            Point startPoint = proj.toScreenLocation(driverMarker.getPosition());
            final LatLng startLatLng = proj.fromScreenLocation(startPoint);
            final Interpolator interpolator = new LinearInterpolator();
            handler.post(new Runnable() {
                long elapsed;
                float t;
                float rot;

                @Override
                public void run() {
                    if (target == null || startLatLng == null) {
                        return;
                    }
                    elapsed = SystemClock.uptimeMillis() - start;
                    t = interpolator.getInterpolation((float) elapsed / duration);
                    double lng = t * target.longitude + (1 - t) * startLatLng.longitude;
                    double lat = t * target.latitude + (1 - t) * startLatLng.latitude;

                    rot = t * toRotation + (1 - t) * currentRotationAngle;
                    if (driverMarker == null)
                        return;

                    driverMarker.setPosition(new LatLng(lat, lng));

                    if (t < 1.0) {
                        // Post again 10ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        isLastAnimationComplete = true;
                        // animation ended
                    }
                }
            });
        } catch (NullPointerException ignored) {

        }
    }

    /************************************************************************************
     * WHEN DRIVER LOCATION UPDATE ROTATE MAP IN THE DIRECTION TO WHICH DRIVER IS MOVING SLOWLY..
     *************************************************************************************/
    public void updateCamera(final String bearing) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mGoogleMap != null && mGoogleMap.getCameraPosition().zoom == 16f) {
                            CameraPosition currentPlace = new CameraPosition.Builder()
                                    .target(new LatLng(mCurrentLocation.getLatitude(),
                                            mCurrentLocation.getLongitude()))
                                    /*.bearing(Float.parseFloat(bearing)).tilt(0f)*/
                                    .zoom(16f).build();
                            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace), 2000, changeMapRotation);
                            mpreLocBearing = bearing;
                        }

                    }
                });
            }
        }).start();
    }

    /************************************************************************************
     * CALLING DRIVER ON ROAD API.. IF THERE IS SOME ISSUE IN CALLING API OR RESPONSE
     * THEN USE THE CURRENT GPS LOCATION VALUES.
     ***********************************************************************************/
    public synchronized void getDriverRoadPosition(Context context, com.google.maps.model.LatLng normalLocation) {
//        List<com.google.maps.model.LatLng> snappedPoints = new ArrayList<>();
        if (normalLocation != null && normalLocation.lat != 0.0 && normalLocation.lng != 0.0) {
//            snappedPoints.add(normalLocation);

           /* if (Connectivity.isConnectedRealFast(context)) {
                RoadLocation roadLocation = new RoadLocation(mGeoContext, context, roadLocationListener);
                roadLocation.execute(snappedPoints);
            } else {*/
            onGetLocation(normalLocation.lat, normalLocation.lng);
//            }
        }
    }

    @Override
    public void onBackPressed() {

    }

    private void boundRoute(LatLng driver, LatLng source, LatLng destination) {
        if (null != mCurrentActivity && null != mGoogleMap) {
            LatLngBounds bounds;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(source);
            builder.include(destination);
            bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, Utils.dpToPx(mCurrentActivity, 64));

            mGoogleMap.moveCamera(cu);

        }
    }

    private void drawRoute(LatLng start, LatLng end, int routeType) {
        if (mRouteLatLng != null && mRouteLatLng.size() > 0) {
            LatLng currentLatLng = new LatLng(AppPreferences.getLatitude(), AppPreferences.getLongitude());
            if (PolyUtil.isLocationOnPath(currentLatLng, mRouteLatLng, false, 20)) {
                Utils.redLog("Route", "isSameRoute " + " -> true");
                for (int i = 0; i < mRouteLatLng.size(); i++) {
                    if (PolyUtil.isLocationOnPath(currentLatLng, mRouteLatLng.subList(0, i), false, 10)) {
                        int lastIndex = i > 1 ? i - 1 : 0;
                        mRouteLatLng.subList(0, lastIndex).clear();
                        if (lastPolyLineLatLng == null) {
                            lastPolyLineLatLng = currentLatLng;
                        }
                        //Our location is updating properly but we will consider last latlng as current location's marker is being animated after 5 sec
                        mRouteLatLng.add(0, lastPolyLineLatLng);
                        lastPolyLineLatLng = currentLatLng;
                        if (mapPolylines != null) {
                            mapPolylines.setPoints(mRouteLatLng);
                        }
                        double updatedDistance = Utils.calculateDistance(mRouteLatLng);
                        long timeDiff = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - AppPreferences.getEtaUpdatedTime());
                        double updatedTime = Double.parseDouble(AppPreferences.getEta()) - timeDiff;
                        int time = (int) Math.ceil(updatedTime);

                        if (updatedDistance < 0d) {
                            updatedDistance = 0d;
                        }
                        if (time < 1) {
                            time = 1;
                        }
                        Utils.redLog("Route", "updatedDistance -> " + updatedDistance + " KM");
                        Utils.redLog("Route", "updatedTime -> " + time + " min");
                        updateEtaAndCallData(time + "", updatedDistance + "");
                        break;
                    } else {
                        Utils.redLog("Route", "isLatLngCovered -> true");
                    }
                }
            } else {
                Utils.redLog("Route", "isSameRoute -> false");
            }
        }
        if (Connectivity.isConnected(mCurrentActivity)
                && (Utils.isDirectionApiCallRequired()) && mGoogleMap != null) {
            AppPreferences.setLastDirectionsApiCallTime(System.currentTimeMillis());
            if (isDirectionApiCallRequired(start)) {
                lastApiCallLatLng = start;
                if (mRouteLatLng != null && mRouteLatLng.size() > 0) {
                    mRouteLatLng.clear();
                }
                Routing.Builder builder = new Routing.Builder();
                if (StringUtils.isNotBlank(Utils.getApiKeyForDirections(mCurrentActivity))) {
                    builder.key(Utils.getApiKeyForDirections(mCurrentActivity));
                }
                builder.context(mCurrentActivity)
                        .waypoints(start, end)
                        .travelMode(AbstractRouting.TravelMode.DRIVING)
                        .withListener(this)
                        .routeType(routeType);
                Routing routing = builder.build();
                routing.execute();
            }
        }
    }

    private boolean isDirectionApiCallRequired(LatLng currentApiCallLatLng) {
        if (lastApiCallLatLng != null && (lastApiCallLatLng.equals(currentApiCallLatLng)
                || Utils.calculateDistance(currentApiCallLatLng.latitude, currentApiCallLatLng.longitude, lastApiCallLatLng.latitude, lastApiCallLatLng.longitude) < 15)) {
            return false;
        }
        return true;
    }

    private void showEstimatedDistTime() {
        if (AppPreferences.getTripStatus().equalsIgnoreCase(TripStatus.ON_START_TRIP)
                && endAddressTv.getText().toString().equalsIgnoreCase(getString(R.string.destination_not_selected_msg))) {
            updateEtaAndCallData(Utils.getTripTime(), Utils.getTripDistance());
        }
    }

    private void drawRouteToPickup() {
        if (StringUtils.isNotBlank(callData.getStartLat())
                && StringUtils.isNotBlank(callData.getStartLng())
                && StringUtils.isNotBlank(AppPreferences.getLatitude() + "")
                && StringUtils.isNotBlank(AppPreferences.getLongitude() + "")) {

            drawRoute(new LatLng(AppPreferences.getLatitude(),
                            AppPreferences.getLongitude()),
                    new LatLng(Double.parseDouble(callData.getStartLat()),
                            Double.parseDouble(callData.getStartLng())), Routing.pickupRoute);


        }
    }

    private void setPickupBounds() {
        /*int padding = 80;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(driverMarker.getPosition());
        builder.include(pickUpMarker.getPosition());
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mGoogleMap.moveCamera(cu);*/

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(getCurrentLatLngBounds(), 30);
        int padding = (int) mCurrentActivity.getResources().getDimension(R.dimen._60sdp);
        mGoogleMap.setPadding(padding, padding, padding, padding);
        mGoogleMap.moveCamera(cu);
//        mGoogleMap.setPadding(0, 0, 0, 0);
    }

    private LatLngBounds getCurrentLatLngBounds() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (pickUpMarker != null) {
            builder.include(pickUpMarker.getPosition());
        }
        builder.include(driverMarker.getPosition());


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

    private static final double EARTHRADIUS = 6366198;

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


    private void drawRouteToDropOff() {
        if (StringUtils.isNotBlank(callData.getStartLat())
                && StringUtils.isNotBlank(callData.getStartLng())
                && StringUtils.isNotBlank(callData.getEndLat())
                && StringUtils.isNotBlank(callData.getEndLng())) {

            drawRoute(new LatLng(AppPreferences.getLatitude(),
                            AppPreferences.getLongitude()),
                    new LatLng(Double.parseDouble(callData.getEndLat()),
                            Double.parseDouble(callData.getEndLng())), Routing.dropOffRoute);


        } else {
            if (mapPolylines != null) {
                mPolylineOptions = null;
                mapPolylines.remove();
            }
//            Dialogs.INSTANCE.showError(mCurrentActivity, jobBtn, "No destination available to draw route");
        }
    }

    private void drawRouteOnChange(LatLng startLatLng, LatLng endLatlng) {
        if (null != startLatLng && null != endLatlng) {
            drawRoute(startLatLng, endLatlng, Routing.onChangeRoute);
        } else {
            if (mapPolylines != null) {
                mPolylineOptions = null;
                mapPolylines.remove();
            }
        }
    }

    @Override
    public void onRoutingFailure(final int routeType, final RouteException e) {
        if (mCurrentActivity != null) {
            mCurrentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    lastApiCallLatLng = null;
                    Utils.redLog("onRoutingFailure", "" + e.getMessage());
                    AppPreferences.setDirectionsApiKeyRequired(true);
                    getDriverRoadPosition(mCurrentActivity,
                            new com.google.maps.model.LatLng(AppPreferences.getLatitude(),
                                    AppPreferences.getLongitude()));
                }
            });
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(final int routeType, final List<Route> route, int shortestRouteIndex) {
        if (mCurrentActivity != null && mGoogleMap != null) {
            mCurrentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRouteLatLng = route.get(0).getPoints();
                    updateEtaAndCallData((route.get(0).getDurationValue() / 60) + "",
                            Utils.formatDecimalPlaces((route.get(0).getDistanceValue() / 1000.0) + "", 1));

                    distanceToPickup = (route.get(0).getDistanceValue());


                    if (mapPolylines != null) {
                        mapPolylines.remove();
                    }
                    PolylineOptions polyOptions = new PolylineOptions();
                    polyOptions.color(ContextCompat.getColor(mCurrentActivity, R.color.blue));
                    polyOptions.width(Utils.dpToPx(mCurrentActivity, 5));
                    polyOptions.addAll(route.get(0).getPoints());
                    mapPolylines = mGoogleMap.addPolyline(polyOptions);


                    if (routeType == Routing.pickupRoute || routeType == Routing.dropOffRoute)

                    {
                        if (mCurrentActivity != null && mGoogleMap != null) {
                            int padding = 40; // offset from edges of the map in pixels
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(route.get(0).getLatLgnBounds(), padding);
                            mGoogleMap.moveCamera(cu);
                        }
                    }

                }
            });
        } else {
            lastApiCallLatLng = null;
        }

    }

    @Override
    public void onRoutingCancelled() {

    }


    private void onGetLocation(double latitude, double longitude) {
        if (null != mCurrentLocation && callData != null) {
            mPreviousLocation = mCurrentLocation;

            mCurrentLocation.setLatitude(latitude);
            mCurrentLocation.setLongitude(longitude);

            updateDriverMarker(mCurrentLocation.getLatitude() + "",
                    mCurrentLocation.getLongitude() + "");

            if (AppPreferences.getTripStatus().equalsIgnoreCase(TripStatus.ON_ARRIVED_TRIP)
                    || AppPreferences.getTripStatus().equalsIgnoreCase(TripStatus.ON_START_TRIP)) {
                if (StringUtils.isNotBlank(callData.getEndLat()) && StringUtils.isNotBlank(callData.getEndLng()))
                    drawRouteOnChange(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()),
                            new LatLng(Double.parseDouble(callData.getEndLat()), Double.parseDouble(callData.getEndLng())));
            } else {
                if (StringUtils.isNotBlank(callData.getStartLat()) && StringUtils.isNotBlank(callData.getStartLng()))
                    drawRouteOnChange(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()),
                            new LatLng(Double.parseDouble(callData.getStartLat()), Double.parseDouble(callData.getStartLng())));
            }

        } else {
            mCurrentLocation = new Location(LocationManager.GPS_PROVIDER);
            mCurrentLocation.setLatitude(latitude);
            mCurrentLocation.setLongitude(longitude);
            mPreviousLocation = mCurrentLocation;
        }

    }

    private RoadLocationListener roadLocationListener = new RoadLocationListener() {
        @Override
        public void onGetRoadLocation(double snappedLat, double snappedLng) {
            onGetLocation(snappedLat, snappedLng);
        }

        @Override
        public void onErrorRoadLocation(String msg) {
//            Dialogs.INSTANCE.showError(mCurrentActivity, jobBtn, msg);
        }
    };


    /******************************************************************************************
     * CALLBACK METHODS ARE IMPLEMENT HERE
     ******************************************************************************************/


    private GoogleMap.CancelableCallback changeMapRotation = new GoogleMap.CancelableCallback() {
        @Override
        public void onFinish() {
           /* LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            animateMarker(latLng, mPreviousLocation.bearingTo(mCurrentLocation));     // Tuesday 2-8-2016*/
        }

        @Override
        public void onCancel() {

        }
    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent && intent.getAction().equalsIgnoreCase(Keys.LOCATION_UPDATE_BROADCAST) && AppPreferences.isLoggedIn()) {
                /*UPDATING DRIVER CURRENT AND PREVIOUS LOCATION
                    FOR TRACKING AND UPDATING DRIVER MARKERS*/

                if (null == mPreviousLocation || null == mCurrentLocation) {
                    mCurrentLocation = intent.getParcelableExtra("location");
                    mPreviousLocation = mCurrentLocation;
                } else {
                    mPreviousLocation = mCurrentLocation;
                    mCurrentLocation = intent.getParcelableExtra("location");
                }

                if (null != mCurrentLocation) {
                    getDriverRoadPosition(mCurrentActivity,
                            new com.google.maps.model.LatLng(mCurrentLocation.getLatitude(),
                                    mCurrentLocation.getLongitude()));
                }


                mLocBearing = intent.getStringExtra("bearing");


                //THIS CHECK IS TO SHOW DROP OFF ICON WHEN DRIVER PRESS ARRIVED BUTTON
                if (AppPreferences.getTripStatus().equalsIgnoreCase(TripStatus.ON_START_TRIP)) {
                    if (StringUtils.isNotBlank(callData.getEndLat()) &&
                            StringUtils.isNotBlank(callData.getEndLng())) {
                        updatePickupMarker(callData.getEndLat(), callData.getEndLng());
                    } else if (pickUpMarker != null) {
                        pickUpMarker.remove();
                        pickUpMarker = null;
                    }
                } else {
                    updatePickupMarker(callData.getStartLat(), callData.getStartLng());
                }
                showEstimatedDistTime();
            }
        }
    };

    //METHOD CALLED WHEN THE WE PLOT ROUTE FROM CURRENT LOCATION TO DESTINATION. IN ROUTING SUCCESS CALLBACK METHOD.
    private void addTraceCircles(Location circleLocation) {

        if (null == mPreviousLocation || null == mGoogleMap) return;
        CircleOptions mStartTraceCirleOptions;
        CircleOptions mArriveTraceCirleOptions;
        if (AppPreferences.getTripStatus().equalsIgnoreCase(TripStatus.ON_ACCEPT_CALL)) {
            if (null != mStartTraceCircleList) mStartTraceCircleList.clear();
            mStartTraceCirleOptions = null;
            mArriveTraceCirleOptions = new CircleOptions();
            mArriveTraceCirleOptions.center(new LatLng(circleLocation.getLatitude(),
                    circleLocation.getLongitude()));
            mArriveTraceCirleOptions.fillColor(ContextCompat.getColor(mCurrentActivity, R.color.color_success));
            mArriveTraceCirleOptions.radius(6);
            mArriveTraceCirleOptions.strokeWidth(0);
            mArriveTraceCircleList.add(mGoogleMap.addCircle(mArriveTraceCirleOptions));

        } else if (AppPreferences.getTripStatus().equalsIgnoreCase(TripStatus.ON_START_TRIP)) {
            if (null != mArriveTraceCircleList) mArriveTraceCircleList.clear();
            mArriveTraceCirleOptions = null;
            mStartTraceCirleOptions = new CircleOptions();
            mStartTraceCirleOptions.center(new LatLng(circleLocation.getLatitude(),
                    circleLocation.getLongitude()));
            mStartTraceCirleOptions.fillColor(ContextCompat.getColor(mCurrentActivity, R.color.color_success));
            mStartTraceCirleOptions.radius(6);
            mStartTraceCirleOptions.strokeWidth(0);
            mStartTraceCircleList.add(mGoogleMap.addCircle(mStartTraceCirleOptions));
        }

    }



    /*private BroadcastReceiver cancelRideReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            if (mCurrentActivity != null && null != intent && null != intent.getExtras()) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (intent.getStringExtra("action").equalsIgnoreCase(Keys.BROADCAST_CANCEL_RIDE)) {
                            cancelByPassenger(false, "");
                        }
                        if (intent.getStringExtra("action").equalsIgnoreCase(Keys.BROADCAST_CANCEL_BY_ADMIN)) {
                            String message = intent.getStringExtra("msg");
                            cancelByPassenger(true, message);
                        }
                        if (intent.getStringExtra("action").equalsIgnoreCase(Keys.BROADCAST_COMPLETE_BY_ADMIN)) {
                            playNotificationSound();
                            onCompleteByAdmin(intent.getStringExtra("msg"));
                        }
                        if (intent.getStringExtra("action").equalsIgnoreCase(Keys.BROADCAST_DROP_OFF_UPDATED)) {
                            playNotificationSound();
                            Utils.appToast(mCurrentActivity, "Drop Off has been Updated by Passenger.");
                            callData = AppPreferences.getCallData();
                            updateDropOff();
                        }
                    }
                });

            }
        }
    };*/

    private void updateDropOffUI() {
        callData = AppPreferences.getCallData();
        if (StringUtils.isNotBlank(callData.getEndAddress())
                && StringUtils.isNotBlank(callData.getEndLat())
                && StringUtils.isNotBlank(callData.getEndLng())) {
            if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_START_TRIP)) {
                setStartedState();
                updateDropOff();
            }
        }

    }


    private void cancelByPassenger(boolean isCanceledByAdmin, String cancelMsg) {
        playNotificationSound();
        Utils.setCallIncomingState();
        AppPreferences.setTripStatus(TripStatus.ON_FREE);
        ActivityStackManager.getInstance().startHomeActivityFromCancelTrip(isCanceledByAdmin, cancelMsg, mCurrentActivity);
        mCurrentActivity.finish();
    }

    private void onCompleteByAdmin(String msg) {
        Utils.setCallIncomingState();
        Dialogs.INSTANCE.showAlertDialogNotSingleton(mCurrentActivity, new StringCallBack() {
            @Override
            public void onCallBack(String msg) {
                ActivityStackManager.getInstance().startHomeActivity(true, mCurrentActivity);
                mCurrentActivity.finish();
            }
        }, null, "Trip Completed", msg);
    }

    private UserDataHandler driversDataHandler = new UserDataHandler() {

        @Override
        public void onUpdateDropOff(final UpdateDropOffResponse data) {
            if (mCurrentActivity != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.INSTANCE.dismissDialog();
                        Utils.appToast(mCurrentActivity, data.getMessage());
                    }
                });
            }
        }

        @Override
        public void onArrived(final ArrivedResponse arrivedResponse) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Dialogs.INSTANCE.dismissDialog();
                    if (arrivedResponse.isSuccess()) {
                        callData = AppPreferences.getCallData();
                        callData.setStatus(TripStatus.ON_ARRIVED_TRIP);
                        AppPreferences.setCallData(callData);
                        endAddressTv.setVisibility(View.VISIBLE);
                        AppPreferences.setTripStatus(TripStatus.ON_ARRIVED_TRIP);
                        setOnArrivedData();
                        // CHANGING DRIVER MARKER FROM SINGLE DRIVER TO DRIVER AND PASSENGER MARKER...
                        changeDriverMarker();
                        updateEtaAndCallData("0", "0");
                    } else {
                        Dialogs.INSTANCE.showError(mCurrentActivity, jobBtn, arrivedResponse.getMessage());
                    }
                }
            });
        }

        @Override
        public void onCancelRide(final CancelRideResponse cancelRideResponse) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Dialogs.INSTANCE.dismissDialog();
                    if (cancelRideResponse.isSuccess()) {
                        try {
                            JSONObject data = new JSONObject();
                            data.put("DriverLocation", AppPreferences.getLatitude() + "," + AppPreferences.getLongitude());
                            data.put("timestamp", Utils.getIsoDate());
                            data.put("CancelBy", "Driver");
                            data.put("TripID", callData.getTripId());
                            data.put("TripNo", callData.getTripNo());
                            data.put("PassengerName", callData.getPassName());
                            data.put("PassengerID", callData.getPassId());
                            data.put("DriverID", AppPreferences.getPilotData().getId());
                            data.put("DriverName", AppPreferences.getPilotData().getFullName());
                            data.put("CancelBeforeAcceptance", "No");
                            data.put("CancelReason", cancelReason);
                            data.put("City", AppPreferences.getPilotData().getCity().getName());

                            Utils.logEvent(mCurrentActivity, callData.getPassId(), Constants.CANCEL_TRIP, data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Utils.appToast(mCurrentActivity, cancelRideResponse.getMessage());
                        Utils.setCallIncomingState();
                        AppPreferences.setWalletAmountIncreased(!cancelRideResponse.isAvailable());
                        AppPreferences.setAvailableStatus(cancelRideResponse.isAvailable());

                        /*dataRepository.requestLocationUpdate(mCurrentActivity);*/ // Required to reduce availability status delay
                        ActivityStackManager.getInstance().startHomeActivity(true, mCurrentActivity);
                        finish();
                    } else {
                        Dialogs.INSTANCE.showError(mCurrentActivity, jobBtn, cancelRideResponse.getMessage());
                    }
                }
            });
        }

        @Override
        public void onEndRide(final EndRideResponse endRideResponse) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Dialogs.INSTANCE.dismissDialog();
//                    jobBtn.setEnabled(true);
                    if (endRideResponse.isSuccess()) {
                        endAddressTv.setEnabled(false);
                        callData = AppPreferences.getCallData();
//                        endAddressTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_red_circle, 0, 0, 0);
                        callData.setStartAddress(endRideResponse.getData().getStartAddress());
                        callData.setEndAddress(endRideResponse.getData().getEndAddress());
                        callData.setTripNo(endRideResponse.getData().getTripNo());
                        callData.setTotalFare(endRideResponse.getData().getTotalFare());
                        callData.setTotalMins(endRideResponse.getData().getTotalMins());
                        callData.setTotalAmount(endRideResponse.getData().getTotalAmount());
                        callData.setDistanceCovered(endRideResponse.getData().getDistanceCovered());
                        if (StringUtils.isNotBlank(endRideResponse.getData().getWallet_deduction())) {
                            callData.setWallet_deduction(endRideResponse.getData().getWallet_deduction());
                        }
                        if (StringUtils.isNotBlank(endRideResponse.getData().getPromo_deduction())) {
                            callData.setPromo_deduction(endRideResponse.getData().getPromo_deduction());
                        }
                        callData.setStatus(TripStatus.ON_FINISH_TRIP);
                        callData.setTrip_charges(endRideResponse.getData().getTrip_charges());
                        AppPreferences.setCallData(callData);
                        tvEstimation.setVisibility(View.GONE);
                        AppPreferences.clearTripDistanceData();
                        AppPreferences.setTripStatus(TripStatus.ON_FINISH_TRIP);
                        ActivityStackManager.getInstance()
                                .startFeedbackActivity(mCurrentActivity);
                        mCurrentActivity.finish();
                    } else {
                        Dialogs.INSTANCE.showError(mCurrentActivity, jobBtn, endRideResponse.getMessage());

                    }
                }
            });
        }


        @Override
        public void onBeginRide(final BeginRideResponse beginRideResponse) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    jobBtn.setEnabled(true);
                    Dialogs.INSTANCE.dismissDialog();
                    if (beginRideResponse.isSuccess()) {
                        hideButtonOnArrived();
                        setStartedState();
                        callData = AppPreferences.getCallData();
                        callData.setStatus(TripStatus.ON_START_TRIP);
                        long startTripTime = System.currentTimeMillis();
                        AppPreferences.setStartTripTime(startTripTime);
                        AppPreferences.setPrevDistanceLatLng(Double.parseDouble(callData.getStartLat()),
                                Double.parseDouble(callData.getStartLng()), startTripTime);
                        AppPreferences.setCallData(callData);
                        AppPreferences.setTripStatus(TripStatus.ON_START_TRIP);
                        // CHANGING DRIVER MARKER FROM SINGLE DRIVER TO DRIVER AND PASSENGER MARKER...
                        changeDriverMarker();
                        showEstimatedDistTime();
                        updateDropOff();
                    } else {
                        Dialogs.INSTANCE.showError(mCurrentActivity, jobBtn, beginRideResponse.getMessage());
                    }
                }
            });
        }


        @Override
        public void onError(final int errorCode, final String error) {
//            isFinishCalled = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Dialogs.INSTANCE.dismissDialog();
                    jobBtn.setEnabled(true);
                    Dialogs.INSTANCE.showError(mCurrentActivity, jobBtn, error);
                }
            });
        }
    };

    private NetworkChangeListener networkChangeListener = new NetworkChangeListener() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("android.location.GPS_ENABLED_CHANGE") ||
                    intent.getAction().equalsIgnoreCase("android.location.PROVIDERS_CHANGED")) {
                checkGps();
            } else {
                if (Connectivity.isConnectedFast(context)) {
                    if (null != progressDialogJobActivity && !isFirstTime) {
                        progressDialogJobActivity.dismiss();
                        new UserRepository().requestRunningTrip(mCurrentActivity, handler);
                    } else {
                        isFirstTime = false;
                    }
                } else {
                    progressDialogJobActivity.show();
                }
            }
        }
    };

    private UserDataHandler handler = new UserDataHandler() {
        @Override
        public void onRunningTrips(final CheckDriverStatusResponse response) {
            if (mCurrentActivity != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (response.getMessage().equalsIgnoreCase("Trip Not Found")) {
                                cancelByPassenger(false, "");
                            } else {
                                if (shouldUpdateTripData(response.getData().getStatus())) {
                                    AppPreferences.setCallData(response.getData());
                                    AppPreferences.setTripStatus(response.getData().getStatus());
                                    callData = response.getData();
                                    updateDropOff();
                                }
                            }
                        } catch (NullPointerException ignored) {

                        }
                    }
                });
            }
        }
    };

    private boolean shouldUpdateTripData(String tripStatusRunningApi) {
        if (callData == null) {
            return true;
        } else if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_START_TRIP)
                && (tripStatusRunningApi.equalsIgnoreCase(TripStatus.ON_ARRIVED_TRIP)
                || tripStatusRunningApi.equalsIgnoreCase(TripStatus.ON_ACCEPT_CALL))) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onEvent(String action) {
        super.onEvent(action);
        if (Keys.ETA_IN_BG_UPDATED.equalsIgnoreCase(action)) {
            updateEtaAndCallData(AppPreferences.getEta(), AppPreferences.getEstimatedDistance());
        }
    }

    @Subscribe
    public void onEvent(final Intent intent) {
        if (mCurrentActivity != null && null != intent && null != intent.getExtras()) {
            mCurrentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (intent.getStringExtra("action").equalsIgnoreCase(Keys.BROADCAST_CANCEL_RIDE)) {
                        cancelByPassenger(false, "");
                    }
                    if (intent.getStringExtra("action").equalsIgnoreCase(Keys.BROADCAST_CANCEL_BY_ADMIN)) {
                        String message = intent.getStringExtra("msg");
                        cancelByPassenger(true, message);
                    }
                    if (intent.getStringExtra("action").equalsIgnoreCase(Keys.BROADCAST_COMPLETE_BY_ADMIN)) {
                        playNotificationSound();
                        onCompleteByAdmin(intent.getStringExtra("msg"));
                    }
                    if (intent.getStringExtra("action").equalsIgnoreCase(Keys.BROADCAST_DROP_OFF_UPDATED)) {
                        playNotificationSound();
                        Utils.appToast(mCurrentActivity, "Drop Off has been Updated by Passenger.");
                        callData = AppPreferences.getCallData();
                        updateDropOff();
                    }
                }
            });

        }
    }


    private void playNotificationSound() {
        if (AppPreferences.isCallingActivityOnForeground()) {
            MediaPlayer
                    .create(mCurrentActivity, R.raw.notification_sound)
                    .start();
        }
    }
}

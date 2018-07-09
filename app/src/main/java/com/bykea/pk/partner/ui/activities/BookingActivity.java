package com.bykea.pk.partner.ui.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bykea.pk.partner.Notifications;
import com.bykea.pk.partner.models.data.PlacesResult;
import com.bykea.pk.partner.models.response.CheckDriverStatusResponse;
import com.bykea.pk.partner.models.response.TopUpPassWalletResponse;
import com.bykea.pk.partner.models.response.UpdateDropOffResponse;
import com.bykea.pk.partner.ui.helpers.adapters.PlaceAutocompleteAdapter;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.widgets.AutoFitFontTextView;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BookingActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener,
        RoutingListener {

    @BindView(R.id.startAddressTv)
    AutoFitFontTextView startAddressTv;

    @BindView(R.id.tvTripId)
    FontTextView tvTripId;

    @BindView(R.id.tvCodAmount)
    AutoFitFontTextView tvCodAmount;

    @BindView(R.id.tvPWalletAmount)
    AutoFitFontTextView tvPWalletAmount;

    @BindView(R.id.llTopRight)
    RelativeLayout llTopRight;
    @BindView(R.id.callbtn)
    ImageView callbtn;
    @BindView(R.id.cancelBtn)
    FontTextView cancelBtn;
    @BindView(R.id.chatBtn)
    ImageView chatBtn;
        /*@BindView(R.id.callerIv)
        CircularImageView callerIv;*/
    @BindView(R.id.callerNameTv)
    FontTextView callerNameTv;
    @BindView(R.id.TimeTv)
    FontTextView timeTv;
    @BindView(R.id.distanceTv)
    FontTextView distanceTv;
    @BindView(R.id.endAddressTv)
    AutoFitFontTextView endAddressTv;
    @BindView(R.id.tvEstimation)
    FontTextView tvEstimation;
    @BindView(R.id.jobBtn)
    FontTextView jobBtn;
    @BindView(R.id.cvDirections)
    CardView cvDirections;

    @BindView(R.id.cvLocation)
    CardView cvLocation;
    @BindView(R.id.ivServiceIcon)
    ImageView ivServiceIcon;
    @BindView(R.id.tvCashWasooliLabel)
    FontTextView tvCashWasooliLabel;
    @BindView(R.id.ivTopUp)
    ImageView ivTopUp;

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

    private Marker driverMarker, dropOffMarker, pickUpMarker/*, passCurrentLocMarker*/;
    private Polyline mapPolylines;
    private List<LatLng> mRouteLatLng;


    //LOCATION CHANGE UPDATE DATA MEMBERS
    private Location mCurrentLocation;
    private Location mPreviousLocation;
    private String mLocBearing = "0.0";
    private boolean animationStart = false, isFirstTime = true, isResume = false;


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
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            Dialogs.INSTANCE.showLocationSettings(mCurrentActivity, Permissions.LOCATION_PERMISSION);

        EventBus.getDefault().post(Constants.Broadcast.UPDATE_FOREGROUND_NOTIFICATION);
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


    private GoogleMap.OnCameraIdleListener mCameraIdleListener = new GoogleMap.OnCameraIdleListener() {
        @Override
        public void onCameraIdle() {
            try {
                if (mGoogleMap == null) {
                    return;
                }
                if (Utils.calculateDistance(mGoogleMap.getCameraPosition().target.latitude, mGoogleMap.getCameraPosition().target.longitude,
                        AppPreferences.getLatitude(), AppPreferences.getLongitude()) < 500) {
                    cvLocation.setVisibility(View.INVISIBLE);
//                    cvLocation.setBackgroundColor(ContextCompat.getColor(mCurrentActivity, R.color.textColorSecondary));
                } else {
                    cvLocation.setVisibility(View.VISIBLE);
//                    cvLocation.setBackgroundColor(ContextCompat.getColor(mCurrentActivity, R.color.white));
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
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
                    mGoogleMap.setOnCameraIdleListener(mCameraIdleListener);
                    getDriverRoadPosition(mCurrentActivity,
                            new com.google.maps.model.LatLng(AppPreferences.getLatitude(),
                                    AppPreferences.getLongitude()));
                    if (callData != null) {
                        if (/*callData.getStatus().equalsIgnoreCase(TripStatus.ON_ARRIVED_TRIP)
                                ||*/ callData.getStatus().equalsIgnoreCase(TripStatus.ON_START_TRIP)) {
                            if (StringUtils.isNotBlank(callData.getEndLat()) && StringUtils.isNotBlank(callData.getEndLng())) {
                                updatePickupMarker(callData.getEndLat(), callData.getEndLng());
                            }
                            setPickupBounds();
                        } else {
                            if (StringUtils.isNotBlank(callData.getStartLat()) && StringUtils.isNotBlank(callData.getStartLng())) {
                                updatePickupMarker(callData.getStartLat(), callData.getStartLng());
                            }
                            setPickupBounds();
                        }
                    }
                }
            });
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 49)//CHECK FOR DROP OFF PLACE RESULT
//        {
//            if (StringUtils.isNotBlank(AppPreferences.getDropOffAddress())) {
//                callData.setEndLat(AppPreferences.getDropOffLat());
//                callData.setEndLng(AppPreferences.getDropOffLng());
//                callData.setEndAddress(AppPreferences.getDropOffAddress());
//                AppPreferences.setCallData(callData);
//                updateDropOffToServer();
//            }
//
//        }
        if (requestCode == Constants.CONFIRM_DROPOFF_REQUEST_CODE && data != null) {
            if (resultCode == RESULT_OK) {
                PlacesResult mDropOff = data.getParcelableExtra(Constants.CONFIRM_DROPOFF_ADDRESS_RESULT);
                callData.setEndLat("" + mDropOff.latitude);
                callData.setEndLng("" + mDropOff.longitude);
                callData.setEndAddress(mDropOff.address);
                AppPreferences.setCallData(callData);
                updateDropOffToServer();
            }
        } else if (requestCode == Permissions.LOCATION_PERMISSION) {
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
                updatePickupMarker(callData.getEndLat(), callData.getEndLng());
            }
        }
    }

    private void hideButtonOnArrived() {
//        callbtn.setVisibility(View.GONE);
//        chatBtn.setVisibility(View.GONE);
        cancelBtn.setVisibility(View.GONE);
    }


    @OnClick({R.id.callbtn, R.id.cancelBtn, R.id.chatBtn, R.id.jobBtn, R.id.cvLocation, R.id.cvDirections,
            R.id.endAddressTv, R.id.ivTopUp})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chatBtn:
                if (callData.isDispatcher() || "IOS".equalsIgnoreCase(callData.getCreator_type())) {
                    Utils.sendSms(mCurrentActivity, callData.getPhoneNo());
                } else {
                    ActivityStackManager.getInstance()
                            .startChatActivity(callData.getPassName(), "", true, mCurrentActivity);
                }
                break;
            case R.id.endAddressTv:
                Intent intent1 = new Intent(mCurrentActivity, SelectPlaceActivity.class);
                if (StringUtils.isNotBlank(callData.getEndLat()) &&
                        StringUtils.isNotBlank(callData.getEndLng()) &&
                        StringUtils.isNotBlank(callData.getEndAddress())) {
                    PlacesResult placesResult = new PlacesResult(callData.getEndAddress(), callData.getEndAddress(),
                            Double.parseDouble(callData.getEndLat()),
                            Double.parseDouble(callData.getEndLng()));
                    intent1.putExtra(Constants.Extras.SELECTED_ITEM, placesResult);
                }
                intent1.putExtra("from", Constants.CONFIRM_DROPOFF_REQUEST_CODE);
                startActivityForResult(intent1, Constants.CONFIRM_DROPOFF_REQUEST_CODE);
//                startActivityForResult(new Intent(mCurrentActivity, PlacesActivity.class), 49);
                break;
            case R.id.callbtn:
                if (StringUtils.isNotBlank(callData.getRec_no())) {
                    showCallPassengerDialog();
                } else {
                    Utils.callingIntent(mCurrentActivity, callData.getPhoneNo());
                }
                break;
            case R.id.cancelBtn:
                if (Utils.isCancelAfter5Min()) {
                    String msg = "پہنچنے کے " + AppPreferences.getSettings().getSettings().getCancel_time() + " منٹ کے اندر کینسل کرنے پر کینسیلیشن فی لگے گی";
                    Dialogs.INSTANCE.showAlertDialogWithTickCross(mCurrentActivity, new View.OnClickListener() {
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
                    }, "Cancel Trip", msg);
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
                            }, " پہنچ گئے؟");
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
                                logMixPanelEvent(TripStatus.ON_START_TRIP);
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Dialogs.INSTANCE.dismissDialog();
                            }
                        }, " اسٹارٹ؟");
                    } else if (jobBtn.getText().toString().equalsIgnoreCase(getString(R.string.button_text_finish))) {
                        Dialogs.INSTANCE.showRideStatusDialog(mCurrentActivity, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Dialogs.INSTANCE.dismissDialog();
                                Dialogs.INSTANCE.showLoader(mCurrentActivity);
                                logMixPanelEvent(TripStatus.ON_FINISH_TRIP);
                                dataRepository.requestEndRide(mCurrentActivity, driversDataHandler);
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Dialogs.INSTANCE.dismissDialog();
                            }
                        }, " مکمل؟");
                    }
                } else {
                    Dialogs.INSTANCE.showError(mCurrentActivity, jobBtn, getString(R.string.error_internet_connectivity));
                }

                break;
            case R.id.cvDirections:
                startGoogleDirectionsApp();
                break;
            case R.id.cvLocation:
                setDriverLocation();
                break;
            case R.id.ivTopUp:
                Dialogs.INSTANCE.showTopUpDialog(mCurrentActivity, Utils.isCourierService(callData.getCallType()), new StringCallBack() {
                    @Override
                    public void onCallBack(String msg) {
                        if (StringUtils.isNotBlank(msg)) {
                            Dialogs.INSTANCE.showLoader(mCurrentActivity);
                            dataRepository.topUpPassengerWallet(mCurrentActivity, callData, msg, new UserDataHandler() {
                                @Override
                                public void onTopUpPassWallet(final TopUpPassWalletResponse response) {
                                    if (mCurrentActivity != null) {
                                        mCurrentActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Dialogs.INSTANCE.dismissDialog();
                                                Utils.appToast(mCurrentActivity, response.getMessage());
                                                if (response.getData() != null) {
                                                    callData.setPassWallet(response.getData().getAmount());
                                                    AppPreferences.setCallData(callData);
                                                    tvPWalletAmount.setText("Rs. " + callData.getPassWallet());
                                                }
                                            }
                                        });
                                    }

                                }

                                @Override
                                public void onError(int errorCode, String errorMessage) {
                                    Dialogs.INSTANCE.dismissDialog();
                                    Utils.appToast(mCurrentActivity, errorMessage);
                                }
                            });
                        }
                    }
                });
                break;
        }
    }

    private void startGoogleDirectionsApp() {
        if (callData != null) {
            String start, end = StringUtils.EMPTY;
            if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_ACCEPT_CALL)) {
                start = Utils.getCurrentLocation();
                if (StringUtils.isNotBlank(callData.getStartLat()) && StringUtils.isNotBlank(callData.getStartLng())) {
                    end = callData.getStartLat() + "," + callData.getStartLng();
                }
            } else if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_START_TRIP)) {
                start = Utils.getCurrentLocation();
//                end = callData.getStartLat() + "," + callData.getStartLng();
                if (StringUtils.isNotBlank(callData.getEndLat()) && StringUtils.isNotBlank(callData.getEndLng())) {
                    end = callData.getEndLat() + "," + callData.getEndLng();
                }
            } else {
                start = Utils.getCurrentLocation();
                if (StringUtils.isNotBlank(callData.getEndLat()) && StringUtils.isNotBlank(callData.getEndLng())) {
//                    end = callData.getEndLat() + "," + callData.getEndLng();
                    end = callData.getStartLat() + "," + callData.getStartLng();
                }
            }

//            String uri = "http://maps.google.com/maps?saddr=" + start + "&daddr=" + end;
//            String uri = "https://www.google.com/maps/dir/?api=1&origin=" + start + "&destination=" + end + "&travelmode=driving";
            try {
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//                intent.setPackage("com.google.android.apps.maps");
//                Utils.redLog("Google Route Link ", uri);
//                startActivity(intent);

                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + end + "&mode=d");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            } catch (Exception ex) {
                Toast.makeText(mCurrentActivity, "Please install Google Maps", Toast.LENGTH_LONG).show();
            }

        }
    }

    private void showCallPassengerDialog() {
        Dialogs.INSTANCE.showCallPassengerDialog(mCurrentActivity, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.callingIntent(mCurrentActivity, callData.getPhoneNo());
                Utils.redLog("BookingActivity", "Call Sender");
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.callingIntent(mCurrentActivity, callData.getRec_no());
                Utils.redLog("BookingActivity", "Call Recipient");
            }
        });
    }
/*
    private void logMixPanelEvent(String status) {
        JSONObject properties = new JSONObject();
        try {
            properties.put("TripNo", callData.getTripNo());
            properties.put("PassengerID", callData.getPassId());
            properties.put("DriverID", AppPreferences.getPilotData().getId());
            properties.put("timestamp", Utils.getIsoDate());
            properties.put("SignUpCity", AppPreferences.getPilotData().getCity().getName());
            properties.put("PassengerName", callData.getPassName());
            properties.put("DriverName", AppPreferences.getPilotData().getFullName());
            properties.put("TripID", callData.getTripId());
            properties.put("type", callData.getCallType());
            if (StringUtils.isNotBlank(Utils.getCurrentLocation())) {
                properties.put("endDropOff", Utils.getCurrentLocation());
            }

            Utils.logEvent(mCurrentActivity, callData.getPassId(), Constants.AnalyticsEvents.RIDE_COMPLETE.replace("_R_", callData.getCallType()), properties);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }*/

    private void requestArrived() {
        Dialogs.INSTANCE.showLoader(mCurrentActivity);
        dataRepository.requestArrived(mCurrentActivity, driversDataHandler);
        logMixPanelEvent(TripStatus.ON_ARRIVED_TRIP);
    }


    private void logMixPanelEvent(String status) {
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
            data.put("ETA", AppPreferences.getEta());
            data.put("EstimatedDistance", AppPreferences.getEstimatedDistance());
            data.put("CurrentLocation", Utils.getCurrentLocation());
            data.put("PassengerName", callData.getPassName());
            data.put("DriverName", AppPreferences.getPilotData().getFullName());
            data.put("type", callData.getCallType());
            data.put("SignUpCity", AppPreferences.getPilotData().getCity().getName());


            if (TripStatus.ON_FINISH_TRIP.equalsIgnoreCase(status)) {
                Utils.logEvent(mCurrentActivity, callData.getPassId(), Constants.AnalyticsEvents.RIDE_COMPLETE.replace(
                        Constants.AnalyticsEvents.REPLACE, callData.getCallType()), data);
            } else if (TripStatus.ON_ARRIVED_TRIP.equalsIgnoreCase(status)) {
                Utils.logEvent(mCurrentActivity, callData.getPassId(), Constants.AnalyticsEvents.ON_ARRIVED.replace(
                        Constants.AnalyticsEvents.REPLACE, callData.getCallType()), data);
            } else if (TripStatus.ON_START_TRIP.equalsIgnoreCase(status)) {
                Utils.logEvent(mCurrentActivity, callData.getPassId(), Constants.AnalyticsEvents.ON_START.replace(
                        Constants.AnalyticsEvents.REPLACE, callData.getCallType()), data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        registerReceiver(locationReceiver, new IntentFilter(Keys.LOCATION_UPDATE_BROADCAST));
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        setInitialData();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.location.PROVIDERS_CHANGED");
        intentFilter.addCategory("android.intent.category.DEFAULT");
        registerReceiver(networkChangeListener, intentFilter);
        checkGps();
        checkConnectivity(mCurrentActivity);
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
//        Utils.flushMixPanelEvent(mCurrentActivity);
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
        ActivityStackManager.getInstance().restartLocationService(mCurrentActivity);
        mLocBearing = AppPreferences.getBearing() + "";
        mCurrentLocation = new Location("");
        mCurrentLocation.setLongitude(AppPreferences.getLongitude());
        mCurrentLocation.setLatitude(AppPreferences.getLatitude());
        mPreviousLocation = new Location("");

        callData = AppPreferences.getCallData();
        showWalletAmount();
        isResume = true;
        if (callData != null) {
            AppPreferences.setTripStatus(callData.getStatus());
//        setCallData();
            tvTripId.setText(callData.getTripNo());

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
            String icon = Utils.getServiceIcon(callData.getCallType());
            if (StringUtils.isNotBlank(icon)) {
                Utils.redLog(mCurrentActivity.getClass().getSimpleName(), Utils.getCloudinaryLink(icon));
                Picasso.get().load(Utils.getCloudinaryLink(icon))
                        .placeholder(Utils.getServiceIcon(callData))
                        .into(ivServiceIcon, new Callback() {
                            @Override
                            public void onSuccess() {
                                Utils.redLog(mCurrentActivity.getClass().getSimpleName(), "Icon OnSuccess");
                            }

                            @Override
                            public void onError(Exception e) {
                                Utils.redLog(mCurrentActivity.getClass().getSimpleName(), "Icon OnError");
                            }
                        });
            } else if (StringUtils.isNotBlank(callData.getCallType())) {
                ivServiceIcon.setImageDrawable(ContextCompat.getDrawable(mCurrentActivity, Utils.getServiceIcon(callData)));
            } else {
                ivServiceIcon.setImageDrawable(ContextCompat.getDrawable(mCurrentActivity, R.drawable.ride));
            }

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

        drawRouteToPickup();
    }

    private void setArrivedState() {
        jobBtn.setText(getString(R.string.button_text_start));
        showDropOff();
        startAddressTv.setVisibility(View.GONE);
        cvDirections.setVisibility(View.INVISIBLE);
        setOnArrivedData();
    }

    private void showDropOff() {
        if (Utils.isCourierService(callData.getCallType())) {
            endAddressTv.setVisibility(View.GONE);
        } else {
            endAddressTv.setVisibility(View.VISIBLE);
        }
    }

    private void setStartedState() {
        startAddressTv.setVisibility(View.GONE);
        showDropOff();
        cvDirections.setVisibility(View.VISIBLE);
        jobBtn.setText(getString(R.string.button_text_finish));
        setOnStartData();

    }

    private void showWalletAmount() {
        tvPWalletAmount.setText("Rs. " + callData.getPassWallet());
        if ((Utils.isDeliveryService(callData.getCallType()) || Utils.isCourierService(callData.getCallType()))
                && TripStatus.ON_ARRIVED_TRIP.equalsIgnoreCase(callData.getStatus())) {
            ivTopUp.setVisibility(View.VISIBLE);
        } else {
            ivTopUp.setVisibility(View.INVISIBLE);
        }
        if (StringUtils.isNotBlank(callData.getCodAmount())) {
            llTopRight.setVisibility(View.VISIBLE);
            tvCodAmount.setText("Rs. " + callData.getCodAmount());
            if (Utils.isPurchaseService(callData.getCallType())) {
                tvCashWasooliLabel.setText("خریداری کی رقم");
            }
        } else {
            llTopRight.setVisibility(View.INVISIBLE);
        }

        if (Utils.isDeliveryService(callData.getCallType())) {
            if (!callData.isCod()) {
                llTopRight.setVisibility(View.INVISIBLE);
            }
        }
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
            endAddressTv.setTextColor(ContextCompat.getColor(mCurrentActivity, R.color.textColorPrimary));
            startAddressTv.setText(callData.getStartAddress());
        } else {
            updateEtaAndCallData("0", "0");
            startAddressTv.setText(callData.getStartAddress());
        }

        jobBtn.setText(getString(R.string.button_text_start));
        cvDirections.setVisibility(View.INVISIBLE);
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
        }

        if (StringUtils.isBlank(callData.getEndAddress())) {
            endAddressTv.setText(getString(R.string.destination_not_selected_msg));
            endAddressTv.setTextColor(ContextCompat.getColor(mCurrentActivity, R.color.Color_Red));
        } else {
            endAddressTv.setText(callData.getEndAddress());
            endAddressTv.setTextColor(ContextCompat.getColor(mCurrentActivity, R.color.textColorPrimary));
        }
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
                            animateMarker(latLng);
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
        if (pickUpMarker != null) {
            pickUpMarker.remove();
        }
        if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_START_TRIP)) {
            pickUpMarker = mGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(
                    R.drawable.ic_drop_off_pin_red))
                    .position(new LatLng(Double.parseDouble(latitude),
                            Double.parseDouble(longitude))));
        } else {
            pickUpMarker = mGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(
                    R.drawable.ic_destination_temp))
                    .position(new LatLng(Double.parseDouble(latitude),
                            Double.parseDouble(longitude))));
        }
    }


    private boolean isLastAnimationComplete = true;

    private synchronized void animateMarker(final LatLng target) {
        try {
            isLastAnimationComplete = false;
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

                @Override
                public void run() {
                    if (target == null || startLatLng == null) {
                        return;
                    }
                    elapsed = SystemClock.uptimeMillis() - start;
                    t = interpolator.getInterpolation((float) elapsed / duration);
                    double lng = t * target.longitude + (1 - t) * startLatLng.longitude;
                    double lat = t * target.latitude + (1 - t) * startLatLng.latitude;

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
    private final int ANIMATE_SPEED_TURN = 1000;

    public void updateCamera(final String bearing) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        && mGoogleMap.getCameraPosition().zoom == 16f
                        if (mGoogleMap != null) {
                            CameraPosition currentPlace = new CameraPosition.Builder()
                                    .target(new LatLng(mCurrentLocation.getLatitude(),
                                            mCurrentLocation.getLongitude()))
                                    /*.bearing(Float.parseFloat(bearing)).tilt(0f)*/
                                    .zoom(16f).build();
//                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(getCurrentLatLngBounds(), 30);
//                            int padding = (int) mCurrentActivity.getResources().getDimension(R.dimen._40sdp);
//                            mGoogleMap.setPadding(padding, padding, padding, padding);
//                            mGoogleMap.animateCamera(cameraUpdate, ANIMATE_SPEED_TURN, changeMapRotation);
                            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace), 2000, changeMapRotation);
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

    private synchronized void drawRoute(LatLng start, LatLng end, int routeType) {
        if (mRouteLatLng != null && mRouteLatLng.size() > 0) {
            LatLng currentLatLng = new LatLng(AppPreferences.getLatitude(), AppPreferences.getLongitude());
            if (PolyUtil.isLocationOnPath(currentLatLng, mRouteLatLng, false, 20)) {
                Utils.redLog("Route", "isSameRoute " + " -> true");
                for (int i = 0; i < mRouteLatLng.size(); i++) {
                    if (PolyUtil.isLocationOnPath(currentLatLng, mRouteLatLng.subList(0, i), false, 20)) {
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
//                    } else {
//                        Utils.redLog("Route", "isLatLngCovered -> true");
//                        break;
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
        int padding = (int) mCurrentActivity.getResources().getDimension(R.dimen._50sdp);
        mGoogleMap.setPadding(0, padding, 0, padding);
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


    private synchronized void drawRouteToDropOff() {
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

            if (/*AppPreferences.getTripStatus().equalsIgnoreCase(TripStatus.ON_ARRIVED_TRIP)
                    ||*/ AppPreferences.getTripStatus().equalsIgnoreCase(TripStatus.ON_START_TRIP)) {
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
            if (null != intent && Keys.LOCATION_UPDATE_BROADCAST.equalsIgnoreCase(intent.getAction()) && AppPreferences.isLoggedIn()) {
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
                        showDropOff();
                        startAddressTv.setVisibility(View.GONE);
                        AppPreferences.setTripStatus(TripStatus.ON_ARRIVED_TRIP);
                        setOnArrivedData();
                        // CHANGING DRIVER MARKER FROM SINGLE DRIVER TO DRIVER AND PASSENGER MARKER...
                        changeDriverMarker();
                        updateEtaAndCallData("0", "0");
                    } else {
                        Dialogs.INSTANCE.showError(mCurrentActivity, jobBtn, arrivedResponse.getMessage());
                    }
                    EventBus.getDefault().post(Constants.Broadcast.UPDATE_FOREGROUND_NOTIFICATION);
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
                            data.put("SignUpCity", AppPreferences.getPilotData().getCity().getName());

                            Utils.logEvent(mCurrentActivity, callData.getPassId(), Constants.AnalyticsEvents.CANCEL_TRIP, data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Utils.appToast(mCurrentActivity, cancelRideResponse.getMessage());
                        Utils.setCallIncomingState();
                        AppPreferences.setWalletAmountIncreased(!cancelRideResponse.isAvailable());
                        AppPreferences.setAvailableStatus(cancelRideResponse.isAvailable());
                        dataRepository.requestLocationUpdate(mCurrentActivity, handler, AppPreferences.getLatitude(), AppPreferences.getLongitude());
                        ActivityStackManager.getInstance().startHomeActivity(true, mCurrentActivity);
                        finish();
                    } else {
                        Dialogs.INSTANCE.showError(mCurrentActivity, jobBtn, cancelRideResponse.getMessage());
                    }
                    EventBus.getDefault().post(Constants.Broadcast.UPDATE_FOREGROUND_NOTIFICATION);
                }
            });
        }

        @Override
        public void onEndRide(final EndRideResponse endRideResponse) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Dialogs.INSTANCE.dismissDialog();
                    logAnalyticsEvent(Constants.AnalyticsEvents.ON_RIDE_COMPLETE);
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
                        if (StringUtils.isNotBlank(endRideResponse.getData().getDropoff_discount())) {
                            callData.setDropoff_discount(endRideResponse.getData().getDropoff_discount());
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
                        callData = AppPreferences.getCallData();
                        callData.setStatus(TripStatus.ON_START_TRIP);
                        setStartedState();
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
                    EventBus.getDefault().post(Constants.Broadcast.UPDATE_FOREGROUND_NOTIFICATION);
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
                    Utils.appToast(mCurrentActivity, error);
//                    Dialogs.INSTANCE.showError(mCurrentActivity, jobBtn, error);
                }
            });
        }
    };

    private NetworkChangeListener networkChangeListener = new NetworkChangeListener() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("android.location.GPS_ENABLED_CHANGE".equalsIgnoreCase(intent.getAction()) ||
                    "android.location.PROVIDERS_CHANGED".equalsIgnoreCase(intent.getAction())) {
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
                    if (intent.getStringExtra("action").equalsIgnoreCase(Keys.TRIP_DATA_UPDATED)) {
                        playNotificationSound();
                        Utils.appToast(mCurrentActivity, "Trip Details has been Added by Passenger.");
                        callData = AppPreferences.getCallData();
                        updateDropOff();
                        showWalletAmount();
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

    private void logAnalyticsEvent(String event) {
        try {
            JSONObject data = new JSONObject();
            data.put("timestamp", System.currentTimeMillis());
            Utils.logFacebookEvent(mCurrentActivity, event, data);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

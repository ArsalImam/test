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
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bykea.pk.partner.Notifications;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.communication.socket.WebIORequestHandler;
import com.bykea.pk.partner.models.data.PlacesResult;
import com.bykea.pk.partner.models.response.ArrivedResponse;
import com.bykea.pk.partner.models.response.BeginRideResponse;
import com.bykea.pk.partner.models.response.CancelRideResponse;
import com.bykea.pk.partner.models.response.CheckDriverStatusResponse;
import com.bykea.pk.partner.models.response.EndRideResponse;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.models.response.TopUpPassWalletResponse;
import com.bykea.pk.partner.models.response.UpdateDropOffResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.tracking.AbstractRouting;
import com.bykea.pk.partner.tracking.Route;
import com.bykea.pk.partner.tracking.RouteException;
import com.bykea.pk.partner.tracking.Routing;
import com.bykea.pk.partner.tracking.RoutingListener;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.StringCallBack;
import com.bykea.pk.partner.ui.helpers.adapters.PlaceAutocompleteAdapter;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.MapUtil;
import com.bykea.pk.partner.utils.NetworkChangeListener;
import com.bykea.pk.partner.utils.Permissions;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.AutoFitFontTextView;
import com.bykea.pk.partner.widgets.FontTextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.PolyUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.Places;

public class BookingActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener,
        RoutingListener {

    @BindView(R.id.llStartAddress)
    LinearLayout llStartAddress;
    @BindView(R.id.startAddressTv)
    AutoFitFontTextView startAddressTv;
    @BindView(R.id.tvCountDown)
    TextView tvCountDown;

    @BindView(R.id.llEndAddress)
    LinearLayout llEndAddress;
    @BindView(R.id.endAddressTv)
    AutoFitFontTextView endAddressTv;
    @BindView(R.id.ivAddressEdit)
    AppCompatImageView ivAddressEdit;
    @BindView(R.id.tvCountDownEnd)
    FontTextView tvCountDownEnd;

    @BindView(R.id.tvTripId)
    FontTextView tvTripId;

    @BindView(R.id.tvCodAmount)
    AutoFitFontTextView tvCodAmount;

    @BindView(R.id.tvFareAmount)
    AutoFitFontTextView tvFareAmount;

    @BindView(R.id.tvPWalletAmount)
    AutoFitFontTextView tvPWalletAmount;

    @BindView(R.id.llTopMiddle)
    RelativeLayout llTopMiddle;
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
    @BindView(R.id.timeTv)
    FontTextView timeTv;
    @BindView(R.id.distanceTv)
    FontTextView distanceTv;
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
    @BindView(R.id.llDetails)
    LinearLayout llDetails;
    @BindView(R.id.tvDetailsNotEntered)
    FontTextView tvDetailsNotEntered;
    @BindView(R.id.tvCustomerName)
    FontTextView tvCustomerName;
    @BindView(R.id.tvCustomerPhone)
    FontTextView tvCustomerPhone;
    @BindView(R.id.tvDetailsAddress)
    FontTextView tvDetailsAddress;

    private String canceOption = "Didn't show up";

    //GOOGLE NEAR BY PLACE SEARCH VIEW
//    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView mAutocompleteView;
//    private Place place = null;

    public static boolean isJobActivityLive = false;
    //    private List<com.google.maps.model.LatLng> mCapturedLocations;


    private BookingActivity mCurrentActivity;
    private NormalCallData callData;
    private UserRepository dataRepository;
    private String cancelReason = StringUtils.EMPTY;

    private Marker driverMarker, dropOffMarker, pickUpMarker/*, passCurrentLocMarker*/;
    private Polyline mapPolylines, mapPolylinesSecondary;
    private List<LatLng> mRouteLatLng, mRouteLatLngSecondary;


    //LOCATION CHANGE UPDATE DATA MEMBERS
    private Location mCurrentLocation;
    private Location mPreviousLocation;
    private String mLocBearing = "0.0";
    private boolean animationStart = false, isFirstTime = true, isResume = false;


    private GoogleMap mGoogleMap;
    private MapView mapView;
    private ProgressDialog progressDialogJobActivity;
    private LatLng lastPolyLineLatLng, lastApiCallLatLng;

    private boolean lastPickUpFlagOnLeft, lastDropOffFlagOnLeft = false;
    boolean shouldRefreshPickupMarker = false, shouldRefreshDropOffMarker = false;
    CountDownTimer countDownTimer;

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
//        mGoogleApiClient = new GoogleApiClient.Builder(mCurrentActivity)
//                .enableAutoManage(mCurrentActivity, 0 /* clientId */, mCurrentActivity)
//                .addApi(Places.GEO_DATA_API)
//                .build();


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
                        updateMarkers();
                        drawRoutes();
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
                updateMarkers();
//                updatePickupMarker(callData.getEndLat(), callData.getEndLng());
            }
        }
    }

    private void hideButtonOnArrived() {
//        callbtn.setVisibility(View.GONE);
//        chatBtn.setVisibility(View.GONE);
        cancelBtn.setVisibility(View.GONE);
    }


    @OnClick({R.id.callbtn, R.id.cancelBtn, R.id.chatBtn, R.id.jobBtn, R.id.cvLocation, R.id.cvDirections,
            R.id.ivAddressEdit, R.id.ivTopUp, R.id.tvCustomerPhone})
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
            case R.id.ivAddressEdit:
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
            case R.id.tvCustomerPhone:
                if (StringUtils.isNotBlank(tvCustomerPhone.getText().toString())) {
                    Utils.callingIntent(mCurrentActivity, tvCustomerPhone.getText().toString());
                }
                break;
            case R.id.cancelBtn:
                if (Utils.isCancelAfter5Min(AppPreferences.getCallData().getSentTime())) {
                    String msg = "پہنچنے کے " + AppPreferences.getSettings()
                            .getSettings().getCancel_time() +
                            " منٹ کے اندر کینسل کرنے پر کینسیلیشن فی لگے گی";
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
                    configCountDown();
                } else {
                    Dialogs.INSTANCE.showError(mCurrentActivity, jobBtn, getString(R.string.error_internet_connectivity));
                }
                updateMarkers();
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
                //Utils.callingIntent(mCurrentActivity, callData.getPhoneNo());
                Utils.callingIntent(mCurrentActivity, getSenderNumber());
                Utils.redLog("BookingActivity", "Call Sender");
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Utils.callingIntent(mCurrentActivity, callData.getRec_no());
                Utils.callingIntent(mCurrentActivity, getRecipientNumber());
                Utils.redLog("BookingActivity", "Call Recipient");
            }
        });
    }


    /***
     * Validates ride type for Food delivery.
     * @return if service type is Food delivery return true, otherwise false.
     */
    private boolean isServiceTypeFoodDelivery() {
        if (callData != null) {
            return Constants.RIDE_TYPE_FOOD_DELIVERY.equalsIgnoreCase(callData.getCallType());
        }
        return false;
    }

    /***
     * Get Sender phone number according to Service type.
     * @return Phone number for Sender
     */
    private String getSenderNumber() {
        if (callData != null) {
            if (isServiceTypeFoodDelivery()) {
                if (TripStatus.ON_ACCEPT_CALL.equalsIgnoreCase(callData.getStatus())
                        || TripStatus.ON_ARRIVED_TRIP.equalsIgnoreCase(callData.getStatus())
                        || TripStatus.ON_START_TRIP.equalsIgnoreCase(callData.getStatus())) {
                    return callData.getRec_no();
                }
            } else {
                return callData.getPhoneNo();
            }
        }
        return StringUtils.EMPTY;
    }

    /***
     * Get Receiver phone number according to Service type.
     * @return Phone number for Receiver
     */
    private String getRecipientNumber() {
        if (callData != null) {
            if (isServiceTypeFoodDelivery()) {
                if (TripStatus.ON_ACCEPT_CALL.equalsIgnoreCase(callData.getStatus())
                        || TripStatus.ON_ARRIVED_TRIP.equalsIgnoreCase(callData.getStatus())) {
                    return callData.getRec_no();
                } else if (TripStatus.ON_START_TRIP.equalsIgnoreCase(callData.getStatus())) {
                    return callData.getPhoneNo();
                }
            } else {
                return callData.getRec_no();
            }
        }
        return StringUtils.EMPTY;
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
//        mGoogleApiClient.connect();
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
//        mGoogleApiClient.disconnect();

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
        ActivityStackManager.getInstance().startLocationService(mCurrentActivity);
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
            //String icon = Utils.getServiceIcon(callData.getCallType());
            String icon = StringUtils.EMPTY;
            //String icon = Utils.getServiceIcon(callData.getCallType());
            if (Utils.useServiceIconProvidedByAPI(callData.getCallType())) {
                icon = callData.getIcon();
            }
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
                ivServiceIcon.setImageDrawable(ContextCompat.getDrawable(mCurrentActivity, R.drawable.ride_right));
            }
            configCountDown();
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
    }

    private void setArrivedState() {
        jobBtn.setText(getString(R.string.button_text_start));
        showDropOffAddress();
        llStartAddress.setVisibility(View.GONE);
        cvDirections.setVisibility(View.INVISIBLE);
        setOnArrivedData();
    }

    /**
     * Hide and show Drop-off address bar if ride type is courier service.
     */
    private void showDropOffAddress() {
        if (Utils.isCourierService(callData.getCallType())) {
            llEndAddress.setVisibility(View.GONE);
        } else {
            llEndAddress.setVisibility(View.VISIBLE);
        }
    }

    private void setStartedState() {
        llStartAddress.setVisibility(View.GONE);
        showDropOffAddress();
        cvDirections.setVisibility(View.VISIBLE);
        jobBtn.setText(getString(R.string.button_text_finish));
        setOnStartData();

    }

    private void showWalletAmount() {
        tvPWalletAmount.setText("Rs." + callData.getPassWallet());
        if ((Utils.isDeliveryService(callData.getCallType()) || Utils.isCourierService(callData.getCallType()))
                && TripStatus.ON_ARRIVED_TRIP.equalsIgnoreCase(callData.getStatus())) {
            ivTopUp.setVisibility(View.VISIBLE);
        } else {
            ivTopUp.setVisibility(View.INVISIBLE);
        }
        if (StringUtils.isNotBlank(callData.getCodAmount())) {
            tvCodAmount.setText("Rs." + callData.getCodAmount());
            if (Utils.isPurchaseService(callData.getCallType())) {
                tvCashWasooliLabel.setText(R.string.kharidari_label);
            }
        } else {
            tvCodAmount.setText("-");
        }

        if (Utils.isDeliveryService(callData.getCallType())) {
            if (!callData.isCod()) {
                llTopMiddle.setVisibility(View.INVISIBLE);
            }
        }
        if (callData.getKraiKiKamai() != 0) {
            tvFareAmount.setText("Rs." + callData.getKraiKiKamai());
        } else {
            tvFareAmount.setText("-");
        }
    }

    private void showDropOffPersonInfo() {
        if (checkIfDetailsAdded()) {
            llDetails.setVisibility(View.VISIBLE);
            tvDetailsNotEntered.setVisibility(View.GONE);

            tvCustomerName.setText(callData.getRecName());
            tvCustomerPhone.setText(callData.getRec_no());
            if (StringUtils.isNotBlank(callData.getComplete_address())) {
                tvDetailsAddress.setVisibility(View.VISIBLE);
                tvDetailsAddress.setText(callData.getComplete_address());
            } else {
                tvDetailsAddress.setVisibility(View.GONE);
            }
        } else {
            tvDetailsNotEntered.setVisibility(View.VISIBLE);
            llDetails.setVisibility(View.GONE);
        }
    }

    private void setOnArrivedData() {
        showWalletAmount();
        showDropOffPersonInfo();
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
            endAddressTv.setTextColor(ContextCompat.getColor(mCurrentActivity, R.color.textColorPrimary676767));
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
        showDropOffPersonInfo();
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
            updateMarkers();
            if (pickUpMarker != null) {
                pickUpMarker.remove();
                pickUpMarker = null;
                mapPolylines.remove();
                mapPolylinesSecondary.remove();
            }
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
//                    updateCamera(mLocBearing);
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

    /**
     * Updates the trip's drop off marker
     */
    private synchronized void updateDropOffMarker() {

        boolean showOnLeft;
        double proposedDistance = 30;

        LatLng latLng = new LatLng(Double.valueOf(callData.getEndLat()), Double.valueOf(callData.getEndLng()));
        boolean isLeftAreaVisible = MapUtil.isVisibleOnMap(mGoogleMap, MapUtil.movePoint(latLng, proposedDistance, 270));

        if (!isLeftAreaVisible) {
            showOnLeft = false;
        } else {
            boolean isRightAreaVisible = MapUtil.isVisibleOnMap(mGoogleMap, MapUtil.movePoint(latLng, proposedDistance, 90));
            if (!isRightAreaVisible) {
                showOnLeft = true;
            } else {
                showOnLeft = isLeftAreaGreater(latLng);
            }
        }
        if (dropOffMarker != null) { // && StringUtils.isNotBlank(lastPickUpFlagOnLeft)) {
            if (!lastDropOffFlagOnLeft == showOnLeft || shouldRefreshDropOffMarker) { // || !lastEtaInFlag.equalsIgnoreCase(eta)) {
                shouldRefreshDropOffMarker = false;
                dropOffMarker.remove();
                dropOffMarker = mGoogleMap.addMarker(getDropOffMarker(latLng, showOnLeft));
            }
        } else {
            dropOffMarker = mGoogleMap.addMarker(getDropOffMarker(latLng, showOnLeft));
        }
    }

    /**
     * Updates the trip's pickup marker
     */
    private synchronized void updatePickupMarker() {

        boolean showOnLeft;
        double proposedDistance = 30;

        LatLng latLng = new LatLng(Double.valueOf(callData.getStartLat()), Double.valueOf(callData.getStartLng()));
        boolean isLeftAreaVisible = MapUtil.isVisibleOnMap(mGoogleMap, MapUtil.movePoint(latLng, proposedDistance, 270));

        if (!isLeftAreaVisible) {
            showOnLeft = false;
        } else {
            boolean isRightAreaVisible = MapUtil.isVisibleOnMap(mGoogleMap, MapUtil.movePoint(latLng, proposedDistance, 90));
            if (!isRightAreaVisible) {
                showOnLeft = true;
            } else {
                showOnLeft = isLeftAreaGreater(latLng);
            }
        }
        if (pickUpMarker != null) {
            if (!lastPickUpFlagOnLeft == showOnLeft || shouldRefreshPickupMarker) {
                shouldRefreshPickupMarker = false;
                pickUpMarker.remove();
                pickUpMarker = mGoogleMap.addMarker(getPickUpMarker(latLng, showOnLeft));
            }
        } else {
            pickUpMarker = mGoogleMap.addMarker(getPickUpMarker(latLng, showOnLeft));
        }

    }

    /**
     * This method creates Custom UI for drop off marker
     *
     * @param latLng     drop off point
     * @param showOnLeft true if we need to show Pick up flag on left side
     * @return MarkerOptions MarkerOptions to add marker
     */
    private MarkerOptions getDropOffMarker(LatLng latLng, boolean showOnLeft) {
        lastDropOffFlagOnLeft = showOnLeft;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        View mCustomMarkerView = MapUtil.getDropOffMarkerLayout(mCurrentActivity, showOnLeft);

        TextView tvDistance = mCustomMarkerView.findViewById(R.id.tvDistance);
        TextView tvDuration = mCustomMarkerView.findViewById(R.id.tvDuration);
        TextView tvRegionName = mCustomMarkerView.findViewById(R.id.tvRegionName);

        tvDistance.setText(String.valueOf(callData.getDropoffStop().getDistance()/1000));
        tvDuration.setText(String.valueOf(TimeUnit.SECONDS.toMinutes(callData.getDropoffStop().getDuration())));
        if (callData.getDropoffStop() != null
                && callData.getDropoffStop().getZoneNameUr() != null
                && !callData.getDropoffStop().getZoneNameUr().isEmpty())
            tvRegionName.setText(callData.getDropoffStop().getZoneNameUr());

        markerOptions.icon(MapUtil.getMarkerBitmapDescriptorFromView(mCustomMarkerView));
        return markerOptions;
    }

    /**
     * This method creates Custom UI for Pick up marker
     *
     * @param latLng     pick up point
     * @param showOnLeft true if we need to show Pick up flag on left side
     * @return MarkerOptions MarkerOptions to add marker
     */
    private MarkerOptions getPickUpMarker(LatLng latLng, boolean showOnLeft) {
        lastPickUpFlagOnLeft = showOnLeft;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        View mCustomMarkerView = MapUtil.getPickupMarkerLayout(mCurrentActivity, showOnLeft);

        TextView tvDistance = mCustomMarkerView.findViewById(R.id.tvDistance);
        TextView tvDuration = mCustomMarkerView.findViewById(R.id.tvDuration);
        TextView tvRegionName = mCustomMarkerView.findViewById(R.id.tvRegionName);

        tvDistance.setText(String.valueOf(callData.getPickupStop().getDistance()/1000));
        if (callData.getPickupStop().getDuration() != null)
            tvDuration.setText(String.valueOf(TimeUnit.SECONDS.toMinutes(callData.getPickupStop().getDuration())));
        if (callData.getPickupStop().getZoneNameUr() != null && !callData.getPickupStop().getZoneNameUr().isEmpty())
            tvRegionName.setText(callData.getPickupStop().getZoneNameUr());


        markerOptions.icon(MapUtil.getMarkerBitmapDescriptorFromView(mCustomMarkerView));
        return markerOptions;
    }

    /**
     * This method updates the trip pickup and drop off markers on basis of trip's current status
     */
    private synchronized void updateMarkers() {
        if (null == mGoogleMap || null == callData) return;

        if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_ACCEPT_CALL)) {
            if (callData.getStartLat() != null && !callData.getStartLat().isEmpty() && callData.getStartLng() != null && !callData.getStartLng().isEmpty())
                updatePickupMarker();
            if (callData.getEndLat() != null && !callData.getEndLat().isEmpty() && callData.getEndLng() != null && !callData.getEndLng().isEmpty())
                updateDropOffMarker();
        } else {
            if (pickUpMarker != null) pickUpMarker.remove();
            if (callData.getEndLat() != null && !callData.getEndLat().isEmpty() && callData.getEndLng() != null && !callData.getEndLng().isEmpty())
                updateDropOffMarker();
        }
        setPickupBounds();
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

    private synchronized void drawRoute(LatLng start, LatLng mid, LatLng end, int routeType) {
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
                if (mid != null)
                    builder.context(mCurrentActivity)
                            .waypoints(start, mid, end)
                            .travelMode(AbstractRouting.TravelMode.DRIVING)
                            .withListener(this)
                            .routeType(routeType);
                else
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

    /**
     * checks if right side has more area or left side has more area and displays marker on side
     * which has more area
     *
     * @param markerPosition Marker's current position/coordinate
     * @return true if left side of map has more area
     */
    private boolean isLeftAreaGreater(LatLng markerPosition) {
        boolean isLeftAreaGreater = false;
        int rightDistance = 31, leftDistance = 31;
        boolean TRUE = true, isRightDistanceCovered = false, isLeftDistanceCovered = false;
        double airDistance = 200;
        if (pickUpMarker != null && dropOffMarker != null) {
            airDistance = Utils.calculateDistance(pickUpMarker.getPosition().latitude, pickUpMarker.getPosition().longitude,
                    dropOffMarker.getPosition().latitude, dropOffMarker.getPosition().longitude);
        }
        int incrementFactor = MapUtil.getIncrementFactor(airDistance);
        while (TRUE) {
            if (MapUtil.isVisibleOnMap(mGoogleMap, MapUtil.movePoint(markerPosition, rightDistance, 90))) {
                rightDistance = rightDistance + incrementFactor;
                isRightDistanceCovered = false;
            } else {
                isRightDistanceCovered = true;
            }
            if (MapUtil.isVisibleOnMap(mGoogleMap, MapUtil.movePoint(markerPosition, leftDistance, 270))) {
                leftDistance = leftDistance + incrementFactor;
                isLeftDistanceCovered = false;
            } else {
                isLeftDistanceCovered = true;
            }

            if (isLeftDistanceCovered || isRightDistanceCovered) {
                isLeftAreaGreater = rightDistance <= leftDistance;
                TRUE = false;
            }
        }
        return isLeftAreaGreater;
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
        mGoogleMap.animateCamera(cu);
//        mGoogleMap.setPadding(0, 0, 0, 0);
    }

    private LatLngBounds getCurrentLatLngBounds() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (pickUpMarker != null) builder.include(pickUpMarker.getPosition());
        if (dropOffMarker != null) builder.include(dropOffMarker.getPosition());
        if (driverMarker != null) builder.include(driverMarker.getPosition());


        LatLngBounds tmpBounds = builder.build();
        /* Add 2 points 1000m northEast and southWest of the center.
         * They increase the bounds only, if they are not already larger
         * than this.
         * 1000m on the diagonal translates into about 709m to each direction. */
        LatLng center = tmpBounds.getCenter();
        LatLng northEast = MapUtil.move(center, 709, 709);
        LatLng southWest = MapUtil.move(center, -709, -709);

        builder.include(southWest);
        builder.include(northEast);
        return builder.build();
    }

    private void drawRoutes() {
        if (null == mGoogleMap || null == callData) return;

        if (StringUtils.isNotBlank(callData.getStartLat())
                && StringUtils.isNotBlank(callData.getStartLng())
                && StringUtils.isNotBlank(callData.getEndLat())
                && StringUtils.isNotBlank(callData.getEndLng())
                && StringUtils.isNotBlank(AppPreferences.getLatitude() + "")
                && StringUtils.isNotBlank(AppPreferences.getLongitude() + "")) {

            if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_ACCEPT_CALL)) {
                drawRoute(new LatLng(AppPreferences.getLatitude(), AppPreferences.getLongitude()),
                        new LatLng(Double.parseDouble(callData.getStartLat()), Double.parseDouble(callData.getStartLng())),
                        new LatLng(Double.parseDouble(callData.getEndLat()), Double.parseDouble(callData.getEndLng())),
                        Routing.pickupRoute);
            } else {
                drawRoute(new LatLng(AppPreferences.getLatitude(), AppPreferences.getLongitude()),
                        null,
                        new LatLng(Double.parseDouble(callData.getEndLat()), Double.parseDouble(callData.getEndLng())),
                        Routing.pickupRoute);
            }
        } else {
            if (mapPolylines != null) mapPolylines.remove();
            if (mapPolylinesSecondary != null) mapPolylinesSecondary.remove();
        }
    }

    private void drawRouteToPickup() {
        if (StringUtils.isNotBlank(callData.getStartLat())
                && StringUtils.isNotBlank(callData.getStartLng())
                && StringUtils.isNotBlank(AppPreferences.getLatitude() + "")
                && StringUtils.isNotBlank(AppPreferences.getLongitude() + "")) {

            drawRoute(
                    new LatLng(AppPreferences.getLatitude(), AppPreferences.getLongitude()),
                    null,
                    new LatLng(Double.parseDouble(callData.getStartLat()), Double.parseDouble(callData.getStartLng())),
                    Routing.pickupRoute
            );


        }
    }

    private synchronized void drawRouteToDropOff() {
        if (StringUtils.isNotBlank(callData.getStartLat())
                && StringUtils.isNotBlank(callData.getStartLng())
                && StringUtils.isNotBlank(callData.getEndLat())
                && StringUtils.isNotBlank(callData.getEndLng())) {

            drawRoute(
                    new LatLng(AppPreferences.getLatitude(), AppPreferences.getLongitude()),
                    null,
                    new LatLng(Double.parseDouble(callData.getEndLat()), Double.parseDouble(callData.getEndLng())),
                    Routing.dropOffRoute);


        } else {
            if (mapPolylines != null) {
                mapPolylines.remove();
            }
//            Dialogs.INSTANCE.showError(mCurrentActivity, jobBtn, "No destination available to draw route");
        }
    }

    private void drawRouteOnChange(LatLng startLatLng, LatLng endLatlng) {
        if (null != startLatLng && null != endLatlng) {
            drawRoute(startLatLng, null, endLatlng, Routing.onChangeRoute);
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
                    Route routeFirst = route.get(0);
                    mRouteLatLng = routeFirst.getPoints();
                    updateEtaAndCallData((routeFirst.getDurationValue() / 60) + "",
                            Utils.formatDecimalPlaces((routeFirst.getDistanceValue() / 1000.0) + "", 1));

                    if (mapPolylines != null) mapPolylines.remove();
                    if (mapPolylinesSecondary != null) mapPolylinesSecondary.remove();

                    if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_ACCEPT_CALL) && route.size() > 1) {
                        Route routeSecondary = route.get(1);

                        PolylineOptions polyOptions = new PolylineOptions();
                        polyOptions.width(Utils.dpToPx(mCurrentActivity, 5));
                        polyOptions.addAll(routeFirst.getPoints());
                        polyOptions.color(ContextCompat.getColor(mCurrentActivity, R.color.kelly_green));
                        mapPolylines = mGoogleMap.addPolyline(polyOptions);

                        polyOptions = new PolylineOptions();
                        polyOptions.width(Utils.dpToPx(mCurrentActivity, 5));
                        polyOptions.addAll(routeSecondary.getPoints());
                        polyOptions.color(ContextCompat.getColor(mCurrentActivity, R.color.blue));
                        mapPolylinesSecondary = mGoogleMap.addPolyline(polyOptions);

                        mRouteLatLngSecondary = routeSecondary.getPoints();
                        callData.getPickupStop().setDistance(routeFirst.getDistanceValue());
                        callData.getPickupStop().setDuration(routeFirst.getDurationValue());
                        callData.getDropoffStop().setDistance(routeSecondary.getDistanceValue());
                        callData.getDropoffStop().setDuration(routeSecondary.getDurationValue());

                    } else {
                        PolylineOptions polyOptions = new PolylineOptions();
                        polyOptions.width(Utils.dpToPx(mCurrentActivity, 5));
                        polyOptions.addAll(routeFirst.getPoints());
                        polyOptions.color(ContextCompat.getColor(mCurrentActivity, R.color.blue));
                        mapPolylines = mGoogleMap.addPolyline(polyOptions);

                        callData.getDropoffStop().setDistance(routeFirst.getDistanceValue());
                        callData.getDropoffStop().setDuration(routeFirst.getDurationValue());
                    }

                    shouldRefreshPickupMarker = true;
                    shouldRefreshDropOffMarker = true;
                    updateMarkers();

                    if (routeType == Routing.pickupRoute || routeType == Routing.dropOffRoute) {
                        if (mCurrentActivity != null && mGoogleMap != null) {
                            int padding = 40; // offset from edges of the map in pixels
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(routeFirst.getLatLgnBounds(), padding);
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

            if (isDirectionApiCallRequired(new LatLng(latitude, longitude))) {
                if (mapPolylines != null) {
                    mapPolylines.remove();
                }
                drawRoutes();
            }

            /*            if (*//*AppPreferences.getTripStatus().equalsIgnoreCase(TripStatus.ON_ARRIVED_TRIP)
                    ||*//* AppPreferences.getTripStatus().equalsIgnoreCase(TripStatus.ON_START_TRIP)) {
                if (StringUtils.isNotBlank(callData.getEndLat()) && StringUtils.isNotBlank(callData.getEndLng()))
                    drawRouteOnChange(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()),
                            new LatLng(Double.parseDouble(callData.getEndLat()), Double.parseDouble(callData.getEndLng())));
            } else {
                if (StringUtils.isNotBlank(callData.getStartLat()) && StringUtils.isNotBlank(callData.getStartLng()))
                    drawRouteOnChange(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()),
                            new LatLng(Double.parseDouble(callData.getStartLat()), Double.parseDouble(callData.getStartLng())));
            }*/

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
                        updateMarkers();
                    } else if (pickUpMarker != null) {
                        pickUpMarker.remove();
                        pickUpMarker = null;
                    }
                } else {
                    updateMarkers();
                }
                showEstimatedDistTime();
            }
        }
    };


    private void cancelByPassenger(boolean isCanceledByAdmin) {
        playNotificationSound();
        Utils.setCallIncomingState();
        AppPreferences.setTripStatus(TripStatus.ON_FREE);
        ActivityStackManager.getInstance().startHomeActivityFromCancelTrip(isCanceledByAdmin, mCurrentActivity);
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

        logAnalyticsEvent(Constants.AnalyticsEvents.ON_RIDE_COMPLETE);
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
                        showDropOffAddress();
                        llStartAddress.setVisibility(View.GONE);
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
//                    jobBtn.setEnabled(true);
                    if (endRideResponse.isSuccess()) {
                        logAnalyticsEvent(Constants.AnalyticsEvents.ON_RIDE_COMPLETE);
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
                            Gson gson = new Gson();
                            String trip = gson.toJson(response.getData().getTrip());
                            Type type = new TypeToken<NormalCallData>() {
                            }.getType();
                            NormalCallData normalCallData = gson.fromJson(trip, type);

                            if (shouldUpdateTripData(normalCallData.getStatus())) {
                                AppPreferences.setCallData(normalCallData);
                                AppPreferences.setTripStatus(normalCallData.getStatus());
                                callData = normalCallData;
                                updateDropOff();
                            }

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        @Override
        public void onError(int errorCode, String errorMessage) {
            switch (errorCode) {
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    EventBus.getDefault().post(Keys.UNAUTHORIZED_BROADCAST);
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    cancelByPassenger(false);
                    break;
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    EventBus.getDefault().post(Keys.MULTIDELIVERY_ERROR_BORADCAST);
                    break;
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
                        cancelByPassenger(false);
                    }
                    if (intent.getStringExtra("action").equalsIgnoreCase(Keys.BROADCAST_CANCEL_BY_ADMIN)) {
                        String message = intent.getStringExtra("msg");
                        cancelByPassenger(true);
                    }
                    if (intent.getStringExtra("action").equalsIgnoreCase(Keys.BROADCAST_COMPLETE_BY_ADMIN)) {
                        playNotificationSound();
                        onCompleteByAdmin(intent.getStringExtra("msg"));
                    }
                    if (intent.getStringExtra("action").equalsIgnoreCase(Keys.BROADCAST_DROP_OFF_UPDATED)) {
                        playNotificationSound();
                        Utils.appToast(mCurrentActivity, "Drop Off has been Updated by Passenger.");
//                        callData = AppPreferences.getCallData();
                        new UserRepository().requestRunningTrip(mCurrentActivity, handler);
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


    private boolean checkIfDetailsAdded() {
        boolean isAdded = true;
        /*if (StringUtils.isBlank(callData.getRecName())) {
            isAdded = false;
        } else */
        if (StringUtils.isBlank(callData.getRec_no())) {
            isAdded = false;
        } else if (StringUtils.isBlank(callData.getCodAmount())) {
            isAdded = false;
        }
        return isAdded;
    }

    /**
     * Sets up Count Down Timer on basis of current ride status
     */
    private void configCountDown() {
        if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_ACCEPT_CALL) && callData.getPickupStop() != null) {
            int pickDuration = callData.getPickupStop().getDuration();
            long eta = AppPreferences.getTripAcceptTime() + TimeUnit.SECONDS.toMillis(pickDuration);
            tvCountDown.setVisibility(View.VISIBLE);
            startCountDown(eta);
        } else if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_START_TRIP) && callData.getDropoffStop() != null) {
            int dropDuration = callData.getDropoffStop().getDuration();
            long eta = AppPreferences.getStartTripTime() + TimeUnit.SECONDS.toMillis(dropDuration);
            tvCountDownEnd.setVisibility(View.VISIBLE);
            startCountDown(eta);
        }

//        else {
//            tvCountDown.setVisibility(View.GONE);
//            tvCountDownEnd.setVisibility(View.GONE);
//        }
    }

    /**
     * Starts count down timer for ETA
     *
     * @param etaInMillis ETA to initiate count down timer with
     */
    private void startCountDown(Long etaInMillis) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        long remainingMillis = etaInMillis - System.currentTimeMillis();
        countDownTimer = new CountDownTimer(remainingMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                try {
                    String time = Utils.formatTimeForTimer(millisUntilFinished);
                    if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_ACCEPT_CALL)) {
                        tvCountDown.setText(time);
                    } else if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_START_TRIP)) {
                        tvCountDownEnd.setText(time);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            public void onFinish() {
                tvCountDown.setText(R.string.clock_zero);
                this.cancel();
            }
        };
        countDownTimer.start();
    }
}

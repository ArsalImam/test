package com.bykea.pk.partner.ui.fragments;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bykea.pk.partner.communication.socket.WebIORequestHandler;
import com.bykea.pk.partner.models.data.PilotData;
import com.bykea.pk.partner.models.data.PlacesResult;
import com.bykea.pk.partner.models.response.CheckDriverStatusResponse;
import com.bykea.pk.partner.models.response.DriverDestResponse;
import com.bykea.pk.partner.models.response.DriverStatsResponse;
import com.bykea.pk.partner.ui.activities.ConfirmDropOffAddressActivity;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.bykea.pk.partner.ui.helpers.IViewTouchEvents;
import com.bykea.pk.partner.ui.helpers.StringCallBack;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.widgets.AutoFitFontTextView;
import com.bykea.pk.partner.widgets.MyRangeBar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.bykea.pk.partner.Notifications;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.HeatmapLatlng;
import com.bykea.pk.partner.models.response.HeatMapResponse;
import com.bykea.pk.partner.models.response.PilotStatusResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.Permissions;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    @Bind(R.id.rlInactiveImage)
    LinearLayout rlInactiveImage;
    @Bind(R.id.mapPinIv)
    ImageView mapPinIv;
    @Bind(R.id.statusTv)
    FontTextView statusTv;
    @Bind(R.id.tvConnectionStatus)
    FontTextView tvConnectionStatus;
    @Bind(R.id.tvFenceError)
    AutoFitFontTextView tvFenceError;
    @Bind(R.id.statusCheck)
    ImageView statusCheck;
    @Bind(R.id.myRangeBar)
    MyRangeBar myRangeBar;
    @Bind(R.id.tvCihIndex1)
    FontTextView tvCihIndex1;
    @Bind(R.id.tvCihIndex2)
    FontTextView tvCihIndex2;
    @Bind(R.id.tvCihIndex3)
    FontTextView tvCihIndex3;
    @Bind(R.id.tvCihIndex4)
    FontTextView tvCihIndex4;
    @Bind(R.id.tvCihIndex5)
    FontTextView tvCihIndex5;
    @Bind(R.id.rl_setDestination)
    RelativeLayout rl_setDestination;
    @Bind(R.id.rl_destinationSelected)
    RelativeLayout rl_destinationSelected;
    @Bind(R.id.tv_destinationName)
    FontTextView tv_destinationName;

    private UserRepository repository;
    private HomeActivity mCurrentActivity;
    private GoogleMap mGoogleMap;
    private MapView mapView;
    private String mGoogleDesLatLng = "";
    private TileOverlay mHeatmapOverlay;
    //    private EventBus mEventBus = EventBus.getDefault();
    private Location mPrevLocToShow;
    private String currentVersion, latestVersion;
    private boolean isScreenInFront;
    private int[] cashInHand;
    private PlacesResult mDropOff;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        mCurrentActivity = ((HomeActivity) getActivity());
        mCurrentActivity.hideToolbarTitle();
        mCurrentActivity.setToolbarLogo();
        mCurrentActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        mEventBus.register(this);
        repository = new UserRepository();
        mapView = (MapView) view.findViewById(R.id.homeMapFragment);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        try {
            MapsInitializer.initialize(mCurrentActivity.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(mapReadyCallback);
        Utils.checkGooglePlayServicesVersion(mCurrentActivity);
        initRangeBar();
    }

    public void initRangeBar() {
        myRangeBar.init(mRangeBarTouch);
        int currentIndex = 0;
        cashInHand = AppPreferences.getCashInHandsRange();
        tvCihIndex1.setText(Utils.getFormattedNumber(cashInHand[0]));
        tvCihIndex2.setText(Utils.getFormattedNumber(cashInHand[1]));
        tvCihIndex3.setText(Utils.getFormattedNumber(cashInHand[2]));
        tvCihIndex4.setText(Utils.getFormattedNumber(cashInHand[3]));
        tvCihIndex5.setText(Utils.getFormattedNumber(cashInHand[4]));
        int length = cashInHand.length;
        int value = AppPreferences.getCashInHands();
        for (int i = 0; i < length; i++) {
            if (cashInHand[i] == value) {
                currentIndex = i;
                break;
            }
        }
        myRangeBar.refreshDrawableState();
        myRangeBar.invalidate();
        myRangeBar.setCurrentIndex(currentIndex);
        myRangeBar.setOnSlideListener(new MyRangeBar.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                Utils.redLog("Cash In Hand", "" + cashInHand[index]);
                AppPreferences.setCashInHands(cashInHand[index]);
            }
        });
    }

    private IViewTouchEvents mRangeBarTouch = new IViewTouchEvents() {
        @Override
        public void onTouchDown() {
            statusCheck.setClickable(false);
        }

        @Override
        public void onTouchUp() {
            statusCheck.setClickable(true);
        }
    };

    private void initViews() {
        mCurrentActivity.setPilotData(AppPreferences.getPilotData());
        if (Utils.isLicenceExpired(mCurrentActivity.getPilotData().getLicenseExpiry())) {
            onUnauthorizedLicenceExpire();
        }
        setStatusBtn();
        setConnectionStatus();
        myRangeBar.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                myRangeBar.getViewTreeObserver().removeOnPreDrawListener(this);
                myRangeBar.updateUI();
                return true;
            }
        });
    }

    /*
    * Update Connection Status according to Signal Strength
    * */
    private void setConnectionStatus() {
        String connectionStatus = Connectivity.getConnectionStatus(mCurrentActivity);
        tvConnectionStatus.setText(connectionStatus);
        tvConnectionStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable._good_sattelite, 0, 0, 0);
        if (connectionStatus.equalsIgnoreCase("Unknown Status")) {
            tvConnectionStatus.setBackgroundColor(ContextCompat.getColor(mCurrentActivity, R.color.textColorSecondary));
        } else if (connectionStatus.equalsIgnoreCase("Battery Low")) {
            tvConnectionStatus.setBackgroundColor(ContextCompat.getColor(mCurrentActivity, R.color.color_error));
            tvConnectionStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.low_battery_icon, 0, 0, 0);
        } else if (connectionStatus.equalsIgnoreCase("Poor Connection") ||
                connectionStatus.equalsIgnoreCase("Fair Connection") || connectionStatus.equalsIgnoreCase("No Connection")) {
            tvConnectionStatus.setBackgroundColor(ContextCompat.getColor(mCurrentActivity, R.color.color_fair_connection));

        } else if (connectionStatus.equalsIgnoreCase("Good Connection")) {
            tvConnectionStatus.setBackgroundColor(ContextCompat.getColor(mCurrentActivity, R.color.colorPrimary));
        }

    }

    private long getHeatMapTimer() {
        long timer = 5;
        if (AppPreferences.getSettings() != null
                && AppPreferences.getSettings().getSettings() != null
                && StringUtils.isNotBlank(AppPreferences.getSettings().getSettings().getHeatmap_refresh_timer())) {
            timer = (long) Math.ceil(Double.parseDouble(AppPreferences.getSettings().getSettings().getHeatmap_refresh_timer()));
        }
        return timer * 60000;
    }

    private CountDownTimer countDownTimer = new CountDownTimer(getHeatMapTimer(), getHeatMapTimer()) {
        @Override
        public void onTick(long millisUntilFinished) {}

        @Override
        public void onFinish() {
            setConnectionStatus();
            if (Connectivity.isConnectedFast(mCurrentActivity) && AppPreferences.getAvailableStatus())
                repository.requestHeatMapData(mCurrentActivity, handler);
            countDownTimer.start();
        }
    };

    private OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            //check if fragment is replaced before map is ready
            if (mCurrentActivity == null || getView() == null) {
                return;
            }
            mGoogleMap = googleMap;
            Utils.formatMap(mGoogleMap);
            mGoogleMap.clear();
            if (mCurrentActivity != null && !Permissions.hasLocationPermissions(mCurrentActivity)) {
                Permissions.getLocationPermissions(HomeFragment.this);
            } else {
                if (ActivityCompat.checkSelfPermission(mCurrentActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(mCurrentActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mGoogleMap.setMyLocationEnabled(true);
            }
            if (AppPreferences.getLatitude() != 0.0 &&
                    AppPreferences.getLongitude() != 0.0)
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(AppPreferences.getLatitude()
                                , AppPreferences.getLongitude())
                        , 12.0f));

            if (mCurrentActivity != null && null != mCurrentActivity.getIntent() && null != mCurrentActivity.getIntent().getExtras() &&
                    mCurrentActivity.getIntent().getBooleanExtra("isCancelledTrip", false) && !Dialogs.INSTANCE.isShowing()) {
                if (!mCurrentActivity.isDialogShown() && getView() != null) {
                    mCurrentActivity.setDialogShown(true);
                    if (mCurrentActivity.getIntent().getBooleanExtra("isCanceledByAdmin", false)) {
                        Dialogs.INSTANCE.showAlertDialogNotSingleton(mCurrentActivity,
                                new StringCallBack() {
                                    @Override
                                    public void onCallBack(String msg) {

                                    }
                                }, null, "Booking Cancelled", "Admin has cancelled the Booking");
                    } else {
                        Dialogs.INSTANCE.showAlertDialogNotSingleton(mCurrentActivity,
                                new StringCallBack() {
                                    @Override
                                    public void onCallBack(String msg) {

                                    }
                                }, null, "Booking Cancelled", "Passenger has cancelled the Trip");
                    }
                }
            }

        }


    };


    @Override
    public void onResume() {
        mapView.onResume();
        isScreenInFront = true;
        Notifications.removeAllNotifications(mCurrentActivity);
        countDownTimer.start();
        if (Connectivity.isConnectedFast(mCurrentActivity) && AppPreferences.getAvailableStatus())
            repository.requestHeatMapData(mCurrentActivity, handler);

        Utils.setCallIncomingState();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Keys.LOCATION_UPDATE_BROADCAST);
        mCurrentActivity.registerReceiver(myReceiver, intentFilter);
        if (AppPreferences.isLoggedIn()) {

            initViews();
            if (Utils.isStatsApiCallRequired()) {
                repository.requestDriverStats(mCurrentActivity, handler);
            }
        }
        repository.requestRunningTrip(mCurrentActivity, handler);
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        isScreenInFront = false;
        mCurrentActivity.unregisterReceiver(myReceiver);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onStop() {
        isScreenInFront = false;
        super.onStop();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @OnClick({R.id.mapPinIv, R.id.statusCheck, R.id.rlInactiveImage, R.id.tvNotice, R.id.tvDemand, R.id.rl_setDestination, R.id.rl_destinationSelected})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_destinationSelected:
            case R.id.rl_setDestination:
                setHomeLocation();
                break;
            case R.id.rlInactiveImage:
                break;
            case R.id.mapPinIv:
                setDriverLocation();
                break;
            case R.id.tvDemand:
                Utils.startCustomWebViewActivity(mCurrentActivity, AppPreferences.getSettings().getSettings().getDemand(), "Demand");
                break;
            case R.id.tvNotice:                             //AppPreferences.getSettings(mCurrentActivity).getSettings().getNotice()
                Utils.startCustomWebViewActivity(mCurrentActivity, AppPreferences.getSettings().getSettings().getNotice(), "Notice");
                break;
            case R.id.statusCheck:
                if (Connectivity.isConnectedFast(mCurrentActivity)) {
                    if (AppPreferences.getAvailableStatus()) {
                        Dialogs.INSTANCE.showInactiveConfirmationDialog(mCurrentActivity, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Dialogs.INSTANCE.dismissDialog();
                                if (Connectivity.isConnectedFast(mCurrentActivity)) {
                                    Dialogs.INSTANCE.showLoader(mCurrentActivity);
                                    AppPreferences.setAvailableStatus(false);
                                    repository.requestUpdateStatus(mCurrentActivity, handler, false);
                                }
                            }
                        });
                    } else {
                        if (Connectivity.isConnectedFast(mCurrentActivity)) {
                            Dialogs.INSTANCE.showLoader(mCurrentActivity);
                            AppPreferences.setAvailableStatus(true);
                            repository.requestUpdateStatus(mCurrentActivity, handler, true);
                        }
                    }
                } else {
                    Dialogs.INSTANCE.showError(mCurrentActivity
                            , mapPinIv, getString(R.string.error_internet_connectivity));
                }
                break;
        }
    }

    private void setHomeLocation() {
        Intent returndropoffIntent = new Intent(mCurrentActivity, ConfirmDropOffAddressActivity.class);
        returndropoffIntent.putExtra("from", Constants.CONFIRM_DROPOFF_REQUEST_CODE);
        returndropoffIntent.putExtra(Constants.TOOLBAR_TITLE, "Confirm Destination");
        returndropoffIntent.putExtra(Constants.SEARCHBOX_TITLE, "Search Destination");
        startActivityForResult(returndropoffIntent, Constants.CONFIRM_DROPOFF_REQUEST_CODE);
//        ActivityStackManager.getInstance(mCurrentActivity).startConfirmDestActivity(mCurrentActivity, "Confirm Destination", "Search Destination");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mCurrentActivity != null) {
            if (requestCode == Constants.CONFIRM_DROPOFF_REQUEST_CODE && data != null) {
                if (resultCode == RESULT_OK) {
                    mDropOff = data.getParcelableExtra(Constants.CONFIRM_DROPOFF_ADDRESS_RESULT);
                    tv_destinationName.setText("Loading...");
                    rl_destinationSelected.setVisibility(View.VISIBLE);
                    repository.requestDriverDropOff(mCurrentActivity
                            , handler
                            , String.valueOf(mDropOff.latitude)
                            , String.valueOf(mDropOff.longitude)
                            , mDropOff.address);
                    //TODO Post Address to Server
                }
            }
        }
    }

    private void setDriverLocation() {
        if (null != mGoogleMap) {
            Utils.formatMap(mGoogleMap);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(AppPreferences.getLatitude()
                            , AppPreferences.getLongitude())
                    , 12.0f));
        }
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            if (mCurrentActivity != null && getView() != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (null != intent && intent.getAction().equalsIgnoreCase(Keys.LOCATION_UPDATE_BROADCAST)) {

                            Utils.redLog("LOCATION BROADCAST", "RECEIVED ==========================================================");
                            if (null != intent.getExtras() && null != intent.getStringExtra("offline_location")
                                    && intent.getStringExtra("offline_location").equalsIgnoreCase(Keys.LOCATION_NOT_UPDATE_BROADCAST)) {
//                    showOfflineDialog();
                            } else {
                                Location location = intent.getParcelableExtra("location");
                                //Move Map's Camera if there's significant change in Location
                                if (mPrevLocToShow == null || location.distanceTo(mPrevLocToShow) > 30) {
                                    mPrevLocToShow = location;
                                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude()
                                                    , location.getLongitude())
                                            , 12.0f));
                                }
                            }
                        }
                    }
                });
            }
        }
    };

    private void onUnauthorized() {
        AppPreferences.saveLoginStatus(false);
        AppPreferences.setIncomingCall(false);
        AppPreferences.setCallData(null);
        AppPreferences.setTripStatus("");
        AppPreferences.saveLoginStatus(false);
        AppPreferences.setPilotData(null);
        HomeActivity.visibleFragmentNumber = 0;
        Dialogs.INSTANCE.showAlertDialogNotSingleton(mCurrentActivity, new StringCallBack() {
            @Override
            public void onCallBack(String msg) {
                ActivityStackManager.getInstance(mCurrentActivity).startLoginActivity();
                mCurrentActivity.finish();
            }
        }, null, "UnAuthorized", "Session Expired. Please Log in again.");
    }

    private void onUnauthorizedLicenceExpire() {
        AppPreferences.saveLoginStatus(false);
        AppPreferences.setIncomingCall(false);
        AppPreferences.setCallData(null);
        AppPreferences.setTripStatus("");
        AppPreferences.saveLoginStatus(false);
        AppPreferences.setPilotData(null);
        HomeActivity.visibleFragmentNumber = 0;
        Dialogs.INSTANCE.showAlertDialogNotSingleton(mCurrentActivity, new StringCallBack() {
            @Override
            public void onCallBack(String msg) {
                ActivityStackManager.getInstance(mCurrentActivity).startLoginActivity();
                mCurrentActivity.finish();
            }
        }, null, "Licence Expired", "Your driving licence is expired. Please renew your driving licence and then contact support.");
    }

    public void setStatusBtn() {
        if (mCurrentActivity == null || getView() == null) {
            return;
        }
        if (!AppPreferences.getAvailableStatus()) {
            statusCheck.setImageDrawable(ContextCompat.getDrawable(mCurrentActivity, R.drawable.inactive_icon));
            statusTv.setText("Inactive");
            rlInactiveImage.setVisibility(View.VISIBLE);
            myRangeBar.setEnabled(true);
        } else {
            statusCheck.setImageResource(R.drawable.active_icon);
            statusTv.setText("Active");
            rlInactiveImage.setVisibility(View.GONE);
            myRangeBar.setEnabled(false);
        }

        if (AppPreferences.isWalletAmountIncreased()) {
            tvFenceError.setText(AppPreferences.getWalletIncreasedError());
            tvFenceError.setVisibility(View.VISIBLE);
            tvConnectionStatus.setVisibility(View.GONE);
        } else if (AppPreferences.isOutOfFence()) {
            tvFenceError.setText("Non Service Area");
            tvFenceError.setVisibility(View.VISIBLE);
            tvConnectionStatus.setVisibility(View.GONE);
        } else {
            tvFenceError.setVisibility(View.GONE);
            tvConnectionStatus.setVisibility(View.VISIBLE);
        }
    }

    private UserDataHandler handler = new UserDataHandler() {

        @Override
        public void onDriverStatsResponse(final DriverStatsResponse response) {
            if (mCurrentActivity != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccess() && response.getData() != null && getView() != null) {
                            if (StringUtils.isNotBlank(response.getData().getRating())
                                    && StringUtils.isNotBlank(response.getData().getAcceptanceRate())
                                    && StringUtils.isNotBlank(response.getData().getTrips())) {
                                PilotData data = AppPreferences.getPilotData();
                                data.setRating(response.getData().getRating());
                                data.setAcceptance_rate(Math.round(Double.parseDouble(response.getData().getAcceptanceRate())) + "");
                                data.setVerified_trips(response.getData().getTrips());
                                AppPreferences.setPilotData(data);
                                AppPreferences.setStatsApiCallRequired(false);
                                AppPreferences.setLastStatsApiCallTime(System.currentTimeMillis());
                            }
                        }
                    }
                });
            }
        }

        @Override
        public void onRunningTrips(final CheckDriverStatusResponse response) {
            if (mCurrentActivity != null && getView() != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccess()) {
                            try {
                                if (StringUtils.isNotBlank(response.getData().getStarted_at())) {
                                    AppPreferences.setStartTripTime(
                                            AppPreferences.getServerTimeDifference() +
                                                    Utils.getTimeInMiles(response.getData().getStarted_at()));
                                }
                                AppPreferences.setCallData(response.getData());
                                AppPreferences.setTripStatus(response.getData().getStatus());
                                if (!response.getData().getStatus().equalsIgnoreCase(TripStatus.ON_FINISH_TRIP)) {
                                    WebIORequestHandler.getInstance().registerChatListener();
                                    ActivityStackManager.getInstance(mCurrentActivity)
                                            .startJobActivity();
                                } else {
                                    ActivityStackManager.getInstance(mCurrentActivity)
                                            .startFeedbackFromResume();
                                }
                                mCurrentActivity.finish();
                            } catch (NullPointerException ignored) {

                            }
                        } else {
                            if (response.getCode() == HTTPStatus.UNAUTHORIZED) {
                                Utils.onUnauthorized(mCurrentActivity);
                            } else {
                                //If there is no pending trip free all states for new trip..
                                Utils.setCallIncomingState();
                            }
                        }
                    }
                });
            }
        }

        @Override
        public void onDropOffUpdated(final DriverDestResponse commonResponse) {
            mCurrentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (commonResponse != null) {
                        if (mDropOff != null) {
                            tv_destinationName.setText(mDropOff.address);
                        }
                        rl_destinationSelected.setVisibility(View.VISIBLE);
                        rl_setDestination.setVisibility(View.GONE);
                    }
                }
            });
        }

        @Override
        public void onUpdateStatus(final PilotStatusResponse pilotStatusResponse) {
            if (mCurrentActivity != null && getView() != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.INSTANCE.dismissDialog();
                        if (pilotStatusResponse.isSuccess()) {
                            if (AppPreferences.getAvailableStatus()) {
                                if (AppPreferences.isWalletAmountIncreased()) {
                                    AppPreferences.setWalletAmountIncreased(false);
                                }
                                if (AppPreferences.isOutOfFence()) {
                                    AppPreferences.setOutOfFence(false);
                                }
                                ActivityStackManager.getInstance(mCurrentActivity).restartLocationService();
                            } else {
                                ActivityStackManager.getInstance(mCurrentActivity).stopLocationService();
                            }
                            setStatusBtn();
                        } else {
                            if (pilotStatusResponse.getCode() == HTTPStatus.UNAUTHORIZED) {
                                onUnauthorized();
                            } else {
                                Utils.appToast(mCurrentActivity, pilotStatusResponse.getMessage());
                                AppPreferences.setAvailableStatus(false);
                                setStatusBtn();
                            }
                        }
                    }
                });
            }
        }

        @Override
        public void getHeatMap(final HeatMapResponse heatMapResponse) {
            if (mCurrentActivity != null && getView() != null && mGoogleMap != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (heatMapResponse.isSuccess()) {
                            if (heatMapResponse.getData().size() > 0) {
                                ArrayList<LatLng> latLngs = new ArrayList<>();
                                for (HeatmapLatlng heatmapLatlng : heatMapResponse.getData()) {
                                    latLngs.add(new LatLng(heatmapLatlng.getLat(), heatmapLatlng.getLng()));
                                }
                                HeatmapTileProvider heatmapTileProvider = new HeatmapTileProvider.Builder().data(latLngs).build();
                                heatmapTileProvider.setRadius(30);
                                if (null != mHeatmapOverlay) {
                                    mHeatmapOverlay.clearTileCache();
                                }
                                if (mGoogleMap != null) {
                                    mGoogleMap.clear();
                                }
                                mHeatmapOverlay = mGoogleMap.addTileOverlay(new TileOverlayOptions().tileProvider(heatmapTileProvider));
                            } else {
                                if (null != mHeatmapOverlay) {
                                    mHeatmapOverlay.clearTileCache();
                                }
                                if (mGoogleMap != null) {
                                    mGoogleMap.clear();
                                }
                            }

                        }
                    }
                });
            }

        }

        @Override
        public void onError(final int errorCode, final String errorMessage) {
            if (mCurrentActivity != null && getView() != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rl_destinationSelected.setVisibility(View.GONE);
                        rl_setDestination.setVisibility(View.VISIBLE);

                        Dialogs.INSTANCE.dismissDialog();
                        if (errorCode == HTTPStatus.UNAUTHORIZED) {
                            onUnauthorized();
                        } else {
                            Dialogs.INSTANCE.showToast(mCurrentActivity, errorMessage);
                        }
                    }
                });
            }
        }
    };


    public void onEvent(final String action) {
        if (mCurrentActivity != null && getView() != null) {
            mCurrentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (action.equalsIgnoreCase(Keys.CONNECTION_BROADCAST)) {
                        setConnectionStatus();
                    } else if (action.equalsIgnoreCase("INACTIVE-PUSH") || action.equalsIgnoreCase("INACTIVE-FENCE")) {
                        setStatusBtn();
                        //TODO Alert Dialog
                    }
                }
            });

        }

    }


    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }


    public void getCurrentVersion() {
        if (mCurrentActivity != null && getView() != null) {
            PackageManager pm = mCurrentActivity.getPackageManager();
            PackageInfo pInfo;

            try {
                pInfo = pm.getPackageInfo(mCurrentActivity.getPackageName(), 0);
                currentVersion = pInfo.versionName;
                if (AppPreferences.getSettings() != null
                        && AppPreferences.getSettings().getSettings() != null) {
                    latestVersion = AppPreferences.getSettings().getSettings().getApp_version();
                }
                if (StringUtils.isNotBlank(latestVersion) && StringUtils.isNotBlank(currentVersion)) {
                    Utils.redLog("VERSION", "Current: " + currentVersion + " Play Store: " + latestVersion);
                    if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                        if (!Dialogs.INSTANCE.isShowing()) {
                            Dialogs.INSTANCE.showUpdateAppDialog(mCurrentActivity, "Update App", "Latest Version Of Bykea is " +
                                    "available on Play Store. Please Update the App for better Service. Thank You !", "https://play.google.com/store/apps/details?id=com.bykea.pk.partner");

                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e1) {
                e1.printStackTrace();
            }

        }

    }
}

package com.bykea.pk.partner.ui.fragments;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.communication.socket.WebIORequestHandler;
import com.bykea.pk.partner.models.data.PilotData;
import com.bykea.pk.partner.models.response.CheckDriverStatusResponse;
import com.bykea.pk.partner.models.response.DriverStatsResponse;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.bykea.pk.partner.ui.helpers.StringCallBack;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.TripStatus;
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
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeFragment extends Fragment {

    @Bind(R.id.rlInactiveImage)
    RelativeLayout rlInactiveImage;
    @Bind(R.id.mapPinIv)
    ImageView mapPinIv;
    @Bind(R.id.statusTv)
    FontTextView statusTv;
    @Bind(R.id.driverNameTv)
    FontTextView driverNameTv;
    @Bind(R.id.driverRating)
    FontTextView driverRating;
    @Bind(R.id.cityTv)
    FontTextView cityTv;
    @Bind(R.id.tvConnectionStatus)
    FontTextView tvConnectionStatus;
    @Bind(R.id.tvFenceError)
    FontTextView tvFenceError;
    @Bind(R.id.acceptanceRateTv)
    FontTextView acceptanceRateTv;
    @Bind(R.id.verifiedTripsTv)
    FontTextView verifiedTripsTv;
    @Bind(R.id.plateNoTv)
    FontTextView plateNoTv;
    @Bind(R.id.statusCheck)
    ImageView statusCheck;

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
    }

    private void initViews() {
        if (Utils.isAppVersionCheckRequired(mCurrentActivity)) {
            getCurrentVersion();
        }
        mCurrentActivity.setPilotData(AppPreferences.getPilotData(mCurrentActivity));
        if (Utils.isLicenceExpired(mCurrentActivity.getPilotData().getLicenseExpiry())) {
            onUnauthorizedLicenceExpire();
        }
        driverNameTv.setText(mCurrentActivity.getPilotData().getFullName());
        if (StringUtils.isNotBlank(mCurrentActivity.getPilotData().getRating())) {
            String rating = Utils.formatDecimalPlaces(mCurrentActivity.getPilotData().getRating());
            if (rating.equalsIgnoreCase("0")) {
                driverRating.setText("Rating N/A");
            } else {
                driverRating.setText("Rating " + Utils.formatDecimalPlaces(mCurrentActivity.getPilotData().getRating()));
            }
        } else {
            driverRating.setText("Rating N/A");
        }
        if (StringUtils.isNotBlank(mCurrentActivity.getPilotData().getAcceptance_rate())) {
            acceptanceRateTv.setText(Math.round(Double.parseDouble(mCurrentActivity.getPilotData().getAcceptance_rate())) + "%");
        }
        if (StringUtils.isNotBlank(mCurrentActivity.getPilotData().getVerified_trips())) {
            verifiedTripsTv.setText(mCurrentActivity.getPilotData().getVerified_trips());
        }
        String plateNo = mCurrentActivity.getPilotData().getPlateNo().replaceAll("[^A-Za-z0-9 ]", "").replaceAll("\\s+", "");
        if (StringUtils.isNotBlank(plateNo)) {
            String city = plateNo.replaceAll("\\d+", "");
            if (StringUtils.isNotBlank(city)) {
                cityTv.setText(city);
            }
            String plate = plateNo.replaceAll("[a-zA-Z]+", "");
            if (StringUtils.isNotBlank(plate)) {
                plateNoTv.setText(plate);
            }
        }
        setStatusBtn();
        setConnectionStatus();
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
        if (AppPreferences.getSettings(DriverApp.getContext()) != null && AppPreferences.getSettings(DriverApp.getContext()).getSettings() != null
                && StringUtils.isNotBlank(AppPreferences.getSettings(DriverApp.getContext()).getSettings().getHeatmap_refresh_timer())) {
            timer = (long) Math.ceil(Double.parseDouble(AppPreferences.getSettings(DriverApp.getContext()).getSettings().getHeatmap_refresh_timer()));
        }
        return timer * 60000;
    }

    private CountDownTimer countDownTimer = new CountDownTimer(getHeatMapTimer(), getHeatMapTimer()) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            setConnectionStatus();
            if (Connectivity.isConnectedFast(mCurrentActivity) && AppPreferences.getAvailableStatus(mCurrentActivity))
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
            if (AppPreferences.getLatitude(mCurrentActivity) != 0.0 &&
                    AppPreferences.getLongitude(mCurrentActivity) != 0.0)
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(AppPreferences.getLatitude(mCurrentActivity)
                                , AppPreferences.getLongitude(mCurrentActivity))
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
        if (Connectivity.isConnectedFast(mCurrentActivity) && AppPreferences.getAvailableStatus(mCurrentActivity))
            repository.requestHeatMapData(mCurrentActivity, handler);

        Utils.setCallIncomingState(mCurrentActivity);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Keys.LOCATION_UPDATE_BROADCAST);
        mCurrentActivity.registerReceiver(myReceiver, intentFilter);
        if (AppPreferences.isLoggedIn(mCurrentActivity)) {
//            checkNotification();
            initViews();
            if (Utils.isStatsApiCallRequired(mCurrentActivity)) {
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
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @OnClick({R.id.mapPinIv, R.id.statusCheck, R.id.rlInactiveImage})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlInactiveImage:
                break;
            case R.id.mapPinIv:
                setDriverLocation();
                break;
            case R.id.statusCheck:
                if (Connectivity.isConnectedFast(mCurrentActivity)) {
                    if (AppPreferences.getAvailableStatus(mCurrentActivity)) {
                        Dialogs.INSTANCE.showInactiveConfirmationDialog(mCurrentActivity, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Dialogs.INSTANCE.dismissDialog();
                                if (Connectivity.isConnectedFast(mCurrentActivity)) {
                                    Dialogs.INSTANCE.showLoader(mCurrentActivity);
                                    AppPreferences.setAvailableStatus(mCurrentActivity, false);
                                    repository.requestUpdateStatus(mCurrentActivity, handler, false);
                                }
                            }
                        });
                    } else {
                        if (Connectivity.isConnectedFast(mCurrentActivity)) {
                            Dialogs.INSTANCE.showLoader(mCurrentActivity);
                            AppPreferences.setAvailableStatus(mCurrentActivity, true);
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

    private void setDriverLocation() {
        if (null != mGoogleMap) {
            Utils.formatMap(mGoogleMap);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(AppPreferences.getLatitude(mCurrentActivity)
                            , AppPreferences.getLongitude(mCurrentActivity))
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
                                Location location = (Location) intent.getParcelableExtra("location");
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
        AppPreferences.saveLoginStatus(mCurrentActivity, false);
        AppPreferences.setIncomingCall(mCurrentActivity, false);
        AppPreferences.setCallData(mCurrentActivity, null);
        AppPreferences.setTripStatus(mCurrentActivity, "");
        AppPreferences.saveLoginStatus(mCurrentActivity, false);
        AppPreferences.setPilotData(mCurrentActivity, null);
        HomeActivity.visibleFragmentNumber = 0;
        ActivityStackManager.activities = 0;
        Dialogs.INSTANCE.showAlertDialogNotSingleton(mCurrentActivity, new StringCallBack() {
            @Override
            public void onCallBack(String msg) {
                ActivityStackManager.getInstance(mCurrentActivity).startLoginActivity();
                mCurrentActivity.finish();
            }
        }, null, "UnAuthorized", "Session Expired. Please Log in again.");
    }

    private void onUnauthorizedLicenceExpire() {
        AppPreferences.saveLoginStatus(mCurrentActivity, false);
        AppPreferences.setIncomingCall(mCurrentActivity, false);
        AppPreferences.setCallData(mCurrentActivity, null);
        AppPreferences.setTripStatus(mCurrentActivity, "");
        AppPreferences.saveLoginStatus(mCurrentActivity, false);
        AppPreferences.setPilotData(mCurrentActivity, null);
        HomeActivity.visibleFragmentNumber = 0;
        ActivityStackManager.activities = 0;
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
        if (!AppPreferences.getAvailableStatus(mCurrentActivity)) {
            statusCheck.setImageDrawable(ContextCompat.getDrawable(mCurrentActivity, R.drawable.inactive_icon));
            statusTv.setText("Inactive");
            rlInactiveImage.setVisibility(View.VISIBLE);
        } else {
            statusCheck.setImageResource(R.drawable.active_icon);
            statusTv.setText("Active");
            rlInactiveImage.setVisibility(View.GONE);
        }

        if (AppPreferences.isWalletAmountIncreased(mCurrentActivity)) {
            tvFenceError.setText(AppPreferences.getWalletIncreasedError(mCurrentActivity));
            tvFenceError.setVisibility(View.VISIBLE);
            tvConnectionStatus.setVisibility(View.GONE);
        } else if (AppPreferences.isOutOfFence(mCurrentActivity)) {
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
                            if (StringUtils.isNotBlank(response.getData().getVerified_trips())) {
                                verifiedTripsTv.setText(response.getData().getVerified_trips());
                            }
                            if (StringUtils.isNotBlank(response.getData().getAcceptance_rate())) {
                                acceptanceRateTv.setText(Math.round(Double.parseDouble(response.getData().getAcceptance_rate())) + "%");
                            }
                            String rating = response.getData().getRating();
                            if (rating.equalsIgnoreCase("0")) {
                                driverRating.setText("Rating N/A");
                            } else {
                                driverRating.setText("Rating " + Utils.formatDecimalPlaces(rating));
                            }
                            if (StringUtils.isNotBlank(rating)
                                    && StringUtils.isNotBlank(response.getData().getAcceptance_rate())
                                    && StringUtils.isNotBlank(response.getData().getVerified_trips())) {
                                PilotData data = AppPreferences.getPilotData(mCurrentActivity);
                                data.setRating(response.getData().getRating());
                                data.setAcceptance_rate(Math.round(Double.parseDouble(response.getData().getAcceptance_rate())) + "");
                                data.setVerified_trips(response.getData().getVerified_trips());
                                AppPreferences.setPilotData(mCurrentActivity, data);
                                AppPreferences.setStatsApiCallRequired(mCurrentActivity, false);
                                AppPreferences.setLastStatsApiCallTime(mCurrentActivity, System.currentTimeMillis());
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
                                    AppPreferences.setStartTripTime(mCurrentActivity,
                                            AppPreferences.getServerTimeDifference(mCurrentActivity) +
                                                    Utils.getTimeInMiles(response.getData().getStarted_at()));
                                }
                                AppPreferences.setCallData(mCurrentActivity, response.getData());
                                AppPreferences.setTripStatus(mCurrentActivity, response.getData().getStatus());
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
                                Utils.setCallIncomingState(mCurrentActivity);
                            }
                        }
                    }
                });
            }
        }


        @Override
        public void onUpdateStatus(final PilotStatusResponse pilotStatusResponse) {
            if (mCurrentActivity != null && getView() != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.INSTANCE.dismissDialog();
                        if (pilotStatusResponse.isSuccess()) {
                            if (AppPreferences.getAvailableStatus(mCurrentActivity)) {
                                if (AppPreferences.isWalletAmountIncreased(mCurrentActivity)) {
                                    AppPreferences.setWalletAmountIncreased(mCurrentActivity, false);
                                }
                                if (AppPreferences.isOutOfFence(mCurrentActivity)) {
                                    AppPreferences.setOutOfFence(mCurrentActivity, false);
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
                                AppPreferences.setAvailableStatus(mCurrentActivity, false);
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
                                HeatmapTileProvider mHeatmapProvider = new HeatmapTileProvider.Builder().data(latLngs).build();
                                mHeatmapProvider.setRadius(HeatmapTileProvider.DEFAULT_RADIUS);
                                if (null != mHeatmapOverlay) {
                                    mHeatmapOverlay.clearTileCache();
                                }
                                if (mGoogleMap != null) {
                                    mGoogleMap.clear();
                                }
                                mHeatmapOverlay = mGoogleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mHeatmapProvider));
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
                    /*if (action.equalsIgnoreCase(Constants.ON_NEW_NOTIFICATION)) {
                        checkNotification();
                    }*/
                    if (action.equalsIgnoreCase("INACTIVE-PUSH")) {
                        setStatusBtn();
                        //TODO Alert Dialog
                    }
                    if (action.equalsIgnoreCase("INACTIVE-FENCE")) {
                        setStatusBtn();
                    }
                    if (action.equalsIgnoreCase(Keys.CONNECTION_BROADCAST)) {
                        setConnectionStatus();
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


    private void getCurrentVersion() {
        if (mCurrentActivity != null && getView() != null) {
            PackageManager pm = mCurrentActivity.getPackageManager();
            PackageInfo pInfo = null;

            try {
                pInfo = pm.getPackageInfo(mCurrentActivity.getPackageName(), 0);
                currentVersion = pInfo.versionName;
                new GetLatestVersion().execute();
            } catch (PackageManager.NameNotFoundException e1) {
                e1.printStackTrace();
            }

        }

    }


    private class GetLatestVersion extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            try {
//It retrieves the latest version by scraping the content of current version from play store at runtime
                Document doc = Jsoup.connect("https://play.google.com/store/apps/details?id=com.bykea.pk.partner").get();
                latestVersion = doc.getElementsByAttributeValue
                        ("itemprop", "softwareVersion").first().text();

            } catch (Exception e) {
                e.printStackTrace();

            }

            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (mCurrentActivity != null && getView() != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (latestVersion != null) {
                            Utils.redLog("VERSION", "Current: " + currentVersion + " Play Store: " + latestVersion);
                            if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                                if (!Dialogs.INSTANCE.isShowing()) {
                                    Dialogs.INSTANCE.showUpdateAppDialog(mCurrentActivity, "Update App", "Latest Version Of Bykea is " +
                                            "available on Play Store. Please Update the App for better Service. Thank You !", "https://play.google.com/store/apps/details?id=com.bykea.pk.partner");
                                }
                            } else {
                                AppPreferences.setVersionCheckTime(mCurrentActivity, System.currentTimeMillis());
                            }

                        }
                    }
                });
            }

            super.onPostExecute(jsonObject);
        }
    }
}

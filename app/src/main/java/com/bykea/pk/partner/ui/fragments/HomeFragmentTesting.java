package com.bykea.pk.partner.ui.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bykea.pk.partner.Notifications;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.communication.socket.WebIORequestHandler;
import com.bykea.pk.partner.models.data.PilotData;
import com.bykea.pk.partner.models.data.PlacesResult;
import com.bykea.pk.partner.models.response.CheckDriverStatusResponse;
import com.bykea.pk.partner.models.response.DriverDestResponse;
import com.bykea.pk.partner.models.response.DriverStatsResponse;
import com.bykea.pk.partner.models.response.HeatMapUpdatedResponse;
import com.bykea.pk.partner.models.response.PilotStatusResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.bykea.pk.partner.ui.activities.SelectPlaceActivity;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.DrawPolygonAsync;
import com.bykea.pk.partner.ui.helpers.IViewTouchEvents;
import com.bykea.pk.partner.ui.helpers.StringCallBack;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.Permissions;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;
import com.bykea.pk.partner.widgets.MyRangeBarRupay;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;
import static android.view.Gravity.CENTER;

public class HomeFragmentTesting extends Fragment {

    private Unbinder unbinder;

    private HomeActivity mCurrentActivity;

    private long mLastClickTime;

    private GoogleMap mGoogleMap;

    private UserRepository repository;

    private boolean isScreenInFront;

    private Location mPrevLocToShow;

    private int[] cashInHand;

    private Polygon mDemandPolygon;

    @BindView(R.id.previusDurationBtn)
    ImageView previusDurationBtn;

    @BindView(R.id.durationBtn)
    ImageView durationBtn;

    @BindView(R.id.myRangeBar)
    MyRangeBarRupay myRangeBar;

    @BindView(R.id.achaconnectionTv)
    TextView achaconnectionTv;

    @BindView(R.id.homeMapFragment)
    MapView mapView;

    @BindView(R.id.muntakhibTv)
    TextView muntakhibTv;

    @BindView(R.id.tvFenceError)
    TextView tvFenceError;

    @BindView(R.id.durationTv)
    TextView durationTv;

    @BindView(R.id.llTopActive)
    RelativeLayout headerTopActiveLayout;

    @BindView(R.id.llTop)
    RelativeLayout headerTopUnActiveLayout;

    @BindView(R.id.layoutupper)
    LinearLayout layoutUpper;

    @BindView(R.id.layoutDuration)
    RelativeLayout layoutDuration;

    @BindView(R.id.driverStatsLayout)
    LinearLayout driverStatsLayout;

    @BindView(R.id.tvCihIndex1)
    FontTextView tvCihIndex1;

    @BindView(R.id.tvCihIndex2)
    FontTextView tvCihIndex2;

    @BindView(R.id.tvCihIndex3)
    FontTextView tvCihIndex3;

    @BindView(R.id.tvCihIndex4)
    FontTextView tvCihIndex4;

    @BindView(R.id.tvCihIndex5)
    FontTextView tvCihIndex5;


    private FrameLayout mapPinIv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_testing, container, false);
        unbinder = ButterKnife.bind(this, view);

        mCurrentActivity = ((HomeActivity) getActivity());
        mCurrentActivity.hideToolbarLogo();


        mKhudaHafizClick();

        mBismillaClick();



        mCurrentActivity.setDemandButtonForBismilla("ڈیمانڈ");
        mCurrentActivity.findViewById(R.id.toolbarLine).setVisibility(View.GONE);
        mCurrentActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        return view;
    }

    private void mKhudaHafizClick() {
        mCurrentActivity.setToolbarLogoKhudaHafiz(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Connectivity.isConnectedFast(mCurrentActivity)) {
                    if (AppPreferences.getAvailableStatus()) {
                        Dialogs.INSTANCE.showInactiveConfirmationDialog(mCurrentActivity, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Dialogs.INSTANCE.dismissDialog();
                                callAvailableStatusAPI(false);
                                mCurrentActivity.showBismillah();
                                mapView.setVisibility(View.GONE);
                                headerTopActiveLayout.setVisibility(View.GONE);
                                headerTopUnActiveLayout.setVisibility(View.VISIBLE);
                                layoutUpper.setVisibility(View.VISIBLE);
                                layoutDuration.setVisibility(View.VISIBLE);
                                driverStatsLayout.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        callAvailableStatusAPI(true);
                        mCurrentActivity.showBismillah();
                        mapView.setVisibility(View.GONE);
                        headerTopActiveLayout.setVisibility(View.GONE);
                        headerTopUnActiveLayout.setVisibility(View.VISIBLE);
                        layoutUpper.setVisibility(View.VISIBLE);
                        layoutDuration.setVisibility(View.VISIBLE);
                        driverStatsLayout.setVisibility(View.VISIBLE);
                    }


                } else {
                    Dialogs.INSTANCE.showError(mCurrentActivity
                            , mapPinIv, getString(R.string.error_internet_connectivity));
                }
            }
        });
    }

    private void mBismillaClick() {
        mCurrentActivity.setToolbarLogoBismilla(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Connectivity.isConnectedFast(mCurrentActivity)) {
                    if (AppPreferences.getAvailableStatus()) {
                        Dialogs.INSTANCE.showInactiveConfirmationDialog(mCurrentActivity, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Dialogs.INSTANCE.dismissDialog();
                                callAvailableStatusAPI(false);
                                mCurrentActivity.showKhudaHafiz();
                                mapView.setVisibility(View.VISIBLE);
                                headerTopActiveLayout.setVisibility(View.VISIBLE);
                                headerTopUnActiveLayout.setVisibility(View.GONE);
                                layoutUpper.setVisibility(View.GONE);
                                layoutDuration.setVisibility(View.GONE);
                                driverStatsLayout.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        callAvailableStatusAPI(true);
                        mCurrentActivity.showKhudaHafiz();
                        mapView.setVisibility(View.VISIBLE);
                        headerTopActiveLayout.setVisibility(View.VISIBLE);
                        headerTopUnActiveLayout.setVisibility(View.GONE);
                        layoutUpper.setVisibility(View.GONE);
                        layoutDuration.setVisibility(View.GONE);
                        driverStatsLayout.setVisibility(View.GONE);
                    }


                } else {
                    Dialogs.INSTANCE.showError(mCurrentActivity
                            , mapPinIv, getString(R.string.error_internet_connectivity));
                }
            }
        });
    }

    private void callAvailableStatusAPI(boolean status) {
        if (Connectivity.isConnectedFast(mCurrentActivity)) {
            Dialogs.INSTANCE.showLoader(mCurrentActivity);
            AppPreferences.setAvailableAPICalling(true);
            repository.requestUpdateStatus(mCurrentActivity, handler, status);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mapView != null){
            mapView.onCreate(savedInstanceState);
        }

//        mapView.onResume();
        try {
            MapsInitializer.initialize(mCurrentActivity.getApplicationContext());
        } catch (Exception e) {
            Utils.redLog("HomeScreenException", e.getMessage());
            e.printStackTrace();
        }
        mapView.getMapAsync(mapReadyCallback);
        //mCurrentActivity.hideToolbarTitle();

        repository = new UserRepository();

        checkGooglePlayService();

        Dialogs.INSTANCE.setCalenderCurrentWeek(durationTv);

        setDestination();
        initRangeBar();
        AppPreferences.setAvailableAPICalling(false);
    }

    private void checkGooglePlayService() {
        Utils.checkGooglePlayServicesVersion(mCurrentActivity);
        initRangeBar();
        AppPreferences.setAvailableAPICalling(false);
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
                ActivityStackManager.getInstance().startLoginActivity(mCurrentActivity);
                mCurrentActivity.finish();
            }
        }, null, "Licence Expired", "Your driving licence is expired. Please renew your driving licence and then contact support.");
    }

    private void initViews() {
        mCurrentActivity.setPilotData(AppPreferences.getPilotData());
        if (StringUtils.isNotBlank(mCurrentActivity.getPilotData().getLicenseExpiry()) && Utils.isLicenceExpired(mCurrentActivity.getPilotData().getLicenseExpiry())) {
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
        //achaconnectionTv.setText(connectionStatus);
        //achaconnectionTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable._good_sattelite, 0, 0, 0);
        switch (connectionStatus) {
            case "Unknown Status":
                //tvConnectionStatus.setBackgroundColor(ContextCompat.getColor(mCurrentActivity, R.color.textColorSecondary));
                break;
            case "Battery Low":
                achaconnectionTv.setTextColor(ContextCompat.getColor(mCurrentActivity, R.color.color_error));
                achaconnectionTv.setText("لو بیٹری");
                break;
            case "Poor Connection":
            case "Fair Connection":
            case "No Connection":
                achaconnectionTv.setText("برا کنکشن");
                //tvConnectionStatus.setBackgroundColor(ContextCompat.getColor(mCurrentActivity, R.color.color_fair_connection));
                break;
            case "Good Connection":
                achaconnectionTv.setText("اچھا کنکشن");
                break;
        }
//        if (connectionStatus.equalsIgnoreCase("Unknown Status")) {
//            tvConnectionStatus.setBackgroundColor(ContextCompat.getColor(mCurrentActivity, R.color.textColorSecondary));
//        } else if (connectionStatus.equalsIgnoreCase("Battery Low")) {
//            tvConnectionStatus.setBackgroundColor(ContextCompat.getColor(mCurrentActivity, R.color.color_error));
//            tvConnectionStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.low_battery_icon, 0, 0, 0);
//        } else if (connectionStatus.equalsIgnoreCase("Poor Connection") ||
//                connectionStatus.equalsIgnoreCase("Fair Connection") ||
//                connectionStatus.equalsIgnoreCase("No Connection")) {
//            tvConnectionStatus.setBackgroundColor(ContextCompat.getColor(mCurrentActivity, R.color.color_fair_connection));
//        } else if (connectionStatus.equalsIgnoreCase("Good Connection")) {
//            tvConnectionStatus.setBackgroundColor(ContextCompat.getColor(mCurrentActivity, R.color.colorPrimary));
//        }

    }

    public synchronized void setStatusBtn() {
        if (mCurrentActivity == null || getView() == null) {
            return;
        }
        if (!AppPreferences.getAvailableStatus()) {     //inactive state


            myRangeBar.setEnabled(true);

            mapView.setVisibility(View.GONE);
            headerTopActiveLayout.setVisibility(View.GONE);
            headerTopUnActiveLayout.setVisibility(View.VISIBLE);
            mCurrentActivity.showBismillah();
            layoutUpper.setVisibility(View.VISIBLE);
            layoutDuration.setVisibility(View.VISIBLE);
            driverStatsLayout.setVisibility(View.VISIBLE);

            if (AppPreferences.getDriverDestination() == null) {

            } else {

                muntakhibTv.setText(AppPreferences.getDriverDestination().address);
            }
        } else {        //active state

            myRangeBar.setEnabled(false);
            mCurrentActivity.showKhudaHafiz();
            mapView.setVisibility(View.VISIBLE);
            headerTopActiveLayout.setVisibility(View.VISIBLE);
            headerTopUnActiveLayout.setVisibility(View.GONE);
            layoutUpper.setVisibility(View.GONE);
            layoutDuration.setVisibility(View.GONE);
            driverStatsLayout.setVisibility(View.GONE);

            if (null != AppPreferences.getDriverDestination()) {

                muntakhibTv.setText(AppPreferences.getDriverDestination().address);
            } else {

            }
        }

        if (AppPreferences.isWalletAmountIncreased()) {
            setFenceError(AppPreferences.getWalletIncreasedError());
        } else if (AppPreferences.isOutOfFence()) {
            setFenceError("Non Service Area");
        } else {
            tvFenceError.setVisibility(View.GONE);
            achaconnectionTv.setVisibility(View.VISIBLE);
        }
    }

    private void setFenceError(String errorMessage) {
        tvFenceError.setText(errorMessage);
        tvFenceError.setVisibility(View.VISIBLE);
        achaconnectionTv.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        mapView.onResume();
        isScreenInFront = true;

        Notifications.removeAllNotifications(mCurrentActivity);

        Utils.setCallIncomingStateWithoutRestartingService();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Keys.LOCATION_UPDATE_BROADCAST);
        mCurrentActivity.registerReceiver(myReceiver, intentFilter);
        if (AppPreferences.isLoggedIn()) {

            initViews();
            if (Utils.isStatsApiCallRequired()) {
                repository.requestDriverStats(mCurrentActivity, handler, true);
            }
        }
        repository.requestRunningTrip(mCurrentActivity, handler);
        if (enableLocation()) return;
        super.onResume();
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            if (mCurrentActivity != null && getView() != null && mGoogleMap != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (null != intent && intent.getAction().equalsIgnoreCase(Keys.LOCATION_UPDATE_BROADCAST)) {
                            if (intent.getExtras() != null) {
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

    public void initRangeBar() {
        //myRangeBar.init(mRangeBarTouch);
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
            if (cashInHand[i] <= value) {
                currentIndex = i;
            }
        }
        myRangeBar.refreshDrawableState();
        myRangeBar.invalidate();
        myRangeBar.setCurrentIndex(currentIndex);
        myRangeBar.setOnSlideListener(new MyRangeBarRupay.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                Utils.redLog("Cash In Hand", "" + cashInHand[index]);
                AppPreferences.setCashInHands(cashInHand[index]);
            }
        });
    }

    private UserDataHandler handler = new UserDataHandler() {

        @Override
        public void onDriverStatsResponse(final DriverStatsResponse response) {
            if (mCurrentActivity != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccess() && response.getData() != null && getView() != null) {
                            if (StringUtils.isNotBlank(response.getData().getRating())) {
                                PilotData data = AppPreferences.getPilotData();
                                data.setRating(response.getData().getRating());
//                                data.setAcceptance_rate(Math.round(Double.parseDouble(response.getData().getAcceptanceRate())) + "");
//                                data.setVerified_trips(response.getData().getTrips());
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
                                /*if (StringUtils.isNotBlank(response.getData().getStarted_at())) {
                                    AppPreferences.setStartTripTime(
                                            AppPreferences.getServerTimeDifference() +
                                                    Utils.getTimeInMiles(response.getData().getStarted_at()));
                                }*/
                                AppPreferences.setCallData(response.getData());
                                AppPreferences.setTripStatus(response.getData().getStatus());
                                if (!response.getData().getStatus().equalsIgnoreCase(TripStatus.ON_FINISH_TRIP)) {
                                    WebIORequestHandler.getInstance().registerChatListener();
                                    ActivityStackManager.getInstance()
                                            .startJobActivity(mCurrentActivity);
                                } else {
                                    ActivityStackManager.getInstance()
                                            .startFeedbackFromResume(mCurrentActivity);
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

        /*
         * Unused*/
        @Override
        public void onDropOffUpdated(final DriverDestResponse commonResponse) {
            mCurrentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (commonResponse != null) {
//                        if (mDropOff != null) {
//                            Utils.appToastDebug(mCurrentActivity,commonResponse.getMessage());
//                        }
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
                            AppPreferences.setAvailableStatus(!AppPreferences.getAvailableStatus());
                            AppPreferences.setAvailableAPICalling(false);
                            if (AppPreferences.getAvailableStatus()) {
                                if (AppPreferences.isWalletAmountIncreased()) {
                                    AppPreferences.setWalletAmountIncreased(false);
                                }
                                if (AppPreferences.isOutOfFence()) {
                                    AppPreferences.setOutOfFence(false);
                                }
                                ActivityStackManager.getInstance().restartLocationService(mCurrentActivity);
                            } else {
                                AppPreferences.setDriverDestination(null);
                                ActivityStackManager.getInstance().stopLocationService(mCurrentActivity);
                            }
                            setStatusBtn();
                        } else {
                            if (pilotStatusResponse.getCode() == HTTPStatus.UNAUTHORIZED) {
                                Utils.onUnauthorized(mCurrentActivity);
                            } else {
                                Utils.appToast(mCurrentActivity, pilotStatusResponse.getMessage());
                                AppPreferences.setAvailableStatus(false);
                                AppPreferences.setDriverDestination(null);
                                setStatusBtn();
                            }
                        }
                    }
                });
            }
        }

        @Override
        public void getHeatMap(final ArrayList<HeatMapUpdatedResponse> heatMapResponse) {
            if (mCurrentActivity != null && getView() != null && mGoogleMap != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateHeatMapUI(heatMapResponse);
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
//                        destinationSet(false);
                        Dialogs.INSTANCE.dismissDialog();
                        if (errorCode == HTTPStatus.UNAUTHORIZED) {
                            Utils.onUnauthorized(mCurrentActivity);
                        } else {
                            Dialogs.INSTANCE.showToast(mCurrentActivity, errorMessage);
                        }
                    }
                });
            }
        }
    };

    private ArrayList<Polygon> mPolygonList = new ArrayList<>();

    private void updateHeatMapUI(final ArrayList<HeatMapUpdatedResponse> data) {
        if (mPolygonList.size() > 0) {
            for (Polygon polygon : mPolygonList) {
                polygon.remove();
            }
            mPolygonList.clear();
        }
        if (mGoogleMap != null) {
            mGoogleMap.clear();
        }
        new DrawPolygonAsync(data, new DrawPolygonAsync.HeatMapCallback() {
            @Override
            public void onHeatMapDataParsed(final PolygonOptions polygonOptions) {
                synchronized (this) {
                    if (mCurrentActivity != null && getView() != null) {
                        mCurrentActivity.runOnUiThread(new Runnable() {
                            @Override
                            public synchronized void run() {
                                mPolygonList.add(mGoogleMap.addPolygon(polygonOptions));
                            }
                        });
                    }
                }
            }
        }).startAsyncTask();


        /*//TODO Remove test code
        LatLng southWest = new LatLng(24.9334716796875, 66.95068359375);
        LatLng northEast = new LatLng(24.8126220703125, 67.115478515625);
        LatLng southEast = new LatLng(24.9334716796875, 67.115478515625);
        LatLng northWest = new LatLng(24.8126220703125, 66.95068359375);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(southWest);
        builder.include(northEast);
        builder.include(southEast);
        builder.include(northWest);
        IconGenerator icnGenerator = new IconGenerator(mCurrentActivity);
        icnGenerator.setTextAppearance(R.style.iconGenText);
//        icnGenerator.setBackground(TRANSPARENT_DRAWABLE);
        icnGenerator.setContentPadding(4, 4, 4, 4);
        Bitmap icon = icnGenerator.makeIcon("TIME 1PM TO 3PM");
        MarkerOptions markerOptions = new MarkerOptions().position(builder.build().getCenter())
                .icon(BitmapDescriptorFactory.fromBitmap(icon)).anchor(0.5f, 0.5f);
        mGoogleMap.addMarker(markerOptions);*/


    }


    private OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            //check if fragment is replaced before map is ready
            if (mCurrentActivity == null || getView() == null) {
                return;
            }
            mGoogleMap = googleMap;

            createDemandShapeonGoogleMap(mGoogleMap);

            Utils.formatMap(mGoogleMap);
            mGoogleMap.clear();
            if (mCurrentActivity != null && !Permissions.hasLocationPermissions(mCurrentActivity)) {
                Permissions.getLocationPermissions(HomeFragmentTesting.this);
            } else {
                if (enableLocation()) return;
            }
            if (AppPreferences.getLatitude() != 0.0 &&
                    AppPreferences.getLongitude() != 0.0)
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(AppPreferences.getLatitude()
                                , AppPreferences.getLongitude())
                        , 12.0f));

            if (mCurrentActivity != null &&
                    null != mCurrentActivity.getIntent() &&
                    null != mCurrentActivity.getIntent().getExtras() &&
                    mCurrentActivity.getIntent().getBooleanExtra("isCancelledTrip", false) &&
                    !Dialogs.INSTANCE.isShowing()) {
                if (!mCurrentActivity.isDialogShown() && getView() != null) {
                    mCurrentActivity.setDialogShown(true);
                    if (mCurrentActivity.getIntent().getBooleanExtra("isCanceledByAdmin", false)) {
                        String message = mCurrentActivity.getIntent().getStringExtra("cancelMsg");
                        Dialogs.INSTANCE.showAlertDialogNotSingleton(mCurrentActivity,
                                new StringCallBack() {
                                    @Override
                                    public void onCallBack(String msg) {
                                    }
                                }, null, "Booking Cancelled", StringUtils.isNotBlank(message) ? message : "");
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

//            ArrayList<HeatMapUpdatedResponse> data = new Gson().fromJson(getString(R.string.heat_map_data), new TypeToken<ArrayList<HeatMapUpdatedResponse>>() {
//            }.getType());
//            updateHeatMapUI(data);
        }
    };

    private void createDemandShapeonGoogleMap(GoogleMap map) {
        try{

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void createDashedLine(GoogleMap map, LatLng latLngOrig, LatLng latLngDest, int color){
        double difLat = latLngDest.latitude - latLngOrig.latitude;
        double difLng = latLngDest.longitude - latLngOrig.longitude;

        double zoom = map.getCameraPosition().zoom;

        double divLat = difLat / (zoom * 2);
        double divLng = difLng / (zoom * 2);

        LatLng tmpLatOri = latLngOrig;

        for(int i = 0; i < (zoom * 2); i++){
            LatLng loopLatLng = tmpLatOri;

            if(i > 0){
                loopLatLng = new LatLng(tmpLatOri.latitude + (divLat * 0.25f), tmpLatOri.longitude + (divLng * 0.25f));
            }

            Polyline polyline = map.addPolyline(new PolylineOptions()
                    .add(loopLatLng)
                    .add(new LatLng(tmpLatOri.latitude + divLat, tmpLatOri.longitude + divLng))
                    .color(color)
                    .width(5f));

            tmpLatOri = new LatLng(tmpLatOri.latitude + divLat, tmpLatOri.longitude + divLng);
        }
    }

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

    private void setDestination() {
        if (AppPreferences.getDriverDestination() != null &&
                StringUtils.isNotBlank(AppPreferences.getDriverDestination().address)){
            muntakhibTv.setText(AppPreferences.getDriverDestination().address);
        }

    }

    @OnClick ( { R.id.shahkarBtn, R.id.statsBtn, R.id.editBtn, R.id.durationTv, R.id.durationBtn, R.id.previusDurationBtn } )
    public void onClick(View view){
        switch (view.getId()){

            case R.id.previusDurationBtn:{
                Dialogs.INSTANCE.setlastWeek(durationTv);
                durationBtn.setVisibility(View.VISIBLE);
                previusDurationBtn.setVisibility(View.GONE);
                break;
            }
            case R.id.durationBtn:{
                Dialogs.INSTANCE.setCalenderCurrentWeek(durationTv);
                durationBtn.setVisibility(View.GONE);
                previusDurationBtn.setVisibility(View.VISIBLE);
                break;
            }

            case R.id.shahkarBtn:{
                //view.startAnimation(AnimationUtils.loadAnimation(mCurrentActivity, R.anim.fade_in));
                ActivityStackManager.getInstance().startShahkarActivity(mCurrentActivity);
                break;
            }

            case R.id.statsBtn:{
                //view.startAnimation(AnimationUtils.loadAnimation(mCurrentActivity, R.anim.fade_in));
                ActivityStackManager.getInstance().startStatsActivity(mCurrentActivity);
                break;
            }

            case R.id.editBtn:{
                //view.startAnimation(AnimationUtils.loadAnimation(mCurrentActivity, R.anim.fade_in));
                if (mLastClickTime != 0 && (SystemClock.elapsedRealtime() - mLastClickTime < 1000)) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                setHomeLocation();
                break;
            }
        }
    }



    private void setHomeLocation() {
        if (!AppPreferences.getAvailableStatus()) {
            Intent returndropoffIntent = new Intent(mCurrentActivity, SelectPlaceActivity.class);
            returndropoffIntent.putExtra(Constants.Extras.SELECTED_ITEM, AppPreferences.getDriverDestination());
            returndropoffIntent.putExtra("from", Constants.CONFIRM_DROPOFF_REQUEST_CODE);
            startActivityForResult(returndropoffIntent, Constants.CONFIRM_DROPOFF_REQUEST_CODE);
        }
//        ActivityStackManager.getInstance(mCurrentActivity).startConfirmDestActivity(mCurrentActivity, "Confirm Destination", "Search Destination");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mCurrentActivity != null) {
            if (requestCode == Constants.CONFIRM_DROPOFF_REQUEST_CODE && data != null) {
                if (resultCode == RESULT_OK) {
                    PlacesResult mDropOff = data.getParcelableExtra(Constants.CONFIRM_DROPOFF_ADDRESS_RESULT);
                    AppPreferences.setDriverDestination(mDropOff);
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isScreenInFront = false;
        mCurrentActivity.unregisterReceiver(myReceiver);
//        if (countDownTimer != null) {
//            countDownTimer.cancel();
//        }
        if (mapView != null) {
            mapView.onPause();
        }
        clearMap();
    }

    @Override
    public void onStop() {
        if (mapView != null) {
            mapView.onStop();
        }
        isScreenInFront = false;
        super.onStop();
        clearMap();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clearMap();
        //Utils.unbindDrawables(rlMain);
//        mCurrentActivity.showToolbar();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null)
            mapView.onDestroy();
    }

    private boolean clearMap() {
        if (mGoogleMap != null) {
            if (ActivityCompat.checkSelfPermission(mCurrentActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mCurrentActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return true;
            }
            mGoogleMap.setMyLocationEnabled(false);
            mGoogleMap.clear();
        }
        return false;
    }



}

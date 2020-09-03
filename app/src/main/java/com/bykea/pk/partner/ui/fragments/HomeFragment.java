package com.bykea.pk.partner.ui.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.Notifications;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.communication.socket.WebIORequestHandler;
import com.bykea.pk.partner.dal.source.JobsDataSource;
import com.bykea.pk.partner.dal.source.JobsRepository;
import com.bykea.pk.partner.dal.source.remote.response.TemperatureSubmitResponse;
import com.bykea.pk.partner.dal.util.Injection;
import com.bykea.pk.partner.models.data.MultiDeliveryCallDriverData;
import com.bykea.pk.partner.models.data.PilotData;
import com.bykea.pk.partner.models.data.PlacesResult;
import com.bykea.pk.partner.models.response.BatchBooking;
import com.bykea.pk.partner.models.response.CheckDriverStatusResponse;
import com.bykea.pk.partner.models.response.DriverDestResponse;
import com.bykea.pk.partner.models.response.DriverPerformanceResponse;
import com.bykea.pk.partner.models.response.DriverStatsResponse;
import com.bykea.pk.partner.models.response.DriverVerifiedBookingResponse;
import com.bykea.pk.partner.models.response.HeatMapUpdatedResponse;
import com.bykea.pk.partner.models.response.LocationResponse;
import com.bykea.pk.partner.models.response.MultiDeliveryTrip;
import com.bykea.pk.partner.models.response.MultipleDeliveryBookingResponse;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.models.response.PilotStatusResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.bykea.pk.partner.ui.activities.SelectPlaceActivity;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.DrawPolygonAsync;
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
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.bykea.pk.partner.utils.Constants.ScreenRedirections.HOME_SCREEN_S;

/**
 * Home landing screen which holds all the options for driver
 */
public class HomeFragment extends Fragment {
    //region Variables and Widgets
    private Unbinder unbinder;
    private HomeActivity mCurrentActivity;
    private long mLastClickTime;
    private GoogleMap mGoogleMap;
    private UserRepository repository;
    private boolean isScreenInFront;
    private Location mPrevLocToShow;
    private int[] cashInHand;
    @BindView(R.id.previusDurationBtn)
    ImageView previusDurationBtn;
    @BindView(R.id.durationBtn)
    ImageView durationBtn;
    @BindView(R.id.myRangeBar)
    MyRangeBarRupay myRangeBar;
    @BindView(R.id.llBottom)
    FrameLayout myRangeBarLayout;
    @BindView(R.id.line)
    View myRangeBarTopLine;
    @BindView(R.id.mapPinIv)
    FrameLayout mapPinIv;
    @BindView(R.id.offlineRideNavigationRL)
    LinearLayout selectedAmountRL;
    @BindView(R.id.weeklybookingTv)
    FontTextView weeklyBookingTv;
    @BindView(R.id.homeMapFragment)
    MapView mapView;
    @BindView(R.id.muntakhibTv)
    FontTextView muntakhibTv;
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
    @BindView(R.id.muntakhibTv1)
    FontTextView muntakhibTv1;
    @BindView(R.id.mukamalBookingTv)
    FontTextView weeklyMukamalBookingTv;
    @BindView(R.id.kamaiTv)
    FontTextView weeklyKamaiTv;
    @BindView(R.id.wqtTv)
    FontTextView weeklyTimeTv;
    @BindView(R.id.takmeelTv)
    FontTextView weeklyTakmeelTv;
    @BindView(R.id.qboliyatTv)
    FontTextView weeklyQaboliatTv;
    @BindView(R.id.ratingTv)
    FontTextView weeklyratingTv;
    @BindView(R.id.totalScoreTv)
    FontTextView totalScoreTv;
    @BindView(R.id.totalBalanceTv)
    FontTextView totalBalanceTv;
    @BindView(R.id.driverImageView)
    ImageView driverImageView;
    @BindView(R.id.muntakhibTvUrdu)
    FontTextView muntakhibTvUrdu;
    @BindView(R.id.authorizedbookingTimeTv)
    FontTextView authorizedbookingTimeTv;
    @BindView(R.id.authorizedbookingTv)
    FontTextView authorizedbookingTv;

    private int WEEK_STATUS = 0;
    private boolean makeDriverOffline = false;
    private boolean isNavigateToOfflineRideRequired = false;
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;
    private boolean isCalled;
    private boolean handleUIChangeForInActive = true;
    private boolean isDialogDisplayingForBattery = false;
    private View view;
    private String currentVersion, latestVersion;
    private boolean isOfflineDialogVisible = false;
    private JobsRepository jobsRepo;
    private Dialog temperatureDialog;
    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_testing, container, false);
        unbinder = ButterKnife.bind(this, view);

        mCurrentActivity = ((HomeActivity) getActivity());
        mCurrentActivity.hideToolbarLogo();


        setInactiveStatusClick();

        setActiveStatusClick();


        mCurrentActivity.setDemandButtonForBismilla("ڈیمانڈ", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demandClick();
            }
        });
        mCurrentActivity.findViewById(R.id.toolbarLine).setVisibility(View.GONE);
        mCurrentActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        resetPositionOfMapPinAndSelectedCashView((int) getResources().getDimension(R.dimen._79sdp),
                (int) getResources().getDimension(R.dimen._110sdp));

        return view;
    }

    private void demandClick() {

        try {
            if (AppPreferences.getPilotData() != null && StringUtils.isNotBlank(AppPreferences.getPilotData().getService_type())
                    && AppPreferences.getPilotData().getService_type().equalsIgnoreCase("van")) {

                Fragment fragment = new DeliveryScheduleFragment();
                mCurrentActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.containerView, fragment)
                        .commit();
                //TODO : visibleFragmentNumber
//                HomeActivity.visibleFragmentNumber = 7;
                return;
            }

            if (AppPreferences.getDriverSettings() != null &&
                    AppPreferences.getDriverSettings().getData() != null &&
                    StringUtils.isNotBlank(AppPreferences.getDriverSettings().getData().getDemand())) {
                String demandLink = AppPreferences.getDriverSettings().getData().getDemand();
                String replaceString = demandLink.replace(Constants.REPLACE_CITY, StringUtils.capitalize(AppPreferences.getPilotData().getCity().getName()));
                Utils.startCustomWebViewActivity(mCurrentActivity, replaceString, "Demand");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * This method sets Click onLoadBoardListFragmentInteractionListener on Khuda Hafiz Logo/Inactive Button
     */
    private void setInactiveStatusClick() {
        mCurrentActivity.setToolbarLogoKhudaHafiz(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Connectivity.isConnectedFast(mCurrentActivity)) {
                    if (AppPreferences.getAvailableStatus()) {
                        AppPreferences.setAvailableStatus(false);
                        isOfflineDialogVisible = true;
                        Dialogs.INSTANCE.showNegativeAlertDialog(mCurrentActivity, getString(R.string.offline_msg_ur), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                isOfflineDialogVisible = false;
                                makeDriverOffline = true;
                                getDriverPerformanceData();
                                Dialogs.INSTANCE.dismissDialog();
                                callAvailableStatusAPI(false);
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                isOfflineDialogVisible = false;
                                Dialogs.INSTANCE.dismissDialog();
                                AppPreferences.setAvailableStatus(true);
                            }
                        });
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            // TODO call battery optimization
                            boolean calledOptimize = Utils.disableBatteryOptimization(mCurrentActivity,
                                    HomeFragment.this);
                            if (!calledOptimize) {
                                handleActivationStatusForBattery(true);
                            }
                        } else {
                            handleUIChangeForInActive = true;
                            handleActivationStatusForBattery(true);
                        }

                    }


                } else {
                    Dialogs.INSTANCE.showError(mCurrentActivity
                            , mapPinIv, getString(R.string.error_internet_connectivity));
                }
            }
        });
    }

    /**
     * This method sets Click onLoadBoardListFragmentInteractionListener on Bismillah Logo/Active Button
     */
    private void setActiveStatusClick() {
        mCurrentActivity.setToolbarLogoBismilla(v -> {
            if (Utils.isGpsEnable()) {
                if (Connectivity.isConnectedFast(mCurrentActivity)) {
                    if (Utils.isPartnerTemperatureRequired()) {
                        if (temperatureDialog == null) {
                            temperatureDialog = Dialogs.INSTANCE.showTemperatureDialog(mCurrentActivity, new StringCallBack() {
                                @Override
                                public void onCallBack(String msg) {
                                    Dialogs.INSTANCE.showLoader(mCurrentActivity);
                                    jobsRepo.submitTemperature(Float.parseFloat(msg), new JobsDataSource.LoadDataCallback<TemperatureSubmitResponse>() {
                                        @Override
                                        public void onDataLoaded(TemperatureSubmitResponse response) {
                                            Dialogs.INSTANCE.dismissDialog(temperatureDialog);
                                            AppPreferences.setLastPartnerTemperatureSubmitTime(System.currentTimeMillis());
                                            setDriverStatusActive();
                                            temperatureDialog = null;
                                        }

                                        @Override
                                        public void onDataNotAvailable(int errorCode, @NotNull String reasonMsg) {
                                            Dialogs.INSTANCE.dismissDialog();
                                            Dialogs.INSTANCE.showToast(reasonMsg);
                                        }
                                    });
                                }
                            });
                        }
                        /* Null Handling Because If Context Of The Activity Is Going To Finish
                           Then The Dialog Creation Function Will Return Null, Local Instance
                           Is Created Because If Temperature Dialog Has To Be Dismiss On Server Response*/
                        if (temperatureDialog != null && !temperatureDialog.isShowing()) {
                            temperatureDialog.show();
                        }
                    } else {
                        setDriverStatusActive();
                    }
                } else {
                    Dialogs.INSTANCE.showError(mCurrentActivity
                            , mapPinIv, getString(R.string.error_internet_connectivity));
                }
            } else {
                Dialogs.INSTANCE.showLocationSettings(mCurrentActivity,
                        Permissions.LOCATION_PERMISSION);
            }
        });
    }

    private void setDriverStatusActive() {
        if (AppPreferences.getAvailableStatus()) {
            Dialogs.INSTANCE.showNegativeAlertDialog(mCurrentActivity, getString(R.string.offline_msg_ur), v -> {
                Dialogs.INSTANCE.dismissDialog();
                callAvailableStatusAPI(false);
            }, null);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // TODO call battery optimization
                boolean calledOptimize = Utils.disableBatteryOptimization(
                        mCurrentActivity, HomeFragment.this);
                if (!calledOptimize) {
                    handleActivationStatusForBattery(false);
                }
            } else {
                handleUIChangeForInActive = false;
                handleActivationStatusForBattery(false);
            }
        }
    }

    private void callAvailableStatusAPI(boolean status) {
        if (Connectivity.isConnectedFast(mCurrentActivity)) {
            Dialogs.INSTANCE.showLoader(mCurrentActivity);
            AppPreferences.setAvailableAPICalling(true);
            repository.requestDriverUpdateStatus(mCurrentActivity, handler, status);
            //repository.requestUpdateStatus(mCurrentActivity, handler, status);
        }
    }

    /***
     * Handle UI logic and API status call for driver availability according to
     * ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS check if user allowed it.
     * Only then we would call status API and update respective UI
     *
     * @param handleForInactive should update UI for Inactive/Active
     * @see Settings#ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
     */
    private void handleActivationStatusForBattery(boolean handleForInactive) {
        callAvailableStatusAPI(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


//        mapView.onResume();
        try {

            mapView.onCreate(savedInstanceState);

            MapsInitializer.initialize(mCurrentActivity.getApplicationContext());
        } catch (Exception e) {
            Utils.redLog("HomeScreenException", e.getMessage());
            e.printStackTrace();
        }
        mapView.getMapAsync(mapReadyCallback);
        //mCurrentActivity.hideToolbarTitle();

        repository = new UserRepository();
        jobsRepo = Injection.INSTANCE.provideJobsRepository(DriverApp.getContext());


        checkGooglePlayService();

        Dialogs.INSTANCE.setCalenderCurrentWeek(durationTv);


        initRangeBar();
        AppPreferences.setAvailableAPICalling(false);
    }

    /**
     * this can be call to get partner booking stats data from kronos
     */
    private void updateVerifiedBookingStats() {
        repository.requestDriverVerifiedBookingStats(mCurrentActivity, WEEK_STATUS, handler);
    }

    private void getDriverPerformanceData() {
        try {
            updateVerifiedBookingStats();
//            if (!isCalled) {


            Dialogs.INSTANCE.showLoader(mCurrentActivity);
            repository.requestDriverPerformance(mCurrentActivity, handler, WEEK_STATUS);
            isCalled = true;
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onApiResponse(DriverPerformanceResponse response) {
        if (mCurrentActivity != null) {

            if (response != null && response.getData() != null) {
                if (StringUtils.isNotBlank(AppPreferences.getPilotData().getPilotImage())) {
                    Utils.loadImgPicasso(driverImageView, R.drawable.profile_pic,
                            Utils.getImageLink(AppPreferences.getPilotData().getPilotImage()));
                }
                if (weeklyBookingTv != null)
                    weeklyBookingTv.setText(String.valueOf(response.getData().getDriverBooking()));

                if (weeklyMukamalBookingTv != null)
                    weeklyMukamalBookingTv.setText(String.valueOf(response.getData().getCompletedBooking()));

                try {
                    if (weeklyKamaiTv != null) {
                        String weeklyBalance = Integer.valueOf(response.getData().getWeeklyBalance()) < 0 ? "0" :
                                response.getData().getWeeklyBalance();
                        weeklyKamaiTv.setText(weeklyBalance);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                if (weeklyTimeTv != null)
                    weeklyTimeTv.setText(String.valueOf(response.getData().getDriverOnTime()));

                if (weeklyTakmeelTv != null)
                    weeklyTakmeelTv.setText(response.getData().getCompletedPercentage() + getString(R.string.percentage_sign));
                if (weeklyQaboliatTv != null)
                    weeklyQaboliatTv.setText(response.getData().getAcceptancePercentage() + getString(R.string.percentage_sign));
                if (weeklyratingTv != null)
                    weeklyratingTv.setText(String.valueOf(response.getData().getWeeklyRating()));

                if (totalBalanceTv != null)
                    totalBalanceTv.setText(getString(R.string.rs) + response.getData().getTotalBalance());

                if (response.getData().getScore() != null && totalScoreTv != null) {
                    if (response.getData().getScore().contains(getString(R.string.minus_sign))) {
                        totalScoreTv.setText(response.getData().getScore());
                    } else {
                        totalScoreTv.setText(getString(R.string.score_urdu) + response.getData().getScore());
                    }
                }


            }

            if (!isDialogDisplayingForBattery)
                Dialogs.INSTANCE.dismissDialog();
        }
    }

    private void checkGooglePlayService() {
        Utils.checkGooglePlayServicesVersion(mCurrentActivity);
    }

    private void onUnauthorizedLicenceExpire() {
        Utils.clearData(mCurrentActivity);
        HomeActivity.visibleFragmentNumber = HOME_SCREEN_S;
        Dialogs.INSTANCE.showAlertDialogNotSingleton(mCurrentActivity, new StringCallBack() {
                    @Override
                    public void onCallBack(String msg) {
                        //ActivityStackManager.getInstance().startLoginActivity(mCurrentActivity);
                        ActivityStackManager.getInstance().startLandingActivity(mCurrentActivity);

                        mCurrentActivity.finish();
                    }
                }, null, getString(R.string.licence_expire_title),
                getString(R.string.licence_expire_message));
    }

    private void initViews() {
        mCurrentActivity.setPilotData(AppPreferences.getPilotData());
        if (StringUtils.isNotBlank(mCurrentActivity.getPilotData().getLicenseExpiry()) && Utils.isLicenceExpired(mCurrentActivity.getPilotData().getLicenseExpiry())) {
            onUnauthorizedLicenceExpire();
        }

        setStatusBtn();
        mCurrentActivity.setConnectionStatus();
        myRangeBar.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (myRangeBar == null || myRangeBar.getViewTreeObserver() == null || !myRangeBar.getViewTreeObserver().isAlive())
                    return true;
                myRangeBar.getViewTreeObserver().removeOnPreDrawListener(this);
                myRangeBar.updateUI();
                return true;
            }
        });
//            callLoadBoardListingAPI();
    }

    public synchronized void setStatusBtn() {
        if (mCurrentActivity == null || getView() == null) {
            return;
        }
        if (!AppPreferences.getAvailableStatus()) {
            mCurrentActivity.hideLoadBoardBottomSheet();

            //inactive state
            getDriverPerformanceData();

//            mCurrentActivity.isVisibleFirstTime = true;
            myRangeBarLayout.setVisibility(View.VISIBLE);
            myRangeBarTopLine.setVisibility(View.VISIBLE);
            myRangeBar.setEnabled(true);
            mapPinIv.setVisibility(View.GONE);
            selectedAmountRL.setVisibility(View.GONE);
            mapView.setVisibility(View.GONE);
            headerTopActiveLayout.setVisibility(View.GONE);
            headerTopUnActiveLayout.setVisibility(View.VISIBLE);
            mCurrentActivity.showBismillah();
            layoutUpper.setVisibility(View.VISIBLE);
            layoutDuration.setVisibility(View.VISIBLE);
            driverStatsLayout.setVisibility(View.VISIBLE);

            if (AppPreferences.getDriverDestination() == null) {
                muntakhibTv.setText(getResources().getString(R.string.muntakhib_text_urdu));
                muntakhibTv1.setText("");
                muntakhibTv.setAttr(mCurrentActivity.getApplicationContext(), "jameel_noori_nastaleeq.ttf");
            } else {

                muntakhibTv.setText(AppPreferences.getDriverDestination().address);
                muntakhibTv.setAttr(mCurrentActivity.getApplicationContext(), "open_sans_regular.ttf");
                muntakhibTv1.setText(AppPreferences.getDriverDestination().address);

            }
        } else {        //active state
            mCurrentActivity.showLoadBoardBottomSheet();

            myRangeBarLayout.setVisibility(View.INVISIBLE);
            myRangeBarTopLine.setVisibility(View.INVISIBLE);
            myRangeBar.setEnabled(false);
            mCurrentActivity.showKhudaHafiz();
            mapView.setVisibility(View.VISIBLE);
            mapPinIv.setVisibility(View.VISIBLE);
            headerTopActiveLayout.setVisibility(View.VISIBLE);
            headerTopUnActiveLayout.setVisibility(View.GONE);
            layoutUpper.setVisibility(View.GONE);
            layoutDuration.setVisibility(View.GONE);
            driverStatsLayout.setVisibility(View.GONE);

            if (null != AppPreferences.getDriverDestination()) {

                muntakhibTv.setText(AppPreferences.getDriverDestination().address);
                muntakhibTv.setAttr(mCurrentActivity.getApplicationContext(), "open_sans_regular.ttf");
                muntakhibTv1.setAttr(mCurrentActivity.getApplicationContext(), "open_sans_regular.ttf");
                muntakhibTv1.setText(AppPreferences.getDriverDestination().address);
                muntakhibTvUrdu.setText(getResources().getString(R.string.muntakhib_manzil_urdu));
            } else {
                muntakhibTvUrdu.setText(getResources().getString(R.string.muntakhib_manzil_krey_urdu));
                muntakhibTv1.setText(getResources().getString(R.string.address_not_set_urdu));
                muntakhibTv1.setAttr(mCurrentActivity.getApplicationContext(), "jameel_noori_nastaleeq.ttf");
            }
            selectedAmountRL.setVisibility(View.VISIBLE);
            if (mGoogleMap != null)
                mGoogleMap.setPadding(0, 0, 0, (int) getResources().getDimension(R.dimen._16sdp));
        }

        if (AppPreferences.isWalletAmountIncreased()) {
            setFenceError(AppPreferences.getWalletIncreasedError());
        } else if (AppPreferences.isOutOfFence()) {
            setFenceError("Non Service Area");
        } else {
            tvFenceError.setVisibility(View.GONE);
            mCurrentActivity.toggleAchaConnection(View.VISIBLE);
        }

        makeDriverOffline = false;
    }

    private void setFenceError(String errorMessage) {
        tvFenceError.setText(errorMessage);
        tvFenceError.setVisibility(View.VISIBLE);
        mCurrentActivity.toggleAchaConnection(View.GONE);
    }

    @Override
    public void onResume() {
        try {
            mapView.onResume();
        } catch (Exception e) {
            e.printStackTrace();
        }

        isScreenInFront = true;

        Notifications.removeAllNotifications(mCurrentActivity);

        //Utils.setCallIncomingStateWithoutRestartingService();
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
//        Dialogs.INSTANCE.setCalenderCurrentWeek(durationTv);
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
                        if (intent != null && intent.getAction() != null &&
                                intent.getAction().equalsIgnoreCase(Keys.LOCATION_UPDATE_BROADCAST)) {
                            if (intent.getExtras() != null) {
                                Location location = intent.getParcelableExtra("location");
                                //Move Map's Camera if there's significant change in Location
                                if (location != null && (mPrevLocToShow == null || location.distanceTo(mPrevLocToShow) > 30)) {
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

        if (getActivity().getIntent().getStringExtra("isLogin") != null) {
            currentIndex = Constants.RESET_CASH_TO_DEFAULT_POSITION;
            AppPreferences.setCashInHands(cashInHand[Constants.RESET_CASH_TO_DEFAULT_POSITION]);
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

    /***
     * Reset slider value for cash in hand if current set amount is less then 1000.
     */
    private void resetCashSliderToDefault() {
        cashInHand = AppPreferences.getCashInHandsRange();
        int currentCashValue = AppPreferences.getCashInHands();
        if (currentCashValue < Constants.RESET_CASH_TO_DEFAULT_AMOUNT) {
            AppPreferences.setCashInHands(cashInHand[Constants.RESET_CASH_TO_DEFAULT_POSITION]);
            myRangeBar.setCurrentIndex(Constants.RESET_CASH_TO_DEFAULT_POSITION);
            myRangeBar.setInitialIndex(Constants.RESET_CASH_TO_DEFAULT_POSITION);
        }
        myRangeBarLayout.setVisibility(View.VISIBLE);
    }

    private UserDataHandler handler = new UserDataHandler() {

        @Override
        public void onDriverVerifiedBookingResponse(DriverVerifiedBookingResponse driverVerifiedBookingResponse) {
            super.onDriverVerifiedBookingResponse(driverVerifiedBookingResponse);
            if (mCurrentActivity == null || getView() == null
                    || !driverVerifiedBookingResponse.isSuccess() || driverVerifiedBookingResponse.getData() == null)
                return;
            authorizedbookingTimeTv.setText(Utils.getFormattedDate("dd MMM",
                    Utils.getTimeInMiles(driverVerifiedBookingResponse.getData().getBookingsTime())));
            authorizedbookingTv.setText(String.valueOf(driverVerifiedBookingResponse.getData().getBookingsCount()));
        }

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
        public void onDriverPerformanceResponse(DriverPerformanceResponse response) {
            AppPreferences.setObjectToSharedPref(response);
            onApiResponse(response);
        }

        @Override
        public void onRunningTrips(final CheckDriverStatusResponse response) {
            if (mCurrentActivity != null && getView() != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccess()) {
                            try {
                                if (response.getData().getTrip() == null) {
                                    Utils.setCallIncomingState();
                                    return;
                                }

                                checkTripType(response);

                            } catch (NullPointerException ignored) {

                            }
                        } else {
                            //TODO need to remove this when backend properly send unauthorized HTTP code
                            if (response.getCode() == HttpURLConnection.HTTP_BAD_REQUEST
                                    && response.getMessage().contentEquals(Constants.UNAUTH_MESSAGE)) {
                                Utils.onUnauthorized(mCurrentActivity);
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
                        if (!isDialogDisplayingForBattery)
                            Dialogs.INSTANCE.dismissDialog();
                        if (pilotStatusResponse.isSuccess()) {
                            AppPreferences.setCash(pilotStatusResponse.getPilotStatusData().isCashValue());
                            if (makeDriverOffline) {
                                AppPreferences.setAvailableStatus(false);
                                makeDriverOffline = false;
                            } else {
                                AppPreferences.setAvailableStatus(!AppPreferences.getAvailableStatus());
                            }
                            AppPreferences.setAvailableAPICalling(false);
                            if (AppPreferences.getAvailableStatus()) {
                                //  Below broadcast has send to update the loadboard bookings request
                                mCurrentActivity.sendBroadcast(new Intent(Constants.Broadcast.UPDATE_LOADBOARD_BOOKINGS_REQUEST));

                                ActivityStackManager.getInstance().startLocationService(mCurrentActivity);
                                //Todo Need to update Server Time difference when status API returns Timestamp for now Calling location API to force update timestamp
                                //Utils.saveServerTimeDifference(response.body().getTimeStampServer());

                                forceUpdatedLocationOnDriverStatus();
                                if (AppPreferences.isWalletAmountIncreased()) {
                                    AppPreferences.setWalletAmountIncreased(false);
                                }
                                if (AppPreferences.isOutOfFence()) {
                                    AppPreferences.setOutOfFence(false);
                                }
                                if (AppPreferences.getIsCash()) {
                                    mCurrentActivity.showLoadBoardBottomSheet();
                                } else {
                                    mCurrentActivity.hideLoadBoardBottomSheet();
                                }
                            } else {
                                AppPreferences.setDriverDestination(null);
                                ActivityStackManager.getInstance().stopLocationService(mCurrentActivity);
                                //todo reset slider to 1000 amount when CIH amount is less then 1000
                                resetCashSliderToDefault();
                                if (isNavigateToOfflineRideRequired) {
                                    isNavigateToOfflineRideRequired = false;
                                    mCurrentActivity.recyclerViewAdapter.updateCurrentFragmentWithOffline();
                                }
                                mCurrentActivity.hideLoadBoardBottomSheet();
                            }
                            setStatusBtn();
                        } else {
                            handleDriverStatusErrorCase(pilotStatusResponse);
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
                        if (!isDialogDisplayingForBattery)
                            Dialogs.INSTANCE.dismissDialog();
                        if (errorCode == HTTPStatus.UNAUTHORIZED) {
                            Utils.onUnauthorized(mCurrentActivity);
                        } else {
                            Dialogs.INSTANCE.showToast(errorMessage);
                        }
                    }
                });
            }
        }
    };

    //region Handle Error cases for Driver Status API

    /***
     * Handle Driver status API Error case for API failures.
     * @param driverStatusResponse latest response from server.
     */
    private void handleDriverStatusErrorCase(PilotStatusResponse driverStatusResponse) {
        if (driverStatusResponse != null) {
            switch (driverStatusResponse.getCode()) {
                case Constants.ApiError.BUSINESS_LOGIC_ERROR: {
                    handleDriverStatusBusinessLogicErrors(driverStatusResponse);
                    break;
                }
                //TODO Will update unauthorized check on error callback when API team adds 401 status code in their middle layer.
                case HTTPStatus.UNAUTHORIZED: {
                    Utils.onUnauthorized(mCurrentActivity);
                    break;
                }
                default:
                    Utils.appToast(driverStatusResponse.getMessage());
                    AppPreferences.setAvailableStatus(false);
                    AppPreferences.setDriverDestination(null);
                    setStatusBtn();
            }
        }
    }

    /***
     * Handle business logic driver Failure use cases for driver status .
     * <ul>
     *     <li> IMEI not registered. </li>
     *     <li> Multiple cancellation block. </li>
     *     <li> Wallet amount exceeds threshold. </li>
     *     <li> Out of service region area block. </li>
     *     <li> Status change during rides. </li>
     * </ul>
     *
     * @param driverStatusResponse Latest response received from API Server
     */
    private void handleDriverStatusBusinessLogicErrors(PilotStatusResponse driverStatusResponse) {
        String displayErrorMessage;
        switch (driverStatusResponse.getSubCode()) {
            case Constants.ApiError.MULTIPLE_CANCELLATION_BLOCK:
                Dialogs.INSTANCE.showAlertDialogUrduWithTick(mCurrentActivity,
                        getString(R.string.frequent_booking_cancel_error_ur));
                break;
            case Constants.ApiError.IMEI_NOT_REGISTERED:
                Dialogs.INSTANCE.showImeiRegistrationErrorDialog(mCurrentActivity,
                        Utils.generateImeiRegistrationErrorMessage(mCurrentActivity),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityStackManager.getInstance().startComplainSubmissionActivity(mCurrentActivity, null, null);
                            }
                        });
                break;
            case Constants.ApiError.WALLET_EXCEED_THRESHOLD:
                Dialogs.INSTANCE.showAlertDialogUrduWithTick(mCurrentActivity,
                        getString(R.string.wallet_amount_exceed_error_ur));
                break;
            case Constants.ApiError.OUT_OF_SERVICE_REGION:
                Dialogs.INSTANCE.showRegionOutErrorDialog(mCurrentActivity,
                        Utils.getSupportHelplineNumber(),
                        getString(R.string.region_out_message_ur));
                break;

            case Constants.ApiError.DRIVER_ACCOUNT_BLOCKED:
                Dialogs.INSTANCE.showRegionOutErrorDialog(mCurrentActivity,
                        Utils.getSupportHelplineNumber(),
                        getString(R.string.account_blocked_message_ur));
                break;
            case Constants.ApiError.DRIVER_ACCOUNT_BLOCKED_BY_ADMIN:
                Dialogs.INSTANCE.showRegionOutErrorDialog(mCurrentActivity,
                        Utils.getSupportHelplineNumber(),
                        getString(R.string.account_blocked_wallet_amount_not_paid));
                break;
            case Constants.ApiError.APP_FORCE_UPDATE:
                Dialogs.INSTANCE.showUpdateAppDialog(mCurrentActivity,
                        getString(R.string.force_app_update_title),
                        getString(R.string.force_app_update_message_local_ur),
                        getString(R.string.force_app_update_link));
                break;
            case Constants.ApiError.STATUS_CHANGE_DURING_RIDE:
            default:
                Utils.appToast(driverStatusResponse.getMessage());
        }
        AppPreferences.setAvailableStatus(false);
        AppPreferences.setDriverDestination(null);
        setStatusBtn();
    }
    //endregion


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

            Utils.formatMap(mGoogleMap);
            mGoogleMap.clear();
            if (mCurrentActivity != null && !Permissions.hasLocationPermissions(mCurrentActivity)) {
                Permissions.getLocationPermissions(HomeFragment.this);
            } else {
                if (enableLocation()) return;
            }
            if (AppPreferences.getLatitude() != 0.0 &&
                    AppPreferences.getLongitude() != 0.0)
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(AppPreferences.getLatitude()
                                , AppPreferences.getLongitude())
                        , 12.0f));

            showCancelDialogIfRequired();
            if (AppPreferences.getIsCash()) {
                /*resetPositionOfMapPinAndSelectedCashView((int) getResources().getDimension(R.dimen._79sdp),
                        (int) getResources().getDimension(R.dimen._110sdp));*/
                setDriverLocation();
            } else {
                /*resetPositionOfMapPinAndSelectedCashView((int) getResources().getDimension(R.dimen._19sdp),
                        (int) getResources().getDimension(R.dimen._19sdp));*/
            }
        }
    };

    /**
     * This method checks if cancel dialog need to be shown or not by checking Intent Extras
     */
    private void showCancelDialogIfRequired() {
        if (Dialogs.INSTANCE.isShowing())
            Dialogs.INSTANCE.dismissDialog();

        if (mCurrentActivity != null &&
                null != mCurrentActivity.getIntent() &&
                null != mCurrentActivity.getIntent().getExtras() &&
                mCurrentActivity.getIntent().getBooleanExtra(Constants.Extras.IS_CANCELED_TRIP, false) &&
                !mCurrentActivity.isFinishing()) {
            if (!mCurrentActivity.isDialogShown() && getView() != null) {
                mCurrentActivity.setDialogShown(true);

                final Runnable runnable = playCancelNotificationSound();
                String cancelMsg = mCurrentActivity.getIntent().getBooleanExtra(Constants.Extras.IS_CANCELED_TRIP_BY_ADMIN, false)
                        ? mCurrentActivity.getString(R.string.cancel_notification_by_admin) : mCurrentActivity.getString(R.string.cancel_notification);
                Dialogs.INSTANCE.showCancelNotification(mCurrentActivity, cancelMsg, new StringCallBack() {
                    @Override
                    public void onCallBack(String msg) {
                        if (runnable != null) {
                            runnable.run();
                        }
                    }
                });
            }
        }
    }

    /**
     * This method plays an audio sound for 8 secs when cancel notification is displayed
     *
     * @return Runnable runnable handler to stop media player
     */
    private Runnable playCancelNotificationSound() {
        final MediaPlayer mediaPlayer = android.media.MediaPlayer
                .create(mCurrentActivity, R.raw.one);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mediaPlayer.stop();
                handler.removeCallbacks(this);
            }
        };
        handler.postDelayed(runnable, 8000);//millisec.

        return runnable;
    }

    private void addHeatMapPolyline() {
        Polyline polyline = mGoogleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .color(getResources().getColor(R.color.color_e73836))
                .add(
                        new LatLng(24.819258, 67.077928),
                        new LatLng(24.827061, 67.043024),
                        new LatLng(24.860397, 67.078386),
                        new LatLng(24.819258, 67.077928)));


        polyline.setStartCap(new RoundCap());
        polyline.setEndCap(new RoundCap());
        polyline.setWidth(6.0f);
        polyline.setJointType(JointType.ROUND);

        List<PatternItem> pattern = Arrays.<PatternItem>asList(
                new Dash(20), new Gap(10));

        polyline.setPattern(pattern);
    }


    private void addHeatMap() {
        int[] colors = new int[]{
                Color.rgb(102, 255, 0),
                Color.rgb(255, 0, 0)
        };

        float[] startpoints = {
                0.2f, 1f
        };

        Gradient gradient = new Gradient(colors, startpoints);

        List<LatLng> list = null;

        // Get the data: latitude/longitude positions of police stations.

        list = new ArrayList<>();


        list.add(new LatLng(24.819258, 67.077928));
        list.add(new LatLng(24.819258, 67.077928));

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        mProvider = new HeatmapTileProvider.Builder()
                .data(list)
                .gradient(gradient)
                .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        mOverlay = mGoogleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));


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
            mGoogleMap.setOnMyLocationChangeListener(location -> {
                Log.e("Location changed", "by google maps");
                AppPreferences.saveLocation(location.getLatitude(), location.getLongitude());
            });
        }
        return false;
    }


    @OnClick({R.id.shahkarBtn, R.id.statsBtn, R.id.editBtn, R.id.durationTv, R.id.durationBtn, R.id.previusDurationBtn,
            R.id.mapPinIv, R.id.walletRL, R.id.offlineRideNavigationRL})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.previusDurationBtn: {
                Dialogs.INSTANCE.setlastWeek(durationTv);
                durationBtn.setVisibility(View.VISIBLE);
                previusDurationBtn.setVisibility(View.GONE);
                WEEK_STATUS = -1;
                getDriverPerformanceData();
                break;
            }

            case R.id.mapPinIv: {
                setDriverLocation();
                break;
            }

            case R.id.durationBtn: {
                Dialogs.INSTANCE.setCalenderCurrentWeek(durationTv);
                durationBtn.setVisibility(View.GONE);
                previusDurationBtn.setVisibility(View.VISIBLE);
                WEEK_STATUS = 0;
                getDriverPerformanceData();
                break;
            }

            case R.id.shahkarBtn: {
                //view.startAnimation(AnimationUtils.loadAnimation(mCurrentActivity, R.anim.fade_in));
                ActivityStackManager.getInstance().startShahkarActivity(mCurrentActivity);
                break;
            }

            case R.id.statsBtn: {
                //view.startAnimation(AnimationUtils.loadAnimation(mCurrentActivity, R.anim.fade_in));
                ActivityStackManager.getInstance().startStatsActivity(mCurrentActivity);
                break;
            }

            case R.id.editBtn: {
                //view.startAnimation(AnimationUtils.loadAnimation(mCurrentActivity, R.anim.fade_in));
                if (mLastClickTime != 0 && (SystemClock.elapsedRealtime() - mLastClickTime < 1000)) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                setHomeLocation();
                break;
            }
            //open wallet screen
            case R.id.walletRL: {
                showWalletFragment();
                break;
            }
            // Open Offline Ride Navigation Acknowledgment Dialog
            case R.id.offlineRideNavigationRL: {
                AppPreferences.setAvailableStatus(false);
                isOfflineDialogVisible = true;
                Dialogs.INSTANCE.showNegativeAlertDialog(mCurrentActivity, getString(R.string.you_are_going_to_offline),
                        tickView -> {
                            isOfflineDialogVisible = false;
                            makeDriverOffline = true;
                            getDriverPerformanceData();
                            Dialogs.INSTANCE.dismissDialog();
                            callAvailableStatusAPI(false);
                            isNavigateToOfflineRideRequired = true;
                        },
                        crossView -> {
                            isOfflineDialogVisible = false;
                            Dialogs.INSTANCE.dismissDialog();
                            AppPreferences.setAvailableStatus(true);
                        });
                break;
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
            } else if (requestCode == Constants.BATTERY_OPTIMIZATION_RESULT) {
                if (resultCode == RESULT_OK) {
                    handleActivationStatusForBattery(handleUIChangeForInActive);
                } else if (resultCode == RESULT_CANCELED) {
                    isDialogDisplayingForBattery = true;
                    Dialogs.INSTANCE.showAlertDialogForBattery(mCurrentActivity,
                            getString(R.string.battery_optimize_error_title),
                            getString(R.string.battery_optimize_error_message),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    isDialogDisplayingForBattery = false;
                                    Dialogs.INSTANCE.dismissDialog();
                                }
                            });
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isOfflineDialogVisible) AppPreferences.setAvailableStatus(true);

        isScreenInFront = false;
        isCalled = false;
        if (myReceiver != null) {
            mCurrentActivity.unregisterReceiver(myReceiver);
        }
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
        temperatureDialog = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null)
            mapView.onDestroy();

        Dialogs.INSTANCE.dismissDialog();
    }

    private boolean clearMap() {
        if (mGoogleMap != null) {
            if (ActivityCompat.checkSelfPermission(mCurrentActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mCurrentActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permis``sions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return true;
            }
            mGoogleMap.setMyLocationEnabled(false);
            mGoogleMap.setOnMyLocationChangeListener(null);
            mGoogleMap.clear();
        }
        return false;
    }

    /***
     * Get Current App version and compare it with latest version returned by Setting API.
     * Show force app update dialog.
     */
    public void getCurrentVersion() {
        if (mCurrentActivity != null && getView() != null) {
            currentVersion = Utils.getAppCurrentVersion();
            if (AppPreferences.getSettings() != null
                    && AppPreferences.getSettings().getSettings() != null) {
                latestVersion = AppPreferences.getSettings().getSettings().getApp_version();
            }
            if (StringUtils.isNotBlank(latestVersion) && StringUtils.isNotBlank(currentVersion)) {
                Utils.redLog("VERSION", "Current: " + currentVersion + " Play Store: " + latestVersion);
                if (Double.parseDouble(currentVersion) < Double.parseDouble(latestVersion)) {
                    Dialogs.INSTANCE.showUpdateAppDialog(mCurrentActivity,
                            getString(R.string.force_app_update_title),
                            getString(R.string.force_app_update_message_local_ur),
                            getString(R.string.force_app_update_link));
                }
            }
        }

    }

    /***
     * Event subscribe for driver active inactive use case.
     * @param action Event action
     */
    public void onEvent(final String action) {
        if (mCurrentActivity != null && getView() != null) {
            mCurrentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (action.equalsIgnoreCase(Keys.CONNECTION_BROADCAST)) {
                        mCurrentActivity.setConnectionStatus();
                    } else if (action.equalsIgnoreCase(Keys.INACTIVE_PUSH) ||
                            action.equalsIgnoreCase(Keys.INACTIVE_FENCE)) {
                        AppPreferences.setDriverDestination(null);
                        if (Connectivity.isConnectedFast(mCurrentActivity)) {
                            makeDriverOffline = true;
                            callAvailableStatusAPI(false);
                        } else {
                            setStatusBtn();
                        }
                    } else if (action.equalsIgnoreCase(Keys.ACTIVE_FENCE)) {
                        setStatusBtn();
                    }
                }
            });

        }

    }

    /**
     * Open wallet fragment from In-Active home wallet icon's tap
     */
    private void showWalletFragment() {
        mCurrentActivity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.containerView, new WalletFragment())
                .commit();
        HomeActivity.visibleFragmentNumber = Constants.ScreenRedirections.WALLET_SCREEN_S;
    }

    /**
     * Check the Type of request is it batch request or single
     *
     * <p>
     * <p>
     * Check if the type is single parse the single trip object i.e {@link NormalCallData}
     * other wise parse the batch trip i.e {@link MultiDeliveryCallDriverData}
     * <p>
     * Check also for unfinished trips if there is unfinished trip remaining land
     * to "Feedback Screen" other wise booking screen according to the type
     *
     * <ul>
     * <li>Check if trip is null thats mean there is no active trip</li>
     * <li>Check if the type is {@linkplain Constants.CallType#SINGLE}</li>
     * <li>Check if the trip status is {@linkplain TripStatus#ON_FINISH_TRIP}</li>
     * </ul>
     *
     * </p>
     *
     * @param response The object of {@linkplain CheckDriverStatusResponse}
     */
    private void checkTripType(CheckDriverStatusResponse response) {
        Gson gson = new Gson();
        if (response.getData().getType()
                .equalsIgnoreCase(Constants.CallType.SINGLE)) {
            AppPreferences.setDeliveryType(Constants.CallType.SINGLE);
            String trip = gson.toJson(response.getData().getTrip());
            Type type = new TypeToken<NormalCallData>() {
            }.getType();
            NormalCallData callData = gson.fromJson(trip, type);
            AppPreferences.setCallData(callData);
            AppPreferences.setTripStatus(callData.getStatus());
            if (!callData.getStatus().
                    equalsIgnoreCase(TripStatus.ON_FINISH_TRIP)) {
                WebIORequestHandler
                        .getInstance()
                        .registerChatListener();
                ActivityStackManager
                        .getInstance()
                        .startJobActivity(mCurrentActivity);
            } else {
                ActivityStackManager
                        .getInstance()
                        .startFeedbackActivity(mCurrentActivity);
            }
        } else if (response.getData().getType()
                .equalsIgnoreCase(Constants.CallType.NEW_BATCH)) {

            AppPreferences.setDeliveryType(Constants.CallType.NEW_BATCH);

            String trip = gson.toJson(response.getData().getTrip());
            Type type = new TypeToken<NormalCallData>() {
            }.getType();

            NormalCallData callData = gson.fromJson(trip, type);
            if (StringUtils.isNotBlank(callData.getStarted_at())) {
                AppPreferences.setStartTripTime(
                        AppPreferences.getServerTimeDifference() +
                                Utils.getTimeInMiles(
                                        callData.getStarted_at())
                );
            }
            AppPreferences.setCallData(callData);
            AppPreferences.setTripStatus(callData.getStatus());

            // check for unfinished ride later
            ArrayList<BatchBooking> bookingResponseList = callData.getBookingList();

            boolean isFinishedStateFound = false;

            for (BatchBooking tripData : bookingResponseList) {
                // if trip status if "finished", open invoice screen
                if (tripData.getStatus().
                        equalsIgnoreCase(TripStatus.ON_FINISH_TRIP)) {
                    isFinishedStateFound = true;
                    ActivityStackManager.getInstance()
                            .startFeedbackActivity(mCurrentActivity);
                    break;
                }
            }
            //Navigate to booking screen if no pending invoices found
            if (!isFinishedStateFound)
                ActivityStackManager.
                        getInstance().
                        startJobActivity(mCurrentActivity);

        } else {
            AppPreferences.setDeliveryType(Constants.CallType.BATCH);
            String trip = gson.toJson(response.getData().getTrip());
            Type type = new TypeToken<MultiDeliveryCallDriverData>() {
            }.getType();
            MultiDeliveryCallDriverData multiDeliveryCallDriverData = gson.fromJson(trip, type);
            AppPreferences.
                    setMultiDeliveryCallDriverData(
                            multiDeliveryCallDriverData
                    );

            List<MultipleDeliveryBookingResponse> bookingResponseList =
                    multiDeliveryCallDriverData.getBookings();

            boolean isFinishedStateFound = false;

            for (MultipleDeliveryBookingResponse bookingResponse : bookingResponseList) {
                // get trip instance
                MultiDeliveryTrip tripData = bookingResponse.getTrip();

                // if trip status if "finished", open invoice screen
                if (tripData.getStatus().
                        equalsIgnoreCase(TripStatus.ON_FINISH_TRIP)) {
                    isFinishedStateFound = true;
                    ActivityStackManager.getInstance()
                            .startMultiDeliveryFeedbackActivity(mCurrentActivity,
                                    tripData.getId(), false);
                    break;
                }
            }

            //Navigate to booking screen if no pending invoices found
            if (!isFinishedStateFound)
                ActivityStackManager.
                        getInstance().
                        startMultiDeliveryBookingActivity(mCurrentActivity);
        }
        mCurrentActivity.finish();

    }


    /**
     * Reposition my location icon and google logo when loadboard visible/gone
     *
     * @param locationPointerBottomMargin my location bottom margin
     * @param googleMapLogoBottomPadding  google logo padding from bottom
     */
    public void resetPositionOfMapPinAndSelectedCashView(int locationPointerBottomMargin, int googleMapLogoBottomPadding) {
        if (mapPinIv != null) {
            RelativeLayout.LayoutParams myLocationPointerParams = (RelativeLayout.LayoutParams) mapPinIv.getLayoutParams();
            myLocationPointerParams.bottomMargin = locationPointerBottomMargin;
            mapPinIv.setLayoutParams(myLocationPointerParams);
        }

        if (selectedAmountRL != null) {
            RelativeLayout.LayoutParams selectedAmountTVLayoutParams = (RelativeLayout.LayoutParams) selectedAmountRL.getLayoutParams();
            selectedAmountTVLayoutParams.bottomMargin = locationPointerBottomMargin;
            selectedAmountRL.setLayoutParams(selectedAmountTVLayoutParams);
        }


        if (mGoogleMap != null) {
            mGoogleMap.setPadding(0, 0, 0, googleMapLogoBottomPadding);
        }

    }


    /**
     * Forcefully sending Location Update API on Server for Updating
     * {@link AppPreferences#setServerTimeDifference} against Time stamp provided by Server.
     */
    private void forceUpdatedLocationOnDriverStatus() {
        if (Utils.isConnected(getActivity(), false)) {
            new UserRepository().requestLocationUpdate(DriverApp.getApplication(), new UserDataHandler() {

                @Override
                public void onLocationUpdate(LocationResponse response) {

                }

                @Override
                public void onError(int errorCode, String errorMessage) {
                }
            }, AppPreferences.getLatitude(), AppPreferences.getLongitude());
        }
    }
}

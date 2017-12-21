package com.bykea.pk.partner.ui.fragments;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
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
import com.bykea.pk.partner.models.response.HeatMapUpdatedResponse;
import com.bykea.pk.partner.ui.activities.ConfirmDropOffAddressActivity;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.bykea.pk.partner.ui.helpers.DrawPolygonAsync;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import com.google.maps.android.ui.IconGenerator;
import com.squareup.okhttp.internal.Util;

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
    LinearLayout rl_destinationSelected;
    @Bind(R.id.rl_main_destination)
    RelativeLayout rl_main_destination;
    @Bind(R.id.tv_destinationName)
    AutoFitFontTextView tv_destinationName;

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
        switch (connectionStatus) {
            case "Unknown Status":
                tvConnectionStatus.setBackgroundColor(ContextCompat.getColor(mCurrentActivity, R.color.textColorSecondary));
                break;
            case "Battery Low":
                tvConnectionStatus.setBackgroundColor(ContextCompat.getColor(mCurrentActivity, R.color.color_error));
                tvConnectionStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.low_battery_icon, 0, 0, 0);
                break;
            case "Poor Connection":
            case "Fair Connection":
            case "No Connection":
                tvConnectionStatus.setBackgroundColor(ContextCompat.getColor(mCurrentActivity, R.color.color_fair_connection));
                break;
            case "Good Connection":
                tvConnectionStatus.setBackgroundColor(ContextCompat.getColor(mCurrentActivity, R.color.colorPrimary));
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
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            setConnectionStatus();
            getHeatMapData();
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
                if (ActivityCompat.checkSelfPermission(mCurrentActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    private Bitmap writeTextOnDrawable(int drawableId, String text) {

        Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId)
                .copy(Bitmap.Config.ARGB_8888, true);

        Typeface tf = Typeface.create("Helvetica", Typeface.NORMAL);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(convertToPixels(mCurrentActivity, 18));

        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(bm);

        //If the text is bigger than the canvas , reduce the font size
        if (textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
            paint.setTextSize(convertToPixels(mCurrentActivity, 7));        //Scaling needs to be used for different dpi's

        //Calculate the positions
//        int xPos = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset

        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
        int center = (int) ((canvas.getHeight() / 3) - ((paint.descent() + paint.ascent()) / 2));

        canvas.drawText(text, center, center, paint);

        return bm;
    }

    public static int convertToPixels(Context context, int nDP) {
        final float conversionScale = context.getResources().getDisplayMetrics().density;

        return (int) ((nDP * conversionScale) + 0.5f);

    }

    @Override
    public void onResume() {
        mapView.onResume();
        isScreenInFront = true;

        Notifications.removeAllNotifications(mCurrentActivity);

        Utils.setCallIncomingState();
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
        super.onResume();
    }

    private void getHeatMapData() {
        if (Connectivity.isConnectedFast(mCurrentActivity) && AppPreferences.getAvailableStatus()) {
            repository.requestHeatMapData(mCurrentActivity, handler);
        }
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
//        mCurrentActivity.showToolbar();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    private long mLastClickTime;

    @OnClick({R.id.mapPinIv, R.id.statusCheck, R.id.rlInactiveImage, R.id.tvNotice, R.id.tvDemand, R.id.rl_setDestination, R.id.rl_destinationSelected})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_destinationSelected:
            case R.id.rl_setDestination:
                if (mLastClickTime != 0 && (SystemClock.elapsedRealtime() - mLastClickTime < 1000)) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                setHomeLocation();
                break;
            case R.id.rlInactiveImage:
                break;
            case R.id.mapPinIv:
                setDriverLocation();
                break;
            case R.id.tvDemand:
                if (AppPreferences.getSettings() != null && AppPreferences.getSettings().getSettings() != null &&
                        StringUtils.isNotBlank(AppPreferences.getSettings().getSettings().getDemand())) {
                    String demandLink = AppPreferences.getSettings().getSettings().getDemand();
//                    demandLink.replace(Constants.REPLACE_CITY,AppPreferences.getPilotData().getCity());
                    String replaceString = demandLink.replace(Constants.REPLACE_CITY,StringUtils.capitalize(AppPreferences.getPilotData().getCity().getName()));
                    Utils.startCustomWebViewActivity(mCurrentActivity, replaceString, "Demand");
                }
                break;
            case R.id.tvNotice:
                if (AppPreferences.getSettings() != null && AppPreferences.getSettings().getSettings() != null &&
                        StringUtils.isNotBlank(AppPreferences.getSettings().getSettings().getNotice())) {
                    Utils.startCustomWebViewActivity(mCurrentActivity, AppPreferences.getSettings().getSettings().getNotice(), "Notice");
                }
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
//                                    AppPreferences.setAvailableStatus(false);
//                                    AppPreferences.setDriverDestination(null);

//                                    destinationSet(false);
                                    repository.requestUpdateStatus(mCurrentActivity, handler, false);
                                }
                            }
                        });
                    } else {
                        if (Connectivity.isConnectedFast(mCurrentActivity)) {
                            Dialogs.INSTANCE.showLoader(mCurrentActivity);
//                            AppPreferences.setAvailableStatus(true);
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
        if (!AppPreferences.getAvailableStatus()) {
            Intent returndropoffIntent = new Intent(mCurrentActivity, ConfirmDropOffAddressActivity.class);
            returndropoffIntent.putExtra("from", Constants.CONFIRM_DROPOFF_REQUEST_CODE);
            returndropoffIntent.putExtra(Constants.TOOLBAR_TITLE, "Confirm Destination");
            returndropoffIntent.putExtra(Constants.SEARCHBOX_TITLE, "Search Destination");
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
//                    destinationSet(true);
//                    repository.requestDriverDropOff(mCurrentActivity
//                            , handler
//                            , String.valueOf(mDropOff.latitude)
//                            , String.valueOf(mDropOff.longitude)
//                            , mDropOff.address);
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

    public synchronized void setStatusBtn() {
        if (mCurrentActivity == null || getView() == null) {
            return;
        }
        if (!AppPreferences.getAvailableStatus()) {     //inactive state
            statusCheck.setImageDrawable(ContextCompat.getDrawable(mCurrentActivity, R.drawable.inactive_icon));
            statusTv.setText("Inactive");
            rlInactiveImage.setVisibility(View.VISIBLE);
            myRangeBar.setEnabled(true);

            rl_main_destination.setVisibility(View.VISIBLE);
            if (AppPreferences.getDriverDestination() == null) {
                rl_setDestination.setVisibility(View.VISIBLE);
                rl_destinationSelected.setVisibility(View.GONE);
            } else {
                rl_setDestination.setVisibility(View.GONE);
                rl_destinationSelected.setVisibility(View.VISIBLE);
                tv_destinationName.setText(AppPreferences.getDriverDestination().address);
            }
        } else {        //active state
            statusCheck.setImageResource(R.drawable.active_icon);
            statusTv.setText("Active");
            rlInactiveImage.setVisibility(View.GONE);
            myRangeBar.setEnabled(false);

            if (null != AppPreferences.getDriverDestination()) {
                rl_main_destination.setVisibility(View.VISIBLE);
                rl_setDestination.setVisibility(View.GONE);
                rl_destinationSelected.setVisibility(View.VISIBLE);
                tv_destinationName.setText(AppPreferences.getDriverDestination().address);
            } else {
                rl_main_destination.setVisibility(View.GONE);
            }

            countDownTimer.start();
            getHeatMapData();
        }

        if (AppPreferences.isWalletAmountIncreased()) {
            setFenceError(AppPreferences.getWalletIncreasedError());
        } else if (AppPreferences.isOutOfFence()) {
            setFenceError("Non Service Area");
        } else {
            tvFenceError.setVisibility(View.GONE);
            tvConnectionStatus.setVisibility(View.VISIBLE);
        }
    }

    private void setFenceError(String errorMessage) {
        tvFenceError.setText(errorMessage);
        tvFenceError.setVisibility(View.VISIBLE);
        tvConnectionStatus.setVisibility(View.GONE);
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
                            if (AppPreferences.getAvailableStatus()) {
                                if (AppPreferences.isWalletAmountIncreased()) {
                                    AppPreferences.setWalletAmountIncreased(false);
                                }
                                if (AppPreferences.isOutOfFence()) {
                                    AppPreferences.setOutOfFence(false);
                                }
                                ActivityStackManager.getInstance(mCurrentActivity).restartLocationService();
                            } else {
                                AppPreferences.setDriverDestination(null);
                                ActivityStackManager.getInstance(mCurrentActivity).stopLocationService();
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


    public void onEvent(final String action) {
        if (mCurrentActivity != null && getView() != null) {
            mCurrentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (action.equalsIgnoreCase(Keys.CONNECTION_BROADCAST)) {
                        setConnectionStatus();
                    } else if (action.equalsIgnoreCase(Keys.INACTIVE_PUSH) || action.equalsIgnoreCase(Keys.INACTIVE_FENCE)) {
                        AppPreferences.setDriverDestination(null);
                        setStatusBtn();
                    } else if (action.equalsIgnoreCase(Keys.ACTIVE_FENCE)) {
                        setStatusBtn();
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

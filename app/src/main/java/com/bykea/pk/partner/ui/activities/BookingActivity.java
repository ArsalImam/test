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
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.Notifications;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.communication.socket.WebIORequestHandler;
import com.bykea.pk.partner.dal.LocCoordinatesInTrip;
import com.bykea.pk.partner.dal.source.JobsDataSource;
import com.bykea.pk.partner.dal.source.JobsRepository;
import com.bykea.pk.partner.dal.source.remote.request.ChangeDropOffRequest;
import com.bykea.pk.partner.dal.source.remote.response.FinishJobResponseData;
import com.bykea.pk.partner.dal.util.Injection;
import com.bykea.pk.partner.models.ReceivedMessageCount;
import com.bykea.pk.partner.models.data.PlacesResult;
import com.bykea.pk.partner.models.data.Stop;
import com.bykea.pk.partner.models.response.ArrivedResponse;
import com.bykea.pk.partner.models.response.BatchBooking;
import com.bykea.pk.partner.models.response.BatchBookingDropoff;
import com.bykea.pk.partner.models.response.BeginRideResponse;
import com.bykea.pk.partner.models.response.CancelRideResponse;
import com.bykea.pk.partner.models.response.CheckDriverStatusResponse;
import com.bykea.pk.partner.models.response.EndRideResponse;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.models.response.TopUpPassWalletResponse;
import com.bykea.pk.partner.models.response.UpdateDropOffResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.repositories.places.IPlacesDataHandler;
import com.bykea.pk.partner.repositories.places.PlacesDataHandler;
import com.bykea.pk.partner.tracking.AbstractRouting;
import com.bykea.pk.partner.tracking.Route;
import com.bykea.pk.partner.tracking.RouteException;
import com.bykea.pk.partner.tracking.Routing;
import com.bykea.pk.partner.tracking.RoutingListener;
import com.bykea.pk.partner.ui.bykeacash.BykeaCashDetailsListener;
import com.bykea.pk.partner.ui.bykeacash.BykeaCashFormFragment;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.StringCallBack;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.GeocodeStrategyManager;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.MapUtil;
import com.bykea.pk.partner.utils.NetworkChangeListener;
import com.bykea.pk.partner.utils.Permissions;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Util;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.utils.audio.BykeaAmazonClient;
import com.bykea.pk.partner.widgets.AutoFitFontTextView;
import com.bykea.pk.partner.widgets.DashedLine;
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
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.PolyUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bykea.pk.partner.utils.Constants.ACTION;
import static com.bykea.pk.partner.utils.Constants.ApiError.BUSINESS_LOGIC_ERROR;
import static com.bykea.pk.partner.utils.Constants.DIGIT_THOUSAND;
import static com.bykea.pk.partner.utils.Constants.DIGIT_ZERO;
import static com.bykea.pk.partner.utils.Constants.DIRECTION_API_MIX_THRESHOLD_METERS;
import static com.bykea.pk.partner.utils.Constants.MAX_LIMIT_LOAD_BOARD;
import static com.bykea.pk.partner.utils.Constants.MSG;
import static com.bykea.pk.partner.utils.Constants.PLUS;
import static com.bykea.pk.partner.utils.Constants.ServiceCode.DISPATCH_RIDE;
import static com.bykea.pk.partner.utils.Constants.ServiceCode.MART;
import static com.bykea.pk.partner.utils.Constants.ServiceCode.OFFLINE_DELIVERY;
import static com.bykea.pk.partner.utils.Constants.ServiceCode.OFFLINE_RIDE;

public class BookingActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, RoutingListener, BykeaCashDetailsListener {

    private final String TAG = BookingActivity.class.getSimpleName();

    @BindView(R.id.llBatchPickUpInfo)
    LinearLayout llBatchPickUpInfo;
    @BindView(R.id.tvBatchPickUpCustomerName)
    TextView tvBatchPickUpCustomerName;
    @BindView(R.id.tvBatchPickUpAddress)
    TextView tvBatchPickUpAddress;
    @BindView(R.id.blueDot)
    ImageView blueDot;
    @BindView(R.id.rLPWalletAmount)
    View rLPWalletAmount;
    @BindView(R.id.tvPWalletAmountLabel)
    TextView tvPWalletAmountLabel;
    @BindView(R.id.rlAddressMainLayout)
    RelativeLayout rlAddressMainLayout;
    @BindView(R.id.green_dot)
    ImageView greenDot;
    @BindView(R.id.dottedLine)
    DashedLine dottedLine;
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
    TextView tvCountDownEnd;
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
    @BindView(R.id.cancelBtn)
    FontTextView cancelBtn;
    @BindView(R.id.chatBtn)
    ImageView chatBtn;
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
    @BindView(R.id.cvRouteView)
    CardView cvRouteView;
    @BindView(R.id.ivServiceIcon)
    ImageView ivServiceIcon;
    @BindView(R.id.tvCashWasooliLabel)
    FontTextView tvCashWasooliLabel;
    @BindView(R.id.ivTopUp)
    ImageView ivTopUp;
    @BindView(R.id.llDetails)
    LinearLayout llDetails;
    @BindView(R.id.llPickUpDetails)
    LinearLayout llPickUpDetails;
    @BindView(R.id.vAddressDivider)
    View vAddressDivider;
    @BindView(R.id.tvPickUpCustomerName)
    TextView tvPickUpCustomerName;
    @BindView(R.id.tvPickUpDetailsAddress)
    TextView tvPickUpDetailsAddress;
    @BindView(R.id.tvPickUpOrderNumber)
    TextView tvPickUpOrderNumber;
    @BindView(R.id.llBykeaSupportContactInfo)
    LinearLayout llBykeaSupportContactInfo;
    @BindView(R.id.tvDetailsNotEntered)
    FontTextView tvDetailsNotEntered;
    @BindView(R.id.tvDetailsBanner)
    FontTextView tvDetailsBanner;
    @BindView(R.id.tvCustomerName)
    FontTextView tvCustomerName;
    @BindView(R.id.tvBykeaSupportContactNumber)
    FontTextView tvBykeaSupportContactNumber;
    @BindView(R.id.ivCustomerPhone)
    ImageView ivCustomerPhone;
    @BindView(R.id.ivPickUpCustomerPhone)
    ImageView ivPickUpCustomerPhone;
    @BindView(R.id.tvDetailsAddress)
    FontTextView tvDetailsAddress;
    @BindView(R.id.cartBadge)
    TextView cartBadge;

    @BindView(R.id.voiceNoteRl)
    RelativeLayout voiceNoteRl;
    @BindView(R.id.imgViewAudioPlay)
    ImageView imgViewAudioPlay;
    @BindView(R.id.progressBarForAudioPlay)
    ProgressBar progressBarForAudioPlay;
    @BindView(R.id.tvOrderNumber)
    FontTextView tvOrderNumber;

    public static boolean isJobActivityLive = false;

    private BookingActivity mCurrentActivity;
    private NormalCallData callData;
    JobsRepository jobsRepo;
    private UserRepository dataRepository;
    private String cancelReason = StringUtils.EMPTY;

    private Marker driverMarker, dropOffMarker, pickUpMarker/*, passCurrentLocMarker*/;
    private Polyline mapPolylines;
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
    private boolean allowTripStatusCall = true;
    boolean isBykeaCashJob = false;
    CountDownTimer countDownTimer;

    private boolean shouldCameraFollowCurrentLocation = false;
    private boolean isFinishedRetried = false;
    private boolean IS_CALLED_FROM_LOADBOARD_VALUE = false;
    private int requestTripCounter = 0;

    BykeaCashFormFragment bykeaCashFormFragment;
    private boolean isMapLoaded = false;
    private boolean isDirectionApiTimeResetRequired = true;
    private GeocodeStrategyManager geocodeStrategyManager;
    private String voiceNoteUrl;
    private MediaPlayer mediaPlayer;
    private Handler voiceNoteHandler = new Handler();

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

                            if (response.getData().getTrip() == null) {
                                requestTripCounter++;
                                if (requestTripCounter < MAX_LIMIT_LOAD_BOARD) {
                                    new Handler().postDelayed(() -> {
                                        dataRepository.getActiveTrip(mCurrentActivity, handler);
                                    }, Constants.HANDLER_POST_DELAY_LOAD_BOARD);
                                } else {
                                    Dialogs.INSTANCE.dismissDialog();
                                    Dialogs.INSTANCE.showTempToast("Request trip limit Exceeded");
                                    ActivityStackManager.getInstance().startHomeActivity(BookingActivity.this);
                                }
                                return;
                            }
                            Dialogs.INSTANCE.dismissDialog();

                            AppPreferences.setTripAcceptTime(System.currentTimeMillis());
                            AppPreferences.setEstimatedFare(normalCallData.getKraiKiKamai());
                            AppPreferences.addLocCoordinateInTrip(AppPreferences.getLatitude(), AppPreferences.getLongitude());
                            AppPreferences.setIsOnTrip(true);

                            if (normalCallData.getStatus() != null &&
                                    shouldUpdateTripData(normalCallData.getStatus())) {
                                AppPreferences.setCallData(normalCallData);
                                AppPreferences.setTripStatus(normalCallData.getStatus());
                                callData = normalCallData;
                                if (callData != null && callData.getServiceCode() != null) {
                                    if (callData.getServiceCode() == OFFLINE_RIDE ||
                                            callData.getServiceCode() == OFFLINE_DELIVERY) {
                                        chatBtn.setVisibility(View.GONE);
                                    }
                                    if (Utils.isVoiceNoteRequired(callData.getServiceCode())) {
                                        voiceNoteUrl = AppPreferences.getBookingVoiceNoteUrlAvailable();
                                        if (StringUtils.isNotEmpty(voiceNoteUrl)) {
                                            voiceNoteRl.setVisibility(View.VISIBLE);
                                        }
                                    }
                                    updateCustomerPickUp();
                                }
                                updateDropOff(isDirectionApiTimeResetRequired);
                                isDirectionApiTimeResetRequired = false;
                                showWalletAmount();
                            }

                            if (normalCallData.getStatus() != null && normalCallData.getStatus().equalsIgnoreCase(TripStatus.ON_FINISH_TRIP)) {
                                AppPreferences.setCallData(normalCallData);
                                AppPreferences.setTripStatus(normalCallData.getStatus());
                                ActivityStackManager.getInstance().startFeedbackActivity(mCurrentActivity);
                            } else {
                                setInitialData();
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

    /**
     * this will show pickup details window only if trip is in accept or arrived state
     * and call type is delivery
     */
    private void updateCustomerPickUp() {

        if (Utils.isNewBatchService(callData.getServiceCode())) {
            llBatchPickUpInfo.setVisibility(View.VISIBLE);
            tvBatchPickUpCustomerName.setText(callData.getSenderName() == null ? "-" : callData.getSenderName());
            tvBatchPickUpAddress.setText(callData.getSenderAddress() == null ? "-" : callData.getSenderAddress());
            ivPickUpCustomerPhone.setTag(Utils.phoneNumberToShow(callData.getSenderPhone()));
            return;
        }

        if ((Util.INSTANCE.isBykeaCashJob(callData.getServiceCode())
                || (Utils.isDeliveryService(callData.getCallType()) && (callData.getServiceCode() != null && callData.getServiceCode() != OFFLINE_DELIVERY)))
                && (callData.getStatus().equalsIgnoreCase(TripStatus.ON_ACCEPT_CALL) ||
                callData.getStatus().equalsIgnoreCase(TripStatus.ON_ARRIVED_TRIP))) {
            llPickUpDetails.setVisibility(View.VISIBLE);
            vAddressDivider.setVisibility(View.VISIBLE);
            greenDot.setVisibility(View.VISIBLE);
            int dotsHeightOffset = getResources().getDimensionPixelOffset(R.dimen._28sdp);
            if (!StringUtils.isEmpty(callData.getSenderName())) {
                tvPickUpCustomerName.setVisibility(View.VISIBLE);
                tvPickUpCustomerName.setText(callData.getSenderName());
                if (callData.getSenderName().length() > Constants.NUMBER_OF_CHARS_IN_LINE) {
                    dotsHeightOffset += getResources().getDimensionPixelOffset(R.dimen._17sdp);
                }
            } else {
                tvPickUpCustomerName.setVisibility(View.GONE);
                dotsHeightOffset -= getResources().getDimensionPixelOffset(R.dimen._17sdp);
            }
            if (!StringUtils.isEmpty(callData.getSenderPhone())) {
                ivPickUpCustomerPhone.setTag(callData.getSenderPhone());
                ivPickUpCustomerPhone.setVisibility(View.VISIBLE);
            } else
                ivPickUpCustomerPhone.setVisibility(View.GONE);
            if (!StringUtils.isEmpty(callData.getSenderAddress())) {
                tvPickUpDetailsAddress.setText(String.format(getString(R.string.formatting_with_street), callData.getSenderAddress()));
                tvPickUpDetailsAddress.setVisibility(View.VISIBLE);
                if (callData.getSenderAddress().length() > Constants.NUMBER_OF_CHARS_IN_LINE) {
                    dotsHeightOffset += getResources().getDimensionPixelOffset(R.dimen._17sdp);
                }
            }
            if (!callData.getStatus().equalsIgnoreCase(TripStatus.ON_ARRIVED_TRIP) && !StringUtils.isEmpty(callData.getOrder_no())) {
                tvPickUpOrderNumber.setText(String.format(getString(R.string.formatting_with_order), callData.getOrder_no()));
                tvPickUpOrderNumber.setVisibility(View.VISIBLE);
            } else {
                tvPickUpOrderNumber.setVisibility(View.GONE);
            }
            if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_ACCEPT_CALL)) {
                vAddressDivider.setVisibility(View.GONE);
            }

            if (!Util.INSTANCE.isBykeaCashJob(callData.getServiceCode())
                    && callData.getStatus().equalsIgnoreCase(TripStatus.ON_ARRIVED_TRIP)) {
                dottedLine.setVisibility(View.VISIBLE);
                blueDot.setVisibility(View.VISIBLE);
                dottedLine.getLayoutParams().height = dotsHeightOffset;
                dottedLine.requestLayout();
            } else {
                dottedLine.setVisibility(View.GONE);
                blueDot.setVisibility(View.GONE);
            }
        } else {
            greenDot.setVisibility(View.GONE);
            dottedLine.setVisibility(View.GONE);
            vAddressDivider.setVisibility(View.GONE);
            llPickUpDetails.setVisibility(View.GONE);
            if ((Util.INSTANCE.isBykeaCashJob(callData.getServiceCode())
                    || Utils.isNewBatchService(callData.getServiceCode())
                    || (callData.getServiceCode() != null && callData.getServiceCode() == OFFLINE_DELIVERY) || Utils.isRideService(callData.getCallType())
                    || callData.getStatus().equalsIgnoreCase(TripStatus.ON_ACCEPT_CALL))) {
                blueDot.setVisibility(View.GONE);
            }
        }
        rlAddressMainLayout.setVisibility(greenDot.getVisibility() == View.VISIBLE
                || blueDot.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);

    }

    private UserDataHandler driversDataHandler = new UserDataHandler() {

        @Override
        public void onUpdateDropOff(final UpdateDropOffResponse data) {
            onDropOffUpdate(data.getMessage());
        }

        @Override
        public void onArrived(final ArrivedResponse arrivedResponse) {
            onArrive(arrivedResponse.isSuccess(), arrivedResponse.getMessage());
        }

        @Override
        public void onCancelRide(final CancelRideResponse cancelRideResponse) {
            onCancelled(cancelRideResponse.isSuccess(), cancelRideResponse.getMessage(), cancelRideResponse.isAvailable());
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
            onStarted(beginRideResponse.isSuccess(), beginRideResponse.getMessage());
        }

        @Override
        public void onError(final int errorCode, final String error) {
            onStatusChangedFailed(error);
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
                        updateMarkers(true);
                    }
                    isMapLoaded = true;
                    if (!TripStatus.ON_ARRIVED_TRIP.equalsIgnoreCase(AppPreferences.getTripStatus()))
                        Utils.setScaleAnimation(cvDirections);

                    mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            if (marker.getTag() != null) return true;
                            try {
//                                int position = (int) marker.getTag();
                                //here, need to open navigation/finish activity

                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                    });


                    mGoogleMap.setMyLocationEnabled(true);
                    mGoogleMap.setOnMyLocationChangeListener(location -> {
                        Log.e("Location changed", "by google maps");
                        AppPreferences.saveLocation(location.getLatitude(), location.getLongitude());
                    });
                }
            });
        }
    };

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
                    cvRouteView.setVisibility(View.VISIBLE);
                    shouldCameraFollowCurrentLocation = true;
                } else {
                    cvLocation.setVisibility(View.VISIBLE);
                    cvRouteView.setVisibility(View.INVISIBLE);
                    shouldCameraFollowCurrentLocation = false;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

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
                    if ((callData != null && callData.getEndLat() != null) && (StringUtils.isNotBlank(callData.getEndLat()) &&
                            StringUtils.isNotBlank(callData.getEndLng()))) {
                        updateMarkers(false);
                    } else if (pickUpMarker != null) {
                        pickUpMarker.remove();
                        pickUpMarker = null;
                    }
                } else {
                    updateMarkers(false);
                }
                //INFO : UNCOMMENT IF REQUIRE TO CALL DIRECTIONS API
                /*drawRoutes();*/
                showEstimatedDistTime();
            }
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
                    if (null != progressDialogJobActivity) {
                        progressDialogJobActivity.dismiss();
                        if (allowTripStatusCall)
                            dataRepository.requestRunningTrip(mCurrentActivity, handler);
                    }
                } else {
                    if (progressDialogJobActivity != null) {
                        dismissProgressDialog();
                        progressDialogJobActivity.dismiss();
                        progressDialogJobActivity.show();
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ButterKnife.bind(this);
        mCurrentActivity = this;
        dataRepository = new UserRepository();
        jobsRepo = Injection.INSTANCE.provideJobsRepository(getApplication().getApplicationContext());

        if (progressDialogJobActivity == null) {
            progressDialogJobActivity = new ProgressDialog(mCurrentActivity);
            progressDialogJobActivity.setCancelable(false);
            progressDialogJobActivity.setIndeterminate(true);
            progressDialogJobActivity.setMessage(getString(R.string.internet_error));
        }

        // FOR CHAT NOTIFCATION BADGE ICON HANDLING
        if (AppPreferences.getReceivedMessageCount() != null) {
            cartBadge.setVisibility(View.VISIBLE);
            cartBadge.setText(String.valueOf(AppPreferences.getReceivedMessageCount().getConversationMessageCount()));
        }

        mCurrentActivity.registerReceiver(mMessageNotificationBadgeReceiver,
                new IntentFilter(Constants.Broadcast.CHAT_MESSAGE_RECEIVED));

        Dialogs.INSTANCE.showLoader(mCurrentActivity);
        dataRepository.getActiveTrip(mCurrentActivity, handler);

        AppPreferences.setStatsApiCallRequired(true);
        Utils.keepScreenOn(mCurrentActivity);
        Notifications.removeAllNotifications(mCurrentActivity);

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
        geocodeStrategyManager = new GeocodeStrategyManager(this, placesDataHandler, Constants.NEAR_LBL);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(locationReceiver, new IntentFilter(Keys.LOCATION_UPDATE_BROADCAST));
    }

    @Override
    protected void onResume() {
        Utils.redLog(TAG, "onResume called: " + allowTripStatusCall);
        mapView.onResume();

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
        setNotificationChatBadge();

        if (AppPreferences.isDropOffUpdateRequired()) {
            AppPreferences.setDropOffUpdateRequired(false);
            // EXECUTE IF CALL DATA IS NOT NULL (SCENARIO HANDLE FOR DROP OFF NOTICATION)
            if (callData != null) {
                shouldRefreshDropOffMarker = true;
                dataRepository.requestRunningTrip(mCurrentActivity, handler);
            }
        }

        super.onResume();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            startPlayProgressUpdater();
        }
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
        if (progressDialogJobActivity != null) progressDialogJobActivity.dismiss();
        AppPreferences.setJobActivityOnForeground(false);
        AppPreferences.setLastDirectionsApiCallTime(0);
        // Unregister here due to some reasons.
//        unregisterReceiver(cancelRideReceiver);
        if (locationReceiver != null) {
            unregisterReceiver(locationReceiver);
        }
        if (networkChangeListener != null) {
            unregisterReceiver(networkChangeListener);
        }
        if (mMessageNotificationBadgeReceiver != null) {
            unregisterReceiver(mMessageNotificationBadgeReceiver);
        }
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //This MUST be done before saving any of your own or your base class's     variables
        final Bundle mapViewSaveState = new Bundle(outState);
        mapView.onSaveInstanceState(mapViewSaveState);
        outState.putBundle("mapViewSaveState", mapViewSaveState);
        //Add any other variables here.
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.CONFIRM_DROPOFF_REQUEST_CODE && data != null) {
            if (resultCode == RESULT_OK) {
                PlacesResult mDropOff = data.getParcelableExtra(Constants.CONFIRM_DROPOFF_ADDRESS_RESULT);
                callData.setEndLat("" + mDropOff.latitude);
                callData.setEndLng("" + mDropOff.longitude);
                callData.setEndAddress(mDropOff.address);
                AppPreferences.setCallData(callData);
                allowTripStatusCall = false;
                Utils.redLog(TAG, "onActivityResult called: " + allowTripStatusCall);
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

    @OnClick({R.id.cancelBtn, R.id.chatBtn, R.id.jobBtn, R.id.cvLocation, R.id.cvRouteView, R.id.cvDirections, R.id.ivPickUpCustomerPhone,
            R.id.ivAddressEdit, R.id.ivTopUp, R.id.ivCustomerPhone, R.id.tvDetailsBanner, R.id.tvBykeaSupportContactNumber,
            R.id.imgViewAudioPlay, R.id.ivBatchPickUpCustomerPhone, R.id.progressBarForAudioPlay})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.chatBtn:
                if (bykeaCashFormFragment != null) bykeaCashFormFragment.dismiss();
                if (callData != null) {
                    if (callData.getCreator_type() != null &&
                            (callData.getCreator_type().toUpperCase().equalsIgnoreCase(Constants.APP) ||
                                    callData.getCreator_type().toUpperCase().equalsIgnoreCase(Constants.CREATOR_PASSENGER) ||
                                    callData.getCreator_type().toUpperCase().equalsIgnoreCase(Constants.ANDROID))) {
                        ActivityStackManager.getInstance()
                                .startChatActivity(callData.getPassName(), "", true, mCurrentActivity);
                    } else {
                        if (callData.getCreator_type().toUpperCase().equalsIgnoreCase(Constants.IOS) ||
                                (callData.getServiceCode() != null && callData.getServiceCode() == DISPATCH_RIDE))
                            Utils.sendSms(mCurrentActivity, callData.getPhoneNo());
                        else
                            Utils.sendSms(mCurrentActivity, callData.getSenderPhone());
                    }
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
                break;
            case R.id.ivCustomerPhone:
                if (ivCustomerPhone.getTag() != null) {
                    String phoneNumber = ivCustomerPhone.getTag().toString();
                    if (StringUtils.isNotBlank(phoneNumber)) {
                        if (Utils.isAppInstalledWithPackageName(mCurrentActivity, Constants.ApplicationsPackageName.WHATSAPP_PACKAGE)) {
                            Utils.openCallDialog(mCurrentActivity, callData, phoneNumber);
                        } else {
                            Utils.callingIntent(mCurrentActivity, phoneNumber);
                        }
                    }
                }
                break;
            case R.id.tvBykeaSupportContactNumber:
                if (StringUtils.isNotBlank(tvBykeaSupportContactNumber.getText().toString())) {
                    Utils.callingIntent(mCurrentActivity, tvBykeaSupportContactNumber.getText().toString());
                }
                break;
            case R.id.ivBatchPickUpCustomerPhone:
            case R.id.ivPickUpCustomerPhone:
                if (ivPickUpCustomerPhone.getTag() != null) {
                    String phoneNumber = ivPickUpCustomerPhone.getTag().toString();
                    if (StringUtils.isNotBlank(phoneNumber)) {
                        if (Utils.isAppInstalledWithPackageName(mCurrentActivity, Constants.ApplicationsPackageName.WHATSAPP_PACKAGE)) {
                            Utils.openCallDialog(mCurrentActivity, callData, phoneNumber);
                        } else {
                            Utils.callingIntent(mCurrentActivity, phoneNumber);
                        }
                    }
                }
                break;
            case R.id.cancelBtn:
                if (bykeaCashFormFragment != null) bykeaCashFormFragment.dismiss();
                if (callData != null) {
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
                }
                break;
            case R.id.jobBtn:
                if (bykeaCashFormFragment != null) bykeaCashFormFragment.dismiss();
                if (Connectivity.isConnectedFast(mCurrentActivity)) {
                    Dialogs.INSTANCE.showLoader(mCurrentActivity);
                    if (jobBtn.getText().toString().equalsIgnoreCase(getString(R.string.button_text_arrived)) &&
                            callData != null) {
                        int distance = (int) Utils.calculateDistance(AppPreferences.getLatitude(), AppPreferences.getLongitude(),
                                Double.parseDouble(callData.getStartLat()), Double.parseDouble(callData.getStartLng()));
                        if (distance > 200) {
                            boolean showTickBtn = distance < AppPreferences.getSettings().getSettings().getArrived_min_dist();
                            Dialogs.INSTANCE.showConfirmArrivalDialog(mCurrentActivity, showTickBtn, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    arriveAtJob();
                                }
                            });
                        } else {
                            Dialogs.INSTANCE.showRideStatusDialog(mCurrentActivity, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    arriveAtJob();
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
                    else if (jobBtn.getText().toString().equalsIgnoreCase(getString(R.string.button_text_start)) && callData != null) {
                        Dialogs.INSTANCE.showRideStatusDialog(mCurrentActivity, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startJob();
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Dialogs.INSTANCE.dismissDialog();
                            }
                        }, " اسٹارٹ؟");
                    } else if (jobBtn.getText().toString().equalsIgnoreCase(getString(R.string.button_text_finish)) && callData != null) {
                        if (Utils.isNewBatchService(callData.getServiceCode())) {
                            ActivityStackManager.getInstance().startFinishDeliveryScreen(BookingActivity.this);
                        } else {
                            Dialogs.INSTANCE.showRideStatusDialog(mCurrentActivity, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finishJob();
                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Dialogs.INSTANCE.dismissDialog();
                                }
                            }, " مکمل؟");
                        }
                    }
                } else {
                    Dialogs.INSTANCE.showError(mCurrentActivity, jobBtn, getString(R.string.error_internet_connectivity));
                }
                updateMarkers(true);
                break;
            case R.id.cvDirections:
                Utils.preventMultipleTap(view);
                if (!callData.getStatus().equalsIgnoreCase(TripStatus.ON_ACCEPT_CALL)
                        && Utils.isNewBatchService(callData.getServiceCode())) {
                    ActivityStackManager.getInstance().startNavigationDeliveryScreen(BookingActivity.this);
                } else {
                    startGoogleDirectionsApp();
                }
                break;
            case R.id.cvLocation:
                setCameraToDriverLocation();
                shouldCameraFollowCurrentLocation = true;
                break;
            case R.id.cvRouteView:
                setCameraToTripView();
                shouldCameraFollowCurrentLocation = false;
                break;
            case R.id.ivTopUp:
                if (callData != null) {
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
                                                    //allowing partner to perform top up only one time if
                                                    //he is in a ride service other than batch
                                                    AppPreferences.setTopUpPassengerWalletAllowed(Utils.isNewBatchService(callData.getServiceCode()));
//                                                ivTopUp.setVisibility(View.INVISIBLE);
                                                    Dialogs.INSTANCE.dismissDialog();
                                                    Utils.appToast(response.getMessage());
                                                    if (response.getData() != null) {
                                                        callData.setPassWallet(response.getData().getAmount());
                                                        AppPreferences.setCallData(callData);
                                                        tvPWalletAmount.setText(String.format(getString(R.string.amount_rs), callData.getPassWallet()));
                                                        updateWalletColor();
                                                    }
                                                }
                                            });
                                        }


                                    }

                                    @Override
                                    public void onError(int errorCode, String errorMessage) {
                                        Dialogs.INSTANCE.dismissDialog();
                                        Utils.appToast(errorMessage);
                                    }
                                });
                            }
                        }
                    });
                }
                break;
            case R.id.tvDetailsBanner:
                Utils.preventMultipleTap(view);
                if (Utils.isNewBatchService(callData.getServiceCode())) {
                    ActivityStackManager.getInstance().startDeliveryListingActivity(BookingActivity.this);
                    return;
                }
                bykeaCashFormFragment = BykeaCashFormFragment.newInstance(callData);
                bykeaCashFormFragment.setCancelable(false);
                bykeaCashFormFragment.show(getSupportFragmentManager(), BykeaCashFormFragment.class.getSimpleName());

                break;
            case R.id.imgViewAudioPlay:
                if (StringUtils.isNotEmpty(voiceNoteUrl)) {
                    voiceClipPlayDownload(voiceNoteUrl);
                } else {
                    Dialogs.INSTANCE.showToast(getString(R.string.no_voice_note_available));
                }
                break;
            case R.id.progressBarForAudioPlay:
                if (mediaPlayer != null) {
                    imgViewAudioPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_audio_play));
                    imgViewAudioPlay.setEnabled(true);
                    progressBarForAudioPlay.setVisibility(View.GONE);
                    mediaPlayer.pause();
                }
                break;
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
                    PolylineOptions polyOptions = new PolylineOptions();
                    polyOptions.width(Utils.dpToPx(mCurrentActivity, 5));
                    polyOptions.addAll(routeFirst.getPoints());
                    updateEtaAndCallData((routeFirst.getDurationValue() / 60) + "",
                            Utils.formatDecimalPlaces((routeFirst.getDistanceValue() / 1000.0) + "", 1));

                    if (mapPolylines != null) mapPolylines.remove();

                    if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_ACCEPT_CALL)) {
                        polyOptions.color(ContextCompat.getColor(mCurrentActivity, R.color.kelly_green));
                        mapPolylines = mGoogleMap.addPolyline(polyOptions);
                        callData.getPickupStop().setDistance(routeFirst.getDistanceValue());
                        callData.getPickupStop().setDuration(routeFirst.getDurationValue());
                    } else {
                        polyOptions.color(ContextCompat.getColor(mCurrentActivity, R.color.blue));
                        mapPolylines = mGoogleMap.addPolyline(polyOptions);
                        if (callData.getDropoffStop() != null) {
                            callData.getDropoffStop().setDistance(routeFirst.getDistanceValue());
                            callData.getDropoffStop().setDuration(routeFirst.getDurationValue());
                        }
                    }

                    shouldRefreshPickupMarker = true;
                    shouldRefreshDropOffMarker = true;
                    updateMarkers(false);

/*                    if (routeType == Routing.pickupRoute || routeType == Routing.dropOffRoute) {
                        if (mCurrentActivity != null && mGoogleMap != null) {
                            int padding = 40; // offset from edges of the map in pixels
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(routeFirst.getLatLgnBounds(), padding);
                            mGoogleMap.moveCamera(cu);
                        }
                    }*/

                }
            });
        } else {
            lastApiCallLatLng = null;
        }

    }

    @Override
    public void onRoutingCancelled() {

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
                    if (intent.getStringExtra(ACTION).equalsIgnoreCase(Keys.BROADCAST_CANCEL_RIDE) ||
                            intent.getStringExtra(ACTION).equalsIgnoreCase(Keys.BROADCAST_CANCEL_BATCH)) {
                        cancelByPassenger(false);
                    }
                    if (intent.getStringExtra(ACTION).equalsIgnoreCase(Keys.BROADCAST_CANCEL_BY_ADMIN)) {
                        String message = intent.getStringExtra(MSG);
                        cancelByPassenger(true);
                    }
                    if (intent.getStringExtra(ACTION).equalsIgnoreCase(Keys.BROADCAST_COMPLETE_BY_ADMIN)) {
                        playNotificationSound();
                        onCompleteByAdmin(intent.getStringExtra(MSG));
                    }
                    if (intent.getStringExtra(ACTION).equalsIgnoreCase(Keys.BROADCAST_DROP_OFF_UPDATED)) {
                        playNotificationSound();
                        Utils.appToast(getString(R.string.drop_off_update_by_passenger));
                        shouldRefreshDropOffMarker = true;
                        dataRepository.requestRunningTrip(mCurrentActivity, handler);
                    }
                    if (intent.getStringExtra(ACTION).equalsIgnoreCase(Keys.TRIP_DATA_UPDATED)) {
                        playNotificationSound();
                        Utils.appToast("Trip Details has been Added by Passenger.");
                        callData = AppPreferences.getCallData();
                        dataRepository.requestRunningTrip(mCurrentActivity, handler);
//                        updateDropOff();
                        shouldRefreshDropOffMarker = true;
                        if (Utils.isDeliveryService(callData.getCallType()) || Utils.isDescriptiveAddressRequired(callData.getServiceCode()))
                            showDropOffPersonInfo();
                        showWalletAmount();
                    }
                    if (intent.getStringExtra(ACTION).equalsIgnoreCase(Keys.BROADCAST_BATCH_UPDATED)) {
                        dataRepository.requestRunningTrip(mCurrentActivity, handler);
                    }
                }
            });

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Utils.appToast("Could not connect to Google API Client: Error " + connectionResult.getErrorCode());
    }


    private void updateDropOffToServer() {
        Dialogs.INSTANCE.showLoader(mCurrentActivity);
        updateDropOff(true);
        if (Utils.isModernService(callData.getServiceCode())) {
            jobsRepo.changeDropOff(callData.getTripId(), new ChangeDropOffRequest.Stop(Double.valueOf(callData.getEndLat()), Double.valueOf(callData.getEndLng()), callData.getEndAddress()), new JobsDataSource.DropOffChangeCallback() {

                @Override
                public void onDropOffChanged() {
                    shouldRefreshDropOffMarker = true;
                    onDropOffUpdate("Drop-off updated");
                }

                @Override
                public void onDropOffChangeFailed() {
                    onStatusChangedFailed("Drop-off update failed");
                }
            });
        } else {
            dataRepository.updateDropOff(driversDataHandler, mCurrentActivity, callData.getTripId(),
                    callData.getEndAddress(), callData.getEndLat() + "", callData.getEndLng() + "");
        }

    }

    private void updateDropOff(boolean isTimeResetRequired) {
        if (callData.getDropoffStop() != null
                && StringUtils.isNotBlank(callData.getEndAddress())
                && StringUtils.isNotBlank(callData.getEndLat())
                && StringUtils.isNotBlank(callData.getEndLng())) {
            setDropOffAddress();
            configCountDown();
//            if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_START_TRIP)) {
            lastApiCallLatLng = null;
            if (isTimeResetRequired) {
                AppPreferences.setLastDirectionsApiCallTime(0);
            }
            if (mRouteLatLng != null && mRouteLatLng.size() > 0) {
                mRouteLatLng.clear();
            }
//                drawRoutes();
            updateMarkers(true);
//                updatePickupMarker(callData.getEndLat(), callData.getEndLng());
//            }
        }
    }

    private void hideButtonOnArrived() {
        cancelBtn.setVisibility(View.GONE);
    }

    private void startGoogleDirectionsApp() {
        if (callData != null) {
            String end = StringUtils.EMPTY;
            if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_ACCEPT_CALL) ||
                    callData.getStatus().equalsIgnoreCase(TripStatus.ON_ARRIVED_TRIP)) {
                if (StringUtils.isNotBlank(callData.getStartLat()) && StringUtils.isNotBlank(callData.getStartLng())) {
                    end = callData.getStartLat() + "," + callData.getStartLng();
                }
            } else if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_START_TRIP)) {
                if (StringUtils.isNotBlank(callData.getEndLat()) && StringUtils.isNotBlank(callData.getEndLng())) {
                    end = callData.getEndLat() + "," + callData.getEndLng();
                }
            } else {
                if (StringUtils.isNotBlank(callData.getEndLat()) && StringUtils.isNotBlank(callData.getEndLng())) {
                    end = callData.getStartLat() + "," + callData.getStartLng();
                }
            }
            openGoogleDirectionsIntent(end);
        }
    }

    private void openGoogleDirectionsIntent(String end) {
        try {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + end + "&mode=d");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        } catch (Exception ex) {
            Utils.appToast("Please install Google Maps");
        }
    }


    /**
     * Validates ride type for Food delivery.
     *
     * @return if service type is Food delivery return true, otherwise false.
     */
    private boolean isServiceTypeFoodDelivery() {
        if (callData != null) {
            return Constants.RIDE_TYPE_FOOD_DELIVERY.equalsIgnoreCase(callData.getCallType());
        }
        return false;
    }

    private void logEvent(String status) {
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

    /**
     * Takes map's camera to Driver's current location
     */
    private void setCameraToDriverLocation() {
        if (null != mGoogleMap) {
            Utils.formatMap(mGoogleMap);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(AppPreferences.getLatitude()
                            , AppPreferences.getLongitude())
                    , 16f));
        }
    }

    /**
     * Sets map's camera to view whole trip route
     */
    private void setCameraToTripView() {
        LatLngBounds builder = getCurrentLatLngBounds();
        if (builder != null) {
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(builder, 30);
            int padding = (int) mCurrentActivity.getResources().getDimension(R.dimen._50sdp);
            mGoogleMap.setPadding(0, padding, 0, padding);
            mGoogleMap.animateCamera(cu);
        }
    }

    private void cancelReasonDialog() {
        Dialogs.INSTANCE.showCancelDialog(mCurrentActivity, new StringCallBack() {
            @Override
            public void onCallBack(String reasonMsg) {
                Dialogs.INSTANCE.showLoader(mCurrentActivity);
                cancelReason = reasonMsg;
                cancelJob(reasonMsg);
            }
        });
    }

    /*******************************************************************************************
     * METHODS FOR SETTING CALL DATA ACCORDING TO THE TRIP STATE
     ****************************************************************************************/
    private void setInitialData() {
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
            tvTripId.setText(callData.getTripNo());

            isBykeaCashJob = Util.INSTANCE.isBykeaCashJob(callData.getServiceCode());

            if (isBykeaCashJob) {
                tvDetailsBanner.setVisibility(View.VISIBLE);
                setAddressDetailsVisible();
            }

            if (callData.isDispatcher()) {
                rlAddressMainLayout.setVisibility(View.GONE);
            }
            cvDirections.setVisibility(View.VISIBLE);
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

            //Driver can not change drop-off for the loadboard jobs
            if (Utils.isLoadboardService(callData.getCallType()))
                ivAddressEdit.setVisibility(View.GONE);
            else if (Utils.isNewBatchService(callData.getServiceCode())) {
                updateMarkers(true);
                updateButtonState();
            }
            //TODO: Rendering service icon sent from server in disabled, until icons get updated on server
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

            if (Utils.isNewBatchService(callData.getServiceCode())) {
                updateBatchDropOffDetails();
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

    /**
     * Set and Configure Accordingly For The Accepted State
     */
    private void setAcceptedState() {
        callerNameTv.setText(callData.getPassName());
        setTimeDistance(Utils.formatETA(callData.getArivalTime()), callData.getDistance());
        setPickUpAddress();

        if (StringUtils.isEmpty(callData.getReceiverAddress())
                && StringUtils.isEmpty(callData.getReceiverName())
                && StringUtils.isEmpty(callData.getReceiverPhone())
                && ((callData.getServiceCode() != null && callData.getServiceCode() == MART) || isBykeaCashJob))
            setAddressDetailsVisible();
    }

    /**
     * Set and Configure Accordingly For The Arrived State
     */
    private void setArrivedState() {
        jobBtn.setText(getString(R.string.button_text_start));
        llStartAddress.setVisibility(View.GONE);
        showDropOffAddress();
        setOnArrivedData();

        if (isBykeaCashJob) setAddressDetailsVisible();
    }

    /**
     * Set and Configure Accordingly For The Started State
     */
    private void setStartedState() {
        if (isMapLoaded) Utils.setScaleAnimation(cvDirections);
        jobBtn.setText(getString(R.string.button_text_finish));
        llStartAddress.setVisibility(View.GONE);
        showDropOffAddress();
        setOnStartData();

        if (isBykeaCashJob) setAddressDetailsVisible();
    }

    /**
     * Hide and show Drop-off address bar if ride type is courier service.
     */
    private void showDropOffAddress() {
        if (Utils.isCourierService(callData.getCallType())) {
            llStartAddress.setVisibility(View.GONE);
            llEndAddress.setVisibility(View.GONE);
        } else if (Utils.isNewBatchService(callData.getServiceCode())) {
            llEndAddress.setVisibility(View.GONE);
        } else if (isBykeaCashJob) {
            llStartAddress.setVisibility(View.VISIBLE);
            llEndAddress.setVisibility(View.GONE);
        } else {
            llStartAddress.setVisibility(View.GONE);
            llEndAddress.setVisibility(View.VISIBLE);
        }
    }

    private void showWalletAmount() {
        tvPWalletAmount.setText(String.format(getString(R.string.amount_rs), callData.getPassWallet()));
        if ((Utils.isDeliveryService(callData.getCallType()) || Utils.isCourierService(callData.getCallType()) || Utils.isNewBatchService(callData.getServiceCode()))
                && TripStatus.ON_ARRIVED_TRIP.equalsIgnoreCase(callData.getStatus()) /*&&
                AppPreferences.isTopUpPassengerWalletAllowed()*/) {
            ivTopUp.setVisibility(View.VISIBLE);
        } else {
            ivTopUp.setVisibility(View.INVISIBLE);
        }

        if (Utils.isPurchaseService(callData.getCallType(), callData.getServiceCode())) {
            tvCashWasooliLabel.setText(R.string.kharidari_label);
            if (StringUtils.isNotBlank(callData.getCodAmount())) {
                tvCodAmount.setText(String.format(getString(R.string.amount_rs), callData.getCodAmount()));
            } else {
                tvCodAmount.setText(R.string.dash);
            }
        } else {
            int cashKiWasooliValue = callData.getCashKiWasooli();
            if (StringUtils.isNotBlank(callData.getCodAmount()) && (callData.isCod() || isBykeaCashJob)) {
                cashKiWasooliValue = cashKiWasooliValue + Integer.valueOf(callData.getCodAmountNotFormatted().trim());
                if (AppPreferences.getSettings().getSettings().isCustomCalculationsAllowForEasyPaisa()
                        && callData.getCreator_type().equalsIgnoreCase(Constants.API)) {
                    if (callData.getServiceCode() != null &&
                            callData.getServiceCode() == Constants.ServiceCode.MOBILE_WALLET &&
                            callData.getActualPassWallet() > NumberUtils.INTEGER_ZERO) {
                        if (callData.getActualPassWallet() < callData.getKraiKiKamai()) {
                            cashKiWasooliValue = cashKiWasooliValue + callData.getActualPassWallet();
                        } else if (callData.getActualPassWallet() >= callData.getKraiKiKamai()) {
                            cashKiWasooliValue = cashKiWasooliValue + callData.getKraiKiKamai();
                        }
                    }
                }
            }
            tvCodAmount.setText(String.format(getString(R.string.amount_rs), String.valueOf(cashKiWasooliValue)));
        }
        if (callData.getKraiKiKamai() != 0) {
            tvFareAmount.setText(String.format(getString(R.string.amount_rs_int), callData.getKraiKiKamai()));

            //adding plus for new batch trip
            if (Utils.isNewBatchService(callData.getServiceCode()) && CollectionUtils.isEmpty(callData.getBookingList())) {
                tvFareAmount.setText(tvFareAmount.getText().toString().concat(Constants.PLUS));
            }
        } else if (AppPreferences.getEstimatedFare() != 0) {
            tvFareAmount.setText(String.format(getString(R.string.amount_rs_int), AppPreferences.getEstimatedFare()));

            //adding plus for new batch trip
            if (Utils.isNewBatchService(callData.getServiceCode()) && CollectionUtils.isEmpty(callData.getBookingList())) {
                tvFareAmount.setText(tvFareAmount.getText().toString().concat(Constants.PLUS));
            }
        } else {
            tvFareAmount.setText(R.string.dash);
        }
        if (!Utils.isNewBatchService(callData.getServiceCode()))
            if (!callData.isDetectWallet())
                tvCodAmount.setText(tvFareAmount.getText().toString());

        updateWalletColor();
    }

    private void updateWalletColor() {
        if (!Utils.isNewBatchService(callData.getServiceCode())) return;
        try {
            int wallet = Integer.valueOf(callData.getPassWallet().replace(PLUS, StringUtils.EMPTY));
            if (wallet <= DIGIT_ZERO) {
                rLPWalletAmount.setBackgroundColor(ContextCompat.getColor(DriverApp.getContext(), R.color.red));
                tvPWalletAmount.setTextColor(ContextCompat.getColor(DriverApp.getContext(), R.color.white));
                tvPWalletAmountLabel.setTextColor(ContextCompat.getColor(DriverApp.getContext(), R.color.white));
            } else {
                rLPWalletAmount.setBackgroundColor(ContextCompat.getColor(DriverApp.getContext(), R.color.blue_light));
                tvPWalletAmount.setTextColor(ContextCompat.getColor(DriverApp.getContext(), R.color.black));
                tvPWalletAmountLabel.setTextColor(ContextCompat.getColor(DriverApp.getContext(), R.color.black));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDropOffPersonInfo() {
        if (checkIfDetailsAdded() || (callData.getServiceCode() != null && callData.getServiceCode() == MART)) {
            setAddressDetailsVisible();
        } else {
            tvDetailsNotEntered.setVisibility(View.VISIBLE);
            llDetails.setVisibility(View.GONE);
        }
    }

    private void setOnArrivedData() {
        showWalletAmount();
        if (Utils.isDeliveryService(callData.getCallType()) || Utils.isDescriptiveAddressRequired(callData.getServiceCode()))
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
            setDropOffAddress();
        } else {
            updateEtaAndCallData("0", "0");
            setPickUpAddress();
        }

        if (StringUtils.isBlank(callData.getEndAddress())) {
            endAddressTv.setText(getString(R.string.destination_not_selected_msg));
            endAddressTv.setTextColor(ContextCompat.getColor(mCurrentActivity, R.color.color_red));
        } else {
            setDropOffAddress();
        }

        jobBtn.setText(getString(R.string.button_text_start));
        AppPreferences.setTripStatus(TripStatus.ON_ARRIVED_TRIP);

        if (Utils.isNewBatchService(callData.getServiceCode())) {
            tvDetailsBanner.setVisibility(View.VISIBLE);

            if (CollectionUtils.isNotEmpty(callData.getBookingList()) && bookingsShouldHaveDropOffs()) {
                tvDetailsBanner.setBackgroundColor(ContextCompat.getColor(BookingActivity.this, R.color.colorAccent));
            } else {
                tvDetailsBanner.setBackgroundColor(ContextCompat.getColor(BookingActivity.this, R.color.booking_red));
            }
            updateButtonState();
            updateMarkers(true);

            updateBatchDropOffDetails();
        }
    }

    /**
     * this will check whether the bookings contain drop offs
     *
     * @return
     */
    private boolean bookingsShouldHaveDropOffs() {
        if (CollectionUtils.isEmpty(callData.getBookingList())) return false;
        for (BatchBooking batchBooking : callData.getBookingList()) {
            if (batchBooking.getDropoff() != null && StringUtils.isNotEmpty(batchBooking.getDropoff().getGpsAddress()) && (batchBooking.getDropoff().getLat() == NumberUtils.DOUBLE_ZERO))
                return false;
        }
        return true;
    }

    private void updateBatchDropOffDetails() {
        ivAddressEdit.setVisibility(View.GONE);
        if (CollectionUtils.isEmpty(callData.getBookingList())) {
            llEndAddress.setVisibility(View.GONE);
        } else {
            llEndAddress.setVisibility(View.VISIBLE);
            endAddressTv.setText(callData.getBookingsSummary());
        }
    }

    private void updateButtonState() {
        if (Utils.isNewBatchService(callData.getServiceCode())) {
            if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_ARRIVED_TRIP)) {

                boolean allBookingsContainDropOffs = bookingsShouldHaveDropOffs();

                jobBtn.setEnabled(allBookingsContainDropOffs && CollectionUtils.isNotEmpty(callData.getBookingList()));
                jobBtn.setBackgroundResource(allBookingsContainDropOffs && CollectionUtils.isNotEmpty(callData.getBookingList())
                        ? R.drawable.button_green
                        : R.drawable.button_grey
                );
            } else {
                jobBtn.setBackgroundResource(R.drawable.button_green/*ContextCompat.getColor(BookingActivity.this, R.color.colorAccent)*/);
                jobBtn.setEnabled(true);
            }
        }
    }


    private void updateEtaAndCallData(String time, String distance) {
        callData.setArivalTime(time);
        callData.setDistance(distance);
        setTimeDistance(callData.getArivalTime(), callData.getDistance());
        AppPreferences.setCallData(callData);
    }

    private void setOnStartData() {
        showWalletAmount();
        if (Utils.isDeliveryService(callData.getCallType()) || Utils.isDescriptiveAddressRequired(callData.getServiceCode()))
            showDropOffPersonInfo();
        hideButtonOnArrived();
        lastApiCallLatLng = null;
        if (isDirectionApiTimeResetRequired) {
            AppPreferences.setLastDirectionsApiCallTime(0);
            isDirectionApiTimeResetRequired = false;
        }
        if (mRouteLatLng != null && mRouteLatLng.size() > 0) {
            mRouteLatLng.clear();
        }
        if (isResume) {
            //INFO : UNCOMMENT IF REQUIRE TO CALL DIRECTIONS API
            /*if (!Utils.isRideService(callData.getCallType())) drawRouteToDropOff();*/
            callerNameTv.setText(callData.getPassName());
        }


        if (AppPreferences.getTripStatus().equalsIgnoreCase(TripStatus.ON_START_TRIP)) {
            setPickUpAddress();
        }

        if (StringUtils.isBlank(callData.getEndAddress())) {
            endAddressTv.setText(getString(R.string.destination_not_selected_msg));
            endAddressTv.setTextColor(ContextCompat.getColor(mCurrentActivity, R.color.color_red));
        } else {
            setDropOffAddress();
        }
        if (StringUtils.isNotBlank(callData.getEndLat()) && StringUtils.isNotBlank(callData.getEndLng())) {
            updateMarkers(true);
            if (pickUpMarker != null) {
                pickUpMarker.remove();
                pickUpMarker = null;
                if (mapPolylines != null) mapPolylines.remove();
            }
        } else {
            updateMarkers(true);
        }
        shouldRefreshDropOffMarker = true;

        if (Utils.isNewBatchService(callData.getServiceCode())) {
            tvDetailsBanner.setVisibility(View.VISIBLE);

            if (CollectionUtils.isNotEmpty(callData.getBookingList()) && bookingsShouldHaveDropOffs()) {
                tvDetailsBanner.setBackgroundColor(ContextCompat.getColor(BookingActivity.this, R.color.colorAccent));
            } else {
                tvDetailsBanner.setBackgroundColor(ContextCompat.getColor(BookingActivity.this, R.color.booking_red));
            }
            updateButtonState();
            updateMarkers(true);
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

        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void updateDriverMarker(String snappedLatitude, String snappedLongitude) {
        if (null != mGoogleMap) {

            //IF DRIVER MARKER IN NULL THEN ADD MARKER TO MAP.
            if (null == driverMarker) {
                driverMarker = mGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(
                        Utils.getMapIcon(callData.getCallType())))
                        .position(new LatLng(Double.parseDouble(snappedLatitude),
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
                showOnLeft = true; //isLeftAreaGreater(latLng);
            }
        }
        if (dropOffMarker != null) { // && StringUtils.isNotBlank(lastPickUpFlagOnLeft)) {
            if (!lastDropOffFlagOnLeft == showOnLeft || shouldRefreshDropOffMarker) { // || !lastEtaInFlag.equalsIgnoreCase(eta)) {
                shouldRefreshDropOffMarker = false;
                dropOffMarker.remove();
                dropOffMarker = mGoogleMap.addMarker(getDropOffMarker(latLng, showOnLeft));
            }
        } else if (latLng.latitude != 0.0 && latLng.longitude != 0.0) {
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
                showOnLeft = false; //isLeftAreaGreater(latLng);
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
        View mCustomMarkerView = MapUtil.getDropoffMarkerWithoutDistanceAndTime(mCurrentActivity);
        TextView tvRegionName = mCustomMarkerView.findViewById(R.id.tvRegionName);
        Stop dropOffStop = callData.getDropoffStop();

        if (dropOffStop.getZoneNameUr() != null && !dropOffStop.getZoneNameUr().isEmpty()) {
            tvRegionName.setText(getString(R.string.pick_drop_name_ur, dropOffStop.getZoneNameUr()));
        } else {
            tvRegionName.setText(getString(R.string.drop_ur));
        }

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
        View mCustomMarkerView = MapUtil.getPickupMarkerWithoutDistanceAndTime(mCurrentActivity);
        TextView tvRegionName = mCustomMarkerView.findViewById(R.id.tvRegionName);
        Stop pickupStop = callData.getPickupStop();

        if (pickupStop.getZoneNameUr() != null && !pickupStop.getZoneNameUr().isEmpty()) {
            tvRegionName.setText(getString(R.string.pick_drop_name_ur, pickupStop.getZoneNameUr()));
        } else {
            tvRegionName.setText(getString(R.string.pick_ur));
        }

        markerOptions.icon(MapUtil.getMarkerBitmapDescriptorFromView(mCustomMarkerView));
        return markerOptions;
    }

    /**
     * This method updates the trip pickup and drop off markers on basis of trip's current status
     *
     * @param shouldUpdateCamera if also update map camera bound after marker update
     */
    private synchronized void updateMarkers(boolean shouldUpdateCamera) {
        if (null == mGoogleMap || null == callData) return;

        mGoogleMap.clear();
        driverMarker = null;

        if (mCurrentLocation != null) {
            updateDriverMarker(mCurrentLocation.getLatitude() + "",
                    mCurrentLocation.getLongitude() + "");
        }

        if (callData.getPickupStop() != null && StringUtils.isNotEmpty(callData.getStartLat()) && StringUtils.isNotEmpty(callData.getStartLng())) {
            if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_ACCEPT_CALL) /*||
                    callData.getStatus().equalsIgnoreCase(TripStatus.ON_ARRIVED_TRIP)*/) {
                // ALWAYS UPDATE PICKUP MARKER FOR ACCEPT STATE
                pickUpMarker = null;
                updatePickupMarker();
            } else if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_START_TRIP)) {
                // DO NOT REMOVE PICKUP MARKER FOR START STATE AS WELL FOR BYKEA CASH
                if (pickUpMarker == null && isBykeaCashJob) {
                    updatePickupMarker();
                } else if (pickUpMarker != null && !isBykeaCashJob) {
                    pickUpMarker.remove();
                }
            }
        }
        if (Utils.isNewBatchService(callData.getServiceCode()) && callData.getStatus().equalsIgnoreCase(TripStatus.ON_ARRIVED_TRIP)) {
            llStartAddress.setVisibility(View.GONE);
        }
        if (callData.getDropoffStop() != null && StringUtils.isNotEmpty(callData.getEndLat()) && StringUtils.isNotEmpty(callData.getEndLng())) {
            dropOffMarker = null;
            updateDropOffMarker();
        }

        if (CollectionUtils.isNotEmpty(callData.getBookingList())) {
            // need to update markers here
            updateDropOffMarkers();
            setMarkersBound();
        } else {
            if (shouldUpdateCamera) setCameraToTripView();
            else if (shouldCameraFollowCurrentLocation) setCameraToDriverLocation();
        }
    }

    /***
     * Add Pickup marker to the pickup location.
     *
     * The Time complexity or rate of growth of a function is: O(n)
     */
    private void updateDropOffMarkers() {
        try {
            if (null == mGoogleMap || callData == null) return;
            ArrayList<BatchBooking> bookings = callData.getBookingList();
            boolean containsReturnRun = Utils.containsReturnRunBooking(bookings);
            for (int i = 0; i < bookings.size(); i++) {
                addDropOffMarker(bookings.get(i), i);
            }
            if (callData.isReturnRun() && !containsReturnRun) {
                BatchBooking batchBooking = new BatchBooking();
                batchBooking.setServiceCode(Constants.ServiceCode.SEND_COD);
                batchBooking.setDisplayTag("Z");
                batchBooking.setStatus(TripStatus.ON_START_TRIP);
                batchBooking.setDropoff(new BatchBookingDropoff());
                batchBooking.getDropoff().setLat(Double.valueOf(callData.getStartLat()));
                batchBooking.getDropoff().setLng(Double.valueOf(callData.getStartLng()));
                addDropOffMarker(batchBooking, callData.getBookingList().size());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void addDropOffMarker(BatchBooking batchBooking, int tag) {
        mGoogleMap.addMarker(new MarkerOptions()
                .icon(Utils.getDropOffBitmapDiscriptorForBooking(mCurrentActivity, batchBooking))
                .position(new LatLng(batchBooking.getDropoff().getLat(), batchBooking.getDropoff().getLng()))).setTag(tag);
    }

    /**
     * fit All dropOff locations and driver's current location to the screen - bound markers within screen
     */
    private void setMarkersBound() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        List<LatLng> latLngList = Utils.getDropDownLatLngList(callData);
        for (LatLng pos : latLngList) {
            builder.include(pos);
        }
        if (driverMarker != null) {
            builder.include(driverMarker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = (int) mCurrentActivity.getResources().getDimension(R.dimen._30sdp);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mGoogleMap.moveCamera(cu);
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
            // DRAW ROUTES FOR THE FIRST TIME AND AFTER THAT DRAW IF JOB ACTIVITY IS IN FOREGROUND
            if (AppPreferences.isJobActivityOnForeground() || mapPolylines == null) {
                AppPreferences.setLastDirectionsApiCallTime(System.currentTimeMillis());
                if (isDirectionApiCallRequired(start)) {
                    Log.v(TAG, "Direction API Called");
                    lastApiCallLatLng = start;
                    if (mRouteLatLng != null && mRouteLatLng.size() > 0) {
                        mRouteLatLng.clear();
                    }
                    Routing.Builder builder = new Routing.Builder();
                    if (StringUtils.isNotBlank(Utils.getApiKeyForDirections())) {
                        builder.key(Utils.getApiKeyForDirections());
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
    }

    private boolean isDirectionApiCallRequired(LatLng currentApiCallLatLng) {
        if ((lastApiCallLatLng != null)
                && (lastApiCallLatLng.equals(currentApiCallLatLng)
                || (Utils.calculateDistance(currentApiCallLatLng.latitude, currentApiCallLatLng.longitude,
                lastApiCallLatLng.latitude, lastApiCallLatLng.longitude) < DIRECTION_API_MIX_THRESHOLD_METERS))) {
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

    private LatLngBounds getCurrentLatLngBounds() {
        int numberOfBounds = 0;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if (pickUpMarker != null) {
            builder.include(pickUpMarker.getPosition());
            numberOfBounds++;
        }
        if (dropOffMarker != null) {
            builder.include(dropOffMarker.getPosition());
            numberOfBounds++;
        }
        if (driverMarker != null) {
            builder.include(driverMarker.getPosition());
            numberOfBounds++;
        }

        if (numberOfBounds == 0)
            return null;

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

        if (isBykeaCashJob) {
            drawRoute(new LatLng(AppPreferences.getLatitude(), AppPreferences.getLongitude()),
                    new LatLng(Double.parseDouble(callData.getStartLat()), Double.parseDouble(callData.getStartLng())),
                    Routing.pickupRoute);
        } else if (StringUtils.isNotBlank(callData.getStartLat())
                && StringUtils.isNotBlank(callData.getStartLng())
                && StringUtils.isNotBlank(callData.getEndLat())
                && StringUtils.isNotBlank(callData.getEndLng())
                && StringUtils.isNotBlank(AppPreferences.getLatitude() + "")
                && StringUtils.isNotBlank(AppPreferences.getLongitude() + "")) {

            if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_ACCEPT_CALL)) {
                drawRoute(new LatLng(AppPreferences.getLatitude(), AppPreferences.getLongitude()),
                        new LatLng(Double.parseDouble(callData.getStartLat()), Double.parseDouble(callData.getStartLng())),
                        Routing.pickupRoute);
            } else if (!Utils.isRideService(callData.getCallType())) {
                drawRoute(new LatLng(AppPreferences.getLatitude(), AppPreferences.getLongitude()),
                        new LatLng(Double.parseDouble(callData.getEndLat()), Double.parseDouble(callData.getEndLng())),
                        Routing.pickupRoute);
            }
        } else {
            if (mapPolylines != null) mapPolylines.remove();
        }
    }

    private void drawRouteToPickup() {
        if (StringUtils.isNotBlank(callData.getStartLat())
                && StringUtils.isNotBlank(callData.getStartLng())
                && StringUtils.isNotBlank(AppPreferences.getLatitude() + "")
                && StringUtils.isNotBlank(AppPreferences.getLongitude() + "")) {

            drawRoute(
                    new LatLng(AppPreferences.getLatitude(), AppPreferences.getLongitude()),
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
            drawRoute(startLatLng, endLatlng, Routing.onChangeRoute);
        } else {
            if (mapPolylines != null) {
                mapPolylines.remove();
            }
        }
    }

    private void onGetLocation(double latitude, double longitude) {
        if (null != mCurrentLocation && callData != null) {
            mPreviousLocation = mCurrentLocation;

            mCurrentLocation.setLatitude(latitude);
            mCurrentLocation.setLongitude(longitude);

            updateDriverMarker(mCurrentLocation.getLatitude() + "",
                    mCurrentLocation.getLongitude() + "");

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

    private void cancelByPassenger(boolean isCanceledByAdmin) {
        AppPreferences.removeReceivedMessageCount();
        playNotificationSound();
        Utils.setCallIncomingState();
        AppPreferences.setTripStatus(TripStatus.ON_FREE);
        ActivityStackManager.getInstance().startHomeActivityFromCancelTrip(isCanceledByAdmin, mCurrentActivity);
        mCurrentActivity.finish();
    }

    private void onCompleteByAdmin(String msg) {
        AppPreferences.removeReceivedMessageCount();
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
        if (StringUtils.isBlank(callData.getReceiverPhone())) {
            isAdded = false;
        } else if (!Utils.isModernService(callData.getServiceCode()) && StringUtils.isBlank(callData.getCodAmount())) {
            isAdded = false;
        }
        return isAdded;
    }

    /**
     * Sets up Countdown timer for ETA on ride Pickup and ride Drop-off stops
     */
    private void configCountDown() {
        if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_ACCEPT_CALL) && callData.getPickupStop() != null &&
                callData.getPickupStop().getDuration() != null) {
            int pickDuration = callData.getPickupStop().getDuration();
            long eta = AppPreferences.getTripAcceptTime() + TimeUnit.SECONDS.toMillis(pickDuration);
            tvCountDown.setVisibility(View.VISIBLE);
            startCountDown(eta);
        } else if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_START_TRIP) && callData.getDropoffStop() != null &&
                callData.getDropoffStop().getDuration() != null) {
            int dropDuration = callData.getDropoffStop().getDuration();
            long eta = AppPreferences.getStartTripTime() + TimeUnit.SECONDS.toMillis(dropDuration);
            tvCountDownEnd.setVisibility(View.VISIBLE);
            startCountDown(eta);
        } else {
            tvCountDown.setText(R.string.clock_zero);
            tvCountDownEnd.setText(R.string.clock_zero);
        }
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
                tvCountDownEnd.setText(R.string.clock_zero);
                this.cancel();
            }
        };
        countDownTimer.start();
    }

    /**
     * Request server to mark arrive at job. Depending upon service types, it does this
     * communication on either REST Api or socket
     */
    private void arriveAtJob() {
        Dialogs.INSTANCE.dismissDialog();
        Dialogs.INSTANCE.showLoader(mCurrentActivity);
        if (Utils.isModernService(callData.getServiceCode())) {
            ArrayList<LocCoordinatesInTrip> route = AppPreferences.getLocCoordinatesInTrip();
            JobsDataSource.ArrivedAtJobCallback cb = new JobsDataSource.ArrivedAtJobCallback() {

                @Override
                public void onJobArrived() {
                    onArrive(true, "Job arrived success");
                }

                @Override
                public void onJobArriveFailed() {
                    onStatusChangedFailed("Failed to mark arrived");
                }
            };
            if (Utils.isNewBatchService(callData.getServiceCode())) {
                jobsRepo.arrivedAtJobForBatch(callData.getTripId(), route, cb);
            } else {
                jobsRepo.arrivedAtJob(callData.getTripId(), route, cb);
            }
        } else {
            dataRepository.requestArrived(mCurrentActivity, driversDataHandler);
        }
        logEvent(TripStatus.ON_ARRIVED_TRIP);
    }

    /**
     * Request server to start job. Depending upon service types, it does this
     * communication on either REST Api or socket
     */
    private void startJob() {
        Dialogs.INSTANCE.dismissDialog();
        Dialogs.INSTANCE.showLoader(mCurrentActivity);
        AppPreferences.clearTripDistanceData();
        if (Utils.isModernService(callData.getServiceCode())) {
            JobsDataSource.StartJobCallback cb = new JobsDataSource.StartJobCallback() {
                @Override
                public void onJobStarted() {
                    onStarted(true, "Job started successfully");
                }

                @Override
                public void onJobStartFailed(@Nullable String message) {
                    onStatusChangedFailed(message);
                }
            };
            if (Utils.isNewBatchService(callData.getServiceCode())) {
                jobsRepo.startJobForBatch(callData.getTripId(), callData.getStartAddress(), cb);
            } else {
                jobsRepo.startJob(callData.getTripId(), callData.getStartAddress(), cb);

            }
        } else {
            dataRepository.requestBeginRide(mCurrentActivity, driversDataHandler,
                    callData.getEndLat(), callData.getEndLng(), callData.getEndAddress());
        }
        logEvent(TripStatus.ON_START_TRIP);
    }

    /**
     * Request server to cancel job. Depending upon service types, it does this
     * communication on either REST Api or socket
     */
    private void cancelJob(String reasonMsg) {
        if (Utils.isModernService(callData.getServiceCode())) {
            JobsDataSource.CancelJobCallback cancelCallBack = new JobsDataSource.CancelJobCallback() {
                @Override
                public void onJobCancelled() {
                    onCancelled(true, "Trip cancelled successfully", true);
                }

                @Override
                public void onJobCancelFailed() {
                    onStatusChangedFailed("Unable to cancel trip");
                }
            };
            if (Utils.isNewBatchService(callData.getServiceCode()))
                jobsRepo.cancelJobForBatch(callData.getTripId(), reasonMsg, cancelCallBack);
            else
                jobsRepo.cancelJob(callData.getTripId(), reasonMsg, cancelCallBack);
        } else {
            dataRepository.requestCancelRide(mCurrentActivity, driversDataHandler, reasonMsg);
        }

    }

    /**
     * Request server to finish job. Depending upon service types, it does this
     * communication on either REST Api or socket
     */
    private void finishJob() {
        AppPreferences.removeReceivedMessageCount();
        Dialogs.INSTANCE.dismissDialog();
        Dialogs.INSTANCE.showLoader(mCurrentActivity);
        logEvent(TripStatus.ON_FINISH_TRIP);
        geocodeStrategyManager.fetchLocation(AppPreferences.getLatitude(), AppPreferences.getLongitude(), false);
    }

    private IPlacesDataHandler placesDataHandler = new PlacesDataHandler() {
        @Override
        public void onPlacesResponse(String response) {
            super.onPlacesResponse(response);
            if (Utils.isModernService(callData.getServiceCode())) {
                finishJobRestApi(response);
            } else {
                dataRepository.requestEndRide(mCurrentActivity, response, driversDataHandler);
            }
        }

    };

    /**
     * Request finish job on Rest API
     */
    private void finishJobRestApi(String endAddress) {
        AppPreferences.removeReceivedMessageCount();
        String endLatString = AppPreferences.getLatitude() + "";
        String endLngString = AppPreferences.getLongitude() + "";
        String lastLat = AppPreferences.getPrevDistanceLatitude();
        String lastLng = AppPreferences.getPrevDistanceLongitude();
        if (!lastLat.equalsIgnoreCase("0.0") && !lastLng.equalsIgnoreCase("0.0")) {
            if (!Utils.isValidLocation(Double.parseDouble(endLatString), Double.parseDouble(endLngString), Double.parseDouble(lastLat), Double.parseDouble(lastLng))) {
                endLatString = lastLat;
                endLngString = lastLng;
            }
        }

        LocCoordinatesInTrip startLatLng = new LocCoordinatesInTrip();
        startLatLng.setLat(AppPreferences.getCallData().getStartLat());
        startLatLng.setLng(AppPreferences.getCallData().getStartLng());
        startLatLng.setDate(Utils.getIsoDate(AppPreferences.getStartTripTime()));

        LocCoordinatesInTrip endLatLng = new LocCoordinatesInTrip();
        endLatLng.setLat(endLatString);
        endLatLng.setLng(endLngString);
        endLatLng.setDate(Utils.getIsoDate());
        ArrayList<LocCoordinatesInTrip> prevLatLngList = AppPreferences.getLocCoordinatesInTrip();
        ArrayList<LocCoordinatesInTrip> latLngList = new ArrayList<>();
        latLngList.add(startLatLng);
        if (prevLatLngList != null && prevLatLngList.size() > 0) {
            latLngList.addAll(prevLatLngList);
        }
        latLngList.add(endLatLng);

        jobsRepo.finishJob(callData.getTripId(), latLngList, endAddress, new JobsDataSource.FinishJobCallback() {
            @Override
            public void onJobFinished(@NotNull FinishJobResponseData data, @NotNull String request, @NotNull String resp) {
                AppPreferences.removeReceivedMessageCount();
                FirebaseCrashlytics.getInstance().setUserId(AppPreferences.getPilotData().getId());
                FirebaseCrashlytics.getInstance().setCustomKey("Finish Job Request Trip ID", callData.getTripId());
                FirebaseCrashlytics.getInstance().setCustomKey("Finish Job Response", resp);

                onFinished(data, endAddress);
            }

            @Override
            public void onJobFinishFailed(@Nullable String message, @Nullable Integer code) {
                if (code != null && code == BUSINESS_LOGIC_ERROR) {
                    dataRepository.getActiveTrip(mCurrentActivity, handler);
                } else {
                    Dialogs.INSTANCE.showError(mCurrentActivity, jobBtn, message);
//                onStatusChangedFailed(message);
                }
            }
        });
    }

    /**
     * On response of Arrived at Job call
     *
     * @param success is response with success
     * @param message response message
     */
    private void onArrive(boolean success, String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialogs.INSTANCE.dismissDialog();
                if (success) {
                    callData = AppPreferences.getCallData();
                    callData.setStatus(TripStatus.ON_ARRIVED_TRIP);
                    AppPreferences.setCallData(callData);
                    showDropOffAddress();
                    AppPreferences.setTripStatus(TripStatus.ON_ARRIVED_TRIP);
                    setOnArrivedData();
                    BookingActivity.this.updateCustomerPickUp();
                    // CHANGING DRIVER MARKER FROM SINGLE DRIVER TO DRIVER AND PASSENGER MARKER...
                    changeDriverMarker();
                    updateEtaAndCallData("0", "0");
                    configCountDown();
                } else {
                    Dialogs.INSTANCE.showError(mCurrentActivity, jobBtn, message);
                }
                EventBus.getDefault().post(Constants.Broadcast.UPDATE_FOREGROUND_NOTIFICATION);
            }
        });
    }

    /**
     * On response of job drop-off changed
     *
     * @param message response message
     */
    private void onDropOffUpdate(String message) {
        if (mCurrentActivity != null) {
            mCurrentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Dialogs.INSTANCE.dismissDialog();
                    Utils.appToast(message);
                    configCountDown();
                    allowTripStatusCall = true;
                    Utils.redLog(TAG, "driversDataHandler called: " + allowTripStatusCall);
                    dataRepository.requestRunningTrip(mCurrentActivity, handler);
                }
            });
        }
    }

    /**
     * On response of job start
     *
     * @param success is response with success
     * @param message response message
     */
    private void onStarted(boolean success, String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                    jobBtn.setEnabled(true);
                Dialogs.INSTANCE.dismissDialog();
                if (success) {
                    hideButtonOnArrived();
                    callData = AppPreferences.getCallData();
                    callData.setStatus(TripStatus.ON_START_TRIP);

                    updateCustomerPickUp();
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
                    updateDropOff(true);
                    configCountDown();
                } else {
                    Dialogs.INSTANCE.showError(mCurrentActivity, jobBtn, message);
                }
                EventBus.getDefault().post(Constants.Broadcast.UPDATE_FOREGROUND_NOTIFICATION);
            }
        });
    }

    /**
     * On response of cancel job
     *
     * @param success     is response with success
     * @param isAvailable either to keep driver online
     */
    private void onCancelled(boolean success, String message, boolean isAvailable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialogs.INSTANCE.dismissDialog();
                if (success) {
                    AppPreferences.removeReceivedMessageCount();
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
                    Utils.appToast(message);
                    Utils.setCallIncomingState();
                    AppPreferences.setWalletAmountIncreased(!isAvailable);
                    AppPreferences.setAvailableStatus(isAvailable);

                    if (Utils.isConnected(BookingActivity.this, false))
                        dataRepository.requestLocationUpdate(mCurrentActivity, handler, AppPreferences.getLatitude(), AppPreferences.getLongitude());

                    ActivityStackManager.getInstance().startHomeActivity(true, mCurrentActivity);
                    finish();
                } else {
                    Dialogs.INSTANCE.showError(mCurrentActivity, jobBtn, message);
                }
                EventBus.getDefault().post(Constants.Broadcast.UPDATE_FOREGROUND_NOTIFICATION);
            }
        });
    }

    /**
     * On response of finish job
     *
     * @param data response data
     */
    private void onFinished(FinishJobResponseData data, String endAddress) {
        if (data == null && !isFinishedRetried) {
            isFinishedRetried = true;
            finishJobRestApi(endAddress); // retry to finish job
        } else {
            Dialogs.INSTANCE.dismissDialog();
            logAnalyticsEvent(Constants.AnalyticsEvents.ON_RIDE_COMPLETE);
            endAddressTv.setEnabled(false);
            callData = AppPreferences.getCallData();
//                callData.setStartAddress(data.getStartAddress());
            callData.setEndAddress(data.getTrip().getEnd_address());
            callData.setTripNo(data.getInvoice().getTrip_no());
            callData.setTotalFare(String.valueOf(data.getInvoice().getTotal()));
            callData.setTotalMins(String.valueOf(data.getInvoice().getMinutes()));
            callData.setDistanceCovered(String.valueOf(data.getInvoice().getKm()));
            callData.setRuleIds(data.getTrip().getRule_ids());
            if (StringUtils.isNotBlank(String.valueOf(data.getInvoice().getWallet_deduction()))) {
                callData.setWallet_deduction(String.valueOf(data.getInvoice().getWallet_deduction()));
            }
            if (StringUtils.isNotBlank(String.valueOf(data.getInvoice().getPromo_deduction()))) {
                callData.setPromo_deduction(String.valueOf(data.getInvoice().getPromo_deduction()));
            }
//                if (StringUtils.isNotBlank(data.getDropoff_discount())) {
//                    callData.setDropoff_discount(data.getDropoff_discount());
//                }
            callData.setStatus(TripStatus.ON_FINISH_TRIP);
            callData.setTrip_charges(String.valueOf(data.getInvoice().getTrip_charges()));
            AppPreferences.setCallData(callData);
            tvEstimation.setVisibility(View.GONE);
            AppPreferences.clearTripDistanceData();
            AppPreferences.setTripStatus(TripStatus.ON_FINISH_TRIP);
            ActivityStackManager.getInstance()
                    .startFeedbackActivity(mCurrentActivity);
            mCurrentActivity.finish();
        }
    }

    /**
     * On failed response status change calls
     *
     * @param error Error message
     */
    private void onStatusChangedFailed(String error) {
        runOnUiThread(() -> {
            Dialogs.INSTANCE.dismissDialog();
            jobBtn.setEnabled(true);
            Utils.appToast(error);
        });
    }

//region

    /**
     * Broadcast Receiver to updated the message badge count.
     */
    private BroadcastReceiver mMessageNotificationBadgeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setNotificationChatBadge();
        }
    };

    public void setNotificationChatBadge() {
        ReceivedMessageCount receivedMessageCount = AppPreferences.getReceivedMessageCount();
        if (!DriverApp.isChatActivityVisible() && receivedMessageCount != null) {
            if (receivedMessageCount.getConversationMessageCount() > 0) {
                cartBadge.setVisibility(View.VISIBLE);
                cartBadge.setText(String.valueOf(receivedMessageCount.getConversationMessageCount()));
            }
        } else if (receivedMessageCount != null) {
            cartBadge.setVisibility(View.GONE);
            AppPreferences.setReceivedMessageCount(new ReceivedMessageCount(receivedMessageCount.getTripId(), Constants.DIGIT_ZERO));
        } else {
            cartBadge.setVisibility(View.GONE);
        }
    }
//endregion

    /**
     * Set PickUpAddress
     */
    public void setPickUpAddress() {
        startAddressTv.setText(callData.getStartAddress());
    }

    /**
     * Set Drop Off Address
     */
    public void setDropOffAddress() {
        endAddressTv.setTextColor(ContextCompat.getColor(mCurrentActivity, R.color.textColorPrimary676767));
        if (Utils.isNewBatchService(callData.getServiceCode())) {
            endAddressTv.setText(callData.getBookingsSummary());
        } else {
            endAddressTv.setText(callData.getEndAddress());
        }
    }

    /**
     * Set Address Details
     * Entered by user for creation of booking
     */
    private void setAddressDetailsVisible() {
        if (Util.INSTANCE.isBykeaCashJob(callData.getServiceCode())) return;

        String senderAddress = callData.getSenderAddress() != null ? callData.getSenderAddress() : "";
        String senderName = callData.getSenderName() != null ? callData.getSenderName() : "";
        String senderPhone = Utils.phoneNumberToShow(callData.getSenderPhone());

        llDetails.setVisibility(View.VISIBLE);
        tvDetailsNotEntered.setVisibility(View.GONE);
        blueDot.setVisibility(View.VISIBLE);
        if (Utils.isDeliveryService(callData.getCallType())) {
            setAddressDetailEitherSenderOrReceiver(tvDetailsAddress, StringUtils.EMPTY,
                    callData.getReceiverAddress(), getString(R.string.formatting_with_street));
            setAddressDetailEitherSenderOrReceiver(tvCustomerName, StringUtils.EMPTY,
                    callData.getReceiverName(), getString(R.string.empty_formatting));
        } else {
            setAddressDetailEitherSenderOrReceiver(tvDetailsAddress, senderAddress, callData.getReceiverAddress(), getString(R.string.formatting_with_street));
            setAddressDetailEitherSenderOrReceiver(tvCustomerName, senderName, callData.getReceiverName(), getString(R.string.empty_formatting));
        }
        if (!StringUtils.isEmpty(callData.getReceiverPhone())) {
            ivCustomerPhone.setTag(Utils.phoneNumberToShow(callData.getReceiverPhone()));
            ivCustomerPhone.setVisibility(View.VISIBLE);
        } else if (!StringUtils.isEmpty(senderPhone)) {
            ivCustomerPhone.setTag(senderPhone);
            ivCustomerPhone.setVisibility(View.VISIBLE);
        } else {
            ivCustomerPhone.setVisibility(View.GONE);
        }

        if (isBykeaCashJob) {
            tvDetailsBanner.setVisibility(View.VISIBLE);
            if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_START_TRIP)) {
                String supportContact = AppPreferences.getSettings().getSettings().getBykeaSupportContact();
                if (StringUtils.isNotBlank(supportContact))
                    tvBykeaSupportContactNumber.setText(supportContact);
                else tvBykeaSupportContactNumber.setText(Constants.BYKEA_SUPPORT_CONTACT_NUMBER);
                llBykeaSupportContactInfo.setVisibility(View.VISIBLE);
                llDetails.setVisibility(View.GONE);
                tvDetailsNotEntered.setVisibility(View.GONE);
            } else {
                llBykeaSupportContactInfo.setVisibility(View.GONE);
                if (senderAddress.equalsIgnoreCase(callData.getStartAddress()) && senderName.equalsIgnoreCase(callData.getPassName()) && senderPhone.equalsIgnoreCase(callData.getPhoneNo())) {
                    llDetails.setVisibility(View.GONE);
                    tvDetailsNotEntered.setVisibility(View.GONE);
                    blueDot.setVisibility(View.GONE);
                }
                if (senderAddress.equalsIgnoreCase(callData.getStartAddress())) {
                    tvDetailsAddress.setVisibility(View.GONE);
                }
                if (senderName.equalsIgnoreCase(callData.getPassName())) {
                    tvCustomerName.setVisibility(View.GONE);
                }
                if (senderPhone.equalsIgnoreCase(callData.getPhoneNo())) {
                    ivCustomerPhone.setVisibility(View.GONE);
                }
            }
        }

        if (StringUtils.isNotEmpty(callData.getOrder_no())) {
            tvOrderNumber.setVisibility(View.VISIBLE);
            String orderNumber = getString(R.string.order_number) + StringUtils.SPACE + callData.getOrder_no();
            tvOrderNumber.setText(orderNumber);
        }

    }

    /**
     * Use to set either sender or reciever value which is available.
     *
     * @param textField     : Widget Reference
     * @param senderField   : Sender Value (Name, Address or Phone)
     * @param receiverField : Receiver Value (Name, Address or Phone)
     * @param format        : format to display text in fields
     */
    public void setAddressDetailEitherSenderOrReceiver(FontTextView textField, String senderField, String receiverField, String format) {
        if (StringUtils.isNotBlank(receiverField)) {
            textField.setVisibility(View.VISIBLE);
            textField.setText(String.format(format, receiverField));
        } else if (StringUtils.isNotBlank(senderField)) {
            textField.setVisibility(View.VISIBLE);
            textField.setText(senderField);
            textField.setText(String.format(format, senderField));
        } else {
            textField.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBykeaCashAmountUpdated() {
        AppPreferences.setCallData(callData);
        int cashKiWasooliValue = callData.getCashKiWasooli();
        cashKiWasooliValue = cashKiWasooliValue + Integer.valueOf(callData.getCodAmountNotFormatted().trim());
        tvCodAmount.setText(String.format(getString(R.string.amount_rs), String.valueOf(cashKiWasooliValue)));
    }

    /**
     * Download audio resource via Amazon SDK
     *
     * @param url Url to download from
     */
    private void voiceClipPlayDownload(String url) {
        if (mediaPlayer != null) {
            imgViewAudioPlay.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_audio_stop));
            imgViewAudioPlay.setEnabled(false);
            progressBarForAudioPlay.setVisibility(View.VISIBLE);
            mediaPlayer.start();
            startPlayProgressUpdater();
        } else {
            Dialogs.INSTANCE.showLoader(mCurrentActivity);
            if (AppPreferences.getDriverSettings() != null &&
                    AppPreferences.getDriverSettings().getData() != null &&
                    StringUtils.isNotBlank(AppPreferences.getDriverSettings().getData().getS3BucketVoiceNotes())) {
                BykeaAmazonClient.INSTANCE.getFileObject(url, new com.bykea.pk.partner.utils.audio.Callback<File>() {
                    @Override
                    public void success(File obj) {
                        Dialogs.INSTANCE.dismissDialog();
                        imgViewAudioPlay.setImageDrawable(ContextCompat.getDrawable(mCurrentActivity, R.drawable.ic_audio_stop));
                        imgViewAudioPlay.setEnabled(false);
                        progressBarForAudioPlay.setVisibility(View.VISIBLE);
                        mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(new FileInputStream(obj).getFD());
                            mediaPlayer.prepare();
                            progressBarForAudioPlay.setMax(mediaPlayer.getDuration());
                            mediaPlayer.start();
                            startPlayProgressUpdater();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void fail(int errorCode, @NotNull String errorMsg) {
                        Dialogs.INSTANCE.dismissDialog();
                        Dialogs.INSTANCE.showToast(getString(R.string.no_voice_note_available));
                    }
                }, AppPreferences.getDriverSettings().getData().getS3BucketVoiceNotes());
            } else {
                Dialogs.INSTANCE.dismissDialog();
                Dialogs.INSTANCE.showToast(getString(R.string.settings_are_not_updated));
            }
        }
    }


    /**
     * Handle to maintain the status for progress bar
     */
    public void startPlayProgressUpdater() {
        if (mediaPlayer != null) {
            progressBarForAudioPlay.setProgress(mediaPlayer.getCurrentPosition());
            if (mediaPlayer.isPlaying()) {
                Runnable notification = this::startPlayProgressUpdater;
                voiceNoteHandler.postDelayed(notification, DIGIT_THOUSAND);
            } else {
                mediaPlayer.pause();
                imgViewAudioPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_audio_play));
                imgViewAudioPlay.setEnabled(true);
                progressBarForAudioPlay.setVisibility(View.GONE);
                progressBarForAudioPlay.setProgress(DIGIT_ZERO);
            }
        }
    }
}
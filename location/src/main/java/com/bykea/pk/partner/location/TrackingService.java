//package com.bykea.pk.partner.location;
//
//import android.Manifest;
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.content.res.Configuration;
//import android.location.Location;
//import android.location.LocationManager;
//import android.os.Binder;
//import android.os.Bundle;
//import android.os.CountDownTimer;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.os.IBinder;
//import android.service.notification.StatusBarNotification;
//import android.util.Log;
//
//import androidx.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//import androidx.core.app.NotificationCompat;
//
//import com.bykea.pk.partner.DriverApp;
//import com.bykea.pk.partner.R;
//import com.bykea.pk.partner.dal.source.pref.AppPref;
//import com.bykea.pk.partner.models.data.MultiDeliveryCallDriverData;
//import com.bykea.pk.partner.models.response.GoogleDistanceMatrixApi;
//import com.bykea.pk.partner.models.response.LocationResponse;
//import com.bykea.pk.partner.models.response.MultipleDeliveryBookingResponse;
//import com.bykea.pk.partner.models.response.NormalCallData;
//import com.bykea.pk.partner.models.response.PilotStatusResponse;
//import com.bykea.pk.partner.repositories.UserDataHandler;
//import com.bykea.pk.partner.repositories.UserRepository;
//import com.bykea.pk.partner.repositories.places.PlacesDataHandler;
//import com.bykea.pk.partner.repositories.places.PlacesRepository;
//import com.bykea.pk.partner.ui.activities.SplashActivity;
//import com.bykea.pk.partner.ui.helpers.AppPreferences;
//import com.bykea.pk.partner.utils.Connectivity;
//import com.bykea.pk.partner.utils.Constants;
//import com.bykea.pk.partner.dal.util.HTTPStatus;
//import com.bykea.pk.partner.common.Keys;
//import com.bykea.pk.partner.common.TripStatus;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.maps.model.LatLng;
//
//import org.apache.commons.lang3.StringUtils;
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//
//import java.util.List;
//
//public class TrackingService extends Service {
//
//    private final String TAG = TrackingService.class.getSimpleName();
//
//    private final IBinder mBinder = new LocalBinder();
//    private final int DISTANCE_MATRIX_API_CALL_TIME = 6;
//    private final int LOCATION_INTERVAL = 5000;
//    private final int LOCATION_DISTANCE = 100;
//    private final int NOTIF_ID = 877;
//    private final float DIRECTION_API_CALL_DISTANCE = 15; //meter
//
//    private String STATUS = StringUtils.EMPTY;
//    private Context mContext;
//    private UserRepository mUserRepository;
//    private LocationRequest mLocationRequest;
//    private boolean shouldCallLocApi = true;
//    private int counter = 0;
//    private EventBus mBus = EventBus.getDefault();
//    private LocationListener mLocationListener;
//    private LocationManager mLocationManager;
//    private LatLng lastApiCallLatLng;
//    private Handler mServiceHandler;
//    private CountDownTimer mCountDownLocationTimer = new CountDownTimer(10000, 1000) {
//        @Override
//        public void onTick(long millisUntilFinished) {
//            if (Connectivity.isConnectedFast(mContext)) {
//                if (AppPref.INSTANCE.isLoggedIn()) {
//                    DriverApp.getApplication().connect();
//                }
//            }
//        }
//
//
//        @Override
//        public void onFinish() {
//            Log.e(TAG, "CountDown Timer onFinish: called ");
//            if (Utils.INSTANCE.canSendLocation()) {
//                synchronized (this) {
//                    double lat = AppPref.INSTANCE.getLat();
//                    double lon = AppPref.INSTANCE.getLng();
//                    boolean isMock = AppPref.INSTANCE.isFromMockLocation();
//                    if (lat != 0.0 && lon != 0.0 && !isMock) {
//                        updateTripRouteList(lat, lon);
//
//                        //we need to add Route LatLng in 10 sec, and call requestLocationUpdate after 20 sec
//                        if (shouldCallLocApi) {
//                            shouldCallLocApi = false;
//                            if (Connectivity.isConnectedFast(mContext) && Utils.isGpsEnable()) {
//                                //mUserRepository.updateDriverLocation(mContext, handler, lat, lon);
//                                //validateDriverOfflineStatus();
//                                mUserRepository.requestLocationUpdate(mContext, handler, lat, lon);
//
//                            } else {
//                                Utils.redLogLocation("request failed", "WiFi -> " +
//                                        Connectivity.isConnectedFast(mContext)
//                                        + " && GPS -> " + Utils.isGpsEnable());
//                            }
//                        } else {
//                            shouldCallLocApi = true;
//                        }
//                    }
//                    Log.e(TAG, "CountDown Timer restarted: called ");
//                    mCountDownLocationTimer.start();
//                    Log.e(TAG, "updateETAIfRequired: called ");
//                    updateETAIfRequired();
//                }
//            } else if (Utils.hasLocationCoordinates()) {
//                stopForegroundService();
//            } else {
//                Log.e(TAG, "CountDown Timer restarted: called ");
//                mCountDownLocationTimer.start();
//            }
//
//        }
//    };
//    private UserDataHandler handler = new UserDataHandler() {
//        @Override
//        public void onLocationUpdate(LocationResponse response) {
//            super.onLocationUpdate(response);
//            if (response.isSuccess()) {
//                AppPref.INSTANCE.setDriverOfflineForcefully(false);
//                AppPref.INSTANCE.setLocationSocketNotReceivedCount(Constants.LOCATION_RESPONSE_COUNTER_RESET);
//                mBus.post(Keys.ACTIVE_FENCE);
//            } else {
//                handleLocationErrorUseCase(response);
//            }
//
//        }
//
//        @Override
//        public void onUpdateStatus(final PilotStatusResponse pilotStatusResponse) {
//            if (pilotStatusResponse.isSuccess()) {
//                AppPref.INSTANCE.setAvailableStatus(false);
//                AppPref.INSTANCE.setAvailableAPICalling(false);
//                AppPref.INSTANCE.setDriverDestination(null);
//                AppPref.INSTANCE.setCash(pilotStatusResponse.getPilotStatusData().isCashValue());
//            } else {
//                AppPref.INSTANCE.setAvailableStatus(false);
//                AppPref.INSTANCE.setDriverDestination(null);
//            }
//            //make service offline as driver is offline now
//            updateServiceForDriverOfflineStatus();
//        }
//
//
//        @Override
//        public void onError(int errorCode, String errorMessage) {
//            switch (errorCode) {
//                case HTTPStatus.UNAUTHORIZED:
//                    EventBus.getDefault().post(Keys.UNAUTHORIZED_BROADCAST);
//                    break;
//            }
//        }
//    };
//
//    public TrackingService() {
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        Utils.redLogLocation(TAG, "onCreate");
//        startForeground(NOTIF_ID, createForegroundNotification());
//        configureInitialServiceProcess();
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        mContext = getApplicationContext();
//        if (!hasForeGroundNotification()) {
//            Utils.redLogLocation(TAG, "onStartCommand (!hasForeGroundNotification)");
//            startForeground(NOTIF_ID, createForegroundNotification());
//        } else {
//            Utils.redLogLocation(TAG, "onStartCommand (hasForeGroundNotification)");
//        }
//        createLocationRequest();
//        requestLocationUpdates();
//        cancelTimer();
//
//        mCountDownLocationTimer.start();
//        //DriverETAService.startDriverETAUpdate(this);
//        //DriverLocationUpdateJob.scheduleLocationUpdateJob();
//        //DriverETAUpdateJob.scheduleDriverETAJob();
//        if (intent == null || Constants.Actions.STARTFOREGROUND_ACTION.equals(intent.getAction())) {
//            if (intent != null && intent.getExtras() != null && intent.hasExtra(Constants.Extras.LOCATION_SERVICE_STATUS)) {
//                STATUS = intent.getStringExtra(Constants.Extras.LOCATION_SERVICE_STATUS);
//            }
//        } else if (Constants.Actions.STOPFOREGROUND_ACTION.equals(intent.getAction())) {
//            stopForegroundService();
//        } else if (Constants.Actions.UPDATE_FOREGROUND_NOTIFICATION.equals(intent.getAction())) {
//            updateForegroundNotification();
//        }
//        checkIfLocationUpdateCustomIntervalShouldSet(intent);
//        return START_STICKY;
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        Utils.redLogLocation(TAG, "in onBind()");
//        //stopForeground(true);
//        return mBinder;
//    }
//
//    @Override
//    public void onRebind(Intent intent) {
//        Utils.redLogLocation(TAG, "in onRebind()");
//        //stopForeground(true);
//        super.onRebind(intent);
//    }
//
//    @Override
//    public boolean onUnbind(Intent intent) {
//        Utils.redLogLocation(TAG, "Last client unbound from service");
//        return true; // Ensures onRebind() is called when a client re-binds.
//    }
//
//    @Override
//    public void onDestroy() {
//        Utils.redLogLocation(TAG, "onDestroy");
//        mServiceHandler.removeCallbacksAndMessages(null);
//        stopForeground(true);
//        stopLocationUpdates();
//        cancelTimer();
//    }
//
//    /***
//     * Setup service initial configuration process with following steps.
//     * 1) Fused Location provider client setup.
//     * 2) Register Location callback for fetch location.
//     * 3) Setup HandlerTread and Service Handler.
//     * 4) Register Event bus.
//     * 5) Create API Repository object.
//     */
//    private void configureInitialServiceProcess() {
//        mBus.register(this);
//        mUserRepository = new UserRepository();
//        mLocationListener = new LocationListener(LocationManager.GPS_PROVIDER);
//        if (mLocationManager == null) {
//            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
//        }
//        getLastLocation();
//        HandlerThread handlerThread = new HandlerThread(TAG);
//        handlerThread.start();
//        mServiceHandler = new Handler(handlerThread.getLooper());
//    }
//
//    /**
//     * Makes a request for location updates.
//     * {@link SecurityException}.
//     */
//    public void requestLocationUpdates() {
//        Utils.redLogLocation(TAG, "Requesting location updates");
//        if (ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            try {
//                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListener);
//            } catch (SecurityException ex) {
//                Log.i(TAG, "fail to request location update, ignore", ex);
//            } catch (IllegalArgumentException ex) {
//                Log.d(TAG, "gps provider does not exist " + ex.getMessage());
//            } catch (RuntimeException ex) {
//                Log.d(TAG, "Runtime exception " + ex.getMessage());
//            } catch (Exception ex) {
//                Log.d(TAG, "Generic exception " + ex.getMessage());
//            }
//        }
//    }
//
//    /**
//     * Send location broadcast.
//     *
//     * @param location Latest location fetched.
//     */
//    private void sendLocationBroadcast(Location location) {
//        Intent locationIntent = new Intent(Keys.LOCATION_UPDATE_BROADCAST);
//        locationIntent.putExtra("lng", location.getLongitude());
//        locationIntent.putExtra("lat", location.getLatitude());
//        locationIntent.putExtra("location", location);
//        locationIntent.putExtra("bearing", location.bearingTo(location) + "");
//        sendBroadcast(locationIntent);
//    }
//
//    /**
//     * this method checks whether location update request should be customize when on trip
//     *
//     * @param intent is provide by onStartCommand with custom data
//     */
//    private void checkIfLocationUpdateCustomIntervalShouldSet(@Nullable Intent intent) {
//        if (intent != null && intent.getExtras() != null && intent.hasExtra(Constants.Extras.ON_TRIP_LOCATION_UPDATE_CUSTOM_INTERVAL)) {
//            Log.e(TAG, "------- Custom location update ON TRIP -------");
//            long updateInterval = intent.getLongExtra(Constants.Extras.ON_TRIP_LOCATION_UPDATE_CUSTOM_INTERVAL,
//                    Constants.ON_TRIP_UPDATE_INTERVAL_IN_MILLISECONDS_DEFAULT);
//            createLocationRequestForOnTrip(updateInterval);
//            requestLocationUpdates();
//            cancelTimer();
//            mCountDownLocationTimer.start();
//        }
//    }
//
//    /**
//     * Stop foreground service
//     */
//    private void stopForegroundService() {
//        stopLocationUpdates();
//        cancelTimer();
//        stopForeground(true);
//        stopSelf();
//    }
//
//    /**
//     * This method generates Foreground Notification when Location Service is Running.
//     *
//     * @return Notification
//     */
//    private Notification createForegroundNotification() {
//        Intent notificationIntent = new Intent(this, SplashActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//                notificationIntent, 0);
//
//        String contentBodyMessage = getNotificationMsg();
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
//                Utils.getChannelIDForOnGoingNotification(this))
//                .setContentTitle(Constants.Notification.NOTIFICATION_CONTENT_TITLE)
//                .setContentText(contentBodyMessage)
//                .setOngoing(true)
//                .setPriority(Notification.PRIORITY_HIGH)
//                .setSmallIcon(R.drawable.ic_stat_onesignal_default)
//                .setContentIntent(pendingIntent)
//                .setWhen(System.currentTimeMillis())
//                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText(contentBodyMessage));
//        return builder.build();
//    }
//
//    /**
//     * Create Notification message for driver foreground service.
//     * Message contains driver status i.e. (Active/In-Active/Fetching Location) and Trip status if its during trip.
//     *
//     * @return generated notification message which would be displayed to user.
//     */
//    private String getNotificationMsg() {
//
//        boolean isDriverLogin = AppPref.INSTANCE.isLoggedIn();
//        boolean driverStatusAvailable = AppPref.INSTANCE.getAvailableStatus();
//        boolean driverOnTrip = AppPref.INSTANCE.isOnTrip();
//        String notificationBodyMessage = "";
//
//        if (!isDriverLogin && !driverStatusAvailable) {
//            notificationBodyMessage = getResources().getString(R.string.notification_title_driver_logout_location);
//        } else if (isDriverLogin && driverOnTrip) {
//            String tripNo = StringUtils.EMPTY;
//            String status = StringUtils.EMPTY;
//            if (StringUtils.isBlank(AppPref.INSTANCE.getDeliveryType())) return StringUtils.EMPTY;
//            if (AppPref.INSTANCE.getDeliveryType().
//                    equalsIgnoreCase(Constants.CallType.SINGLE)) {
//                NormalCallData callData = AppPref.INSTANCE.getCallData();
//                if (callData != null) {
//                    tripNo = (callData.getTripNo() != null) ? callData.getTripNo() : StringUtils.EMPTY;
//                    status = (callData.getStatus() != null) ? callData.getStatus() : StringUtils.EMPTY;
//                }
//            } else {
//                MultiDeliveryCallDriverData callDriverData = AppPref.INSTANCE.getMultiDeliveryCallDriverData();
//                status = (callDriverData != null) ? callDriverData.getBatchStatus() : StringUtils.EMPTY;
//                List<MultipleDeliveryBookingResponse> bookingResponseList = callDriverData.getBookings();
//                int n = (bookingResponseList != null) ? bookingResponseList.size() : 0;
//
//                int i = 0;
//                while (i < n) {
//                    tripNo += bookingResponseList.get(i).getTrip().getTripNo();
//                    i++;
//                    if (i != n)
//                        tripNo += ", ";
//                }
//            }
//            notificationBodyMessage = getResources().getString(R.string.notification_title_driver_trip,
//                    tripNo,
//                    StringUtils.capitalize(status));
//        } else if (isDriverLogin && driverStatusAvailable) {
//            notificationBodyMessage = getResources().getString(R.string.notification_title_driver_status,
//                    Constants.Driver.STATUS_ACTIVE);
//        } else if (!driverStatusAvailable) {
//            notificationBodyMessage = getResources().getString(R.string.notification_title_driver_login_location);
//        }
//        return notificationBodyMessage;
//    }
//
//    /**
//     * This method checks if our fore ground notification is being displayed in notification bar or not
//     */
//    private boolean hasForeGroundNotification() {
//        boolean hasForeGroundNotification = true;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//            hasForeGroundNotification = false;
//            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            if (mNotificationManager != null) {
//                StatusBarNotification[] notifications = mNotificationManager.getActiveNotifications();
//                for (StatusBarNotification notification : notifications) {
//                    if (notification.getId() == NOTIF_ID) {
//                        hasForeGroundNotification = true;
//                        break;
//                    }
//                }
//            }
//
//        }
//        return hasForeGroundNotification;
//
//    }
//
//    /**
//     * updates notification during trip according to its current status
//     */
//    private void updateForegroundNotification() {
//        Notification notification = createForegroundNotification();
//        NotificationManager mNotificationManager = (NotificationManager)
//                getSystemService(Context.NOTIFICATION_SERVICE);
//        if (mNotificationManager != null) {
//            mNotificationManager.notify(NOTIF_ID, notification);
//        }
//    }
//
//    /**
//     * Get Last known location if its available in Fused Location client.
//     */
//    private void getLastLocation() {
//        try {
//            Utils.redLogLocation(TAG, " getLastLocation() called");
//            if (ActivityCompat.checkSelfPermission(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                Location location = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
//                onNewLocation(location);
//            }
//        } catch (SecurityException unlikely) {
//            Utils.redLogLocation(TAG, "Lost location permission." + unlikely);
//        }
//    }
//
//    /**
//     * Handles fetched location and save's it inside shared preference and send local broadcast.
//     * We filter out mock location.
//     *
//     * @param location Location object which contains latest fetched location.
//     */
//    private void onNewLocation(Location location) {
//        if (location != null) {
//            if (!Utils.isMockLocation(location, mContext)) {
//                AppPref.INSTANCE.saveLocation(new LatLng(location.getLatitude(),
//                                location.getLongitude()), "" + location.getBearing(),
//                        location.getAccuracy(), false);
//                sendLocationBroadcast(location);
//                Utils.redLogLocation(TAG, location.getLatitude() + "," +
//                        location.getLongitude() + "  (" + Utils.getUTCDate(location.getTime()) + ")");
//            } else {
//                Utils.redLogLocation(TAG, "Mock location Received...");
//                EventBus.getDefault().post(Keys.MOCK_LOCATION);
//            }
//        }
//    }
//
//    /**
//     * Create location request with update interval and fastest update interval values.
//     * We always request location Priority as High Accuracy and has smallest displacement value as well for locations.
//     */
//    protected void createLocationRequest() {
//        //int UPDATE_INTERVAL = 10000;
//        //int FASTEST_INTERVAL = 5000;
//        mLocationRequest = LocationRequest.create();
//        mLocationRequest.setInterval(Constants.UPDATE_INTERVAL_IN_MILLISECONDS);
//        mLocationRequest.setFastestInterval(Constants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
//        if (AppPref.INSTANCE.isOnTrip()) {
//            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        } else {
//            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//        }
//
//        /*if (Utils.hasLocationCoordinates()) {
//            mLocationRequest.setSmallestDisplacement(Constants.LOCATION_SMALLEST_DISPLACEMENT);
//        }*/
//    }
//
//    /**
//     * Create location update request with custom when ON TRIP
//     *
//     * @param updateInterval custom interval in millis
//     */
//    protected void createLocationRequestForOnTrip(long updateInterval) {
//        mLocationRequest = LocationRequest.create();
//        mLocationRequest.setInterval(updateInterval);
//        mLocationRequest.setFastestInterval(updateInterval / Constants.ON_TRIP_UPDATE_INTERVAL_DIVISIBLE);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//    }
//
//    protected void stopLocationUpdates() {
//        try {
////            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
//            mLocationManager.removeUpdates(mLocationListener);
//        } catch (Exception ignored) {
//        }
//    }
//
//    /**
//     *  Update latitude and longitude and distance preview time in shared preference.
//     * @param lat current latitude fetched.
//     * @param lon current longitude fetched.
//     * @param updatePrevTime should updated previous time.
//     */
//    private void addLatLng(double lat, double lon, boolean updatePrevTime) {
//        AppPref.INSTANCE.addLocCoordinateInTrip(lat, lon, STATUS);
//        AppPref.INSTANCE.setPrevDistanceLatLng(lat, lon, updatePrevTime);
//        STATUS = StringUtils.EMPTY;
//    }
//
//    public void updateTripRouteList(double lat, double lon) {
//        Utils.redLogLocation("TripStatus", AppPref.INSTANCE.getTripStatus());
//        if (TripStatus.ON_START_TRIP.equalsIgnoreCase(AppPref.INSTANCE.getTripStatus()) ||
//                TripStatus.ON_ACCEPT_CALL.equalsIgnoreCase(AppPref.INSTANCE.getTripStatus())) {
//            synchronized (this) {
//                String lastLat = AppPref.INSTANCE.getPrevDistanceLatitude();
//                String lastLng = AppPref.INSTANCE.getPrevDistanceLongitude();
//                if (!lastLat.equalsIgnoreCase("0.0") && !lastLng.equalsIgnoreCase("0.0")) {
//                    float distance = Utils.calculateDistance(lat, lon, Double.parseDouble(lastLat), Double.parseDouble(lastLng));
//                    if (Utils.isValidLocation(/*lat, lon, Double.parseDouble(lastLat), Double.parseDouble(lastLng), */distance)) {
//                        addLatLng(lat, lon, distance > 0f);
//                        //Removing Google Directions API call to avoid duplicate GPS entries. Check https://bykeapk.atlassian.net/browse/BS-1042 for details
////                        if ((distance > 1000) && !isDirectionApiRunning) {
////                            getRouteLatLng(lat, lon, lastLat, lastLng);
////                        }
//                    } else {
//                        addLatLng(Double.parseDouble(lastLat), Double.parseDouble(lastLng), false);
//                    }
//                } else {
//                    addLatLng(lat, lon, true);
//                }
//            }
//        }
//
//
//    }
//
//    /**
//     * when Booking Screen is in background & driver is in any trip then, we need to call distance
//     * matrix API in order to get Estimated time & distance, when booking screen is in foreground it
//     * is already being handled via Direction API when we are showing Route to driver.
//     * counter == DISTANCE_MATRIX_API_CALL_TIME == 6 indicates that API will be called after 60 sec
//     */
//    private void updateETAIfRequired() {
//        if (counter == DISTANCE_MATRIX_API_CALL_TIME) {
//            counter = 0;
//        }
//        counter++;
//        if (AppPref.INSTANCE.isOnTrip() && !AppPref.INSTANCE.isJobActivityOnForeground() && counter == DISTANCE_MATRIX_API_CALL_TIME) {
//            Utils.redLogLocation("Direction -> Trip Status ", AppPref.INSTANCE.getTripStatus());
//            if (TripStatus.ON_START_TRIP.equalsIgnoreCase(AppPref.INSTANCE.getTripStatus())) {
//                NormalCallData callData = AppPref.INSTANCE.getCallData();
//                if (callData != null && StringUtils.isNotBlank(callData.getEndLat()) &&
//                        StringUtils.isNotBlank(callData.getEndLng())) {
//                    String destination = callData.getEndLat() + "," + callData.getEndLng();
//                    callDistanceMatrixApi(destination);
//                } else {
//                    //in case when there is no drop off add distance covered and time taken
//                    updateETA(Utils.getTripTime(), Utils.getTripDistance());
//
//                }
//            } else if (TripStatus.ON_ACCEPT_CALL.equalsIgnoreCase(AppPref.INSTANCE.getTripStatus())
//                    || TripStatus.ON_ARRIVED_TRIP.equalsIgnoreCase(AppPref.INSTANCE.getTripStatus())) {
//                NormalCallData callData = AppPref.INSTANCE.getCallData();
//                if (callData != null && StringUtils.isNotBlank(callData.getStartLat()) &&
//                        StringUtils.isNotBlank(callData.getStartLng())) {
//                    String destination = callData.getStartLat() + "," + callData.getStartLng();
//                    callDistanceMatrixApi(destination);
//                }
//            }
//
//        }
//    }
//
//    /**
//     * Update ETA time and distance in shared preference.
//     *
//     * @param time     updated time which needs to be saved.
//     * @param distance updated distance which needs to be saved.
//     */
//    private void updateETA(String time, String distance) {
//        AppPref.INSTANCE.setEta(time);
//        AppPref.INSTANCE.setEstimatedDistance(distance);
//        mBus.post(Keys.ETA_IN_BG_UPDATED);
//    }
//
//    /**
//     * Send request to DistanceMatrix API.
//     * @param destination destination request address
//     */
//    private void callDistanceMatrixApi(String destination) {
//        LatLng newLatLng = new LatLng(AppPref.INSTANCE.getLatitude(), AppPref.INSTANCE.getLongitude());
//        if (isDirectionApiCallRequired(newLatLng) && Connectivity.isConnected(mContext)) {
//            lastApiCallLatLng = newLatLng;
//            String origin = newLatLng.latitude + "," + newLatLng.longitude;
//            new PlacesRepository().getDistanceMatrix(origin, destination, mContext,
//                    new PlacesDataHandler() {
//                        @Override
//                        public void onDistanceMatrixResponse(GoogleDistanceMatrixApi response) {
//                            if (response != null && response.getRows() != null
//                                    && response.getRows().length > 0
//                                    && response.getRows()[0].getElements() != null
//                                    && response.getRows()[0].getElements().length > 0
//                                    && response.getRows()[0].getElements()[0].getDuration() != null
//                                    && response.getRows()[0].getElements()[0].getDistance() != null) {
//                                String time = (response.getRows()[0]
//                                        .getElements()[0].getDuration().getValueInt() / 60) + "";
//                                String distance = Utils.formatDecimalPlaces(
//                                        (response.getRows()[0].getElements()[0]
//                                                .getDistance().getValueInt() / 1000.0) + "", 1);
//                                updateETA(time, distance);
//                                Utils.redLogLocation("onDistanceMatrixResponse",
//                                        "Time -> " + time + " Distance ->" + distance);
//                            }
//                        }
//                    });
//        }
//    }
//
//    /**
//     * check if last start latlng and current start latlng has at least 15 m difference
//     * @param currentApiCallLatLng current Latitude and Longitude returned from API
//     * @return True is current lat/lng and start lat/lng have 15m difference else return false
//     */
//    private boolean isDirectionApiCallRequired(LatLng currentApiCallLatLng) {
//        if (lastApiCallLatLng != null &&
//                Utils.calculateDistance(currentApiCallLatLng.latitude,
//                        currentApiCallLatLng.longitude,
//                        lastApiCallLatLng.latitude,
//                        lastApiCallLatLng.longitude
//                ) < DIRECTION_API_CALL_DISTANCE) {
//            return false;
//        } else {
//            return true;
//        }
//    }
//
//    /**
//     * Sending driver offline status request to API server.
//     * When we don't receive any response from socket event for driver update
//     * latitude and longitude event after grace retry period
//     * we forcefully send driver offline request.
//     */
//    private void sendDriverOfflineStatusRequest() {
//        AppPref.INSTANCE.setAvailableAPICalling(true);
//        mUserRepository.requestDriverUpdateStatus(this, handler, false);
//    }
//
//    /**
//     * Cancel count down timer
//     */
//    private void cancelTimer() {
//        if (mCountDownLocationTimer != null) {
//            mCountDownLocationTimer.cancel();
//        }
//    }
//
//    /**
//     * Stops location updates and cancels countdown timer which is used to sending Lat/Lng value to API server via socket.
//     * And updates foreground notification to reflect driver offline status.
//     */
//    private void updateServiceForDriverOfflineStatus() {
//        stopLocationUpdates();
//        cancelTimer();
//        updateForegroundNotification();
//    }
//
//    /**
//     * update location when socket is reconnected, this will sync/update socket id on server
//     */
//    @Subscribe
//    public void onEvent(String event) {
//        if (Constants.ON_SOCKET_CONNECTED.equalsIgnoreCase(event)) {
//            if (Utils.INSTANCE.canSendLocation()) {
//                synchronized (this) {
//                    double lat = AppPref.INSTANCE.getLatitude();
//                    double lon = AppPref.INSTANCE.getLongitude();
//                    boolean isMock = AppPref.INSTANCE.isFromMockLocation();
//                    if (lat != 0.0 && lon != 0.0 && !isMock) {
//                        if (Connectivity.isConnectedFast(mContext) && Utils.isGpsEnable()) {
//                            Utils.redLogLocation(TAG, "onSocketConnected");
////                            mUserRepository.requestLocationUpdate(mContext, handler, lat, lon);
//                        }
//                    }
//                }
//            }
//        } else if (Constants.Broadcast.UPDATE_FOREGROUND_NOTIFICATION.equalsIgnoreCase(event)) {
//            updateForegroundNotification();
//        }
//    }
//
//    /**
//     * Handle Location API Error case for API failures.
//     *
//     * @param locationResponse latest response from server.
//     */
//    private void handleLocationErrorUseCase(LocationResponse locationResponse) {
//        if (locationResponse != null) {
//            switch (locationResponse.getCode()) {
//                case Constants.ApiError.BUSINESS_LOGIC_ERROR: {
//                    Utils.handleLocationBusinessLogicErrors(mBus, locationResponse);
//                    break;
//                }
//                //TODO Will update unauthorized check on error callback when API team adds 401 status code in their middle layer.
//                case HTTPStatus.UNAUTHORIZED: {
//                    EventBus.getDefault().post(Keys.UNAUTHORIZED_BROADCAST);
//                    break;
//                }
//                default:
//                    Utils.appToast(locationResponse.getMessage());
//            }
//        }
//
//    }
//
//    /**
//     * Implementation of Location Listener
//     */
//    private class LocationListener implements android.location.LocationListener {
//        private final String TAG = "LocationListener";
//        private Location location;
//
//        public LocationListener(String provider) {
//            location = new Location(provider);
//        }
//
//        @Override
//        public void onLocationChanged(Location location) {
//            Log.i(TAG, "LocationChanged: " + location);
//            this.location = location;
//            onNewLocation(this.location);
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//            Log.e(TAG, "onProviderDisabled: " + provider);
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//            Log.e(TAG, "onProviderEnabled: " + provider);
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//            Log.e(TAG, "onStatusChanged: " + status);
//        }
//    }
//
//    /**
//     * Class used for the client Binder.  Since this service runs in the same process as its
//     * clients, we don't need to deal with IPC.
//     */
//    public class LocalBinder extends Binder {
//        public TrackingService getService() {
//            return TrackingService.this;
//        }
//    }
//
//}

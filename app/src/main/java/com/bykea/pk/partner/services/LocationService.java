package com.bykea.pk.partner.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;

import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.LocCoordinatesInTrip;
import com.bykea.pk.partner.models.response.DriverLocationResponse;
import com.bykea.pk.partner.models.response.GoogleDistanceMatrixApi;
import com.bykea.pk.partner.models.response.LocationResponse;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.repositories.places.PlacesDataHandler;
import com.bykea.pk.partner.repositories.places.PlacesRepository;
import com.bykea.pk.partner.tracking.AbstractRouting;
import com.bykea.pk.partner.tracking.Route;
import com.bykea.pk.partner.tracking.RouteException;
import com.bykea.pk.partner.tracking.Routing;
import com.bykea.pk.partner.tracking.RoutingListener;
import com.bykea.pk.partner.ui.activities.SplashActivity;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;


public class LocationService extends Service {
    private String STATUS = StringUtils.EMPTY;
    private Context mContext;
    private UserRepository mUserRepository;
    private LocationRequest mLocationRequest;
    private boolean shouldCallLocApi = true;

    private int counter = 0;
    private EventBus mBus = EventBus.getDefault();

    private FusedLocationProviderClient mFusedLocationClient;

    private LocationCallback mLocationCallback;

    private final int DISTANCE_MATRIX_API_CALL_TIME = 6;

    private final int NOTIF_ID = 877;
    private LatLng lastApiCallLatLng;

    private final String TAG = LocationService.class.getSimpleName();

    private final IBinder mBinder = new LocalBinder();

    private boolean isDirectionApiRunning;

    /*private BroadcastReceiver mDozeModeStatusReceiver;
    private WifiManager.WifiLock mWifiLock = null;
    private PowerManager.WakeLock mWakeLock = null;*/


    /**
     * Used to check whether the bound activity has really gone away and not unbound as part of an
     * orientation change. We create a foreground service notification only if the former takes
     * place.
     */
    private boolean mChangingConfiguration = false;

    private Handler mServiceHandler;

    /**
     * The current location.
     */
    private Location mLocation;

    public LocationService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Utils.redLogLocation(TAG, "onCreate");
        configureInitialServiceProcess();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = getApplicationContext();
        if (!hasForeGroundNotification()) {
            Utils.redLogLocation(TAG, "onStartCommand (!hasForeGroundNotification)");
            startForeground(NOTIF_ID, createForegroundNotification());
        } else {
            Utils.redLogLocation(TAG, "onStartCommand (hasForeGroundNotification)");
        }
        requestLocationUpdates();
        cancelTimer();
        mCountDownTimer.start();
        if (intent == null || Constants.Actions.STARTFOREGROUND_ACTION.equals(intent.getAction())) {
            if (intent != null && intent.getExtras() != null && intent.hasExtra(Constants.Extras.LOCATION_SERVICE_STATUS)) {
                STATUS = intent.getStringExtra(Constants.Extras.LOCATION_SERVICE_STATUS);
            }
        } else if (Constants.Actions.STOPFOREGROUND_ACTION.equals(intent.getAction())) {
            stopForegroundService();
        } else if (Constants.Actions.UPDATE_FOREGROUND_NOTIFICATION.equals(intent.getAction())) {
            updateForegroundNotification();
        }
        return START_STICKY;
    }


    //region General Helper methods for Service Setup and other methods

    /***
     * Setup service initial configuration process with following steps.
     * 1) Fused Location provider client setup.
     * 2) Register Location callback for fetch location.
     * 3) Setup HandlerTread and Service Handler.
     * 4) Register Event bus.
     * 5) Create API Repository object.
     */
    private void configureInitialServiceProcess() {
        mBus.register(this);
        mUserRepository = new UserRepository();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };
        createLocationRequest();
        getLastLocation();
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
    }

    /**
     * Send location broadcast.
     *
     * @param location Latest location fetched.
     */
    private void sendLocationBroadcast(Location location) {
        Intent locationIntent = new Intent(Keys.LOCATION_UPDATE_BROADCAST);
        locationIntent.putExtra("lng", location.getLongitude());
        locationIntent.putExtra("lat", location.getLatitude());
        locationIntent.putExtra("location", location);
        locationIntent.putExtra("bearing", location.bearingTo(location) + "");
        sendBroadcast(locationIntent);
    }

    /**
     * Makes a request for location updates.
     * {@link SecurityException}.
     */
    public void requestLocationUpdates() {
        Utils.redLogLocation(TAG, "Requesting location updates");
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                        mLocationCallback, Looper.myLooper());
            } catch (SecurityException unlikely) {
                Utils.redLogLocation(TAG, "Lost location permission. Could not request updates. " + unlikely);
            }
        }
    }

    /***
     * Stop foreground service
     */
    private void stopForegroundService() {
        stopLocationUpdates();
        cancelTimer();
        stopForeground(true);
        stopSelf();
    }

    //endregion

    //region Life Cycle events and binder override methods
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Utils.redLogLocation(TAG, "in onBind()");
        //stopForeground(true);
        mChangingConfiguration = false;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Utils.redLogLocation(TAG, "in onRebind()");
        //stopForeground(true);
        mChangingConfiguration = false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Utils.redLogLocation(TAG, "Last client unbound from service");
        return true; // Ensures onRebind() is called when a client re-binds.
    }

    @Override
    public void onDestroy() {
        Utils.redLogLocation(TAG, "onDestroy");
        mServiceHandler.removeCallbacksAndMessages(null);
        stopForeground(true);
        stopLocationUpdates();
        cancelTimer();
    }

    //endregion

    //region Helper methods for notification messages and display logic

    /**
     * This method generates Foreground Notification when Location Service is Running.
     *
     * @return Notification
     */
    private Notification createForegroundNotification() {
        Intent notificationIntent = new Intent(this, SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        String contentBodyMessage = getNotificationMsg();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                Utils.getChannelIDForOnGoingNotification(this))
                .setContentTitle(Constants.Notification.NOTIFICATION_CONTENT_TITLE)
                .setContentText(contentBodyMessage)
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(contentBodyMessage));
        return builder.build();
    }


    /***
     * Create Notification message for driver foreground service.
     * Message contains driver status i.e. (Active/In-Active/Fetching Location) and Trip status if its during trip.
     * @return generated notification message which would be displayed to user.
     */
    private String getNotificationMsg() {

        boolean isDriverLogin = AppPreferences.isLoggedIn();
        boolean driverStatusAvailable = AppPreferences.getAvailableStatus();
        boolean driverOnTrip = AppPreferences.isOnTrip();
        String notificationBodyMessage = "";

        if (!isDriverLogin && !driverStatusAvailable) {
            notificationBodyMessage = getResources().getString(R.string.notification_title_driver_logout_location);
        } else if (isDriverLogin && driverOnTrip) {
            NormalCallData callData = AppPreferences.getCallData();
            notificationBodyMessage = getResources().getString(R.string.notification_title_driver_trip,
                    callData.getTripNo(),
                    StringUtils.capitalize(callData.getStatus()));
        } else if (isDriverLogin && driverStatusAvailable) {
            notificationBodyMessage = getResources().getString(R.string.notification_title_driver_status,
                    Constants.Driver.STATUS_ACTIVE);
        } else if (!driverStatusAvailable) {
            notificationBodyMessage = getResources().getString(R.string.notification_title_driver_login_location);
        }
        return notificationBodyMessage;
    }


    /**
     * This method checks if our fore ground notification is being displayed in notification bar or not
     */

    private boolean hasForeGroundNotification() {
        boolean hasForeGroundNotification = true;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasForeGroundNotification = false;
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (mNotificationManager != null) {
                StatusBarNotification[] notifications = mNotificationManager.getActiveNotifications();
                for (StatusBarNotification notification : notifications) {
                    if (notification.getId() == NOTIF_ID) {
                        hasForeGroundNotification = true;
                        break;
                    }
                }
            }

        }
        return hasForeGroundNotification;

    }


    /**
     * updates notification during trip according to its current status
     */
    private void updateForegroundNotification() {
        Notification notification = createForegroundNotification();
        NotificationManager mNotificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null) {
            mNotificationManager.notify(NOTIF_ID, notification);
        }
    }


    //endregion

    //#region Helper methods for Location Permission and Request creation

    /***
     * Get Last known location if its available in Fused Location client.
     */
    private void getLastLocation() {
        try {
            Utils.redLogLocation(TAG, " getLastLocation() called");
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mFusedLocationClient.getLastLocation()
                        .addOnCompleteListener(new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    onNewLocation(task.getResult());
                                    Utils.redLogLocation(TAG, " getLastLocation() Success");
                                } else {
                                    Utils.redLogLocation(TAG, " getLastLocation() Error");
                                }
                            }
                        });
            }
        } catch (SecurityException unlikely) {
            Utils.redLogLocation(TAG, "Lost location permission." + unlikely);
        }
    }

    /**
     * Handles fetched location and save's it inside shared preference and send local broadcast.
     * We filter out mock location.
     *
     * @param location Location object which contains latest fetched location.
     */
    private void onNewLocation(Location location) {
        if (location != null) {
            if (!Utils.isMockLocation(location, mContext)) {
                AppPreferences.saveLocation(new LatLng(location.getLatitude(),
                                location.getLongitude()), "" + location.getBearing(),
                        location.getAccuracy(), false);
                sendLocationBroadcast(location);
                Utils.redLogLocation(TAG, location.getLatitude() + "," +
                        location.getLongitude() + "  (" + Utils.getUTCDate(location.getTime()) + ")");
            } else {
                Utils.redLogLocation(TAG, "Mock location Received...");
                EventBus.getDefault().post(Keys.MOCK_LOCATION);
            }
        }
    }

    /**
     * Create location request with update interval and fastest update interval values.
     * We always request location Priority as High Accuracy and has smallest displacement value as well for locations.
     */
    protected void createLocationRequest() {
        //int UPDATE_INTERVAL = 10000;
        //int FASTEST_INTERVAL = 5000;
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(Constants.UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(Constants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        if (AppPreferences.isOnTrip()) {
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        } else {
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        }

        /*if (Utils.hasLocationCoordinates()) {
            mLocationRequest.setSmallestDisplacement(Constants.LOCATION_SMALLEST_DISPLACEMENT);
        }*/
    }


    protected void stopLocationUpdates() {
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        } catch (Exception ignored) {
        }
    }

    //#endregion

    //region Helper methods for updating values in shared preference

    /**
     * Update ETA time and distance in shared preference.
     *
     * @param time     updated time which needs to be saved.
     * @param distance updated distance which needs to be saved.
     */
    private void updateETA(String time, String distance) {
        AppPreferences.setEta(time);
        AppPreferences.setEstimatedDistance(distance);
        mBus.post(Keys.ETA_IN_BG_UPDATED);
    }

    /***
     *  Update latitude and longitude and distance preview time in shared preference.
     * @param lat current latitude fetched.
     * @param lon current longitude fetched.
     * @param updatePrevTime should updated previous time.
     */
    private void addLatLng(double lat, double lon, boolean updatePrevTime) {
        AppPreferences.addLocCoordinateInTrip(lat, lon, STATUS);
        AppPreferences.setPrevDistanceLatLng(lat, lon, updatePrevTime);
        STATUS = StringUtils.EMPTY;
    }
    //endregion

    // region Helper methods for Trip/ETA/Distance API
    public void updateTripRouteList(double lat, double lon) {
        Utils.redLogLocation("TripStatus", AppPreferences.getTripStatus());
        if (TripStatus.ON_START_TRIP.equalsIgnoreCase(AppPreferences.getTripStatus()) ||
                TripStatus.ON_ACCEPT_CALL.equalsIgnoreCase(AppPreferences.getTripStatus())) {
            synchronized (this) {
                String lastLat = AppPreferences.getPrevDistanceLatitude();
                String lastLng = AppPreferences.getPrevDistanceLongitude();
                if (!lastLat.equalsIgnoreCase("0.0") && !lastLng.equalsIgnoreCase("0.0")) {
                    float distance = Utils.calculateDistance(lat, lon, Double.parseDouble(lastLat), Double.parseDouble(lastLng));
                    if (Utils.isValidLocation(/*lat, lon, Double.parseDouble(lastLat), Double.parseDouble(lastLng), */distance)) {
                        addLatLng(lat, lon, distance > 0f);
                        //Removing Google Directions API call to avoid duplicate GPS entries. Check https://bykeapk.atlassian.net/browse/BS-1042 for details
//                        if ((distance > 1000) && !isDirectionApiRunning) {
//                            getRouteLatLng(lat, lon, lastLat, lastLng);
//                        }
                    } else {
                        addLatLng(Double.parseDouble(lastLat), Double.parseDouble(lastLng), false);
                    }
                } else {
                    addLatLng(lat, lon, true);
                }
            }
        }


    }

    /**
     * when Booking Screen is in background & driver is in any trip then, we need to call distance
     * matrix API in order to get Estimated time & distance, when booking screen is in foreground it
     * is already being handled via Direction API when we are showing Route to driver.
     * counter == DISTANCE_MATRIX_API_CALL_TIME == 6 indicates that API will be called after 60 sec
     */
    private void updateETAIfRequired() {
        if (counter == DISTANCE_MATRIX_API_CALL_TIME) {
            counter = 0;
        }
        counter++;
        if (AppPreferences.isOnTrip() && !AppPreferences.isJobActivityOnForeground() && counter == DISTANCE_MATRIX_API_CALL_TIME) {
            Utils.redLogLocation("Direction -> Trip Status ", AppPreferences.getTripStatus());
            if (TripStatus.ON_START_TRIP.equalsIgnoreCase(AppPreferences.getTripStatus())) {
                NormalCallData callData = AppPreferences.getCallData();
                if (callData != null && StringUtils.isNotBlank(callData.getEndLat()) &&
                        StringUtils.isNotBlank(callData.getEndLng())) {
                    String destination = callData.getEndLat() + "," + callData.getEndLng();
                    callDistanceMatrixApi(destination);
                } else {
                    //in case when there is no drop off add distance covered and time taken
                    updateETA(Utils.getTripTime(), Utils.getTripDistance());

                }
            } else if (TripStatus.ON_ACCEPT_CALL.equalsIgnoreCase(AppPreferences.getTripStatus())
                    || TripStatus.ON_ARRIVED_TRIP.equalsIgnoreCase(AppPreferences.getTripStatus())) {
                NormalCallData callData = AppPreferences.getCallData();
                if (callData != null && StringUtils.isNotBlank(callData.getStartLat()) &&
                        StringUtils.isNotBlank(callData.getStartLng())) {
                    String destination = callData.getStartLat() + "," + callData.getStartLng();
                    callDistanceMatrixApi(destination);
                }
            }

        }
    }

    /***
     * check if last start latlng and current start latlng has at least 15 m difference
     * @param currentApiCallLatLng current Latitude and Longitude returned from API
     * @return True is current lat/lng and start lat/lng have 15m difference else return false
     */
    private boolean isDirectionApiCallRequired(LatLng currentApiCallLatLng) {
        if (lastApiCallLatLng != null &&
                Utils.calculateDistance(currentApiCallLatLng.latitude,
                        currentApiCallLatLng.longitude,
                        lastApiCallLatLng.latitude,
                        lastApiCallLatLng.longitude
                ) < 15) {
            return false;
        } else {
            return true;
        }
    }

    /***
     * Send request to DistanceMatrix API.
     * @param destination destination request address
     */
    private void callDistanceMatrixApi(String destination) {
        LatLng newLatLng = new LatLng(AppPreferences.getLatitude(), AppPreferences.getLongitude());
        if (isDirectionApiCallRequired(newLatLng) && Connectivity.isConnected(mContext)) {
            lastApiCallLatLng = newLatLng;
            String origin = newLatLng.latitude + "," + newLatLng.longitude;
            new PlacesRepository().getDistanceMatrix(origin, destination, mContext,
                    new PlacesDataHandler() {
                        @Override
                        public void onDistanceMatrixResponse(GoogleDistanceMatrixApi response) {
                            if (response != null && response.getRows() != null
                                    && response.getRows().length > 0
                                    && response.getRows()[0].getElements() != null
                                    && response.getRows()[0].getElements().length > 0
                                    && response.getRows()[0].getElements()[0].getDuration() != null
                                    && response.getRows()[0].getElements()[0].getDistance() != null) {
                                String time = (response.getRows()[0]
                                        .getElements()[0].getDuration().getValueInt() / 60) + "";
                                String distance = Utils.formatDecimalPlaces(
                                        (response.getRows()[0].getElements()[0]
                                                .getDistance().getValueInt() / 1000.0) + "", 1);
                                updateETA(time, distance);
                                Utils.redLogLocation("onDistanceMatrixResponse",
                                        "Time -> " + time + " Distance ->" + distance);
                            }
                        }
                    });
        }
    }

    private synchronized void getRouteLatLng(double lat, double lon, String lastLat, String lastLng) {
        isDirectionApiRunning = true;
        int index = AppPreferences.getLocCoordinatesInTrip().size();
        index = index > 1 ? index - 1 : 0;
        Routing.Builder builder = new Routing.Builder();
        if (StringUtils.isNotBlank(Utils.getApiKeyForDirections(mContext))) {
            builder.key(Utils.getApiKeyForDirections(mContext));
        }
        final int finalIndex = index;
        builder.context(mContext)
                .waypoints(new LatLng(Double.parseDouble(lastLat), Double.parseDouble(lastLng)), new LatLng(lat, lon))
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(new RoutingListener() {
                    @Override
                    public void onRoutingFailure(int routeType, RouteException e) {
                        isDirectionApiRunning = false;
                    }

                    @Override
                    public void onRoutingStart() {

                    }

                    @Override
                    public void onRoutingSuccess(int routeType, List<Route> route, int shortestRouteIndex) {
                        List<LatLng> latLngs = route.get(0).getPoints();
                        ArrayList<LocCoordinatesInTrip> locCoordinatesLatLng = new ArrayList<>();
                        for (LatLng latLng : latLngs) {
                            LocCoordinatesInTrip currentLatLng = new LocCoordinatesInTrip();
                            currentLatLng.setDate("" + Utils.getIsoDate());
                            currentLatLng.setLat("" + latLng.latitude);
                            currentLatLng.setLng("" + latLng.longitude);
                            locCoordinatesLatLng.add(currentLatLng);
                        }
                        AppPreferences.addLocCoordinateInTrip(locCoordinatesLatLng, finalIndex);
                        isDirectionApiRunning = false;
                    }

                    @Override
                    public void onRoutingCancelled() {
                        isDirectionApiRunning = false;
                    }
                })
                .routeType(Routing.onChangeRoute);
        Routing routing = builder.build();
        routing.execute();
    }

    //endregion

    //region  Countdown timer for sending location to server.
    private CountDownTimer mCountDownTimer = new CountDownTimer(10000, 4990) {
        @Override
        public void onTick(long millisUntilFinished) {
            if (Connectivity.isConnectedFast(mContext)) {
                DriverApp.getApplication().connect();
            }
        }


        @Override
        public void onFinish() {
            if (Utils.canSendLocation()) {
                synchronized (this) {
                    double lat = AppPreferences.getLatitude();
                    double lon = AppPreferences.getLongitude();
                    boolean isMock = AppPreferences.isFromMockLocation();
                    if (lat != 0.0 && lon != 0.0 && !isMock) {
                        updateTripRouteList(lat, lon);
                        updateETAIfRequired();
                        //we need to add Route LatLng in 10 sec, and call requestLocationUpdate after 20 sec
                        if (shouldCallLocApi) {
                            shouldCallLocApi = false;
                            if (Connectivity.isConnectedFast(mContext) && Utils.isGpsEnable(mContext)) {
                                //mUserRepository.updateDriverLocation(mContext, handler, lat, lon);
                                validateDriverOfflineStatus();
                                mUserRepository.requestLocationUpdate(mContext, handler, lat, lon);

                            } else {
                                Utils.redLogLocation("request failed", "WiFi -> " + Connectivity.isConnectedFast(mContext)
                                        + " && GPS -> " + Utils.isGpsEnable(mContext));
                            }
                        } else {
                            shouldCallLocApi = true;
                        }
                    }
                    // restart the timer
                    mCountDownTimer.start();
                }
            } else if (Utils.hasLocationCoordinates())

            {
                stopForegroundService();
            } else

            {
                // restart the timer
                mCountDownTimer.start();
            }

        }
    };

    /***
     * Validate driver offline status against location socket event.
     * If socket event is not received for more then allowed retry counter
     * we forcefully turn off driver and update UI.
     */
    public void validateDriverOfflineStatus() {
        int socketResponseNotReceivedCount = AppPreferences.getSocketResponseNotReceivedCount();
        if (socketResponseNotReceivedCount >= Constants.LOCATION_RESPONSE_NOT_RECEIEVED_ALLOWED_COUNTER) {
            // check is app logged in and driver is not on currently running trip.
            if (AppPreferences.isLoggedIn() && !AppPreferences.isOnTrip()) {
                // Offline driver forcefully.
                AppPreferences.setDriverOfflineForcefully(true);
            }
            // Check is driver logged in and is out of fence
            else if (AppPreferences.isLoggedIn() && AppPreferences.isOutOfFence()) {
                // Offline driver forcefully.
                AppPreferences.setDriverOfflineForcefully(true);
            }
        } else {
            AppPreferences.setLocationSocketNotReceivedCount(socketResponseNotReceivedCount++);
        }

    }

    /***
     * Cancel count down timer
     */
    private void cancelTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

//endregion

    //region Socket Events response Handler


    private UserDataHandler handler = new UserDataHandler() {
        @Override
        public void onDriverLocationResponse(DriverLocationResponse response) {
            Utils.redLogLocation(TAG, "Driver location Response: " + new Gson().toJson(response));

        }

        @Override
        public void onLocationUpdate(LocationResponse response) {
            super.onLocationUpdate(response);
            Utils.redLogLocation(TAG, "location Socket Response: " + new Gson().toJson(response));
            AppPreferences.setDriverOfflineForcefully(false);
            AppPreferences.setLocationSocketNotReceivedCount(Constants.LOCATION_RESPONSE_COUNTER_RESET);

        }

        @Override
        public void onError(int errorCode, String errorMessage) {
            switch (errorCode) {
                case HTTPStatus.UNAUTHORIZED:
                    EventBus.getDefault().post(Keys.UNAUTHORIZED_BROADCAST);
                    break;
                case HTTPStatus.FENCE_ERROR:
                    AppPreferences.setOutOfFence(true);
                    AppPreferences.setAvailableStatus(false);
                    mBus.post(Keys.INACTIVE_FENCE);
                    break;
                case HTTPStatus.INACTIVE_DUE_TO_WALLET_AMOUNT:
                    if (StringUtils.isNotBlank(errorMessage)) {
                        AppPreferences.setWalletIncreasedError(errorMessage);
                    }
                    AppPreferences.setWalletAmountIncreased(true);
                    AppPreferences.setAvailableStatus(false);
                    mBus.post(Keys.INACTIVE_FENCE);
                    break;
                case HTTPStatus.FENCE_SUCCESS:
                    AppPreferences.setOutOfFence(false);
                    AppPreferences.setAvailableStatus(true);
                    mBus.post(Keys.ACTIVE_FENCE);
                    break;
            }
        }
    };
    //endregion

    //region Event bus socket

    /**
     * update location when socket is reconnected, this will sync/update socket id on server
     */
    @Subscribe
    public void onEvent(String event) {
        if (Constants.ON_SOCKET_CONNECTED.equalsIgnoreCase(event)) {
            if (Utils.canSendLocation()) {
                synchronized (this) {
                    double lat = AppPreferences.getLatitude();
                    double lon = AppPreferences.getLongitude();
                    boolean isMock = AppPreferences.isFromMockLocation();
                    if (lat != 0.0 && lon != 0.0 && !isMock) {
                        if (Connectivity.isConnectedFast(mContext) && Utils.isGpsEnable(mContext)) {
                            Utils.redLogLocation(TAG, "onSocketConnected");
                            mUserRepository.requestLocationUpdate(mContext, handler, lat, lon);
                        }
                    }
                }
            }
        } else if (Constants.Broadcast.UPDATE_FOREGROUND_NOTIFICATION.equalsIgnoreCase(event)) {
            updateForegroundNotification();
        }
    }

    //endregion

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

}

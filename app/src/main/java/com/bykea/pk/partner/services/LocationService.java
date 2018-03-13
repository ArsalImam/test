package com.bykea.pk.partner.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.bykea.pk.partner.models.data.LocCoordinatesInTrip;
import com.bykea.pk.partner.models.response.GoogleDistanceMatrixApi;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.repositories.places.PlacesDataHandler;
import com.bykea.pk.partner.repositories.places.PlacesRepository;
import com.bykea.pk.partner.tracking.AbstractRouting;
import com.bykea.pk.partner.tracking.Route;
import com.bykea.pk.partner.tracking.RouteException;
import com.bykea.pk.partner.tracking.Routing;
import com.bykea.pk.partner.tracking.RoutingListener;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.TripStatus;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.okhttp.internal.Util;

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

    private PowerManager.WakeLock wakeLock;
    private EventBus mBus = EventBus.getDefault();

    private FusedLocationProviderClient mFusedLocationClient;

    private LocationCallback mLocationCallback;

    private final int DISTANCE_MATRIX_API_CALL_TIME = 6;
    private LatLng lastApiCallLatLng;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.redLog("LocServ", "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.redLog("LocServ", "onStartCommand");
        if (intent != null && intent.getExtras() != null && intent.hasExtra(Constants.Extras.LOCATION_SERVICE_STATUS)) {
            STATUS = intent.getStringExtra(Constants.Extras.LOCATION_SERVICE_STATUS);
        }
        mContext = getApplicationContext();
        //acquire wake lock services to make service run
        PowerManager mgr = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        if (mgr != null) {
            wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        }
        if (wakeLock != null) {
            wakeLock.acquire();
        }
        init();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.redLog("LocServ", "onDestroy");
        stopLocationUpdates();
        if (wakeLock != null) {
            wakeLock.release();
        }
        cancelTimer();
    }

    private void cancelTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }


    private void init() {
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
        startLocationUpdates();
        cancelTimer();
        mCountDownTimer.start();
    }


    private void getLastLocation() {
        try {
            Utils.redLog("Location", " getLastLocation() called");
            if (ActivityCompat.checkSelfPermission(this,
                    "android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
                mFusedLocationClient.getLastLocation()
                        .addOnCompleteListener(new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    onNewLocation(task.getResult());
                                    Utils.redLog("Location", " getLastLocation() Success");
                                } else {
                                    Utils.redLog("Location", " getLastLocation() Error");
                                }
                            }
                        });
            }
        } catch (SecurityException unlikely) {
            Utils.redLog("Location", "Lost location permission." + unlikely);
        }
    }

    private void onNewLocation(Location location) {
        if (location != null) {
            if (!Utils.isMockLocation(location, mContext)) {
                AppPreferences.saveLocation(new LatLng(location.getLatitude(),
                        location.getLongitude()), "" + location.getBearing(), location.getAccuracy(), false);
                sendLocationBroadcast(location);
                Utils.redLog("Location", location.getLatitude() + "," + location.getLongitude() + "  (" + Utils.getUTCDate(location.getTime()) + ")");
            } else {
                Utils.redLog("Location", "Mock location Received...");
//                Intent intent = new Intent(Keys.MOCK_LOCATION);
//                sendBroadcast(intent);
                EventBus.getDefault().post(Keys.MOCK_LOCATION);
            }
        }
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        int UPDATE_INTERVAL = 10000;
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        int FASTEST_INTERVAL = 5000;
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        int DISPLACEMENT = 1;
//        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                "android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        }
    }

    protected void stopLocationUpdates() {
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        } catch (Exception ignored) {
        }
    }

    public void updateTripRouteList(double lat, double lon) {
        Utils.redLog("TripStatus", AppPreferences.getTripStatus());
        if (TripStatus.ON_START_TRIP.equalsIgnoreCase(AppPreferences.getTripStatus()) ||
                TripStatus.ON_ACCEPT_CALL.equalsIgnoreCase(AppPreferences.getTripStatus())) {
            synchronized (this) {
                String lastLat = AppPreferences.getPrevDistanceLatitude();
                String lastLng = AppPreferences.getPrevDistanceLongitude();
                if (!lastLat.equalsIgnoreCase("0.0") && !lastLng.equalsIgnoreCase("0.0")) {
                    float distance = Utils.calculateDistance(lat, lon, Double.parseDouble(lastLat), Double.parseDouble(lastLng));
                    if (Utils.isValidLocation(/*lat, lon, Double.parseDouble(lastLat), Double.parseDouble(lastLng), */distance)) {
                        addLatLng(lat, lon, distance > 0f);
                        if ((distance > 1000) && !isDirectionApiRunning) {
                            getRouteLatLng(lat, lon, lastLat, lastLng);
                        }
                    } else {
                        addLatLng(Double.parseDouble(lastLat), Double.parseDouble(lastLng), false);
                    }
                } else {
                    addLatLng(lat, lon, true);
                }
            }
        }


    }

    /*
    * when Booking Screen is in background & driver is in any trip then, we need to call distance
    * matrix API in order to get Estimated time & distance, when booking screen is in foreground it
    * is already being handled via Direction API when we are showing Route to driver.
    * counter == DISTANCE_MATRIX_API_CALL_TIME == 6 indicates that API will be called after 60 sec
    * */
    private void updateETAIfRequired() {
        if (counter == DISTANCE_MATRIX_API_CALL_TIME) {
            counter = 0;
        }
        counter++;
        if (AppPreferences.isOnTrip() && !AppPreferences.isJobActivityOnForeground() && counter == DISTANCE_MATRIX_API_CALL_TIME) {
            Utils.redLog("Direction -> Trip Status ", AppPreferences.getTripStatus());
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

    /*
    * check if last start latlng and current start latlng has at least 15 m difference
    * */

    private boolean isDirectionApiCallRequired(LatLng currentApiCallLatLng) {
        if (lastApiCallLatLng != null &&
                Utils.calculateDistance(currentApiCallLatLng.latitude, currentApiCallLatLng.longitude, lastApiCallLatLng.latitude, lastApiCallLatLng.longitude) < 15) {
            return false;
        } else {
            return true;
        }
    }

    private void callDistanceMatrixApi(String destination) {
        LatLng newLatLng = new LatLng(AppPreferences.getLatitude(), AppPreferences.getLongitude());
        if (isDirectionApiCallRequired(newLatLng) && Connectivity.isConnected(mContext)) {
            lastApiCallLatLng = newLatLng;
            String origin = newLatLng.latitude + "," + newLatLng.longitude;
            new PlacesRepository().getDistanceMatrix(origin, destination, mContext, new PlacesDataHandler() {
                @Override
                public void onDistanceMatrixResponse(GoogleDistanceMatrixApi response) {
                    if (response != null && response.getRows() != null
                            && response.getRows().length > 0 && response.getRows()[0].getElements() != null
                            && response.getRows()[0].getElements().length > 0
                            && response.getRows()[0].getElements()[0].getDuration() != null
                            && response.getRows()[0].getElements()[0].getDistance() != null) {
                        String time = (response.getRows()[0].getElements()[0].getDuration().getValueInt() / 60) + "";
                        String distance = Utils.formatDecimalPlaces((response.getRows()[0].getElements()[0].getDistance().getValueInt() / 1000.0) + "", 1);
                        updateETA(time, distance);

                        Utils.redLog("onDistanceMatrixResponse", "Time -> " + time + " Distance ->" + distance);
                    }
                }
            });
        }
    }

    private void updateETA(String time, String distance) {
        AppPreferences.setEta(time);
        AppPreferences.setEstimatedDistance(distance);
        mBus.post(Keys.ETA_IN_BG_UPDATED);
    }

    private void addLatLng(double lat, double lon, boolean updatePrevTime) {
        AppPreferences.addLocCoordinateInTrip(lat, lon, STATUS);
        AppPreferences.setPrevDistanceLatLng(lat, lon, updatePrevTime);
        STATUS = StringUtils.EMPTY;
    }

    private CountDownTimer mCountDownTimer = new CountDownTimer(10000, 4990) {
        @Override
        public void onTick(long millisUntilFinished) {
            if (Connectivity.isConnectedFast(mContext)) {
                DriverApp.getApplication().connect();
            }
        }


        @Override
        public void onFinish() {
            if (AppPreferences.isLoggedIn() && (AppPreferences.getAvailableStatus() ||
                    AppPreferences.isOutOfFence() || AppPreferences.isOnTrip())) {
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
                                mUserRepository.requestLocationUpdate(mContext, handler, lat, lon);
                            }
                        } else {
                            shouldCallLocApi = true;
                        }
                    }
                }
            }
            // restart the timer
            mCountDownTimer.start();
        }
    };

    private boolean isDirectionApiRunning;

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

    private UserDataHandler handler = new UserDataHandler() {
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


    private void sendLocationBroadcast(Location location) {
        Intent locationIntent = new Intent(Keys.LOCATION_UPDATE_BROADCAST);
        locationIntent.putExtra("lng", location.getLongitude());
        locationIntent.putExtra("lat", location.getLatitude());
        locationIntent.putExtra("location", location);
        locationIntent.putExtra("bearing", location.bearingTo(location) + "");
        sendBroadcast(locationIntent);
    }


    /*
    * update location when socket is reconnected, this will sync/update socket id on server
    * */
    @Subscribe
    public void onEvent(String event) {
        if (Constants.ON_SOCKET_CONNECTED.equalsIgnoreCase(event)) {
            if (AppPreferences.isLoggedIn() && (AppPreferences.getAvailableStatus() ||
                    AppPreferences.isOutOfFence() || AppPreferences.isOnTrip())) {
                synchronized (this) {
                    double lat = AppPreferences.getLatitude();
                    double lon = AppPreferences.getLongitude();
                    boolean isMock = AppPreferences.isFromMockLocation();
                    if (lat != 0.0 && lon != 0.0 && !isMock) {
                        if (Connectivity.isConnectedFast(mContext) && Utils.isGpsEnable(mContext)) {
                            Utils.redLog("requestLocationUpdate", "onSocketConnected");
                            mUserRepository.requestLocationUpdate(mContext, handler, lat, lon);
                        }
                    }
                }
            }
        }
    }

    /*@Override
    public void onTaskRemoved(Intent rootIntent) {
        Utils.redLog("LocServ", "onTaskRemoved");
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }*/
}

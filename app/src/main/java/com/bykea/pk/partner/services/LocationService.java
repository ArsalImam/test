package com.bykea.pk.partner.services;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.bykea.pk.partner.models.data.LocCoordinatesInTrip;
import com.bykea.pk.partner.tracking.AbstractRouting;
import com.bykea.pk.partner.tracking.Route;
import com.bykea.pk.partner.tracking.RouteException;
import com.bykea.pk.partner.tracking.Routing;
import com.bykea.pk.partner.tracking.RoutingListener;
import com.bykea.pk.partner.utils.TripStatus;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.communication.socket.WebIO;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.Utils;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


public class LocationService extends Service {

    private LocationUpdatesListener mLocationUpdatesListener;
    private static Context mContext;
    private UserRepository mUserRepository;
    private Location prevLocation;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private boolean shouldCallLocApi = true;

    private PowerManager.WakeLock wakeLock;
    private EventBus mBus = EventBus.getDefault();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static void setContext(Context context) {
        mContext = context;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = getApplicationContext();
        //acquire wake lock services to make service run
        PowerManager mgr = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        if (wakeLock != null) {
            wakeLock.acquire();
        }
        init();
        Utils.redLog("BYKEA LOCATION SERVICE ", "ONCREATE CALLED...");
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            prevLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
            Utils.infoLog("Current Device Location ", (null != prevLocation) ? prevLocation.toString() : "No Location");
        }
//        Utils.appToastDebug(getApplicationContext(), "Location service started..");
        Utils.redLog("BYKEA LOCATION SERVICE ", "ON START COMMAND CALLED...");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }
        if (wakeLock != null) {
            wakeLock.release();
        }
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
//        registerAlarm();
//        Utils.appToastDebug(getApplicationContext(), "Location service Destroyed..");
        Utils.redLog("BYKEA LOCATION SERVICE ", "ON DESTROY CALLED...");

    }

    private void registerAlarm() {
        if (!AppPreferences.isStopServiceCalled(mContext)) {
            AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intentReceiver = new Intent(mContext, AlarmReceiver.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext, 0, intentReceiver, 0);
            alarmMgr.set(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis() + (4 * 1000)), alarmIntent);
        } else {
            AppPreferences.setStopService(mContext, false);
        }

    }

    private void init() {
        mUserRepository = new UserRepository();
        createLocationRequest();
        buildGoogleApiClient();
        startLocationUpdates();
        mLocationUpdatesListener = new LocationUpdatesListener();
        mCountDownTimer.start();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(myConnectionCallbacks)
                .addOnConnectionFailedListener(myConnectionFailedListener)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        int UPDATE_INTERVAL = 10000;
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        int FASTEST_INTERVAL = 5000;
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        int DISPLACEMENT = 1;
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this,
                "android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") ==
                        PackageManager.PERMISSION_GRANTED) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, mLocationUpdatesListener);
                prevLocation = LocationServices.FusedLocationApi
                        .getLastLocation(mGoogleApiClient);
                if (null != prevLocation && !Utils.isMockLocation(prevLocation, mContext)) {
                    AppPreferences.saveLocation(mContext, new LatLng(prevLocation.getLatitude(),
                            prevLocation.getLongitude()), "0.0", prevLocation.getAccuracy(), Utils.isMockLocation(prevLocation, mContext));
                    sendLocationBroadcast(prevLocation);
                }
            }
        }


    }

    public void updateLastKnowLocation() {

        if (ActivityCompat.checkSelfPermission(this,
                "android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") ==
                        PackageManager.PERMISSION_GRANTED) {
            if (mGoogleApiClient.isConnected()) {
                prevLocation = LocationServices.FusedLocationApi
                        .getLastLocation(mGoogleApiClient);
                if (prevLocation != null && (prevLocation.getAccuracy() < 80f || AppPreferences.getLatitude(this) == 0.0)) {
                    if (!Utils.isMockLocation(prevLocation, mContext)) {
                        AppPreferences.saveLocation(mContext, new LatLng(prevLocation.getLatitude(),
                                prevLocation.getLongitude()), "0.0", prevLocation.getAccuracy(), Utils.isMockLocation(prevLocation, mContext));
                        sendLocationBroadcast(prevLocation);
                    } else if (Utils.isGpsEnable(mContext)) {
                        LocationServices.FusedLocationApi.requestLocationUpdates(
                                mGoogleApiClient, mLocationRequest, mLocationUpdatesListener);
                        Utils.infoLog("Location", "Mock location Received...");
                        Intent intent = new Intent(Keys.MOCK_LOCATION);
                        sendBroadcast(intent);
                    }
                } else if (Utils.isGpsEnable(mContext)) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(
                            mGoogleApiClient, mLocationRequest, mLocationUpdatesListener);
                }
            }
        }


    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, mLocationUpdatesListener);
    }

    private class LocationUpdatesListener implements LocationListener {

        public void onLocationChanged(final Location location) {
            if (!Utils.isMockLocation(location, mContext)) {
                if (null != location && location.getLongitude() != 0.0 &&
                        location.getLongitude() != 0.0) {
                    handleLocationUpdate(location);
                } else {
                    Utils.redLog("LOCATION UPDATE" + " No location found Lat Lng: ", "" + location.getLatitude()
                            + " , " + location.getLongitude());

                }
            } else if (Utils.isGpsEnable(mContext)) {
                Utils.infoLog("Location", "Mock location Received...");
                Intent intent = new Intent(Keys.MOCK_LOCATION);
                sendBroadcast(intent);
            }

        }
    }

    private GoogleApiClient.OnConnectionFailedListener myConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            mGoogleApiClient.connect();
        }
    };

    private GoogleApiClient.ConnectionCallbacks myConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            startLocationUpdates();
        }

        @Override
        public void onConnectionSuspended(int i) {
            mGoogleApiClient.connect();
        }
    };

    public void updateTripRouteList(double lat, double lon) {
        Utils.redLog("TripStatus", AppPreferences.getTripStatus(mContext));
        if (TripStatus.ON_START_TRIP.equalsIgnoreCase(AppPreferences.getTripStatus(mContext))) {
            synchronized (this) {
                String lastLat = AppPreferences.getPrevDistanceLatitude(mContext);
                String lastLng = AppPreferences.getPrevDistanceLongitude(mContext);
                if (!lastLat.equalsIgnoreCase("0.0") && !lastLng.equalsIgnoreCase("0.0")) {
                    float distance = Utils.calculateDistance(lat, lon, Double.parseDouble(lastLat), Double.parseDouble(lastLng));
                    if (Utils.isValidLocation(/*lat, lon, Double.parseDouble(lastLat), Double.parseDouble(lastLng), */distance)) {
                        addLatLng(lat, lon, true);
                        if (distance > 1000) {
                            if (!isDirectionApiRunning) {
                                getRouteLatLng(lat, lon, lastLat, lastLng);
                            }
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

    private void addLatLng(double lat, double lon, boolean updatePrevTime) {
        AppPreferences.addLocCoordinateInTrip(mContext, lat, lon);
        AppPreferences.setPrevDistanceLatLng(mContext, lat, lon, updatePrevTime);
    }


    private CountDownTimer mCountDownTimer = new CountDownTimer(10000, 4990) {
        @Override
        public void onTick(long millisUntilFinished) {

            if (!WebIO.getInstance().isSocketConnected() && Connectivity.isConnectedFast(mContext)) {
                ((DriverApp) getApplicationContext()).connect("From the Service");
            }
            updateLastKnowLocation();


        }


        @Override
        public void onFinish() {
            /*
            * We will do any processing only when user is logged In and Available status is true.
            * */
            if (AppPreferences.isLoggedIn(mContext) && (AppPreferences.getAvailableStatus(mContext) ||
                    AppPreferences.isOutOfFence(mContext) || AppPreferences.isOnTrip(mContext))) {
                synchronized (this) {
                    double lat = AppPreferences.getLatitude(mContext);
                    double lon = AppPreferences.getLongitude(mContext);
                    boolean isMock = AppPreferences.isFromMockLocation(mContext);
                    if (lat != 0.0 && lon != 0.0 && Utils.isGpsEnable(mContext)
                            && !isMock && Connectivity.isConnectedFast(mContext)) {
                        updateTripRouteList(lat, lon);

                        //we need to add Route LatLng in 10 sec, and call requestLocationUpdate after 20 sec
                        if (shouldCallLocApi) {
                            shouldCallLocApi = false;
                            mUserRepository.requestLocationUpdate(mContext, handler, lat, lon);
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
        int index = AppPreferences.getLocCoordinatesInTrip(mContext).size();
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
                        AppPreferences.addLocCoordinateInTrip(mContext, locCoordinatesLatLng, finalIndex);
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
            if (errorCode == HTTPStatus.UNAUTHORIZED) {
                Intent locationIntent = new Intent(Keys.UNAUTHORIZED_BROADCAST);
                sendBroadcast(locationIntent);
            } else if (errorCode == HTTPStatus.FENCE_ERROR) {
                AppPreferences.setOutOfFence(mContext, true);
                AppPreferences.setAvailableStatus(mContext, false);
                mBus.post("INACTIVE-FENCE");
            } else if (errorCode == HTTPStatus.INACTIVE_DUE_TO_WALLET_AMOUNT) {
                if (StringUtils.isNotBlank(errorMessage)) {
                    AppPreferences.setWalletIncreasedError(mContext, errorMessage);
                }
                AppPreferences.setWalletAmountIncreased(mContext, true);
                AppPreferences.setAvailableStatus(mContext, false);
                mBus.post("INACTIVE-FENCE");
            } else if (errorCode == HTTPStatus.FENCE_SUCCESS) {
                AppPreferences.setOutOfFence(mContext, false);
                AppPreferences.setAvailableStatus(mContext, true);
                mBus.post("INACTIVE-FENCE");
            }
        }
    };


    private void handleLocationUpdate(Location location) {

        // Save this location for further use
        if (AppPreferences.isLoggedIn(mContext)/* && AppPreferences.getAvailableStatus(mContext)*/) {
            if (AppPreferences.isOnTrip(mContext)) {
                    /*IF ON RESUME TRIP THEN CHECK DRIVER LOCATION IS NULL OR NOT
                    * AND ALSO PREFERENCE LATITUDE AND LONGITUDE */
                if (AppPreferences.getLongitude(mContext) == 0.0 ||
                        AppPreferences.getLatitude(mContext) == 0.0) {
                    AppPreferences.saveLocation(mContext, new LatLng(location.getLatitude(),
                            location.getLongitude()), "0.0", location.getAccuracy(), Utils.isMockLocation(location, mContext));
                }
            }
            sendLocationBroadcast(location);

            //SAVING LOCATION IN PREFERENCE FOR USAGE IN ACTIVITIES....
            if (null != prevLocation)
                AppPreferences.saveLocation(mContext,
                        new LatLng(location.getLatitude(), location.getLongitude()),
                        prevLocation.bearingTo(location) + "", location.getAccuracy(), Utils.isMockLocation(location, mContext));
            prevLocation = location;


        } /*else {
            stopSelf();
            Utils.infoLog("LocationUpdate", "Gps Location not found.");
        }*/
    }

    private void sendLocationBroadcast(Location location) {
        if (null == location) return;
                     /*SENDING LOCATION UPDATE BROADCAST TO JOB ACTIVITY FOR UPDATING DRIVER ICON.*/
        Intent locationIntent = new Intent(Keys.LOCATION_UPDATE_BROADCAST);
        locationIntent.putExtra("lng", location.getLongitude());
        locationIntent.putExtra("lat", location.getLatitude());
        locationIntent.putExtra("location", location);
        if (null != prevLocation) {
            locationIntent.putExtra("bearing", prevLocation.bearingTo(location) + "");
        }
        sendBroadcast(locationIntent);
    }

}

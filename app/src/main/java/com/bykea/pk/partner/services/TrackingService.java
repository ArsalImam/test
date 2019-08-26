package com.bykea.pk.partner.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.dal.source.LocationRepository;
import com.bykea.pk.partner.models.data.MultiDeliveryCallDriverData;
import com.bykea.pk.partner.models.response.LocationResponse;
import com.bykea.pk.partner.models.response.MultipleDeliveryBookingResponse;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.models.response.PilotStatusResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.SplashActivity;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;
import com.google.android.gms.maps.model.LatLng;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class TrackingService extends Service {

    private final String TAG = TrackingService.class.getSimpleName();

    private final LocalBinder binder = new LocalBinder();
    private final int LOCATION_INTERVAL = 5000;
    private final int LOCATION_DISTANCE = 100;
    private LocationListener mLocationListener;
    //    private MyLocationManager mLocationManager;
    private LocationManager mLocationManager;
    private NotificationManager notificationManager;

    /**
     * Request location updates with the highest possible frequency on gps.
     * Typically, this means one update per second for gps.
     */
    private static final long GPS_TIME = 1000;
    /**
     * For the network provider, which gives locations with less accuracy (less reliable),
     * request updates every 5 seconds.
     */
    private static final long NET_TIME = 5000;
    /**
     * For the filter-time argument we use a "real" value: the predictions are triggered by a timer.
     * Lets say we want 5 updates (estimates) per second = update each 200 millis.
     */
    private static final long FILTER_TIME = 200;
    private LocationRepository locationRepo;
    private UserRepository mUserRepository;
    private String STATUS = StringUtils.EMPTY;
    private EventBus mBus = EventBus.getDefault();
    private UserDataHandler handler = new UserDataHandler() {
        @Override
        public void onLocationUpdate(LocationResponse response) {
            super.onLocationUpdate(response);
            if (response.isSuccess()) {
                AppPreferences.setDriverOfflineForcefully(false);
                AppPreferences.setLocationSocketNotReceivedCount(Constants.LOCATION_RESPONSE_COUNTER_RESET);
                mBus.post(Keys.ACTIVE_FENCE);
            } else {
                handleLocationErrorUseCase(response);
            }

        }

        @Override
        public void onUpdateStatus(final PilotStatusResponse pilotStatusResponse) {
            if (pilotStatusResponse.isSuccess()) {
                AppPreferences.setAvailableStatus(false);
                AppPreferences.setAvailableAPICalling(false);
                AppPreferences.setDriverDestination(null);
                AppPreferences.setCash(pilotStatusResponse.getPilotStatusData().isCashValue());
            } else {
                AppPreferences.setAvailableStatus(false);
                AppPreferences.setDriverDestination(null);
            }
            //make service offline as driver is offline now
//            updateServiceForDriverOfflineStatus();
        }


        @Override
        public void onError(int errorCode, String errorMessage) {
            switch (errorCode) {
                case HTTPStatus.UNAUTHORIZED:
                    EventBus.getDefault().post(Keys.UNAUTHORIZED_BROADCAST);
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
//        startForeground(12345678, getNotification());
        startForeground(12345678, createForegroundNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTracking();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            try {
                mLocationManager.removeUpdates(mLocationListener);
            } catch (Exception ex) {
                Log.i(TAG, "fail to remove location listners, ignore", ex);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void initializeLocationManager() {
        if (mLocationManager == null) {
//            mLocationManager = new MyLocationManager(this);
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public void stopTracking() {
        this.onDestroy();
    }

    public void startTracking() {
        initializeLocationManager();
//        locationRepo = Injection.INSTANCE.provideLocationRepository(this);
        mUserRepository = new UserRepository();
        mLocationListener = new LocationListener(LocationManager.GPS_PROVIDER);

        try {

//            mLocationManager.requestLocationUpdates(MyLocationManager.UseProvider.GPS, FILTER_TIME, GPS_TIME, NET_TIME, mLocationListener, true);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListener);

        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        } catch (RuntimeException ex) {
            Log.d(TAG, "Runtime exception " + ex.getMessage());
        } catch (Exception ex) {
            Log.d(TAG, "Generic exception " + ex.getMessage());
        }

    }

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

//    private Notification getNotification() {
//
//        NotificationChannel channel = new NotificationChannel("channel_01", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);
//
//        NotificationManager notificationManager = getSystemService(NotificationManager.class);
//        notificationManager.createNotificationChannel(channel);
//
//        Notification.Builder builder = new Notification.Builder(getApplicationContext(), "channel_01").setAutoCancel(true);
//        return builder.build();
//    }

    /**
     * Create Notification message for driver foreground service.
     * Message contains driver status i.e. (Active/In-Active/Fetching Location) and Trip status if its during trip.
     *
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
            String tripNo = StringUtils.EMPTY;
            String status = StringUtils.EMPTY;
            if (StringUtils.isBlank(AppPreferences.getDeliveryType())) return StringUtils.EMPTY;
            if (AppPreferences.getDeliveryType().
                    equalsIgnoreCase(Constants.CallType.SINGLE)) {
                NormalCallData callData = AppPreferences.getCallData();
                if (callData != null) {
                    tripNo = (callData.getTripNo() != null) ? callData.getTripNo() : StringUtils.EMPTY;
                    status = (callData.getStatus() != null) ? callData.getStatus() : StringUtils.EMPTY;
                }
            } else {
                MultiDeliveryCallDriverData callDriverData = AppPreferences.getMultiDeliveryCallDriverData();
                status = (callDriverData != null) ? callDriverData.getBatchStatus() : StringUtils.EMPTY;
                List<MultipleDeliveryBookingResponse> bookingResponseList = callDriverData.getBookings();
                int n = (bookingResponseList != null) ? bookingResponseList.size() : 0;

                int i = 0;
                while (i < n) {
                    tripNo += bookingResponseList.get(i).getTrip().getTripNo();
                    i++;
                    if (i != n)
                        tripNo += ", ";
                }
            }
            notificationBodyMessage = getResources().getString(R.string.notification_title_driver_trip,
                    tripNo,
                    StringUtils.capitalize(status));
        } else if (isDriverLogin && driverStatusAvailable) {
            notificationBodyMessage = getResources().getString(R.string.notification_title_driver_status,
                    Constants.Driver.STATUS_ACTIVE);
        } else if (!driverStatusAvailable) {
            notificationBodyMessage = getResources().getString(R.string.notification_title_driver_login_location);
        }
        return notificationBodyMessage;
    }

    /**
     * Handles fetched location and save's it inside shared preference and send local broadcast.
     * We filter out mock location.
     * <p>
     * <p>
     * //        AppPreferences.saveLocation(new LatLng(location.getLatitude(), location.getLongitude()), "" + location.getBearing(), location.getAccuracy(), false);
     *
     * @param location Location object which contains latest fetched location.
     */
    private void onNewLocationY(Location location) {
//        locationRepo.insert(location.getLatitude(), location.getLongitude());
        sendLocationBroadcast(location);
    }

    private void onNewLocation(Location location) {

        // Save location locally
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        AppPreferences.saveLocation(latLng, "" + location.getBearing(), location.getAccuracy(), false);
        updateTripRouteList(location.getLatitude(), location.getLongitude());

        // Send to server
        setToServer(location);

        // Broadcast update
        sendLocationBroadcast(location);
    }

    private void setToServer(Location location) {
        if (Utils.canSendLocation()) {
            synchronized (this) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                boolean isMock = AppPreferences.isFromMockLocation();
                if (lat != 0.0 && lon != 0.0 && !isMock) {
                    mUserRepository.requestLocationUpdate(getApplicationContext(), handler, lat, lon);
                }
            }
        }
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

    /***
     * Handle Location API Error case for API failures.
     * @param locationResponse latest response from server.
     */
    private void handleLocationErrorUseCase(LocationResponse locationResponse) {
        if (locationResponse != null) {
            switch (locationResponse.getCode()) {
                case Constants.ApiError.BUSINESS_LOGIC_ERROR: {
                    Utils.handleLocationBusinessLogicErrors(mBus, locationResponse);
                    break;
                }
                //TODO Will update unauthorized check on error callback when API team adds 401 status code in their middle layer.
                case HTTPStatus.UNAUTHORIZED: {
                    EventBus.getDefault().post(Keys.UNAUTHORIZED_BROADCAST);
                    break;
                }
                default:
                    Utils.appToast(locationResponse.getMessage());
            }
        }

    }

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

    public class LocalBinder extends Binder {
        public TrackingService getService() {
            return TrackingService.this;
        }
    }

    //endregion

    private class LocationListener implements android.location.LocationListener {
        private final String TAG = "LocationListener";
        private Location mLastLocation;

        public LocationListener(String provider) {
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "LocationChanged: " + location);
            mLastLocation = location;
            onNewLocation(mLastLocation);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + status);
        }
    }

    //endregion

}

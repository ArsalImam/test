package com.bykea.pk.partner.ui.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import com.bykea.pk.partner.Notifications;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.Place;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.tracking.AbstractRouting;
import com.bykea.pk.partner.tracking.Route;
import com.bykea.pk.partner.tracking.RouteException;
import com.bykea.pk.partner.tracking.Routing;
import com.bykea.pk.partner.tracking.RoutingListener;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.NetworkChangeListener;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.PolyUtil;
import com.instabug.library.util.StringUtility;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * MultiDelivery Booking Activity.
 */
public class MultipleDeliveryBookingActivity extends BaseActivity implements RoutingListener {

    private MultipleDeliveryBookingActivity mCurrentActivity;
    private UserRepository dataRepository;
    private MapView mapView;
    private GoogleMap mGoogleMap;
    private NormalCallData mCallData;
    private Marker pickupMarker;
    private Marker dropOffMarker;
    private Location mCurrentLocation;
    private float mLocBearing = 0.0f;
    private boolean animationStart = false;
    private boolean isResume = false;
    private boolean isLastAnimationComplete = true;
    private ProgressDialog progressDialogJobActivity;
    private boolean isFirstTime = false;
    private Marker driverMarker;
    private LatLng lastApiCallLatLng;
    private List<LatLng> mRouteLatLng;

    @BindView(R.id.timeTv)
    TextView timeTv;

    @BindView(R.id.distanceTv)
    TextView distanceTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_delivery_booking);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        initialize();
        mapView.onCreate(savedInstanceState);
        setUpMapView();
        AppPreferences.setStatsApiCallRequired(true);
        Utils.keepScreenOn(mCurrentActivity);
        Notifications.removeAllNotifications(mCurrentActivity);
    }

    /***
     * Initialize data i.e activity, register ButterKnife, initialize UserRepository,  etc
     */
    private void initialize() {
        mCurrentActivity = this;
        ButterKnife.bind(this);
        dataRepository = new UserRepository();
        mapView = (MapView) findViewById(R.id.mapFragment);
    }

    /***
     * Set data according to trip states
     */
    private void setInitialData() {
        setProgressDialog();
        AppPreferences.setIsOnTrip(true);
        ActivityStackManager.getInstance().restartLocationService(mCurrentActivity);
        mLocBearing = (float) AppPreferences.getBearing();
        mCurrentLocation = new Location(StringUtils.EMPTY);
        setCurrentLocation();
        mCallData = AppPreferences.getCallData();
        isResume = true;
        setTripStates();
        checkGps();
        checkConnectivity(mCurrentActivity);
        AppPreferences.setJobActivityOnForeground(true);
    }

    /***
     * Draw the route on location update.
     *
     * @param startLatLng an starting address lat lng
     * @param endLatlng an ending address lat lng.
     */
    private void drawRouteOnChange(LatLng startLatLng, LatLng endLatlng) {
        if (null != startLatLng && null != endLatlng) {
            drawRoute(startLatLng, endLatlng, Routing.onChangeRoute);
        } else {
            /*if (mapPolylines != null) {
                mapPolylines.remove();
            }*/
        }
    }

    /***
     * Create Progress Dialog indeterminate with an network error message.
     */
    private void setProgressDialog() {
        if (progressDialogJobActivity == null) {
            progressDialogJobActivity = new ProgressDialog(mCurrentActivity);
            progressDialogJobActivity.setCancelable(false);
            progressDialogJobActivity.setIndeterminate(true);
            progressDialogJobActivity.setMessage(getString(R.string.internet_error));
        }
    }

    /***
     * Set the trip states according to the incomming states i.e accepted, arrived, started, etc.
     */
    private void setTripStates() {
        if (mCallData != null) {
            AppPreferences.setTripStatus(mCallData.getStatus());
            if (StringUtils.isBlank(mCallData.getStatus())) {
                setAcceptedState();
            } else {
                setAcceptedState();
            }
        }
    }

    /***
     * Set the time, distance, draw route to pickup in accepted state
     */
    private void setAcceptedState() {
        setTimeDistance(mCallData.getArivalTime(), mCallData.getDistance());
        drawRouteToPickup();
    }

    /***
     *
     */
    private void drawRouteToPickup() {
        if (StringUtils.isNotBlank(mCallData.getStartLat())
                && StringUtils.isNotBlank(mCallData.getStartLng())
                && StringUtils.isNotBlank(String.valueOf(AppPreferences.getLatitude()))
                && StringUtils.isNotBlank(String.valueOf(AppPreferences.getLongitude()))) {

            drawRoute(new LatLng(AppPreferences.getLatitude(),
                            AppPreferences.getLongitude()),
                    new LatLng(Double.parseDouble(mCallData.getStartLat()),
                            Double.parseDouble(mCallData.getStartLng())), Routing.pickupRoute);

        }
    }

    /***
     * Draw the route by using Routing.Builder.
     *
     * Please see the {@Link Routing.Builder} class
     *
     * @param start an start lat lng.
     * @param end an end lat lng.
     * @param routeType is a route type.
     */
    private synchronized void drawRoute(LatLng start, LatLng end, int routeType) {
        if (Connectivity.isConnected(mCurrentActivity)
                && (Utils.isDirectionApiCallRequired()) && mGoogleMap != null) {
            AppPreferences.setLastDirectionsApiCallTime(System.currentTimeMillis());
            if (isDirectionApiCallRequired(start)) {
                lastApiCallLatLng = start;
                if (mRouteLatLng != null && mRouteLatLng.size() > 0) {
                    mRouteLatLng.clear();
                }
                Routing.Builder builder = new Routing.Builder();
                if (StringUtils.isNotBlank(Utils.getApiKeyForDirections(mCurrentActivity))) {
                    builder.key(Utils.getApiKeyForDirections(mCurrentActivity));
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

    private boolean isDirectionApiCallRequired(LatLng currentApiCallLatLng) {
        if (lastApiCallLatLng != null && (lastApiCallLatLng.equals(currentApiCallLatLng)
                || Utils.calculateDistance(currentApiCallLatLng.latitude, currentApiCallLatLng.longitude, lastApiCallLatLng.latitude, lastApiCallLatLng.longitude) < 15)) {
            return false;
        }
        return true;
    }


    /***
     * Set the time & distance.
     *
     * @param time an arrival time.
     * @param distance an away distance.
     */
    private void setTimeDistance(String time, String distance) {
        timeTv.setText(time);
        distanceTv.setText(distance);
        AppPreferences.setEta(time);
        AppPreferences.setEstimatedDistance(distance);
    }

    /***
     * Update ETA & call data from routing listener.
     *
     * @param time an arrival time.
     * @param distance an away distance.
     */
    private void updateEtaAndCallData(String time, String distance) {
        mCallData.setArivalTime(time);
        mCallData.setDistance(distance);
        setTimeDistance(mCallData.getArivalTime(), mCallData.getDistance());
        AppPreferences.setCallData(mCallData);
    }

    /***
     * Set the current location which is comming from shared preferences.
     */
    private void setCurrentLocation() {
        mCurrentLocation.setLatitude(AppPreferences.getLatitude());
        mCurrentLocation.setLongitude(AppPreferences.getLongitude());
    }

    /***
     * Needs to call MapsInitializer before doing any CameraUpdateFactory calls
     */
    private void setUpMapView() {
        try {
            MapsInitializer.initialize(mCurrentActivity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(mapReadyCallback);
    }

    /***
     * mapReadyCallback is a callback listener for map ready.
     */
    private OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        /**
         * Called when a google map is ready.
         *
         * @param googleMap The google map.
         */
        @Override
        public void onMapReady(final GoogleMap googleMap) {
            if (mCurrentActivity == null) {
                return;
            }

            /***
             * Set the google map loaded call back.
             */
            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                /***
                 * Called when a google map is loaded.
                 */
                @Override
                public void onMapLoaded() {
                    mGoogleMap = googleMap;
                    mGoogleMap.clear();
                    Utils.formatMap(mGoogleMap);
                    com.google.maps.model.LatLng driverLatLng = new com.google.maps.model.LatLng(
                            AppPreferences.getLatitude(),
                            AppPreferences.getLongitude()
                    );
                    getDriverRoadPosition(driverLatLng);
                    addPickupMarker();
                    addDropOffMarkers();
                }
            });
        }
    };

    /***
     * Calling driver road API. if there is an issue in api response
     * then use the current GPS Location points
     *
     * @param normalLocation a location (latitude & longitude)
     */
    public synchronized void getDriverRoadPosition(com.google.maps.model.LatLng normalLocation) {
        if (normalLocation != null && normalLocation.lat != 0.0 && normalLocation.lng != 0.0) {
            onGetLocation(normalLocation.lat, normalLocation.lng);
        }
    }

    /***
     * Fetch the location from (lat & lng) from calling Api. If it is not
     * found fetch the location from GPS_PROVIDER.
     *
     * Please see the {@link LocationManager} class.
     *
     * @param lat a latitude.
     * @param lng a longitude.
     */
    private void onGetLocation(double lat, double lng) {
        if (mCurrentLocation != null && mCallData != null) {
            mCurrentLocation.setLatitude(lat);
            mCurrentLocation.setLongitude(lng);
            updateDriverMarker(
                    String.valueOf(mCurrentLocation.getLatitude()),
                    String.valueOf(mCurrentLocation.getLongitude())
            );

            if (AppPreferences.getTripStatus().equalsIgnoreCase(TripStatus.ON_START_TRIP)) {
                if (StringUtils.isNotBlank(mCallData.getEndLat()) &&
                        StringUtils.isNotBlank(mCallData.getEndLng()))
                    drawRouteOnChange(
                            new LatLng(mCurrentLocation.getLatitude(),
                                    mCurrentLocation.getLongitude()),
                            new LatLng(Double.parseDouble(mCallData.getEndLat()),
                                    Double.parseDouble(mCallData.getEndLng()))
                    );
            } else {
                if (StringUtils.isNotBlank(mCallData.getStartLat()) &&
                        StringUtils.isNotBlank(mCallData.getStartLng()))
                    drawRouteOnChange(
                            new LatLng(mCurrentLocation.getLatitude(),
                                    mCurrentLocation.getLongitude()),
                            new LatLng(Double.parseDouble(mCallData.getStartLat()),
                                    Double.parseDouble(mCallData.getStartLng()))
                    );
            }

        } else {
            mCurrentLocation = new Location(LocationManager.GPS_PROVIDER);
            mCurrentLocation.setLatitude(lat);
            mCurrentLocation.setLongitude(lng);
        }
    }

    private void updateDriverMarker(String snappedLatitude, String snappedLongitude) {
        if (null != mGoogleMap) {
            //if driver marker is null add driver marker on google map
            if (null == driverMarker) {
                driverMarker = mGoogleMap.addMarker(new MarkerOptions().
                        icon(
                                BitmapDescriptorFactory.fromResource(
                                        Utils.getMapIcon(
                                                mCallData.getCallType()
                                        )
                                )
                        )
                        .position(
                                new LatLng(
                                        Double.parseDouble(snappedLatitude),
                                        Double.parseDouble(snappedLongitude)
                                )
                        )
                        .rotation(mLocBearing));
            }

            //When activity created and load map first time do not start animation.
            if (animationStart) {
                // Update camera rotation according to marker direction
                if (null != mCurrentLocation) {
                    updateCamera(mLocBearing);
                    // ANIMATE DRIVER MARKER TO THE TARGET LOCATION.
                    if (isLastAnimationComplete) {
                        if (Utils.calculateDistance(driverMarker.getPosition().latitude,
                                driverMarker.getPosition().longitude, mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()) >= 10) {
                            //LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                            //animateMarker(latLng);
                        }
                    }
                }
            }
            animationStart = true;
        }
    }

    /***
     * Add Pickup marker to the pickup location.
     */
    private void addPickupMarker() {
        try {
            if (null == mGoogleMap) return;
            if (pickupMarker != null) {
                pickupMarker.remove();
            }
            double lat = Double.parseDouble(mCallData.getStartLat());
            double lng = Double.parseDouble(mCallData.getStartLng());
            LatLng startLatLng = new LatLng(lat, lng);
            pickupMarker = mGoogleMap.addMarker(new MarkerOptions().
                    icon(Utils.getBitmapDiscriptor(mCurrentActivity, true))
                    .position(startLatLng));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    /***
     * Add Pickup marker to the pickup location.
     */
    private void addDropOffMarkers() {
        try {
            if (null == mGoogleMap) return;
            if (dropOffMarker != null) {
                dropOffMarker.remove();
            }
            List<LatLng> latLngList = new ArrayList<>();
            latLngList.add(new LatLng(24.781380, 67.055888));
            latLngList.add(new LatLng(24.781672, 67.054214));

            for (int i = 0; i < latLngList.size(); i++) {
                dropOffMarker = mGoogleMap.addMarker(new MarkerOptions().
                        icon(Utils.getBitmapDiscriptor(mCurrentActivity, false))
                        .position(latLngList.get(i)));
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    /***
     * Update the google map  camera according to the current lat lng
     *
     * @param bearing The camera bearing is the direction in which a vertical line on the map
     *                points, measured in degrees clockwise from north. Someone driving a car often
     *                turns a road map to align it with their direction of travel
     */
    public void updateCamera(final float bearing) {
        if (mGoogleMap != null) {
            CameraPosition currentPlace = new CameraPosition.Builder()
                    .target(new LatLng(mCurrentLocation.getLatitude(),
                            mCurrentLocation.getLongitude()))
                    .zoom(16f)
                    .bearing(bearing)
                    .build();
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace));
        }
    }

    /***
     * Network Change Broadcast Reciever
     */
    private NetworkChangeListener networkChangeListener = new NetworkChangeListener() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("android.location.GPS_ENABLED_CHANGE".equalsIgnoreCase(intent.getAction()) ||
                    "android.location.PROVIDERS_CHANGED".equalsIgnoreCase(intent.getAction())) {
                checkGps();
            } else {
                if (Connectivity.isConnectedFast(context)) {
                    if (null != progressDialogJobActivity && !isFirstTime) {
                        progressDialogJobActivity.dismiss();
                        //new UserRepository().requestRunningTrip(mCurrentActivity, handler);
                    } else {
                        isFirstTime = false;
                    }
                } else {
                    progressDialogJobActivity.show();
                }
            }
        }
    };

    /***
     * Location broad cast reciever for fetching latest location updates.
     */
    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            mCurrentLocation = intent.getParcelableExtra(
                    getResources().getString(R.string.location_text));
            if (null != mCurrentLocation) {
                getDriverRoadPosition(
                        new com.google.maps.model.LatLng(
                                mCurrentLocation.getLatitude(),
                                mCurrentLocation.getLongitude()
                        )
                );
            }
        }
    };

    @Override
    public void onRoutingFailure(int routeType, RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(int routeType, List<Route> route, int shortestRouteIndex) {
        mRouteLatLng = route.get(0).getPoints();
        updateEtaAndCallData(
                String.valueOf((route.get(0).getDurationValue() / 60)),
                Utils.formatDecimalPlaces(String.valueOf(
                        (route.get(0).getDistanceValue() / 1000.0)), 1));
    }

    @Override
    public void onRoutingCancelled() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(locationReceiver, new IntentFilter(Keys.LOCATION_UPDATE_BROADCAST));
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        setInitialData();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(locationReceiver);
        mapView.onDestroy();
        super.onDestroy();

    }


    @Override
    public void onBackPressed() {

    }
}

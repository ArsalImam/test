package com.bykea.pk.partner.ui.activities;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.bykea.pk.partner.Notifications;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.MultiDeliveryCallDriverData;
import com.bykea.pk.partner.models.response.MultiDeliveryCancelBatchResponse;
import com.bykea.pk.partner.models.response.MultiDeliveryDriverArrivedResponse;
import com.bykea.pk.partner.models.response.MultiDeliveryDriverStartedResponse;
import com.bykea.pk.partner.repositories.IUserDataHandler;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.repositories.places.PlacesDataHandler;
import com.bykea.pk.partner.tracking.AbstractRouting;
import com.bykea.pk.partner.tracking.Route;
import com.bykea.pk.partner.tracking.RouteException;
import com.bykea.pk.partner.tracking.Routing;
import com.bykea.pk.partner.tracking.RoutingListener;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.LatLngInterpolator;
import com.bykea.pk.partner.ui.helpers.Spherical;
import com.bykea.pk.partner.ui.helpers.StringCallBack;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.GeocodeStrategyManager;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.NetworkChangeListener;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;
import com.google.android.gms.maps.CameraUpdate;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.net.HttpURLConnection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bykea.pk.partner.utils.Constants.DIRECTION_API_MIX_THRESHOLD_METERS_FOR_MULTIDELIVERY;

/***
 * MultiDelivery Booking Activity.
 */
public class MultipleDeliveryBookingActivity extends BaseActivity implements RoutingListener {
    private final String TAG = MultipleDeliveryBookingActivity.class.getSimpleName();
    private static final float ZOOM_LEVEL = 14.0f;
    private MultipleDeliveryBookingActivity mCurrentActivity;
    private UserRepository dataRepository;
    private MapView mapView;
    private GoogleMap mGoogleMap;
    private Marker pickupMarker;
    private Marker dropOffMarker;
    private Location mCurrentLocation;
    private float mLocBearing = 0.0f;
    private boolean animationStart = false;
    private boolean isResume = false;
    private boolean isLastAnimationComplete = true;
    private ProgressDialog progressDialogJobActivity;
    private boolean isFirstTime = true;
    private Marker driverMarker;
    private LatLng lastApiCallLatLng;
    private List<LatLng> mRouteLatLng;
    private static final double EARTHRADIUS = 6366198;
    private Polyline mapPolylines;
    private static final int ARRIVAL_MAX_DISTANCE_VALUE = 200;
    private static final int SECONDS_IN_MINUTES = 60;
    private static final double METERS_IN_KILOMETER = 1000.0;
    private MultiDeliveryCallDriverData callDriverData;
    private UserRepository repository;

    @BindView(R.id.timeTv)
    TextView timeTv;

    @BindView(R.id.TimeUnitLabelTv)
    TextView timeUnitLabelTv;

    @BindView(R.id.TimeView)
    View timeView;

    @BindView(R.id.distanceTv)
    TextView distanceTv;

    @BindView(R.id.pickView)
    View pickView;

    @BindView(R.id.pickUpDistanceUnit)
    TextView pickUpDistanceUnit;

    @BindView(R.id.jobBtn)
    FontTextView jobBtn;

    @BindView(R.id.cancelBtn)
    FontTextView cancelBtn;

    @BindView(R.id.tafseelLayout)
    FrameLayout tafseelLayout;

    @BindView(R.id.serviceImageView)
    AppCompatImageView serviceImageView;

    private GeocodeStrategyManager geocodeStrategyManager;

    private PlacesDataHandler placesDataHandler = new PlacesDataHandler() {
        @Override
        public void onPlacesResponse(String response) {
            super.onPlacesResponse(response);
            repository.requestMultiDeliveryDriverStarted(mCurrentActivity, handler, response);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_delivery_booking);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        initialize();
        mapView.onCreate(savedInstanceState);
        AppPreferences.setLastDirectionsApiCallTime(0);
        setUpMapView();
        AppPreferences.setStatsApiCallRequired(true);
        Utils.keepScreenOn(mCurrentActivity);
        Notifications.removeAllNotifications(mCurrentActivity);
        EventBus.getDefault().post(Constants.Broadcast.UPDATE_FOREGROUND_NOTIFICATION);
        geocodeStrategyManager = new GeocodeStrategyManager(this, placesDataHandler, Constants.NEAR_LBL);
    }

    /***
     * Initialize data i.e activity, register ButterKnife, initialize UserRepository,  etc.
     */
    private void initialize() {
        mCurrentActivity = this;
        repository = new UserRepository();
        ButterKnife.bind(this);
        dataRepository = new UserRepository();
        mapView = (MapView) findViewById(R.id.mapFragment);
    }

    /***
     * Set data according to tripInfo states
     */
    private void setInitialData() {
        setProgressDialog();

        //call once on resume app to display last saved time and distance
        callDriverData = AppPreferences.getMultiDeliveryCallDriverData();
        setTimeDistanceOnResume();
        ActivityStackManager.getInstance().restartLocationServiceWithCustomIntervals(mCurrentActivity, Constants.ON_TRIP_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocBearing = (float) AppPreferences.getBearing();
        mCurrentLocation = new Location(StringUtils.EMPTY);
        setCurrentLocation();
        isResume = true;
        AppPreferences.setIsOnTrip(true);
        checkGps();
        setTripStates();
        checkConnectivity(mCurrentActivity);
        AppPreferences.setJobActivityOnForeground(true);
        AppPreferences.setMultiDeliveryJobActivityOnForeground(true);
        Utils.loadImgURL(serviceImageView, callDriverData.getImageURL());
    }

    /***
     * Draw the route on location update. Remove the polyline from the map
     *
     * @param startLatLng an starting address lat lng
     * @param endLatlng an ending address lat lng.
     */
    private void drawRouteOnChange(LatLng startLatLng, LatLng endLatlng) {
        if (null != startLatLng && null != endLatlng) {
            drawRoute(startLatLng, endLatlng, Routing.onChangeRoute);
        } else {
            if (mapPolylines != null) {
                mapPolylines.remove();
            }
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
     * Set the tripInfo states according to the incomming states i.e accepted, arrived, started, etc.
     */
    private void setTripStates() {
        if (callDriverData != null) {
            AppPreferences.setTripStatus(callDriverData.getBatchStatus());
            setAcceptedState();
            if (callDriverData.getBatchStatus().equalsIgnoreCase(TripStatus.ON_ACCEPT_CALL)) {
                setAcceptedState();
            } else if (callDriverData.getBatchStatus().equalsIgnoreCase(TripStatus.ON_ARRIVED_TRIP)) {
                setArrivedState();
            } else if (callDriverData.getBatchStatus().equalsIgnoreCase(TripStatus.ON_START_TRIP)) {
                setStartedState();
            } else {
                EventBus.getDefault().post(Keys.MULTIDELIVERY_ERROR_BORADCAST);
            }
        }
    }

    /***
     * Set the time, distance, draw route to pickup in accepted state
     */
    private void setAcceptedState() {
        int duration = Integer.parseInt(
                Utils.formatETA(
                        String.valueOf(Utils.getDuration(
                                callDriverData
                                        .getPickup()
                                        .getDuration()
                                )
                        )
                )
        );

        //commented out because it continuously showing value 0 as the duration and getDistance() is 0
//        setTimeDistance(duration, callDriverData.getPickup().getDistance());
        drawRouteToPickup();
    }

    /***
     * Draw the route from driver current position toward pickupLocation
     */
    private void drawRouteToPickup() {
        if (StringUtils.isNotBlank(String.valueOf(callDriverData.getPickup().getLat()))
                && StringUtils.isNotBlank(String.valueOf(callDriverData.getPickup().getLng()))
                && StringUtils.isNotBlank(String.valueOf(mCurrentLocation.getLatitude()))
                && StringUtils.isNotBlank(String.valueOf(mCurrentLocation.getLatitude()))
                && callDriverData.getBatchStatus().equalsIgnoreCase(TripStatus.ON_ACCEPT_CALL)) {

            drawRoute(new LatLng(mCurrentLocation.getLatitude(),
                            mCurrentLocation.getLongitude()),
                    new LatLng(callDriverData.getPickup().getLat(),
                            callDriverData.getPickup().getLng()), Routing.pickupRoute);

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
                && (Utils.isDirectionApiCallRequiredForMultiDelivery()) && mGoogleMap != null) {
            if (callDriverData != null && callDriverData.getBatchStatus() != null &&
                    callDriverData.getBatchStatus().equalsIgnoreCase(TripStatus.ON_ACCEPT_CALL) &&
                    (AppPreferences.isMultiDeliveryJobActivityOnForeground() || mapPolylines == null)) {
                // DRAW ROUTES FOR THE FIRST TIME AND AFTER THAT DRAW IF MULTIDELIVERY JOB ACTIVITY IS IN FOREGROUND
                AppPreferences.setLastDirectionsApiCallTime(System.currentTimeMillis());
                if (isDirectionApiCallRequired(start)) {
                    Log.v(TAG, "Direction API Called");
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
    }

    /**
     * Invoked this method when direction API call required.
     *
     * @param currentApiCallLatLng The Pickup lat lang from
     *                             {@link MultiDeliveryCallDriverData#getPickup()}
     * @return true if last api lat lng is not equals and if the current lat lng
     * and last api call lat lng difference is greater than 15 other wise return false
     */
    private boolean isDirectionApiCallRequired(LatLng currentApiCallLatLng) {
        if (lastApiCallLatLng != null && (lastApiCallLatLng.equals(currentApiCallLatLng)
                || Utils.calculateDistance(currentApiCallLatLng.latitude,
                currentApiCallLatLng.longitude, lastApiCallLatLng.latitude,
                lastApiCallLatLng.longitude) < DIRECTION_API_MIX_THRESHOLD_METERS_FOR_MULTIDELIVERY)) {
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
    private void setTimeDistance(int time, float distance) {
        timeTv.setText(String.valueOf(time));
        distanceTv.setText(Utils.getDistance(distance));
        AppPreferences.setEta(String.valueOf(time));
        AppPreferences.setEstimatedDistance(Utils.getDistance(distance));
    }

    /**
     * show last saved time and distance on resume app
     */
    private void setTimeDistanceOnResume() {
        //getEta = 0 is a default value given by sharedPref
        if (AppPreferences.getEta().equalsIgnoreCase("0") || AppPreferences.getEstimatedDistance().equalsIgnoreCase("0")) {
            //when the booking screen comes first after accepting ride
            //the expected ETA and Distance would be 0, so in that case we have to show the data coming from callDriverData
            if (callDriverData != null && callDriverData.getPickup() != null
                    && callDriverData.getPickup().getDuration() != null && callDriverData.getPickup().getDistance() != null) {
                AppPreferences.setEta(String.valueOf(Utils.getDuration(callDriverData.getPickup().getDuration())));
                AppPreferences.setEstimatedDistance(String.valueOf(Utils.getDistance(callDriverData.getPickup().getDistance())));
                timeTv.setText(AppPreferences.getEta());
                distanceTv.setText(String.valueOf(AppPreferences.getEstimatedDistance()));
            }
        } else {
            timeTv.setText(AppPreferences.getEta());
            distanceTv.setText(String.valueOf(AppPreferences.getEstimatedDistance()));
        }
    }

    /***
     * Update ETA & call data from routing listener.
     *
     * @param time an arrival time.
     * @param distance an away distance.
     */
    private void updateEtaAndCallData(String time, String distance) {
        callDriverData.getPickup().setDuration(Integer.valueOf(time));
        callDriverData.getPickup().setDistance(Integer.valueOf(distance));
        setTimeDistance(
                Utils.getDuration(
                        callDriverData.getPickup().getDuration()
                ),
                callDriverData.getPickup().getDistance());
        AppPreferences.setMultiDeliveryCallDriverData(callDriverData);
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
                    if (!callDriverData.getBatchStatus().equalsIgnoreCase(TripStatus.ON_START_TRIP))
                        addPickupMarker();
                    setTripStates();
                    updateDropOffMarkers();
                    setPickupBounds();
                    setMarkersBound();
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
        if (mCurrentLocation != null && callDriverData != null) {
            mCurrentLocation.setLatitude(lat);
            mCurrentLocation.setLongitude(lng);
            updateDriverMarker(
                    String.valueOf(mCurrentLocation.getLatitude()),
                    String.valueOf(mCurrentLocation.getLongitude())
            );
            drawRouteOnChange(
                    new LatLng(mCurrentLocation.getLatitude(),
                            mCurrentLocation.getLongitude()),
                    new LatLng(Double.valueOf(callDriverData.getPickup().getLat()),
                            Double.valueOf(callDriverData.getPickup().getLng()))
            );

        } else {
            mCurrentLocation = new Location(LocationManager.GPS_PROVIDER);
            mCurrentLocation.setLatitude(lat);
            mCurrentLocation.setLongitude(lng);
        }
    }

    /***
     * Set Pickup Bounds to move google map camera
     */
    private void setPickupBounds() {
        int padding = (int) mCurrentActivity.getResources().getDimension(R.dimen._30sdp);
        LatLngBounds latLngBounds = getCurrentLatLngBounds();
        if (latLngBounds != null) {
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(getCurrentLatLngBounds(), padding);
            padding = (int) mCurrentActivity.getResources().getDimension(R.dimen._50sdp);
            mGoogleMap.setPadding(0, padding, 0, padding);
            mGoogleMap.moveCamera(cu);
        }
    }

    /***
     * get the current lat lng bounds when the driver and passenger is very close google map zoom
     * and it overlap passenger and driver marker to resolve this problem we have added the fake
     * bounds.
     *
     * Add 2 points 1000m northEast and southWest of the center. They increase the bounds only,
     * if they are not already larger than this. 1000m on the diagonal translates into about
     * 709m to each direction.
     *
     * @return corrected latitude and longitude bounds object.
     */
    private LatLngBounds getCurrentLatLngBounds() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (pickupMarker != null) {
            builder.include(pickupMarker.getPosition());
        }
        if (driverMarker != null) {
            builder.include(driverMarker.getPosition());
        }

        try {
            LatLngBounds tmpBounds = builder.build();
            LatLng center = tmpBounds.getCenter();
            LatLng northEast = move(center, 709, 709);
            LatLng southWest = move(center, -709, -709);

            builder.include(southWest);
            builder.include(northEast);
            return builder.build();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * Adjust provided latitude and longitude according to south and north
     * direction according to fixed meter difference.
     *
     * @param startLL Starting position Latitude and Longitude.
     * @param toNorth NorthEast meters which needs to be added.
     * @param toEast SouthWest meters which needs to be added.
     *
     * @return LatLng object having coordinates which are corrected according to NorthEast and SouthWest
     */
    private static LatLng move(LatLng startLL, double toNorth, double toEast) {
        double lonDiff = meterToLongitude(toEast, startLL.latitude);
        double latDiff = meterToLatitude(toNorth);
        return new LatLng(startLL.latitude + latDiff, startLL.longitude
                + lonDiff);
    }

    /***
     * Converts meters to longitude.
     *
     * @param meterToEast number of meters which needs to be included in longitude
     * @param latitude current latitude value.
     *
     * @return Longitude value which includes meter difference as well.
     */
    private static double meterToLongitude(double meterToEast, double latitude) {
        double latArc = Math.toRadians(latitude);
        double radius = Math.cos(latArc) * EARTHRADIUS;
        double rad = meterToEast / radius;
        return Math.toDegrees(rad);
    }

    /***
     * Converts meter to latitude
     *
     * @param meterToNorth number of meters which needs to be included in latitude
     * @return Latitude value which includes meter difference as well.
     */
    private static double meterToLatitude(double meterToNorth) {
        double rad = meterToNorth / EARTHRADIUS;
        return Math.toDegrees(rad);
    }

    /**
     * fit All dropOff locations and driver's current location to the screen - bound markers within screen
     */
    private void setMarkersBound() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        List<LatLng> latLngList = Utils.getDropDownLatLngList(callDriverData);
        for (LatLng pos : latLngList) {
            builder.include(pos);
        }
        if (pickupMarker != null) {
            builder.include(pickupMarker.getPosition());
        }
        if (driverMarker != null) {
            builder.include(driverMarker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = (int) mCurrentActivity.getResources().getDimension(R.dimen._30sdp);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mGoogleMap.moveCamera(cu);
    }

    /***
     * Animate marker when gps points change location.
     *
     * @param destination new latlng comming from GPS.
     * @param latLngInterpolator is an interface which is used for smooth animation on
     *                           smallest device i.e ice cream sandwitch
     */
    private synchronized void animateMarker(final LatLng destination,
                                            final LatLngInterpolator latLngInterpolator) {
        final LatLng startPosition = driverMarker.getPosition();

        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = animation.getAnimatedFraction();
                LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, destination);
                driverMarker.setPosition(newPosition);
            }
        });
        valueAnimator.setFloatValues(0, 1); // Ignored.
        valueAnimator.setDuration(3000);
        valueAnimator.start();
        drawRouteToPickup();
    }

    /**
     * This method will be invoked when location API has been called.
     * Update the driver marker according to current lat lng
     *
     * @param snappedLatitude  The current driver latitude.
     * @param snappedLongitude The current driver longitude.
     */
    private void updateDriverMarker(String snappedLatitude, String snappedLongitude) {
        if (null != mGoogleMap) {
            //if driver marker is null add driver marker on google map
            if (null == driverMarker) {
                Bitmap driverIcon = Utils.getBitmap(mCurrentActivity, R.drawable.ic_delivery_bike);
                driverMarker = mGoogleMap.addMarker(new MarkerOptions().
                        icon(
                                BitmapDescriptorFactory.fromBitmap(
                                        driverIcon
                                )
                        )
                        .position(
                                new LatLng(
                                        Double.parseDouble(snappedLatitude),
                                        Double.parseDouble(snappedLongitude)
                                )
                        ));
            }

            //When activity created and load map first time do not start animation.
            if (animationStart) {
                // Update camera rotation according to marker direction
                if (null != mCurrentLocation) {
                    // Animate driver marker to the target location.
                    if (isLastAnimationComplete) {
                        if (Utils.calculateDistance(driverMarker.getPosition().latitude,
                                driverMarker.getPosition().longitude,
                                mCurrentLocation.getLatitude(),
                                mCurrentLocation.getLongitude()) >= 10) {
                            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(),
                                    mCurrentLocation.getLongitude());
                            LatLngInterpolator interpolator = new Spherical();
                            animateMarker(latLng, interpolator);
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
            if (null == mGoogleMap || callDriverData == null) return;
            if (pickupMarker != null) {
                pickupMarker.remove();
            }

            if (callDriverData.getPickup().getLat() != null &&
                    callDriverData.getPickup().getLng() != null) {
                LatLng startLatLng = new LatLng(
                        callDriverData.getPickup().getLat(),
                        callDriverData.getPickup().getLng()
                );
                pickupMarker = mGoogleMap.addMarker(new MarkerOptions().
                        icon(Utils.getBitmapDiscriptor(mCurrentActivity))
                        .position(startLatLng));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    /***
     * Add Pickup marker to the pickup location.
     *
     * The Time complexity or rate of growth of a function is: O(n)
     */
    private void updateDropOffMarkers() {
        try {
            if (null == mGoogleMap || callDriverData == null) return;
            if (dropOffMarker != null) {
                dropOffMarker.remove();
            }
            List<LatLng> latLngList = Utils.getDropDownLatLngList(callDriverData);

            for (int i = 0; i < latLngList.size(); i++) {

                dropOffMarker = mGoogleMap.addMarker(new MarkerOptions().
                        icon(Utils.getDropOffBitmapDiscriptor(mCurrentActivity,
                                String.valueOf(i + 1)))
                        .position(latLngList.get(i)));
            }


        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }


    /***
     * OnClick listener for an activity.
     *
     * @param view The view that has been clicked.
     */
    @OnClick({R.id.currentLocationIv, R.id.cancelBtn, R.id.cvLocation, R.id.jobBtn,
            R.id.callBtn, R.id.tafseelLayout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.currentLocationIv: {
                directionClick();
                break;
            }

            case R.id.jobBtn: {
                checkTripButtonClick();
                break;
            }

            case R.id.cancelBtn: {
                if (Utils.isCancelAfter5Min(callDriverData.getAcceptedTime())) {
                    showCancelationDialogWIthFee();
                } else {
                    cancelReasonDialog();
                }
                break;
            }

            case R.id.cvLocation: {
                setDriverLocation();
                break;
            }

            case R.id.callBtn: {
                ActivityStackManager.getInstance().startMapDetailsActivity(
                        mCurrentActivity,
                        Constants.MapDetailsFragmentTypes.TYPE_CALL
                );
                break;
            }

            case R.id.tafseelLayout: {
                ActivityStackManager.getInstance().startMapDetailsActivity(
                        mCurrentActivity,
                        Constants.MapDetailsFragmentTypes.TYPE_TAFSEEL
                );
                break;
            }
        }
    }

    /**
     * Show the cancelation Dialog with cancelation fee message.
     */
    private void showCancelationDialogWIthFee() {
        String msg = getString(R.string.cancelation_message,
                AppPreferences.getSettings().getSettings().getCancel_time());
        Dialogs.INSTANCE.showAlertDialogWithTickCross(mCurrentActivity, v -> {
            Dialogs.INSTANCE.dismissDialog();
            cancelReasonDialog();
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialogs.INSTANCE.dismissDialog();
            }
        }, getString(R.string.cancel_batch), msg);
    }

    /**
     * Cancel the multi delivery batch request by giving the cancel reason.
     */
    private void cancelReasonDialog() {
        Dialogs.INSTANCE.showCancelDialog(mCurrentActivity, new StringCallBack() {
            @Override
            public void onCallBack(String msg) {
                Dialogs.INSTANCE.showLoader(mCurrentActivity);
                dataRepository.requestMultiDeliveryCancelBatch(msg, handler);
            }
        });

    }

    /**
     * Invoked this method when current location cardinal button has been clicked
     */
    private void setDriverLocation() {
        if (null != mGoogleMap) {
            Utils.formatMap(mGoogleMap);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(AppPreferences.getLatitude()
                            , AppPreferences.getLongitude())
                    , ZOOM_LEVEL));
        }
    }

    /**
     * Invoked this method when direction google button has been clicked.
     */
    private void directionClick() {
        if (callDriverData != null && callDriverData.getBatchStatus()
                .equalsIgnoreCase(TripStatus.ON_ACCEPT_CALL)) {
            Utils.navigateToGoogleMap(mCurrentActivity, callDriverData);
        } else {
            ActivityStackManager.getInstance().startMapDetailsActivity(
                    mCurrentActivity,
                    Constants.MapDetailsFragmentTypes.TYPE_TAFSEEL
            );
        }
    }

    /***
     * Check the tripInfo button click if the button text is equal to specific tripInfo status i.e "پہنچ گئے",
     * etc. To perform the specific operation like driver arrival dialog,
     * start tripInfo dialog & finish tripInfo dialog
     */
    private void checkTripButtonClick() {
        if (Connectivity.isConnectedFast(mCurrentActivity)) {
            Dialogs.INSTANCE.showLoader(mCurrentActivity);
            if (jobBtn.getText().toString().equalsIgnoreCase(getString(
                    R.string.button_text_arrived))) {
                showDriverArrivedDialog();
            } else if (jobBtn.getText().toString().equalsIgnoreCase(getString(
                    R.string.button_text_start))) {
                showDriverStartedDialog();

            } else if (jobBtn.getText().toString().equalsIgnoreCase(getString(
                    R.string.button_text_finish))) {
                Dialogs.INSTANCE.dismissDialog();
                ActivityStackManager.getInstance().startMapDetailsActivity(
                        mCurrentActivity,
                        Constants.MapDetailsFragmentTypes.TYPE_MUKAMAL
                );
            }
        }
    }


    /***
     * show tripInfo started confirmation dialog.
     *
     * Todo 2: add request started socket event
     */
    private void showDriverStartedDialog() {
        if (jobBtn.getText().toString().equalsIgnoreCase(getString(R.string.button_text_start))) {
            Dialogs.INSTANCE.showRideStatusDialog(mCurrentActivity, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialogs.INSTANCE.dismissDialog();
                    requestDriverStarted();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialogs.INSTANCE.dismissDialog();
                }
            }, getString(R.string.ask_started_text));
        }
    }

    /***
     * Show the driver arrived dialog based on the distance from the pickup location.
     * Find the distance between driver location & pickup location if the driver is away
     * show the message that "آپ ابھی بھی کچھ دورہیں" otherwise ask the user to confirm
     * that you have arrived.
     *
     * Todo 1: add request Arrived socket event
     */
    private void showDriverArrivedDialog() {
        int distance = (int) Utils.calculateDistance(AppPreferences.getLatitude(),
                AppPreferences.getLongitude(),
                callDriverData.getPickup().getLat(),
                callDriverData.getPickup().getLng());
        if (distance > ARRIVAL_MAX_DISTANCE_VALUE) {
            boolean showTickBtn = distance < AppPreferences.getSettings().
                    getSettings().getArrived_min_dist();
            Dialogs.INSTANCE.showConfirmArrivalDialog(mCurrentActivity,
                    showTickBtn, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Dialogs.INSTANCE.dismissDialog();
                            //requestArrived();
                            requestDriverArrived();
                        }
                    });
        } else {
            Dialogs.INSTANCE.showRideStatusDialog(mCurrentActivity, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialogs.INSTANCE.dismissDialog();
                    requestDriverArrived();
                    //requestArrived();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialogs.INSTANCE.dismissDialog();
                }
            }, getString(R.string.ask_arrived_text));
        }
    }

    /***
     * Set the tripInfo arrived state.
     */
    private void requestDriverArrived() {
        Dialogs.INSTANCE.showLoader(mCurrentActivity);
        repository.requestMultiDeliveryDriverArrived(handler);
    }

    /***
     * Set the tripInfo started state.
     */
    private void requestDriverStarted() {
        Dialogs.INSTANCE.showLoader(mCurrentActivity);
        geocodeStrategyManager.fetchLocation(AppPreferences.getLatitude(), AppPreferences.getLongitude());
    }

    /***
     * Network Change Broadcast Reciever to listen GPS change & internet conectivity change
     */
    private NetworkChangeListener networkChangeListener = new NetworkChangeListener() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.Actions.ON_GPS_ENABLED_CHANGE.equalsIgnoreCase(intent.getAction()) ||
                    Constants.Actions.ON_LOCATION_CHANGED.
                            equalsIgnoreCase(intent.getAction())) {
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
        lastApiCallLatLng = null;
        AppPreferences.setDirectionsApiKeyRequired(true);
        getDriverRoadPosition(
                new com.google.maps.model.LatLng(
                        AppPreferences.getLatitude(),
                        AppPreferences.getLongitude()
                )
        );
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(int routeType, List<Route> route, int shortestRouteIndex) {
        if (mCurrentActivity != null && mGoogleMap != null) {
            mRouteLatLng = route.get(0).getPoints();
            updateEtaAndCallData(
                    String.valueOf((route.get(0).getDurationValue())),
                    String.valueOf(
                            (route.get(0).getDistanceValue())));
            updatePolyLine(route);
        } else {
            lastApiCallLatLng = null;
        }
    }

    /***
     * Update the polyline on the map.
     *
     * @param route list of route to draw the polyline on the specific route.
     */
    public void updatePolyLine(List<Route> route) {
        if (mapPolylines != null) {
            mapPolylines.remove();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(ContextCompat.getColor(mCurrentActivity, R.color.blue));
        polyOptions.width(Utils.dpToPx(mCurrentActivity, 5));
        polyOptions.addAll(route.get(0).getPoints());
        mapPolylines = mGoogleMap.addPolyline(polyOptions);
    }

    @Override
    public void onRoutingCancelled() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        //register the location reciever to recieve location from LocationService.
        registerReceiver(locationReceiver, new IntentFilter(Keys.LOCATION_UPDATE_BROADCAST));
        //register the network reciever to recieve gps/location changes & network connection changes
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.Actions.ON_CONECTIVITY_CHANGED);
        intentFilter.addAction(Constants.Actions.ON_LOCATION_CHANGED);
        intentFilter.addCategory(Constants.Category.ON_CATEGORY_DEFAULT);
        registerReceiver(networkChangeListener, intentFilter);
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

    /**
     * Call back that will be invoked when response received for the event
     * i.e arrived, started & complete.
     */
    private IUserDataHandler handler = new UserDataHandler() {

        @Override
        public void onMultiDeliveryDriverCancelBatch(MultiDeliveryCancelBatchResponse response) {
            onCancelBatch();
            EventBus.getDefault().post(Constants.Broadcast.UPDATE_FOREGROUND_NOTIFICATION);
        }

        @Override
        public void onMultiDeliveryDriverArrived(MultiDeliveryDriverArrivedResponse response) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mCurrentActivity != null) {
                        Dialogs.INSTANCE.dismissDialog();
                        setArrivedState();
                        EventBus.getDefault().post(Constants.Broadcast.UPDATE_FOREGROUND_NOTIFICATION);
                    }
                }
            });
        }

        @Override
        public void onMultiDeliveryDriverStarted(MultiDeliveryDriverStartedResponse response) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mCurrentActivity != null) {
                        Dialogs.INSTANCE.dismissDialog();
                        setStartedState();
                        EventBus.getDefault().post(Constants.Broadcast.UPDATE_FOREGROUND_NOTIFICATION);
                    }
                }
            });
        }

        @Override
        public void onError(int errorCode, String errorMessage) {
            Dialogs.INSTANCE.dismissDialog();
            if (errorCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                EventBus.getDefault().post(Keys.UNAUTHORIZED_BROADCAST);
            } else {
                EventBus.getDefault().post(Keys.MULTIDELIVERY_ERROR_BORADCAST);
            }
        }
    };

    /**
     * Invoked this method when driver has been started.
     * Change the batch status, remove the poly lines & set the bottom button to "Mukamal"
     */
    private void setStartedState() {
        try {
            cancelBtn.setVisibility(View.GONE);
            if (mapPolylines != null) {
                mapPolylines.remove();
            }
            timeTv.setVisibility(View.GONE);
            timeUnitLabelTv.setVisibility(View.GONE);
            timeView.setVisibility(View.GONE);
            if (pickupMarker != null)
                pickupMarker.remove();
            pickView.setVisibility(View.GONE);
            distanceTv.setVisibility(View.GONE);
            pickUpDistanceUnit.setVisibility(View.GONE);
            callDriverData.setBatchStatus(TripStatus.ON_START_TRIP);
            AppPreferences.setTripStatus(TripStatus.ON_START_TRIP);
            AppPreferences.setMultiDeliveryCallDriverData(callDriverData);
            updateDriverMarker(
                    String.valueOf(mCurrentLocation.getLatitude()),
                    String.valueOf(mCurrentLocation.getLongitude())
            );
            tafseelLayout.setVisibility(View.VISIBLE);
            jobBtn.setText(getString(R.string.button_text_finish));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Invoked this method when driver has been arrived.
     * Change the batch status, remove the poly lines & set the bottom button to "Start"
     */
    private void setArrivedState() {
        try {
            if (mapPolylines != null)
                mapPolylines.remove();
            timeTv.setVisibility(View.GONE);
            timeUnitLabelTv.setVisibility(View.GONE);
            timeView.setVisibility(View.GONE);
            if (pickupMarker != null)
                pickupMarker.remove();
            pickView.setVisibility(View.GONE);
            distanceTv.setVisibility(View.GONE);
            pickUpDistanceUnit.setVisibility(View.GONE);
            callDriverData.setBatchStatus(TripStatus.ON_ARRIVED_TRIP);
            AppPreferences.setTripStatus(TripStatus.ON_ARRIVED_TRIP);
            AppPreferences.setMultiDeliveryCallDriverData(callDriverData);
            jobBtn.setText(getString(R.string.button_text_start));
            tafseelLayout.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Invoked this method when multi delivery batch has been canceled.
     */
    private void onCancelBatch() {
        try {
            Dialogs.INSTANCE.dismissDialog();
            AppPreferences.setAvailableStatus(true);
            callDriverData.setBatchStatus(TripStatus.ON_FREE);
            AppPreferences.setMultiDeliveryCallDriverData(callDriverData);

            if (Utils.isConnected(MultipleDeliveryBookingActivity.this, false)) {
                dataRepository.requestLocationUpdate(
                        mCurrentActivity,
                        handler,
                        AppPreferences.getLatitude(),
                        AppPreferences.getLongitude());
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ActivityStackManager
                            .getInstance()
                            .startHomeActivity(true, mCurrentActivity);
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        AppPreferences.setMultiDeliveryJobActivityOnForeground(false);
        //unregister the location receiver to stop receiving location when activity has destroyed.
        unregisterReceiver(locationReceiver);
        //unregister the network change receiver to stop receiving when activity has destroyed.
        unregisterReceiver(networkChangeListener);
        mapView.onDestroy();
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        super.onStop();
        AppPreferences.setMultiDeliveryJobActivityOnForeground(false);
    }

    @Override
    public void onBackPressed() {

    }


}

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
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bykea.pk.partner.Notifications;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.response.MultiDeliveryCallDriverData;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.tracking.AbstractRouting;
import com.bykea.pk.partner.tracking.Route;
import com.bykea.pk.partner.tracking.RouteException;
import com.bykea.pk.partner.tracking.Routing;
import com.bykea.pk.partner.tracking.RoutingListener;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.LatLngInterpolator;
import com.bykea.pk.partner.ui.helpers.Spherical;
import com.bykea.pk.partner.ui.helpers.adapters.CallAdapter;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 * MultiDelivery Booking Activity.
 *
 * Check todo of this class.
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
        callDriverData = AppPreferences.getMultiDeliveryCallDriverData();
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
        setTimeDistance(Utils.formatETA(mCallData.getArivalTime()),
                mCallData.getDistance());
        drawRouteToPickup();
    }

    /***
     * Draw the route from driver current position toward pickupLocation
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
                || Utils.calculateDistance(currentApiCallLatLng.latitude,
                currentApiCallLatLng.longitude, lastApiCallLatLng.latitude,
                lastApiCallLatLng.longitude) < 15)) {
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
                    setPickupBounds();
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

    /***
     * Set Pickup Bounds to move google map camera
     */
    private void setPickupBounds() {
        int padding = (int) mCurrentActivity.getResources().getDimension(R.dimen._30sdp);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(getCurrentLatLngBounds(), padding);
        padding = (int) mCurrentActivity.getResources().getDimension(R.dimen._50sdp);
        mGoogleMap.setPadding(0, padding, 0, padding);
        mGoogleMap.moveCamera(cu);
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



        LatLngBounds tmpBounds = builder.build();
        LatLng center = tmpBounds.getCenter();
        LatLng northEast = move(center, 709, 709);
        LatLng southWest = move(center, -709, -709);

        builder.include(southWest);
        builder.include(northEast);
        return builder.build();
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
    }

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
                    updateCamera(mLocBearing);
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
                        icon(Utils.getBitmapDiscriptor(mCurrentActivity, true))
                        .position(startLatLng));
            }
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
     * OnClick listener for an activity.
     *
     * @param view a view where user have clicked.
     */
    @OnClick({R.id.currentLocationIv, R.id.jobBtn, R.id.callBtn, R.id.tafseelLayout})
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

    private void directionClick() {
        if (mCallData != null && mCallData.getStatus().equalsIgnoreCase(TripStatus.ON_START_TRIP)) {
            ActivityStackManager.getInstance().startMapDetailsActivity(
                    mCurrentActivity,
                    Constants.MapDetailsFragmentTypes.TYPE_TAFSEEL
            );
        } else {
            Utils.navigateToGoogleMap(mCurrentActivity, mCallData);
        }
    }

    /***
     * Check the trip button click if the button text is equal to specific trip status i.e "پہنچ گئے",
     * etc. To perform the specific operation like driver arrival dialog,
     * start trip dialog & finish trip dialog
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
     * show trip started confirmation dialog.
     *
     * Todo 2: add request started socket event
     */
    private void showDriverStartedDialog() {
        if (jobBtn.getText().toString().equalsIgnoreCase(getString(R.string.button_text_start))) {
            Dialogs.INSTANCE.showRideStatusDialog(mCurrentActivity, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialogs.INSTANCE.dismissDialog();
                    setStartedState();
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
                Double.parseDouble(mCallData.getStartLat()),
                Double.parseDouble(mCallData.getStartLng()));
        if (distance > ARRIVAL_MAX_DISTANCE_VALUE) {
            boolean showTickBtn = distance < AppPreferences.getSettings().
                    getSettings().getArrived_min_dist();
            Dialogs.INSTANCE.showConfirmArrivalDialog(mCurrentActivity,
                    showTickBtn, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Dialogs.INSTANCE.dismissDialog();
                            //requestArrived();
                            setArrivedState();
                        }
                    });
        } else {
            Dialogs.INSTANCE.showRideStatusDialog(mCurrentActivity, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialogs.INSTANCE.dismissDialog();
                    setArrivedState();
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
     * Set the trip arrived state.
     *
     * Todo 3: set the arrived data when socket implemented
     */
    private void setArrivedState() {
        timeTv.setVisibility(View.GONE);
        timeUnitLabelTv.setVisibility(View.GONE);
        timeView.setVisibility(View.GONE);

        pickView.setVisibility(View.GONE);
        distanceTv.setVisibility(View.GONE);
        pickUpDistanceUnit.setVisibility(View.GONE);

        jobBtn.setText(getString(R.string.button_text_start));
        tafseelLayout.setVisibility(View.VISIBLE);
    }

    /***
     * Set the trip started state.
     *
     * Todo 4: set the trip started data
     */
    private void setStartedState() {
        cancelBtn.setVisibility(View.GONE);
        jobBtn.setText(getString(R.string.button_text_finish));
        if (mapPolylines != null){
            mapPolylines.remove();
        }

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
                    String.valueOf((route.get(0).getDurationValue() / SECONDS_IN_MINUTES)),
                    Utils.formatDecimalPlaces(String.valueOf(
                            (route.get(0).getDistanceValue() / METERS_IN_KILOMETER)),
                            1));
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

    @Override
    protected void onDestroy() {
        //unregister the location reciever to stop recieving location when activity has destroyed.
        unregisterReceiver(locationReceiver);
        //unregister the network change reciever to stop recieving when activity has destroyed.
        unregisterReceiver(networkChangeListener);
        mapView.onDestroy();
        super.onDestroy();

    }


    @Override
    public void onBackPressed() {

    }
}

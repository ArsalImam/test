package com.bykea.pk.partner.ui.activities;

import android.location.Location;
import android.os.Bundle;

import com.bykea.pk.partner.Notifications;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.Place;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
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
import com.instabug.library.util.StringUtility;

import org.apache.commons.lang3.StringUtils;

import butterknife.ButterKnife;

/***
 * MultiDelivery Booking Activity.
 */
public class MultipleDeliveryBookingActivity extends BaseActivity {

    private MultipleDeliveryBookingActivity mCurrentActivity;
    private UserRepository dataRepository;
    private MapView mapView;
    private GoogleMap mGoogleMap;
    private NormalCallData mCallData;
    private Marker pickupMarker;
    private Location mCurrentLocation;
    private float mLocBearing = 0.0f;

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
        mCallData = AppPreferences.getCallData();
        mCurrentLocation = new Location(StringUtils.EMPTY);
        mLocBearing = (float) AppPreferences.getBearing();
    }

    /***
     * Set the current location which is comming from shared preferences.
     */
    private void setCurrentLocation(){
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
                    setCurrentLocation();
                    addPickupMarker();
                    updateCamera(mLocBearing);
                }
            });
        }
    };

    /***
     * Add Pickup marker to the pickup location.
     */
    private void addPickupMarker() {
        try{
            double lat = Double.parseDouble(mCallData.getStartLat());
            double lng = Double.parseDouble(mCallData.getStartLng());
            LatLng startLatLng = new LatLng(lat, lng);
            pickupMarker = mGoogleMap.addMarker(new MarkerOptions().
                    icon(Utils.getBitmapDiscriptor(mCurrentActivity, true))
                    .position(startLatLng));
        }catch (NumberFormatException e){
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


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}

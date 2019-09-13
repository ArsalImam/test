package com.bykea.pk.partner.tracking;

import android.content.Context;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.bykea.pk.partner.ui.helpers.AppPreferences;


public enum TrackingMap {
    INSTANCE;


    private GoogleMap mGoogleMap;
    private Marker mdriverMarker;
    private Marker pickupMarker;
    private Marker dropOffMarker;

    public GoogleMap getmGoogleMap() {
        return mGoogleMap;
    }

    public void setmGoogleMap(GoogleMap mGoogleMap) {
        this.mGoogleMap = mGoogleMap;
    }

    public Marker getMdriverMarker() {
        return mdriverMarker;
    }

    public void setMdriverMarker(Marker mdriverMarker) {
        this.mdriverMarker = mdriverMarker;
    }

    public Marker getPickupMarker() {
        return pickupMarker;
    }

    public void setPickupMarker(Marker pickupMarker) {
        this.pickupMarker = pickupMarker;
    }

    public Marker getDropOffMarker() {
        return dropOffMarker;
    }

    public void setDropOffMarker(Marker dropOffMarker) {
        this.dropOffMarker = dropOffMarker;
    }

    public void boundLatLng() {

    }

    public void updateCamera() {

    }

    public void animateMarker() {

    }

    public static void addCurrentMarker(GoogleMap map, int icon) {
        if (null == map) return;
        map.addMarker(new MarkerOptions().position(new LatLng(AppPreferences.getLatitude(),
                AppPreferences.getLongitude())).flat(true)
                .icon(BitmapDescriptorFactory.fromResource(icon)));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(AppPreferences.getLatitude()
                        , AppPreferences.getLongitude())
                , 16.0f));
    }

    public static void addMarker(Context context, String bearing, GoogleMap map, double lat, double lng, int icon) {
        if (null == map) return;
                map.addMarker(new MarkerOptions().position(new LatLng(lat,
                        lng)).icon(BitmapDescriptorFactory.fromResource(icon)));
    }
}

package com.bykea.pk.partner.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;

import com.bykea.pk.partner.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;

import static java.lang.Math.PI;

public class MapUtil {

    private static final double EARTHRADIUS = 6366198;

    public static LatLng move(LatLng startLL, double toNorth, double toEast) {
        double lonDiff = meterToLongitude(toEast, startLL.latitude);
        double latDiff = meterToLatitude(toNorth);
        return new LatLng(startLL.latitude + latDiff, startLL.longitude
                + lonDiff);
    }

    public static double meterToLongitude(double meterToEast, double latitude) {
        double latArc = Math.toRadians(latitude);
        double radius = Math.cos(latArc) * EARTHRADIUS;
        double rad = meterToEast / radius;
        return Math.toDegrees(rad);
    }

    public static double meterToLatitude(double meterToNorth) {
        double rad = meterToNorth / EARTHRADIUS;
        return Math.toDegrees(rad);
    }

    public static View getPickupMarkerLayout(Context context, boolean showOnLeft) {
        int layoutId = showOnLeft ? R.layout.custom_marker_left : R.layout.custom_marker_right;
        return ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(layoutId, null);
    }

    public static View getDropOffMarkerLayout(Context context, boolean showOnLeft) {
        int layoutId = showOnLeft ? R.layout.custom_marker_left_drop : R.layout.custom_marker_right_drop;
        return ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(layoutId, null);
    }

    public static BitmapDescriptor getMarkerBitmapDescriptorFromView(View view) {

        // TODO: Single views have trouble with measure.
        final int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(spec, spec);

        final int width = view.getMeasuredWidth();
        final int height = view.getMeasuredHeight();

        view.layout(0, 0, width, height);

        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);

        Drawable drawable = view.getBackground();
        if (drawable != null) {
            drawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        }


        view.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static LatLng movePoint(LatLng originalLatLng, double distanceInMetres, double bearing) {
        double brngRad = Math.toRadians(bearing);
        double latRad = Math.toRadians(originalLatLng.latitude);
        double lonRad = Math.toRadians(originalLatLng.longitude);
        int earthRadiusInMetres = 6371000;
        double distFrac = distanceInMetres / earthRadiusInMetres;

        double latitudeResult = Math.asin(Math.sin(latRad) * Math.cos(distFrac) + Math.cos(latRad) * Math.sin(distFrac) * Math.cos(brngRad));
        double a = Math.atan2(Math.sin(brngRad) * Math.sin(distFrac) * Math.cos(latRad), Math.cos(distFrac) - Math.sin(latRad) * Math.sin(latitudeResult));
        double longitudeResult = (lonRad + a + 3 * PI) % (2 * PI) - PI;

        LatLng latLng = new LatLng(Math.toDegrees(latitudeResult), Math.toDegrees(longitudeResult));
//        Utils.redLog("Loc: ", Math.toDegrees(latitudeResult) + "," + Math.toDegrees(longitudeResult));
        return latLng;
    }

    /**
     * This method determines how much we should increase distance while checking marker has
     * more area on left or right
     *
     * @param airDistance Air Distance in Meter between Pick Up and Drop Off marker
     * @return distance to be incremented
     */
    public static int getIncrementFactor(double airDistance) {
        int incrementFactor = Constants.MARKER_INCREMENT_FACTOR_DEFAULT; //meter
        if (airDistance > Constants.MARKER_INCREMENT_FACTOR_TEN_KM) {
            incrementFactor = (Constants.MARKER_INCREMENT_FACTOR_TEN_KM / 1000) * incrementFactor;
        } else if (airDistance > Constants.MARKER_INCREMENT_FACTOR_SIX_KM) {
            incrementFactor = (Constants.MARKER_INCREMENT_FACTOR_SIX_KM / 1000) * incrementFactor;
        } else if (airDistance > Constants.MARKER_INCREMENT_FACTOR_FOUR_KM) {
            incrementFactor = (Constants.MARKER_INCREMENT_FACTOR_FOUR_KM / 1000) * incrementFactor;
        } else if (airDistance > Constants.MARKER_INCREMENT_FACTOR_TWO_KM) {
            incrementFactor = (Constants.MARKER_INCREMENT_FACTOR_TWO_KM / 1000) * incrementFactor;
        }
        return incrementFactor;
    }

    /**
     * @param map
     * @param latLng
     * @return
     */
    public static boolean isVisibleOnMap(GoogleMap map, LatLng latLng) {
        VisibleRegion vr = map.getProjection().getVisibleRegion();
        return vr.latLngBounds.contains(latLng);
    }

}

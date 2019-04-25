package com.bykea.pk.partner.ui.helpers;

import com.google.android.gms.maps.model.LatLng;

import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

public class Spherical implements LatLngInterpolator {

    /***
     * Computes Spherical interpolation coefficients from the start lat lng to end lat lng i.e
     * animation start point to animation end point.
     *
     * @Link https://gist.github.com/broady/6314689
     *
     * @param fraction
     * @param from start latlng points
     * @param to end latlng points
     * @return The polar latlng
     */

    @Override
    public LatLng interpolate(float fraction, LatLng from, LatLng to) {
        double fromLat = toRadians(from.latitude);
        double fromLng = toRadians(from.longitude);
        double toLat = toRadians(to.latitude);
        double toLng = toRadians(to.longitude);
        double cosFromLat = cos(fromLat);
        double cosToLat = cos(toLat);

        // Computes Spherical interpolation coefficients.
        double angle = computeAngleBetween(fromLat, fromLng, toLat, toLng);
        double sinAngle = sin(angle);
        if (sinAngle < 1E-6) {
            return from;
        }
        double a = sin((1 - fraction) * angle) / sinAngle;
        double b = sin(fraction * angle) / sinAngle;

        // Converts from polar to vector and interpolate.
        double x = a * cosFromLat * cos(fromLng) + b * cosToLat * cos(toLng);
        double y = a * cosFromLat * sin(fromLng) + b * cosToLat * sin(toLng);
        double z = a * sin(fromLat) + b * sin(toLat);

        // Converts interpolated vector back to polar.
        double lat = atan2(z, sqrt(x * x + y * y));
        double lng = atan2(y, x);
        return new LatLng(toDegrees(lat), toDegrees(lng));
    }

    /***
     * Using Haversine's formula.
     * Find the angle between start latlng to endlatlng
     *
     * @param fromLat start latitude
     * @param fromLng start longitude
     * @param toLat end latitude
     * @param toLng end longitude
     *
     * @return the angle between the start latlng to end latlng
     */
    private double computeAngleBetween(double fromLat, double fromLng, double toLat, double toLng) {
        double dLat = fromLat - toLat;
        double dLng = fromLng - toLng;
        return 2 * asin(sqrt(pow(sin(dLat / 2), 2) +
                cos(fromLat) * cos(toLat) * pow(sin(dLng / 2), 2)));
    }
}

package com.bykea.pk.partner.ui.helpers;

import com.google.android.gms.maps.model.LatLng;

public interface LatLngInterpolator {
    public LatLng interpolate(float fraction, LatLng a, LatLng b);
}

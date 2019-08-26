package com.bykea.pk.partner.map.tracking;


public interface RoadLocationListener {

    void onGetRoadLocation(double lat, double lng);

    void onErrorRoadLocation(String msg);
}

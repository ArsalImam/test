package com.bykea.pk.partner.map.tracking;

import java.util.List;

public interface RoutingListener {
    void onRoutingFailure(int routeType, RouteException e);

    void onRoutingStart();

    void onRoutingSuccess(int routeType, List<Route> route, int shortestRouteIndex);

    void onRoutingCancelled();
}
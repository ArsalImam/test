package com.bykea.pk.partner.tracking;

import java.util.List;

public interface RoutingListener {
    void onRoutingFailure(int routeType, RouteException e);

    void onRoutingStart();

    void onRoutingSuccess(int routeType, List<Route> route, int shortestRouteIndex);

    void onRoutingCancelled();
}
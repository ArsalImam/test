package com.bykea.pk.partner.tracking.parser;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;

import com.bykea.pk.partner.tracking.GoogleParser;
import com.bykea.pk.partner.tracking.Route;
import com.bykea.pk.partner.tracking.RouteException;
import com.bykea.pk.partner.tracking.Routing;
import com.bykea.pk.partner.tracking.RoutingListener;
import com.bykea.pk.partner.utils.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRouter extends AsyncTask<Void, Void, List<Route>> {
    protected List<RoutingListener> alisteners;

    private int routeType = 0;
    private Context mContext;


    protected static final String DIRECTIONS_API_URL = "https://maps.googleapis.com/maps/api/directions/json?";

    /* Private member variable that will hold the RouteException instance created in the background thread */
    private RouteException mException = null;

    public enum TravelMode {
        BIKING("bicycling"),
        DRIVING("driving"),
        WALKING("walking"),
        TRANSIT("transit");

        protected String sValue;

        TravelMode(String sValue) {
            this.sValue = sValue;
        }

        protected String getValue() {
            return sValue;
        }
    }

    public enum AvoidKind {
        TOLLS(1, "tolls"),
        HIGHWAYS(1 << 1, "highways"),
        FERRIES(1 << 2, "ferries");

        private final String sRequestParam;
        private final int sBitValue;

        AvoidKind(int bit, String param) {
            this.sBitValue = bit;
            this.sRequestParam = param;
        }

        protected int getBitValue() {
            return sBitValue;
        }

        protected static String getRequestParam(int bit) {
            StringBuilder ret = new StringBuilder();
            for (AvoidKind kind : AvoidKind.values()) {
                if ((bit & kind.sBitValue) == kind.sBitValue) {
                    ret.append(kind.sRequestParam).append('|');
                }
            }
            return ret.toString();
        }
    }

    protected AbstractRouter(RoutingListener listener, int routeType, Context context) {
        this.alisteners = new ArrayList<RoutingListener>();
        this.routeType = routeType;
        this.mContext = context;
        registerListener(listener);
    }

    public void registerListener(RoutingListener mListener) {
        if (mListener != null) {
            alisteners.add(mListener);
        }
    }

    protected void dispatchOnStart() {
        for (RoutingListener mListener : alisteners) {
            mListener.onRoutingStart();
        }
    }

    protected void dispatchOnFailure(RouteException exception) {
        for (RoutingListener mListener : alisteners) {
            mListener.onRoutingFailure(routeType, exception);
        }
    }

    protected void dispatchOnSuccess(List<Route> route, int shortestRouteIndex) {
        for (RoutingListener mListener : alisteners) {
            mListener.onRoutingSuccess(routeType, route, shortestRouteIndex);
        }
    }

    private void dispatchOnCancelled() {
        for (RoutingListener mListener : alisteners) {
            mListener.onRoutingCancelled();
        }
    }

    /**
     * Performs the call to the google maps API to acquire routing data and
     * deserializes it to a format the map can display.
     *
     * @return an array list containing the routes
     */
    @Override
    protected List<Route> doInBackground(Void... voids) {
        List<Route> result = new ArrayList<Route>();
        try {
            result = new GoogleParser(constructURL()).parse();
        } catch (RouteException e) {
            mException = e;
        }
        return result;
    }

    protected abstract String constructURL();

    @Override
    protected void onPreExecute() {
        dispatchOnStart();
    }

    @Override
    protected void onPostExecute(List<Route> result) {
        if (!result.isEmpty()) {
            int shortestRouteIndex = 0;
            int minDistance = Integer.MAX_VALUE;

            for (int i = 0; i < result.size(); i++) {
                PolylineOptions mOptions = new PolylineOptions();
                Route route = result.get(i);

                //Find the shortest route index
                if (route.getLength() < minDistance) {
                    shortestRouteIndex = i;
                    minDistance = route.getLength();
                }

                //In case of more than 5 alternative routes
                for (LatLng point : route.getPoints()) {
                    mOptions.add(point);
                }
                int colorIndex = i % Routing.COLORS.length;
                mOptions.color(ContextCompat.getColor(mContext, Routing.COLORS[colorIndex]));
                mOptions.width(Utils.dpToPx(mContext, 10 + i * 3));
                result.get(i).setPolyOptions(mOptions);
            }
            dispatchOnSuccess(result, shortestRouteIndex);
        } else {
            dispatchOnFailure(mException);
        }
    }//end onPostExecute method

    @Override
    protected void onCancelled() {
        dispatchOnCancelled();
    }

}
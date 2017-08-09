package com.bykea.pk.partner.tracking;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.GeoApiContext;
import com.google.maps.RoadsApi;
import com.google.maps.model.SnappedPoint;
import com.bykea.pk.partner.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class RoadLocation extends AsyncTask<List<com.google.maps.model.LatLng>, Void, List<SnappedPoint>> {

    private GeoApiContext mGeoApiContext;
    private RoadLocationListener mRoadLocationListener;

    public RoadLocation(GeoApiContext geoApiContext, Context context, RoadLocationListener listener) {
        Context mContext = context;
        this.mGeoApiContext = geoApiContext;
        this.mRoadLocationListener = listener;
    }

    @Override
    protected List<SnappedPoint> doInBackground(List<com.google.maps.model.LatLng>... params) {

        try {
            return snapToRoads(mGeoApiContext, params[0]);
        } catch (final Exception ex) {
            ex.printStackTrace();
            mRoadLocationListener.onErrorRoadLocation("Error in getting location from road api.");
            return null;
        }

    }

    @Override
    protected void onPostExecute(List<SnappedPoint> snappedPoints) {
        if (null != snappedPoints) {
            try {
                LatLng[] mapPoints =
                        new LatLng[snappedPoints.size()];
                int i = 0;
                LatLngBounds.Builder bounds = new LatLngBounds.Builder();
                for (SnappedPoint point : snappedPoints) {
                    mapPoints[i] = new LatLng(point.location.lat,
                            point.location.lng);
                    bounds.include(mapPoints[i]);
                    i += 1;
                }

                Utils.redLog("ROAD LOCATION RECEIVED", mapPoints[0].latitude + " : " + mapPoints[0].longitude);
                mRoadLocationListener.onGetRoadLocation(mapPoints[0].latitude, mapPoints[0].longitude);

            } catch (Exception e) {
                e.printStackTrace();
                mRoadLocationListener.onErrorRoadLocation("Error in getting location from road api.");
            }
        }
    }


    private List<SnappedPoint> snapToRoads(GeoApiContext context, List<com.google.maps.model.LatLng> capturedLocations) throws Exception {
        List<SnappedPoint> snappedPoints = new ArrayList<>();

        int offset = 0;
        while (offset < capturedLocations.size()) {
            // Calculate which points to include in this request. We can't exceed the APIs
            // maximum and we want to ensure some overlap so the API can infer a good location for
            // the first few points in each request.
            if (offset > 0) {
                offset -= 5;   // Rewind to include some previous points
            }
            int lowerBound = offset;
            int upperBound = Math.min(offset + 50, capturedLocations.size());

            // Grab the data we need for this page.
            com.google.maps.model.LatLng[] page = capturedLocations
                    .subList(lowerBound, upperBound)
                    .toArray(new com.google.maps.model.LatLng[upperBound - lowerBound]);

            // Perform the request. Because we have interpolate=true, we will get extra data points
            // between our originally requested path. To ensure we can concatenate these points, we
            // only start adding once we've hit the first new point (i.e. skip the overlap).
            SnappedPoint[] points = RoadsApi.snapToRoads(context, true, page).await();
            boolean passedOverlap = false;
            for (SnappedPoint point : points) {
                if (offset == 0 || point.originalIndex >= 5) {
                    passedOverlap = true;
                }
                if (passedOverlap) {
                    snappedPoints.add(point);
                }
            }

            offset = upperBound;
        }

        return snappedPoints;
    }
}

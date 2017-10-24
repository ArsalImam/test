package com.bykea.pk.partner.ui.helpers;

import android.graphics.Color;
import android.os.AsyncTask;

import com.bykea.pk.partner.models.response.HeatMapUpdatedResponse;
import com.bykea.pk.partner.utils.Constants;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

public class DrawPolygonAsync extends AsyncTask<Object, Object, Void> {

    private ArrayList<HeatMapUpdatedResponse> mHeatMapResponse;
    private HeatMapCallback mCallback;

    public DrawPolygonAsync(ArrayList<HeatMapUpdatedResponse> heatMapResponse, HeatMapCallback callback) {
        mHeatMapResponse = heatMapResponse;
        mCallback = callback;
    }

    public void startAsyncTask() {
        execute();
    }

    @Override
    protected Void doInBackground(Object... params) {
        for (int i = 0; i < mHeatMapResponse.size(); i++) {

            try {
                HeatMapUpdatedResponse.Bounds bounds = mHeatMapResponse.get(i).getBounds();

                LatLng southWest = new LatLng(bounds.getSw().getLat(), bounds.getSw().getLon());
                LatLng northEast = new LatLng(bounds.getNe().getLat(), bounds.getNe().getLon());
                LatLng southEast = new LatLng(bounds.getSw().getLat(), bounds.getNe().getLon());
                LatLng northWest = new LatLng(bounds.getNe().getLat(), bounds.getSw().getLon());

                int opacity = (int) (Math.ceil(mHeatMapResponse.get(i).getOpacity() * Constants.ANDROID_OPACITY));
                float CODE_AREA_STROKE_WIDTH = 0.0f;
                int color = (i + 1) == mHeatMapResponse.size() ? Color.argb(opacity, 0, 0, 255) : Color.argb(opacity, 255, 0, 0);
                PolygonOptions polygonOptions = new PolygonOptions()
                        .strokeColor(color)
                        .strokeWidth(CODE_AREA_STROKE_WIDTH)
                        .fillColor(color);
                polygonOptions.add(southWest, northWest, northEast, southEast);

                mCallback.onHeatMapDataParsed(polygonOptions);
            } catch (Exception ignored) {
                //if there is error in any single hashcode, it won't break the whole task
            }

        }
        return null;
    }

    public interface HeatMapCallback {
        void onHeatMapDataParsed(PolygonOptions options);
    }
}







/* LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng latLng : polygonOptions.getPoints()) {
                    builder.include(latLng);
                }
                IconGenerator icnGenerator = new IconGenerator(mCurrentActivity);
                icnGenerator.setTextAppearance(R.style.iconGenText);
                icnGenerator.setBackground(TRANSPARENT_DRAWABLE);
                icnGenerator.setContentPadding(4, 4, 4, 4);
                Bitmap icon = icnGenerator.makeIcon("1.7");
                MarkerOptions markerOptions = new MarkerOptions().position(builder.build().getCenter())
                        .icon(BitmapDescriptorFactory.fromBitmap(icon)).anchor(0.5f, 0.5f);*/
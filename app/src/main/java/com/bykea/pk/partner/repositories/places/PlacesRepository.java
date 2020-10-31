package com.bykea.pk.partner.repositories.places;

import android.content.Context;

import com.bykea.pk.partner.communication.IResponseCallback;
import com.bykea.pk.partner.communication.rest.RestRequestHandler;
import com.bykea.pk.partner.communication.socket.WebIORequestHandler;
import com.bykea.pk.partner.models.response.BykeaDistanceMatrixResponse;
import com.bykea.pk.partner.models.response.GeoCodeApiResponse;
import com.bykea.pk.partner.models.response.PlaceAutoCompleteResponse;
import com.bykea.pk.partner.models.response.PlaceDetailsResponse;
import com.bykea.pk.partner.ui.helpers.AppPreferences;

public class PlacesRepository {

    private IPlacesDataHandler mUserCallback;
    private RestRequestHandler mRestRequestHandler;

    public PlacesRepository() {
        mRestRequestHandler = new RestRequestHandler();
        WebIORequestHandler mWebIOHandler = WebIORequestHandler.getInstance();
    }


    public void getGoogleGeoCoder(IPlacesDataHandler handler,
                                  String lat, String lng, Context context) {
        mUserCallback = handler;
        mRestRequestHandler.callGeoCoderApi(lat, lng, mGeoCoderPlaces, context);
    }

    private IResponseCallback mGeoCoderPlaces = new IResponseCallback() {
        @Override
        public void onResponse(Object object) {
            if (object instanceof String) {
                mUserCallback.onPlacesResponse((String) object);
            } else if (object instanceof BykeaDistanceMatrixResponse) {
                mUserCallback.onDistanceMatrixResponse((BykeaDistanceMatrixResponse) object);
            } else if (object instanceof PlaceDetailsResponse) {
                mUserCallback.onPlaceDetailsResponse((PlaceDetailsResponse) object);
            } else if (object instanceof PlaceAutoCompleteResponse) {
                mUserCallback.onPlaceAutoCompleteResponse((PlaceAutoCompleteResponse) object);
            } else if (object instanceof GeoCodeApiResponse) {
                mUserCallback.onGeoCodeApiResponse((GeoCodeApiResponse) object);
            }
        }

        @Override
        public void onError(int code, String error) {
            mUserCallback.onError(error);
        }

    };

    /**
     * @param origin      : Longitude and Latitude - String Concatenation
     * @param destination : Longitude and Latitude - String Concatenation
     * @param context     : Calling Activity
     * @param handler     : CallBack
     */

    public void getDistanceMatrix(String origin, String destination, Context context, IPlacesDataHandler handler) {
        mUserCallback = handler;
        mRestRequestHandler.getDistanceMatrix(origin, destination, mGeoCoderPlaces, context);
    }

    /**
     * @param placeId : Place Id
     * @param context : Calling Activity
     * @param handler : CallBack
     */
    public void getPlaceDetails(String placeId, Context context, IPlacesDataHandler handler) {
        mUserCallback = handler;
        mRestRequestHandler.getPlaceDetails(placeId, context, mGeoCoderPlaces);
    }

    /**
     * @param context : Calling Activity
     * @param search  : Filter String Value
     * @param handler : CallBack
     */
    public void getPlaceAutoComplete(Context context, String search, IPlacesDataHandler handler) {
        mUserCallback = handler;
        mRestRequestHandler.autocomplete(context, search, mGeoCoderPlaces);
    }

    /**
     * @param placeId : Place Id
     * @param context : Calling Activity
     * @param handler : CallBack
     */
    public void geoCodeWithPlaceId(String placeId, Context context, IPlacesDataHandler handler) {
        mUserCallback = handler;
        AppPreferences.setGeoCoderApiKeyRequired(true);
        mRestRequestHandler.callGeoCodeApiWithPlaceId(placeId, context, mGeoCoderPlaces);
    }

    public void getOSMGeoCoder(IPlacesDataHandler handler,
                               String lat, String lng, Context context) {
        mUserCallback = handler;
        mRestRequestHandler.callOSMGeoCoderApi(lat, lng, mGeoCoderPlaces, context);
    }
}
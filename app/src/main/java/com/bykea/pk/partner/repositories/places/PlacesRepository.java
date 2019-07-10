package com.bykea.pk.partner.repositories.places;

import android.content.Context;

import com.bykea.pk.partner.communication.IResponseCallback;
import com.bykea.pk.partner.communication.rest.RestRequestHandler;
import com.bykea.pk.partner.communication.socket.WebIORequestHandler;
import com.bykea.pk.partner.models.response.GeoCodeApiResponse;
import com.bykea.pk.partner.models.response.GoogleDistanceMatrixApi;
import com.bykea.pk.partner.models.response.PlaceAutoCompleteResponse;
import com.bykea.pk.partner.models.response.PlaceDetailsResponse;

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
            } else if (object instanceof GoogleDistanceMatrixApi) {
                mUserCallback.onDistanceMatrixResponse((GoogleDistanceMatrixApi) object);
            } else if (object instanceof PlaceDetailsResponse) {
                mUserCallback.onPlaceDetailsResponse((PlaceDetailsResponse) object);
            }else if (object instanceof PlaceAutoCompleteResponse) {
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

    public void getDistanceMatrix(String origin, String destination, Context context, IPlacesDataHandler handler) {
        mUserCallback = handler;
        mRestRequestHandler.getDistanceMatriax(origin, destination, mGeoCoderPlaces, context);
    }

    public void getPlaceDetails(String placeId, Context context, IPlacesDataHandler handler) {
        mUserCallback = handler;
        mRestRequestHandler.getPlaceDetails(placeId, context, mGeoCoderPlaces);
    }

    public void getPlaceAutoComplete(Context context, String search, IPlacesDataHandler handler) {
        mUserCallback = handler;
        mRestRequestHandler.autocomplete(context, search, mGeoCoderPlaces);
    }

    public void geoCodeWithPlaceId(String placeId, Context context, IPlacesDataHandler handler) {
        mUserCallback = handler;
        mRestRequestHandler.callGeoCodeApiWithPlaceId(placeId, context, mGeoCoderPlaces);
    }


}

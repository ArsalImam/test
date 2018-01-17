package com.bykea.pk.partner.repositories.places;

import android.content.Context;

import com.bykea.pk.partner.communication.IResponseCallback;
import com.bykea.pk.partner.communication.rest.RestRequestHandler;
import com.bykea.pk.partner.communication.socket.WebIORequestHandler;
import com.bykea.pk.partner.models.response.GoogleDistanceMatrixApi;


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

    public void getDistanceMatrix(String origin, String destination, Context context, IPlacesDataHandler handler) {
        mUserCallback = handler;
        mRestRequestHandler.getDistanceMatriax(origin, destination, mGeoCoderPlaces, context);
    }


    private IResponseCallback mGeoCoderPlaces = new IResponseCallback() {
        @Override
        public void onResponse(Object object) {
            if (object instanceof String) {
                mUserCallback.onPlacesResponse((String) object);
            } else if (object instanceof GoogleDistanceMatrixApi) {
                mUserCallback.onDistanceMatrixResponse((GoogleDistanceMatrixApi) object);
            }
        }

        @Override
        public void onSuccess() {

        }

        @Override
        public void onError(int code, String error) {
            mUserCallback.onError(error);
        }

    };


}

package com.bykea.pk.partner.repositories.places;

import com.bykea.pk.partner.models.data.PlacesList;
import com.bykea.pk.partner.models.response.GoogleDistanceMatrixApi;

public class PlacesDataHandler implements IPlacesDataHandler {

    @Override
    public void onPlacesResponse(PlacesList response) {
    }

    @Override
    public void onPlacesResponse(String response) {
    }

    @Override
    public void onDistanceMatrixResponse(GoogleDistanceMatrixApi response) {

    }

    @Override
    public void onError(String error) {
    }
}


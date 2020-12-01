package com.bykea.pk.partner.repositories.places;

import com.bykea.pk.partner.models.PlaceAutoCompleteResponse;
import com.bykea.pk.partner.models.data.PlacesList;
import com.bykea.pk.partner.models.response.BykeaDistanceMatrixResponse;
import com.bykea.pk.partner.models.response.BykeaPlaceDetailsResponse;
import com.bykea.pk.partner.models.response.GeoCodeApiResponse;
import com.bykea.pk.partner.models.response.PlaceDetailsResponse;

public class PlacesDataHandler implements IPlacesDataHandler {

    @Override
    public void onPlacesResponse(PlacesList response) {
    }

    @Override
    public void onPlacesResponse(String response) {
    }

    @Override
    public void onDistanceMatrixResponse(BykeaDistanceMatrixResponse response) {

    }

    @Override
    public void onPlaceDetailsResponse(PlaceDetailsResponse response) {

    }

    @Override
    public void onBykeaPlaceDetailsResponse(BykeaPlaceDetailsResponse response) {

    }

    @Override
    public void onPlaceAutoCompleteResponse(PlaceAutoCompleteResponse response) {

    }

    @Override
    public void onGeoCodeApiResponse(GeoCodeApiResponse response) {

    }

    @Override
    public void onError(String error) {
    }
}


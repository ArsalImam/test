package com.bykea.pk.partner.repositories.places;


import com.bykea.pk.partner.models.data.PlacesList;
import com.bykea.pk.partner.models.response.GoogleDistanceMatrixApi;
import com.bykea.pk.partner.models.response.PlaceDetailsResponse;

public interface IPlacesDataHandler {
    void onPlacesResponse(PlacesList response);

    void onPlacesResponse(String response);

    void onDistanceMatrixResponse(GoogleDistanceMatrixApi response);

    void onPlaceDetailsResponse(PlaceDetailsResponse response);

    void onError(String error);
}

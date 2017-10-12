package com.bykea.pk.partner.repositories.places;


import com.bykea.pk.partner.models.data.PlacesList;
import com.bykea.pk.partner.models.response.GoogleDistanceMatrixApi;

public interface IPlacesDataHandler {
    void onPlacesResponse(PlacesList response);
    void onPlacesResponse(String response);
    void onDistanceMatrixResponse(GoogleDistanceMatrixApi response);
    void onError(String error);
}

package com.bykea.pk.partner.repositories.places;


import com.bykea.pk.partner.models.PlaceAutoCompleteResponse;
import com.bykea.pk.partner.models.data.PlacesList;
import com.bykea.pk.partner.models.response.BykeaDistanceMatrixResponse;
import com.bykea.pk.partner.models.response.BykeaPlaceDetailsResponse;
import com.bykea.pk.partner.models.response.GeoCodeApiResponse;
import com.bykea.pk.partner.models.response.PlaceDetailsResponse;

public interface IPlacesDataHandler {
    void onPlacesResponse(PlacesList response);
    void onPlacesResponse(String response);
    void onDistanceMatrixResponse(BykeaDistanceMatrixResponse response);
    void onPlaceDetailsResponse(PlaceDetailsResponse response);
    void onBykeaPlaceDetailsResponse(BykeaPlaceDetailsResponse response);
    void onPlaceAutoCompleteResponse(PlaceAutoCompleteResponse response);
    void onGeoCodeApiResponse(GeoCodeApiResponse response);
    void onError(String error);
}

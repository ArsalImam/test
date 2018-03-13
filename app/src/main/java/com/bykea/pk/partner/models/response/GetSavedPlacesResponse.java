package com.bykea.pk.partner.models.response;


import com.bykea.pk.partner.models.data.SavedPlaces;

import java.util.ArrayList;

public class GetSavedPlacesResponse extends CommonResponse {
    private ArrayList<SavedPlaces> data;

    public ArrayList<SavedPlaces> getData() {
        return data;
    }

    public void setData(ArrayList<SavedPlaces> data) {
        this.data = data;
    }
}

package com.bykea.pk.partner.models.response;

import com.bykea.pk.partner.models.data.Predictions;

import java.util.ArrayList;

public class PlaceAutoCompleteResponse {
    private ArrayList<Predictions> predictions;

    private String status;

    public ArrayList<Predictions> getPredictions() {
        return predictions;
    }

    public void setPredictions(ArrayList<Predictions> predictions) {
        this.predictions = predictions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
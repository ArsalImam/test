package com.bykea.pk.partner.models.data;

import java.util.ArrayList;

/**
 * Model class for Auto Complete Data
 */
public class PlacesAutoCompleteData {

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

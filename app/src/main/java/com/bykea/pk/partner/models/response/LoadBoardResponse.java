package com.bykea.pk.partner.models.response;

import com.bykea.pk.partner.models.data.DeliveryScheduleModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Model Class for Load Board API Response
 */
public class LoadBoardResponse extends CommonResponse {

    @SerializedName("data")
    private ArrayList<DeliveryScheduleModel> loadBoardBody;

    public ArrayList<DeliveryScheduleModel> getLoadBoardBody() {
        return loadBoardBody;
    }

    public void setLoadBoardBody(ArrayList<DeliveryScheduleModel> loadBoardBody) {
        this.loadBoardBody = loadBoardBody;
    }
}

package com.bykea.pk.partner.models.response;

import com.bykea.pk.partner.models.data.DeliveryScheduleModel;
import com.bykea.pk.partner.models.data.LoadBoardListingData;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Model class for loadboard jobs listing
 */
public class LoadBoardListingResponse extends CommonResponse {

    private ArrayList<LoadBoardListingData> data;

    public ArrayList<LoadBoardListingData> getData() {
        return data;
    }
}

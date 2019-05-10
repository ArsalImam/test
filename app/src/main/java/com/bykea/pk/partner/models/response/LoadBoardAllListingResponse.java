package com.bykea.pk.partner.models.response;

import com.bykea.pk.partner.models.data.LoadBoardAllListingData;

import java.util.ArrayList;

/**
 * Data Model class for loadboard jobs listing
 */
public class LoadBoardAllListingResponse extends CommonResponse {

    private ArrayList<LoadBoardAllListingData> data;

    public ArrayList<LoadBoardAllListingData> getData() {
        return data;
    }
}

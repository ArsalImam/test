package com.bykea.pk.partner.models.response;

import com.google.gson.annotations.SerializedName;
import com.bykea.pk.partner.models.data.TripHistoryData;

import java.util.ArrayList;


public class TripHistoryResponse extends CommonResponse {

    ArrayList<TripHistoryData> data;
    @SerializedName("next")
    int page;
    @SerializedName("total")
    int pages;

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public ArrayList<TripHistoryData> getData() {
        return data;
    }

    public void setData(ArrayList<TripHistoryData> data) {
        this.data = data;
    }
}

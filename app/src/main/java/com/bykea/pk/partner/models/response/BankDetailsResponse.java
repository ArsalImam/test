package com.bykea.pk.partner.models.response;

import com.bykea.pk.partner.models.data.BankData;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class BankDetailsResponse extends CommonResponse {

    private ArrayList<BankData.BankAgentData> data;

    @SerializedName("next")
    String page;
    @SerializedName("total")
    int pages;

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public ArrayList<BankData.BankAgentData> getData() {
        return data;
    }

    public void setData(ArrayList<BankData.BankAgentData> data) {
        this.data = data;
    }
}

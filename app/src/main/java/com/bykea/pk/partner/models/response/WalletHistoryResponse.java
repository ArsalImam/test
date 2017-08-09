package com.bykea.pk.partner.models.response;

import com.google.gson.annotations.SerializedName;
import com.bykea.pk.partner.models.data.WalletData;

import java.util.ArrayList;

public class WalletHistoryResponse extends CommonResponse {


    String total_amount;
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


     ArrayList<WalletData> data;

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public ArrayList<WalletData> getData() {
        return data;
    }

    public void setData(ArrayList<WalletData> data) {
        this.data = data;
    }
}
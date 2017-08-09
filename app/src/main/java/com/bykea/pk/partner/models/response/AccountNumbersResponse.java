package com.bykea.pk.partner.models.response;

import com.bykea.pk.partner.models.data.AccountsData;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class AccountNumbersResponse extends CommonResponse {


    ArrayList<AccountsData> data;

    public ArrayList<AccountsData> getData() {
        return data;
    }

    public void setData(ArrayList<AccountsData> data) {
        this.data = data;
    }


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
}

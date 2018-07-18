package com.bykea.pk.partner.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoadBoardResponse extends CommonResponse {

    @SerializedName("data")
    private List<LoadBoardBody> loadBoardBody;

    public List<LoadBoardBody> getLoadBoardBody() {
        return loadBoardBody;
    }

    public void setLoadBoardBody(List<LoadBoardBody> loadBoardBody) {
        this.loadBoardBody = loadBoardBody;
    }
}

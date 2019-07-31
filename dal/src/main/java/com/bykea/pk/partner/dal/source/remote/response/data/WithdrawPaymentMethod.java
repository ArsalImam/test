package com.bykea.pk.partner.dal.source.remote.response.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WithdrawPaymentMethod {
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("fees")
    @Expose
    private Double fees;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("comments")
    @Expose
    private String comments;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getFees() {
        return fees;
    }

    public void setFees(Double fees) {
        this.fees = fees;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
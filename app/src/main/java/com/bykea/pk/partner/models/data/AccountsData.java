package com.bykea.pk.partner.models.data;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringUtils;

public class AccountsData {

    @SerializedName("account_title")
    String accountTitle;
    @SerializedName("account_number")
    String accountNumber;
    @SerializedName("bank_name")
    String bankName;
    private String link;

    public String getAccountTitle() {
        return accountTitle;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public String getLink() {
        return StringUtils.isNotBlank(link) ? link : "https://www.bykea.com";
    }
}
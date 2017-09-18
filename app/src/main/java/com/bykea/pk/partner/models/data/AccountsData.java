package com.bykea.pk.partner.models.data;

import com.google.gson.annotations.SerializedName;

public class AccountsData {

    @SerializedName("account_title")
    String accountTitle;
    @SerializedName("account_number")
    String accountNumber;
    @SerializedName("bank_name")
    String bankName;

    public String getAccountTitle() {
        return accountTitle;
    }

     public String getAccountNumber() {
        return accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

}
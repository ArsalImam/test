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

    public void setAccountTitle(String accountTitle) {
        this.accountTitle = accountTitle;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
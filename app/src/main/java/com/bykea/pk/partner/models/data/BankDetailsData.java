package com.bykea.pk.partner.models.data;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class BankDetailsData {

    @SerializedName("account_title")
    String accountTitle;
    @SerializedName("account_number")
    String accountNumber;
    @SerializedName("bank_name")
    String bankName;
    private String link;

    private ArrayList<BankAgentData> agentsData;

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

    public ArrayList<BankAgentData> getAgentsData() {
        return agentsData;
    }

    public void setAgentsData(ArrayList<BankAgentData> agentsData) {
        this.agentsData = agentsData;
    }

    public class BankAgentData{

    }
}
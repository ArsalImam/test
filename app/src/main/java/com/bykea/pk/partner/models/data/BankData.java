package com.bykea.pk.partner.models.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BankData implements Parcelable {
    private String _id;
    private String img;

    @SerializedName("account_title")
    String accountTitle;
    @SerializedName("account_number")
    String accountNumber;
    @SerializedName("bank_name")
    String bankName;

    private ArrayList<BankAgentData> agentsData;


    protected BankData(Parcel in) {
        _id = in.readString();
        img = in.readString();
        accountTitle = in.readString();
        accountNumber = in.readString();
        bankName = in.readString();
    }

    public static final Creator<BankData> CREATOR = new Creator<BankData>() {
        @Override
        public BankData createFromParcel(Parcel in) {
            return new BankData(in);
        }

        @Override
        public BankData[] newArray(int size) {
            return new BankData[size];
        }
    };

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }


    public String getAccountTitle() {
        return accountTitle;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public ArrayList<BankAgentData> getAgentsData() {
        return agentsData;
    }

    public void setAgentsData(ArrayList<BankAgentData> agentsData) {
        this.agentsData = agentsData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(_id);
        parcel.writeString(img);
        parcel.writeString(accountTitle);
        parcel.writeString(accountNumber);
        parcel.writeString(bankName);
    }

    public class BankAgentData {
        @SerializedName("ph")
        private String phone;

        @SerializedName("name")
        private String agentName;

        private String address;

        @SerializedName("agentName")
        private String contactPerson;

        private ArrayList<Double> loc;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getAgentName() {
            return agentName;
        }

        public void setAgentName(String agentName) {
            this.agentName = agentName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getContactPerson() {
            return contactPerson;
        }

        public void setContactPerson(String contactPerson) {
            this.contactPerson = contactPerson;
        }

        public ArrayList<Double> getLoc() {
            return loc;
        }

        public void setLoc(ArrayList<Double> loc) {
            this.loc = loc;
        }
    }
}

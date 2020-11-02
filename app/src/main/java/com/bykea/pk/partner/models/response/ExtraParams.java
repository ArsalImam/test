package com.bykea.pk.partner.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ExtraParams implements Parcelable {
    private String telco_name;
    private String vendor_name;
    private String bill_company_name;

    private String account_number;
    private String cnic;
    private String iban;
    private String phone;

    @SerializedName("is_paid")
    private boolean isPaid;

    public ExtraParams() {}

    protected ExtraParams(Parcel in) {
        telco_name = in.readString();
        vendor_name = in.readString();
        bill_company_name = in.readString();
        account_number = in.readString();
        cnic = in.readString();
        iban = in.readString();
        phone = in.readString();
        isPaid = in.readByte() != 0;
    }

    public static final Creator<ExtraParams> CREATOR = new Creator<ExtraParams>() {
        @Override
        public ExtraParams createFromParcel(Parcel in) {
            return new ExtraParams(in);
        }

        @Override
        public ExtraParams[] newArray(int size) {
            return new ExtraParams[size];
        }
    };

    public String getTelco_name() {
        return telco_name;
    }

    public void setTelco_name(String telco_name) {
        this.telco_name = telco_name;
    }

    public String getVendor_name() {
        return vendor_name;
    }

    public void setVendor_name(String vendor_name) {
        this.vendor_name = vendor_name;
    }

    public String getBill_company_name() {
        return bill_company_name;
    }

    public void setBill_company_name(String bill_company_name) {
        this.bill_company_name = bill_company_name;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(telco_name);
        dest.writeString(vendor_name);
        dest.writeString(bill_company_name);
        dest.writeString(account_number);
        dest.writeString(cnic);
        dest.writeString(iban);
        dest.writeString(phone);
        dest.writeByte((byte) (isPaid ? 1 : 0));
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }
}

package com.bykea.pk.partner.models.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class PersonalInfoData implements Parcelable {

    @SerializedName("phone")
    private String phone;

    @SerializedName("mobile_2")
    private String secondaryMobileNumber;

    @SerializedName("mobile_1")
    private String primaryMobileNumber;

    @SerializedName("brand")
    private String brand;

    @SerializedName("horse_power")
    private String horsePower;

    @SerializedName("model_number")
    private String modelNumber;

    @SerializedName("chassis_number")
    private String chassisNumber;

    @SerializedName("engine_number")
    private String engineNumber;

    @SerializedName("account_number")
    private String accountNumber;

    @SerializedName("account_title")
    private String accountTitle;

    @SerializedName("finance")
    private String finance;

    @SerializedName("plate_no")
    private String plateNo;

    @SerializedName("driver_license_number")
    private String driverLicenseNumber;

    @SerializedName("city")
    private String city;

    @SerializedName("registration_date")
    private String registrationDate;

    @SerializedName("excise_verified")
    private String exciseVerified;

    @SerializedName("license_expire")
    private String licenseExpire;

    @SerializedName("license_city")
    private String licenseCity;

    @SerializedName("img_id")
    private String imgId;

    @SerializedName("email")
    private String email;

    @SerializedName("address")
    private String address;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("cnic")
    private String cnic;

    //TODO Update Home Lat/Lng keys when available in API
    @SerializedName("current_lat")
    private String homeLat;
    @SerializedName("current_lng")
    private String homeLng;


    public PersonalInfoData() {
    }

    //region Parcelable helper methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.phone);
        dest.writeString(this.secondaryMobileNumber);
        dest.writeString(this.primaryMobileNumber);
        dest.writeString(this.brand);
        dest.writeString(this.horsePower);
        dest.writeString(this.modelNumber);
        dest.writeString(this.chassisNumber);
        dest.writeString(this.engineNumber);
        dest.writeString(this.accountNumber);
        dest.writeString(this.accountTitle);
        dest.writeString(this.finance);
        dest.writeString(this.plateNo);
        dest.writeString(this.driverLicenseNumber);
        dest.writeString(this.city);
        dest.writeString(this.registrationDate);
        dest.writeString(this.exciseVerified);
        dest.writeString(this.licenseExpire);
        dest.writeString(this.licenseCity);
        dest.writeString(this.imgId);
        dest.writeString(this.email);
        dest.writeString(this.address);
        dest.writeString(this.fullName);
        dest.writeString(this.cnic);
        dest.writeString(this.homeLat);
        dest.writeString(this.homeLng);
    }


    protected PersonalInfoData(Parcel in) {
        this.phone = in.readString();
        this.secondaryMobileNumber = in.readString();
        this.primaryMobileNumber = in.readString();
        this.brand = in.readString();
        this.horsePower = in.readString();
        this.modelNumber = in.readString();
        this.chassisNumber = in.readString();
        this.engineNumber = in.readString();
        this.accountNumber = in.readString();
        this.accountTitle = in.readString();
        this.finance = in.readString();
        this.plateNo = in.readString();
        this.driverLicenseNumber = in.readString();
        this.city = in.readString();
        this.registrationDate = in.readString();
        this.exciseVerified = in.readString();
        this.licenseExpire = in.readString();
        this.licenseCity = in.readString();
        this.imgId = in.readString();
        this.email = in.readString();
        this.address = in.readString();
        this.fullName = in.readString();
        this.cnic = in.readString();
        this.homeLat = in.readString();
        this.homeLng = in.readString();
    }

    public static final Creator<PersonalInfoData> CREATOR = new Creator<PersonalInfoData>() {
        @Override
        public PersonalInfoData createFromParcel(Parcel source) {
            return new PersonalInfoData(source);
        }

        @Override
        public PersonalInfoData[] newArray(int size) {
            return new PersonalInfoData[size];
        }
    };

    //endregion

    //region Getter Setter

    public String getPhone() {
        return phone;
    }


    public String getPlateNo() {
        return plateNo;
    }


    public String getDriverLicenseNumber() {
        return driverLicenseNumber;
    }


    public String getCity() {
        return city;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public String getExciseVerified() {
        return exciseVerified;
    }

    public String getLicenseExpire() {
        return licenseExpire;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFullName() {
        return fullName;
    }

    public String getCnic() {
        return cnic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImgId() {
        return imgId;
    }

    public String getSecondaryMobileNumber() {
        return secondaryMobileNumber;
    }

    public String getPrimaryMobileNumber() {
        return primaryMobileNumber;
    }

    public String getBrand() {
        return brand;
    }

    public String getHorsePower() {
        return horsePower;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public String getChassisNumber() {
        return chassisNumber;
    }

    public String getEngineNumber() {
        return engineNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountTitle() {
        return accountTitle;
    }

    public String getFinance() {
        return finance;
    }

    public String getLicenseCity() {
        return licenseCity;
    }

    public String getHomeLat() {
        return homeLat;
    }

    public String getHomeLng() {
        return homeLng;
    }
    //endregion
}

		
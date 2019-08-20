package com.bykea.pk.partner.dal.source.remote.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class PersonalInfoData(
        @SerializedName("phone") val phone: String?,
        @SerializedName("mobile_2") val secondaryMobileNumber: String,
        @SerializedName("mobile_1") val primaryMobileNumber: String,
        @SerializedName("brand") val brand: String,
        @SerializedName("horse_power") val horsePower: String,
        @SerializedName("model_number") val modelNumber: String,
        @SerializedName("chassis_number") val chassisNumber: String,
        @SerializedName("engine_number") val engineNumber: String,
        @SerializedName("account_number") val accountNumber: String,
        @SerializedName("account_title") val accountTitle: String,
        @SerializedName("finance") val finance: String,
        @SerializedName("plate_no") val plateNo: String,
        @SerializedName("driver_license_number") val driverLicenseNumber: String,
        @SerializedName("city") val city: String,
        @SerializedName("registration_date") val registrationDate: String,
        @SerializedName("excise_verified") val exciseVerified: String,
        @SerializedName("license_expire") val licenseExpire: String,
        @SerializedName("license_city") val licenseCity: String,
        @SerializedName("wallet") var wallet: Double = 0.toDouble(),
        @SerializedName("img_id") val imgId: String,
        @SerializedName("email") var email: String,
        @SerializedName("address") var address: String,
        @SerializedName("full_name") val fullName: String,
        @SerializedName("cnic") val cnic: String,
        @SerializedName("current_lat") val homeLat: String,
        @SerializedName("current_lng") val homeLng: String,
        @SerializedName("app_version") val appVersion: String
) : Parcelable {

    //endregion
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readDouble(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(phone)
        parcel.writeString(secondaryMobileNumber)
        parcel.writeString(primaryMobileNumber)
        parcel.writeString(brand)
        parcel.writeString(horsePower)
        parcel.writeString(modelNumber)
        parcel.writeString(chassisNumber)
        parcel.writeString(engineNumber)
        parcel.writeString(accountNumber)
        parcel.writeString(accountTitle)
        parcel.writeString(finance)
        parcel.writeString(plateNo)
        parcel.writeString(driverLicenseNumber)
        parcel.writeString(city)
        parcel.writeString(registrationDate)
        parcel.writeString(exciseVerified)
        parcel.writeString(licenseExpire)
        parcel.writeString(licenseCity)
        parcel.writeDouble(wallet)
        parcel.writeString(imgId)
        parcel.writeString(email)
        parcel.writeString(address)
        parcel.writeString(fullName)
        parcel.writeString(cnic)
        parcel.writeString(homeLat)
        parcel.writeString(homeLng)
        parcel.writeString(appVersion)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PersonalInfoData> {
        override fun createFromParcel(parcel: Parcel): PersonalInfoData {
            return PersonalInfoData(parcel)
        }

        override fun newArray(size: Int): Array<PersonalInfoData?> {
            return arrayOfNulls(size)
        }
    }
}
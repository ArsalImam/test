package com.bykea.pk.partner.dal.source.remote.data

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.SerializedName

class PersonalInfoData : Parcelable {

    //endregion

    //region Getter Setter

    @SerializedName("phone")
    val phone: String?

    @SerializedName("mobile_2")
    val secondaryMobileNumber: String?

    @SerializedName("mobile_1")
    val primaryMobileNumber: String?

    @SerializedName("brand")
    val brand: String?

    @SerializedName("horse_power")
    val horsePower: String?

    @SerializedName("model_number")
    val modelNumber: String?

    @SerializedName("chassis_number")
    val chassisNumber: String?

    @SerializedName("engine_number")
    val engineNumber: String?

    @SerializedName("account_number")
    val accountNumber: String?

    @SerializedName("account_title")
    val accountTitle: String?

    @SerializedName("finance")
    val finance: String?

    @SerializedName("plate_no")
    val plateNo: String?

    @SerializedName("driver_license_number")
    val driverLicenseNumber: String?

    @SerializedName("city")
    val city: String?

    @SerializedName("registration_date")
    val registrationDate: String?

    @SerializedName("excise_verified")
    val exciseVerified: String?

    @SerializedName("license_expire")
    val licenseExpire: String?

    @SerializedName("license_city")
    val licenseCity: String?

    @SerializedName("wallet")
    var wallet: Double = 0.toDouble()

    @SerializedName("img_id")
    val imgId: String?

    @SerializedName("email")
    var email: String? = null

    @SerializedName("address")
    var address: String? = null

    @SerializedName("full_name")
    val fullName: String?

    @SerializedName("cnic")
    val cnic: String?

    //TODO Update Home Lat/Lng keys when available in API
    @SerializedName("current_lat")
    val homeLat: String?
    @SerializedName("current_lng")
    val homeLng: String?

    @SerializedName("app_version")
    val appVersion: String?


    constructor() {}

    //region Parcelable helper methods
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.phone)
        dest.writeString(this.secondaryMobileNumber)
        dest.writeString(this.primaryMobileNumber)
        dest.writeString(this.brand)
        dest.writeString(this.horsePower)
        dest.writeString(this.modelNumber)
        dest.writeString(this.chassisNumber)
        dest.writeString(this.engineNumber)
        dest.writeString(this.accountNumber)
        dest.writeString(this.accountTitle)
        dest.writeString(this.finance)
        dest.writeString(this.plateNo)
        dest.writeString(this.driverLicenseNumber)
        dest.writeString(this.city)
        dest.writeString(this.registrationDate)
        dest.writeString(this.exciseVerified)
        dest.writeString(this.licenseExpire)
        dest.writeString(this.licenseCity)
        dest.writeString(this.imgId)
        dest.writeString(this.email)
        dest.writeString(this.address)
        dest.writeString(this.fullName)
        dest.writeString(this.cnic)
        dest.writeString(this.homeLat)
        dest.writeString(this.homeLng)
        dest.writeString(this.appVersion)
    }


    protected constructor(`in`: Parcel) {
        this.phone = `in`.readString()
        this.secondaryMobileNumber = `in`.readString()
        this.primaryMobileNumber = `in`.readString()
        this.brand = `in`.readString()
        this.horsePower = `in`.readString()
        this.modelNumber = `in`.readString()
        this.chassisNumber = `in`.readString()
        this.engineNumber = `in`.readString()
        this.accountNumber = `in`.readString()
        this.accountTitle = `in`.readString()
        this.finance = `in`.readString()
        this.plateNo = `in`.readString()
        this.driverLicenseNumber = `in`.readString()
        this.city = `in`.readString()
        this.registrationDate = `in`.readString()
        this.exciseVerified = `in`.readString()
        this.licenseExpire = `in`.readString()
        this.licenseCity = `in`.readString()
        this.imgId = `in`.readString()
        this.email = `in`.readString()
        this.address = `in`.readString()
        this.fullName = `in`.readString()
        this.cnic = `in`.readString()
        this.homeLat = `in`.readString()
        this.homeLng = `in`.readString()
        this.appVersion = `in`.readString()
    }

    companion object {

        val CREATOR: Parcelable.Creator<PersonalInfoData> = object : Parcelable.Creator<PersonalInfoData> {
            override fun createFromParcel(source: Parcel): PersonalInfoData {
                return PersonalInfoData(source)
            }

            override fun newArray(size: Int): Array<PersonalInfoData> {
                return arrayOfNulls(size)
            }
        }
    }

    //endregion
}

		
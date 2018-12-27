package com.bykea.pk.partner.models.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.StringUtils

/**
 * Multi Delivery Invoice Data
 */
class MultiDeliveryInvoiceData() : Parcelable {
    @SerializedName("trip_charges")
    var tripCharges: Int? = 0

    @SerializedName("received_amount")
    var receivedAmount: Int? = 0

    @SerializedName("remaining_amount")
    var remainingAmount: Int? = 0

    @SerializedName("wallet_deduction")
    var walletDeduction: Int? = 0

    @SerializedName("promo_deduction")
    var promoDeduction: Int? = 0

    @SerializedName("driver_payout")
    var driverPayout: Int? = 0

    @SerializedName("is_deleted")
    var isDeleted: Boolean? = false

    @SerializedName("_id")
    var _id: String? = StringUtils.EMPTY

    @SerializedName("admin_fee")
    var adminFee: Int? = 0

    @SerializedName("trip_id")
    var tripID: String? = StringUtils.EMPTY

    @SerializedName("passenger_id")
    var passengerID: String? = StringUtils.EMPTY

    @SerializedName("driver_id")
    var driverID: String? = StringUtils.EMPTY

    @SerializedName("trip_no")
    var tripNo: String? = StringUtils.EMPTY

    @SerializedName("wc_value")
    var walletCreditValue: Int? = 0

    @SerializedName("wait_mins")
    var waitMins: Int? = 0

    @SerializedName("minutes")
    var minutes: Float? = 0f

    @SerializedName("total")
    var total: Long? = 0

    @SerializedName("start_balance")
    var startBalance: Int? = 0

    @SerializedName("created_at")
    var createdAt: String? = StringUtils.EMPTY

    @SerializedName("__v")
    var mongoDbVersion: Int? = 0

    /**
     * Constructor
     *
     * @param source The Parcel to read the object's data from.
     */
    constructor(parcel: Parcel) : this() {
        tripCharges = parcel.readValue(Int::class.java.classLoader) as? Int
        receivedAmount = parcel.readValue(Int::class.java.classLoader) as? Int
        remainingAmount = parcel.readValue(Int::class.java.classLoader) as? Int
        walletDeduction = parcel.readValue(Int::class.java.classLoader) as? Int
        promoDeduction = parcel.readValue(Int::class.java.classLoader) as? Int
        driverPayout = parcel.readValue(Int::class.java.classLoader) as? Int
        isDeleted = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        _id = parcel.readString()
        adminFee = parcel.readValue(Int::class.java.classLoader) as? Int
        tripID = parcel.readString()
        passengerID = parcel.readString()
        driverID = parcel.readString()
        tripNo = parcel.readString()
        walletCreditValue = parcel.readValue(Int::class.java.classLoader) as? Int
        waitMins = parcel.readValue(Int::class.java.classLoader) as? Int
        minutes = parcel.readValue(Float::class.java.classLoader) as? Float
        total = parcel.readValue(Int::class.java.classLoader) as? Long
        startBalance = parcel.readValue(Int::class.java.classLoader) as? Int
        createdAt = parcel.readString()
        mongoDbVersion = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest The Parcel in which the object should be written.
     */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(tripCharges)
        parcel.writeValue(receivedAmount)
        parcel.writeValue(remainingAmount)
        parcel.writeValue(walletDeduction)
        parcel.writeValue(promoDeduction)
        parcel.writeValue(driverPayout)
        parcel.writeValue(isDeleted)
        parcel.writeString(_id)
        parcel.writeValue(adminFee)
        parcel.writeString(tripID)
        parcel.writeString(passengerID)
        parcel.writeString(driverID)
        parcel.writeString(tripNo)
        parcel.writeValue(walletCreditValue)
        parcel.writeValue(waitMins)
        parcel.writeValue(minutes)
        parcel.writeValue(total)
        parcel.writeValue(startBalance)
        parcel.writeString(createdAt)
        parcel.writeValue(mongoDbVersion)
    }

    override fun describeContents(): Int {
        return 0
    }

    /**
     * An object declaration inside a class can be marked with the companion keyword. Members of
     * the companion object can be called by using simply the class name as the qualifier
     *
     * CREATOR: field that generates instances of your Parcelable class from a Parcel
     */
    companion object CREATOR : Parcelable.Creator<MultiDeliveryInvoiceData> {
        override fun createFromParcel(parcel: Parcel): MultiDeliveryInvoiceData {
            return MultiDeliveryInvoiceData(parcel)
        }

        override fun newArray(size: Int): Array<MultiDeliveryInvoiceData?> {
            return arrayOfNulls(size)
        }
    }


}
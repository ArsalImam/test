package com.bykea.pk.partner.models.response;

import com.google.gson.annotations.SerializedName;

public class EndRideResponse extends CommonResponse {

    EndRideResponse data;


    @SerializedName("total")
    private String totalFare;
    @SerializedName("trip_no")
    private String tripNo;
    @SerializedName("saddress")
    private String startAddress;
    @SerializedName("eaddress")
    private String endAddress;

    @SerializedName("km")
    private String distanceCovered;
    @SerializedName("minutes")
    private String totalMins;
    @SerializedName("total_amount")
    private String totalAmount;
    private String wallet_deduction;
    private String promo_deduction;
    private String trip_charges;
    private String dropoff_discount;

    public String getDistanceCovered() {
        return distanceCovered;
    }

    public String getTotalMins() {
        return totalMins;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public EndRideResponse getData() {
        return data;
    }

    public void setData(EndRideResponse data) {
        this.data = data;
    }

    public String getTotalFare() {
        return totalFare;
    }

    public String getTripNo() {
        return tripNo;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public String getWallet_deduction() {
        return wallet_deduction;
    }

    public String getPromo_deduction() {
        return promo_deduction;
    }

    public String getTrip_charges() {
        return trip_charges;
    }

    public String getDropoff_discount() {
        return dropoff_discount;
    }

    public void setDropoff_discount(String dropoff_discount) {
        this.dropoff_discount = dropoff_discount;
    }
}

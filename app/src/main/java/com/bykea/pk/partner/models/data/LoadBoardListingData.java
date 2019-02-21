package com.bykea.pk.partner.models.data;

import com.google.gson.annotations.SerializedName;

/**
 * Data Model class for loadboard individuals jobs
 */
public class LoadBoardListingData {

    @SerializedName("_id")
    private String id;
    @SerializedName("trip_type")
    private String tripType;
    @SerializedName("order_no")
    private String orderNo;
    @SerializedName("pickup_zone")
    private LoadBoardListingZoneData pickupZone;
    @SerializedName("dropoff_zone")
    private LoadBoardListingZoneData dropoffZone;

    public String getId() {
        return id;
    }

    public String getTripType() {
        return tripType;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public LoadBoardListingZoneData getPickupZone() {
        return pickupZone;
    }

    public LoadBoardListingZoneData getDropoffZone() {
        return dropoffZone;
    }
}

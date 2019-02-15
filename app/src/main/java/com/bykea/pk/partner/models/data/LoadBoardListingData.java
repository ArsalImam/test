package com.bykea.pk.partner.models.data;

import com.google.gson.annotations.SerializedName;

public class LoadBoardListingData {

    @SerializedName("_id")
    private String id;
    @SerializedName("trip_type")
    private String tripType;
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

    public LoadBoardListingZoneData getPickupZone() {
        return pickupZone;
    }

    public LoadBoardListingZoneData getDropoffZone() {
        return dropoffZone;
    }
}

package com.bykea.pk.partner.models.data;

public class LoadBoardListingData {

    private String _id;
    private String trip_type;
    private LoadBoardListingZoneData pickup_zone;
    private LoadBoardListingZoneData dropoff_zone;

    public String get_id() {
        return _id;
    }

    public String getTrip_type() {
        return trip_type;
    }

    public LoadBoardListingZoneData getPickup_zone() {
        return pickup_zone;
    }

    public LoadBoardListingZoneData getDropoff_zone() {
        return dropoff_zone;
    }
}

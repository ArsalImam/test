package com.bykea.pk.partner.models.data;

import java.util.List;

/***
 * Multi Delivery Call Data Model Class.
 */
public class MultiDeliveryCallData {

    MultiDeliveryPickup pickupData;
    List<MultiDeliveryDropOff> dropOffList;

    /*Added this ride status to handle making calls to drop off location's person
     * Call option can only be enabled when ride has started or arrived*/
    private String rideStatus;

    public MultiDeliveryCallData(MultiDeliveryPickup pickupData,
                                 List<MultiDeliveryDropOff> dropOffList, String rideStatus) {
        this.pickupData = pickupData;
        this.dropOffList = dropOffList;
        this.rideStatus = rideStatus;
    }

    public MultiDeliveryPickup getPickupData() {
        return pickupData;
    }

    public void setPickupData(MultiDeliveryPickup pickupData) {
        this.pickupData = pickupData;
    }

    public List<MultiDeliveryDropOff> getDropOffList() {
        return dropOffList;
    }

    public void setDropOffList(List<MultiDeliveryDropOff> dropOffList) {
        this.dropOffList = dropOffList;
    }

    public String getRideStatus() {
        return rideStatus;
    }
}

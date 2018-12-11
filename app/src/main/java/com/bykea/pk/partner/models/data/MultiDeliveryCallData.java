package com.bykea.pk.partner.models.data;

import java.util.List;

/***
 * Multi Delivery Call Data Model Class.
 */
public class MultiDeliveryCallData {

    MultiDeliveryPickup pickupData;
    List<MultiDeliveryDropOff> dropOffList;

    public MultiDeliveryCallData(MultiDeliveryPickup pickupData,
                                 List<MultiDeliveryDropOff> dropOffList) {
        this.pickupData = pickupData;
        this.dropOffList = dropOffList;
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
}

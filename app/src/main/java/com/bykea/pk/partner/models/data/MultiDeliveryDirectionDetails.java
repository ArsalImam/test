package com.bykea.pk.partner.models.data;

import java.util.List;

/***
 * MultiDelivery Direction Details Model Class
 */

public class MultiDeliveryDirectionDetails {
    MultiDeliveryPickup pickupData;
    List<DirectionDropOffData> dropOffData;

    public MultiDeliveryDirectionDetails(MultiDeliveryPickup pickupData,
                                         List<DirectionDropOffData> dropOffData) {
        this.pickupData = pickupData;
        this.dropOffData = dropOffData;
    }

    public MultiDeliveryPickup getPickupData() {
        return pickupData;
    }

    public void setPickupData(MultiDeliveryPickup pickupData) {
        this.pickupData = pickupData;
    }

    public List<DirectionDropOffData> getDropOffList() {
        return dropOffData;
    }

    public void setDropOffList(List<DirectionDropOffData> dropOffData) {
        this.dropOffData = dropOffData;
    }
}

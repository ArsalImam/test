package com.bykea.pk.partner.models.data.loadboard;

import com.bykea.pk.partner.models.data.LoadBoardListingZoneData;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LoadboardBookingDetailData {

    @SerializedName("_id")
    private String id;

    @SerializedName("pickup_zone")
    private LoadBoardListingZoneData pickupZone;

    @SerializedName("pickup_loc")
    private LoadboardBookingLocationData pickupLoc;

    @SerializedName("pickup_phone")
    private String pickupPhone;

    @SerializedName("pickup_name")
    private String pickupName;

    @SerializedName("pickup_address")
    private String pickupAddress;

    @SerializedName("pickup_eta")
    private int pickupEta;

    @SerializedName("pickup_distance")
    private int pickupDistance;

    @SerializedName("dropoff_zone")
    private LoadBoardListingZoneData dropoffZone;

    @SerializedName("end_loc")
    private LoadboardBookingLocationData endLoc;

    @SerializedName("dropoff_address")
    private String dropoffAddress;

    @SerializedName("dropoff_eta")
    private int dropoffEta;

    @SerializedName("dropoff_distance")
    private int dropoffDistance;

    @SerializedName("receiver_name")
    private String receiverName;

    @SerializedName("receiver_phone")
    private String receiverPhone;

    @SerializedName("complete_address")
    private String completeAddress;

    @SerializedName("fare_estimation")
    private int fareEstimation;

    @SerializedName("passenger_id")
    private String passengerId;

    @SerializedName("passenger_fullName")
    private String passengerFullName;

    @SerializedName("passenger_number")
    private String passengerNumber;

    @SerializedName("amount")
    private int amount;

    @SerializedName("purchase_amount")
    private int purchaseAmount;

    @SerializedName("cart_amount")
    private int cartAmount;

    @SerializedName("trip_type")
    private String tripType;

    @SerializedName("order_no")
    private String orderNo;

    @SerializedName("city")
    private String city;

    @SerializedName("return_run")
    private boolean returnRun;

    @SerializedName("orders")
    private ArrayList<LoadboardBookingOrderData> orders = null;

    @SerializedName("created_by")
    private String createdBy;

    @SerializedName("delivery_timings")
    private String deliveryTimings;

//    @SerializedName("status_history")
//    private List<Object> statusHistory = null;
    @SerializedName("status")
    private String status;

    @SerializedName("ddt")
    private String ddt;

    @SerializedName("dt")
    private String dt;

    @SerializedName("dtu")
    private String dtu;

    public String getId() {
        return id;
    }

    public LoadBoardListingZoneData getPickupZone() {
        return pickupZone;
    }

    public LoadboardBookingLocationData getPickupLoc() {
        return pickupLoc;
    }

    public String getPickupPhone() {
        return pickupPhone;
    }

    public String getPickupName() {
        return pickupName;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public int getPickupEta() {
        return pickupEta;
    }

    public int getPickupDistance() {
        return pickupDistance;
    }

    public LoadBoardListingZoneData getDropoffZone() {
        return dropoffZone;
    }

    public LoadboardBookingLocationData getEndLoc() {
        return endLoc;
    }

    public String getDropoffAddress() {
        return dropoffAddress;
    }

    public int getDropoffEta() {
        return dropoffEta;
    }

    public int getDropoffDistance() {
        return dropoffDistance;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public String getCompleteAddress() {
        return completeAddress;
    }

    public int getFareEstimation() {
        return fareEstimation;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public String getPassengerFullName() {
        return passengerFullName;
    }

    public String getPassengerNumber() {
        return passengerNumber;
    }

    public int getAmount() {
        return amount;
    }

    public int getPurchaseAmount() {
        return purchaseAmount;
    }

    public int getCartAmount() {
        return cartAmount;
    }

    public String getTripType() {
        return tripType;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getCity() {
        return city;
    }

    public boolean isReturnRun() {
        return returnRun;
    }

    public ArrayList<LoadboardBookingOrderData> getOrders() {
        return orders;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getDeliveryTimings() {
        return deliveryTimings;
    }

    public String getStatus() {
        return status;
    }

    public String getDdt() {
        return ddt;
    }

    public String getDt() {
        return dt;
    }

    public String getDtu() {
        return dtu;
    }
}

package com.bykea.pk.partner.models.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ResumeTripData implements Serializable {

    @SerializedName("phone_no")
    public String phoneNo;
    @SerializedName("start_lat")
    public String startLat;
    @SerializedName("start_lng")
    public String startLng;
    @SerializedName("end_lat")
    public String endLat;
    @SerializedName("end_lng")
    public String endLng;
    @SerializedName("start_address")
    public String startAddress;
    @SerializedName("end_address")
    public String endAddress;
    @SerializedName("vehicle_type")
    public String vehicleType;
    @SerializedName("first_name")
    public String firstName;
    @SerializedName("last_name")
    public String lastName;
    @SerializedName("pass_img")
    public String passImage;
    @SerializedName("est_time")
    public String estimateTime;
    @SerializedName("passenger_id")
    public String passId;
    @SerializedName("status")
    public String tripStatus;
    @SerializedName("trip_id")
    public String tripId;
    @SerializedName("trip_no")
    private String tripNo;
    @SerializedName("full_name")
    public String username;
    @SerializedName("rating")
    public String rating;
    @SerializedName("total")
    private String totalFare;
    @SerializedName("est_distance")
    public String distance;

    @SerializedName("km")
    private String distanceCovered;
    @SerializedName("minutes")
    private String totalMins;
    @SerializedName("total_amount")
    private String totalAmount;

    private boolean draw;

    public boolean isDraw() {
        return draw;
    }

    public void setDraw(boolean draw) {
        this.draw = draw;
    }

    /*private ArrayList<RouteData> route;

    public ArrayList<RouteData> getRoute() {
        return route;
    }

    public void setRoute(ArrayList<RouteData> route) {
        this.route = route;
    }*/

    public String getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(String totalFare) {
        this.totalFare = totalFare;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getStartLat() {
        return startLat;
    }

    public void setStartLat(String startLat) {
        this.startLat = startLat;
    }

    public String getStartLng() {
        return startLng;
    }

    public void setStartLng(String startLng) {
        this.startLng = startLng;
    }

    public String getEndLat() {
        return endLat;
    }

    public void setEndLat(String endLat) {
        this.endLat = endLat;
    }

    public String getEndLng() {
        return endLng;
    }

    public void setEndLng(String endLng) {
        this.endLng = endLng;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassImage() {
        return passImage;
    }

    public void setPassImage(String passImage) {
        this.passImage = passImage;
    }

    public String getEstimateTime() {
        return estimateTime;
    }

    public void setEstimateTime(String estimateTime) {
        this.estimateTime = estimateTime;
    }

    public String getPassId() {
        return passId;
    }

    public void setPassId(String passId) {
        this.passId = passId;
    }

    public String getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(String tripStatus) {
        this.tripStatus = tripStatus;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTripNo() {
        return tripNo;
    }

    public void setTripNo(String tripNo) {
        this.tripNo = tripNo;
    }

    public String getDistanceCovered() {
        return distanceCovered;
    }

    public void setDistanceCovered(String distanceCovered) {
        this.distanceCovered = distanceCovered;
    }

    public String getTotalMins() {
        return totalMins;
    }

    public void setTotalMins(String totalMins) {
        this.totalMins = totalMins;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
}

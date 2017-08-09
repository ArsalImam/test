package com.bykea.pk.partner.models.response;

import com.google.gson.annotations.SerializedName;

public class EndRideResponse extends CommonResponse {

    EndRideResponse data;


    @SerializedName("total")
    private String totalFare;
    @SerializedName("trip_id")
    private String invoiceId;
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

    public void setTripNo(String tripNo) {
        this.tripNo = tripNo;
    }

    public void setTotalFare(String totalFare) {
        this.totalFare = totalFare;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
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

    public String getWallet_deduction() {
        return wallet_deduction;
    }

    public void setWallet_deduction(String wallet_deduction) {
        this.wallet_deduction = wallet_deduction;
    }

    public String getPromo_deduction() {
        return promo_deduction;
    }

    public void setPromo_deduction(String promo_deduction) {
        this.promo_deduction = promo_deduction;
    }

    public String getTrip_charges() {
        return trip_charges;
    }

    public void setTrip_charges(String trip_charges) {
        this.trip_charges = trip_charges;
    }

    /*  invoice_id "573304d9cb0aa0b80cbb440e"
        trip_charges 5918.95    */
}

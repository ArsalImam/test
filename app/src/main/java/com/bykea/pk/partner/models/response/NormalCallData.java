package com.bykea.pk.partner.models.response;

import com.bykea.pk.partner.models.data.Stop;
import com.bykea.pk.partner.utils.Utils;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringUtils;

public class NormalCallData extends CommonResponse {

    private String status;
    private String icon;
    private NormalCallData data;
    private String trip_charges;

    @SerializedName("initiate_time")
    private long sentTime;

    @SerializedName("trip_id")
    private String tripId;
    @SerializedName("trip_no")
    private String tripNo;
    private String referenceId;
    @SerializedName("start_address")
    private String startAddress;
    @SerializedName("start_lat")
    private String startLat;
    @SerializedName("start_lng")
    private String startLng;

    @SerializedName("end_address")
    private String endAddress;
    @SerializedName("end_lat")
    private String endLat;
    @SerializedName("end_lng")
    private String endLng;

    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;

    @Deprecated
    @SerializedName("est_distance")
    private String distance;
    @SerializedName("est_time")
    private String arivalTime;

    @SerializedName("est")
    private int kraiKiKamai;

    @SerializedName("dist")
    private float estimatedDistance;

    @SerializedName("payable")
    private int cashKiWasooli;

    @SerializedName("passenger_id")
    private String passId;
    @SerializedName("full_name")
    private String passName;
    @SerializedName("pass_img")
    private String passImage;
    @SerializedName("phone_no")
    private String phoneNo;
    @SerializedName("rating")
    private String rating;
    @SerializedName("total")
    private String totalFare;
    private String pass_socket_id;

    @SerializedName("km")
    private String distanceCovered;
    @SerializedName("minutes")
    private String totalMins;
    @SerializedName("total_amount")
    private String totalAmount;
    private String started_at;


    @SerializedName("trip_type")
    private String callType;
    private boolean isDispatcher;
    @SerializedName("cType")
    private String creator_type;


    private String wallet_deduction;
    @SerializedName("wc")
    private int passWallet;
    private String promo_deduction;
    @SerializedName("amount")
    private String codAmount;
    @SerializedName("is_cod")
    private boolean isCod;
    @SerializedName("tripDetail")
    private boolean tripDetailsAdded;

    private String details;
    private String dropoff_discount;

    private String amount_parcel_value;
    @SerializedName("return_run")
    private boolean isReturnRun;
    @SerializedName("wallet_deposit")
    private boolean isWalletDeposit;
    private String sub_type;
    private String order_no;

    @SerializedName("receiver_phone")
    private String receiverPhone;

    @SerializedName("receiver_name")
    private String receiverName;

    @SerializedName("receiver_address")
    private String receiverAddress;

    @Deprecated
    private String recName;

    @Deprecated
    private String rec_no;

    @Deprecated
    @SerializedName("cAddr")
    private String complete_address;

    @Deprecated
    @SerializedName("driver_passenger_eta")
    private Long driverToPassengerEta;

    @Deprecated
    @SerializedName("trip_eta")
    private String tripEta;

    @Deprecated
    @SerializedName("distance")
    private String driverToPassengerDistance;

    @Deprecated
    @SerializedName("trip_distance")
    private String tripDistance;

    @Deprecated
    @SerializedName("zone_pickup_name_urdu")
    private String zoneNamePickupUrdu;

    @Deprecated
    @SerializedName("zone_dropoff_name_urdu")
    private String zoneNameDropOffUrdu;

    //new ride request design is required this field
    @Deprecated
    @SerializedName("dropoff_zone_name")
    private String dropoffZoneName;

    //new ride request design is required this field
    @Deprecated
    @SerializedName("dropoff_zone_name_urdu")
    private String dropoffZoneNameUrdu;

    @SerializedName("pickup")
    private Stop pickupStop;

    @SerializedName("dropoff")
    private Stop dropoffStop;

    @SerializedName("trip_status_code")
    private Integer serviceCode;


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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(String totalFare) {
        this.totalFare = totalFare;
    }

    public String getPassId() {
        return passId;
    }

    public void setPassId(String passId) {
        this.passId = passId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public void setData(NormalCallData data) {
        this.data = data;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
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

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
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

    public String getDistance() {
        return "" + (Math.round(Double.parseDouble(distance) * 10.0) / 10.0);
    }

    @Deprecated
    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getArivalTime() {
        return arivalTime;
    }

    public void setArivalTime(String arivalTime) {
        this.arivalTime = arivalTime;
    }

    public String getPassName() {
        return StringUtils.capitalize(passName);
    }

    public void setPassName(String passName) {
        this.passName = passName;
    }

    public String getPhoneNo() {
        return Utils.phoneNumberToShow(phoneNo);
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public NormalCallData getData() {
        return data;
    }

    public String getPassImage() {
        return passImage;
    }

    public String getTripNo() {
        return tripNo;
    }

    public void setTripNo(String tripNo) {
        this.tripNo = tripNo;
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

    public void setPassImage(String passImage) {
        this.passImage = passImage;
    }


    public String getPass_socket_id() {
        return pass_socket_id;
    }

    public void setPass_socket_id(String pass_socket_id) {
        this.pass_socket_id = pass_socket_id;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public long getSentTime() {
        return sentTime != 0 ? sentTime : System.currentTimeMillis();
    }

    public void setSentTime(long sentTime) {
        this.sentTime = sentTime;
    }

    public boolean isDispatcher() {
        return isDispatcher;
    }

    public void setDispatcher(boolean dispatcher) {
        isDispatcher = dispatcher;
    }

    public String getStarted_at() {
        return started_at;
    }

    public void setStarted_at(String started_at) {
        this.started_at = started_at;
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

    public String getCodAmount() {
        return Utils.getCommaFormattedAmount(codAmount);
    }

    public String getCodAmountNotFormatted() {
        return codAmount;
    }

    public void setCodAmount(String codAmount) {
        this.codAmount = codAmount;
    }

    public boolean isCod() {
        return isCod;
    }

    public void setCod(boolean cod) {
        isCod = cod;
    }

    public String getTrip_charges() {
        return StringUtils.isNotBlank(trip_charges) ? trip_charges : "0";
    }

    public void setTrip_charges(String trip_charges) {
        this.trip_charges = trip_charges;
    }

    public String getPassWallet() {
        return passWallet < 200 ? "" + passWallet : "200+";
    }

    public void setPassWallet(int passWallet) {
        this.passWallet = passWallet;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Deprecated
    public String getRec_no() {
        return rec_no;
    }

    @Deprecated
    public void setRec_no(String rec_no) {
        this.rec_no = rec_no;
    }

    public String getDropoff_discount() {
        return dropoff_discount;
    }

    public void setDropoff_discount(String dropoff_discount) {
        this.dropoff_discount = dropoff_discount;
    }

    public String getCreator_type() {
        return creator_type;
    }

    public void setCreator_type(String creator_type) {
        this.creator_type = creator_type;
    }

    public boolean isTripDetailsAdded() {
        return tripDetailsAdded;
    }

    public void setTripDetailsAdded(boolean tripDetailsAdded) {
        this.tripDetailsAdded = tripDetailsAdded;
    }

    public String getAmount_parcel_value() {
        return amount_parcel_value;
    }

    public void setAmount_parcel_value(String amount_parcel_value) {
        this.amount_parcel_value = amount_parcel_value;
    }

    public boolean isReturnRun() {
        return isReturnRun;
    }

    public void setReturnRun(boolean returnRun) {
        isReturnRun = returnRun;
    }

    public boolean isWalletDeposit() {
        return isWalletDeposit;
    }

    public void setWalletDeposit(boolean walletDeposit) {
        isWalletDeposit = walletDeposit;
    }

    @Deprecated
    public String getComplete_address() {
        return complete_address;
    }

    @Deprecated
    public void setComplete_address(String complete_address) {
        this.complete_address = complete_address;
    }

    public String getSub_type() {
        return sub_type;
    }

    public void setSub_type(String sub_type) {
        this.sub_type = sub_type;
    }

    @Deprecated
    public String getRecName() {
        return recName;
    }

    @Deprecated
    public void setRecName(String recName) {
        this.recName = recName;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public int getKraiKiKamai() {
        return kraiKiKamai;
    }

    public void setKraiKiKamai(int kraiKiKamai) {
        this.kraiKiKamai = kraiKiKamai;
    }

    public float getEstimatedDistance() {
        return estimatedDistance;
    }

    public void setEstimatedDistance(float estimatedDistance) {
        this.estimatedDistance = estimatedDistance;
    }

    public int getCashKiWasooli() {
        return cashKiWasooli;
    }

    public void setCashKiWasooli(int cashKiWasooli) {
        this.cashKiWasooli = cashKiWasooli;
    }

    @Deprecated
    public String getDropoffZoneName() {
        return dropoffZoneName;
    }

    @Deprecated
    public String getDropoffZoneNameUrdu() {
        return dropoffZoneNameUrdu;
    }

    @Deprecated
    public Long getDriverToPassengerEta() {
        return driverToPassengerEta;
    }

    @Deprecated
    public void setDriverToPassengerEta(Long driverToPassengerEta) {
        this.driverToPassengerEta = driverToPassengerEta;
    }

    @Deprecated
    public String getTripEta() {
        return tripEta;
    }

    @Deprecated
    public void setTripEta(String tripEta) {
        this.tripEta = tripEta;
    }

    @Deprecated
    public String getDriverToPassengerDistance() {
        return driverToPassengerDistance;
    }

    @Deprecated
    public void setDriverToPassengerDistance(String driverToPassengerDistance) {
        this.driverToPassengerDistance = driverToPassengerDistance;
    }

    @Deprecated
    public String getTripDistance() {
        return tripDistance;
    }

    @Deprecated
    public void setTripDistance(String tripDistance) {
        this.tripDistance = tripDistance;
    }

    @Deprecated
    public String getZoneNamePickupUrdu() {
        return zoneNamePickupUrdu;
    }

    @Deprecated
    public void setZoneNamePickupUrdu(String zoneNamePickupUrdu) {
        this.zoneNamePickupUrdu = zoneNamePickupUrdu;
    }

    @Deprecated
    public String getZoneNameDropOffUrdu() {
        return zoneNameDropOffUrdu;
    }

    @Deprecated
    public void setZoneNameDropOffUrdu(String zoneNameDropOffUrdu) {
        this.zoneNameDropOffUrdu = zoneNameDropOffUrdu;
    }

    @Deprecated
    public void setDropoffZoneName(String dropoffZoneName) {
        this.dropoffZoneName = dropoffZoneName;
    }

    @Deprecated
    public void setDropoffZoneNameUrdu(String dropoffZoneNameUrdu) {
        this.dropoffZoneNameUrdu = dropoffZoneNameUrdu;
    }

    public Stop getPickupStop() {
        return pickupStop;
    }

    public void setPickupStop(Stop pickupStop) {
        this.pickupStop = pickupStop;
    }

    public Stop getDropoffStop() {
        return dropoffStop;
    }

    public void setDropoffStop(Stop dropoffStop) {
        this.dropoffStop = dropoffStop;
    }

    public Integer getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(Integer serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }
}

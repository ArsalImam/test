package com.bykea.pk.partner.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.bykea.pk.partner.models.data.Stop;
import com.bykea.pk.partner.utils.Utils;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class NormalCallData extends CommonResponse implements Parcelable {

    private String status;
    private String icon;
    private NormalCallData data;
    private String trip_charges;

    @SerializedName("initiate_time")
    private long sentTime;

    @SerializedName("bookings_summary")
    private String bookingsSummary;

    @SerializedName("bookings")
    private ArrayList<BatchBooking> bookingList;

    @SerializedName("trip_id")
    private String tripId;
    @SerializedName("trip_no")
    private String tripNo;
    @SerializedName("batch_code")
    private String batchCode;
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

    @SerializedName("sender_name")
    private String senderName;
    @SerializedName("sender_phone")
    private String senderPhone;
    @SerializedName("sender_address")
    private String senderAddress;

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

    @SerializedName("rule_ids")
    private ArrayList<String> ruleIds;

    @SerializedName("dropoff")
    private Stop dropoffStop;

    @SerializedName("trip_status_code")
    private Integer serviceCode;

    @SerializedName("extra_params")
    private ExtraParams extraParams;

    public NormalCallData() {
    }

    protected NormalCallData(Parcel in) {
        status = in.readString();
        icon = in.readString();
        data = in.readParcelable(NormalCallData.class.getClassLoader());
        trip_charges = in.readString();
        sentTime = in.readLong();
        tripId = in.readString();
        tripNo = in.readString();
        referenceId = in.readString();
        startAddress = in.readString();
        startLat = in.readString();
        batchCode = in.readString();
        startLng = in.readString();
        endAddress = in.readString();
        endLat = in.readString();
        endLng = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        distance = in.readString();
        arivalTime = in.readString();
        kraiKiKamai = in.readInt();
        estimatedDistance = in.readFloat();
        cashKiWasooli = in.readInt();
        passId = in.readString();
        passName = in.readString();
        passImage = in.readString();
        phoneNo = in.readString();
        rating = in.readString();
        totalFare = in.readString();
        pass_socket_id = in.readString();
        distanceCovered = in.readString();
        totalMins = in.readString();
        started_at = in.readString();
        callType = in.readString();
        isDispatcher = in.readByte() != 0;
        creator_type = in.readString();
        wallet_deduction = in.readString();
        passWallet = in.readInt();
        promo_deduction = in.readString();
        codAmount = in.readString();
        isCod = in.readByte() != 0;
        tripDetailsAdded = in.readByte() != 0;
        details = in.readString();
        dropoff_discount = in.readString();
        amount_parcel_value = in.readString();
        isReturnRun = in.readByte() != 0;
        isWalletDeposit = in.readByte() != 0;
        sub_type = in.readString();
        order_no = in.readString();
        receiverPhone = in.readString();
        receiverName = in.readString();
        receiverAddress = in.readString();
        senderName = in.readString();
        senderPhone = in.readString();
        senderAddress = in.readString();
        recName = in.readString();
        rec_no = in.readString();
        complete_address = in.readString();
        if (in.readByte() == 0) {
            driverToPassengerEta = null;
        } else {
            driverToPassengerEta = in.readLong();
        }
        tripEta = in.readString();
        driverToPassengerDistance = in.readString();
        tripDistance = in.readString();
        zoneNamePickupUrdu = in.readString();
        zoneNameDropOffUrdu = in.readString();
        dropoffZoneName = in.readString();
        dropoffZoneNameUrdu = in.readString();
        if (in.readByte() == 0) {
            serviceCode = null;
        } else {
            serviceCode = in.readInt();
        }
        extraParams = in.readParcelable(ExtraParams.class.getClassLoader());
    }

    public static final Creator<NormalCallData> CREATOR = new Creator<NormalCallData>() {
        @Override
        public NormalCallData createFromParcel(Parcel in) {
            return new NormalCallData(in);
        }

        @Override
        public NormalCallData[] newArray(int size) {
            return new NormalCallData[size];
        }
    };

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
        if (StringUtils.isNotEmpty(distance)) {
            return "" + (Math.round(Double.valueOf(distance) * 10.0) / 10.0);
        } else {
            return "0";
        }
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

    /**
     * this will return the unformatted customer wallet
     *
     * @return wallet of customer
     */
    public int getActualPassWallet() {
        return passWallet;
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
        if (receiverPhone != null && !receiverPhone.isEmpty()) return receiverPhone;
        else return rec_no;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverName() {
        if (receiverName != null && !receiverName.isEmpty()) return receiverName;
        else return recName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverAddress() {
        if (receiverAddress != null && !receiverAddress.isEmpty()) return receiverAddress;
        else return complete_address;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public ExtraParams getExtraParams() {
        return extraParams;
    }

    public void setExtraParams(ExtraParams extraParams) {
        this.extraParams = extraParams;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeString(icon);
        dest.writeParcelable(data, flags);
        dest.writeString(trip_charges);
        dest.writeLong(sentTime);
        dest.writeString(tripId);
        dest.writeString(tripNo);
        dest.writeString(batchCode);
        dest.writeString(referenceId);
        dest.writeString(startAddress);
        dest.writeString(startLat);
        dest.writeString(startLng);
        dest.writeString(endAddress);
        dest.writeString(endLat);
        dest.writeString(endLng);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(distance);
        dest.writeString(arivalTime);
        dest.writeInt(kraiKiKamai);
        dest.writeFloat(estimatedDistance);
        dest.writeInt(cashKiWasooli);
        dest.writeString(passId);
        dest.writeString(passName);
        dest.writeString(passImage);
        dest.writeString(phoneNo);
        dest.writeString(rating);
        dest.writeString(totalFare);
        dest.writeString(pass_socket_id);
        dest.writeString(distanceCovered);
        dest.writeString(totalMins);
        dest.writeString(started_at);
        dest.writeString(callType);
        dest.writeByte((byte) (isDispatcher ? 1 : 0));
        dest.writeString(creator_type);
        dest.writeString(wallet_deduction);
        dest.writeInt(passWallet);
        dest.writeString(promo_deduction);
        dest.writeString(codAmount);
        dest.writeByte((byte) (isCod ? 1 : 0));
        dest.writeByte((byte) (tripDetailsAdded ? 1 : 0));
        dest.writeString(details);
        dest.writeString(dropoff_discount);
        dest.writeString(amount_parcel_value);
        dest.writeByte((byte) (isReturnRun ? 1 : 0));
        dest.writeByte((byte) (isWalletDeposit ? 1 : 0));
        dest.writeString(sub_type);
        dest.writeString(order_no);
        dest.writeString(receiverPhone);
        dest.writeString(receiverName);
        dest.writeString(receiverAddress);
        dest.writeString(senderName);
        dest.writeString(senderPhone);
        dest.writeString(senderAddress);
        dest.writeString(recName);
        dest.writeString(rec_no);
        dest.writeString(complete_address);
        if (driverToPassengerEta == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(driverToPassengerEta);
        }
        dest.writeString(tripEta);
        dest.writeString(driverToPassengerDistance);
        dest.writeString(tripDistance);
        dest.writeString(zoneNamePickupUrdu);
        dest.writeString(zoneNameDropOffUrdu);
        dest.writeString(dropoffZoneName);
        dest.writeString(dropoffZoneNameUrdu);
        if (serviceCode == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(serviceCode);
        }
        dest.writeParcelable(extraParams, flags);
    }

    public ArrayList<String> getRuleIds() {
        return ruleIds;
    }

    public void setRuleIds(ArrayList<String> ruleIds) {
        this.ruleIds = ruleIds;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public ArrayList<BatchBooking> getBookingList() {
        return bookingList;
    }

    public void setBookingList(ArrayList<BatchBooking> bookingList) {
        this.bookingList = bookingList;
    }

    public String getBookingsSummary() {
        return bookingsSummary;
    }

    public void setBookingsSummary(String bookingsSummary) {
        this.bookingsSummary = bookingsSummary;
    }
}

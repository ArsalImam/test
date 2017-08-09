package com.bykea.pk.partner.models.data;

import com.google.gson.annotations.SerializedName;

public class TripHistoryData  {

    private String id;
    @SerializedName("start_address")
    private String startAddress;
    @SerializedName("end_address")
    private String endAddress;
    private String startTime;
    private String endTime;
    @SerializedName("trip_no")
    private String tripNo;
    private TripId trip_id;
    private String username;
    private String totalCharges;
    private String rating;
    private String status;
    private String trip_type;
    private boolean is_verified;

    @SerializedName("invoice_id")
    private Invoice invoice;
    @SerializedName("driver_id")
    Driver driver;
    @SerializedName("passenger_id")
    Passenger passenger;
    @SerializedName("rating_driving_id")
    DriverRating driverRating;
    @SerializedName("rating_pass_id")
    PassRating passRating;

    @SerializedName("arrived_time")
    private String arrivedTime;
    @SerializedName("accpted_time")
    private String acceptTime;
    @SerializedName("ended_at")
    private String finishTime;
    @SerializedName("cancelled_at")
    private String cancelTime;
    private String created_at;
    private String initiate_time;
    private String cancel_by;

    public String getStatus() {
        return status;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoiceId(Invoice invoiceId) {
        this.invoice = invoiceId;
    }

    public DriverRating getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(DriverRating driverRating) {
        this.driverRating = driverRating;
    }

    public PassRating getPassRating() {
        return passRating;
    }

    public void setPassRating(PassRating passRating) {
        this.passRating = passRating;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public String getArrivedTime() {
        return arrivedTime;
    }

    public void setArrivedTime(String arrivedTime) {
        this.arrivedTime = arrivedTime;
    }

    public String getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(String acceptTime) {
        this.acceptTime = acceptTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(String cancelTime) {
        this.cancelTime = cancelTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTripNo() {
        return tripNo;
    }

    public void setTripNo(String tripNo) {
        this.tripNo = tripNo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTotalCharges() {
        return totalCharges;
    }

    public void setTotalCharges(String totalCharges) {
        this.totalCharges = totalCharges;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTrip_type() {
        return trip_type;
    }

    public void setTrip_type(String trip_type) {
        this.trip_type = trip_type;
    }

    public String getCancel_by() {
        return cancel_by;
    }

    public void setCancel_by(String cancel_by) {
        this.cancel_by = cancel_by;
    }

    public boolean is_verified() {
        return is_verified;
    }

    public void setIs_verified(boolean is_verified) {
        this.is_verified = is_verified;
    }

    public String getInitiate_time() {
        return initiate_time;
    }

    public void setInitiate_time(String initiate_time) {
        this.initiate_time = initiate_time;
    }

    public TripId getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(TripId trip_id) {
        this.trip_id = trip_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }


    public static class Driver {
        @SerializedName("_id")
        private String id;
        private String phone;
        @SerializedName("full_name")
        private String name;
        private String plate_no;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPlate_no() {
            return plate_no;
        }

        public void setPlate_no(String plate_no) {
            this.plate_no = plate_no;
        }
    }

    public static class Passenger {
        @SerializedName("_id")
        String id;
        String phone;
        @SerializedName("full_name")
        String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Invoice {
        String total;
        String km;
        String minutes;
        @SerializedName("remaining_amount")
        String remainingAmount;
        @SerializedName("received_amount")
        String receivedAmount;
        @SerializedName("base_fare")
        String baseFare;
        @SerializedName("price_km")
        String pricePerKm;
        @SerializedName("price_per_minute")
        String pricePerMin;
        @SerializedName("admin_fee")
        String adminFee;
        private String start_balance;

        @SerializedName("trip_charges")
        private String tripCharges;
        private String promo_deduction;
        private String wallet_deduction;

        public String getKm() {
            return km;
        }

        public void setKm(String km) {
            this.km = km;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getMinutes() {
            return minutes;
        }

        public void setMinutes(String minutes) {
            this.minutes = minutes;
        }

        public String getRemainingAmount() {
            return remainingAmount;
        }

        public void setRemainingAmount(String remainingAmount) {
            this.remainingAmount = remainingAmount;
        }

        public String getReceivedAmount() {
            return receivedAmount;
        }

        public void setReceivedAmount(String receivedAmount) {
            this.receivedAmount = receivedAmount;
        }

        public String getBaseFare() {
            return baseFare;
        }

        public void setBaseFare(String baseFare) {
            this.baseFare = baseFare;
        }

        public String getPricePerKm() {
            return pricePerKm;
        }

        public void setPricePerKm(String pricePerKm) {
            this.pricePerKm = pricePerKm;
        }

        public String getPricePerMin() {
            return pricePerMin;
        }

        public void setPricePerMin(String pricePerMin) {
            this.pricePerMin = pricePerMin;
        }

        public String getAdminFee() {
            return adminFee;
        }

        public void setAdminFee(String adminFee) {
            this.adminFee = adminFee;
        }

        public String getPromo_deduction() {
            return promo_deduction;
        }

        public void setPromo_deduction(String promo_deduction) {
            this.promo_deduction = promo_deduction;
        }

        public String getWallet_deduction() {
            return wallet_deduction;
        }

        public void setWallet_deduction(String wallet_deduction) {
            this.wallet_deduction = wallet_deduction;
        }

        public String getTripCharges() {
            return tripCharges;
        }

        public void setTripCharges(String tripCharges) {
            this.tripCharges = tripCharges;
        }

        public String getStart_balance() {
            return start_balance;
        }

        public void setStart_balance(String start_balance) {
            this.start_balance = start_balance;
        }
    }


    public static class PassRating {
        String rate;
        private String[] feedback_message;

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }

        public String[] getFeedback_message() {
            return feedback_message;
        }

        public void setFeedback_message(String[] feedback_message) {
            this.feedback_message = feedback_message;
        }
    }

    public static class DriverRating {
        String rate;

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }
    }

    public static class TripId {
        private String _id;
        private String trip_no;

        public String getTrip_no() {
            return trip_no;
        }

        public void setTrip_no(String trip_no) {
            this.trip_no = trip_no;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }
    }
}


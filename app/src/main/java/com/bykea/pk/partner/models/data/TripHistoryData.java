package com.bykea.pk.partner.models.data;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class TripHistoryData implements Serializable {

    @SerializedName("_id")
    private String id;
    @SerializedName("start_address")
    private String startAddress;
    @SerializedName("end_address")
    private String endAddress;
    @SerializedName("trip_no")
    private String tripNo;
    private TripId trip_id;
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

    @SerializedName("accpted_time")
    private String acceptTime;
    @SerializedName("ended_at")
    private String finishTime;
    @SerializedName("cancelled_at")
    private String cancelTime;
    private String created_at;
    private String cancel_by;
    private String cancel_fee;

    private boolean dd;

    public String getStatus() {
        return status;
    }

    public Invoice getInvoice() {
        return invoice;
    }


    public DriverRating getDriverRating() {
        return driverRating;
    }

    public PassRating getPassRating() {
        return passRating;
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

    public String getAcceptTime() {
        return acceptTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public String getCancelTime() {
        return cancelTime;
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

    public String getEndAddress() {
        return endAddress;
    }

    public String getTripNo() {
        return tripNo;
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

    public String getCancel_feeNoCheck() {
        return cancel_fee;
    }

    public String getCancel_fee() {
        return StringUtils.isNotBlank(cancel_fee) ? showCancelFee() ? cancel_fee : "0" : "0";
    }

    public boolean showCancelFee() {
        return getCancel_by().equals("Driver") || getCancel_by().equals("Partner");
    }

    public String getCancel_by() {
        return StringUtils.isNotBlank(cancel_by) ? StringUtils.capitalize(cancel_by) : "Admin";
    }

    public boolean is_verified() {
        return is_verified;
    }

    public TripId getTrip_id() {
        return trip_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public boolean isDd() {
        return dd;
    }

    public void setDd(boolean dd) {
        this.dd = dd;
    }


    public static class Driver implements Serializable {
        private String plate_no;

        public String getPlate_no() {
            return plate_no;
        }
    }

    public static class Passenger implements Serializable {
        @SerializedName("full_name")
        String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Invoice implements Serializable {
        String total;
        String km;
        String minutes;

        @SerializedName("wait_mins")
        private String waitMins;

        @SerializedName("wc_value")
        private String wait_charges;

        @SerializedName("base_fare")
        String baseFare;
        @SerializedName("price_km")
        String pricePerKm;
        @SerializedName("price_per_minute")
        String pricePerMin;
        private String start_balance;

        @SerializedName("trip_charges")
        private String tripCharges;
        private String promo_deduction;
        private String dropoff_discount;
        private String wallet_deduction;

        private boolean dd;

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

        public String getBaseFare() {
            return baseFare;
        }

        public String getPricePerKm() {
            return pricePerKm;
        }

        public String getPricePerMin() {
            return pricePerMin;
        }

        public String getPromo_deduction() {
            return promo_deduction;
        }

        public String getWallet_deduction() {
            return wallet_deduction;
        }

        public String getTripCharges() {
            return tripCharges;
        }

        public String getStart_balance() {
            return start_balance;
        }

        public String getDropoff_discount() {
            return StringUtils.isNotBlank(dropoff_discount) ? dropoff_discount : "0";
        }

        public void setDropoff_discount(String dropoff_discount) {
            this.dropoff_discount = dropoff_discount;
        }

        public String getWaitMins() {
            return waitMins;
        }

        public void setWaitMins(String waitMins) {
            this.waitMins = waitMins;
        }

        public String getWait_charges() {
            return wait_charges;
        }

        public void setWait_charges(String wait_charges) {
            this.wait_charges = wait_charges;
        }

        public boolean isDd() {
            return dd;
        }

        public void setDd(boolean dd) {
            this.dd = dd;
        }
    }


    public static class PassRating implements Serializable {
        String rate;
        private String[] feedback_message;

        public String getRate() {
            return rate;
        }

        public String[] getFeedback_message() {
            return feedback_message;
        }
    }

    public static class DriverRating implements Serializable {
        String rate;

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }
    }

    public static class TripId implements Serializable {
        private String trip_no;

        public String getTrip_no() {
            return trip_no;
        }
    }
}


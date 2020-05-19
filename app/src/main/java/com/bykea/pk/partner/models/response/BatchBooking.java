package com.bykea.pk.partner.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BatchBooking {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("booking_code")
    @Expose
    private String bookingCode;
    @SerializedName("display_tag")
    @Expose
    private String displayTag;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("dropoff")
    @Expose
    private BatchBookingDropoff dropoff;
    private boolean selected;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public String getDisplayTag() {
        return displayTag;
    }

    public void setDisplayTag(String displayTag) {
        this.displayTag = displayTag;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BatchBookingDropoff getDropoff() {
        return dropoff;
    }

    public void setDropoff(BatchBookingDropoff dropoff) {
        this.dropoff = dropoff;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
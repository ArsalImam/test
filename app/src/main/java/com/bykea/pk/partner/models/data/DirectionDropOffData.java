package com.bykea.pk.partner.models.data;

/***
 * Direction Drop off Model Class
 */

public class DirectionDropOffData extends MultiDeliveryDropOff {

    String tripNumber;
    String driverName;
    int codValue;

    public DirectionDropOffData(String mArea, String tripNumber, String driverName, int codValue,
                                String streetAddress, String dropOffNumberText) {
        super(mArea, streetAddress, dropOffNumberText);
        this.tripNumber = tripNumber;
        this.driverName = driverName;
        this.codValue = codValue;
    }

    public String getmArea() {
        return mArea;
    }

    public void setmArea(String mArea) {
        this.mArea = mArea;
    }

    public String getTripNumber() {
        return tripNumber;
    }

    public void setTripNumber(String tripNumber) {
        this.tripNumber = tripNumber;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public int getCodValue() {
        return codValue;
    }

    public void setCodValue(int codValue) {
        this.codValue = codValue;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getDropOffNumberText() {
        return dropOffNumberText;
    }

    public void setDropOffNumberText(String dropOffNumberText) {
        this.dropOffNumberText = dropOffNumberText;
    }
}

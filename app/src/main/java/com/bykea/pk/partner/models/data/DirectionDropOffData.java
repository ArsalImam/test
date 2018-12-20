package com.bykea.pk.partner.models.data;

import org.apache.commons.lang3.StringUtils;

/***
 * Direction Drop off Model Class
 */

public class DirectionDropOffData extends MultiDeliveryDropOff {

    String tripNumber;
    String passengerName;
    int codValue;

    public DirectionDropOffData(String mArea, String tripNumber, String passengerName, int codValue,
                                String streetAddress, String dropOffNumberText) {
        super(mArea, streetAddress, dropOffNumberText);
        this.tripNumber = tripNumber;
        this.passengerName = passengerName;
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

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
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

    /***
     * Builder pattern is a creational design pattern it means its solves problem related to
     * object creation.It is also very oftenly used in android development. Best example would be
     * an AlertDialog class from AOSP.
     *
     * Builder pattern is used to create instance of very complex object in easiest way.
     */
    public static class Builder {

        String driverName;
        String mArea;
        String dropOffNumberText;

        public Builder setDriverName(String driverName) {
            this.driverName = driverName;
            return this;
        }

        public Builder setmArea(String mArea) {
            this.mArea = mArea;
            return this;
        }

        public Builder setDropOffNumberText(String dropOffNumberText) {
            this.dropOffNumberText = dropOffNumberText;
            return this;
        }

        public DirectionDropOffData build(){
            return new DirectionDropOffData(mArea, StringUtils.EMPTY, driverName, 0,
                    StringUtils.EMPTY, dropOffNumberText);
        }
    }
}

package com.bykea.pk.partner;

import android.util.Patterns;

import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Utils;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class UtilsUnitTest {

    /**
     * Test method to validate Email Address.
     * This test case will be considered correct/pass when Email address is Correct
     */

    /*@Test
    public void emailValidator() throws NullPointerException {
        assertTrue(Utils.isValidEmail("name@email.com"));
    }*/


    /**
     * Test method to convert double to 2 decimal points
     * This test case will be considered correct/pass when any double no will have 2 or less digits after point
     */

    @Test
    public void convertDoubleTo2DecimalPlaces() {
        String convertedNo = Utils.upto2decimalPlaces(5.333293);
        assertTrue(convertedNo.split("\\.")[1].length() <= 2);
        convertedNo = Utils.upto2decimalPlaces(0.1);
        assertTrue(convertedNo.split("\\.")[1].length() <= 2);
        convertedNo = Utils.upto2decimalPlaces(19.22);
        assertTrue(convertedNo.split("\\.")[1].length() <= 2);
        convertedNo = Utils.upto2decimalPlaces(129.094);
        assertTrue(convertedNo.split("\\.")[1].length() <= 2);
        convertedNo = Utils.upto2decimalPlaces(129.);
        assertTrue(convertedNo.split("\\.")[1].length() <= 2);
    }

    /**
     * Test method to convert double to 2 decimal points
     * This test case will be considered correct/pass when any double no will have 2 or less digits after point
     */

    @Test
    public void calculateDistance() {
        String distance = Utils.calculateDistanceInKm(33.6844, 73.0479,
                33.6844, 73.0479);
        assertTrue(StringUtils.isNotBlank(distance));
        distance = Utils.calculateDistanceInKm(24.8607, 67.0011,
                33.6844, 73.0479);
        assertTrue(StringUtils.isNotBlank(distance));
    }

    //region Multi Delivery Helper Method Test Cases

    @Test
    public void getDistance_Kilometer() {
        float distanceInMeter = 1000;
        assertEquals("Failed when distance is meter" ,Utils.getDistance(distanceInMeter), "1.0");
    }

    @Test
    public void getDuration_minutes() {
        int durationInSeconds = 60;
        assertEquals("Failed when duration in seconds is less than minute",
                Utils.getDuration(durationInSeconds),
                1);
    }

    @Test
    public void getTimeIn_percentage() {
        int durationInMiliSeconds = 20000;
        assertEquals("Failed when percentage calculation is wrong",
                Utils.getTimeInPercentage(durationInMiliSeconds, 5),
                1000);
    }

    //endregion

}
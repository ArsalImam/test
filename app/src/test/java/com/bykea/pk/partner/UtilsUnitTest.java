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
     * Test method to validate Phone No.
     * This test case will be considered correct/pass when Phone No format is correct
     */

    @Test
    public void phoneNoValidator() throws NullPointerException {
        assertTrue(isValidNumber("03135744774"));
    }


    /**
     * Test method to validate Phone No.
     * This test case will be considered correct/pass when Phone No format is not correct
     */

    @Test
    public void phoneNoValidatorToCheckWrongPhNoFormat() throws NullPointerException {
        assertFalse(isValidNumber("92389833"));
        assertFalse(isValidNumber("+92389833"));
        assertFalse(isValidNumber("04135744774"));
        assertFalse(isValidNumber("83628ehduhuf"));
        assertFalse(isValidNumber("0-3284e093278r9nciu"));
    }


    /**
     * Same Method as in Utils but removed dependencies on Context and FontEditText
     * //TODO use Utils method in instrumentations tests
     */
    private boolean isValidNumber(String number) {
        if (StringUtils.isBlank(number)) {
            return false;
        } else if (!number.startsWith("03")) {
            return false;
        } else if (number.length() < 11) {
            return false;
        } else {
            return true;
        }
    }


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

}
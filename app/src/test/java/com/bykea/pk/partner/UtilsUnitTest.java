package com.bykea.pk.partner;

import android.util.Patterns;

import com.bykea.pk.partner.utils.Utils;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class UtilsUnitTest {

    /**
     * Test method to validate Email Address.
     * true when Correct Email address
     */

    /*@Test
    public void emailValidator() throws NullPointerException {
        assertTrue(Utils.isValidEmail("name@email.com"));
    }*/


    /**
     * Test method to validate Phone No.
     * @return true when Correct Phone No is used
     */

    @Test
    public void phoneNoValidator() throws NullPointerException {
        assertTrue(isValidNumber("03135744774"));
    }


    /**
     * Test method to validate Phone No.
     * @return false when Phone No is not correct
     */

    @Test
    public void phoneNoValidatorToCheckWrongPhNoFormat() throws NullPointerException {
        assertFalse(isValidNumber("92389833"));
    }


    public static boolean isValidNumber(String number) {
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
     * This test method is to check if 2 strings are equal
     */
    @Test
    public void stringEqualsIgnoreCase() {
        String str = "someString";
        String str1 = "someString";
        assertThat(str.equalsIgnoreCase(str1), is(true));
    }

    @Test
    public void stringNotEqualsIgnoreCase() {
        assertThat("SomeString".equalsIgnoreCase("SomeDiffString"), is(false));
    }
}
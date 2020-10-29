package com.bykea.pk.partner.utils;

import android.text.InputFilter;
import android.text.Spanned;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bykea.pk.partner.utils.Constants.DIGIT_ZERO;

public class DecimalDigitsInputFilter implements InputFilter {

    private Pattern mPattern;
    private int digitsBeforeZero;

    public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
        this.digitsBeforeZero = digitsBeforeZero;
        mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero) + "}+((\\.[0-9]{0," + (digitsAfterZero) + "})?)");
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String txt = getAllText(source, dest, dstart);
        Matcher matcher = mPattern.matcher(txt);
        if (!matcher.matches())
            return StringUtils.EMPTY;
        else {
            if (dest.length() == digitsBeforeZero)
                return StringUtils.EMPTY;
        }
        return null;
    }


    private String getAllText(CharSequence source, Spanned dest, int dstart) {
        String allText = StringUtils.EMPTY;
        if (!dest.toString().isEmpty()) {
            if (source.toString().isEmpty()) {
                allText = deleteCharAtIndex(dest, dstart);
            } else {
                allText = new StringBuilder(dest).insert(dstart, source).toString();
            }
        }
        return allText;
    }

    private String deleteCharAtIndex(Spanned dest, int dstart) {
        try {
            StringBuilder builder = new StringBuilder(dest);
            if (builder.length() > DIGIT_ZERO) builder.deleteCharAt(dstart);
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return StringUtils.EMPTY;
        }
    }
}
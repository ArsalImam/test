package com.bykea.pk.partner.utils;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            return "";
        else {
            if (dest.length() == digitsBeforeZero)
                return "";
        }
        return null;
    }


    private String getAllText(CharSequence source, Spanned dest, int dstart) {
        String allText = "";
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
        StringBuilder builder = new StringBuilder(dest);
        builder.deleteCharAt(dstart);
        return builder.toString();
    }
}
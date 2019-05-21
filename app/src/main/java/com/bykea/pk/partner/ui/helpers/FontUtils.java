package com.bykea.pk.partner.ui.helpers;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.text.style.MetricAffectingSpan;
import android.util.LruCache;

public class FontUtils {
    private static LruCache<String, Typeface> TYPEFACE =
            new LruCache<>(13);
//    private static Map<String, Typeface> TYPEFACE = new HashMap<String, Typeface>();

    public static Typeface getFonts(Context context, String name) {
        Typeface typeface = TYPEFACE.get(name);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), "fonts/"
                    + name);
            TYPEFACE.put(name, typeface);
        }
        return typeface;
    }

    public static SpannableString getStyledTitle(Context context, String title, String fontName) {
        SpannableString s = new SpannableString(title);
        s.setSpan(new TypefaceSpan(context, fontName), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return s;
    }

    public static SpannableString getStyledTitle(Context context, int title, String fontName) {
        SpannableString s = new SpannableString(context.getString(title));
        s.setSpan(new TypefaceSpan(context, fontName), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return s;
    }

    public static SpannableString getStylesString(String string, int color, int start, int end) {
        SpannableString spannableString = new SpannableString(string);
        spannableString.setSpan(new ForegroundColorSpan(color), start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private static class TypefaceSpan extends MetricAffectingSpan {
//        private static LruCache<String, Typeface> sTypefaceCache =
//                new LruCache<String, Typeface>(12);

        private Typeface mTypeface;

        public TypefaceSpan(Context context, String typefaceName) {
            mTypeface = getFonts(context, typefaceName);

//            if (mTypeface == null) {
//                mTypeface = Typeface.createFromAsset(context.getApplicationContext()
//                        .getAssets(), String.format("fonts/%s", typefaceName));
//                sTypefaceCache.put(typefaceName, mTypeface);
//            }
        }

        @Override
        public void updateMeasureState(TextPaint p) {
            p.setTypeface(mTypeface);
            p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        }

        @Override
        public void updateDrawState(TextPaint tp) {
            tp.setTypeface(mTypeface);
            tp.setFlags(tp.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        }
    }
}
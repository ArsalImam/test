package com.bykea.pk.partner.loadboard.widgets

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ForegroundColorSpan
import android.text.style.MetricAffectingSpan
import android.util.LruCache

object FontUtils {
    private val TYPEFACE = LruCache<String, Typeface>(13)
    //    private static Map<String, Typeface> TYPEFACE = new HashMap<String, Typeface>();

    fun getFonts(context: Context, name: String): Typeface? {
        var typeface: Typeface? = TYPEFACE.get(name)
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.assets, "fonts/$name")
            TYPEFACE.put(name, typeface)
        }
        return typeface
    }

    fun getStyledTitle(context: Context, title: String, fontName: String): SpannableString {
        val s = SpannableString(title)
        s.setSpan(TypefaceSpan(context, fontName), 0, s.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return s
    }

    fun getStyledTitle(context: Context, title: Int, fontName: String): SpannableString {
        val s = SpannableString(context.getString(title))
        s.setSpan(TypefaceSpan(context, fontName), 0, s.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return s
    }

    fun getStylesString(string: String, color: Int, start: Int, end: Int): SpannableString {
        val spannableString = SpannableString(string)
        spannableString.setSpan(ForegroundColorSpan(color), start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString
    }

    private class TypefaceSpan(context: Context, typefaceName: String) : MetricAffectingSpan() {
        //        private static LruCache<String, Typeface> sTypefaceCache =
        //                new LruCache<String, Typeface>(12);

        private val mTypeface: Typeface?

        init {
            mTypeface = getFonts(context, typefaceName)

            //            if (mTypeface == null) {
            //                mTypeface = Typeface.createFromAsset(context.getApplicationContext()
            //                        .getAssets(), String.format("fonts/%s", typefaceName));
            //                sTypefaceCache.put(typefaceName, mTypeface);
            //            }
        }

        override fun updateMeasureState(p: TextPaint) {
            p.typeface = mTypeface
            p.flags = p.flags or Paint.SUBPIXEL_TEXT_FLAG
        }

        override fun updateDrawState(tp: TextPaint) {
            tp.typeface = mTypeface
            tp.flags = tp.flags or Paint.SUBPIXEL_TEXT_FLAG
        }
    }
}
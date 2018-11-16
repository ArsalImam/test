package com.bykea.pk.partner.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.Button;

import com.bykea.pk.partner.R;

public class FontButton extends Button {

    //    private static final String fontExtension = ".ttf";
    private static final String sDefaultFontName = "MuseoSans_1.otf";

    public FontButton(Context context) {
        super(context);
    }

    public FontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyAttributes(context, attrs);
    }

    public FontButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyAttributes(context, attrs);
    }

    private void applyAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.FontTextView);
        String fontName = Fonts.values()[a.getInt(R.styleable.FontTextView_fontName, 0)].getName();
        a.recycle();
        if (!isInEditMode() && !TextUtils.isEmpty(sDefaultFontName)) {
            setTypeface(FontUtils.getFonts(sDefaultFontName));
        }
    }
}
package com.bykea.pk.partner.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.AttributeSet;


import com.bykea.pk.partner.R;

/***
 * Custom view Edit Text with out be configured with our custom font at time of initialization.
 */
public class FontEditText extends AppCompatEditText {

    //    private static final String fontExtension = ".ttf";
    private static final String sDefaultFontName = "";

    public FontEditText(Context context) {
        super(context);
    }

    public FontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyAttributes(context, attrs);
    }

    public FontEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyAttributes(context, attrs);
    }

    private void applyAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.FontTextView);
        String fontName = Fonts.values()[a.getInt(R.styleable.FontTextView_fontName, 0)].getName();
        a.recycle();
        if (!isInEditMode() && !TextUtils.isEmpty(fontName)) {
            setTypeface(FontUtils.getFonts(fontName));
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
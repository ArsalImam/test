package com.bykea.pk.partner.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.bykea.pk.partner.R;

public class FontTextView extends AppCompatTextView {

    AttributeSet attributeSet;

    public FontTextView(Context context) {
        super(context);
    }

    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attributeSet = attrs;
        applyAttributes(context, attrs);
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyle) {
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

    public void setAttr(Context context, String fontNames){
        TypedArray a = context.obtainStyledAttributes(attributeSet,
                R.styleable.FontTextView);
        String fontName = fontNames;
        a.recycle();
        if (!isInEditMode() && !TextUtils.isEmpty(fontName)) {
            setTypeface(FontUtils.getFonts(fontName));
        }
    }
}
package com.bykea.pk.partner.loadboard.widgets

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet

import com.bykea.pk.partner.loadboard.R
import me.grantland.widget.AutofitTextView

class AutoFitFontTextView : AutofitTextView {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        applyAttributes(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        applyAttributes(context, attrs)
    }

    private fun applyAttributes(context: Context, attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs,
                R.styleable.FontTextView)
        val fontName = Fonts.values()[a.getInt(R.styleable.FontTextView_fontName, 0)].getName()
        a.recycle()
        if (!isInEditMode && !TextUtils.isEmpty(fontName)) {
            typeface = FontUtils.getFonts(context,fontName)
        }
    }
}
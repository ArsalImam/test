package com.bykea.pk.partner.widgets;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;

public class DownOnlyAutoCompleteTextView extends AppCompatAutoCompleteTextView {

    private final static int MINIMAL_HEIGHT = 100;

    public DownOnlyAutoCompleteTextView(Context context) {
        super(context);
    }

    public DownOnlyAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DownOnlyAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void showDropDown() {
        Rect displayFrame = new Rect();
        getWindowVisibleDisplayFrame(displayFrame);

        int[] locationOnScreen = new int[2];
        getLocationOnScreen(locationOnScreen);

        int bottom = locationOnScreen[1] + getHeight();
        int availableHeightBelow = displayFrame.bottom - bottom;
        if (availableHeightBelow >= MINIMAL_HEIGHT) {
            setDropDownHeight(availableHeightBelow);
        }

        super.showDropDown();
    }
}
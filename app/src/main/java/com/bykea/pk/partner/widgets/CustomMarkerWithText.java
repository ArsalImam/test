package com.bykea.pk.partner.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bykea.pk.partner.R;

public class CustomMarkerWithText extends AppCompatImageView {

    Path clipPath;
    String text = "1";

    /**
     * Used to set size and color of the member initials
     */
    TextPaint plainText;

    /**
     * Used as background of the initials with user specific color
     */
    Paint paint;

    /**
     * Used as background of the initials with user specific color for strike
     */
    Paint strikePaint;

    /**
     * Image width and height (both are same and constant, defined in dimens.xml
     * We cache them in this field
     */
    private int imageSize;

    /**
     * Bounds of the canvas in float
     * Used to set bounds of member initial and background
     */
    RectF rectF;
    /**
     * A text size of drawn text.
     */
    private float textSize;
    /**
     * text style for drawn text
     */
    private int textStyle;


    public CustomMarkerWithText(Context context) {
        super(context);
        initialize();
    }

    public CustomMarkerWithText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomMarkerWithText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    /**
     * Initialize fields
     *
     * @param set is an interface reference variable.
     */
    protected void init(AttributeSet set){

        if (set == null) return;

        TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.CustomMarkerWithText);
        text = ta.getString(R.styleable.CustomMarkerWithText_imageText);
        textSize = ta.getDimension(R.styleable.CustomMarkerWithText_textSize, 12f);
        textStyle = ta.getInt(R.styleable.CustomMarkerWithText_textStyle, Typeface.NORMAL);
        ta.recycle();
        initialize();

    }

    private void initialize() {
        rectF = new RectF();
        clipPath = new Path();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        plainText = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        plainText.setColor(Color.WHITE);
        plainText.setTextSize(textSize);
        plainText.setTypeface(Typeface.defaultFromStyle(textStyle));

        strikePaint = new Paint();
        strikePaint.setColor(Color.BLACK);
        strikePaint.setStrokeWidth(getResources().getDisplayMetrics().density * 1);
    }

    /**
     * Set the canvas bounds here
     *
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int screenWidth = MeasureSpec.getSize(widthMeasureSpec);
        int screenHeight = MeasureSpec.getSize(heightMeasureSpec);
        rectF.set(0, 0, screenWidth, screenHeight);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Fetch Center point coordinates
        int centerX = Math.round(canvas.getWidth() * 0.5f);
        int centerY = Math.round(canvas.getHeight() * 0.5f);

        if (text != null) {
            //To Draw Text
            float textWidth = plainText.measureText(text) * 0.5f;
            float textBaseLineHeight = plainText.getFontMetrics().ascent * -0.4f;
            //Draw the text above the ImageView
            canvas.drawText(text,
                    centerX - textWidth,
                    centerY + textBaseLineHeight,
                    plainText);
        }

        super.onDraw(canvas);
    }

    //region setter and getter for custom view
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getImageSize() {
        return imageSize;
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public int getTextStyle() {
        return textStyle;
    }

    public void setTextStyle(int textStyle) {
        this.textStyle = textStyle;
    }

    //endregion
}
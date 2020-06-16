package com.bykea.pk.partner.widgets.record_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;

import com.bykea.pk.partner.R;


/**
 * Record audio button with mic icon
 * Created by Devlomi on 13/12/2017.
 *
 * @see <a href="https://github.com/3llomi/RecordView">Library Documentation</a>
 */
public class RecordButton extends AppCompatImageView implements View.OnTouchListener, View.OnClickListener {

    private ScaleAnim scaleAnim;
    private RecordView recordView;
    private boolean listenForRecord = true;
    private OnRecordClickListener onRecordClickListener;


    /**
     * Setter method for RecordView
     *
     * @param recordView current {@link RecordView}
     */
    public void setRecordView(RecordView recordView) {
        this.recordView = recordView;
    }

    /**
     * Parametrised Constructor
     *
     * @param context Calling Context
     */
    public RecordButton(Context context) {
        super(context);
        init(context, null);
    }

    /**
     * Parameterized Constructor
     *
     * @param context Calling Context
     * @param attrs   Attributes of View
     */
    public RecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * Parameterized Constructor
     *
     * @param context      Calling Context
     * @param attrs        Attributes of View
     * @param defStyleAttr Style ID
     */
    public RecordButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);


    }

    /**
     * Initializes view
     *
     * @param context Calling Context
     * @param attrs   Attributes of View
     */
    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RecordButton);

            int imageResource = typedArray.getResourceId(R.styleable.RecordButton_mic_icon, -1);


            if (imageResource != -1) {
                setTheImageResource(imageResource);
            }

            typedArray.recycle();
        }


        scaleAnim = new ScaleAnim(this);


        this.setOnTouchListener(this);
        this.setOnClickListener(this);


    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setClip(this);
    }

    /**
     * By default, children are clipped to their bounds before drawing. This allows view groups to
     * override this behavior for animations, etc.
     *
     * @param v View
     */
    public void setClip(View v) {
        if (v.getParent() == null) {
            return;
        }

        if (v instanceof ViewGroup) {
            ((ViewGroup) v).setClipChildren(false);
            ((ViewGroup) v).setClipToPadding(false);
        }

        if (v.getParent() instanceof View) {
            setClip((View) v.getParent());
        }
    }

    /**
     * Sets Image Icon for Mic
     *
     * @param imageResource Image Resource ID
     */
    private void setTheImageResource(int imageResource) {
        Drawable image = AppCompatResources.getDrawable(getContext(), imageResource);
        setImageDrawable(image);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (isListenForRecord()) {
            switch (event.getAction()) {

                case MotionEvent.ACTION_CANCEL:
                    recordView.onActionMove((RecordButton) view, event);
                    break;

                case MotionEvent.ACTION_DOWN:
                    recordView.onActionDown((RecordButton) view);
                    break;


                case MotionEvent.ACTION_MOVE:
                    recordView.onActionMove((RecordButton) view, event);
                    break;

                case MotionEvent.ACTION_UP:
                    recordView.onActionUp((RecordButton) view);
                    break;

            }

        }
        return isListenForRecord();


    }

    /**
     * Starts scale animation
     */
    protected void startScale() {
        scaleAnim.start();
    }

    /**
     * Stops scale animation
     */
    protected void stopScale() {
        scaleAnim.stop();
    }

    /**
     * Enables/Disables Recording Listener
     *
     * @param listenForRecord True to enable Listener, False to disable it
     */
    public void setListenForRecord(boolean listenForRecord) {
        this.listenForRecord = listenForRecord;
    }

    /**
     * Getter method for Record Listener
     *
     * @return True if Listener is enabled and False otherwise
     */
    public boolean isListenForRecord() {
        return listenForRecord;
    }

    /**
     * Sets on click Listener
     *
     * @param onRecordClickListener onClick callback listener
     */
    public void setOnRecordClickListener(OnRecordClickListener onRecordClickListener) {
        this.onRecordClickListener = onRecordClickListener;
    }

    @Override
    public void onClick(View v) {
        if (onRecordClickListener != null)
            onRecordClickListener.onClick(v);
    }
}

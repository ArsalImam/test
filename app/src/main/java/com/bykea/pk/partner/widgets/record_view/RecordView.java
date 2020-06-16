package com.bykea.pk.partner.widgets.record_view;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;


import com.bykea.pk.partner.R;
import com.bykea.pk.partner.utils.Utils;

import java.io.IOException;

import io.supercharge.shimmerlayout.ShimmerLayout;

import static com.bykea.pk.partner.utils.Constants.DIGIT_EIGHT;
import static com.bykea.pk.partner.utils.Constants.DIGIT_THIRTY;
import static com.bykea.pk.partner.utils.Constants.DIGIT_ZERO;
import static com.bykea.pk.partner.utils.Constants.NEGATIVE_DIGIT_ONE;

/**
 * Custom class for Record View
 * Created by Devlomi on 24/08/2017.
 *
 * @see <a href="https://github.com/3llomi/RecordView">Library Documentation</a>
 */
public class RecordView extends RelativeLayout {

    public static final int DEFAULT_CANCEL_BOUNDS = DIGIT_EIGHT; //8dp
    private AppCompatImageView smallBlinkingMic, basketImg;
    private Chronometer counterTime;
    private TextView slideToCancel;
    private ShimmerLayout slideToCancelLayout;
    private AppCompatImageView arrow;
    private float initialX, basketInitialY, difX = DIGIT_ZERO;
    private float cancelBounds = DEFAULT_CANCEL_BOUNDS;
    private long startTime, elapsedTime = DIGIT_ZERO;
    private Context context;
    private OnRecordListener recordListener;
    private boolean isSwiped, isLessThanSecondAllowed = false;
    private boolean isSoundEnabled = true;
    private int RECORD_START = R.raw.record_start;
    private int RECORD_FINISHED = R.raw.record_finished;
    private int RECORD_ERROR = R.raw.record_error;
    private MediaPlayer player;
    private AnimationHelper animationHelper;

    /**
     * Parameterized Constructor
     *
     * @param context Calling Context
     */
    public RecordView(Context context) {
        super(context);
        this.context = context;
        init(context, null, NEGATIVE_DIGIT_ONE, NEGATIVE_DIGIT_ONE);
    }

    /**
     * Parameterized Constructor
     *
     * @param context Calling Context
     * @param attrs   Attributes of View
     */
    public RecordView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context, attrs, NEGATIVE_DIGIT_ONE, NEGATIVE_DIGIT_ONE);
    }

    /**
     * Parameterized Constructor
     *
     * @param context      Calling Context
     * @param attrs        Attributes of View
     * @param defStyleAttr Style ID
     */
    public RecordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context, attrs, defStyleAttr, NEGATIVE_DIGIT_ONE);
    }

    /**
     * Initializes view
     *
     * @param context      Calling Context
     * @param attrs        Attributes of View
     * @param defStyleAttr Style ID
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        View view = View.inflate(context, R.layout.record_view_layout, null);
        addView(view);


        ViewGroup viewGroup = (ViewGroup) view.getParent();
        viewGroup.setClipChildren(false);

        arrow = view.findViewById(R.id.arrow);
        slideToCancel = view.findViewById(R.id.slide_to_cancel);
        smallBlinkingMic = view.findViewById(R.id.glowing_mic);
        counterTime = view.findViewById(R.id.counter_tv);
        basketImg = view.findViewById(R.id.basket_img);
        slideToCancelLayout = view.findViewById(R.id.shimmer_layout);


        hideViews(true);


        if (attrs != null && defStyleAttr == NEGATIVE_DIGIT_ONE && defStyleRes == NEGATIVE_DIGIT_ONE) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RecordView,
                    defStyleAttr, defStyleRes);


            int slideArrowResource = typedArray.getResourceId(R.styleable.RecordView_slide_to_cancel_arrow, NEGATIVE_DIGIT_ONE);
            String slideToCancelText = typedArray.getString(R.styleable.RecordView_slide_to_cancel_text);
            int slideMarginRight = (int) typedArray.getDimension(R.styleable.RecordView_slide_to_cancel_margin_right, DIGIT_THIRTY);
            int counterTimeColor = typedArray.getColor(R.styleable.RecordView_counter_time_color, NEGATIVE_DIGIT_ONE);
            int arrowColor = typedArray.getColor(R.styleable.RecordView_slide_to_cancel_arrow_color, NEGATIVE_DIGIT_ONE);


            int cancelBounds = typedArray.getDimensionPixelSize(R.styleable.RecordView_slide_to_cancel_bounds, NEGATIVE_DIGIT_ONE);

            if (cancelBounds != NEGATIVE_DIGIT_ONE)
                setCancelBounds(cancelBounds);//don't convert it to pixels since it's already in pixels


            if (slideArrowResource != NEGATIVE_DIGIT_ONE) {
                Drawable slideArrow = AppCompatResources.getDrawable(getContext(), slideArrowResource);
                arrow.setImageDrawable(slideArrow);
            }

            if (slideToCancelText != null)
                slideToCancel.setText(slideToCancelText);

            if (counterTimeColor != NEGATIVE_DIGIT_ONE)
                setCounterTimeColor(counterTimeColor);


            if (arrowColor != NEGATIVE_DIGIT_ONE)
                setSlideToCancelArrowColor(arrowColor);


            setMarginRight(slideMarginRight);

            typedArray.recycle();
        }


        animationHelper = new AnimationHelper(context, basketImg, smallBlinkingMic);

    }

    /**
     * Hides Views
     *
     * @param hideSmallMic True to hide mic icon
     */
    private void hideViews(boolean hideSmallMic) {
        slideToCancelLayout.setVisibility(GONE);
        counterTime.setVisibility(GONE);
        if (hideSmallMic)
            smallBlinkingMic.setVisibility(GONE);
    }

    /**
     * Shows Views
     */
    private void showViews() {
        slideToCancelLayout.setVisibility(VISIBLE);
        smallBlinkingMic.setVisibility(VISIBLE);
        counterTime.setVisibility(VISIBLE);
    }

    /**
     * Check if Audio recording length is less than 1 sec
     *
     * @param time Time difference b/w current and start time
     * @return True if length is less than 1 sec or False otherwise
     */
    private boolean isLessThanOneSecond(long time) {
        return time <= 1000;
    }

    /**
     * Plays Audio Start/Canceled/Error sound
     *
     * @param soundRes Current Audio Resource ID
     */
    private void playSound(int soundRes) {

        if (isSoundEnabled) {
            if (soundRes == DIGIT_ZERO)
                return;

            try {
                player = new MediaPlayer();
                AssetFileDescriptor afd = context.getResources().openRawResourceFd(soundRes);
                if (afd == null) return;
                player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
                player.prepare();
                player.start();
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                    }

                });
                player.setLooping(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * Handles on Action down onTouch Event
     *
     * @param recordBtn Record Button View
     */
    protected void onActionDown(RecordButton recordBtn) {

        if (recordListener != null)
            recordListener.onStart();


        animationHelper.setStartRecorded(true);
        animationHelper.resetBasketAnimation();
        animationHelper.resetSmallMic();


        recordBtn.startScale();
        slideToCancelLayout.startShimmerAnimation();

        initialX = recordBtn.getX();

        basketInitialY = basketImg.getY() + 90;

        playSound(RECORD_START);

        showViews();

        animationHelper.animateSmallMicAlpha();
        counterTime.setBase(SystemClock.elapsedRealtime());
        startTime = System.currentTimeMillis();
        counterTime.start();
        isSwiped = false;

    }

    /**
     * Handles on Action Move & Cancel onTouch Event
     *
     * @param recordBtn   Record Button View
     * @param motionEvent OnTouch motion event
     */
    protected void onActionMove(RecordButton recordBtn, MotionEvent motionEvent) {


        long time = System.currentTimeMillis() - startTime;

        if (!isSwiped) {
            //Swipe To Cancel
            if ((slideToCancelLayout.getX() != DIGIT_ZERO && slideToCancelLayout.getX() <= counterTime.getRight() + cancelBounds) || MotionEvent.ACTION_CANCEL == motionEvent.getAction()) {

                //if the time was less than one second then do not start basket animation
                if (isLessThanOneSecond(time)) {
                    hideViews(true);
                    animationHelper.clearAlphaAnimation(false);


                    animationHelper.onAnimationEnd();

                } else {
                    hideViews(false);
                    animationHelper.animateBasket(basketInitialY);
                }

                animationHelper.moveRecordButtonAndSlideToCancelBack(recordBtn, slideToCancelLayout, initialX, difX);

                counterTime.stop();
                slideToCancelLayout.stopShimmerAnimation();
                isSwiped = true;


                animationHelper.setStartRecorded(false);

                if (recordListener != null)
                    recordListener.onCancel();


            } else {


                //if statement is to Prevent Swiping out of bounds
                if (motionEvent.getRawX() < initialX) {
                    recordBtn.animate()
                            .x(motionEvent.getRawX())
                            .setDuration(DIGIT_ZERO)
                            .start();


                    if (difX == DIGIT_ZERO)
                        difX = (initialX - slideToCancelLayout.getX());


                    slideToCancelLayout.animate()
                            .x(motionEvent.getRawX() - difX)
                            .setDuration(DIGIT_ZERO)
                            .start();


                }


            }

        }
    }

    /**
     * Handles on Action Up onTouch Event
     *
     * @param recordBtn Record Button View
     */
    protected void onActionUp(RecordButton recordBtn) {

        elapsedTime = System.currentTimeMillis() - startTime;

        if (!isLessThanSecondAllowed && isLessThanOneSecond(elapsedTime) && !isSwiped) {
            if (recordListener != null)
                recordListener.onLessThanSecond();

            animationHelper.setStartRecorded(false);

            playSound(RECORD_ERROR);


        } else {
            if (recordListener != null && !isSwiped)
                recordListener.onFinish(elapsedTime);

            animationHelper.setStartRecorded(false);


            if (!isSwiped)
                playSound(RECORD_FINISHED);

        }


        //if user has swiped then do not hide SmallMic since it will be hidden after swipe Animation
        hideViews(!isSwiped);


        if (!isSwiped)
            animationHelper.clearAlphaAnimation(true);

        animationHelper.moveRecordButtonAndSlideToCancelBack(recordBtn, slideToCancelLayout, initialX, difX);
        counterTime.stop();
        slideToCancelLayout.stopShimmerAnimation();


    }

    /**
     * Sets Mergin right to Cancel Layout
     *
     * @param marginRight Margin Right value in dp
     */
    private void setMarginRight(int marginRight) {
        LayoutParams layoutParams = (LayoutParams) slideToCancelLayout.getLayoutParams();
        layoutParams.rightMargin = (int) Utils.toPixel(marginRight);

        slideToCancelLayout.setLayoutParams(layoutParams);
    }

    /**
     * Sets On Record Listener
     *
     * @param recrodListener OnRecordListener callback interface
     */
    public void setOnRecordListener(OnRecordListener recrodListener) {
        this.recordListener = recrodListener;
    }

    /**
     * Sets Chronometer color
     *
     * @param color Color ID
     */
    public void setCounterTimeColor(int color) {
        counterTime.setTextColor(color);
    }

    /**
     * Sets Slide to cancel arrow color
     *
     * @param color Color ID
     */
    public void setSlideToCancelArrowColor(int color) {
        arrow.setColorFilter(color);
    }

    /**
     * Sets cancel bounds
     *
     * @param cancelBounds Cancel Bounds in px
     */
    private void setCancelBounds(float cancelBounds) {
        this.cancelBounds = cancelBounds;
    }

}



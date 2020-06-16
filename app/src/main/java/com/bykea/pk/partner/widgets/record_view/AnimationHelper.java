package com.bykea.pk.partner.widgets.record_view;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import androidx.vectordrawable.graphics.drawable.AnimatorInflaterCompat;

import com.bykea.pk.partner.R;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.bykea.pk.partner.utils.Constants.ANIMATION_SCALE_TO_XY;
import static com.bykea.pk.partner.utils.Constants.ANIMATION_SCALE_XY;
import static com.bykea.pk.partner.utils.Constants.BASKET_DELTA_FROM_Y_DIFF;
import static com.bykea.pk.partner.utils.Constants.BASKET_DELTA_TO_Y_DIFF;
import static com.bykea.pk.partner.utils.Constants.DIGIT_ZERO;
import static com.bykea.pk.partner.utils.Constants.HIDE_VIEW_ANIMATION_DELAY;
import static com.bykea.pk.partner.utils.Constants.HIDE_VIEW_HANDLER_DELAY;
import static com.bykea.pk.partner.utils.Constants.MIC_BLINK_ANIMATION_DURATION;
import static com.bykea.pk.partner.utils.Constants.SHOW_VIEW_ANIMATION_DELAY;
import static com.bykea.pk.partner.utils.Constants.SHOW_VIEW_HANDLER_DELAY;

/**
 * Animation Helper class
 * Created by Devlomi on 24/08/2017.
 *
 * @see <a href="https://github.com/3llomi/RecordView">Library Documentation</a>
 */
public class AnimationHelper {
    private Context context;
    private AnimatedVectorDrawableCompat animatedVectorDrawable;
    private AppCompatImageView basketImg, smallBlinkingMic;
    private AlphaAnimation alphaAnimation;
    private OnBasketAnimationEnd onBasketAnimationEndListener;
    private boolean isBasketAnimating, isStartRecorded = false;
    private float micX, micY = DIGIT_ZERO;
    private AnimatorSet micAnimation;
    private TranslateAnimation translateAnimationToShowBasket, translateAnimationToHideBasket;
    private Handler startAnimationHandler, endAnimationHandler;

    /**
     * Parameterized Constructor
     *
     * @param context          Calling Context
     * @param basketImg        Basket ImageView
     * @param smallBlinkingMic Blinking mic icon ImageView
     */
    public AnimationHelper(Context context, AppCompatImageView basketImg, AppCompatImageView smallBlinkingMic) {
        this.context = context;
        this.smallBlinkingMic = smallBlinkingMic;
        this.basketImg = basketImg;
        animatedVectorDrawable = AnimatedVectorDrawableCompat.create(context, R.drawable.recv_basket_animated);
    }

    /**
     * Starts animation to animate basket
     *
     * @param basketInitialY Basket IV's Y position in px
     */
    @SuppressLint("RestrictedApi")
    public void animateBasket(float basketInitialY) {
        isBasketAnimating = true;

        clearAlphaAnimation(false);

        //save initial x,y values for mic icon
        if (micX == DIGIT_ZERO) {
            micX = smallBlinkingMic.getX();
            micY = smallBlinkingMic.getY();
        }


        micAnimation = (AnimatorSet) AnimatorInflaterCompat.loadAnimator(context, R.animator.delete_mic_animation);
        micAnimation.setTarget(smallBlinkingMic); // set the view you want to animate


        translateAnimationToShowBasket = new TranslateAnimation(DIGIT_ZERO, DIGIT_ZERO, basketInitialY, basketInitialY - BASKET_DELTA_TO_Y_DIFF);
        translateAnimationToShowBasket.setDuration(SHOW_VIEW_ANIMATION_DELAY);

        translateAnimationToHideBasket = new TranslateAnimation(DIGIT_ZERO, DIGIT_ZERO, basketInitialY - BASKET_DELTA_FROM_Y_DIFF, basketInitialY);
        translateAnimationToHideBasket.setDuration(HIDE_VIEW_ANIMATION_DELAY);


        micAnimation.start();
        basketImg.setImageDrawable(animatedVectorDrawable);

        startAnimationHandler = new Handler();
        startAnimationHandler.postDelayed(() -> {
            basketImg.setVisibility(VISIBLE);
            basketImg.startAnimation(translateAnimationToShowBasket);
        }, SHOW_VIEW_HANDLER_DELAY);

        translateAnimationToShowBasket.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                animatedVectorDrawable.start();
                endAnimationHandler = new Handler();
                endAnimationHandler.postDelayed(() -> {
                    basketImg.startAnimation(translateAnimationToHideBasket);
                    smallBlinkingMic.setVisibility(INVISIBLE);
                    basketImg.setVisibility(INVISIBLE);
                }, HIDE_VIEW_HANDLER_DELAY);


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        translateAnimationToHideBasket.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                basketImg.setVisibility(INVISIBLE);

                isBasketAnimating = false;

                //if the user pressed the record button while the animation is running
                // then do NOT call on Animation end
                if (onBasketAnimationEndListener != null && !isStartRecorded) {
                    onBasketAnimationEndListener.onAnimationEnd();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    /**
     * Resets Basket animation if the user started a new Record while the Animation is running
     * then we want to stop the current animation and revert views back to default state
     */
    public void resetBasketAnimation() {
        if (isBasketAnimating) {

            translateAnimationToShowBasket.reset();
            translateAnimationToShowBasket.cancel();
            translateAnimationToHideBasket.reset();
            translateAnimationToHideBasket.cancel();

            micAnimation.cancel();

            smallBlinkingMic.clearAnimation();
            basketImg.clearAnimation();


            if (startAnimationHandler != null)
                startAnimationHandler.removeCallbacksAndMessages(null);
            if (endAnimationHandler != null)
                endAnimationHandler.removeCallbacksAndMessages(null);

            basketImg.setVisibility(INVISIBLE);
            smallBlinkingMic.setX(micX);
            smallBlinkingMic.setY(micY);
            smallBlinkingMic.setVisibility(View.GONE);

            isBasketAnimating = false;


        }
    }

    /**
     * Clears Alpha animation and hides blinking mic icon if required
     *
     * @param hideView True to hide blinking mic icon
     */
    public void clearAlphaAnimation(boolean hideView) {
        alphaAnimation.cancel();
        alphaAnimation.reset();
        smallBlinkingMic.clearAnimation();
        if (hideView) {
            smallBlinkingMic.setVisibility(View.GONE);
        }
    }

    /**
     * Starts Alpha animation on mic icon
     */
    public void animateSmallMicAlpha() {
        alphaAnimation = new AlphaAnimation(ANIMATION_SCALE_XY, ANIMATION_SCALE_TO_XY);
        alphaAnimation.setDuration(MIC_BLINK_ANIMATION_DURATION);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        smallBlinkingMic.startAnimation(alphaAnimation);
    }

    /**
     * Resets Record View to its default position when Audio recoding is canceled
     *
     * @param recordBtn           RecordButton
     * @param slideToCancelLayout Slide to cancel view
     * @param initialX            Initial X value of RecordView
     * @param difX                Y value of difference between cancel layout and record view
     */
    public void moveRecordButtonAndSlideToCancelBack(final RecordButton recordBtn, FrameLayout slideToCancelLayout,
                                                     float initialX, float difX) {
        final ValueAnimator positionAnimator =
                ValueAnimator.ofFloat(recordBtn.getX(), initialX);

        positionAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        positionAnimator.addUpdateListener(animation -> {
            float x = (Float) animation.getAnimatedValue();
            recordBtn.setX(x);
        });

        recordBtn.stopScale();
        positionAnimator.setDuration(DIGIT_ZERO);
        positionAnimator.start();


        // if the move event was not called ,then the difX will still 0 and there is no need to move it back
        if (difX != DIGIT_ZERO) {
            float x = initialX - difX;
            slideToCancelLayout.animate()
                    .x(x)
                    .setDuration(DIGIT_ZERO)
                    .start();
        }


    }

    /**
     * Resets Mic Icon
     */
    public void resetSmallMic() {
        smallBlinkingMic.setAlpha(ANIMATION_SCALE_XY);
        smallBlinkingMic.setScaleX(ANIMATION_SCALE_XY);
        smallBlinkingMic.setScaleY(ANIMATION_SCALE_XY);
    }

    /**
     * Sets callback interface for basket animation end
     *
     * @param onBasketAnimationEndListener OnBasketAnimationEnd callback interface
     */
    public void setOnBasketAnimationEndListener(OnBasketAnimationEnd onBasketAnimationEndListener) {
        this.onBasketAnimationEndListener = onBasketAnimationEndListener;

    }

    /**
     * Handles on Animation end
     */
    protected void onAnimationEnd() {
        if (onBasketAnimationEndListener != null)
            onBasketAnimationEndListener.onAnimationEnd();
    }

    /**
     * Sets FLAG to check if the user started a new Record by pressing the RecordButton
     *
     * @param startRecorded True when audio is started and False when it's stoped
     */
    public void setStartRecorded(boolean startRecorded) {
        isStartRecorded = startRecorded;
    }

}

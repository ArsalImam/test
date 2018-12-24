package com.bykea.pk.partner.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontEditText;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnTextChanged;

import static butterknife.OnTextChanged.*;

public class MultiDeliveryFeedbackActivity extends AppCompatActivity {

    @BindView(R.id.tvTripId)
    FontTextView tvTripId;

    @BindView(R.id.startAddressTv)
    FontTextView startAddressTv;

    @BindView(R.id.invoiceMsgTv)
    FontTextView invoiceMsgTv;

    @BindView(R.id.endAddressTv)
    FontTextView endAddressTv;

    @BindView(R.id.totalAmountTv)
    FontTextView totalAmountTv;

    @BindView(R.id.tvTotalTime)
    FontTextView tvTotalTime;

    @BindView(R.id.tvTotalDistance)
    FontTextView tvTotalDistance;

    @BindView(R.id.receivedAmountEt)
    FontEditText receivedAmountEt;

    @BindView(R.id.llKharedari)
    LinearLayout llKharedari;

    @BindView(R.id.llTotal)
    LinearLayout llTotal;

    @BindView(R.id.callerRb)
    RatingBar callerRb;

    @BindView(R.id.feedbackBtn)
    ImageView feedbackBtn;

    @BindView(R.id.tvWalletDeduction)
    FontTextView tvWalletDeduction;

    @BindView(R.id.tvAmountToGet)
    FontTextView tvAmountToGet;

    @BindView(R.id.rlWalletDeduction)
    RelativeLayout rlWalletDeduction;

    @BindView(R.id.rlPromoDeduction)
    RelativeLayout rlPromoDeduction;

    @BindView(R.id.tvPromoDeduction)
    FontTextView tvPromoDeduction;

    @BindView(R.id.tvCOD)
    FontTextView tvCOD;

    @BindView(R.id.tvAmountToGetLable)
    FontTextView tvAmountToGetLable;

    @BindView(R.id.totalAmountTvLable)
    FontTextView totalAmountTvLable;

    @BindView(R.id.rlCOD)
    RelativeLayout rlCOD;

    @BindView(R.id.rlDeliveryStatus)
    RelativeLayout rlDeliveryStatus;

    @BindView(R.id.spDeliveryStatus)
    Spinner spDeliveryStatus;

    @BindView(R.id.llReceiverInfo)
    LinearLayout llReceiverInfo;

    @BindView(R.id.ivRight0)
    ImageView ivRight0;

    @BindView(R.id.etReceiverName)
    FontEditText etReceiverName;

    @BindView(R.id.etReceiverMobileNo)
    FontEditText etReceiverMobileNo;

    @BindView(R.id.kharedariAmountEt)
    FontEditText kharedariAmountEt;

    @BindView(R.id.rlDropOffDiscount)
    RelativeLayout rlDropOffDiscount;

    @BindView(R.id.tvDropOffDiscount)
    FontTextView tvDropOffDiscount;

    @BindView(R.id.scrollView)
    ScrollView scrollView;

    private String totalCharges = StringUtils.EMPTY;
    private int TOP_UP_LIMIT;
    private int AMOUNT_LIMIT;

    private MultiDeliveryFeedbackActivity mCurrentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_delivery_feedback);
        mCurrentActivity = this;
        EventBus.getDefault().post(Constants.Broadcast.UPDATE_FOREGROUND_NOTIFICATION);
        updateScroll();
    }

    /**
     * This method listens for touch on receivedAmountEt and moves scrollview to bottom
     */
    private void updateScroll() {
        moveScrollViewToBottom();
        receivedAmountEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });

        etReceiverName.requestFocus();
    }

    /**
     * This method scrolls down scroll view when it's ready
     */
    private void moveScrollViewToBottom() {
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                scrollView.fullScroll(View.FOCUS_DOWN);
                scrollView.clearFocus();
                scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @OnTextChanged(value = R.id.receivedAmountEt,
            callback = Callback.AFTER_TEXT_CHANGED)
    void afterTextChanged(Editable editable) {
        if (StringUtils.isNotBlank(editable) && StringUtils.isNotBlank(totalCharges)) {
            if (editable.toString().matches(Constants.REG_EX_DIGIT)) {
                if (Integer.parseInt(editable.toString()) >
                        (Integer.parseInt(totalCharges) + TOP_UP_LIMIT)) {
                    setEtError("Amount can't be more than " +
                            (Integer.parseInt(totalCharges) + TOP_UP_LIMIT));
                } else if (Integer.parseInt(editable.toString()) > AMOUNT_LIMIT) {
                    setEtError("Amount can't be more than " + AMOUNT_LIMIT);
                }
            } else {
                Utils.appToast(mCurrentActivity, "Please enter valid amount.");
            }
        }
    }


}

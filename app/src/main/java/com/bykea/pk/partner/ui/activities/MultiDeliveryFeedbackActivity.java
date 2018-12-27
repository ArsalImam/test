package com.bykea.pk.partner.ui.activities;

import android.os.SystemClock;
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

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.CallDriverAcknowledgeData;
import com.bykea.pk.partner.models.data.MultiDeliveryRideCompleteTripInfo;
import com.bykea.pk.partner.models.response.MultiDeliveryCallDriverAcknowledgeResponse;
import com.bykea.pk.partner.models.data.MultiDeliveryInvoiceData;
import com.bykea.pk.partner.models.response.MultiDeliveryFeedbackResponse;
import com.bykea.pk.partner.repositories.IUserDataHandler;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontEditText;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.HttpURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

    @BindView(R.id.rlDropOffDiscount)
    RelativeLayout rlDropOffDiscount;

    @BindView(R.id.tvDropOffDiscount)
    FontTextView tvDropOffDiscount;

    @BindView(R.id.scrollView)
    ScrollView scrollView;

    private long totalCharges;
    private int TOP_UP_LIMIT;
    private int AMOUNT_LIMIT;

    private MultiDeliveryFeedbackActivity mCurrentActivity;
    private long mLastClickTime;
    private UserRepository repository;
    private MultiDeliveryRideCompleteTripInfo tripInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_delivery_feedback);
        mCurrentActivity = this;
        ButterKnife.bind(this);
        repository = new UserRepository();
        EventBus.getDefault().post(Constants.Broadcast.UPDATE_FOREGROUND_NOTIFICATION);
        updateScroll();
        init();
    }

    private void init() {
        TOP_UP_LIMIT = AppPreferences.getSettings().getSettings().getTop_up_limit();
        AMOUNT_LIMIT = AppPreferences.getSettings().getSettings().getAmount_limit();
        Bundle bundle = getIntent().getExtras();
        MultiDeliveryCallDriverAcknowledgeResponse response = bundle
                .getParcelable(Keys.MULTIDELIVERY_COMPLETE_DATA);
        if (response != null) {
            CallDriverAcknowledgeData data = response.getData();
            MultiDeliveryInvoiceData invoice = data.getInvoice();
            tripInfo = data.getTripInfo();
            tvTotalDistance.setText(getString(R.string.distance_covered,
                    Utils.getDistance(
                            Float.valueOf(tripInfo.getTripDistance())
                    )));
            tvTotalTime.setText(getString(R.string.duration, tripInfo.getTripDuration()));
            startAddressTv.setText(tripInfo.getStartAddress());
            endAddressTv.setText(tripInfo.getEndAddress());
            totalAmountTv.setText(String.valueOf(invoice.getTripCharges()));
            tvPromoDeduction.setText(String.valueOf(invoice.getPromoDeduction()));
            tvWalletDeduction.setText(String.valueOf(invoice.getWalletDeduction()));
            totalCharges = invoice.getTotal();
            tvAmountToGet.setText(Utils.getCommaFormattedAmount(totalCharges));
        }
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

        //etReceiverName.requestFocus();
    }

    /**
     * This method scrolls down scroll view when it's ready
     */
    private void moveScrollViewToBottom() {
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                        scrollView.clearFocus();
                        scrollView.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                });
    }

    @OnTextChanged(value = R.id.receivedAmountEt,
            callback = Callback.AFTER_TEXT_CHANGED)
    void afterTextChanged(Editable editable) {
        if (StringUtils.isNotBlank(editable) &&
                StringUtils.isNotBlank(receivedAmountEt.getText().toString())) {
            if (editable.toString().matches(Constants.REG_EX_DIGIT)) {
                if (Integer.parseInt(editable.toString()) >
                        (totalCharges) + TOP_UP_LIMIT) {
                    setEtError(getString(R.string.amount_error,
                            (totalCharges) + TOP_UP_LIMIT));
                } else if (Integer.parseInt(editable.toString()) > AMOUNT_LIMIT) {
                    setEtError(getString(R.string.amount_error,
                            AMOUNT_LIMIT));
                }
            } else {
                Utils.appToast(mCurrentActivity, getString(R.string.invalid_amout));
            }
        }
    }

    @OnClick(R.id.feedbackBtn)
    public void onClick() {
        //For avoiding multiple clicks
        if (mLastClickTime != 0 && (SystemClock.elapsedRealtime() - mLastClickTime < 1000)) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        if (isValid()) {
            requestFeedback();
        }


    }

    /**
     * Request feed back for a passenger against the completed tripInfo.
     */
    private void requestFeedback() {
        Dialogs.INSTANCE.showLoader(mCurrentActivity);
        int receivedAmount = 0;
        try {
            receivedAmount = Integer.parseInt(receivedAmountEt.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        repository.requestMultiDeliveryDriverFeedback(
                tripInfo.getTripID(),
                receivedAmount,
                callerRb.getRating(),
                handler
        );
    }


    /**
     * Set Edit text error
     *
     * @param error The error message.
     */
    private void setEtError(String error) {
        receivedAmountEt.setError(error);
        receivedAmountEt.requestFocus();
    }

    /**
     * Feedback validation on the following cases.
     *
     * <ul>
     * <li>Check that the amount lie in the digit only regix</li>
     * <li>Check that the entered amount should be same to the total charges</li>
     * <li>Check that the entered amount should not be greater than
     * {@link MultiDeliveryFeedbackActivity#AMOUNT_LIMIT}</li>
     * <li>Check that the entered amount should be same to the total charges</li>
     * <li>Check that the rating should be given</li>
     * <li>Check that the amount should be entered & should not less than 0</li>
     * </ul>
     *
     * @return
     */
    private boolean isValid() {
        String charges = StringUtils.EMPTY;
        try {
            charges = String.valueOf(totalCharges);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (!receivedAmountEt.getText().toString().matches(Constants.REG_EX_DIGIT)) {
            setEtError(getString(R.string.error_invalid_amount));
            return false;
        } else if (charges.matches(Constants.REG_EX_DIGIT)
                && Integer.parseInt(receivedAmountEt.getText().toString()) < totalCharges) {
            setEtError(getString(R.string.error_amount_greater_than_total));
            return false;
        } else if (charges.matches(Constants.REG_EX_DIGIT)
                && Integer.parseInt(receivedAmountEt.getText().toString()) >
                (totalCharges + TOP_UP_LIMIT)) {
            setEtError(getString(R.string.amount_error,
                    (totalCharges + TOP_UP_LIMIT)));
            return false;
        } else if (Integer.parseInt(receivedAmountEt.getText().toString()) > AMOUNT_LIMIT) {
            setEtError(getString(R.string.amount_error, AMOUNT_LIMIT));
            return false;
        } else if (callerRb.getRating() <= 0.0) {
            Dialogs.INSTANCE.showError(mCurrentActivity, feedbackBtn,
                    getString(R.string.passenger_rating));
            return false;
        } else if (StringUtils.isNotBlank(receivedAmountEt.getText().toString())) {
            try {
                int receivedPrice = Integer.parseInt(receivedAmountEt.getText().toString());
                if (receivedPrice < 0) {
                    setEtError(getString(R.string.amount_not_acceptable));
                    return false;
                }
            } catch (Exception e) {
                setEtError(getString(R.string.amount_not_acceptable));
                return false;
            }
        }
        return true;
    }

    /**
     * Callback to be invoked when socket event has been emitted.
     */
    private IUserDataHandler handler = new UserDataHandler() {

        @Override
        public void onMultiDeliveryDriverFeedback(MultiDeliveryFeedbackResponse response) {
            if (mCurrentActivity != null) {
                Dialogs.INSTANCE.dismissDialog();
                Dialogs.INSTANCE.showToast(mCurrentActivity, response.getMessage());
                Utils.setCallIncomingState();
                AppPreferences.setWalletAmountIncreased(false);
                AppPreferences.setAvailableStatus(true);
                ActivityStackManager.getInstance().startHomeActivity(true,
                        mCurrentActivity);
                mCurrentActivity.finish();
            }
        }

        @Override
        public void onError(int errorCode, String errorMessage) {
            Dialogs.INSTANCE.dismissDialog();
            if (errorCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                EventBus.getDefault().post(Keys.UNAUTHORIZED_BROADCAST);
            } else {
                EventBus.getDefault().post(Keys.MULTIDELIVERY_ERROR_BORADCAST);
            }
        }
    };

    @Subscribe
    public void onEvent(final String action) {
        if (action.equalsIgnoreCase(Keys.MULTIDELIVERY_BATCH_COMPLETED)) {
            ActivityStackManager
                    .getInstance()
                    .startHomeActivity(true, mCurrentActivity);
            finish();
        }
    }

}

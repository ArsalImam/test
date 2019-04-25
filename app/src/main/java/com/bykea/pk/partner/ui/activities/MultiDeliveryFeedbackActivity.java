package com.bykea.pk.partner.ui.activities;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.CallDriverAcknowledgeData;
import com.bykea.pk.partner.models.data.MultiDeliveryCallDriverData;
import com.bykea.pk.partner.models.data.MultiDeliveryRideCompleteTripInfo;
import com.bykea.pk.partner.models.response.MultiDeliveryCallDriverAcknowledgeResponse;
import com.bykea.pk.partner.models.data.MultiDeliveryInvoiceData;
import com.bykea.pk.partner.models.response.MultiDeliveryFeedbackResponse;
import com.bykea.pk.partner.models.response.MultiDeliveryTrip;
import com.bykea.pk.partner.models.response.MultipleDeliveryBookingResponse;
import com.bykea.pk.partner.repositories.IUserDataHandler;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.adapters.DeliveryMsgsSpinnerAdapter;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontEditText;
import com.bykea.pk.partner.widgets.FontTextView;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static butterknife.OnTextChanged.*;

/**
 * Multi Delivery Feedback Activity for providing
 * feedback for passenger against the multi delivery trip
 */
public class MultiDeliveryFeedbackActivity extends BaseActivity {

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

    @BindView(R.id.cashOnDeliveryTV)
    FontTextView cashOnDeliveryTV;

    @BindView(R.id.tvAmountToGetLable)
    FontTextView tvAmountToGetLable;

    @BindView(R.id.totalAmountTvLable)
    FontTextView totalAmountTvLable;

    @BindView(R.id.cashOnDeliveryRL)
    RelativeLayout cashOnDeliveryRL;

    @BindView(R.id.rlDropOffDiscount)
    RelativeLayout rlDropOffDiscount;

    @BindView(R.id.tvDropOffDiscount)
    FontTextView tvDropOffDiscount;

    @BindView(R.id.scrollView)
    ScrollView scrollView;

    @BindView(R.id.llReceiverInfo)
    LinearLayout llReceiverInfo;
    @BindView(R.id.rlDeliveryStatus)
    RelativeLayout rlDeliveryStatus;
    @BindView(R.id.spDeliveryStatus)
    Spinner spDeliveryStatus;
    @BindView(R.id.ivRight0)
    ImageView ivRight0;
    @BindView(R.id.etReceiverName)
    FontEditText etReceiverName;
    @BindView(R.id.etReceiverMobileNo)
    FontEditText etReceiverMobileNo;

    private long totalCharges;
    private int TOP_UP_LIMIT;
    private int AMOUNT_LIMIT;

    private MultiDeliveryFeedbackActivity mCurrentActivity;
    private long mLastClickTime;
    private UserRepository repository;
    private MultiDeliveryTrip tripInfo;
    private int tripCounts;
    private MultiDeliveryInvoiceData invoice;
    private String TAG = MultiDeliveryFeedbackResponse.class.getSimpleName();
    private boolean isComingFromOnGoingRide;
    private int selectedMsgPosition = 0; //delivery status message selection position from drop down

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

    /**
     * Initialize the data and map data into view.
     */
    private void init() {
        TOP_UP_LIMIT = AppPreferences.getSettings().getSettings().getTop_up_limit();
        AMOUNT_LIMIT = AppPreferences.getSettings().getSettings().getAmount_limit();
        tripCounts = AppPreferences.getMultiDeliveryCallDriverData().getBookings().size();

        MultiDeliveryCallDriverData callDriverData = AppPreferences.
                getMultiDeliveryCallDriverData();

        Bundle bundle = getIntent().getExtras();
        String tripID = StringUtils.EMPTY;
        if (bundle != null) {
            isComingFromOnGoingRide = bundle.getBoolean(Keys.MULTIDELIVERY_FEEDBACK_SCREEN, false);
            tripID = bundle.getString(Keys.MULTIDELIVERY_TRIP_ID, StringUtils.EMPTY);
        }
        if (callDriverData != null) {

            MultipleDeliveryBookingResponse response = callDriverData.getTripById(tripID);
            if (response != null) {
                invoice = response.getInvoice();
                tripInfo = response.getTrip();
            }
            if (tripInfo != null) {
                try {
                    tvTotalDistance.setText(getString(R.string.distance_covered,
                            Utils.getDistance(
                                    Float.valueOf(tripInfo.getTripDistance())
                            )));
                    tvTotalTime.setText(getString(R.string.duration,
                            Float.valueOf(tripInfo.getTripDuration())
                    ));
                    startAddressTv.setText(tripInfo.getStartAddress());
                    endAddressTv.setText(tripInfo.getEndAddress());
                    tvTripId.setText(tripInfo.getTripNo());
                    if(tripInfo.getTripStatusCode() == Constants.TRIP_STATUS_CODE_DELIVERY)
                        updateUIICODelivery();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            if (invoice != null) {
                totalAmountTv.setText(String.valueOf(invoice.getTripCharges()));
                tvPromoDeduction.setText(String.valueOf(invoice.getPromoDeduction()));
                tvWalletDeduction.setText(String.valueOf(invoice.getWalletDeduction()));
                totalCharges = invoice.getTotal();
            }
            tvAmountToGet.setText(Utils.getCommaFormattedAmount(totalCharges));
        }
        etReceiverMobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Utils.isValidNumber(mCurrentActivity, etReceiverMobileNo);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * This method listens for touch on receivedAmountEt and moves scrollview to bottom
     */
    private void updateScroll() {
        moveScrollViewToBottom();
        receivedAmountEt.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
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

    /**
     * Text watcher for listening text changes in amount edit text.
     *
     * @param editable This is the interface for text whose content and markup
     *    can be changed (as opposed to immutable text like Strings).
     */
    @OnTextChanged(value = R.id.receivedAmountEt,
            callback = Callback.AFTER_TEXT_CHANGED)
    void afterTextChanged(Editable editable) {
        if (StringUtils.isNotBlank(editable) &&
                StringUtils.isNotBlank(receivedAmountEt.getText().toString())) {
            if (editable.toString().matches(Constants.REG_EX_DIGIT)) {
                if (Integer.parseInt(editable.toString()) >
                        (totalCharges) + TOP_UP_LIMIT) {
                    setEtError(receivedAmountEt,getString(R.string.amount_error, (totalCharges) + TOP_UP_LIMIT));
                } else if (Integer.parseInt(editable.toString()) > AMOUNT_LIMIT) {
                    setEtError(receivedAmountEt,getString(R.string.amount_error, AMOUNT_LIMIT));
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
        if(tripInfo != null){
            Dialogs.INSTANCE.showLoader(mCurrentActivity);
            int receivedAmount = 0;
            try {
                receivedAmount = Integer.parseInt(receivedAmountEt.getText().toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            //submit delivery feedback
            if (tripInfo.getTripStatusCode() == Constants.TRIP_STATUS_CODE_DELIVERY) {
                repository.requestMultiDeliveryDriverFeedback(
                        tripInfo.getId(),
                        receivedAmount,
                        callerRb.getRating(),
                        true,
                        selectedMsgPosition == 0,
                        Utils.getDeliveryMsgsList(mCurrentActivity)[selectedMsgPosition],
                        etReceiverName.getText().toString(),
                        etReceiverMobileNo.getText().toString(),
                        handler);
            }
            //submit ride feedback
            else {
                repository.requestMultiDeliveryDriverFeedback(
                        tripInfo.getId(),
                        receivedAmount,
                        callerRb.getRating(),
                        false, false, null, null, null, handler);
            }
        }
    }


    /**
     * Set Edit text error
     *
     * @param error The error message.
     */
    private void setEtError(EditText editText, String error) {
        editText.setError(error);
        editText.requestFocus();
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
     * @return true if all the validation is true otherwise false
     */
    private boolean isValid() {
        String charges = StringUtils.EMPTY;
        try {
            charges = String.valueOf(totalCharges);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        //require receiver name and phone number if feedback screen is for delivery ride
        if(tripInfo != null && tripInfo.getTripStatusCode() == Constants.TRIP_STATUS_CODE_DELIVERY &&
                selectedMsgPosition == Constants.KAMYAB_DELIVERY && llReceiverInfo.getVisibility() == View.VISIBLE){
            if(StringUtils.isBlank(etReceiverName.getText().toString())){
                setEtError(etReceiverName,getString(R.string.receiver_name));
                return false;
            }
            if(!Utils.isValidNumber(etReceiverMobileNo)){
                setEtError(etReceiverMobileNo,getString(R.string.error_phone_number_1));
                return false;
            }

        }
        if(selectedMsgPosition != Constants.KAMYAB_DELIVERY && StringUtils.isNotBlank(etReceiverMobileNo.getText().toString())
                && !Utils.isValidNumber(etReceiverMobileNo)){
            setEtError(etReceiverMobileNo,getString(R.string.error_phone_number_1));
            return false;
        }
        if (!receivedAmountEt.getText().toString().matches(Constants.REG_EX_DIGIT)) {
            setEtError(receivedAmountEt,getString(R.string.error_invalid_amount));
            return false;
        } else if (charges.matches(Constants.REG_EX_DIGIT)
                && Integer.parseInt(receivedAmountEt.getText().toString()) < totalCharges) {
            setEtError(receivedAmountEt,getString(R.string.error_amount_greater_than_total));
            return false;
        } else if (charges.matches(Constants.REG_EX_DIGIT)
                && Integer.parseInt(receivedAmountEt.getText().toString()) >
                (totalCharges + TOP_UP_LIMIT)) {
            setEtError(receivedAmountEt,getString(R.string.amount_error, (totalCharges + TOP_UP_LIMIT)));
            return false;
        } else if (Integer.parseInt(receivedAmountEt.getText().toString()) > AMOUNT_LIMIT) {
            setEtError(receivedAmountEt,getString(R.string.amount_error, AMOUNT_LIMIT));
            return false;
        } else if (callerRb.getRating() <= 0.0) {
            Dialogs.INSTANCE.showError(mCurrentActivity, feedbackBtn,
                    getString(R.string.passenger_rating));
            return false;
        } else if (StringUtils.isNotBlank(receivedAmountEt.getText().toString())) {
            try {
                int receivedPrice = Integer.parseInt(receivedAmountEt.getText().toString());
                if (receivedPrice < 0) {
                    setEtError(receivedAmountEt,getString(R.string.amount_not_acceptable));
                    return false;
                }
            } catch (Exception e) {
                setEtError(receivedAmountEt,getString(R.string.amount_not_acceptable));
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
        public void onMultiDeliveryDriverFeedback(final MultiDeliveryFeedbackResponse response) {
            if (mCurrentActivity != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (tripInfo.getId().equalsIgnoreCase(response.getData().getTripID())) {
                            Dialogs.INSTANCE.dismissDialog();
                            MultiDeliveryCallDriverData callDriverData = AppPreferences.
                                    getMultiDeliveryCallDriverData();

                            MultipleDeliveryBookingResponse bookingResponse = callDriverData.
                                    getTripById(tripInfo.getId());

                            if (bookingResponse != null) {
                                bookingResponse.getTrip().setStatus(TripStatus.ON_FEEDBACK_TRIP);
                                AppPreferences.setMultiDeliveryCallDriverData(callDriverData);
                                checkForUnfinishedTrip(callDriverData);
                                Utils.redLog(TAG, new Gson().toJson(callDriverData));

                            }

                        }
                    }
                });
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

    /**
     * Check for un finished trip.
     *
     * <p>If batch have the unfinished trip navigate to Multi Delivery
     * Booking Screen other wise navigate to home screen</p>
     *
     * @param callDriverData The Multi Delivery Batch Object.
     */
    private void checkForUnfinishedTrip(MultiDeliveryCallDriverData callDriverData) {

        boolean isUnFinishedTripRemainig = callDriverData.
                isUnfinishedTripRemaining(callDriverData);
        if (isUnFinishedTripRemainig) {
            ActivityStackManager.getInstance()
                    .startMultiDeliveryBookingActivity(mCurrentActivity);
        } else {
            Utils.multiDeliveryFreeDriverOnBatchComplete();
            ActivityStackManager.getInstance().startHomeActivity(true,
                    mCurrentActivity);
            mCurrentActivity.finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * overrided because user must complete feedback without going back
     * user cannot go back to previous screen
     * if Feedback screen is not coming from ongoing complete ride screen then this will behave normally.
     *
     */
    @Override
    public void onBackPressed() {
        if(!isComingFromOnGoingRide)
            super.onBackPressed();
    }

    /**
     * update UI according to delivery feedback
     */
    private void updateUIICODelivery() {
        llReceiverInfo.setVisibility(View.VISIBLE);

        rlDeliveryStatus.setVisibility(View.VISIBLE);
        tvAmountToGetLable.setText(getString(R.string.total_urdu));
        ivRight0.setImageDrawable(Utils.changeDrawableColor(mCurrentActivity, R.drawable.polygon, R.color.blue_dark));
        initAdapter(tripInfo);

        if(tripInfo.getDeliveryInfo() != null){
            if (tripInfo.getDeliveryInfo().isCashOnDelivery()) {
                cashOnDeliveryRL.setVisibility(View.VISIBLE);
                cashOnDeliveryTV.setText(getString(R.string.display_integer_value,tripInfo.getDeliveryInfo().getAmount()));
            } else {
                cashOnDeliveryRL.setVisibility(View.GONE);
            }
        }

        receivedAmountEt.clearFocus();
        etReceiverName.requestFocus();
    }

    /**
     * initialize delivery feedback message in dropdown list
     * @param tripInfo current trip info
     */
    private void initAdapter(final MultiDeliveryTrip tripInfo) {

        final DeliveryMsgsSpinnerAdapter adapter = new DeliveryMsgsSpinnerAdapter(mCurrentActivity, Utils.getDeliveryMsgsList(mCurrentActivity));


        spDeliveryStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, final View view, final int position, long id) {

                if (view != null) {
                    view.findViewById(R.id.tvItem).setPadding(0, 0, (int) mCurrentActivity.getResources().getDimension(R.dimen._34sdp), 0);
                } else {
                    final ViewTreeObserver layoutObserver = spDeliveryStatus.getViewTreeObserver();
                    layoutObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            View selectedView = spDeliveryStatus.getSelectedView();
                            if (selectedView != null) {
                                selectedView.findViewById(R.id.tvItem).setPadding(0, 0, (int) mCurrentActivity.getResources().getDimension(R.dimen._34sdp), 0);
                            }
                            spDeliveryStatus.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    });
                }
                selectedMsgPosition = position;
                if (tripInfo.getDeliveryInfo() != null && tripInfo.getDeliveryInfo().isCashOnDelivery()) {
                    if (position == 0) {
                        TOP_UP_LIMIT = AppPreferences.getSettings().getSettings().getTop_up_limit() + tripInfo.getDeliveryInfo().getAmount();
                        cashOnDeliveryTV.setPaintFlags(cashOnDeliveryTV.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        totalCharges = (invoice.getTotal() + tripInfo.getDeliveryInfo().getAmount());
                    } else {
                        TOP_UP_LIMIT = AppPreferences.getSettings().getSettings().getTop_up_limit();
                        cashOnDeliveryTV.setPaintFlags(cashOnDeliveryTV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        totalCharges = invoice.getTotal();
                    }
                    tvAmountToGet.setText(Utils.getCommaFormattedAmount(totalCharges));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spDeliveryStatus.setAdapter(adapter);
        spDeliveryStatus.setSelection(0);
    }
}

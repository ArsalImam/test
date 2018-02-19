package com.bykea.pk.partner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.view.View;
import android.widget.RatingBar;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.response.FeedbackResponse;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.NumericKeyBoardTransformationMethod;
import com.bykea.pk.partner.utils.Permissions;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.CircularImageView;
import com.bykea.pk.partner.widgets.FontEditText;
import com.bykea.pk.partner.widgets.FontTextView;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class FeedbackActivity extends BaseActivity {

    /* @Bind(R.id.logo)
     ImageView logo;*/
    @Bind(R.id.tvTripId)
    FontTextView tvTripId;
    @Bind(R.id.startAddressTv)
    FontTextView startAddressTv;
    @Bind(R.id.invoiceMsgTv)
    FontTextView invoiceMsgTv;
    @Bind(R.id.endAddressTv)
    FontTextView endAddressTv;
    @Bind(R.id.callerIv)
    CircularImageView callerIv;
    @Bind(R.id.callerNameTv)
    FontTextView callerNameTv;
    @Bind(R.id.totalAmountTv)
    FontTextView totalAmountTv;

    @Bind(R.id.tvTotalTime)
    FontTextView tvTotalTime;

    @Bind(R.id.tvTotalDistance)
    FontTextView tvTotalDistance;
    @Bind(R.id.receivedAmountEt)
    FontEditText receivedAmountEt;
    @Bind(R.id.callerRb)
    RatingBar callerRb;
    @Bind(R.id.ratingValueTv)
    FontTextView ratingValueTv;
    @Bind(R.id.feedbackBtn)
    FontTextView feedbackBtn;

    private FeedbackActivity mCurrentActivity;
    private String totalCharges = StringUtils.EMPTY;
    private int TOP_UP_LIMIT, AMOUNT_LIMIT;


    private MixpanelAPI mixpanelAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ButterKnife.bind(this);
        initViews();

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            Dialogs.INSTANCE.showLocationSettings(mCurrentActivity, Permissions.LOCATION_PERMISSION);
        mixpanelAPI = MixpanelAPI.getInstance(mCurrentActivity, Constants.MIX_PANEL_API_KEY);
    }

    @OnTextChanged(value = R.id.receivedAmountEt,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterTextChanged(Editable editable) {
        if (StringUtils.isNotBlank(editable) && StringUtils.isNotBlank(totalCharges)) {
            if (editable.toString().matches(Constants.REG_EX_DIGIT)) {
                if (Integer.parseInt(editable.toString()) > (Integer.parseInt(totalCharges) + TOP_UP_LIMIT)) {
                    setEtError("Amount can't be more than " + (Integer.parseInt(totalCharges) + TOP_UP_LIMIT));
                } else if (Integer.parseInt(editable.toString()) > AMOUNT_LIMIT) {
                    setEtError("Amount can't be more than " + AMOUNT_LIMIT);
                }
            } else {
                Utils.appToast(mCurrentActivity, "Please enter valid amount.");
            }
        }
    }

    private void initViews() {
        mCurrentActivity = this;

        NormalCallData callData = AppPreferences.getCallData();
        boolean isSendType = StringUtils.containsIgnoreCase(callData.getCallType(), "Send") || StringUtils.containsIgnoreCase(callData.getCallType(), "Delivery");
        boolean isBringType = StringUtils.containsIgnoreCase(callData.getCallType(), "Bring") || StringUtils.containsIgnoreCase(callData.getCallType(), "Purchase");
        receivedAmountEt.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        tvTripId.setText(callData.getTripNo());
        totalCharges = callData.getTotalFare();
        TOP_UP_LIMIT = AppPreferences.getSettings().getSettings().getTop_up_limit();
        if (StringUtils.isNotBlank(callData.getCodAmountNotFormatted())) {
            String amount = callData.getCodAmountNotFormatted();
            if (isSendType) {
                if (callData.isCod()) {
                    TOP_UP_LIMIT = TOP_UP_LIMIT + Integer.parseInt(amount);
                }
            } else if (!isBringType) {
                TOP_UP_LIMIT = TOP_UP_LIMIT + Integer.parseInt(amount);
            }
        }
        AMOUNT_LIMIT = AppPreferences.getSettings().getSettings().getAmount_limit();
        totalAmountTv.setText("Rs. " + (StringUtils.isNotBlank(totalCharges) ? totalCharges : "N/A"));
        startAddressTv.setText(callData.getStartAddress());
        tvTotalDistance.setText(callData.getDistanceCovered() + " km");
        tvTotalTime.setText(callData.getTotalMins() + " mins");
        endAddressTv.setText((StringUtils.isBlank(callData.getEndAddress())
                ? "N/A" : callData.getEndAddress()));
        callerNameTv.setText(callData.getPassName());
        String invoiceMsg = StringUtils.EMPTY;
        if (StringUtils.isNotBlank(callData.getPromo_deduction()) && Double.parseDouble(callData.getPromo_deduction()) > 0
                && StringUtils.isNotBlank(callData.getWallet_deduction()) && Double.parseDouble(callData.getWallet_deduction()) > 0) {
            invoiceMsg = "Promo + Wallet Deduction";
        } else if (StringUtils.isNotBlank(callData.getPromo_deduction()) && Double.parseDouble(callData.getPromo_deduction()) > 0) {
            invoiceMsg = "Promo Deduction";
        } else if (StringUtils.isNotBlank(callData.getWallet_deduction()) && Double.parseDouble(callData.getWallet_deduction()) > 0) {
            invoiceMsg = "Wallet Deduction";
        }
        if (StringUtils.isNotBlank(invoiceMsg)) {
            invoiceMsgTv.setVisibility(View.VISIBLE);
            invoiceMsgTv.setText(invoiceMsg);
        } else {
            invoiceMsgTv.setVisibility(View.INVISIBLE);
        }
        callerRb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingValueTv.setText(Utils.formatDecimalPlaces(rating + ""));
            }
        });
        receivedAmountEt.requestFocus();
    }

    private long mLastClickTime;

    @OnClick(R.id.feedbackBtn)
    public void onClick() {
        if (mLastClickTime != 0 && (SystemClock.elapsedRealtime() - mLastClickTime < 1000)) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        if (valid()) {
            Dialogs.INSTANCE.showLoader(mCurrentActivity);
            logMPEvent();
            new UserRepository().requestFeedback(mCurrentActivity, handler,
                    "Nice driver", callerRb.getRating() + "", receivedAmountEt.getText().toString());
        }
    }

    private void logMPEvent() {
        try {
            NormalCallData callData = AppPreferences.getCallData();
            mixpanelAPI.identify(callData.getPassId());
            mixpanelAPI.getPeople().identify(callData.getPassId());
            JSONObject revenue = new JSONObject();
            revenue.put("$time", Utils.getUTCCurrentDate());
            revenue.put("TripID", callData.getTripId());
            revenue.put("TripNo", callData.getTripNo());
            revenue.put("PassengerID", callData.getPassId());
            revenue.put("DriverID", AppPreferences.getPilotData().getId());
            mixpanelAPI.getPeople().trackCharge(Double.parseDouble(callData.getTrip_charges()), revenue);
            mixpanelAPI.getPeople().increment("Total Trips", 1L);


            JSONObject properties = new JSONObject();
            properties.put("TripID", callData.getTripId());
            properties.put("TripNo", callData.getTripNo());
            properties.put("PassengerID", callData.getPassId());
            properties.put("DriverID", AppPreferences.getPilotData().getId());
            properties.put("Amount", callData.getTrip_charges());
            properties.put("AmountEntered", receivedAmountEt.getText().toString());
            properties.put("Time", callData.getTotalMins() + "");
            properties.put("KM", callData.getDistanceCovered());
            properties.put("type", callData.getCallType());
            properties.put("timestamp", Utils.getIsoDate());
            properties.put("City", AppPreferences.getPilotData().getCity().getName());

            properties.put("PassengerName", callData.getPassName());
            properties.put("DriverName", AppPreferences.getPilotData().getFullName());
            if (StringUtils.isNotBlank(callData.getPromo_deduction())) {
                properties.put("PromoDeduction", callData.getPromo_deduction());
            } else {
                properties.put("PromoDeduction", "0");
            }
            if (StringUtils.isNotBlank(callData.getWallet_deduction())) {
                properties.put("WalletDeduction", callData.getWallet_deduction());
            } else {
                properties.put("WalletDeduction", "0");
            }
            Utils.logEvent(mCurrentActivity, callData.getPassId(), Constants.AnalyticsEvents.RIDE_FARE.replace(
                    Constants.AnalyticsEvents.REPLACE, callData.getCallType()), properties);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private UserDataHandler handler = new UserDataHandler() {
        @Override
        public void onFeedback(final FeedbackResponse feedbackResponse) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Dialogs.INSTANCE.dismissDialog();
                    Dialogs.INSTANCE.showToast(mCurrentActivity, feedbackResponse.getMessage());
                    Utils.setCallIncomingState();
                    AppPreferences.setWalletAmountIncreased(!feedbackResponse.isAvailable());
                    AppPreferences.setAvailableStatus(feedbackResponse.isAvailable());
                    ActivityStackManager.getInstance().startHomeActivity(true, mCurrentActivity);
                    mCurrentActivity.finish();
                }
            });

        }

        @Override
        public void onError(final int errorCode, final String errorMessage) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Dialogs.INSTANCE.dismissDialog();
                    if (errorCode == HTTPStatus.UNAUTHORIZED) {
                        Intent locationIntent = new Intent(Keys.UNAUTHORIZED_BROADCAST);
                        sendBroadcast(locationIntent);
                    } else {
                        Dialogs.INSTANCE.showError(mCurrentActivity, feedbackBtn, errorMessage);
                    }
                }
            });

        }
    };

    private boolean valid() {
        if (StringUtils.isBlank(receivedAmountEt.getText().toString())) {
            setEtError("Enter received amount");
            return false;
        } else if (!receivedAmountEt.getText().toString().matches(Constants.REG_EX_DIGIT)) {
            setEtError("Invalid amount");
            return false;
        } else if (Constants.REG_EX_DIGIT.matches(totalCharges)
                && Integer.parseInt(receivedAmountEt.getText().toString()) < Integer.parseInt(totalCharges)) {
            setEtError("Amount can't be less than Total Charges");
            return false;
        } else if (Constants.REG_EX_DIGIT.matches(totalCharges)
                && Integer.parseInt(receivedAmountEt.getText().toString()) > (Integer.parseInt(totalCharges) + TOP_UP_LIMIT)) {
            setEtError("Amount can't be more than " + (Integer.parseInt(totalCharges) + TOP_UP_LIMIT));
            return false;
        } else if (Integer.parseInt(receivedAmountEt.getText().toString()) > AMOUNT_LIMIT) {
            setEtError("Amount can't be more than " + AMOUNT_LIMIT);
            return false;
        } else if (callerRb.getRating() <= 0.0) {
            Dialogs.INSTANCE.showError(mCurrentActivity, feedbackBtn, "Please Rate Passenger.");
            return false;
        } else if (StringUtils.isNotBlank(receivedAmountEt.getText().toString())) {
            try {
                int receivedPrice = Integer.parseInt(receivedAmountEt.getText().toString());
                if (receivedPrice < 0) {
                    setEtError("Amount is not acceptable");
                    return false;
                }
            } catch (Exception e) {
                setEtError("Amount is not acceptable");
                return false;
            }
        }
        return true;
    }

    private void setEtError(String error) {
        receivedAmountEt.setError(error);
        receivedAmountEt.requestFocus();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
//        Utils.flushMixPanelEvent(mCurrentActivity);
        super.onDestroy();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Permissions.LOCATION_PERMISSION) {
            LocationManager locationManager;
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                Dialogs.INSTANCE.showLocationSettings(mCurrentActivity, Permissions.LOCATION_PERMISSION);
            else {
                ActivityStackManager.getInstance().startLocationService(mCurrentActivity);
            }
        }
    }
}

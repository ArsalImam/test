package com.bykea.pk.partner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.response.FeedbackResponse;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.adapters.DeliveryMsgsSpinnerAdapter;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.NumericKeyBoardTransformationMethod;
import com.bykea.pk.partner.utils.Permissions;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontEditText;
import com.bykea.pk.partner.widgets.FontTextView;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
    //    @Bind(R.id.callerNameTv)
//    FontTextView callerNameTv;
    @Bind(R.id.totalAmountTv)
    FontTextView totalAmountTv;

    @Bind(R.id.tvTotalTime)
    FontTextView tvTotalTime;

    @Bind(R.id.tvTotalDistance)
    FontTextView tvTotalDistance;
    @Bind(R.id.receivedAmountEt)
    FontEditText receivedAmountEt;
    @Bind(R.id.llKharedari)
    LinearLayout llKharedari;
    @Bind(R.id.llTotal)
    LinearLayout llTotal;
    @Bind(R.id.callerRb)
    RatingBar callerRb;
    //    @Bind(R.id.ratingValueTv)
//    FontTextView ratingValueTv;
    @Bind(R.id.feedbackBtn)
    ImageView feedbackBtn;

    @Bind(R.id.tvWalletDeduction)
    FontTextView tvWalletDeduction;
    @Bind(R.id.tvAmountToGet)
    FontTextView tvAmountToGet;
    @Bind(R.id.rlWalletDeduction)
    RelativeLayout rlWalletDeduction;
    @Bind(R.id.rlPromoDeduction)
    RelativeLayout rlPromoDeduction;
    @Bind(R.id.tvPromoDeduction)
    FontTextView tvPromoDeduction;
    @Bind(R.id.tvCOD)
    FontTextView tvCOD;
    @Bind(R.id.tvAmountToGetLable)
    FontTextView tvAmountToGetLable;
    @Bind(R.id.totalAmountTvLable)
    FontTextView totalAmountTvLable;
    @Bind(R.id.rlCOD)
    RelativeLayout rlCOD;
    @Bind(R.id.rlDeliveryStatus)
    RelativeLayout rlDeliveryStatus;
    @Bind(R.id.spDeliveryStatus)
    Spinner spDeliveryStatus;
    @Bind(R.id.llReceiverInfo)
    LinearLayout llReceiverInfo;
    @Bind(R.id.ivRight0)
    ImageView ivRight0;
    @Bind(R.id.etReceiverName)
    FontEditText etReceiverName;
    @Bind(R.id.etReceiverMobileNo)
    FontEditText etReceiverMobileNo;
    @Bind(R.id.kharedariAmountEt)
    FontEditText kharedariAmountEt;

    @Bind(R.id.rlDropOffDiscount)
    RelativeLayout rlDropOffDiscount;

    @Bind(R.id.tvDropOffDiscount)
    FontTextView tvDropOffDiscount;


    private FeedbackActivity mCurrentActivity;
    private String totalCharges = StringUtils.EMPTY, lastKhareedariAmount = StringUtils.EMPTY;
    private int TOP_UP_LIMIT, AMOUNT_LIMIT;


    private MixpanelAPI mixpanelAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_new);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ButterKnife.bind(this);
        initViews();

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
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

    private boolean isDeliveryType, isPurchaseType;
    private NormalCallData callData;

    private void initViews() {
        mCurrentActivity = this;

        callData = AppPreferences.getCallData();
        isDeliveryType = Utils.isDeliveryService(callData.getCallType());
        isPurchaseType = Utils.isPurchaseService(callData.getCallType());
        receivedAmountEt.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        tvTripId.setText(callData.getTripNo());
        if (StringUtils.isNotBlank(callData.getTotalFare())) {
            totalCharges = callData.getTotalFare();
        }
        TOP_UP_LIMIT = AppPreferences.getSettings().getSettings().getTop_up_limit();
        /*if (StringUtils.isNotBlank(callData.getCodAmountNotFormatted())) {
            String amount = callData.getCodAmountNotFormatted();
            if (isDeliveryType) {
                if (callData.isCod()) {
                    TOP_UP_LIMIT = TOP_UP_LIMIT + Integer.parseInt(amount);
                }
            } else if (!isPurchaseType) {
                TOP_UP_LIMIT = TOP_UP_LIMIT + Integer.parseInt(amount);
            }
        }*/
        AMOUNT_LIMIT = AppPreferences.getSettings().getSettings().getAmount_limit();
//        totalAmountTv.setText((StringUtils.isNotBlank(totalCharges) ? totalCharges : "N/A"));
        totalAmountTv.setText((StringUtils.isNotBlank(callData.getTrip_charges()) ? callData.getTrip_charges() : "N/A"));
        startAddressTv.setText(callData.getStartAddress());
        tvTotalDistance.setText("(" + callData.getDistanceCovered() + " km, ");
        tvTotalTime.setText(callData.getTotalMins() + " mins)");
        endAddressTv.setText((StringUtils.isBlank(callData.getEndAddress())
                ? "N/A" : callData.getEndAddress()));
//        callerNameTv.setText(callData.getPassName());
        if (StringUtils.isNotBlank(callData.getPromo_deduction()) && Double.parseDouble(callData.getPromo_deduction()) > 0) {
            tvPromoDeduction.setText(callData.getPromo_deduction());
        } else {
            rlPromoDeduction.setVisibility(View.GONE);
        }
        if (StringUtils.isNotBlank(callData.getWallet_deduction()) && Double.parseDouble(callData.getWallet_deduction()) > 0) {
            tvWalletDeduction.setText(callData.getWallet_deduction());
        } else {
            rlWalletDeduction.setVisibility(View.GONE);
        }
        tvAmountToGet.setText(Utils.getCommaFormattedAmount(totalCharges));

        if (StringUtils.isNotBlank(callData.getDropoff_discount())) {
            rlDropOffDiscount.setVisibility(View.VISIBLE);
            tvDropOffDiscount.setText(callData.getDropoff_discount());
        }
        if (isDeliveryType) {
            receivedAmountEt.requestFocus();
            updateUIICODelivery();
        } else if (isPurchaseType) {
            updateUIforPurcahseService();
        } else {
            receivedAmountEt.requestFocus();
        }
    }

    private void updateUIforPurcahseService() {
        totalAmountTvLable.setText(" کرائے کی کمائی");
        receivedAmountEt.clearFocus();
        llKharedari.setVisibility(View.VISIBLE);
        kharedariAmountEt.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        kharedariAmountEt.requestFocus();
        initKhareedadiSuggestion();
        kharedariAmountEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (StringUtils.isNotBlank(kharedariAmountEt.getText().toString())
                        && !kharedariAmountEt.getText().toString().equalsIgnoreCase(lastKhareedariAmount)) {
                    lastKhareedariAmount = kharedariAmountEt.getText().toString();
                    totalCharges = "" + (Integer.parseInt(lastKhareedariAmount) + Integer.parseInt(callData.getTotalFare()));
                    receivedAmountEt.setHint("Suggested Rs. " + Utils.getCommaFormattedAmount(totalCharges));
                } else if (StringUtils.isBlank(kharedariAmountEt.getText().toString())) {
                    initKhareedadiSuggestion();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initKhareedadiSuggestion() {
        lastKhareedariAmount = StringUtils.EMPTY;
        totalCharges = callData.getTotalFare();
        receivedAmountEt.setHint("Suggested Rs. " + Utils.getCommaFormattedAmount(totalCharges));
    }

    private ArrayList<String> getDeliveryMsgsList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("کامیاب ڈیلیوری");
        list.add("ناكام - وصول کرنے والا رابطے میں نہیں");
        list.add("ناكام - وصول کرنے والا موجود نہیں ہے");
        list.add("ناكام - پارسل وصول کرنے سے انکار");
        list.add("ناكام - پتہ نہیں ملا");
        return list;
    }

    private void updateUIICODelivery() {
        llReceiverInfo.setVisibility(View.VISIBLE);
        llReceiverInfo.setPadding(0, 0, 0, (int) mCurrentActivity.getResources().getDimension(R.dimen._8sdp));

        rlDeliveryStatus.setVisibility(View.VISIBLE);
        tvAmountToGetLable.setText(" ٹوٹل");
        ivRight0.setImageDrawable(Utils.changeDrawableColor(mCurrentActivity, R.drawable.polygon, R.color.blue_dark));
        initAdapter(callData);

        if (StringUtils.isNotBlank(callData.getCodAmount()) && callData.isCod()) {
            rlCOD.setVisibility(View.VISIBLE);
            tvCOD.setText(callData.getCodAmount());
        } else {
            rlCOD.setVisibility(View.GONE);
        }
    }

    private int selectedMsgPosition = 0;

    private void initAdapter(final NormalCallData callData) {

        final DeliveryMsgsSpinnerAdapter adapter = new DeliveryMsgsSpinnerAdapter(mCurrentActivity, getDeliveryMsgsList());


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
                if (StringUtils.isNotBlank(callData.getCodAmount()) && callData.isCod()) {
                    if (position == 0) {
//                        TOP_UP_LIMIT = AppPreferences.getSettings().getSettings().getTop_up_limit() + Integer.parseInt(callData.getCodAmountNotFormatted());
                        tvCOD.setPaintFlags(tvCOD.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        totalCharges = "" + (Integer.parseInt(callData.getTotalFare()) + Integer.parseInt(callData.getCodAmountNotFormatted()));
                    } else {
//                        TOP_UP_LIMIT = AppPreferences.getSettings().getSettings().getTop_up_limit();
                        tvCOD.setPaintFlags(tvCOD.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        totalCharges = callData.getTotalFare();
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

    private long mLastClickTime;

    @OnClick(R.id.feedbackBtn)
    public void onClick() {
        if (mLastClickTime != 0 && (SystemClock.elapsedRealtime() - mLastClickTime < 1000)) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        /*if (isPurchaseType && llTotal.getVisibility() != View.VISIBLE && StringUtils.isNotBlank(kharedariAmountEt.getText().toString())) {
            llTotal.setVisibility(View.VISIBLE);
        } else */
        if (valid()) {
            Dialogs.INSTANCE.showLoader(mCurrentActivity);
            logMPEvent();
            if (isDeliveryType) {
                new UserRepository().requestFeedback(mCurrentActivity, handler,
                        "Nice driver", callerRb.getRating() + "", receivedAmountEt.getText().toString()
                        , selectedMsgPosition == 0, getDeliveryMsgsList().get(selectedMsgPosition), etReceiverName.getText().toString(),
                        etReceiverMobileNo.getText().toString());
            } else if (isPurchaseType) {
                new UserRepository().requestFeedback(mCurrentActivity, handler,
                        "Nice driver", callerRb.getRating() + "", receivedAmountEt.getText().toString(),
                        kharedariAmountEt.getText().toString());
            } else {
                new UserRepository().requestFeedback(mCurrentActivity, handler,
                        "Nice driver", callerRb.getRating() + "", receivedAmountEt.getText().toString());
            }
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
                        EventBus.getDefault().post(Keys.UNAUTHORIZED_BROADCAST);
                    } else {
                        Dialogs.INSTANCE.showError(mCurrentActivity, feedbackBtn, errorMessage);
                    }
                }
            });

        }
    };

    private boolean valid() {
        if (isPurchaseType && StringUtils.isBlank(kharedariAmountEt.getText().toString())) {
            kharedariAmountEt.setError("Enter amount");
            kharedariAmountEt.requestFocus();
            return false;
        } else if (StringUtils.isBlank(receivedAmountEt.getText().toString())) {
            setEtError("Enter received amount");
            return false;
        } else if (isDeliveryType /*&& selectedMsgPosition == 0*/ && StringUtils.isBlank(etReceiverName.getText().toString())) {
            etReceiverName.setError("Required");
            etReceiverName.requestFocus();
            return false;
        } else if ((isDeliveryType || isPurchaseType) && StringUtils.isNotBlank(etReceiverMobileNo.getText().toString())
                && !Utils.isValidNumber(mCurrentActivity, etReceiverMobileNo)) {
            return false;
        } else if (!receivedAmountEt.getText().toString().matches(Constants.REG_EX_DIGIT)) {
            setEtError("Invalid amount");
            return false;
        } else if (totalCharges.matches(Constants.REG_EX_DIGIT)
                && Integer.parseInt(receivedAmountEt.getText().toString()) < Integer.parseInt(totalCharges)) {
            setEtError("Amount can't be less than Total Charges");
            return false;
        } else if (totalCharges.matches(Constants.REG_EX_DIGIT)
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

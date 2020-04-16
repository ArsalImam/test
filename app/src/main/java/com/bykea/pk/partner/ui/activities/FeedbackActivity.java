package com.bykea.pk.partner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.dal.source.JobsDataSource;
import com.bykea.pk.partner.dal.source.JobsRepository;
import com.bykea.pk.partner.dal.source.remote.response.ConcludeJobBadResponse;
import com.bykea.pk.partner.dal.util.Injection;
import com.bykea.pk.partner.models.response.DriverPerformanceResponse;
import com.bykea.pk.partner.models.response.FeedbackResponse;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.StringCallBack;
import com.bykea.pk.partner.ui.helpers.adapters.DeliveryMsgsSpinnerAdapter;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.NumericKeyBoardTransformationMethod;
import com.bykea.pk.partner.utils.Permissions;
import com.bykea.pk.partner.utils.Util;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.utils.audio.BykeaAmazonClient;
import com.bykea.pk.partner.widgets.FontEditText;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class FeedbackActivity extends BaseActivity {

    private final String PERMISSION = "android.permission.CAMERA";
    /* @BindView(R.id.logo)
     ImageView logo;*/
    @BindView(R.id.tvTripId)
    FontTextView tvTripId;
    @BindView(R.id.ivTakeImage)
    ImageView ivTakeImage;
    @BindView(R.id.ivEyeView)
    ImageView ivEyeView;
    @BindView(R.id.startAddressTv)
    FontTextView startAddressTv;
    @BindView(R.id.invoiceMsgTv)
    FontTextView invoiceMsgTv;
    @BindView(R.id.ic_pin)
    View ic_pin;
    @BindView(R.id.addressDivider)
    View addressDivider;
    @BindView(R.id.dotted_line)
    View dotted_line;
    @BindView(R.id.endAddressTv)
    FontTextView endAddressTv;
    //    @BindView(R.id.callerNameTv)
//    FontTextView callerNameTv;
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
    //    @BindView(R.id.ratingValueTv)
//    FontTextView ratingValueTv;
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
    @BindView(R.id.tvPayment)
    FontTextView tvPayment;
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


    private FeedbackActivity mCurrentActivity;
    private String totalCharges = StringUtils.EMPTY, lastKhareedariAmount = StringUtils.EMPTY;
    private int PARTNER_TOP_UP_NEGATIVE_LIMIT, AMOUNT_LIMIT, PARTNER_TOP_UP_POSITIVE_LIMIT;
    private JobsRepository repo;

    int driverWallet;
    private boolean isJobSuccessful = true;
    private File imageUri;
    private File tempUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_new);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ButterKnife.bind(this);
        try {
            driverWallet = Integer.parseInt(((DriverPerformanceResponse) AppPreferences.getObjectFromSharedPref(DriverPerformanceResponse.class)).getData().getTotalBalance());
        } catch (Exception e) {
            driverWallet = -1;
        }
        initViews();

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            Dialogs.INSTANCE.showLocationSettings(mCurrentActivity, Permissions.LOCATION_PERMISSION);
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
                //moveScrollViewToBottom();
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
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterTextChanged(Editable editable) {
        if (StringUtils.isNotBlank(editable) && StringUtils.isNotBlank(totalCharges)) {
            if (editable.toString().matches(Constants.REG_EX_DIGIT)) {
                if (driverWallet <= PARTNER_TOP_UP_NEGATIVE_LIMIT
                        && Integer.parseInt(editable.toString()) >= (Integer.parseInt(totalCharges) + PARTNER_TOP_UP_NEGATIVE_LIMIT + Constants.DIGIT_ONE) &&
                        !Util.INSTANCE.isBykeaCashJob(callData.getServiceCode())) {
                    //WHEN THE WALLET IS LESS THAN ZERO, RECEIVED AMOUNT CAN NOT BE GREATER THAN THE SUM OF (TOTAL CHARGES AND PARTNER TOP UP NEGATIVE LIMIT)
                    setEtError(getString(R.string.amount_error, (Integer.parseInt(totalCharges) + PARTNER_TOP_UP_NEGATIVE_LIMIT)));
                } else if ((driverWallet > PARTNER_TOP_UP_NEGATIVE_LIMIT && driverWallet < PARTNER_TOP_UP_POSITIVE_LIMIT) &&
                        Integer.parseInt(editable.toString()) >= (Integer.parseInt(totalCharges) + driverWallet + Constants.DIGIT_ONE) &&
                        !Util.INSTANCE.isBykeaCashJob(callData.getServiceCode())) {
                    //WHEN THE WALLET IS GREATER THAN ZERO BUT LESS THAN THE MAX POSITIVE TOP UP LIMIT,
                    //RECEIVED AMOUNT CAN NOT BE GREATER THAN THE SUM OF (TOTAL CHARGES AND WALLET)
                    setEtError(getString(R.string.amount_error, (Integer.parseInt(totalCharges) + driverWallet)));
                } else if ((Util.INSTANCE.isBykeaCashJob(callData.getServiceCode()) || driverWallet >= PARTNER_TOP_UP_POSITIVE_LIMIT) &&
                        Integer.parseInt(editable.toString()) >= (Integer.parseInt(totalCharges) + PARTNER_TOP_UP_POSITIVE_LIMIT + Constants.DIGIT_ONE)) {
                    //WHEN THE WALLET IS GREATER THAN MAX POSITIVE TOP UP LIMIT,
                    //RECEIVED AMOUNT CAN NOT BE GREATER THAN THE SUM OF (TOTAL CHARGES AND PARTNER TOP UP POSITIVE LIMIT)
                    setEtError(getString(R.string.amount_error, (Integer.parseInt(totalCharges) + PARTNER_TOP_UP_POSITIVE_LIMIT)));
                } else if (Integer.parseInt(editable.toString()) >= (AMOUNT_LIMIT + Constants.DIGIT_ONE)) {
                    setEtError(getString(R.string.amount_error, AMOUNT_LIMIT));
                }
            } else {
                Utils.appToast(getString(R.string.invalid_amout));
            }
        }
    }

    private boolean isBykeaCashType, isDeliveryType, isOfflineDeliveryType, isPurchaseType;
    private NormalCallData callData;

    private void initViews() {
        mCurrentActivity = this;

        callData = AppPreferences.getCallData();
        isBykeaCashType = Util.INSTANCE.isBykeaCashJob(callData.getServiceCode());
        isDeliveryType = Utils.isDeliveryService(callData.getCallType());
        isOfflineDeliveryType = callData.getServiceCode() != null && callData.getServiceCode() == Constants.ServiceCode.OFFLINE_DELIVERY;
        isPurchaseType = Utils.isPurchaseService(callData.getCallType(), callData.getServiceCode());
        etReceiverMobileNo.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        receivedAmountEt.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        tvTripId.setText(callData.getTripNo());
        if (StringUtils.isNotBlank(callData.getTotalFare())) {
            totalCharges = callData.getTotalFare();
        }
        PARTNER_TOP_UP_NEGATIVE_LIMIT = AppPreferences.getSettings().getSettings().getTop_up_limit();
        AMOUNT_LIMIT = AppPreferences.getSettings().getSettings().getAmount_limit();
        PARTNER_TOP_UP_POSITIVE_LIMIT = AppPreferences.getSettings().getSettings().getPartnerTopUpLimitPositive();
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

        if (isBykeaCashType) {
            updateUIBykeaCash();
        } else if (isDeliveryType || isOfflineDeliveryType) {
            updateUIICODelivery();
        } else if (isPurchaseType) {
            updateUIforPurcahseService();
        } else {
            receivedAmountEt.requestFocus();
        }

        //updating the visibility of camera icon
        ivTakeImage.setVisibility(isProofRequired() ? View.VISIBLE : View.GONE);
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

        receivedAmountEt.clearFocus();
        etReceiverName.requestFocus();
    }

    private void updateUIBykeaCash() {
        endAddressTv.setVisibility(View.GONE);
        dotted_line.setVisibility(View.GONE);
        ic_pin.setVisibility(View.GONE);
        tvTotalTime.setVisibility(View.GONE);
        tvTotalDistance.setVisibility(View.GONE);
        addressDivider.setVisibility(View.GONE);

        rlDeliveryStatus.setVisibility(View.VISIBLE);
        ivRight0.setImageDrawable(Utils.changeDrawableColor(mCurrentActivity, R.drawable.polygon, R.color.blue_dark));
        initAdapter(callData);

        if (StringUtils.isNotBlank(callData.getCodAmount())) {
            rlCOD.setVisibility(View.VISIBLE);
            tvCOD.setText(callData.getCodAmount());
            tvPayment.setText(R.string.payment);
        } else {
            rlCOD.setVisibility(View.GONE);
        }
        receivedAmountEt.requestFocus();
    }

    private int selectedMsgPosition = 0;

    private void initAdapter(final NormalCallData callData) {

        String[] list;
        if (isBykeaCashType) list = Utils.getBykeaCashJobStatusMsgList(mCurrentActivity);
        else list = Utils.getDeliveryMsgsList(mCurrentActivity);

        final DeliveryMsgsSpinnerAdapter adapter = new DeliveryMsgsSpinnerAdapter(mCurrentActivity, list);

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
                if (StringUtils.isNotBlank(callData.getCodAmount()) && (callData.isCod() || isBykeaCashType)) {
                    if (position == 0) {
//                        PARTNER_TOP_UP_NEGATIVE_LIMIT = AppPreferences.getSettings().getSettings().getTop_up_limit() + Integer.parseInt(callData.getCodAmountNotFormatted());
                        tvCOD.setPaintFlags(tvCOD.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        totalCharges = "" + (Integer.parseInt(callData.getTotalFare()) + Integer.parseInt(callData.getCodAmountNotFormatted()));
                        isJobSuccessful = true;
                    } else {
//                        PARTNER_TOP_UP_NEGATIVE_LIMIT = AppPreferences.getSettings().getSettings().getTop_up_limit();
                        tvCOD.setPaintFlags(tvCOD.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        totalCharges = callData.getTotalFare();
                        isJobSuccessful = false;
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

    @OnClick({R.id.ivTakeImage, R.id.feedbackBtn, R.id.ivEyeView})
    public void onClick(View v) {
        if (mLastClickTime != 0 && (SystemClock.elapsedRealtime() - mLastClickTime < 1000)) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        switch (v.getId()) {
            case R.id.ivTakeImage:
                takePicture();
                break;
            case R.id.ivEyeView:
                previewImage();
                break;
            case R.id.feedbackBtn:
                if (valid()) {
                    Dialogs.INSTANCE.showLoader(mCurrentActivity);
                    logMPEvent();
                    if (isProofRequired()) {
                        uploadProofOfDelivery();
                    } else {
                        finishTrip();
                    }
                }
                break;
        }
    }

    private void uploadProofOfDelivery() {
        repo = Injection.INSTANCE.provideJobsRepository(getApplication().getApplicationContext());
        //TODO need to handle image uploading here
        BykeaAmazonClient.INSTANCE.uploadFile(imageUri.getName(), imageUri, new com.bykea.pk.partner.utils.audio.Callback<String>() {
            @Override
            public void success(String obj) {
                imageUri.delete();
                repo.pushTripDetails(callData.getTripId(), obj, new JobsDataSource.PushTripDetailCallback() {
                    @Override
                    public void onSuccess() {
                        finishTrip();
                    }

                    @Override
                    public void onFail(int code, @Nullable String message) {
                        Dialogs.INSTANCE.dismissDialog();
                        Dialogs.INSTANCE.showToast(message);
                    }
                });
            }

            @Override
            public void fail(int errorCode, @NotNull String errorMsg) {
                Dialogs.INSTANCE.dismissDialog();
                Dialogs.INSTANCE.showToast(getString(R.string.no_file_available));
            }
        }, Constants.Amazon.BUCKET_NAME_POD);

    }

    private void finishTrip() {
        JobsDataSource.ConcludeJobCallback jobCallback = new JobsDataSource.ConcludeJobCallback() {

            @Override
            public void onJobConcluded(@NotNull ConcludeJobBadResponse response) {
                Dialogs.INSTANCE.dismissDialog();
                Dialogs.INSTANCE.showToast(response.getMessage());
                Utils.setCallIncomingState();
//                    AppPreferences.setWalletAmountIncreased(!response.isAvailable());
//                    AppPreferences.setAvailableStatus(response.isAvailable());
                ActivityStackManager.getInstance().startHomeActivity(true, mCurrentActivity);
                mCurrentActivity.finish();
            }


            @Override
            public void onJobConcludeFailed(@Nullable String message, @Nullable Integer code) {
                Dialogs.INSTANCE.dismissDialog();
                if (code != null && code == HTTPStatus.UNAUTHORIZED) {
                    EventBus.getDefault().post(Keys.UNAUTHORIZED_BROADCAST);
                } else {
                    Dialogs.INSTANCE.showError(mCurrentActivity, feedbackBtn, message);
                }
            }
        };

        boolean isLoadboardJob = Utils.isModernService(callData.getServiceCode());
        if (isLoadboardJob)
            repo = Injection.INSTANCE.provideJobsRepository(getApplication().getApplicationContext());

        if (isBykeaCashType) {
            if (isLoadboardJob) {
                String name = callData.getSenderName() != null ? callData.getSenderName() : callData.getPassName();
                String number = callData.getSenderPhone() != null ? callData.getSenderPhone() : callData.getPhoneNo();


                repo.concludeJob(
                        callData.getTripId(),
                        (int) callerRb.getRating(),
                        Integer.parseInt(receivedAmountEt.getText().toString()),
                        jobCallback,
                        Utils.getBykeaCashJobStatusMsgList(mCurrentActivity)[selectedMsgPosition],
                        selectedMsgPosition == 0,
                        null,
                        name,
                        number
                );
            } else
                new UserRepository().requestFeedback(
                        mCurrentActivity,
                        handler,
                        "",
                        callerRb.getRating() + "",
                        receivedAmountEt.getText().toString(),
                        selectedMsgPosition == 0,
                        Utils.getBykeaCashJobStatusMsgList(mCurrentActivity)[selectedMsgPosition],
                        etReceiverName.getText().toString(),
                        etReceiverMobileNo.getText().toString()
                );
        } else if (isDeliveryType || isOfflineDeliveryType) {
            if (isLoadboardJob)
                repo.concludeJob(
                        callData.getTripId(),
                        (int) callerRb.getRating(),
                        Integer.valueOf(receivedAmountEt.getText().toString()),
                        jobCallback,
                        Utils.getDeliveryMsgsList(mCurrentActivity)[selectedMsgPosition],
                        selectedMsgPosition == 0,
                        null,
                        etReceiverName.getText().toString(),
                        etReceiverMobileNo.getText().toString()
                );
            else
                new UserRepository().requestFeedback(mCurrentActivity, handler,
                        "Nice driver", callerRb.getRating() + "", receivedAmountEt.getText().toString()
                        , selectedMsgPosition == 0, Utils.getDeliveryMsgsList(mCurrentActivity)[selectedMsgPosition], etReceiverName.getText().toString(),
                        etReceiverMobileNo.getText().toString());
        } else if (isPurchaseType) {
            if (isLoadboardJob)
                repo.concludeJob(callData.getTripId(), (int) callerRb.getRating(), Integer.valueOf(receivedAmountEt.getText().toString()),
                        jobCallback, null, null,
                        Integer.valueOf(kharedariAmountEt.getText().toString()), null, null);
            else
                new UserRepository().requestFeedback(mCurrentActivity, handler,
                        "Nice driver", callerRb.getRating() + "", receivedAmountEt.getText().toString(),
                        kharedariAmountEt.getText().toString());
        } else {
            if (isLoadboardJob)
                repo.concludeJob(callData.getTripId(), (int) callerRb.getRating(), Integer.valueOf(receivedAmountEt.getText().toString()), jobCallback, null, null, null, null, null);
            else
                new UserRepository().requestFeedback(mCurrentActivity, handler,
                        "Nice driver", callerRb.getRating() + "", receivedAmountEt.getText().toString());
        }
    }

    private void logMPEvent() {
        try {
            NormalCallData callData = AppPreferences.getCallData();
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
            properties.put("SignUpCity", AppPreferences.getPilotData().getCity().getName());

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
                    Dialogs.INSTANCE.showToast(feedbackResponse.getMessage());
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

    /**
     * Feedback validation on the following cases.
     *
     * <ul>
     * <li>Check that the amount lie in the digit only regix</li>
     * <li>Check that the entered amount should be same to the total charges</li>
     * <li>Check that the entered amount should not be greater than
     * {@link FeedbackActivity#AMOUNT_LIMIT}</li>
     * <li>Check that the entered amount should be same to the total charges</li>
     * <li>Check that the rating should be given</li>
     * <li>Check that the amount should be entered & should not less than 0</li>
     * </ul>
     *
     * @return true if all the validation is true otherwise false
     */
    private boolean valid() {
        if (isPurchaseType && StringUtils.isBlank(kharedariAmountEt.getText().toString())) {
            kharedariAmountEt.setError(getString(R.string.enter_amount));
            kharedariAmountEt.requestFocus();
            return false;
        } else if (StringUtils.isBlank(receivedAmountEt.getText().toString())) {
            setEtError(getString(R.string.enter_received_amount));
            return false;
        } else if ((isDeliveryType || isOfflineDeliveryType) && selectedMsgPosition == Constants.KAMYAB_DELIVERY && StringUtils.isBlank(etReceiverName.getText().toString())) {
            etReceiverName.setError(getString(R.string.error_field_empty));
            etReceiverName.requestFocus();
            return false;
        } else if ((isDeliveryType || isOfflineDeliveryType) && selectedMsgPosition == Constants.KAMYAB_DELIVERY && StringUtils.isBlank(etReceiverMobileNo.getText().toString())) {
            etReceiverMobileNo.setError(getString(R.string.error_field_empty));
            etReceiverMobileNo.requestFocus();
            return false;
        } else if ((isDeliveryType || isOfflineDeliveryType || isPurchaseType) && StringUtils.isNotBlank(etReceiverMobileNo.getText().toString())
                && !Utils.isValidNumber(mCurrentActivity, etReceiverMobileNo)) {
            return false;
        } else if (!receivedAmountEt.getText().toString().matches(Constants.REG_EX_DIGIT)) {
            setEtError(getString(R.string.error_invalid_amount));
            return false;
        } else if (totalCharges.matches(Constants.REG_EX_DIGIT)
                && Integer.parseInt(receivedAmountEt.getText().toString()) < Integer.parseInt(totalCharges)
                && (!isBykeaCashType || isJobSuccessful)) {
            setEtError(getString(R.string.error_amount_greater_than_total));
            return false;
        } else if (totalCharges.matches(Constants.REG_EX_DIGIT) &&
                driverWallet <= PARTNER_TOP_UP_NEGATIVE_LIMIT &&
                Integer.parseInt(receivedAmountEt.getText().toString()) >= (Integer.parseInt(totalCharges) + PARTNER_TOP_UP_NEGATIVE_LIMIT + Constants.DIGIT_ONE) &&
                !Util.INSTANCE.isBykeaCashJob(callData.getServiceCode())) {
            //WHEN THE WALLET IS LESS THAN ZERO, RECEIVED AMOUNT CAN NOT BE GREATER THAN THE SUM OF (TOTAL CHARGES AND PARTNER TOP UP NEGATIVE LIMIT)
            setEtError(getString(R.string.amount_error, (Integer.parseInt(totalCharges) + PARTNER_TOP_UP_NEGATIVE_LIMIT)));
            return false;
        } else if (totalCharges.matches(Constants.REG_EX_DIGIT) &&
                (driverWallet > PARTNER_TOP_UP_NEGATIVE_LIMIT && driverWallet < PARTNER_TOP_UP_POSITIVE_LIMIT) &&
                Integer.parseInt(receivedAmountEt.getText().toString()) >= (Integer.parseInt(totalCharges) + driverWallet + Constants.DIGIT_ONE) &&
                !Util.INSTANCE.isBykeaCashJob(callData.getServiceCode())) {
            //WHEN THE WALLET IS GREATER THAN ZERO BUT LESS THAN THE MAX POSITIVE TOP UP LIMIT,
            //RECEIVED AMOUNT CAN NOT BE GREATER THAN THE SUM OF (TOTAL CHARGES AND WALLET)
            setEtError(getString(R.string.amount_error, (Integer.parseInt(totalCharges) + driverWallet)));
            return false;
        } else if (totalCharges.matches(Constants.REG_EX_DIGIT) &&
                (Util.INSTANCE.isBykeaCashJob(callData.getServiceCode()) || driverWallet >= PARTNER_TOP_UP_POSITIVE_LIMIT) &&
                Integer.parseInt(receivedAmountEt.getText().toString()) >= (Integer.parseInt(totalCharges) + PARTNER_TOP_UP_POSITIVE_LIMIT + Constants.DIGIT_ONE)) {
            //WHEN THE WALLET IS GREATER THAN MAX POSITIVE TOP UP LIMIT,
            //RECEIVED AMOUNT CAN NOT BE GREATER THAN THE SUM OF (TOTAL CHARGES AND PARTNER TOP UP POSITIVE LIMIT)
            setEtError(getString(R.string.amount_error, (Integer.parseInt(totalCharges) + PARTNER_TOP_UP_POSITIVE_LIMIT)));
            return false;
        } else if (Integer.parseInt(receivedAmountEt.getText().toString()) >= (AMOUNT_LIMIT + Constants.DIGIT_ONE)) {
            setEtError(getString(R.string.amount_error, AMOUNT_LIMIT));
            return false;
        } else if (callerRb.getRating() <= 0.0) {
            Dialogs.INSTANCE.showError(mCurrentActivity, feedbackBtn, getString(R.string.passenger_rating));
            return false;
        } else if (isProofRequired() && imageUri == null) {
            Dialogs.INSTANCE.showAlertDialogTick(FeedbackActivity.this, null, getString(R.string.valid_image_required), view -> Dialogs.INSTANCE.dismissDialog());
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

    private boolean isProofRequired() {
        List<String> codes = AppPreferences.getSettings().getSettings().getPodServiceCodes();
        boolean isRequired = false;
        for (String code : codes) {
            if (callData.getServiceCode() != null && code.equalsIgnoreCase(String.valueOf(callData.getServiceCode()))) {
                isRequired = true;
                break;
            }
        }
        return isRequired && selectedMsgPosition == 0;
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
        } else if (requestCode == Constants.REQUEST_CAMERA) {
            if (resultCode == RESULT_OK && tempUri != null && tempUri.exists()) {
                imageUri = tempUri;
                previewImage();
            }
        }
    }

    private void previewImage() {
        ivEyeView.setVisibility(View.VISIBLE);
        ivTakeImage.setVisibility(View.GONE);
        Dialogs.INSTANCE.showChangeImageDialog(FeedbackActivity.this, imageUri, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialogs.INSTANCE.dismissDialog();
            }
        }, view -> {
            Dialogs.INSTANCE.dismissDialog();
            takePicture();
        });
    }

    private void takePicture() {
        try {
            if (checkPermissions()) {
                tempUri = Utils.createImageFile(FeedbackActivity.this, "doc");
                Utils.startCameraByIntent(mCurrentActivity, tempUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkPermissions() {
        boolean hasPermission = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int location = ContextCompat.checkSelfPermission(mCurrentActivity.getApplicationContext(), PERMISSION);
            if (location != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{PERMISSION}, 1011);
            } else {
                hasPermission = true;
            }
        } else {
            hasPermission = true;
        }
        return hasPermission;
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull String[] permissions, @NonNull final int[] grantResults) {
        if (mCurrentActivity != null) {
            switch (requestCode) {
                case 1011:
                    if (grantResults.length > 0) {
                        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                            onPermissionResult();
                        } else {
                            checkPermissions();
                        }
                    }
                    break;
            }
        }
    }


    private void onPermissionResult() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(PERMISSION)) {
                Dialogs.INSTANCE.showAlertDialogNotSingleton(mCurrentActivity,
                        new StringCallBack() {
                            @Override
                            public void onCallBack(String msg) {
                                checkPermissions();
                            }
                        }, null, getString(R.string.camera_permission)
                        , getString(R.string.permissions_docs));
            } else {
                Dialogs.INSTANCE.showPermissionSettings(mCurrentActivity,
                        1011, getString(R.string.permissions_required),
                        getString(R.string.java_camera_permission_msg));
            }
        }
    }
}
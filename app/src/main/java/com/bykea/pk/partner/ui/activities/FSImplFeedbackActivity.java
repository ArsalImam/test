package com.bykea.pk.partner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
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
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.dal.source.JobsDataSource;
import com.bykea.pk.partner.dal.source.JobsRepository;
import com.bykea.pk.partner.dal.source.remote.data.Invoice;
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetailInfo;
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetails;
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetailsLocationInfoData;
import com.bykea.pk.partner.dal.source.remote.response.ConcludeJobBadResponse;
import com.bykea.pk.partner.dal.source.remote.response.FeedbackInvoiceResponse;
import com.bykea.pk.partner.dal.util.Injection;
import com.bykea.pk.partner.models.response.BatchBooking;
import com.bykea.pk.partner.models.response.DriverPerformanceResponse;
import com.bykea.pk.partner.models.response.FeedbackResponse;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.common.LastAdapter;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.adapters.DeliveryMsgsSpinnerAdapter;
import com.bykea.pk.partner.ui.nodataentry.BatchNaKamiyabDialog;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.NumericKeyBoardTransformationMethod;
import com.bykea.pk.partner.utils.Permissions;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Util;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontEditText;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static com.bykea.pk.partner.utils.Constants.Extras.DELIVERY_DETAILS_OBJECT;
import static com.bykea.pk.partner.utils.Constants.RequestCode.RC_ADD_EDIT_DELIVERY_DETAILS;
import static com.bykea.pk.partner.utils.Constants.ServiceCode.NEW_BATCH_DELIVERY_COD;

public class FSImplFeedbackActivity extends BaseActivity {

    @BindView(R.id.llbatchNaKamiyabDelivery)
    LinearLayout llbatchNaKamiyabDelivery;
    @BindView(R.id.tvTotalRakmLabel)
    TextView tvTotalRakmLabel;
    @BindView(R.id.ivBatchInfo)
    ImageView ivBatchInfo;
    @BindView(R.id.goto_purchaser)
    TextView tvGotoPurchaser;
    @BindView(R.id.imageViewAddDelivery)
    ImageView imageViewAddDelivery;
    @BindView(R.id.llFailureDelivery)
    LinearLayout llFailureDelivery;
    @BindView(R.id.ivPickUpCustomerPhone)
    ImageView ivPickUpCustomerPhone;
    @BindView(R.id.tvTripId)
    FontTextView tvTripId;
    @BindView(R.id.invoiceRecyclerView)
    RecyclerView invoiceRecyclerView;
    @BindView(R.id.startAddressTv)
    FontTextView startAddressTv;
    @BindView(R.id.ic_pin)
    View ic_pin;
    @BindView(R.id.addressDivider)
    View addressDivider;
    @BindView(R.id.dotted_line)
    View dotted_line;
    @BindView(R.id.endAddressTv)
    FontTextView endAddressTv;
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
    @BindView(R.id.scrollView)
    ScrollView scrollView;

    private int selectedMsgPosition = 0;
    private ArrayList<Invoice> invoiceData = new ArrayList<>();

    private FSImplFeedbackActivity mCurrentActivity;
    private String totalCharges = StringUtils.EMPTY, lastKhareedariAmount = StringUtils.EMPTY;
    private int PARTNER_TOP_UP_NEGATIVE_LIMIT, AMOUNT_LIMIT, PARTNER_TOP_UP_POSITIVE_LIMIT;
    private JobsRepository repo;

    int driverWallet;
    private boolean isJobSuccessful = true;
    private LastAdapter<Invoice> invoiceAdapter;
    private boolean isNewBatchFlow;
    private int batchServiceCode;
    private String batchId;
    private boolean isRerouteCreated = false;
    private boolean mLastReturnRunBooking;
    private ArrayList<Invoice> batchInvoiceList;

    private boolean isBykeaCashType, isDeliveryType, isOfflineDeliveryType, isPurchaseType;
    private NormalCallData callData;
    private DeliveryDetails reRouteDeliveryDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fs_impl_feedback_new);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ButterKnife.bind(this);
        repo = Injection.INSTANCE.provideJobsRepository(getApplication().getApplicationContext());
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
        updateInvoice();
    }

    /**
     * this method will update the invoice details of the current booking,
     * will populate the recycler view's adapter
     */
    private void updateInvoice() {
        repo.getInvoiceDetails(AppPreferences.getSettings()
                .getSettings().getFeedbackInvoiceListingUrl(), callData.getTripId(), new JobsDataSource.GetInvoiceCallback() {
            @Override
            public void onInvoiceDataLoaded(@NotNull FeedbackInvoiceResponse bookingDetailResponse) {
                FSImplFeedbackActivity.this.invoiceData = bookingDetailResponse.getData();
                updateAdapter();
            }

            @Override
            public void onInvoiceDataFailed(@Nullable String errorMessage) {
                Utils.appToast(errorMessage);
            }
        });
    }

    private void updateAdapter() {
        ArrayList<Invoice> filtered = new ArrayList<>();
        for (Invoice invoice : invoiceData) {
            if (invoice.getDeliveryStatus() == null) {
                filtered.add(invoice);
            } else if (selectedMsgPosition == 0 && invoice.getDeliveryStatus() == 1) {
                filtered.add(invoice);
            } else if (selectedMsgPosition != 0 && invoice.getDeliveryStatus() != 1) {
                filtered.add(invoice);
            }
        }
        if (!(mLastReturnRunBooking && containsCodBooking())) updateTotal(filtered);
        invoiceAdapter.setItems(filtered);
    }

    private void handleInputInfoForBatch(boolean isKamiyabDelivery) {
        if (!isNewBatchFlow) return;
        llReceiverInfo.setVisibility(isKamiyabDelivery ? View.VISIBLE : View.GONE);
        llTotal.setVisibility(isKamiyabDelivery ? View.VISIBLE : View.GONE);
        callerRb.setVisibility(isKamiyabDelivery ? View.VISIBLE : View.GONE);
        llbatchNaKamiyabDelivery.setVisibility(!isKamiyabDelivery ? View.VISIBLE : View.GONE);
        imageViewAddDelivery.setVisibility(!isKamiyabDelivery ? View.VISIBLE : View.GONE);
        llFailureDelivery.setVisibility(View.GONE);
        feedbackBtn.setEnabled(isKamiyabDelivery);
        if (reRouteDeliveryDetails != null) {
            onRerouteCreated(reRouteDeliveryDetails);
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
                if (Utils.isNewBatchService(batchServiceCode)) {
                    return;
                }
                if (driverWallet <= PARTNER_TOP_UP_NEGATIVE_LIMIT
                        && Integer.parseInt(editable.toString()) >= (Integer.parseInt(totalCharges) + PARTNER_TOP_UP_NEGATIVE_LIMIT + Constants.DIGIT_ONE)
                        && !Util.INSTANCE.isBykeaCashJob(callData.getServiceCode())) {
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


    private void initViews() {
        mCurrentActivity = this;
        invoiceAdapter = new LastAdapter<>(R.layout.adapter_booking_detail_invoice, item -> {

        });
        invoiceRecyclerView.setAdapter(invoiceAdapter);
        invoiceRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        initCallData();
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
        startAddressTv.setText(callData.getStartAddress());
        endAddressTv.setText((StringUtils.isBlank(callData.getEndAddress())
                ? "N/A" : callData.getEndAddress()));

        if (Utils.isNewBatchService(batchServiceCode)) {
            etReceiverName.setHint(R.string.consignees_name);

            boolean hasCodBooking = containsCodBooking();

            if (mLastReturnRunBooking && hasCodBooking) {
                tvTotalRakmLabel.setTextSize(getResources().getDimension(R.dimen._11sdp));
                ivBatchInfo.setVisibility(View.VISIBLE);
                repo.getReturnRunBatchInvoice(AppPreferences.getSettings()
                        .getSettings().getBatchBookingInvoiceUrl(), batchId, new JobsDataSource.GetInvoiceCallback() {

                    @Override
                    public void onInvoiceDataLoaded(@NotNull FeedbackInvoiceResponse feedbackInvoiceResponse) {
                        batchInvoiceList = feedbackInvoiceResponse.getData();
                        Dialogs.INSTANCE.showReturnRunInvoice(FSImplFeedbackActivity.this, batchInvoiceList, null);
                        receivedAmountEt.setHint("Suggested Rs. " + updateTotal(batchInvoiceList));
                    }

                    @Override
                    public void onInvoiceDataFailed(@Nullable String errorMessage) {
                        Utils.appToast(errorMessage);
                    }
                });
            }
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
        checkForRerouteUI();
    }

    private void checkForRerouteUI() {
//        if (reRouteDeliveryDetails != null) {
////            spDeliveryStatus.setSelection(selectedMsgPosition);
////            handleInputInfoForBatch(false);
////            onRerouteCreated(reRouteDeliveryDetails);
//        }
    }

    private boolean containsCodBooking() {
        if (Utils.isNewBatchService(batchServiceCode)) {
            for (BatchBooking batchBooking : callData.getBookingList())
                if (batchBooking.getServiceCode() == Constants.ServiceCode.SEND_COD)
                    return true;
        }
        return false;
    }

    private String updateTotal(ArrayList<Invoice> invoiceList) {
        String total = StringUtils.EMPTY;
        for (Invoice invoice : invoiceList) {
            if (invoice.getField() != null && invoice.getField().equalsIgnoreCase("total")) {
                total = invoice.getValue();
                break;
            }
        }
        if (StringUtils.isNotEmpty(total)) {
            totalCharges = total;
            callData.setTotalFare(totalCharges);
        }
        return total;
    }

    private void initCallData() {
        callData = AppPreferences.getCallData();
        isNewBatchFlow = Utils.isNewBatchService(callData.getServiceCode());
        //this will manage the single ride flow
        if (!isNewBatchFlow) return;
        //extracting batch service code
        batchServiceCode = callData.getServiceCode();
        batchId = callData.getTripId();
        // check for finished trip
        ArrayList<BatchBooking> bookingResponseList = callData.getBookingList();
        //this will be the finished trip
        BatchBooking trip = null;
        for (BatchBooking tripData : bookingResponseList) {
            // if trip status if "finished", getting trip details
            if (tripData.getStatus().
                    equalsIgnoreCase(TripStatus.ON_FINISH_TRIP)) {
                trip = tripData;
                break;
            }
        }
        callData.setCallType(batchServiceCode == NEW_BATCH_DELIVERY_COD ? Constants.CallType.COD : Constants.CallType.NOD);
        callData.setTotalFare("0"); //this will automatically update through the invoice api
        callData.setTripId(trip.getId());
        callData.setTripNo(trip.getBookingCode());
        callData.setServiceCode(trip.getServiceCode());
        callData.setEndAddress(trip.getDropoff().getGpsAddress());
        callData.setEndLat(String.valueOf(trip.getDropoff().getLat()));
        callData.setEndLng(String.valueOf(trip.getDropoff().getLng()));

        mLastReturnRunBooking = trip.getDisplayTag().equalsIgnoreCase("z");

        //checking for reroute
        if (StringUtils.isNotEmpty(trip.getDropoff().getRerouteBookingId())) {
            //trip contains reroute
            for (BatchBooking tripData : bookingResponseList) {
                //find for routed booking
                if (trip.getDropoff().getRerouteBookingId().equalsIgnoreCase(tripData.getId())) {
                    //delivery data is mapping with failed data
                    DeliveryDetails deliveryDetails = new DeliveryDetails();
                    deliveryDetails.setDetails(new DeliveryDetailInfo());
                    deliveryDetails.setDropoff(new DeliveryDetailsLocationInfoData());
                    deliveryDetails.getDropoff().setZone_dropoff_name_urdu(tripData.getDropoff().getAddress());
                    deliveryDetails.getDetails().setTrip_id(tripData.getId());
                    reRouteDeliveryDetails = deliveryDetails;
                    selectedMsgPosition = AppPreferences.getLastSelectedMsgPosition();
                    break;
                }
            }
        }
    }

    private void updateUIforPurcahseService() {
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
        ivRight0.setImageDrawable(Utils.changeDrawableColor(mCurrentActivity, R.drawable.polygon, R.color.blue_dark));
        initAdapter(callData);

        receivedAmountEt.clearFocus();
        etReceiverName.requestFocus();
    }

    private void updateUIBykeaCash() {
        endAddressTv.setVisibility(View.GONE);
        dotted_line.setVisibility(View.GONE);
        ic_pin.setVisibility(View.GONE);
        addressDivider.setVisibility(View.GONE);

        rlDeliveryStatus.setVisibility(View.VISIBLE);
        ivRight0.setImageDrawable(Utils.changeDrawableColor(mCurrentActivity, R.drawable.polygon, R.color.blue_dark));
        initAdapter(callData);

        receivedAmountEt.requestFocus();
    }


    private void initAdapter(final NormalCallData callData) {

        String[] list;
        if (isBykeaCashType) list = Utils.getBykeaCashJobStatusMsgList(mCurrentActivity);
        else if (mLastReturnRunBooking) list = new String[]{getString(R.string.return_run_spinner)};
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

                if (reRouteDeliveryDetails != null && AppPreferences.getLastSelectedMsgPosition() != position) {
                    spDeliveryStatus.setSelection(AppPreferences.getLastSelectedMsgPosition());
                    return;
                }
                AppPreferences.setLastSelectedMsgPosition(position);
                selectedMsgPosition = position;
                updateAdapter();
                handleInputInfoForBatch(selectedMsgPosition == 0);
                if (StringUtils.isNotBlank(callData.getCodAmount()) && (callData.isCod() || isBykeaCashType)) {
                    if (position == 0) {
//                        PARTNER_TOP_UP_NEGATIVE_LIMIT = AppPreferences.getSettings().getSettings().getTop_up_limit() + Integer.parseInt(callData.getCodAmountNotFormatted());
                        totalCharges = "" + (Integer.parseInt(callData.getTotalFare()) + Integer.parseInt(callData.getCodAmountNotFormatted()));
                        isJobSuccessful = true;
                    } else {
//                        PARTNER_TOP_UP_NEGATIVE_LIMIT = AppPreferences.getSettings().getSettings().getTop_up_limit();
                        totalCharges = callData.getTotalFare();
                        isJobSuccessful = false;
                    }
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

    @OnClick({R.id.feedbackBtn, R.id.ivBatchInfo, R.id.ivPickUpCustomerPhone, R.id.imageViewAddDelivery})
    public void onClick(View v) {
        if (mLastClickTime != 0 && (SystemClock.elapsedRealtime() - mLastClickTime < 1000)) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        switch (v.getId()) {
            case R.id.imageViewAddDelivery:
                String tripId = callData.getTripId();
                if (reRouteDeliveryDetails != null) {
                    tripId = reRouteDeliveryDetails.getDetails().getTrip_id();
                }
                new BatchNaKamiyabDialog(batchId, tripId, new BatchNaKamiyabDialog.OnResult() {
                    @Override
                    public void onReturnRun() {
                        callData.setReturnRun(true);
                        updateFailureDeliveryLabel(null);
                        feedbackBtn.setEnabled(true);
                    }

                    @Override
                    public void onReRoute() {
                    }

                }).show(getSupportFragmentManager());
                break;
            case R.id.ivPickUpCustomerPhone:
                String phoneNumber = callData.getSenderPhone();
                if (StringUtils.isNotBlank(phoneNumber)) {
                    if (Utils.isAppInstalledWithPackageName(mCurrentActivity, Constants.ApplicationsPackageName.WHATSAPP_PACKAGE)) {
                        Utils.openCallDialog(mCurrentActivity, callData, phoneNumber);
                    } else {
                        Utils.callingIntent(mCurrentActivity, phoneNumber);
                    }
                }
                break;
            case R.id.ivBatchInfo:
                if (batchInvoiceList != null)
                    Dialogs.INSTANCE.showReturnRunInvoice(FSImplFeedbackActivity.this, batchInvoiceList, null);
                break;

            case R.id.feedbackBtn:
                if (valid()) {
                    Dialogs.INSTANCE.showLoader(mCurrentActivity);
                    logMPEvent();

                    JobsDataSource.ConcludeJobCallback jobCallback = new JobsDataSource.ConcludeJobCallback() {

                        @Override
                        public void onJobConcluded(@NotNull ConcludeJobBadResponse response) {
                            Dialogs.INSTANCE.dismissDialog();
                            Dialogs.INSTANCE.showToast(response.getMessage());
                            //handled old flow if not a batch service
                            if (allBookingInCompletedState()) {
                                Utils.setCallIncomingState();
                                ActivityStackManager.getInstance().startHomeActivity(true, mCurrentActivity);
                                mCurrentActivity.finish();
                            } else {
                                //check if contains any pending booking or has any failed booking
                                mCurrentActivity.finish();
                            }
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
                                    getDeliveryFeedback(),
                                    selectedMsgPosition == 0,
                                    null,
                                    etReceiverName.getText().toString(),
                                    etReceiverMobileNo.getText().toString()
                            );
                        else
                            new UserRepository().requestFeedback(mCurrentActivity, handler,
                                    "Nice driver", callerRb.getRating() + "", receivedAmountEt.getText().toString()
                                    , selectedMsgPosition == 0, getDeliveryFeedback(), etReceiverName.getText().toString(),
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
                break;
        }
    }

    private String getDeliveryFeedback() {
        if (mLastReturnRunBooking) {
            return getString(R.string.return_run_spinner);
        }
        return Utils.getDeliveryMsgsList(mCurrentActivity)[selectedMsgPosition];
    }

    private boolean allBookingInCompletedState() {
        if (isNewBatchFlow) {
            //saving foreach, if booking is not a return run
            if (!callData.isReturnRun()) {
                for (BatchBooking batchBooking : callData.getBookingList()) {
                    if (!batchBooking.isCompleted()) {
                        return true;
                    }
                }
            }
            //need to handle re-route case above return run
            if (isRerouteCreated) {
                return false;
            }
            //checking whether z's booking exist
            if (callData.isReturnRun()) {
                return Utils.containsReturnRunBooking(callData.getBookingList());
            }
            return false;
        } else
            return true;
    }

    private void logMPEvent() {
        try {
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
     * {@link FSImplFeedbackActivity#AMOUNT_LIMIT}</li>
     * <li>Check that the entered amount should be same to the total charges</li>
     * <li>Check that the rating should be given</li>
     * <li>Check that the amount should be entered & should not less than 0</li>
     * </ul>
     *
     * @return true if all the validation is true otherwise false
     */
    private boolean valid() {
        if (isNewBatchFlow && selectedMsgPosition != 0) {
            receivedAmountEt.setText(String.valueOf(NumberUtils.INTEGER_ZERO));
            return true;
        }
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

    private void setEtError(String error) {
        receivedAmountEt.setError(error);
        receivedAmountEt.requestFocus();
    }

    @Override
    public void onBackPressed() {

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
        } else if (requestCode == RC_ADD_EDIT_DELIVERY_DETAILS) {
            if (resultCode == RESULT_OK) {
                onRerouteCreated(data.getParcelableExtra(DELIVERY_DETAILS_OBJECT));
            }
        }
    }

    private void onRerouteCreated(DeliveryDetails data) {
        isRerouteCreated = true;
        feedbackBtn.setEnabled(true);
        spDeliveryStatus.setEnabled(false);
        spDeliveryStatus.setClickable(false);
        reRouteDeliveryDetails = data;
        updateFailureDeliveryLabel(data);
    }

    private void updateFailureDeliveryLabel(DeliveryDetails deliveryDetails) {
        if (!isNewBatchFlow) return;
        String formattedString = getResources().getString(R.string.problem_item);
        if (deliveryDetails != null) {
            llFailureDelivery.setVisibility(View.VISIBLE);
            imageViewAddDelivery.setVisibility(View.GONE);
            tvGotoPurchaser.setText(String.format(formattedString, deliveryDetails.getDropoff().getZone_dropoff_name_urdu()));
        } else if (callData.isReturnRun()) {
            llFailureDelivery.setVisibility(View.VISIBLE);
            imageViewAddDelivery.setVisibility(View.GONE);
            tvGotoPurchaser.setText(String.format(formattedString, getString(R.string.goto_purchaser)));
        }
    }
}

package com.bykea.pk.partner.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.RatingBar;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.response.FeedbackResponse;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.NetworkChangeListener;
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
    ProgressDialog progressDialog;


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

    private void initViews() {
        mCurrentActivity = this;

        progressDialog = new ProgressDialog(mCurrentActivity);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.internet_error));

        NormalCallData callData = AppPreferences.getCallData(mCurrentActivity);
        receivedAmountEt.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        tvTripId.setText(callData.getTripNo());
        totalAmountTv.setText("Rs. " + callData.getTotalFare());
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
            try {
                NormalCallData callData = AppPreferences.getCallData(mCurrentActivity);
                mixpanelAPI.getPeople().identify(callData.getPassId());
                JSONObject revenue = new JSONObject();
                revenue.put("$time", Utils.getUTCCurrentDate());
                revenue.put("TripID", callData.getTripId());
                revenue.put("TripNo", callData.getTripNo());
                revenue.put("PassengerID", callData.getPassId());
                revenue.put("DriverID", AppPreferences.getPilotData(mCurrentActivity).getId());
                mixpanelAPI.getPeople().trackCharge(Double.parseDouble(callData.getTrip_charges()), revenue);
                JSONObject properties = new JSONObject();
                properties.put("TripID", callData.getTripId());
                properties.put("TripNo", callData.getTripNo());
                properties.put("PassengerID", callData.getPassId());
                properties.put("DriverID", AppPreferences.getPilotData(mCurrentActivity).getId());
                properties.put("Amount", callData.getTrip_charges());
                properties.put("AmountEntered", receivedAmountEt.getText().toString());
                properties.put("Time", callData.getTotalMins() + "");
                properties.put("KM", callData.getDistanceCovered());
                mixpanelAPI.track(Constants.RIDE_FARE, properties);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            UserRepository repository = new UserRepository();
            repository.requestFeedback(mCurrentActivity, handler,
                    "Nice driver", callerRb.getRating() + "", receivedAmountEt.getText().toString());
        }
    }

    private UserDataHandler handler = new UserDataHandler() {
        @Override
        public void onFeedback(final FeedbackResponse feedbackResponse) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Dialogs.INSTANCE.dismissDialog();
                    Dialogs.INSTANCE.showToast(mCurrentActivity, "Your trip is successfully completed.");
                    Utils.setCallIncomingState(mCurrentActivity);
                    ActivityStackManager.getInstance(mCurrentActivity).startHomeActivity(true);
                    finish();
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
            receivedAmountEt.setError("Enter received amount");
            receivedAmountEt.requestFocus();
            return false;
        } else if (!receivedAmountEt.getText().toString().matches(Constants.REG_EX_DIGIT)) {
            receivedAmountEt.setError("Invalid amount");
            receivedAmountEt.requestFocus();
            return false;
        } else if (Integer.parseInt(receivedAmountEt.getText().toString()) > Constants.AMOUNT_LIMIT) {
            receivedAmountEt.setError("Amount can't be more than " + Constants.AMOUNT_LIMIT);
            receivedAmountEt.requestFocus();
            return false;
        } else if (callerRb.getRating() <= 0.0) {
            Dialogs.INSTANCE.showError(mCurrentActivity, feedbackBtn, "Please Rate Passenger.");
            return false;
        } else if (StringUtils.isNotBlank(receivedAmountEt.getText().toString())) {
            //todo: here we should check price entered should be only numbers.
            try {
                int receivedPrice = Integer.parseInt(receivedAmountEt.getText().toString());
                if (receivedPrice < 0) {
                    receivedAmountEt.setError("Amount is not acceptable");
                    receivedAmountEt.requestFocus();
                    return false;
                }
            } catch (Exception e) {
                receivedAmountEt.setError("Amount is not acceptable");
                receivedAmountEt.requestFocus();
                return false;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.location.GPS_ENABLED_CHANGE");
        registerReceiver(networkChangeListener, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkChangeListener);
    }

    @Override
    protected void onDestroy() {
        mixpanelAPI.flush();
        super.onDestroy();
        progressDialog.dismiss();

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
                ActivityStackManager.getInstance(mCurrentActivity).startLocationService();
            }
        }
    }

    private NetworkChangeListener networkChangeListener = new NetworkChangeListener() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("android.location.GPS_ENABLED_CHANGE")) {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    Dialogs.INSTANCE.showLocationSettings(mCurrentActivity, Permissions.LOCATION_PERMISSION);
                else
                    Dialogs.INSTANCE.dismissDialog();
            } else {
                if (Connectivity.isConnectedFast(context)) {
                    if (null != progressDialog)
                        progressDialog.dismiss();
                } else {
                    progressDialog.show();
                }
            }
        }
    };
}

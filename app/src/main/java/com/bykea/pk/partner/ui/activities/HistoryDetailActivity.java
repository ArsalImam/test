package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import com.bykea.pk.partner.models.data.TripHistoryData;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.widgets.AutoFitFontTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.Predefine_rating;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HistoryDetailActivity extends BaseActivity {

    @Bind(R.id.startAddressTv)
    FontTextView startAddressTv;
    @Bind(R.id.endAddressTv)
    FontTextView endAddressTv;
    @Bind(R.id.nameTv)
    AutoFitFontTextView nameTv;
    @Bind(R.id.timeTv)
    FontTextView timeTv;/*
    @Bind(R.id.dateTv)
    FontTextView dateTv;*/
    @Bind(R.id.totalDistanceTv)
    FontTextView totalDistanceTv;
    @Bind(R.id.totalTimeTv)
    FontTextView totalTimeTv;
    @Bind(R.id.basefareTv)
    FontTextView basefareTv;
    @Bind(R.id.totalAmountTv)
    FontTextView totalAmountTv;

    @Bind(R.id.rlFeedbackMsg1)
    LinearLayout rlFeedbackMsg1;
    @Bind(R.id.rlFeedbackMsg2)
    LinearLayout rlFeedbackMsg2;


    @Bind(R.id.tvMsg1)
    FontTextView tvMsg1;
    @Bind(R.id.tvMsg2)
    FontTextView tvMsg2;
    @Bind(R.id.tvMsg3)
    FontTextView tvMsg3;
    @Bind(R.id.tvMsg4)
    FontTextView tvMsg4;
    @Bind(R.id.tvMsg5)
    FontTextView tvMsg5;
    @Bind(R.id.tvMsg6)
    FontTextView tvMsg6;


    @Bind(R.id.serviceTypeTv)
    FontTextView serviceTypeTv;
    @Bind(R.id.distancePriceTv)
    FontTextView distancePriceTv;
    @Bind(R.id.timePriceTv)
    FontTextView timePriceTv;
    @Bind(R.id.fareTv)
    FontTextView fareTv;
    @Bind(R.id.promoTv)
    FontTextView promoTv;

    @Bind(R.id.dropOffDiscTv)
    FontTextView dropOffDiscTv;

    @Bind(R.id.walletTv)
    FontTextView walletTv;
    @Bind(R.id.driverRb)
    RatingBar driverRb;
    @Bind(R.id.passengerRb)
    RatingBar passengerRb;

    @Bind(R.id.btnProblem)
    FontTextView btnProblem;

//    private String tripNo;

    ArrayList<Predefine_rating> rattingToShow = new ArrayList<>();
    private TripHistoryData data;


    private HistoryDetailActivity mCurrentActivity;
    private final static String REQUIRED_DATE_FORMAT = "dd MMM, hh:mm a";
    private final static String CURRENT_DATE_FORMAT = "HH:mm:ss MM-dd-yyyy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ButterKnife.bind(this);
        mCurrentActivity = this;
        if (null != getIntent()) {
            data = (TripHistoryData) getIntent().getSerializableExtra(Constants.Extras.TRIP_DETAILS);
            if (data.getInvoice() != null) {
                endAddressTv.setText(data.getEndAddress());
                startAddressTv.setText(data.getStartAddress());
                try {
                    nameTv.setText(data.getPassenger() != null && StringUtils.isNotBlank(data.getPassenger().getName())
                            ? data.getPassenger().getName() : "N/A");
                    timeTv.setText(Utils.getFormattedDate(data.getAcceptTime(),
                            CURRENT_DATE_FORMAT,
                            REQUIRED_DATE_FORMAT));
                    serviceTypeTv.setText(StringUtils.capitalize(data.getTrip_type()));
                    totalDistanceTv.setText(data.getInvoice().getKm() + " km");
                    float totalTime = Float.parseFloat(data.getInvoice().getMinutes());
                    float pricePerMin = Float.parseFloat(data.getInvoice().getPricePerMin());
                    float pricePerKm = Float.parseFloat(data.getInvoice().getPricePerKm());
                    totalTimeTv.setText(Math.round(totalTime) + " mins");
                    timePriceTv.setText("" + (Math.round(Math.ceil(totalTime * pricePerMin))));
                    distancePriceTv.setText("" + (Math.round(Math.ceil(pricePerKm))));
                    fareTv.setText("" + data.getInvoice().getTripCharges());
                    basefareTv.setText("" + data.getInvoice().getBaseFare());
                    totalAmountTv.setText("Rs. " + data.getInvoice().getTotal());
                    promoTv.setText("" + data.getInvoice().getPromo_deduction());
                    dropOffDiscTv.setText("" + data.getInvoice().getDropoff_discount());

                    String start_balance = verifyData(data.getInvoice().getStart_balance());
                    String wallet = verifyData(data.getInvoice().getWallet_deduction());
                    if (start_balance.contains("-")) {
                        walletTv.setText("(" + start_balance.substring(1) + ")");
                    } else {
                        if (wallet.equalsIgnoreCase("0")) {
                            walletTv.setText("" + wallet);
                        } else {
                            walletTv.setText("- " + wallet);
                        }
                    }
                    if (data.getDriverRating() != null && StringUtils.isNotBlank(data.getDriverRating().getRate())) {
                        driverRb.setRating(Float.parseFloat(data.getDriverRating().getRate()));
                    }
                    if (data.getPassRating() != null && StringUtils.isNotBlank(data.getPassRating().getRate())) {
                        passengerRb.setRating(Float.parseFloat(data.getPassRating().getRate()));
                    }
                    String feedbackComments = data.getPassRating().getFeedback_message()[0];
                    if (StringUtils.isNotBlank(feedbackComments)) {
                        rattingToShow = new Gson().fromJson(feedbackComments, new TypeToken<ArrayList<Predefine_rating>>() {
                        }.getType());
                    } else {
                        rlFeedbackMsg1.setVisibility(View.GONE);
                        rlFeedbackMsg2.setVisibility(View.GONE);
                    }
                    if (rattingToShow != null && rattingToShow.size() > 0) {
                        populatePredefineMsgs();
                    }
                    if (Utils.getDaysInBetween(System.currentTimeMillis(),
                            new SimpleDateFormat(CURRENT_DATE_FORMAT).parse(data.getAcceptTime()).getTime()) >=
                            AppPreferences.getSettings().getSettings().getTrip_support_max_days()) {
                        btnProblem.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        setBackNavigation();
        setToolbarTitle((data.getTripNo()).toUpperCase());
        hideToolbarLogo();
    }

    private String verifyData(String value) {
        return StringUtils.isNotBlank(value) ? value : StringUtils.EMPTY;
    }


    @OnClick({R.id.btnProblem})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnProblem:
//                String params = "name=" + AppPreferences.getPilotData().getFullName() +
//                        "&booking=" + data.getTripNo() + "&phone=" + AppPreferences.getPilotData().getPhoneNo();
//                Utils.startCustomWebViewActivity(mCurrentActivity,
//                        AppPreferences.getSettings().getSettings().getTrip_support_link() + params, data.getTripNo());

                ActivityStackManager.getInstance(mCurrentActivity).startProblemActivity(mCurrentActivity, data.getTripNo());
                break;
        }
    }

    private void populatePredefineMsgs() {
        tvMsg1.setBackground(ContextCompat.getDrawable(mCurrentActivity, R.drawable.bg_unselected_feedback_comment));
        tvMsg2.setBackground(ContextCompat.getDrawable(mCurrentActivity, R.drawable.bg_unselected_feedback_comment));
        tvMsg3.setBackground(ContextCompat.getDrawable(mCurrentActivity, R.drawable.bg_unselected_feedback_comment));
        tvMsg4.setBackground(ContextCompat.getDrawable(mCurrentActivity, R.drawable.bg_unselected_feedback_comment));
        tvMsg5.setBackground(ContextCompat.getDrawable(mCurrentActivity, R.drawable.bg_unselected_feedback_comment));
        tvMsg6.setBackground(ContextCompat.getDrawable(mCurrentActivity, R.drawable.bg_unselected_feedback_comment));
        rlFeedbackMsg1.setVisibility(View.GONE);
        rlFeedbackMsg2.setVisibility(View.GONE);
        tvMsg1.setVisibility(View.GONE);
        tvMsg2.setVisibility(View.GONE);
        tvMsg3.setVisibility(View.GONE);
        tvMsg4.setVisibility(View.GONE);
        tvMsg5.setVisibility(View.GONE);
        tvMsg6.setVisibility(View.GONE);
        if (rattingToShow.size() > 0) {
            rlFeedbackMsg1.setVisibility(View.VISIBLE);
            tvMsg1.setVisibility(View.VISIBLE);
            tvMsg1.setText(rattingToShow.get(0).getMessage());
            tvMsg1.setTag(rattingToShow.get(0).get_id());
            if (rattingToShow.size() > 1) {
                tvMsg2.setVisibility(View.VISIBLE);
                tvMsg2.setText(rattingToShow.get(1).getMessage());
                tvMsg2.setTag(rattingToShow.get(1).get_id());
                if (rattingToShow.size() > 2) {
                    tvMsg3.setVisibility(View.VISIBLE);
                    tvMsg3.setText(rattingToShow.get(2).getMessage());
                    tvMsg3.setTag(rattingToShow.get(2).get_id());
                    if (rattingToShow.size() > 3) {
                        rlFeedbackMsg2.setVisibility(View.VISIBLE);
                        tvMsg4.setVisibility(View.VISIBLE);
                        tvMsg4.setText(rattingToShow.get(3).getMessage());
                        tvMsg4.setTag(rattingToShow.get(3).get_id());
                        if (rattingToShow.size() > 4) {
                            tvMsg5.setVisibility(View.VISIBLE);
                            tvMsg5.setText(rattingToShow.get(4).getMessage());
                            tvMsg5.setTag(rattingToShow.get(4).get_id());
                            if (rattingToShow.size() > 5) {
                                tvMsg6.setVisibility(View.VISIBLE);
                                tvMsg6.setText(rattingToShow.get(5).getMessage());
                                tvMsg6.setTag(rattingToShow.get(5).get_id());
                            }
                        }
                    }
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        setToolbarLogo();
        hideToolbarTitle();
    }
}


package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.RatingBar;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.Predefine_rating;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HistoryDetailActivity extends BaseActivity {

    @Bind(R.id.startAddressTv)
    FontTextView startAddressTv;
    @Bind(R.id.endAddressTv)
    FontTextView endAddressTv;
    @Bind(R.id.nameTv)
    FontTextView nameTv;
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
    RelativeLayout rlFeedbackMsg1;
    @Bind(R.id.rlFeedbackMsg2)
    RelativeLayout rlFeedbackMsg2;


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
    @Bind(R.id.walletTv)
    FontTextView walletTv;
    @Bind(R.id.driverRb)
    RatingBar driverRb;
    @Bind(R.id.passengerRb)
    RatingBar passengerRb;


    private String tripNo;
    private String startAddress;
    private String endAddress;
    private String name;
    private String time;
    private String date;
    private String basefare;
    private String totalfare;
    private String type;
    private String promo;
    private String wallet;
    private String driverRating;
    private String passRating;
    private String start_balance;
    private String receivedAmount;

    float totalTime, pricePerkm, totalDistance, pricePerMin;

    ArrayList<Predefine_rating> rattingToShow = new ArrayList<>();


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
            String status = getIntent().getStringExtra("status");
            endAddress = getIntent().getStringExtra("eaddress");
            startAddress = getIntent().getStringExtra("saddress");
            tripNo = getIntent().getStringExtra("trip_no");
            name = getIntent().getStringExtra("name");
            time = getIntent().getStringExtra("time");
            basefare = getIntent().getStringExtra("basefare");
            totalfare = getIntent().getStringExtra("amount");
            start_balance = getIntent().getStringExtra("start_balance");
            receivedAmount = getIntent().getStringExtra("receivedAmount");
            totalTime = Float.parseFloat(getIntent().getStringExtra("total_time"));
            pricePerMin = Float.parseFloat(getIntent().getStringExtra("pricePerMin"));
            pricePerkm = Float.parseFloat(getIntent().getStringExtra("pricePerKm"));
            totalDistance = Float.parseFloat(getIntent().getStringExtra("total_distance"));

            type = getIntent().getStringExtra("type");
            promo = getIntent().getStringExtra("promo");
            wallet = getIntent().getStringExtra("wallet");
            driverRating = getIntent().getStringExtra("driverRating");
            passRating = getIntent().getStringExtra("passRating");
            String feedbackComments = getIntent().getStringExtra("feedbackComments");
            if (StringUtils.isNotBlank(feedbackComments)) {
                rattingToShow = new Gson().fromJson(feedbackComments, new TypeToken<ArrayList<Predefine_rating>>() {
                }.getType());
            } else {
                rlFeedbackMsg1.setVisibility(View.GONE);
                rlFeedbackMsg2.setVisibility(View.GONE);
            }
        }

        setBackNavigation();
        setToolbarTitle((tripNo).toUpperCase());
        hideToolbarLogo();
        startAddressTv.setText(startAddress);
        endAddressTv.setText(endAddress);

        try {
            nameTv.setText(name);
            timeTv.setText(Utils.getFormattedDate(time, CURRENT_DATE_FORMAT,
                    REQUIRED_DATE_FORMAT));
            serviceTypeTv.setText(StringUtils.capitalize(type));
            totalDistanceTv.setText(totalDistance + " km");
            totalTimeTv.setText(Math.round(totalTime) + " mins");
            timePriceTv.setText("" + (Math.round(Math.ceil(totalTime * pricePerMin))));
            distancePriceTv.setText("" + (Math.round(Math.ceil(pricePerkm))));
            fareTv.setText("" + totalfare);
            basefareTv.setText("" + basefare);
            totalAmountTv.setText("Rs. " + receivedAmount);
            promoTv.setText("" + promo);
            if (start_balance.contains("-")) {
                walletTv.setText("(" + start_balance.substring(1) + ")");
            } else {
                if (wallet.equalsIgnoreCase("0")) {
                    walletTv.setText("" + wallet);
                } else {
                    walletTv.setText("- " + wallet);
                }
            }
            if (StringUtils.isNotBlank(driverRating)) {
                driverRb.setRating(Float.parseFloat(driverRating));
            }
            if (StringUtils.isNotBlank(passRating)) {
                passengerRb.setRating(Float.parseFloat(passRating));
            }


            if (rattingToShow != null && rattingToShow.size() > 0) {
                populatePredefineMsgs();

            }
        } catch (Exception e) {
            e.printStackTrace();
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


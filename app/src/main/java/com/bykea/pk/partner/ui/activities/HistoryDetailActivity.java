package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import com.bykea.pk.partner.models.data.TripHistoryData;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.adapters.CommentsAdapter;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HistoryDetailActivity extends BaseActivity {

    @BindView(R.id.startAddressTv)
    FontTextView startAddressTv;
    @BindView(R.id.endAddressTv)
    FontTextView endAddressTv;
    @BindView(R.id.nameTv)
    AutoFitFontTextView nameTv;
    @BindView(R.id.timeTv)
    FontTextView timeTv;/*
    @BindView(R.id.dateTv)
    FontTextView dateTv;*/
    @BindView(R.id.totalDistanceTv)
    FontTextView totalDistanceTv;
    @BindView(R.id.totalTimeTv)
    FontTextView totalTimeTv;
    @BindView(R.id.basefareTv)
    FontTextView basefareTv;
    @BindView(R.id.totalAmountTv)
    FontTextView totalAmountTv;

    @BindView(R.id.rlFeedbackMsg1)
    LinearLayout rlFeedbackMsg1;
    @BindView(R.id.rlFeedbackMsg2)
    LinearLayout rlFeedbackMsg2;


    @BindView(R.id.tvMsg1)
    FontTextView tvMsg1;
    @BindView(R.id.tvMsg2)
    FontTextView tvMsg2;
    @BindView(R.id.tvMsg3)
    FontTextView tvMsg3;
    @BindView(R.id.tvMsg4)
    FontTextView tvMsg4;
    @BindView(R.id.tvMsg5)
    FontTextView tvMsg5;
    @BindView(R.id.tvMsg6)
    FontTextView tvMsg6;


    @BindView(R.id.serviceTypeTv)
    FontTextView serviceTypeTv;
    @BindView(R.id.distancePriceTv)
    FontTextView distancePriceTv;
    @BindView(R.id.timePriceTv)
    FontTextView timePriceTv;
    @BindView(R.id.fareTv)
    FontTextView fareTv;
    @BindView(R.id.promoTv)
    FontTextView promoTv;

    @BindView(R.id.dropOffDiscTv)
    FontTextView dropOffDiscTv;

    @BindView(R.id.walletTv)
    FontTextView walletTv;
    @BindView(R.id.driverRb)
    RatingBar driverRb;
    @BindView(R.id.passengerRb)
    RatingBar passengerRb;

    @BindView(R.id.btnProblem)
    FontTextView btnProblem;


    @BindView(R.id.tvWaitMins)
    FontTextView tvWaitMins;

    @BindView(R.id.tvWaitPrice)
    FontTextView tvWaitPrice;

    @BindView(R.id.rvComments)
    RecyclerView rvComments;
    private CommentsAdapter mAdapter;

    @BindView(R.id.flCommentsRv)
    FrameLayout flCommentsRv;
//    private String tripNo;

    ArrayList<Predefine_rating> ratingToShow = new ArrayList<>();
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
                    totalDistanceTv.setText(getString(R.string.kilometer_with_amount_ur,Math.round(Double.parseDouble(data.getInvoice().getKm()))));
                    float totalTime = Float.parseFloat(data.getInvoice().getMinutes());
                    float pricePerMin = Float.parseFloat(data.getInvoice().getPricePerMin());
                    float pricePerKm = Float.parseFloat(data.getInvoice().getPricePerKm());
                    totalTimeTv.setText(getString(R.string.minute_with_amount_ur,Math.round(totalTime)));
                    timePriceTv.setText(getString(R.string.display_integer_value,Math.round(Math.ceil(totalTime * pricePerMin))));
                    distancePriceTv.setText(getString(R.string.display_integer_value,Math.round(Math.ceil(pricePerKm))));
                    fareTv.setText(data.getInvoice().getTripCharges());
                    basefareTv.setText(data.getInvoice().getBaseFare());
                    totalAmountTv.setText(getString(R.string.display_string_value,data.getInvoice().getTotal()));
                    promoTv.setText(data.getInvoice().getPromo_deduction());
                    dropOffDiscTv.setText(data.getInvoice().getDropoff_discount());

//                    if(StringUtils.isNotBlank(data.getInvoice().getWaitMins())){
//                        float wait_min = Math.round(Float.parseFloat(data.getInvoice().getWaitMins()));
//                        if (wait_min > 1) {
//                            tvWaitMins.setText(wait_min + " Wait mins");
//                        } else {
//                            tvWaitMins.setText(wait_min + " Wait min");
//                        }
//                    }

                    if (StringUtils.isNotBlank(data.getInvoice().getWait_charges())) {
                        float wait_charges = Float.parseFloat(data.getInvoice().getWait_charges());
                        tvWaitPrice.setText(getString(R.string.display_integer_value,Math.round(Math.ceil(wait_charges))));
                    } else {
                        tvWaitPrice.setText("0");
                    }

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
                    String feedbackComments = StringUtils.EMPTY;
                    if (data.getPassRating() != null && data.getPassRating().getFeedback_message() != null
                            && StringUtils.isNotBlank(data.getPassRating().getFeedback_message()[0])) {
                        feedbackComments = data.getPassRating().getFeedback_message()[0];
                    }
                    if (StringUtils.isNotBlank(feedbackComments)) {
                        ratingToShow = new Gson().fromJson(feedbackComments, new TypeToken<ArrayList<Predefine_rating>>() {
                        }.getType());
                    } else {
                        rlFeedbackMsg1.setVisibility(View.GONE);
                        rlFeedbackMsg2.setVisibility(View.GONE);
                    }
                    if (ratingToShow != null && ratingToShow.size() > 0) {
//                        populatePredefineMsgs();
                        populateComments();
                    } else {
                        rlFeedbackMsg1.setVisibility(View.GONE);
                        rlFeedbackMsg2.setVisibility(View.GONE);
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

                ActivityStackManager.getInstance().startProblemActivity(mCurrentActivity, data);
                break;
        }
    }


    private void populateComments() {
        flCommentsRv.setVisibility(View.VISIBLE);

        GridLayoutManager layoutManager = new GridLayoutManager(mCurrentActivity, 6);
        final int span;
        span = ratingToShow.size() % 3;
        final int rows = ((ratingToShow.size() - 1) - span);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position <= rows) {
                    return 2;
                } else {
                    return span == 1 ? 6 : 3;
                }
            }
        });
        mAdapter = new CommentsAdapter(mCurrentActivity, ratingToShow);
        rvComments.setLayoutManager(layoutManager);
        rvComments.setHasFixedSize(true);
        rvComments.setAdapter(mAdapter);
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
        if (ratingToShow.size() > 0) {
            rlFeedbackMsg1.setVisibility(View.VISIBLE);
            tvMsg1.setVisibility(View.VISIBLE);
            tvMsg1.setText(ratingToShow.get(0).getMessage());
            tvMsg1.setTag(ratingToShow.get(0).get_id());
            if (ratingToShow.size() > 1) {
                tvMsg2.setVisibility(View.VISIBLE);
                tvMsg2.setText(ratingToShow.get(1).getMessage());
                tvMsg2.setTag(ratingToShow.get(1).get_id());
                if (ratingToShow.size() > 2) {
                    tvMsg3.setVisibility(View.VISIBLE);
                    tvMsg3.setText(ratingToShow.get(2).getMessage());
                    tvMsg3.setTag(ratingToShow.get(2).get_id());
                    if (ratingToShow.size() > 3) {
                        rlFeedbackMsg2.setVisibility(View.VISIBLE);
                        tvMsg4.setVisibility(View.VISIBLE);
                        tvMsg4.setText(ratingToShow.get(3).getMessage());
                        tvMsg4.setTag(ratingToShow.get(3).get_id());
                        if (ratingToShow.size() > 4) {
                            tvMsg5.setVisibility(View.VISIBLE);
                            tvMsg5.setText(ratingToShow.get(4).getMessage());
                            tvMsg5.setTag(ratingToShow.get(4).get_id());
                            if (ratingToShow.size() > 5) {
                                tvMsg6.setVisibility(View.VISIBLE);
                                tvMsg6.setText(ratingToShow.get(5).getMessage());
                                tvMsg6.setTag(ratingToShow.get(5).get_id());
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


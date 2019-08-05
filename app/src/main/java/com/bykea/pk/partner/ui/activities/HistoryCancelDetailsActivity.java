package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.TripHistoryData;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.AutoFitFontTextView;
import com.bykea.pk.partner.widgets.DashedLine;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HistoryCancelDetailsActivity extends BaseActivity {

    @BindView(R.id.startAddressTv)
    FontTextView startAddressTv;
    @BindView(R.id.endAddressTv)
    FontTextView endAddressTv;
    @BindView(R.id.nameTv)
    AutoFitFontTextView nameTv;
    @BindView(R.id.timeTv)
    FontTextView timeTv;
    @BindView(R.id.totalAmountTv)
    FontTextView totalAmountTv;
    @BindView(R.id.btnProblem)
    FontTextView btnProblem;

    @BindView(R.id.serviceTypeTv)
    FontTextView serviceTypeTv;
    @BindView(R.id.tvCancelBy)
    FontTextView tvCancelBy;
    @BindView(R.id.tvCancelFee)
    FontTextView tvCancelFee;
    @BindView(R.id.dotted_line)
    DashedLine dotted_line;
    @BindView(R.id.ic_pin)
    ImageView ic_pin;
    @BindView(R.id.centerLine)
    View centerLine;
    @BindView(R.id.lastLine)
    View lastLine;


    @BindView(R.id.tvCancelFeeLabel)
    FontTextView tvCancelFeeLabel;

    @BindView(R.id.tvTotalLabel)
    FontTextView tvTotalLabel;


    private HistoryCancelDetailsActivity mCurrentActivity;
    private final String REQUIRED_DATE_FORMAT = "dd MMM, hh:mm a";
    private final String CURRENT_DATE_FORMAT = "HH:mm:ss MM-dd-yyyy";
    private TripHistoryData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history_cancel_details);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ButterKnife.bind(this);
        mCurrentActivity = this;
        initViews();


    }

    private void initViews() {
        if (null != getIntent()) {
            try {
                data = (TripHistoryData) getIntent().getSerializableExtra(Constants.Extras.TRIP_DETAILS);
                setBackNavigation();
                setToolbarTitle(data.getTripNo().toUpperCase());
                hideToolbarLogo();
                startAddressTv.setText(data.getStartAddress());
                if (StringUtils.isNotBlank(data.getEndAddress())) {
                    endAddressTv.setVisibility(View.VISIBLE);
                    dotted_line.setVisibility(View.VISIBLE);
                    ic_pin.setVisibility(View.VISIBLE);
                    centerLine.setVisibility(View.VISIBLE);
                    endAddressTv.setText(data.getEndAddress());
                }
                timeTv.setText(Utils.getFormattedDate(data.getCancelTime(), CURRENT_DATE_FORMAT,
                        REQUIRED_DATE_FORMAT));
                serviceTypeTv.setText(StringUtils.capitalize(data.getTrip_type()));

                if (data.showCancelFee()) {
                    totalAmountTv.setVisibility(View.VISIBLE);
                    tvCancelFee.setVisibility(View.VISIBLE);
                    lastLine.setVisibility(View.VISIBLE);
                    tvTotalLabel.setVisibility(View.VISIBLE);
                    tvCancelFeeLabel.setVisibility(View.VISIBLE);

//                    if (StringUtils.isNotBlank(data.getCancel_feeNoCheck())) {
                    totalAmountTv.setText("Rs. " + ("0".equalsIgnoreCase(data.getCancel_fee()) ? "0" : "-" + data.getCancel_fee()));
                    tvCancelFee.setText("Rs. " + data.getCancel_fee());
//                    } else {
//                        totalAmountTv.setText("N/A");
//                        tvCancelFee.setText("N/A");
//                    }

                } else {
                    totalAmountTv.setVisibility(View.GONE);
                    tvCancelFee.setVisibility(View.GONE);
                    lastLine.setVisibility(View.GONE);
                    tvTotalLabel.setVisibility(View.GONE);
                    tvCancelFeeLabel.setVisibility(View.GONE);
                }


                tvCancelBy.setText(data.getCancel_by());
                if (Utils.getDaysInBetween(System.currentTimeMillis(), new SimpleDateFormat(CURRENT_DATE_FORMAT).parse(data.getCancelTime()).getTime()) >= AppPreferences.getSettings().getSettings().getTrip_support_max_days()) {
                    btnProblem.setVisibility(View.GONE);
                }
                nameTv.setText(data.getPassenger().getName());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @OnClick({R.id.btnProblem})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnProblem:
                if (data != null) {
//                    String params = "name=" + AppPreferences.getPilotData().getFullName() + "&booking=" + data.getTripNo() + "&phone=" + AppPreferences.getPilotData().getPhoneNo();
//                    Utils.startCustomWebViewActivity(mCurrentActivity, AppPreferences.getSettings().getSettings().getTrip_support_link() + params, data.getTripNo());
                    ActivityStackManager.getInstance().startComplainSubmissionActivity(mCurrentActivity,data);
                }
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}


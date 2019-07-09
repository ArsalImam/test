package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.adapters.ProblemItemsAdapter;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.widgets.FontTextView;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportActivity extends BaseActivity {

    private ReportActivity mCurrentActivity;
    private ProblemItemsAdapter mAdapter;
    private ArrayList<String> mReportList;
    private final String BOOKING_PROBLEM = "Booking se mutalik koi masla";
    private boolean isHelpShowing = false;
    private String[] contactReasonsSupervisors;
    private String[] contactReasonsFinance;

    @BindView(R.id.reportList)
    RecyclerView rvReportList;

    @BindView(R.id.ivBackBtn)
    ImageView ivBackBtn;
    @BindView(R.id.tvTitle)
    FontTextView tvTitle;
    @BindView(R.id.tvTitleUrdu)
    FontTextView tvTitleUrdu;
    @BindView(R.id.helpLayout)
    LinearLayout helpLayout;

    private String contactType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        mCurrentActivity = this;
        ButterKnife.bind(mCurrentActivity);
        helpLayout.setVisibility(View.GONE);
        setGreenActionBarTitle("Report", "رپورٹ");
        contactType = getIntent().getStringExtra(Constants.Extras.CONTACT_TYPE);
        createData();
        initRecyclerView();

    }

    private void initRecyclerView() {
        mAdapter = new ProblemItemsAdapter(mReportList, mCurrentActivity);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mCurrentActivity);
        rvReportList.setLayoutManager(mLayoutManager);
        rvReportList.setItemAnimator(new DefaultItemAnimator());
        rvReportList.setAdapter(mAdapter);
        mAdapter.setMyOnItemClickListener((position, view, reason) -> {
            if (reason.equalsIgnoreCase(BOOKING_PROBLEM)) {
                isHelpShowing = true;
                rvReportList.setVisibility(View.GONE);
                helpLayout.setVisibility(View.VISIBLE);
            } else {
                ActivityStackManager.getInstance().startProblemActivity(mCurrentActivity, null);
//                ActivityStackManager.getInstance().startReportPostActivity(mCurrentActivity, reason, contactType);
            }
        });
    }

    private void createData() {
        mReportList = new ArrayList<>();
        contactReasonsSupervisors = AppPreferences.getSettings().getPredefine_messages().getContact_reason();
        contactReasonsFinance = AppPreferences.getSettings().getPredefine_messages().getContact_reason_finance();

        if (contactReasonsSupervisors != null)
            Collections.addAll(mReportList, contactReasonsSupervisors);

        if (contactReasonsFinance != null)
            Collections.addAll(mReportList, contactReasonsFinance);
    }

    @Override
    public void onBackPressed() {
        if (isHelpShowing) {
            isHelpShowing = false;
            rvReportList.setVisibility(View.VISIBLE);
            helpLayout.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }
}

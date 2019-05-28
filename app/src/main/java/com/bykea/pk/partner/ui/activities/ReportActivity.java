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
    private String[] contactReasongs;

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
        mAdapter.setMyOnItemClickListener(new ProblemItemsAdapter.MyOnItemClickListener() {
            @Override
            public void onItemClickListener(int position, View view, String reason) {
                if (reason.equalsIgnoreCase(BOOKING_PROBLEM)) {
                    isHelpShowing = true;
                    rvReportList.setVisibility(View.GONE);
                    helpLayout.setVisibility(View.VISIBLE);
                } else {
                    ActivityStackManager.getInstance().startReportPostActivity(mCurrentActivity, reason, contactType);
                }
            }
        });
    }

    private void createData() {
        mReportList = new ArrayList<>();
        if (contactType.equalsIgnoreCase("f")){
            contactReasongs = AppPreferences.getSettings().getPredefine_messages().getContact_reason_finance();
        }else {
            contactReasongs = AppPreferences.getSettings().getPredefine_messages().getContact_reason();
        }
        if (contactReasongs != null){
            Collections.addAll(mReportList, contactReasongs);
        }

//        mReportList.add("App me masla hai");
//        mReportList.add("Mujhay request nahi arahi");
//        mReportList.add("Guarantee me masla he");
//        mReportList.add("Guarantee me masla he");
//        mReportList.add("Payout ka sms nahi mila");
//        mReportList.add("Penalty issue");
//        mReportList.add("Wallet ki Entry me masla he");
//        mReportList.add(BOOKING_PROBLEM);
//        mReportList.add("Others");
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

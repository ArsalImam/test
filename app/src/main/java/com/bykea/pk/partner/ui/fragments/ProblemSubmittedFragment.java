package com.bykea.pk.partner.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.ui.activities.ProblemActivity;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zendesk.support.guide.HelpCenterActivity;

public class ProblemSubmittedFragment extends Fragment {

    @BindView(R.id.tVIssueNumber)
    FontTextView tVIssueNumber;

    private ProblemActivity mCurrentActivity;

    public ProblemSubmittedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_problem_submitted, container, false);
        ButterKnife.bind(this, rootView);
        setRetainInstance(true);
        mCurrentActivity = (ProblemActivity) getActivity();

        tVIssueNumber.setText(getContext().getString(R.string.issue_no) + " " + getContext().getString(R.string.issue_submitted));

        return rootView;
    }

    @OnClick({R.id.submittedIssueDetail, R.id.navigateHomeScreen})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submittedIssueDetail:
                Utils.appToast(mCurrentActivity, "submittedIssueDetail");
                HelpCenterActivity.builder().show(mCurrentActivity);
                break;
            case R.id.navigateHomeScreen:
                ActivityStackManager.getInstance().startHomeActivity(mCurrentActivity);
                break;
        }
    }
}

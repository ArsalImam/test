package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.ui.fragments.ProblemListFragment;
import com.bykea.pk.partner.ui.fragments.ProblemSubmittedFragment;
import com.bykea.pk.partner.widgets.FontTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProblemActivity extends BaseActivity {

    private ProblemActivity mCurrentActivity;
    public String tripId;
    public String selectedReason;

    public static final String LIST_FRAGMENT = "LIST_FRAGMENT";
    public static final String DETAIL_FRAGMENT = "DETAIL_FRAGMENT";
    public static final String DETAIL_SUBMITTED_FRAGMENT = "DETAIL_SUBMITTED_FRAGMENT";

    @BindView(R.id.toolbar)
    FrameLayout toolbar;
    @BindView(R.id.ivBackBtn)
    ImageView ivBackBtn;
    @BindView(R.id.tvTitle)
    FontTextView tvTitle;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        mCurrentActivity = this;
        ButterKnife.bind(mCurrentActivity);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        tripId = getIntent().getStringExtra("TRIP_ID");
        fragmentManager = getSupportFragmentManager();
        loadFragment(new ProblemListFragment(), LIST_FRAGMENT);
        setGreenActionBarTitle(tripId, "");
    }

    public void loadFragment(Fragment fragment, String fragmentTag) {
        fragmentManager
                .beginTransaction()
                .replace(R.id.containerView, fragment, fragmentTag)
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .commit();
    }

    @Override
    public void onBackPressed() {
        /*switch (getSupportFragmentManager().getFragments()
                .get(1).getTag()) {

        }*/
        if (getSupportFragmentManager().findFragmentByTag(DETAIL_SUBMITTED_FRAGMENT) instanceof ProblemSubmittedFragment) {
            finish();
        } else {
            super.onBackPressed();
        }
    }
}

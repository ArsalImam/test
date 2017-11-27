package com.bykea.pk.partner.ui.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.ui.fragments.ProblemFragment;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.adapters.ProblemItemsAdapter;
import com.bykea.pk.partner.widgets.FontTextView;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProblemActivity extends BaseActivity {

    private ProblemActivity mCurrentActivity;
    private ProblemItemsAdapter mAdapter;
    private ArrayList<String> mProblemList;
    private LinearLayoutManager mLayoutManager;
    public String tripId;
    String[] probReasons;
    public String selectedReason;
    public boolean isMain = true,isSubmitted = false;

    @Bind(R.id.rvProblemList)
    RecyclerView rvProblemList;
    @Bind(R.id.toolbar)
    FrameLayout toolbar;
    @Bind(R.id.ivBackBtn)
    ImageView ivBackBtn;
    @Bind(R.id.tvTitle)
    FontTextView tvTitle;
    private android.support.v4.app.FragmentManager fragmentManager;

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
        loadFragment(new ProblemFragment(),true);
        setGreenActionbarTitle(tripId,"");
        mProblemList = new ArrayList<>();
        probReasons = AppPreferences.getSettings().getPredefine_messages().getReasons();
        copyList();
        initProblemList();
    }

    public void loadFragment(Fragment fragment, boolean isMainFrag) {
        setMain(isMainFrag);
        fragmentManager
                .beginTransaction()
                .replace(R.id.containerView, fragment)
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .commit();
    }

    public static Drawable setTint(Drawable d, int color) {
        Drawable wrappedDrawable = DrawableCompat.wrap(d);
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }

    private void copyList() {
        if (probReasons != null) {
            Collections.addAll(mProblemList, probReasons);
        } else {
            mProblemList.add("Partner ka ravaiya gair ikhlaqi tha");
            mProblemList.add("Partner ne aik gair mansooba stop kia");
            mProblemList.add("Partner ne khud safar ka aghaz kar k ikhtitam kardia, mere pas ai baghair).");
            mProblemList.add("Partner ne khud safar ka aghaz kar k ikhtitam kardia, mere pas ai baghair).");
            mProblemList.add("Parner ne baqaya raqam mere wallet me nahi daali");
            mProblemList.add("main haadse main malavas tha.");
        }
    }

    private void initProblemList() {
        mAdapter = new ProblemItemsAdapter(mProblemList);
        mLayoutManager = new LinearLayoutManager(mCurrentActivity);
        rvProblemList.setLayoutManager(mLayoutManager);
        rvProblemList.setItemAnimator(new DefaultItemAnimator());
        rvProblemList.setAdapter(mAdapter);
        mAdapter.setMyOnItemClickListener(new ProblemItemsAdapter.MyOnItemClickListener() {
            @Override
            public void onItemClickListener(int position, View view, String reason) {
                ActivityStackManager.getInstance(mCurrentActivity).startProblemPostActivity(mCurrentActivity, tripId, reason);
//                selectedReason = reason;
//                loadFragment(new PostProblemFragment());
            }
        });
    }

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean main) {
        isMain = main;
    }

    @Override
    public void onBackPressed() {
        if(isSubmitted){
            finish();
        } else if(isMain()){
            super.onBackPressed();
        }else{
            loadFragment(new ProblemFragment(),true);
        }
    }
}

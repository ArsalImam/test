package com.bykea.pk.partner.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.bykea.pk.partner.R;
import com.bykea.pk.partner.ui.activities.ProblemActivity;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.adapters.ProblemItemsAdapter;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProblemFragment extends Fragment {

    private ProblemActivity mCurrentActivity;
    private ProblemItemsAdapter mAdapter;
    private ArrayList<String> mProblemList;
    private LinearLayoutManager mLayoutManager;
    private String tripId ;
    String[] probReasons;

    @Bind(R.id.rvProblemList)
    RecyclerView rvProblemList;

    public ProblemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_problem, container, false);
        ButterKnife.bind(this, view);
        setRetainInstance(true);
        mCurrentActivity = (ProblemActivity) getActivity();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mProblemList = new ArrayList<>();
        probReasons = AppPreferences.getSettings().getPredefine_messages().getReasons();
        mCurrentActivity.findViewById(R.id.ivBackBtn).setVisibility(View.VISIBLE);
        copyList();
        initProblemList();
    }


    private void copyList() {
        if(probReasons != null) {
            Collections.addAll(mProblemList, probReasons);
        }else{
            mProblemList.add("Partner ka ravaiya gair ikhlaqi tha");
            mProblemList.add("Partner ne aik gair mansooba stop kia");
            mProblemList.add("Partner ne khud safar ka aghaz kar k ikhtitam kardia, mere pas ai baghair).");
            mProblemList.add("Partner ne khud safar ka aghaz kar k ikhtitam kardia, mere pas ai baghair).");
            mProblemList.add("Parner ne baqaya raqam mere wallet me nahi daali");
            mProblemList.add("main haadse main manavas tha.");
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
//                ActivityStackManager.getInstance().startProblemPostActivity(mCurrentActivity,tripId,reason);
                mCurrentActivity.selectedReason = reason;
                mCurrentActivity.loadFragment(new PostProblemFragment(),false);
            }
        });
    }
}

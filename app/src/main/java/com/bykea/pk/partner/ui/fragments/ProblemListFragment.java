package com.bykea.pk.partner.ui.fragments;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.bykea.pk.partner.R;
import com.bykea.pk.partner.ui.activities.ProblemActivity;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.adapters.ProblemItemsAdapter;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bykea.pk.partner.ui.activities.ProblemActivity.DETAIL_FRAGMENT;

public class ProblemListFragment extends Fragment {

    private ProblemActivity mCurrentActivity;
    private ProblemItemsAdapter mAdapter;
    private ArrayList<String> mProblemList;
    private LinearLayoutManager mLayoutManager;
    private String tripId;
    String[] probReasons;

    @BindView(R.id.rvProblemList)
    RecyclerView rvProblemList;

    public ProblemListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_problem_list, container, false);
        ButterKnife.bind(this, rootView);
        setRetainInstance(true);
        mCurrentActivity = (ProblemActivity) getActivity();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mProblemList = new ArrayList<>();
        probReasons = AppPreferences.getSettings().getPredefine_messages().getReasons();
        mCurrentActivity.findViewById(R.id.ivBackBtn).setVisibility(View.VISIBLE);
        copyList();
        setupAdapter();
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
            mProblemList.add("main haadse main manavas tha.");
        }
    }

    private void setupAdapter() {
        mAdapter = new ProblemItemsAdapter(mProblemList, mCurrentActivity);
        mLayoutManager = new LinearLayoutManager(mCurrentActivity);
        rvProblemList.setLayoutManager(mLayoutManager);
        rvProblemList.setItemAnimator(new DefaultItemAnimator());
        rvProblemList.setAdapter(mAdapter);

        mAdapter.setMyOnItemClickListener((position, view, reason) -> {
            mCurrentActivity.selectedReason = reason;
            mCurrentActivity.loadFragment(new ProblemDetailFragment(), DETAIL_FRAGMENT);
        });
    }
}

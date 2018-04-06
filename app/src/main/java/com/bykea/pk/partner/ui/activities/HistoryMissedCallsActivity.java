package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.TripHistoryData;
import com.bykea.pk.partner.models.response.TripMissedHistoryResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.adapters.HistoryMissedCallsAdapter;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryMissedCallsActivity extends BaseActivity {
    @BindView(R.id.noDataIv)
    ImageView noDataIv;
    @BindView(R.id.loader)
    ProgressBar loader;

    private UserRepository repository;
    private HistoryMissedCallsAdapter mHistoryAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<TripHistoryData> mHistoryList;


    private int page = 1;
    private int pages;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 10;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private HistoryMissedCallsActivity mCurrentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missed_trip_history);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ButterKnife.bind(this);
        mCurrentActivity = this;
        setBackNavigation();
        setToolbarTitle("Missed Jobs");
        hideToolbarLogo();
        initViews();
    }


    private void initViews() {

        repository = new UserRepository();
        mRecyclerView = (RecyclerView) findViewById(R.id.historyMissedCallsRV);

        mHistoryList = new ArrayList<>();

        mHistoryAdapter = new HistoryMissedCallsAdapter(mCurrentActivity, mHistoryList);
        mLayoutManager = new LinearLayoutManager(mCurrentActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mHistoryAdapter);


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = mRecyclerView.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold) && page < pages) {
                    // End has been reached
                    page++;
                    getHistory();

                    loading = true;
                }
            }
        });
        getHistory();

    }

    private void getHistory() {
        loader.setVisibility(View.VISIBLE);
        repository.requestMissedTripHistory(mCurrentActivity, callbackHandler, page + "");
    }


    private UserDataHandler callbackHandler = new UserDataHandler() {

        @Override
        public void onGetMissedTripHistory(final TripMissedHistoryResponse historyResponse) {
            mCurrentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loader.setVisibility(View.GONE);
                    if (historyResponse.isSuccess()) {
                        if (historyResponse.getData().size() > 0) {

                            pages = historyResponse.getPages() ;
                            noDataIv.setVisibility(View.GONE);
                            mHistoryList.addAll(historyResponse.getData());
                            mHistoryAdapter.notifyDataSetChanged();

                        } else {
                            showNoTripData();
                        }
                    } else {
                        showNoTripData();
                        if (historyResponse.getCode() == HTTPStatus.UNAUTHORIZED) {
                            Utils.onUnauthorized(mCurrentActivity);
                        }
                    }

                }
            });
        }

        @Override
        public void onError(final int errorCode, final String errorMessage) {
            if (mCurrentActivity != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loader.setVisibility(View.GONE);
                        Dialogs.INSTANCE.showToast(mCurrentActivity, errorMessage);
                        if (errorCode == HTTPStatus.UNAUTHORIZED) {
                            Utils.logout(mCurrentActivity);
                        }

                    }
                });
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setToolbarLogo();
        hideToolbarTitle();
    }

    private void showNoTripData() {
        noDataIv.setImageDrawable(ContextCompat.getDrawable(mCurrentActivity, R.drawable.no_data));
        noDataIv.setVisibility(View.VISIBLE);
    }
}


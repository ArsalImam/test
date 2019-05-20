package com.bykea.pk.partner.ui.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.TripHistoryData;
import com.bykea.pk.partner.models.response.TripHistoryResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.adapters.HistoryAdapter;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TripHistoryFragment extends Fragment {

    //no_data
    @BindView(R.id.noDataIv)
    ImageView noDataIv;
    @BindView(R.id.loader)
    ProgressBar loader;

    private UserRepository repository;
    private HistoryAdapter mHistoryAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<TripHistoryData> mHistoryList;

    private int page = 1;
    private int pages;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 10;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    private Unbinder unbinder;

    private HomeActivity mCurrentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_history, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCurrentActivity = (HomeActivity) getActivity();
        mCurrentActivity.setToolbarTitle(getString(R.string.trip_history_title),
                getString(R.string.trip_history_title_ur));
        mCurrentActivity.hideToolbarLogo();
        mCurrentActivity.hideStatusCompletely();
        mCurrentActivity.showStatusLayout();
        mCurrentActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCurrentActivity.findViewById(R.id.toolbarLine).setVisibility(View.VISIBLE);
        initViews(view);
    }

    private String verifyData(String value) {
        return StringUtils.isNotBlank(value) ? value : StringUtils.EMPTY;
    }

    private void initViews(View view) {

        repository = new UserRepository();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.historyRV);

        mHistoryList = new ArrayList<>();

        mHistoryAdapter = new HistoryAdapter(mCurrentActivity, mHistoryList);
        mHistoryAdapter.setMyOnItemClickListener(new HistoryAdapter.MyOnItemClickListener() {
            @Override
            public void onItemClickListener(int position, View view, TripHistoryData historyData) {
                if (!historyData.getStatus().equalsIgnoreCase("cancelled")) {
                    if (historyData.getInvoice() != null) {
                        ActivityStackManager.getInstance().startCompletedDetailsActivity(historyData, mCurrentActivity);
                        HomeActivity.visibleFragmentNumber = 2;
                    }
                } else {
                    ActivityStackManager.getInstance().startCancelDetailsActivity(historyData, mCurrentActivity);
                }
            }
        });
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
        setMissedCallsIcon();
        getHistory();

    }

    private void getHistory() {
        if (loader != null) {
            loader.setVisibility(View.VISIBLE);
        }
        repository.requestTripHistory(mCurrentActivity, callbackHandler, page + "");
    }

    private void setMissedCallsIcon() {
        mCurrentActivity.showMissedCallIcon(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityStackManager.getInstance().startMissedCallsActivity(mCurrentActivity);
            }
        });
    }

    @Override
    public void onDestroyView() {
        mCurrentActivity.hideMissedCallIcon();
        mCurrentActivity.hideUrduTitle();
        super.onDestroyView();
        unbinder.unbind();
    }

    private UserDataHandler callbackHandler = new UserDataHandler() {
        @Override
        public void onGetTripHistory(final TripHistoryResponse historyResponse) {
            mCurrentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (getView() != null) {
                        if (loader != null) {
                            loader.setVisibility(View.GONE);
                        }
                        if (historyResponse.isSuccess()) {
                            if (historyResponse.getData().size() > 0) {

                                pages = historyResponse.getPages();
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
                }
            });
        }

        @Override
        public void onError(final int errorCode, final String errorMessage) {
            if (mCurrentActivity != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (getView() != null) {
                            if (loader != null) {
                                loader.setVisibility(View.GONE);
                            }
                            Dialogs.INSTANCE.showToast(mCurrentActivity, errorMessage);
                            if (errorCode == HTTPStatus.UNAUTHORIZED) {
                                Utils.logout(mCurrentActivity);
                            }
                        }
                    }
                });
            }
        }
    };

    private void showNoTripData() {
        noDataIv.setImageDrawable(ContextCompat.getDrawable(mCurrentActivity, R.drawable.no_data));
        noDataIv.setVisibility(View.VISIBLE);
    }
}

package com.bykea.pk.partner.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bykea.pk.partner.ui.activities.HistoryDetailActivity;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.bykea.pk.partner.ui.helpers.adapters.HistoryAdapter;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.TripHistoryData;
import com.bykea.pk.partner.models.response.TripHistoryResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TripHistoryFragment extends Fragment {

    @Bind(R.id.noDataIv)
    ImageView noDataIv;
    @Bind(R.id.loader)
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

    private HomeActivity mCurrentActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_history, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCurrentActivity = (HomeActivity) getActivity();
        mCurrentActivity.setToolbarTitle("Trip History");
        mCurrentActivity.hideToolbarLogo();
        mCurrentActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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
                    /*Intent intent = new Intent(mCurrentActivity, HistoryDetailActivity.class);
                    intent.putExtra("historyData", historyData);
                    startActivity(intent);*/

                    Intent intent = new Intent(mCurrentActivity, HistoryDetailActivity.class);
                    intent.putExtra("status", historyData.getStatus());
                    intent.putExtra("trip_no", historyData.getTripNo());
                    intent.putExtra("saddress", historyData.getStartAddress());
                    intent.putExtra("eaddress", historyData.getEndAddress());
                    intent.putExtra("name", historyData.getPassenger() != null && StringUtils.isNotBlank(historyData.getPassenger().getName())
                            ? historyData.getPassenger().getName() : "N/A");
                    intent.putExtra("time", historyData.getAcceptTime());
                    if (historyData.getInvoice() != null) {
                        intent.putExtra("total_distance", verifyData(historyData.getInvoice().getKm()));
                        intent.putExtra("total_time", verifyData(historyData.getInvoice().getMinutes()));
                        intent.putExtra("amount", verifyData(historyData.getInvoice().getTripCharges()));
                        intent.putExtra("receivedAmount", verifyData(historyData.getInvoice().getTotal()));
                        intent.putExtra("basefare", verifyData(historyData.getInvoice().getBaseFare()));
                        intent.putExtra("pricePerMin", verifyData(historyData.getInvoice().getPricePerMin()));
                        intent.putExtra("pricePerKm", verifyData(historyData.getInvoice().getPricePerKm()));
                        intent.putExtra("promo", verifyData(historyData.getInvoice().getPromo_deduction()));
                        intent.putExtra("wallet", verifyData(historyData.getInvoice().getWallet_deduction()));
                        intent.putExtra("start_balance", verifyData(historyData.getInvoice().getStart_balance()));
                    } else {
                        return; // When there is no invoice data there's no need to show details. The data for this Trip is already invalid.
                    }

                    intent.putExtra("plate", historyData.getDriver().getPlate_no());
                    intent.putExtra("type", historyData.getTrip_type());
                    if (historyData.getDriverRating() != null && StringUtils.isNotBlank(historyData.getDriverRating().getRate())) {
                        intent.putExtra("driverRating", historyData.getDriverRating().getRate());
                    }
                    if (historyData.getPassRating() != null && StringUtils.isNotBlank(historyData.getPassRating().getRate())) {
                        intent.putExtra("passRating", historyData.getPassRating().getRate());
                    }
                    if (historyData.getPassRating() != null && historyData.getPassRating().getFeedback_message() != null
                            && historyData.getPassRating().getFeedback_message().length > 0
                            && StringUtils.isNotBlank(historyData.getPassRating().getFeedback_message()[0])) {
                        intent.putExtra("feedbackComments", historyData.getPassRating().getFeedback_message()[0]);
                    }
                    startActivity(intent);
                    HomeActivity.visibleFragmentNumber = 2;
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
                ActivityStackManager.getInstance(mCurrentActivity).startMissedCallsActivity();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        mCurrentActivity.hideMissedCallIcon();
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
                                noDataIv.setVisibility(View.VISIBLE);
                            }
                        } else {
                            noDataIv.setVisibility(View.VISIBLE);
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
}

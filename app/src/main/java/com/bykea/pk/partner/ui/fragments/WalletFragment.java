package com.bykea.pk.partner.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.WalletData;
import com.bykea.pk.partner.models.response.WalletHistoryResponse;
import com.bykea.pk.partner.repositories.IUserDataHandler;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.bykea.pk.partner.ui.helpers.adapters.WalletHistoryAdapter;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class WalletFragment extends Fragment {

    @BindView(R.id.noDataIv)
    ImageView noDataIv;
    @BindView(R.id.loader)
    ProgressBar loader;
    @BindView(R.id.totalAmountTv)
    TextView totalAmountTv;
    @BindView(R.id.llTitle)
    LinearLayout llTitle;
    @BindView(R.id.rlTop)
    RelativeLayout rlTop;
    private Unbinder unbinder;

    private UserRepository repository;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<WalletData> mHistoryList;
    private HomeActivity mCurrentActivity;

    private String nextPage = StringUtils.EMPTY;
    private int pages;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 10;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private WalletHistoryAdapter mHistoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCurrentActivity = (HomeActivity) getActivity();
        mCurrentActivity.setToolbarTitle("Wallet", "بٹوا");
        mCurrentActivity.hideToolbarLogo();
        mCurrentActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCurrentActivity.findViewById(R.id.toolbarLine).setVisibility(View.VISIBLE);
        initViews(view);
    }

    private void initViews(View view) {
        repository = new UserRepository();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.walletHistoryRV);

        mHistoryList = new ArrayList<>();

        mHistoryAdapter = new WalletHistoryAdapter(mCurrentActivity, mHistoryList);

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
                        <= (firstVisibleItem + visibleThreshold) && StringUtils.isNotBlank(nextPage) && !nextPage.equalsIgnoreCase("0")) {
                    // End has been reached
                    getHistory();

                    loading = true;
                }
            }
        });
//        setWalletIcon();
        getHistory();

    }


    private void setWalletIcon() {
        mCurrentActivity.showWalletIcon(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityStackManager.getInstance().startRequestPaymentActivity(mCurrentActivity);
            }
        });
    }


    private void getHistory() {
        if (loader != null) {
            loader.setVisibility(View.VISIBLE);
        }
        repository.requestWalletHistory(mCurrentActivity, callbackHandler, nextPage + "");
    }


    @Override
    public void onDestroyView() {
        mCurrentActivity.hideUrduTitle();
        super.onDestroyView();
        unbinder.unbind();
//        mCurrentActivity.hideWalletIcon();
    }


    private IUserDataHandler callbackHandler = new UserDataHandler() {


        @Override
        public void getWalletData(final WalletHistoryResponse response) {
            if (mCurrentActivity != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (getView() != null) {
                            loader.setVisibility(View.GONE);
                            if (response.isSuccess()) {
                                if (StringUtils.isNotBlank(response.getTotal_amount())) {
                                    totalAmountTv.setText("Rs " + response.getTotal_amount());
                                } /*else {
                                    totalAmountTv.setText("Rs 0");
                                }*/
                                if (response.getData().size() > 0) {
                                    llTitle.setVisibility(View.VISIBLE);
                                    rlTop.setVisibility(View.VISIBLE);
                                    nextPage = response.getPage();
                                    noDataIv.setVisibility(View.GONE);
                                    mHistoryList.addAll(response.getData());
                                    mHistoryAdapter.notifyDataSetChanged();
                                } else {
                                    if (mLayoutManager.getItemCount() == 0) {
                                        showNoTripData();
                                    }
                                }
                            } else {
                                if (mLayoutManager.getItemCount() == 0) {
                                    showNoTripData();
                                }
                                if (response.getCode() == HTTPStatus.UNAUTHORIZED) {
                                    Utils.onUnauthorized(mCurrentActivity);
                                }
                            }
                        }
                    }
                });
            }
        }

        @Override
        public void onError(final int errorCode, final String errorMessage) {
            if (mCurrentActivity != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (getView() != null) {
                            loader.setVisibility(View.GONE);
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

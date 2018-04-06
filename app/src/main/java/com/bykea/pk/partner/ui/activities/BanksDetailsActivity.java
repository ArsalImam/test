package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.BankData;
import com.bykea.pk.partner.models.response.BankDetailsResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.adapters.BankDetailsAdapter;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BanksDetailsActivity extends BaseActivity {


    @BindView(R.id.bankAccountsView)
    RecyclerView mRecyclerView;
    @BindView(R.id.loader)
    ProgressBar loader;
    @BindView(R.id.llDetails)
    LinearLayout llDetails;
    @BindView(R.id.accountTitle)
    FontTextView accountTitle;
    @BindView(R.id.accountNumber)
    FontTextView accountNumber;


    private BanksDetailsActivity mCurrentActivity;
    private UserRepository mRepository;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<BankData.BankAgentData> mList;
    private BankDetailsAdapter mAdapter;


    private String nextPage = StringUtils.EMPTY;
    private int pages;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 10;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_list);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        mCurrentActivity = this;
        ButterKnife.bind(this);

        setTitleCustomToolbarWithUrdu("Bykea Bank", "بائیکیا بینک");
//        setBackNavigation();
//        hideToolbarLogo();

        initViews();

    }

    private void initViews() {
        llDetails.setVisibility(View.VISIBLE);
        mList = new ArrayList<>();
        mRepository = new UserRepository();
        mAdapter = new BankDetailsAdapter(mCurrentActivity, mList);

        mLayoutManager = new LinearLayoutManager(mCurrentActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        callApi();
    }

    private void callApi() {
        if (getIntent() != null) {
            BankData data = getIntent().getParcelableExtra(Constants.Extras.SELECTED_ITEM);
            if (data != null && StringUtils.isNotBlank(data.get_id())) {
                initBankDetails(data);
                loader.setVisibility(View.VISIBLE);
                mRepository.requestBankAccountsDetails(mCurrentActivity, data.get_id(), handler);
            }
        }
    }

    private void initBankDetails(BankData bankData) {
        accountNumber.setText(bankData.getAccountNumber());
        accountTitle.setText(bankData.getAccountTitle());
    }


    private UserDataHandler handler = new UserDataHandler() {

        @Override
        public void onBankDetailsResponse(BankDetailsResponse response) {
            loader.setVisibility(View.GONE);
            if (response.isSuccess() && response.getData() != null) {
                if (response.getData().size() > 0) {
                    nextPage = response.getPage();
                    mList.addAll(response.getData());
                    mAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onError(int errorCode, String errorMessage) {
            loader.setVisibility(View.GONE);
            Utils.appToast(mCurrentActivity, errorMessage);
            if (errorCode == HTTPStatus.UNAUTHORIZED) {
                Utils.onUnauthorized(mCurrentActivity);
            }
        }
    };
}

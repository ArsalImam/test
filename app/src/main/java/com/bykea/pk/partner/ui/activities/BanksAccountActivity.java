package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.BankData;
import com.bykea.pk.partner.models.response.BankAccountListResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.adapters.BankAccountsAdapter;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BanksAccountActivity extends BaseActivity {


    @BindView(R.id.bankAccountsView)
    RecyclerView mRecyclerView;
    @BindView(R.id.loader)
    ProgressBar loader;
    private BanksAccountActivity mCurrentActivity;
    private UserRepository mRepository;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<BankData> mList;
    private BankAccountsAdapter mAdapter;


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

        setTitleCustomToolbarWithUrdu("Bykea Bank","بائیکیا بینک" );
//        setBackNavigation();
//        hideToolbarLogo();

        initViews();

    }

    private void initViews() {
        mList = new ArrayList<>();
        mRepository = new UserRepository();
        mAdapter = new BankAccountsAdapter(mCurrentActivity, mList);
        mAdapter.setOnItemClickListener(new BankAccountsAdapter.MyOnItemClickListener() {
            @Override
            public void onItemClickListener(int position, View view, BankData data) {
                ActivityStackManager.getInstance().startBankDetailsActivity(mCurrentActivity, data);
            }
        });

        mLayoutManager = new LinearLayoutManager(mCurrentActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);


        /*lv.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = lv.getChildCount();
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
                    callApi();

                    loading = true;
                }
            }
        });*/
        callApi();
    }

    private void callApi() {

        loader.setVisibility(View.VISIBLE);
        mRepository.requestBankAccounts(mCurrentActivity, handler);
    }


    private UserDataHandler handler = new UserDataHandler() {

        @Override
        public void getAccountNumbers(BankAccountListResponse response) {
            loader.setVisibility(View.GONE);
            if (response.isSuccess()) {
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

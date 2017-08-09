package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.AccountsData;
import com.bykea.pk.partner.models.response.AccountNumbersResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.adapters.BankAccountsAdapter;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AccountsListActivity extends BaseActivity {


    @Bind(R.id.bankAccountsView)
    RecyclerView bankAccountsView;
    @Bind(R.id.noDataIv)
    ImageView noDataIv;
    @Bind(R.id.loader)
    ProgressBar loader;
    private AccountsListActivity mCurrentActivity;
    private UserRepository repository;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<AccountsData> mAccountsList;
    private BankAccountsAdapter mAccountsAdapter;


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

        setToolbarTitle("Bank Accounts");
        setBackNavigation();
        hideToolbarLogo();

        initViews();

    }

    private void initViews() {
        mAccountsList = new ArrayList<>();
        repository = new UserRepository();
        mAccountsAdapter = new BankAccountsAdapter(mCurrentActivity, mAccountsList);

        mLayoutManager = new LinearLayoutManager(mCurrentActivity);
        bankAccountsView.setLayoutManager(mLayoutManager);
        bankAccountsView.setItemAnimator(new DefaultItemAnimator());
        bankAccountsView.setAdapter(mAccountsAdapter);


        bankAccountsView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = bankAccountsView.getChildCount();
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
        getHistory();
    }

    private void getHistory() {

        loader.setVisibility(View.VISIBLE);
        repository.requestAccountNumbers(mCurrentActivity, handler, nextPage);
    }


    private UserDataHandler handler = new UserDataHandler() {

        @Override
        public void getAccountNumbers(AccountNumbersResponse response) {
            loader.setVisibility(View.GONE);
            if (response.isSuccess()) {
                if (response.getData().size() > 0) {
                    nextPage = response.getPage();
                    noDataIv.setVisibility(View.GONE);
                    mAccountsList.addAll(response.getData());
                    mAccountsAdapter.notifyDataSetChanged();
                } else {
                    noDataIv.setVisibility(View.VISIBLE);
                }
            } else {
                noDataIv.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onError(int errorCode, String errorMessage) {
            loader.setVisibility(View.GONE);
            Dialogs.INSTANCE.showError(mCurrentActivity, noDataIv, errorMessage);
            if (errorCode == HTTPStatus.UNAUTHORIZED) {
                Utils.onUnauthorized(mCurrentActivity);
            }
        }
    };
}

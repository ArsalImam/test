package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.CitiesData;
import com.bykea.pk.partner.models.data.ShahkarData;
import com.bykea.pk.partner.models.response.GetZonesResponse;
import com.bykea.pk.partner.models.response.ShahkarResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.fragments.ZoneFragment;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.adapters.ShahkarAdapter;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShahkarActivity extends BaseActivity {

    @BindView(R.id.shahkar_rv)
    RecyclerView mRecyclerView;

    ShahkarActivity mCurrentActivity;
    private UserRepository mRepository;
    private List<ShahkarData> listShahkar;
    private ShahkarAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shahkar);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ButterKnife.bind(this);
        mCurrentActivity = this;

        mRepository = new UserRepository();

        setBackNavigation();

        setToolbarTitle("Top 10 Partners");
        hideToolbarLogo();

        setupRecyclerview();

        getShahkarData();
    }

    private void getShahkarData() {
        try{
            ShahkarResponse response = (ShahkarResponse) AppPreferences.getObjectFromSharedPref(ShahkarResponse.class);
            if (response != null && response.getData() != null
                    && response.getData().size() > 0 ) {
                onApiResponse(response);
            } else {
                Dialogs.INSTANCE.showLoader(mCurrentActivity);
                mRepository.requestShahkar(mCurrentActivity, mCallBack);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void onApiResponse(ShahkarResponse response) {
        if (mCurrentActivity != null) {

            if (response.getData() != null && response.getData().size() > 0) {
                for (int i=0; i<response.getData().size(); i++){
                    response.getData().get(i).setNumber(i+1);
                    listShahkar.add(response.getData().get(i));

                }
            }
            adapter.notifyDataSetChanged();

            Dialogs.INSTANCE.dismissDialog();
        }
    }

    private void setupRecyclerview() {
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(Utils.newLLM(mCurrentActivity));

        listShahkar = new ArrayList<>();

        adapter = new ShahkarAdapter(listShahkar);
        mRecyclerView.setAdapter(adapter);
    }

    private UserDataHandler mCallBack = new UserDataHandler() {

        @Override
        public void onShahkarResponse(ShahkarResponse response) {
            AppPreferences.setObjectToSharedPref(response);
            onApiResponse(response);
        }

        @Override
        public void onError(int errorCode, String errorMessage) {
            if (mCurrentActivity != null) {
                Dialogs.INSTANCE.dismissDialog();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listShahkar = null;
        mCurrentActivity = null;
        adapter = null;
        mRepository = null;
        mRecyclerView = null;
    }
}

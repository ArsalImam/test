package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.HaftaBookingBonusModel;
import com.bykea.pk.partner.models.data.Ranking;
import com.bykea.pk.partner.models.data.RankingPosition;
import com.bykea.pk.partner.models.data.RankingResponse;
import com.bykea.pk.partner.models.data.WeeklyBonus;
import com.bykea.pk.partner.models.response.GetZonesResponse;
import com.bykea.pk.partner.models.response.ShahkarResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.adapters.RankingWeeklyStatsAdapter;
import com.bykea.pk.partner.utils.Dialogs;
import com.thefinestartist.finestwebview.enums.Position;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RankingActivity extends BaseActivity {

    private RankingActivity mCurrentActivity;

    @BindView(R.id.stats_weekly_rv)
    RecyclerView mRecyclerView2;

    @BindView(R.id.priceTv_driver1)
    TextView priceTv_driver1;

    @BindView(R.id.priceTv_driver2)
    TextView priceTv_driver2;

    @BindView(R.id.priceTv_driver3)
    TextView priceTv_driver3;

    @BindView(R.id.commisionTv_driver1)
    TextView commisionTv_driver1;

    @BindView(R.id.commisionTv_driver2)
    TextView commisionTv_driver2;

    @BindView(R.id.commisionTv_driver3)
    TextView commisionTv_driver3;

    @BindView(R.id.bookingTv_driver1)
    TextView bookingTv_driver1;

    @BindView(R.id.bookingTv_driver2)
    TextView bookingTv_driver2;

    @BindView(R.id.bookingTv_driver3)
    TextView bookingTv_driver3;

    @BindView(R.id.insuranceTv_driver1)
    TextView insuranceTv_driver1;

    @BindView(R.id.insuranceTv_driver2)
    TextView insuranceTv_driver2;

    @BindView(R.id.insuranceTv_driver3)
    TextView insuranceTv_driver3;

    @BindView(R.id.rankingTv1)
    TextView ranking_goldTv;

    @BindView(R.id.rankingTv2)
    TextView ranking_silverTv;

    @BindView(R.id.rankingTv3)
    TextView ranking_plateniumTv;



    private UserRepository mRepository;
    private RankingWeeklyStatsAdapter adapter;
    private List<HaftaBookingBonusModel> listHaftaBonusBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ButterKnife.bind(this);
        mCurrentActivity = this;

        mRepository = new UserRepository();

        setBackNavigation();

        setToolbarTitle("Ranking", "درجہ بندی");
        hideToolbarLogo();

        setupRecyclerview();

        getBonusData();
    }

    private void getBonusData() {
        RankingResponse response = (RankingResponse) AppPreferences.getObjectFromSharedPref(GetZonesResponse.class);
        if (response != null && response.getData() != null) {
            onApiResponse(response);
        } else {
            Dialogs.INSTANCE.showLoader(mCurrentActivity);
            mRepository.requestBonusChart(mCurrentActivity, mCallBack);
        }
    }

    private void onApiResponse(RankingResponse response) {
        if (mCurrentActivity != null) {

            if (response.getData() != null) {


                for (WeeklyBonus weeklyBonus : response.getData().
                        getWeeklyBonus()) {
                    listHaftaBonusBooking.add(new HaftaBookingBonusModel(weeklyBonus.getBonus(),
                            weeklyBonus.getBooking()));
                }

                for (Ranking ranking : response.getData().getRanking()) {

                    switch (ranking.getRank()) {
                        case "1": {

                            priceTv_driver2.setText(ranking.getCredit());
                            commisionTv_driver2.setText(ranking.getComision());
                            bookingTv_driver2.setText(ranking.getBooking());
                            insuranceTv_driver2.setText(ranking.getInsurance());

                            break;
                        }

                        case "2": {
                            priceTv_driver1.setText(ranking.getCredit());
                            commisionTv_driver1.setText(ranking.getComision());
                            bookingTv_driver1.setText(ranking.getBooking());
                            insuranceTv_driver1.setText(ranking.getInsurance());
                            break;
                        }

                        case "3": {
                            priceTv_driver3.setText(ranking.getCredit());
                            commisionTv_driver3.setText(ranking.getComision());
                            bookingTv_driver3.setText(ranking.getBooking());
                            insuranceTv_driver3.setText(ranking.getInsurance());
                            break;
                        }


                    }
                }

                for (RankingPosition rankingPosition : response.getData().getPosition()) {
                    switch (rankingPosition.getName()){



                        case "Gold":{
                            ranking_goldTv.setText(rankingPosition.getScore());
                            break;
                        }

                        case "Silver":{
                            ranking_silverTv.setText(rankingPosition.getScore());
                            break;
                        }

                        case "Platinum":{
                            ranking_plateniumTv.setText(rankingPosition.getScore());
                            break;
                        }
                    }

                }





                adapter.notifyDataSetChanged();

                Dialogs.INSTANCE.dismissDialog();
            }
        }

    }

    private void setupRecyclerview() {

        mRecyclerView2.setHasFixedSize(true);


        mRecyclerView2.setLayoutManager(newLLM());

        listHaftaBonusBooking = new ArrayList<>();

        adapter = new RankingWeeklyStatsAdapter(listHaftaBonusBooking);
        mRecyclerView2.setAdapter(adapter);
    }

    private LinearLayoutManager newLLM() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mCurrentActivity);
        return linearLayoutManager;
    }

    private UserDataHandler mCallBack = new UserDataHandler() {

        @Override
        public void onBonusChartResponse(RankingResponse response) {
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
}

package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.RankingStatsTypeModel;
import com.bykea.pk.partner.models.data.RankingWeeklyStatsModel;
import com.bykea.pk.partner.ui.helpers.adapters.RankingWeeklyStatsAdapter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ButterKnife.bind(this);
        mCurrentActivity = this;

        setBackNavigation();

        setToolbarTitle("Ranking", "درجہ بندی");
        hideToolbarLogo();

        setupRecyclerview();
    }

    private void setupRecyclerview() {

        mRecyclerView2.setHasFixedSize(true);


        mRecyclerView2.setLayoutManager(newLLM());

        List<RankingStatsTypeModel> list = new ArrayList<>();

        /*list.add(new RankingStatsTypeModel("Rs. 2,500", "Rs. 5,000",
                "Rs. 20,000", "کریڈیٹ"));*/

        priceTv_driver1.setText("Rs. 2,500");
        priceTv_driver2.setText( "Rs. 5,000");
        priceTv_driver3.setText("Rs. 20,000");

        commisionTv_driver1.setText("15%");
        commisionTv_driver2.setText("10%");
        commisionTv_driver3.setText("5%");

        bookingTv_driver1.setText("صرف رائیڈ");
        bookingTv_driver2.setText("رائیڈ اور خریداری");
        bookingTv_driver3.setText("تمام");

        insuranceTv_driver1.setText("نو انشورنس");
        insuranceTv_driver2.setText("فری");
        insuranceTv_driver3.setText("فری");




        List<RankingWeeklyStatsModel> list_weekly = new ArrayList<>();

        list_weekly.add(new RankingWeeklyStatsModel("Rs. 5,500", "105"));

        list_weekly.add(new RankingWeeklyStatsModel("4,000", "90"));

        list_weekly.add(new RankingWeeklyStatsModel("3,000", "75"));

        list_weekly.add(new RankingWeeklyStatsModel("2,200", "60"));

        list_weekly.add(new RankingWeeklyStatsModel("1,500", "45"));

        list_weekly.add(new RankingWeeklyStatsModel("900", "30"));

        list_weekly.add(new RankingWeeklyStatsModel("400", "15"));

        RankingWeeklyStatsAdapter adapter1 = new RankingWeeklyStatsAdapter(list_weekly);
        mRecyclerView2.setAdapter(adapter1);
    }

    private LinearLayoutManager newLLM() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mCurrentActivity);
        return linearLayoutManager;
    }
}

package com.bykea.pk.partner.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.RankingStatsTypeModel;
import com.bykea.pk.partner.models.data.RankingWeeklyStatsModel;
import com.bykea.pk.partner.ui.helpers.adapters.RankingStatsTypeAdapter;
import com.bykea.pk.partner.ui.helpers.adapters.RankingWeeklyStatsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RankingActivity extends BaseActivity {

    private RankingActivity mCurrentActivity;

    @BindView(R.id.stats_rv)
    RecyclerView mRecyclerView1;

    @BindView(R.id.stats_weekly_rv)
    RecyclerView mRecyclerView2;

    @BindView(R.id.priceTv_driver1)
    TextView priceTv_driver1;

    @BindView(R.id.priceTv_driver2)
    TextView priceTv_driver2;

    @BindView(R.id.priceTv_driver3)
    TextView priceTv_driver3;

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
        mRecyclerView1.setHasFixedSize(true);
        mRecyclerView2.setHasFixedSize(true);

        mRecyclerView1.setLayoutManager(newLLM());
        mRecyclerView2.setLayoutManager(newLLM());

        List<RankingStatsTypeModel> list = new ArrayList<>();

        /*list.add(new RankingStatsTypeModel("Rs. 2,500", "Rs. 5,000",
                "Rs. 20,000", "کریڈیٹ"));*/

        priceTv_driver1.setText("Rs. 2,500");
        priceTv_driver2.setText( "Rs. 5,000");
        priceTv_driver3.setText("Rs. 20,000");
        

        list.add(new RankingStatsTypeModel("15%", "10%",
                "5%", " کمیشن"));

        list.add(new RankingStatsTypeModel("صرف رائیڈ", "رائیڈ اور خریداری",
                "تمام", "بکنگز"));

        list.add(new RankingStatsTypeModel("نو انشورنس", "فری",
                "فری", "انشورنس"));

        RankingStatsTypeAdapter adapter = new RankingStatsTypeAdapter(list);
        mRecyclerView1.setAdapter(adapter);

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

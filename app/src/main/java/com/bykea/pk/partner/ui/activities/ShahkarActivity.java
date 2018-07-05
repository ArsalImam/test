package com.bykea.pk.partner.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.ShahkarModel;
import com.bykea.pk.partner.ui.helpers.adapters.ShahkarAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShahkarActivity extends BaseActivity {

    @BindView(R.id.shahkar_rv)
    RecyclerView mRecyclerView;

    ShahkarActivity mCurrentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shahkar);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ButterKnife.bind(this);
        mCurrentActivity = this;

        setBackNavigation();

        setToolbarTitle("Top 10 Partners");
        hideToolbarLogo();

        setupRecyclerview();
    }

    private void setupRecyclerview() {
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mCurrentActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);

        List<ShahkarModel> list = new ArrayList<>();
        for (int i=1; i < 11; i++){
            list.add(new ShahkarModel(i, "Syed Naeem Khan", 90, 5.0f, 35000));
        }

        ShahkarAdapter adapter = new ShahkarAdapter(list);
        mRecyclerView.setAdapter(adapter);
    }
}

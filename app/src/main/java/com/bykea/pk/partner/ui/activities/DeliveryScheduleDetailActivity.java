package com.bykea.pk.partner.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.bykea.pk.partner.R;

import butterknife.ButterKnife;

public class DeliveryScheduleDetailActivity extends BaseActivity {

    private DeliveryScheduleDetailActivity mCurrentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_schedule_detail);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        mCurrentActivity = this;
        ButterKnife.bind(mCurrentActivity);
        setTitleCustomToolbarWithUrdu("XYZ123", "");
    }

}
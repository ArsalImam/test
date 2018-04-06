package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.PersonalInfoData;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MotorbikeActivity extends BaseActivity {

    @BindView(R.id.vehicleBrand)
    FontTextView vehicleBrand;
    @BindView(R.id.horsePower)
    FontTextView horsePower;
    @BindView(R.id.modelYear)
    FontTextView modelYear;
    @BindView(R.id.chasisNumber)
    FontTextView chasisNumber;
    @BindView(R.id.engineNumber)
    FontTextView engineNumber;
    @BindView(R.id.exciseVerified)
    FontTextView exciseVerified;
    MotorbikeActivity mCurrentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motorbike);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ButterKnife.bind(this);
        mCurrentActivity = this;
        setToolbar();
        hideToolbarLogo();
        setToolbarTitle("Motorbike Information");
        setBackNavigation();
        setData();
    }

    private void setData() {
        if (getIntent() != null) {
            PersonalInfoData data = (PersonalInfoData) getIntent().getSerializableExtra(Constants.SETTINGS_DATA_EXTRAS);
            vehicleBrand.setText(data.getBrand());
            horsePower.setText(data.getHorse_power());
            chasisNumber.setText(data.getChassis_number());
            modelYear.setText(data.getModel_number());
            engineNumber.setText(data.getEngine_number());
            if (StringUtils.isNotBlank(data.getExcise_verified()) && data.getExcise_verified().equalsIgnoreCase("true")) {
                exciseVerified.setText("Yes");
            }
        }

    }
}

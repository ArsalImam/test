package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.PersonalInfoData;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LicenseActivity extends BaseActivity {

    @BindView(R.id.vehiclePlateNo)
    FontTextView vehiclePlateNo;
    @BindView(R.id.licenseNo)
    FontTextView licenseNo;
    @BindView(R.id.licenseCity)
    FontTextView licenseCity;
    @BindView(R.id.licenseExpDate)
    FontTextView licenseExpDate;


    private final static String REQUIRED_DATE_FORMAT = "MMM-dd-yyyy";
    private final static String CURRENT_DATE_FORMAT = "HH:mm:ss MM-dd-yyyy";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ButterKnife.bind(this);

        setToolbar();
        hideToolbarLogo();
        setToolbarTitle("License Information");
        setBackNavigation();

        setData();
    }

    private void setData() {
        if (getIntent() != null) {
            PersonalInfoData data = (PersonalInfoData) getIntent().getSerializableExtra(Constants.SETTINGS_DATA_EXTRAS);
            vehiclePlateNo.setText(data.getPlate_no());
            licenseNo.setText(data.getDriver_license_number());
            if(StringUtils.isNotBlank(data.getLicense_city())) {
                licenseCity.setText(data.getLicense_city());
            }
            if(StringUtils.isNotBlank(data.getLicense_expire())){
                /*licenseExpDate.setText(Utils.getFormattedDate(data.getLicense_expire().replace("T", " ").replace("Z",""), CURRENT_DATE_FORMAT_1,
                        REQUIRED_DATE_FORMAT));*/
                //TODO Update date format
//                licenseExpDate.setText(data.getLicense_expire());
                licenseExpDate.setText(Utils.getFormattedDate(data.getLicense_expire(), CURRENT_DATE_FORMAT,
                        REQUIRED_DATE_FORMAT));

            }
        }
    }

}

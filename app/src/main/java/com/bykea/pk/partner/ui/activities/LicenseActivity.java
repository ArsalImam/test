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
        setToolbarTitle("License","لائسنس" );
        setBackNavigation();

        setData();
    }

    private void setData() {
        if (getIntent() != null) {
            PersonalInfoData data = (PersonalInfoData) getIntent().getSerializableExtra(Constants.SETTINGS_DATA_EXTRAS);
            vehiclePlateNo.setText(data.getPlateNo());
            licenseNo.setText(data.getDriverLicenseNumber());
            if(StringUtils.isNotBlank(data.getLicenseCity())) {
                licenseCity.setText(data.getLicenseCity());
            }
            if(StringUtils.isNotBlank(data.getLicenseExpire())){
                /*licenseExpDate.setText(Utils.getFormattedDate(data.getLicenseExpire().replace("T", " ").replace("Z",""), CURRENT_DATE_FORMAT_1,
                        REQUIRED_DATE_FORMAT));*/
                //TODO Update date format
//                licenseExpDate.setText(data.getLicenseExpire());
                licenseExpDate.setText(Utils.getFormattedDate(data.getLicenseExpire(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                        REQUIRED_DATE_FORMAT));

            }
        }
    }

}

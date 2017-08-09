package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import android.widget.ImageView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.PersonalInfoData;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PersonalActivity extends BaseActivity {
    @Bind(R.id.pilotName)
    FontTextView pilotName;

    @Bind(R.id.pilotImage)
    ImageView pilotImage;

    @Bind(R.id.pilotAddress)
    FontTextView pilotAddress;

    @Bind(R.id.pilotCity)
    FontTextView pilotCity;
    @Bind(R.id.pilotCnic)
    FontTextView pilotCnic;
    @Bind(R.id.pilotMobile)
    FontTextView pilotMobile;
    @Bind(R.id.pilotMobile2)
    FontTextView pilotMobile2;
    @Bind(R.id.pilotMobile3)
    FontTextView pilotMobile3;
    @Bind(R.id.pilotEmail)
    FontTextView pilotEmail;
    @Bind(R.id.pilotRegisteredDate)
    FontTextView pilotRegisteredDate;
    @Bind(R.id.pilotVerifiedDate)
    FontTextView pilotVerifiedDate;

    PersonalActivity mCurrentActivity;

    private final static String REQUIRED_DATE_FORMAT = "MMM-dd-yyyy";
    private final static String CURRENT_DATE_FORMAT = "yyyy-mm-dd hh:mm:ss";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ButterKnife.bind(this);
        mCurrentActivity = this;
        setToolbar();
        hideToolbarLogo();
        setToolbarTitle("Personal Details");
        setBackNavigation();
        setData();
    }

    private void setData(){
        if(getIntent() != null){
            PersonalInfoData data = (PersonalInfoData) getIntent().getSerializableExtra(Constants.SETTINGS_DATA_EXTRAS);
            pilotName.setText(data.getFull_name());
            pilotAddress.setText(data.getAddress());
            pilotCity.setText(data.getCity());
            pilotCnic.setText(data.getCnic());
            pilotMobile.setText(Utils.phoneNumberToShow(data.getPhone()));
            pilotMobile2.setText(Utils.phoneNumberToShow(data.getMobile_1()));
            pilotMobile3.setText(Utils.phoneNumberToShow(data.getMobile_2()));

            if (StringUtils.isNotBlank(AppPreferences.getPilotData(mCurrentActivity).getPilotImage())) {
                Utils.loadImgPicasso(mCurrentActivity, pilotImage, R.drawable.profile_pic,
                        Utils.getImageLink(AppPreferences.getPilotData(mCurrentActivity).getPilotImage()));
            }
            if(StringUtils.isNotBlank(data.getEmail())){
                pilotEmail.setText(data.getEmail());
            }else {
                pilotEmail.setText("No Email Found (Optional)");
            }
            if (StringUtils.isNotBlank(data.getRegistration_date())) {
                pilotRegisteredDate.setText(Utils.getFormattedDate(data.getRegistration_date().replace("T", " ").replace("Z", ""), CURRENT_DATE_FORMAT,
                        REQUIRED_DATE_FORMAT));
            }

            if (StringUtils.isNotBlank(data.getRegistration_date())) {
                pilotVerifiedDate.setText(Utils.getFormattedDate(data.getRegistration_date().replace("T", " ").replace("Z", ""), CURRENT_DATE_FORMAT,
                        REQUIRED_DATE_FORMAT));
            }

        }
    }

}

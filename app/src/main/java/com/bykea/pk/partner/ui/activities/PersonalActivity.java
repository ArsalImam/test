package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.PersonalInfoData;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalActivity extends BaseActivity {
    @BindView(R.id.pilotName)
    FontTextView pilotName;

    @BindView(R.id.pilotImage)
    CircleImageView pilotImage;

    @BindView(R.id.pilotAddress)
    FontTextView pilotAddress;

    @BindView(R.id.pilotCity)
    FontTextView pilotCity;
    @BindView(R.id.pilotCnic)
    FontTextView pilotCnic;
    @BindView(R.id.pilotMobile)
    FontTextView pilotMobile;
    @BindView(R.id.pilotMobile2)
    FontTextView pilotMobile2;
    @BindView(R.id.pilotMobile3)
    FontTextView pilotMobile3;
    @BindView(R.id.pilotEmail)
    FontTextView pilotEmail;
    @BindView(R.id.pilotRegisteredDate)
    FontTextView pilotRegisteredDate;
    @BindView(R.id.pilotVerifiedDate)
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
        setToolbarTitle(getString(R.string.personal_detail_title),
                getString(R.string.personal_detail_title_ur));
        hideStatusLayout();
        setBackNavigation();
        setData();
    }

    /***
     * Populate data in UI
     */
    private void setData() {
        if (getIntent() != null) {
            PersonalInfoData data = getIntent()
                    .getParcelableExtra(Constants.SETTINGS_DATA_EXTRAS);
            pilotName.setText(data.getFullName());
            pilotAddress.setText(data.getAddress());
            pilotCity.setText(data.getCity());
            pilotCnic.setText(data.getCnic());
            pilotMobile.setText(Utils.phoneNumberToShow(data.getPhone()));
            pilotMobile2.setText(Utils.phoneNumberToShow(data.getPrimaryMobileNumber()));
            pilotMobile3.setText(Utils.phoneNumberToShow(data.getSecondaryMobileNumber()));

            if (StringUtils.isNotBlank(AppPreferences.getPilotData().getPilotImage())) {
                Utils.loadImgPicasso(mCurrentActivity, pilotImage, R.drawable.profile_pic,
                        Utils.getImageLink(AppPreferences.getPilotData().getPilotImage()));
            }
            if (StringUtils.isNotBlank(data.getEmail())) {
                pilotEmail.setText(data.getEmail());
            } else {
                pilotEmail.setText("No Email Found (Optional)");
            }
            if (StringUtils.isNotBlank(data.getRegistrationDate())) {
                pilotRegisteredDate.setText(Utils.getFormattedDate(data.getRegistrationDate().replace("T", " ").replace("Z", ""), CURRENT_DATE_FORMAT,
                        REQUIRED_DATE_FORMAT));
            }

            if (StringUtils.isNotBlank(data.getRegistrationDate())) {
                pilotVerifiedDate.setText(Utils.getFormattedDate(data.getRegistrationDate().replace("T", " ").replace("Z", ""), CURRENT_DATE_FORMAT,
                        REQUIRED_DATE_FORMAT));
            }

        }
    }

}

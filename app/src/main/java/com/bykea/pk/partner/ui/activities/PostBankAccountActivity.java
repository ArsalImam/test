package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import android.view.View;

import com.bykea.pk.partner.R;

import com.bykea.pk.partner.models.data.PersonalInfoData;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PostBankAccountActivity extends BaseActivity {

    @BindView(R.id.bankName)
    FontTextView bankName;
    @BindView(R.id.bankAccountNumber)
    FontTextView bankAccountNumber;
    @BindView(R.id.callbtn)
    FontTextView callbtn;

    private PostBankAccountActivity mCurrentActivity;
    private String financeNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_account);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ButterKnife.bind(this);
        mCurrentActivity = this;
        setToolbar();
        hideToolbarLogo();
        setToolbarTitle("Bank Account Details");
        setBackNavigation();
        setData();
    }

    private void setData() {
        if (getIntent() != null) {
            PersonalInfoData data = (PersonalInfoData) getIntent().getSerializableExtra(Constants.SETTINGS_DATA_EXTRAS);
            bankName.setText(data.getAccount_title());
            bankAccountNumber.setText(data.getAccount_number());
            financeNumber = data.getFinance();
        }
    }

    @OnClick({R.id.callbtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.callbtn:
                if(StringUtils.isNotBlank(financeNumber)) {
                    Utils.callingIntent(mCurrentActivity, financeNumber);
                }else {
                    Dialogs.INSTANCE.showToast(mCurrentActivity, "Number not available.");
                }
                break;
        }
    }
}

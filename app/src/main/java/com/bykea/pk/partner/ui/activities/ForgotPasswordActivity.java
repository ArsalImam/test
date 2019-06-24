package com.bykea.pk.partner.ui.activities;


import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.response.ForgotPasswordResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.NumericKeyBoardTransformationMethod;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontEditText;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPasswordActivity extends BaseActivity {

    @BindView(R.id.phoneNumberEt)
    FontEditText phoneNumberEt;


    @BindView(R.id.mainScrollView)
    ScrollView mainScrollView;
    private UserRepository repository;
    private ForgotPasswordActivity mCurrentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        mCurrentActivity = this;
        ButterKnife.bind(this);
        repository = new UserRepository();
        phoneNumberEt.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        if (StringUtils.isNotBlank(AppPreferences.getPhoneNumber())) {
            phoneNumberEt.setText(Utils.phoneNumberToShow(AppPreferences.getPhoneNumber()));
        }

        phoneNumberEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    Utils.hideSoftKeyboard(mCurrentActivity, phoneNumberEt);
                    return true;
                }
                return false;
            }
        });
        Utils.scrollToBottom(mainScrollView);
        setBackNavigation();
        setToolbarTitle("Forgot", "بھول گئے");
        hideToolbarLogo();
    }


    @OnClick({R.id.sendBtn, R.id.phoneNumberEt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sendBtn:
                if (Connectivity.isConnectedFast(mCurrentActivity)) {
                    if (Utils.isValidNumber(mCurrentActivity, phoneNumberEt)) {
                        Dialogs.INSTANCE.showLoader(mCurrentActivity);
                        repository.requestForgotPassword(mCurrentActivity, handler,
                                Utils.phoneNumberForServer(phoneNumberEt.getText().toString()));
                    }
                } else {
                    Dialogs.INSTANCE.showToast("Please check your internet connection.");
                }
                break;
            case R.id.phoneNumberEt:
                if (!phoneNumberEt.hasFocus()) {
                    phoneNumberEt.requestFocus();
                }
                break;
        }
    }

    private UserDataHandler handler = new UserDataHandler() {
        @Override
        public void onForgotPassword(ForgotPasswordResponse commonResponse) {
            if (mCurrentActivity != null) {
                Dialogs.INSTANCE.dismissDialog();
                if (commonResponse.isSuccess()) {
                    Dialogs.INSTANCE.showAlertDialogUrduWithTickCross(mCurrentActivity,
                            getString(R.string.forgot_password_success_msg),
                            0f,
                            null,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Dialogs.INSTANCE.dismissDialog();
                                    mCurrentActivity.finish();
                                }
                            });
                } else {
                    Utils.appToast(mCurrentActivity, commonResponse.getMessage());
                }
            }
        }

        @Override
        public void onError(int errorCode, String errorMessage) {
            Dialogs.INSTANCE.dismissDialog();
            Utils.appToast(mCurrentActivity, errorMessage);
        }
    };
}

package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.response.ChangePinResponse;
import com.bykea.pk.partner.repositories.IUserDataHandler;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontButton;
import com.bykea.pk.partner.widgets.FontEditText;

import org.apache.commons.lang3.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePinActivity extends BaseActivity {


    private ChangePinActivity mCurrentActivity;
    private UserRepository mUserRepository;

    @Bind(R.id.tvOldPin)
    FontEditText tvOldPin;
    @Bind(R.id.tvNewPin)
    FontEditText tvNewPin;
    @Bind(R.id.saveBtn)
    FontButton saveBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pin_code);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        mCurrentActivity = this;
        ButterKnife.bind(this);
        mUserRepository = new UserRepository();
        setToolbar();
        hideToolbarLogo();
        setToolbarTitle("Change Pin Code");
        setBackNavigation();

    }


    @OnClick({R.id.saveBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveBtn:
                if (Connectivity.isConnectedFast(mCurrentActivity)) {
                    if (validate()) {
                        Dialogs.INSTANCE.showLoader(mCurrentActivity);
                        mUserRepository.requestChangePin(mCurrentActivity, tvOldPin.getText().toString(),
                                tvNewPin.getText().toString(), mUserDataHandler);
                    }
                }
                break;
        }
    }

    private boolean validate() {
        if (StringUtils.isBlank(tvNewPin.getText().toString())) {
            tvNewPin.setError(getString(R.string.error_field_empty));
            tvNewPin.requestFocus();
            return false;
        }
        if (StringUtils.isBlank(tvOldPin.getText().toString())) {
            tvOldPin.setError(getString(R.string.error_field_empty));
            tvOldPin.requestFocus();
            return false;
        }
        if (tvNewPin.getText().toString().length() < 4) {
            tvNewPin.setError(getString(R.string.error_field_pin));
            tvNewPin.requestFocus();
            return false;
        }
        if (tvOldPin.getText().toString().length() < 4) {
            tvOldPin.setError(getString(R.string.error_field_pin));
            tvOldPin.requestFocus();
            return false;
        }
        return true;
    }

    private IUserDataHandler mUserDataHandler = new UserDataHandler() {

        @Override
        public void onChangePinResponse(final ChangePinResponse response) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Dialogs.INSTANCE.dismissDialog();
                    Dialogs.INSTANCE.showToast(mCurrentActivity, response.getMessage());
                    if (response.isSuccess()) {
                        mCurrentActivity.finish();
                    }
                }
            });
        }


        @Override
        public void onError(final int errorCode, final String errorMessage) {
            if (mCurrentActivity != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Dialogs.INSTANCE.dismissDialog();
                        if (errorCode == HTTPStatus.UNAUTHORIZED) {
                            Utils.logout(mCurrentActivity);
                        } else {
                            Toast.makeText(mCurrentActivity, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }
    };

}

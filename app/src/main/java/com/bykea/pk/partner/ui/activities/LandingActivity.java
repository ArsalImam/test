package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LandingActivity extends AppCompatActivity {
    public final String TAG = LandingActivity.class.getSimpleName();

    private LandingActivity mCurrentActivity;

    @BindView(R.id.iv_splash)
    AppCompatImageView imgSplash;

    @BindView(R.id.tv_login)
    FontTextView txtLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mCurrentActivity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        ButterKnife.bind(this);

        screenConfigurationSetup();
    }


    //region General Helper methods

    /***
     * Configure Screen for initial setup
     */
    private void screenConfigurationSetup() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        Utils.setFullScreen(mCurrentActivity);
    }
    //endregion

    //#region Life Cycle methods

    @Override
    protected void onResume() {
        super.onResume();
        screenConfigurationSetup();
    }

    //endregion

    //region View Click methods
    @OnClick(R.id.tv_login)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_login: {
                ActivityStackManager.getInstance().startLoginActivity(mCurrentActivity,
                        false);
                finish();
            }
        }
    }

    //endregion

}

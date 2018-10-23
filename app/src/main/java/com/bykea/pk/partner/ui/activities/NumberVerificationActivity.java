package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import android.view.View;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.ui.fragments.CodeVerificationFragment;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

public class NumberVerificationActivity extends BaseActivity {
    private NumberVerificationActivity mCurrentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentActivity = this;
        setContentView(R.layout.activity_number_verification);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.contentFrame, new CodeVerificationFragment())
                .commit();

    }

    /***
     * Update Toolbar Title and Back button
     * @param title Title for toolbar
     */
    public void setTitleCustomToolbar(String title) {
        ((FontTextView) findViewById(R.id.tvTitle)).setText(title);
        findViewById(R.id.ivBackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    /***
     * Update toolbar title only.
     * @param title Title for toolbar
     */
    public void updateTitleCustomToolbar(String title) {
        ((FontTextView) findViewById(R.id.tvTitle)).setText(title);
    }


    @Override
    public void onBackPressed() {
        if (Utils.isActivityRunning(mCurrentActivity, LoginActivity.class)) {
            super.onBackPressed();
        } else {
            ActivityStackManager.getInstance().startLoginActivityNoFlag(mCurrentActivity);
        }
        mCurrentActivity.finish();
    }
}

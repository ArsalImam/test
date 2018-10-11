package com.bykea.pk.partner.ui.activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.MultiDeliveryDirectionDetails;
import com.bykea.pk.partner.ui.fragments.MultiDeliveryCallFragment;
import com.bykea.pk.partner.ui.fragments.MultiDeliveryDirectionFragment;
import com.bykea.pk.partner.ui.fragments.MultiDeliveryRideCompleteFragment;
import com.bykea.pk.partner.ui.helpers.adapters.MultiDeliveryCompleteRideAdapter;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.FragmentUtils;
import com.bykea.pk.partner.utils.Keys;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapDetailsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_details);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        if (getIntent().getStringExtra(Keys.FRAGMENT_TYPE_NAME) != null) {
            String type = getIntent().getStringExtra(Keys.FRAGMENT_TYPE_NAME);
            updateFragment(type);
            setToolbarTitle(type);
            toolbarBackClick();
        }
    }

    /***
     * Set the toolbar title according to the type.
     *
     * @param type a type of fragment fetched from intent.
     */
    private void setToolbarTitle(String type) {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setTitle("");
        mToolbar.setSubtitle("");
        ((TextView)mToolbar.findViewById(R.id.title)).setText(type);
    }

    /***
     * Toolbar back button click listener.
     */
    private void toolbarBackClick() {
        mToolbar.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /***
     * Update the fragment according to the type.
     *
     * @param type a type of fragment fetched from intent.
     */
    public void updateFragment(String type) {
        switch (type) {
            case Constants.MapDetailsFragmentTypes.TYPE_CALL: {
                FragmentUtils.pushFragment(this, R.id.container,
                        MultiDeliveryCallFragment.newInstance(), null, null,
                        false, false);
                break;
            }

            case Constants.MapDetailsFragmentTypes.TYPE_TAFSEEL: {
                FragmentUtils.pushFragment(this, R.id.container,
                        MultiDeliveryDirectionFragment.newInstance(), null, null,
                        false, false);
                break;
            }

            case Constants.MapDetailsFragmentTypes.TYPE_MUKAMAL: {
                FragmentUtils.pushFragment(this, R.id.container,
                        MultiDeliveryRideCompleteFragment.newInstance(), null, null,
                        false, false);
                break;
            }
        }
    }
}

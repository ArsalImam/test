package com.bykea.pk.partner.ui.activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.ui.fragments.MultiDeliveryCallFragment;
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
            updateFragment(getIntent().getStringExtra(Keys.FRAGMENT_TYPE_NAME));
        }

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setTitle("");
        mToolbar.setSubtitle("");
        mToolbar.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public void updateFragment(String type) {
        switch (type) {
            case Constants.MapDetailsFragmentTypes.TYPE_CALL: {
                FragmentUtils.pushFragment(this, R.id.container,
                        MultiDeliveryCallFragment.newInstance(), null, null,
                        false, false);
                break;
            }
        }
    }
}

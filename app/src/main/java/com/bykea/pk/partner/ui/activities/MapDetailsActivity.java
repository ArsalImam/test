package com.bykea.pk.partner.ui.activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.ui.fragments.MultiDeliveryCallFragment;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.FragmentUtils;
import com.bykea.pk.partner.utils.Keys;

public class MapDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_details);

        if (getIntent().getStringExtra(Keys.FRAGMENT_TYPE_NAME) != null) {
            updateFragment(getIntent().getStringExtra(Keys.FRAGMENT_TYPE_NAME));
        }

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

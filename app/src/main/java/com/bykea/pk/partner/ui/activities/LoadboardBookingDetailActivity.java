package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.response.LoadboardBookingDetailResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.fragments.LoadboardBookingDetailFragment;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Loadboard booking detail screen ACTIVITY - opening from homeScreen's loadboard listing items
 * this activity will hold booking detail fragment
 */
public class LoadboardBookingDetailActivity extends BaseActivity {

    private LoadboardBookingDetailActivity mCurrentActivity;
    private UserRepository mRepository;

    public static String BOOKING_ID = "BOOKING_ID";

    @BindView(R.id.backBtn)
    AppCompatImageView backBtn;
    @BindView(R.id.bookingTypeIV)
    AppCompatImageView bookingTypeIV;
    @BindView(R.id.bookingNoTV)
    FontTextView bookingNoTV;

    private String bookingId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadboard_booking_detail);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        mCurrentActivity = this;
        bookingId = getIntent().getStringExtra(BOOKING_ID);
        ButterKnife.bind(this);

        initViews();
        initListeners();
    }

    /**
     * initialize views and objects related to this screen
     */
    private void initViews(){
        mRepository = new UserRepository();
        Dialogs.INSTANCE.showLoader(mCurrentActivity);
        mRepository.loadboardBookingDetail(mCurrentActivity, bookingId, new UserDataHandler(){
            @Override
            public void onLoadboardBookingDetailResponse(LoadboardBookingDetailResponse response) {
                Dialogs.INSTANCE.dismissDialog();
                bookingNoTV.setText(response.getData().getOrderNo());
//                bookingTypeIV.setImageResource();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.bookingDetailContainerFL, LoadboardBookingDetailFragment.newInstance(response.getData()))
                        .commitAllowingStateLoss();
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                Dialogs.INSTANCE.dismissDialog();
                Utils.appToast(mCurrentActivity, errorMessage);
            }
        });
    }

    /**
     * initialize click listeners for this screen's button or widgets
     */
    private void initListeners(){
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

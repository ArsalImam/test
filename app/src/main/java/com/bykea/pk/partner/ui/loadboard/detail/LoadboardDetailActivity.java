package com.bykea.pk.partner.ui.loadboard.detail;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageView;

import android.view.View;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.response.LoadboardBookingDetailResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.BaseActivity;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Loadboard booking detail screen ACTIVITY - opening from homeScreen's loadboard listing items
 * this activity will hold booking detail fragment
 */
public class LoadboardDetailActivity extends BaseActivity implements View.OnClickListener {

    private LoadboardDetailActivity mCurrentActivity;
    private UserRepository mRepository;

    public static String BOOKING_ID = "BOOKING_ID";

    @BindView(R.id.backBtn)
    AppCompatImageView backBtn;
//    @BindView(R.id.bookingTypeIV)
//    AppCompatImageView bookingTypeIV;
//    @BindView(R.id.bookingNoTV)
//    FontTextView bookingNoTV;

    @BindView(R.id.imgViewDelivery)
    AppCompatImageView imgViewDelivery;
    @BindView(R.id.tVEstimatedFare)
    FontTextView tVEstimatedFare;
    @BindView(R.id.tVCODAmount)
    FontTextView tVCODAmount;

    private String bookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadboard_detail);
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
    private void initViews() {
        mRepository = new UserRepository();
        Dialogs.INSTANCE.showLoader(mCurrentActivity);
        mRepository.loadboardBookingDetail(mCurrentActivity, bookingId, new UserDataHandler() {
            @Override
            public void onLoadboardBookingDetailResponse(LoadboardBookingDetailResponse response) {
                Dialogs.INSTANCE.dismissDialog();
                tVEstimatedFare.setText("Rs." + response.getData().getAmount() + "");
                tVCODAmount.setText("Rs." + response.getData().getCartAmount() + "");

                //bookingNoTV.setText(response.getData().getOrderNo());
//                bookingTypeIV.setImageResource();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.bookingDetailContainerFL, LoadboardDetailFragment.newInstance(response.getData()))
                        .commitAllowingStateLoss();
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                Dialogs.INSTANCE.dismissDialog();
                if (errorCode == HTTPStatus.UNAUTHORIZED) {
                    Utils.onUnauthorized(mCurrentActivity);
                } else {
                    Dialogs.INSTANCE.showToast(mCurrentActivity, errorMessage);
                }
            }
        });
    }

    /**
     * initialize click listeners for this screen's button or widgets
     */
    private void initListeners() {
        backBtn.setOnClickListener(this);
        imgViewDelivery.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                finish();
                break;

            case R.id.imgViewDelivery:
                Utils.appToast(getApplicationContext(), "imgViewDelivery");
                break;
        }
    }
}

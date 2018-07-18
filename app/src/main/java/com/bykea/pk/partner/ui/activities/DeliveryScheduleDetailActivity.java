package com.bykea.pk.partner.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.response.LoadBoardResponse;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeliveryScheduleDetailActivity extends BaseActivity {

    @BindView(R.id.nameTv)
    FontTextView nameTv;

    @BindView(R.id.distanceTv)
    FontTextView distanceTv;

    @BindView(R.id.addressTv)
    FontTextView addressTv;

    @BindView(R.id.durationTv)
    FontTextView durationTv;

    @BindView(R.id.closeBtn)
    ImageView closeBtn;

    private DeliveryScheduleDetailActivity mCurrentActivity;

    private String phone;
    private String destination;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_schedule_detail);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        mCurrentActivity = this;
        ButterKnife.bind(mCurrentActivity);


        getCustomerData();
    }

    private void getCustomerData() {
        try {
            int pos = getIntent().getIntExtra(Constants.Extras.POSITION_DELIVERY_SCHEDULE, 0);
            LoadBoardResponse response = (LoadBoardResponse) AppPreferences.getObjectFromSharedPref(LoadBoardResponse.class);
            if (response.getLoadBoardBody() != null) {
                setTitleCustomToolbarWithUrdu(response.getLoadBoardBody().get(pos).
                        getCustomerResponses().getFullName(), "");
                nameTv.setText(response.getLoadBoardBody().get(pos).getCustomerResponses().getFullName());
                phone = response.getLoadBoardBody().get(pos).getCustomerResponses().getMobileNumber();

                double distance = Double.valueOf(Double.valueOf(response.getLoadBoardBody().get(pos).getDistance())/1000);

                distanceTv.setText(String.format("%.1f", distance) + " km");

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                Date date = (Date) simpleDateFormat.parse(response.getLoadBoardBody().get(pos).getDateTime());

                String duration = DateUtils.getRelativeTimeSpanString(date.getTime(), System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS).toString();

                durationTv.setText(duration);

                String address = Utils.getLocationAddress(response.getLoadBoardBody().get(pos).getLatlng().get(0),
                        response.getLoadBoardBody().get(pos).getLatlng().get(1), mCurrentActivity);

                destination = response.getLoadBoardBody().get(pos).getLatlng().get(0) + "," +
                        response.getLoadBoardBody().get(pos).getLatlng().get(1);
                addressTv.setText(Utils.formatAddress(address));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.callbtn, R.id.closeBtn, R.id.directionBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.callbtn: {
                Utils.phoneCall(mCurrentActivity, phone);
                break;
            }

            case R.id.directionBtn: {
                navigateToGoogleMap();
                break;
            }

            case R.id.closeBtn: {
                onBackPressed();
                break;
            }
        }
    }

    private void navigateToGoogleMap() {
        try{
            String currentLocation = Utils.getCurrentLocation();
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=" + currentLocation + "&daddr="+destination));
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
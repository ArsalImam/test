package com.bykea.pk.partner.ui.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.SignUpCity;
import com.bykea.pk.partner.models.data.SignUpUserData;
import com.bykea.pk.partner.models.response.BiometricApiResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.adapters.DocumentsGridAdapter;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class JsBankFingerSelectionActivity extends BaseActivity implements View.OnTouchListener {
    private String DRIVER_ID;
    private String CNIC;
    private String BASE_IMG_URL;
    private SignUpCity mSelectedCity;
    private String VIDEO_ID;
    private SignUpUserData signUpData;//todo save insta
    private JsBankFingerSelectionActivity mCurrentActivity;
    private String financeNumber;
    private Resources resources;

    @BindView(R.id.ivFingerSelection)
    ImageView ivFingerSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_js_bank_finger_selection);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ButterKnife.bind(this);
        mCurrentActivity = this;
        setTitleCustomToolbarWithUrduHideBackBtn("Bank Account", "بینک اکاؤنٹ");
        resources = getResources();
        ImageView iv = (ImageView) findViewById(R.id.ivFingerSelection);
        if (iv != null) {
            iv.setOnTouchListener(this);
        }
        if (getIntent() != null && getIntent().getExtras() != null) {
            mSelectedCity = getIntent().getExtras().getParcelable(Constants.Extras.SELECTED_ITEM);
            signUpData = getIntent().getExtras().getParcelable(Constants.Extras.SIGN_UP_DATA);
            DRIVER_ID = getIntent().getExtras().getString(Constants.Extras.DRIVER_ID);
            CNIC = getIntent().getExtras().getString(Constants.Extras.CNIC);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent ev) {
        final int action = ev.getAction();
        final int evX = (int) ev.getX();
        final int evY = (int) ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                if (!checkClickTime()) {
                    // On the UP, we do the click action.
                    // The hidden image (image_areas) has three different hotspots on it.
                    // The colors are red, blue, and yellow.
                    // Use image_areas to determine which region the user touched.
                    int touchColor = getHotspotColor(R.id.image_areas, evX, evY);

                    // Compare the touchColor to the expected values. Switch to a different image, depending on what color was touched.
                    // Note that we use a Color Tool object to test whether the observed color is close enough to the real color to
                    // count as a match. We do this because colors on the screen do not match the map exactly because of scaling and
                    // varying pixel density.
//                ColorTool ct = new ColorTool();
                    int tolerance = 25;
//                nextImage = R.drawable.p2_ship_default;
                    if (closeMatch(resources.getColor(R.color.colorOne), touchColor, tolerance)) {
                        callScanActivity(1);
                    } else if (closeMatch(resources.getColor(R.color.colorTwo), touchColor, tolerance)) {
                        callScanActivity(2);
                    } else if (closeMatch(resources.getColor(R.color.colorThree), touchColor, tolerance)) {
                        callScanActivity(3);
                    } else if (closeMatch(resources.getColor(R.color.colorFour), touchColor, tolerance)) {
                        callScanActivity(4);
                    } else if (closeMatch(resources.getColor(R.color.colorFive), touchColor, tolerance)) {
                        callScanActivity(5);
                    } else if (closeMatch(resources.getColor(R.color.colorSix), touchColor, tolerance)) {
                        callScanActivity(6);
                    } else if (closeMatch(resources.getColor(R.color.colorSeven), touchColor, tolerance)) {
                        callScanActivity(7);
                    } else if (closeMatch(resources.getColor(R.color.colorEight), touchColor, tolerance)) {
                        callScanActivity(8);
                    } else if (closeMatch(resources.getColor(R.color.colorNine), touchColor, tolerance)) {
                        callScanActivity(9);
                    } else if (closeMatch(resources.getColor(R.color.colorTen), touchColor, tolerance)) {
                        callScanActivity(10);
                    }
                }
                break;
            default:
        }
        return true;
    }


    public boolean closeMatch(int color1, int color2, int tolerance) {
        if ((int) Math.abs(Color.red(color1) - Color.red(color2)) > tolerance) return false;
        if ((int) Math.abs(Color.green(color1) - Color.green(color2)) > tolerance) return false;
        if ((int) Math.abs(Color.blue(color1) - Color.blue(color2)) > tolerance) return false;
        return true;
    } // end match

    private void log(String s) {
        Log.e("Touch", s);
    }

    private synchronized int getHotspotColor(int hotspotId, int x, int y) {
        ImageView img = (ImageView) findViewById(hotspotId);
        if (img == null) {
            Log.d("MainActivity", "Hot spot image not found");
            return 0;
        } else {
            img.setDrawingCacheEnabled(true);
            Bitmap hotspots = Bitmap.createBitmap(img.getDrawingCache());
            if (hotspots == null) {
                Log.d("MainActivity", "Hot spot bitmap was not created");
                return 0;
            } else {
                img.setDrawingCacheEnabled(false);
                return hotspots.getPixel(x, y);
            }
        }
    }

    private void callScanActivity(int index) {
        /*Intent intent = new Intent(mCurrentActivity, ScannFingerPrintsActivity.class);
        intent.putExtra(Constants.Extras.CNIC, CNIC);
        intent.putExtra(Constants.Extras.SELECTED_INDEX, index);
        startActivityForResult(intent, Constants.RequestCode.SCAN_FINGER_PRINTS);*/
    }

    private boolean isVerified;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RequestCode.SCAN_FINGER_PRINTS && data != null) {
            if (resultCode == RESULT_OK) {
                Dialogs.INSTANCE.showLoader(mCurrentActivity);
                isVerified = data.getBooleanExtra(Constants.Extras.IS_FINGER_PRINTS_SUCCESS, false);
                new UserRepository().postBiometricVerification(mCurrentActivity, DRIVER_ID, isVerified, mCallback);
            }
        }
    }

    private void showVerificationDialog() {
        Dialogs.INSTANCE.showVerificationDialog(mCurrentActivity, isVerified, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentActivity.finish();
                ActivityStackManager.getInstance().startLoginActivity(mCurrentActivity);
                if (DocumentsGridAdapter.getmInstanceForNullCheck() != null) {
                    DocumentsGridAdapter.getInstance().resetTheInstance();
                }
            }
        });
    }


    private UserDataHandler mCallback = new UserDataHandler() {

        @Override
        public void onBiometricApiResponse(BiometricApiResponse response) {
            if (mCurrentActivity != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.INSTANCE.dismissDialog();
                        showVerificationDialog();
                    }
                });
            }
        }

        @Override
        public void onError(int errorCode, String errorMessage) {
            if (mCurrentActivity != null) {
                Dialogs.INSTANCE.dismissDialog();
                Utils.appToast(mCurrentActivity, errorMessage);
            }
        }
    };


}
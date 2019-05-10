package com.bykea.pk.partner.ui.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bykea.pk.partner.BuildConfig;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.DocumentsData;
import com.bykea.pk.partner.models.data.Images;
import com.bykea.pk.partner.models.data.SignUpAddNumberResponse;
import com.bykea.pk.partner.models.data.SignUpCity;
import com.bykea.pk.partner.models.data.SignUpOptionalDataResponse;
import com.bykea.pk.partner.models.data.SignUpUserData;
import com.bykea.pk.partner.models.data.SignupUplodaImgResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.IntegerCallBack;
import com.bykea.pk.partner.ui.helpers.StringCallBack;
import com.bykea.pk.partner.ui.helpers.adapters.DocumentsGridAdapter;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Dialogs_new;
import com.bykea.pk.partner.utils.NumericKeyBoardTransformationMethod;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontEditText;
import com.google.android.youtube.player.YouTubePlayerFragment;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import okhttp3.internal.Util;

public class DocumentsRegistrationActivity extends BaseActivity {
    @BindView(R.id.phoneNumberEt)
    FontEditText phoneNumberEt;
    @BindView(R.id.ytIcon)
    ImageView ytIcon;

    @BindView(R.id.rvDocuments)
    RecyclerView mRecyclerView;

    @BindView(R.id.ivThumbnail)
    ImageView ivThumbnail;
    @BindView(R.id.etEmail)
    FontEditText etEmail;


    @BindView(R.id.mainScrollView)
    NestedScrollView mainScrollView;

    private boolean isImgCompressing;
    //    private String imagPath;
    private String DRIVER_ID, CNIC, BASE_IMG_URL, REGISTERED_PHONE_NUMBER;
    private boolean isBiometricVerRequired;
    private DocumentsRegistrationActivity mCurrentActivity;
    private SignUpCity mSelectedCity;
    private String VIDEO_ID;
    private YouTubePlayerFragment playerFragment;
    private UserRepository mUserRepository;
    private SignUpUserData signUpData;//todo save insta

    private DocumentsGridAdapter mAdapter;
//    private int selectedDocument;


    public DocumentsRegistrationActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (BuildConfig.DEBUG) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
//            Utils.redLog("DocumentsRegistrationActivity", "onCreate");
//        }
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_documents_registeration);
        ButterKnife.bind(this);
        mUserRepository = new UserRepository();
        mCurrentActivity = this;
        mUserRepository.setCallback(mCallback);
        mCurrentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initView();
    }


    public void initView() {
        phoneNumberEt.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        mCurrentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (getIntent() != null && getIntent().getExtras() != null) {
            mSelectedCity = getIntent().getExtras().getParcelable(Constants.Extras.SELECTED_ITEM);
            signUpData = getIntent().getExtras().getParcelable(Constants.Extras.SIGN_UP_DATA);
            DRIVER_ID = getIntent().getExtras().getString(Constants.Extras.DRIVER_ID);
            BASE_IMG_URL = getIntent().getExtras().getString(Constants.Extras.SIGN_UP_IMG_BASE);
            CNIC = getIntent().getExtras().getString(Constants.Extras.CNIC);
            REGISTERED_PHONE_NUMBER = getIntent().getExtras().getString(Constants.Extras.PHONE_NUMBER);
            isBiometricVerRequired = getIntent().getExtras().getBoolean(Constants.Extras.IS_BIOMETRIC_VERIFIED);
            if (signUpData != null) {
                if (StringUtils.isNotBlank(signUpData.getRef_number())) {
                    phoneNumberEt.setText(signUpData.getRef_number());
                }
                if (StringUtils.isNotBlank(signUpData.getEmail())) {
                    etEmail.setText(signUpData.getEmail());
                }
            }
            if (mSelectedCity != null) {
                VIDEO_ID = mSelectedCity.getVideo();
                initYouTube();
            }
        }
        checkPermissions();
        initAdapter(getDocumentsData());

//        etEmail.setOnFocusChangeListener(mFocusChangedListener);
//        phoneNumberEt.setOnFocusChangeListener(mFocusChangedListener);
        /*mainScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
// We take the last son in the scrollview
                View view = (View) mainScrollView.getChildAt(mainScrollView.getChildCount() - 1);
                int diff = (view.getBottom() - (mainScrollView.getHeight() + mainScrollView.getScrollY()));

                // if diff is zero, then the bottom has been reached
                if (diff <= 10) {
                    isScrollViewAtBottom = true;
                } else {
                    isScrollViewAtBottom = false;
                }
            }
        });*/

    }

    private boolean isScrollViewAtBottom;

    private View.OnFocusChangeListener mFocusChangedListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (mCurrentActivity != null) {
                if (hasFocus) {
                    mCurrentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    if (!isScrollViewAtBottom) {
                        Utils.scrollToBottom(mainScrollView);
                    }
                }
            }
        }
    };


    private void initAdapter(ArrayList<DocumentsData> list) {
        if (DocumentsGridAdapter.getmInstanceForNullCheck() == null) {
            mAdapter = DocumentsGridAdapter.getInstance();
            mAdapter.init(list, new DocumentsGridAdapter.OnItemClickListener() {
                @Override
                public void onItemClickListener(int position, int prevPosition) {
                    if (!isImgCompressing) {
                        mAdapter.setSelectedItemIndex(position);
//                        selectedDocument = position;
                        startPickImageDialog();
                    }
                }
            }, new IntegerCallBack() {
                @Override
                public void onCallBack(int position) {

                }
            });
        } else {
            mAdapter = DocumentsGridAdapter.getInstance();
        }

        initRv();

    }


    private void initRv() {
        GridLayoutManager layoutManager = new GridLayoutManager(mCurrentActivity, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
               /* int span;
                span = mDocumnetList.size() % 2;*/
                if (position <= 0) {
                    return 2;
                }/* else if (position <= ((mDocumnetList.size() - 1) - span)) {
                    return 1;
                } */ else {
                    return 1;
                }
            }
        });
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setAdapter(mAdapter);
//        Utils.redLog("DocumentsRegistration", "initRv -> adapter = " + mAdapter.toString());
//        Utils.redLog("DocumentsRegistration", "initRv -> mCurrentActivity = " + mCurrentActivity.toString());
    }

    private static final String TYPE_SELFIE = "selfie";
    private static final String TYPE_NIC_FR = "cnic_fr";
    private static final String TYPE_NIC_BK = "cnic_bk";
    private static final String TYPE_BIKE_PAPER = "bike_paper";
    private static final String TYPE_LICENSE = "license";

    private ArrayList<DocumentsData> getDocumentsData() {
        ArrayList<DocumentsData> documentsData = new ArrayList<>();
        String selfie = isDocAlreadyUploaded(TYPE_SELFIE);
        String nicFront = isDocAlreadyUploaded(TYPE_NIC_FR);
        String nicBack = isDocAlreadyUploaded(TYPE_NIC_BK);
        String bike_paper = isDocAlreadyUploaded(TYPE_BIKE_PAPER);
        String license = isDocAlreadyUploaded(TYPE_LICENSE);

        documentsData.add(new DocumentsData("آپ کی سیلفی", "Your Photo", selfie, TYPE_SELFIE, StringUtils.isNotBlank(selfie)));
        documentsData.add(new DocumentsData("شناختی کارڈ", "NIC (Front)", nicFront, TYPE_NIC_FR, StringUtils.isNotBlank(nicFront)));
        documentsData.add(new DocumentsData("شناختی کارڈ", "NIC (Back)", nicBack, TYPE_NIC_BK, StringUtils.isNotBlank(nicBack)));
        documentsData.add(new DocumentsData("بائیک کےکاغذات", "Bike Papers", bike_paper, TYPE_BIKE_PAPER, StringUtils.isNotBlank(bike_paper)));
        documentsData.add(new DocumentsData("لائسنس", "License", license, TYPE_LICENSE, StringUtils.isNotBlank(license)));
        return documentsData;
    }

    private String isDocAlreadyUploaded(String type) {
        String link = StringUtils.EMPTY;
        if (signUpData != null) {
            for (Images img : signUpData.getImages()) {
                if (type.equalsIgnoreCase(img.getType())) {
                    link = getCompleteImgUrl(img.getType(), img.getLink());
                    break;
                }
            }
        }
        return link;
    }

    private String getCompleteImgUrl(String type, String id) {
        return BASE_IMG_URL + type + "/" + id;
    }

    private final String PERMISSION = "android.permission.CAMERA";
    private final String PERMISSION_READ_ES = "android.permission.READ_EXTERNAL_STORAGE";


    private void initYouTube() {
        playerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.player_fragment);
        Utils.initPlayerFragment(playerFragment, ytIcon, ivThumbnail, VIDEO_ID);
    }

    private void logAnalyticsEvent() {
        try {
            JSONObject data = new JSONObject();
            data.put("DriverId", DRIVER_ID);
            data.put("Email", etEmail.getText().toString());
            data.put("Reference", phoneNumberEt.getText().toString());
            Utils.logFacebookEvent(mCurrentActivity, Constants.AnalyticsEvents.ON_SIGN_UP_COMPLETE, data);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @OnClick({R.id.ytIcon, R.id.nextBtn, R.id.rlRef, R.id.rlEmail})
    public void onClick(View view) {
        switch (view.getId()) {
            /*case R.id.rlEmail:
            case R.id.rlRef:
                if (!isScrollViewAtBottom) {
                    Utils.scrollToBottom(mainScrollView);
                }
                break;*/
            case R.id.ytIcon:
                Utils.playVideo(mCurrentActivity, VIDEO_ID, ivThumbnail, ytIcon, playerFragment);
                break;
            case R.id.nextBtn:
                Utils.redLog("DocumentsRegistrationActivity", "onClick - > nextBtn");
                if (isValidData() && AppPreferences.isSignUpApiCalled()) {
                    Dialogs.INSTANCE.showLoader(mCurrentActivity);
                    AppPreferences.setSignUpApiCalled(false);
                    mUserRepository.postOptionalSignupData(mCurrentActivity, DRIVER_ID,
                            etEmail.getText().toString(), phoneNumberEt.getText().toString(), mCallback);
                }
                break;
        }
    }

    private boolean isValidData() {
        boolean valid = true;
        for (DocumentsData data : mAdapter.getItemsList()) {
            if (!data.isUploaded()) {
                valid = false;
                break;
            }
        }
        if (!valid) {
            Utils.appToast(mCurrentActivity, "Please upload all documents");
        } else if (StringUtils.isNotBlank(etEmail.getText().toString())
                && !Utils.isValidEmail(etEmail.getText().toString())) {
            etEmail.setError("Enter valid Email");
            etEmail.requestFocus();
            valid = false;
        } else if (StringUtils.isNotBlank(phoneNumberEt.getText().toString())) {
            valid = Utils.isValidNumber(mCurrentActivity, phoneNumberEt);
        } else if (!Utils.isConnected(mCurrentActivity, true)) {
            valid = false;
        }
        return valid;
    }

    private void showSuccessDialog() {
        Dialogs.INSTANCE.showSignUpSuccessDialog(mCurrentActivity, REGISTERED_PHONE_NUMBER, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Dialogs.INSTANCE.dismissDialog();

                    ActivityStackManager.getInstance().startLoginActivity(mCurrentActivity);
                    if (DocumentsGridAdapter.getmInstanceForNullCheck() != null) {
                        DocumentsGridAdapter.getInstance().resetTheInstance();
                    }
                    mCurrentActivity.finish();
            }
        });
    }

    private File createImageFile() throws IOException {
        String imageFileName = "BykeaDocument" + mAdapter.getItem(mAdapter.getSelectedItemIndex()).getType();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                Constants.UPLOAD_IMG_EXT,         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mAdapter.setImgPath(image.getAbsolutePath());
        return image;
    }

    private void startPickImageDialog() {
        final Dialogs_new d = new Dialogs_new(mCurrentActivity);
        d.showActionSheet(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialogs.INSTANCE.dismissDialog();
                try {
                    mAdapter.getItem(mAdapter.getSelectedItemIndex()).setImageUri(Utils.startCameraByIntent(mCurrentActivity, createImageFile()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                d.onBackPressed();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialogs.INSTANCE.dismissDialog();
                Utils.startGalleryByIntent(mCurrentActivity);
                d.onBackPressed();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.onBackPressed();

            }
        }, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                d.onBackPressed();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && mCurrentActivity != null) {

            if (requestCode == Constants.REQUEST_GALLERY && data != null && data.getData() != null) {
                InputStream istream = null;
                try {
                    istream = mCurrentActivity.getContentResolver().openInputStream(data.getData());
                    mAdapter.getItem(mAdapter.getSelectedItemIndex()).setImageUri(data.getData());
                    if (istream != null) {

                        File file = new File(getCacheDir(), "cacheFileAppeal.srl");
                        try {
                            OutputStream output = new FileOutputStream(file);
                            try {
                                byte[] buffer = new byte[4 * 1024]; // or other buffer size
                                int read;

                                while ((read = istream.read(buffer)) != -1) {
                                    output.write(buffer, 0, read);
                                }

                                output.flush();
                            } finally {
                                output.close();
                            }
                        } finally {
                            istream.close();
                        }

                        compressImage(file);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.appToast(mCurrentActivity, getString(R.string.error_try_again));
                }
            } else if (requestCode == Constants.REQUEST_CAMERA) {
                try {
                    File imgFile = new File(mAdapter.getImgPath());
                    if (imgFile.exists()) {
                        compressImage(imgFile);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.appToast(mCurrentActivity, getString(R.string.error_try_again));
                }
            }
        } else {
            Utils.appToast(mCurrentActivity, getString(R.string.error_picture_not_selected));
        }
    }


   /* private void compressImage(Bitmap bitmap) {
        isImgCompressing = true;
        mDocumnetList.get( mAdapter.getSelectedItemIndex()).setUploading(true);
        mDocumnetList.get( mAdapter.getSelectedItemIndex()).setUploaded(false);
        mAdapter.notifyItemChanged( mAdapter.getSelectedItemIndex());

        String imgname = "BykeaDocument" + ".webp";
        String path1 = mCurrentActivity.getFilesDir().toString();
        File file = new File(path1, imgname);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.WEBP, 70, fos);
            }
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bitmap != null) {
            bitmap.recycle();
        }
        logFileSize(file);
        imagPath = file.getAbsolutePath();
        startUploadImageTask(file);
    }*/

    private void compressImage(File actualImage) {
        isImgCompressing = true;
        mAdapter.getItem(mAdapter.getSelectedItemIndex()).setUploading(true);
        mAdapter.getItem(mAdapter.getSelectedItemIndex()).setUploaded(false);
        mAdapter.notifyItemChanged(mAdapter.getSelectedItemIndex());

//        logFileSize(actualImage);
        String imgname = "BykeaDocument" + Constants.UPLOAD_IMG_EXT;
        String path1 = mCurrentActivity.getFilesDir().toString();
        File file = new File(path1, imgname);
        try {
            file = new Compressor(this)
                    .setMaxWidth(640)
                    .setMaxHeight(480)
                    .setQuality(70)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .compressToFile(actualImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        logFileSize(file);
        mAdapter.setImgPath(file.getAbsolutePath());
        startUploadImageTask(file);
    }

    private void logFileSize(File file) {
        String value = null;
        long Filesize = Utils.getFolderSize(file) / 1024;//call function and convert bytes into Kb
        if (Filesize >= 1024)
            value = Filesize / 1024 + " Mb";
        else
            value = Filesize + " Kb";

        Utils.redLog("ImgSize", value);
    }

    private void startUploadImageTask(File file) {
//        Utils.redLog("DocumentsRegistration", "startUploadImageTask -> adapter = " + mAdapter.toString());
//        Utils.redLog("DocumentsRegistration", "startUploadImageTask -> mCurrentActivity = " + mCurrentActivity.toString());
        mUserRepository.uploadDocumentImage(mCurrentActivity, DRIVER_ID,
                mAdapter.getItem(mAdapter.getSelectedItemIndex()).getType(), file, mCallback);
        isImgCompressing = false;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
//        if (StringUtils.isNotBlank(imagPath)) {
//            outState.putString("photopath", imagPath);
//        }
        outState.putInt("selectedDocument", mAdapter.getSelectedItemIndex());
        outState.putParcelableArrayList("mDocumnetList", mAdapter.getItemsList());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("photopath")) {
//                imagPath = savedInstanceState.getString("photopath");
//                selectedDocument = savedInstanceState.getInt("selectedDocument");
//                ArrayList<DocumentsData> list = savedInstanceState.getParcelableArrayList("mDocumnetList");
//                mAdapter.setItemsList(list);
//                initAdapter(list);
//                if (StringUtils.isNotBlank(imagPath)) {
//                    Picasso.with(mCurrentActivity).load(imagPath).placeholder(R.drawable.place_holder)
//                            .into(profileImageView);
//                }

            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    private int getSelectedDocument(String type) {
        int index = mAdapter.getSelectedItemIndex();
        for (int i = 0; i < mAdapter.getItemsList().size(); i++) {
            if (type.equalsIgnoreCase(mAdapter.getItem(i).getType())) {
                index = i;
                break;
            }
        }
        return index;
    }

    private UserDataHandler mCallback = new UserDataHandler() {

        @Override
        public void onSignUpOptionalResponse(SignUpOptionalDataResponse response) {
            if (mCurrentActivity != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.INSTANCE.dismissDialog();
                        logAnalyticsEvent();
                        showSuccessDialog();
                    }
                });
            }
        }

        @Override
        public void onSignUpImageResponse(final SignupUplodaImgResponse response) {
            if (mCurrentActivity != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.INSTANCE.dismissDialog();
                        int currentSelectedDocument = getSelectedDocument(response.getType());
                        mAdapter.getItem(currentSelectedDocument).setUploaded(true);
                        mAdapter.getItem(currentSelectedDocument).setUploading(false);
                        mAdapter.getItem(currentSelectedDocument).setImage(getCompleteImgUrl(response.getType(), response.getLink()));
                        mAdapter.notifyItemChanged(currentSelectedDocument);
//                        Utils.redLog("DocumentsRegistration", "onSignUpImageResponse -> adapter = " + mAdapter.toString());
//                        Utils.redLog("DocumentsRegistration", "onSignUpImageResponse -> mCurrentActivity = " + mCurrentActivity.toString());
                    }
                });
            }
        }

        @Override
        public void onError(int errorCode, String errorMessage) {
            if (mCurrentActivity != null) {
                AppPreferences.setSignUpApiCalled(true);
                Dialogs.INSTANCE.dismissDialog();
                mAdapter.getItem(mAdapter.getSelectedItemIndex()).setUploading(false);
                mAdapter.notifyItemChanged(mAdapter.getSelectedItemIndex());
                Utils.appToast(mCurrentActivity, errorMessage);
            }
        }
    };


    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull String[] permissions, @NonNull final int[] grantResults) {
        if (mCurrentActivity != null) {
            mCurrentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (requestCode) {
                        case 1011:
                            if (grantResults.length > 0) {
                                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                                    onPermissionResult();
                                } else {
                                    checkPermissions();
                                }
                            }
                            break;
                    }
                }
            });
        }
    }


    private void onPermissionResult() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(PERMISSION) /*&& shouldShowRequestPermissionRationale(PHONE_STATE)*/) {
                Dialogs.INSTANCE.showAlertDialogNotSingleton(mCurrentActivity,
                        new StringCallBack() {
                            @Override
                            public void onCallBack(String msg) {
                                checkPermissions();
                            }
                        }, null, getString(R.string.camera_permission)
                        , getString(R.string.permissions_docs));
            } else {
                Dialogs.INSTANCE.showPermissionSettings(mCurrentActivity,
                        1011, getString(R.string.permissions_required),
                        getString(R.string.java_camera_permission_msg));
            }
        }
    }

    private boolean checkPermissions() {
        boolean hasPermission = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int location = ContextCompat.checkSelfPermission(mCurrentActivity.getApplicationContext(), PERMISSION);
            if (location != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{PERMISSION}, 1011);
            } else {
                hasPermission = true;
            }
        } else {
            hasPermission = true;
        }
        return hasPermission;
    }

    private void nextActivity() {
        if (DocumentsGridAdapter.getmInstanceForNullCheck() != null) {
            DocumentsGridAdapter.getInstance().resetTheInstance();
        }
        AppPreferences.setSignUpApiCalled(true);
        Intent intent = new Intent(mCurrentActivity, JsBankFingerSelectionActivity.class);
        intent.putExtra(Constants.Extras.CNIC, CNIC);
        intent.putExtra(Constants.Extras.SELECTED_ITEM, mSelectedCity);
        intent.putExtra(Constants.Extras.DRIVER_ID, DRIVER_ID);
        intent.putExtra(Constants.Extras.SIGN_UP_DATA, signUpData);
        startActivity(intent);
        Dialogs.INSTANCE.dismissDialog();
        mCurrentActivity.finish();
    }
    /*private boolean checkPermissions() {
        boolean hasPermission = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int camera = ContextCompat.checkSelfPermission(mCurrentActivity.getApplicationContext(), PERMISSION);
            int read_es = ContextCompat.checkSelfPermission(mCurrentActivity.getApplicationContext(), PERMISSION_READ_ES);
            if (read_es != -PackageManager.PERMISSION_GRANTED &&
                    camera != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{PERMISSION, PERMISSION_READ_ES}, 1011);
            } else if (camera != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{PERMISSION}, 1011);
            } else if (read_es != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{PERMISSION_READ_ES}, 1011);
            } else {
                hasPermission = true;
            }
        } else {
            hasPermission = true;
        }
        return hasPermission;
    }*/
}

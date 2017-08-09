package com.bykea.pk.partner.ui.fragments;


import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bykea.pk.partner.ui.activities.LoginActivity;
import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.ServiceTypeData;
import com.bykea.pk.partner.models.response.RegisterResponse;
import com.bykea.pk.partner.models.response.ServiceTypeResponse;
import com.bykea.pk.partner.models.response.UploadDocumentFile;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Permissions;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontEditText;
import com.bykea.pk.partner.widgets.FontTextView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;


public class VehicleInfoFragment extends Fragment {


    @Bind(R.id.backBtn)
    ImageView backBtn;
    @Bind(R.id.sendBtn)
    FontTextView sendBtn;
    @Bind(R.id.plateNoEt)
    FontEditText plateNoEt;
    @Bind(R.id.licenseNoEt)
    FontEditText licenseNoEt;
    @Bind(R.id.expiryEt)
    FontEditText expiryEt;
    @Bind(R.id.cnicEt)
    FontEditText cnicEt;
    @Bind(R.id.addLicenseIv)
    ImageView addLicenseIv;
    @Bind(R.id.serviceTypeSp)
    Spinner serviceTypeSp;
    private ArrayList<String> serviceTypes = new ArrayList<>();


    private LoginActivity mCurrentActivity;
    private UserRepository repository;
    private String mPath;
    private Uri mCropImageUri;
    private Bitmap imageBitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vehicle_info, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new UserRepository();
        mCurrentActivity = ((LoginActivity) getActivity());
        getServiceTypes();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void getServiceTypes() {
        if (Connectivity.isConnectedFast(mCurrentActivity)) {
            repository.requestGetServiceTypes(mCurrentActivity, new UserDataHandler() {
                @Override
                public void onGetServiceTypes(ServiceTypeResponse serviceTypeResponse) {
                    serviceTypes.clear();
                    serviceTypes.add("Service Type");
                    if (serviceTypeResponse.isSuccess()) {
                        for (ServiceTypeData serviceTypeData : serviceTypeResponse.getData())
                            serviceTypes.add(serviceTypeData.getName());
                        setServiceSpinner();
                    }
                }

                @Override
                public void onError(int errorCode, String errorMessage) {
                    getServiceTypes();
                }
            });
        } else {
            Dialogs.INSTANCE.showError(mCurrentActivity, sendBtn, getString(R.string.error_internet_connectivity));
        }
    }

    private void setServiceSpinner() {
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
                serviceTypes) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) return false;
                return super.isEnabled(position);
            }

            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                TextView tv = ((TextView) v);
                Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_light.ttf");
                tv.setTypeface(typeface);
                if (position == 0)
                    tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.textColorSecondary));
                else
                    tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.textColorPrimary));

                tv.setText(serviceTypes.get(position));
                tv.setSingleLine();
                tv.setEllipsize(TextUtils.TruncateAt.END);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                        getResources().getDimension(R.dimen._10sdp) / getResources().getDisplayMetrics().density);
                return v;
            }

            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                TextView tv = ((TextView) v);
                Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_light.ttf");
                tv.setTypeface(typeface);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                        getResources().getDimension(R.dimen._10sdp) / getResources().getDisplayMetrics().density);
                tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.textColorPrimary));
                return v;
            }
        };
        stateAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        serviceTypeSp.setAdapter(stateAdapter);
        serviceTypeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    mCurrentActivity.getPilotData().setVehicleType(serviceTypes.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        serviceTypeSp.setSelection(0);

    }

    @OnClick({R.id.backBtn, R.id.sendBtn, R.id.expiryEt, R.id.addLicenseIv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addLicenseIv:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        !Permissions.hasCameraPermissions(getActivity()))
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
                else
                    startActivityForResult(CropImage.getPickImageChooserIntent(getContext()), 200);
                break;
            case R.id.backBtn:
                getActivity().onBackPressed();
                break;
            case R.id.expiryEt:
                showDatePicker();
                break;
            case R.id.sendBtn:
                if (isValid()) {
                    Dialogs.INSTANCE.showLoader(mCurrentActivity);
                    mCurrentActivity.getLocation();
                    mCurrentActivity.getPilotData().setPlateNo(plateNoEt.getText().toString());
                    mCurrentActivity.getPilotData().setLicenseNo(licenseNoEt.getText().toString());
                    mCurrentActivity.getPilotData().setLicenseExpiry(expiryEt.getText().toString());
                    mCurrentActivity.getPilotData().setCnic(cnicEt.getText().toString()
                            .replace("-", ""));
                    repository.requestUserRegister(mCurrentActivity, handler, mCurrentActivity.getPilotData());
                }

                break;
        }
    }

    /**********************************************************************
     * Method to show date picker dialog to select expiry dates of license *
     *********************************************************************/
    private void showDatePicker() {
        Utils.hideKeyboard(getActivity());
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());

        Dialogs.INSTANCE.showDatePicker(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        Calendar cal = Calendar.getInstance();
                        cal.set(year, monthOfYear, dayOfMonth);
                        Calendar cal2 = Calendar.getInstance();
                        cal2.setTimeInMillis(System.currentTimeMillis());
                        if (cal.compareTo(cal2) == 1) {
                            expiryEt.setText(dayOfMonth + "-" + monthOfYear + "-" + year);
                        } else {
                            Dialogs.INSTANCE.showError(getActivity(), sendBtn,
                                    getString(R.string.error_license_expired));
                        }
                    }
                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH) + 1,
                mCalendar.get(Calendar.DAY_OF_MONTH), new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Dialogs.INSTANCE.dismissDialog();
                    }
                });
    }

    private UserDataHandler handler = new UserDataHandler() {
        @Override
        public void onUserRegister(RegisterResponse registerUser) {
            Dialogs.INSTANCE.dismissDialog();
            if (registerUser.isSuccess()) {
                AppPreferences.setPilotData(mCurrentActivity, registerUser.getUser());
                AppPreferences.saveLoginStatus(mCurrentActivity, true);
                AppPreferences.setAvailableStatus(getActivity(), registerUser.getUser().isAvailable());
                AppPreferences.setVerifiedStatus(getActivity(), registerUser.getUser().isVerified());
                ActivityStackManager.getInstance(mCurrentActivity.getApplicationContext()).startLocationService();

                // Connect socket
                ((DriverApp) getActivity().getApplicationContext()).connect("VEHICLE FRAGMENT");
                ActivityStackManager.getInstance(getActivity()).startHomeActivity(false);
                getActivity().finish();
            } else {
                Dialogs.INSTANCE.showError(getActivity(), sendBtn, registerUser.getMessage());
            }
        }

        @Override
        public void onUploadFile(UploadDocumentFile uploadDocumentFile) {
            Dialogs.INSTANCE.dismissDialog();
            if (uploadDocumentFile.isSuccess()) {
                mCurrentActivity.getPilotData().setLicenseImage(uploadDocumentFile.getImagePath());
                addLicenseIv.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_add_photo_active));
                Dialogs.INSTANCE.showSuccessMessage(mCurrentActivity, sendBtn,
                        uploadDocumentFile.getMessage());
            } else {
                Dialogs.INSTANCE.showError(getActivity(), sendBtn, uploadDocumentFile.getMessage());
            }
        }

        @Override
        public void onError(int errorCode, String errorMessage) {
            Dialogs.INSTANCE.dismissDialog();
            Dialogs.INSTANCE.showError(getActivity(), sendBtn, errorMessage);
        }
    };

    private boolean isValid() {
        if (Connectivity.isConnectedFast(mCurrentActivity)) {
            if (StringUtils.isBlank(plateNoEt.getText().toString())) {
                plateNoEt.setError(getString(R.string.error_field_empty));
                plateNoEt.requestFocus();
                return false;
            } else if (StringUtils.isBlank(licenseNoEt.getText().toString())) {
                licenseNoEt.setError(getString(R.string.error_field_empty));
                licenseNoEt.requestFocus();
                return false;
            }/* else if (StringUtils.isBlank(expiryEt.getText().toString())) {
                expiryEt.setError(getString(R.string.error_field_empty));
                expiryEt.requestFocus();
                return false;
            }*/ else if (StringUtils.isBlank(cnicEt.getText().toString())) {
                cnicEt.setError(getString(R.string.error_field_empty));
                cnicEt.requestFocus();
                return false;
            } else if (StringUtils.isBlank(mCurrentActivity.getPilotData().getVehicleType())) {
                Dialogs.INSTANCE.showError(mCurrentActivity, sendBtn, "Select Service type.");
                if (serviceTypes.size() <= 0) getServiceTypes();
                return false;
            } /*else if (StringUtils.isBlank(mCurrentActivity.getPilotData().getLicenseImage())) {
                Dialogs.INSTANCE.showError(mCurrentActivity, sendBtn, "Upload License Image");
                return false;
            }*/

            return true;
        } else {
            Dialogs.INSTANCE.showError(mCurrentActivity, sendBtn, getString(R.string.error_internet_connectivity));
            return false;
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(getContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(getContext(), data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(getActivity(), imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mCropImageUri = result.getUri();
                Dialogs.INSTANCE.showLoader(getActivity());
                repository.requestUploadFile(getActivity(), handler, new File(mCropImageUri.getPath()));
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Dialogs.INSTANCE.showError(getActivity(), sendBtn, "Cropping failed, Try again");
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // required permissions granted, start crop image activity
                    startCropImageActivity(mCropImageUri);
                } else {
                    Dialogs.INSTANCE.showError(getActivity(), sendBtn, "Required permissions are not granted");
                }
                break;
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // required permissions granted, start crop image activity
                    startActivityForResult(CropImage.getPickImageChooserIntent(getContext()), 200);
                } else {
                    Dialogs.INSTANCE.showError(getActivity(), sendBtn, "Required permissions are not granted");
                }
                break;
        }
    }

}

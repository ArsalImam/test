package com.bykea.pk.partner.ui.nodataentry

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.View
import android.widget.SeekBar
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetailInfo
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetails
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetailsLocationInfoData
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.MetaData
import com.bykea.pk.partner.dal.util.DIGIT_ZERO
import com.bykea.pk.partner.databinding.ActivityAddEditDeliveryDetailsBinding
import com.bykea.pk.partner.models.data.PlacesResult
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.bykea.pk.partner.ui.activities.SelectPlaceActivity
import com.bykea.pk.partner.ui.common.obtainViewModel
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.*
import com.bykea.pk.partner.utils.Constants.*
import com.bykea.pk.partner.utils.Constants.Extras.*
import com.bykea.pk.partner.utils.Constants.ServiceCode.SEND
import com.bykea.pk.partner.utils.Constants.ServiceCode.SEND_COD
import com.bykea.pk.partner.utils.Constants.TripTypes.DELIVERY_TYPE
import com.bykea.pk.partner.utils.audio.BykeaAmazonClient
import com.bykea.pk.partner.utils.audio.Callback
import com.bykea.pk.partner.utils.audio.MediaPlayerHolder
import com.bykea.pk.partner.utils.audio.PlaybackInfoListener
import com.bykea.pk.partner.widgets.record_view.OnRecordListener
import kotlinx.android.synthetic.main.activity_add_edit_delivery_details.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.math.NumberUtils
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * This activity is being used to add or update the batch booking.
 */
class AddEditDeliveryDetailsActivity : BaseActivity() {
    lateinit var binding: ActivityAddEditDeliveryDetailsBinding
    lateinit var viewModel: AddEditDeliveryDetailsViewModel

    private var mUserIsSeeking = false
    lateinit var mMediaPlayerHolder: MediaPlayerHolder
    private var audioTimer: Timer? = null
    private var mRecorder: MediaRecorder? = null
    private var isRecordingAudio: Boolean = false
    private var isAudioReleased: Boolean = false
    private val audioRecordTimerHandler = Handler()
    private var recordedAudioTime = DIGIT_ZERO
    var flowForAddOrEdit: Int = DIGIT_ZERO
    var mDropOffResult: PlacesResult? = null
    private var isFileDownloadFromAmazon: Boolean = false
    private var voiceNoteUploadUrl: String = StringUtils.EMPTY
    private var isFileUploadToAmazonRequired: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_delivery_details)
        viewModel = obtainViewModel(AddEditDeliveryDetailsViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@AddEditDeliveryDetailsActivity

        // CHECK FLOW IS FOR ADD OR EDIT DELIVERY DETAILS
        if (intent != null && intent?.extras != null) {
            if (intent?.extras!!.containsKey(FLOW_FOR)) {
                flowForAddOrEdit = intent?.extras!!.get(FLOW_FOR) as Int
            }
            if (intent?.extras!!.containsKey(FAILED_BOOKING_ID)) {
                viewModel.failedBookingId = intent?.extras!!.get(FAILED_BOOKING_ID) as String
            }
            if (intent?.extras!!.containsKey(DELIVERY_DETAILS_OBJECT)) {
                viewModel.deliveryDetails.value = intent?.extras!!.getParcelable(DELIVERY_DETAILS_OBJECT) as DeliveryDetails
                tVLocationAlphabet.text = viewModel.deliveryDetails.value?.details?.display_tag
                fLLocation.visibility = View.VISIBLE
            }
        }

        // handled case for open api
        // if latitude/longitude is not exist but gps_address is available, need to ask user to
        // reselect drop off location
        // [ref] https://bykeapk.atlassian.net/browse/BS-5168
        viewModel.deliveryDetails?.value?.let {
            if (StringUtils.isNotEmpty(it.dropoff?.gps_address) && (it.dropoff?.lat == null || it.dropoff?.lat == NumberUtils.DOUBLE_ZERO)) {
                it.dropoff?.gps_address = StringUtils.EMPTY
            }
        }

        setTitleCustomToolbarUrdu(getString(R.string.parcel_details))

        viewModel.getActiveTrip()

        viewModel.isAddedOrUpdatedSuccessful.observe(this@AddEditDeliveryDetailsActivity, androidx.lifecycle.Observer {
            val intent = Intent()
            intent.putExtra(DELIVERY_DETAILS_OBJECT, viewModel.deliveryDetails.value)
            setResult(RESULT_OK, intent)
            finish()
        })

        viewModel.isCashLimitLow.observe(this@AddEditDeliveryDetailsActivity, androidx.lifecycle.Observer {
            if (it) {
                viewModel.isCashLimitLow.value = false
                viewModel.isCashLimitLeftValue.value?.let { cashLeftAmount ->
                    Dialogs.INSTANCE.showAmountLimitMessageDialog(this@AddEditDeliveryDetailsActivity, cashLeftAmount)
                }
            }
        })

        viewModel.isCustomerWalletTopUpRequired.observe(this@AddEditDeliveryDetailsActivity, androidx.lifecycle.Observer {
            if (it) {
                viewModel.isCustomerWalletTopUpRequired.value = false
                Dialogs.INSTANCE.showPassengerNegativeDialog(this@AddEditDeliveryDetailsActivity)
            }
        })

        binding.listener = object : GenericListener {
            override fun addOrEditDeliveryDetails() {
                pausePlaying()
                if (isValidate()) {
                    if (Utils.isConnected(this@AddEditDeliveryDetailsActivity, true)) {
                        Dialogs.INSTANCE.showLoader(this@AddEditDeliveryDetailsActivity)
                        if (audioPlayRL.visibility == View.VISIBLE && isFileUploadToAmazonRequired) {
                            //voice note is recorded by user.. upload to amazon
                            BykeaAmazonClient.uploadFile(fileNameToBeUploadedToAmazon(), createAudioFile(), object : Callback<String> {
                                override fun success(obj: String) {
                                    voiceNoteUploadUrl = obj
                                    callPostApiNow()
                                }

                                override fun fail(errorCode: Int, errorMsg: String) {
                                    Utils.appToast(getString(R.string.error_uploading_file))
                                }
                            })
                        } else {
                            callPostApiNow()
                        }
                    }
                }
            }

            override fun navigateToPlaceSearch(view: View) {
                Utils.preventMultipleTap(view)
                Intent(this@AddEditDeliveryDetailsActivity, SelectPlaceActivity::class.java).apply {
                    putExtra(FROM, CONFIRM_DROPOFF_REQUEST_CODE)
                    startActivityForResult(this, CONFIRM_DROPOFF_REQUEST_CODE)
                }
            }
        }

        audioRecordButton.setRecordView(audioRecordView)
        addListeners()
        checkIfRecordPermissionRequired()
        mMediaPlayerHolder = MediaPlayerHolder(this@AddEditDeliveryDetailsActivity)
        mMediaPlayerHolder.setPlaybackInfoListener(PlaybackListener())
        initViews()
        setTextChangeListeners()
    }

    private fun setTextChangeListeners() {
        editTextMobileNumber.addTextChangedListener(object : TextWatcherUtil() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                editTextMobileNumber.error = null
                validateMobileNumber(false)
            }
        })

        textViewGPSAddress.addTextChangedListener(object : TextWatcherUtil() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                textViewGPSAddress.error = null
                validateGPSAddress(false)
            }
        })

        editTextParcelValue.addTextChangedListener(object : TextWatcherUtil() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                editTextParcelValue.error = null
                validateParcelValue(false)
            }
        })

        editTextCODAmount.addTextChangedListener(object : TextWatcherUtil() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                editTextCODAmount.error = null
                validateCODAmount(false)
            }
        })
    }

    /**
     * Validate Mobile Number
     */
    private fun validateMobileNumber(showError: Boolean = true): Boolean {
        return if (!Utils.isValidNumber(editTextMobileNumber)) {
            if (showError) {
                editTextMobileNumber.requestFocus()
                editTextMobileNumber.error = getString(R.string.error_phone_number_1)
                linLayoutMobileNumber.setBackgroundResource(R.drawable.border_details_form_square_red)
            }
            false
        } else {
            editTextMobileNumber.error = null
            linLayoutMobileNumber.setBackgroundResource(R.drawable.border_details_form_square_light)
            true
        }
    }

    /**
     * Validate GPS Address
     */
    private fun validateGPSAddress(showError: Boolean = true): Boolean {
        return if (textViewGPSAddress.text.isNullOrEmpty()) {
            if (showError) {
                Dialogs.INSTANCE.showToast(getString(R.string.select_gps_address))
                textViewGPSAddress.error = getString(R.string.select_gps_address)
                linLayoutGPSAddress.setBackgroundResource(R.drawable.border_details_form_square_red)
            }
            false
        } else {
            textViewGPSAddress.error = null
            linLayoutGPSAddress.setBackgroundResource(R.drawable.border_details_form_square_light)
            true
        }
    }

    /**
     * Validate Parcel Value
     */
    private fun validateParcelValue(showError: Boolean = true): Boolean {
        return if (editTextParcelValue.text.isNullOrEmpty() || editTextParcelValue.text.toString().trim().toDouble() == NumberUtils.DOUBLE_ZERO) {
            if (showError) {
                editTextParcelValue.requestFocus()
                editTextParcelValue.error = getString(R.string.enter_correct_parcel_value)
                linLayoutParcelValue.setBackgroundResource(R.drawable.border_details_form_square_red)
            }
            false
        } else if (editTextParcelValue.text.isNullOrEmpty() ||
                (editTextParcelValue.text.toString().trim().toDouble() < (NumberUtils.DOUBLE_ONE) &&
                        editTextParcelValue.text.toString().trim().toDouble() > (AMOUNT_LIMIT + DIGIT_ONE).toDouble())) {
            if (showError) {
                editTextParcelValue.requestFocus()
                editTextParcelValue.error = String.format(getString(R.string.parcel_value_cannot_greater).plus(StringUtils.SPACE).plus(getString(R.string.amount_rs_int)), AMOUNT_LIMIT)
                linLayoutParcelValue.setBackgroundResource(R.drawable.border_details_form_square_red)
            }
            false
        } else {
            editTextParcelValue.error = null
            linLayoutParcelValue.setBackgroundResource(R.drawable.border_details_form_square_light)
            true
        }
    }

    /**
     * Validate COD Amount
     */
    private fun validateCODAmount(showError: Boolean = true): Boolean {
        return if (editTextCODAmount.text.toString().isNotEmpty() &&
                editTextCODAmount.text.toString().trim().toInt() == DIGIT_ZERO) {
            if (showError) {
                editTextCODAmount.requestFocus()
                editTextCODAmount.error = getString(R.string.enter_correct_cod_amount)
                linLayoutCODAmount.setBackgroundResource(R.drawable.border_details_form_square_red)
            }
            false
        } else {
            editTextCODAmount.error = null
            linLayoutCODAmount.setBackgroundResource(R.drawable.border_details_form_square_light)
            true
        }
    }

    /**
     * creating unique file name to be uploaded to amazon
     */
    private fun fileNameToBeUploadedToAmazon() =
            "${DELIVERY_TYPE.toLowerCase()}_${AppPreferences.getDriverId()}_${System.currentTimeMillis()}"


    /**
     * call delivery POST api after uploading file to amazon
     */
    private fun callPostApiNow() {
        when (flowForAddOrEdit) {
            ADD_DELIVERY_DETAILS -> {
                viewModel.requestAddDeliveryDetails(createRequestBodyForAddEdit())
            }
            EDIT_DELIVERY_DETAILS -> {
                viewModel.requestEditDeliveryDetail(createRequestBodyForAddEdit())
            }
        }
    }

    /**
     * Initializes views for Parcel Summary fragment
     */
    private fun initViews() {
        if (Permissions.hasAudioPermissions() && !viewModel.deliveryDetails.value?.details?.voice_note.isNullOrEmpty()) {
            showPlayAudioLayout()
            retrieveAudioFileFromAmazon(viewModel.deliveryDetails.value?.details?.voice_note!!, false)
        }
    }

    override fun onPause() {
        super.onPause()
        stopRecording()
        stopAudioTimer()
        pausePlaying()
    }

    /**
     * Validate the fields which are required
     */
    fun isValidate(): Boolean {
        return validateMobileNumber() && validateGPSAddress() && validateParcelValue() && validateCODAmount()
    }

    /**
     * Create Request For Add or Edit DeliveryDetails
     */
    private fun createRequestBodyForAddEdit(): DeliveryDetails {
        return DeliveryDetails().apply {
            meta = MetaData()
            dropoff = DeliveryDetailsLocationInfoData()
            details = DeliveryDetailInfo()

            if (editTextCODAmount.text.isNullOrEmpty()) {
                meta?.service_code = SEND
            } else {
                meta?.service_code = SEND_COD
            }

            // DELIVERY DETAILS DROP OFF - PHONE
            if (!editTextMobileNumber.text.isNullOrEmpty()) {
                dropoff?.phone = Utils.phoneNumberForServer(editTextMobileNumber.text.toString().trim())
            }
            // DELIVERY DETAILS DROP OFF - CONSIGNEE NAME
            if (!editTextConsigneeName.text.isNullOrEmpty()) {
                dropoff?.name = editTextConsigneeName.text.toString().trim()
            }

            // DELIVERY DETAILS DROP OFF - ADDRESS
            if (!editTextAddress.text.isNullOrEmpty()) {
                dropoff?.address = editTextAddress.text.toString().trim()
            }

            mDropOffResult?.let {
                dropoff?.gps_address = mDropOffResult?.name
                dropoff?.lat = mDropOffResult?.latitude
                dropoff?.lng = mDropOffResult?.longitude
            } ?: run {
                dropoff?.gps_address = viewModel.deliveryDetails.value?.dropoff?.gps_address
                dropoff?.lat = viewModel.deliveryDetails.value?.dropoff?.lat
                dropoff?.lng = viewModel.deliveryDetails.value?.dropoff?.lng
            }

            // DELIVERY DETAILS INFO - PARCEL VALUE
            if (!editTextParcelValue.text.isNullOrEmpty()) {
                details?.parcel_value = editTextParcelValue.text.toString().trim()
            }

            // DELIVERY DETAILS INFO - ORDER NUMBER VALUE
            if (!editTextOrderNumber.text.isNullOrEmpty()) {
                details?.order_no = editTextOrderNumber.text.toString().trim()
            }

            // DELIVERY DETAILS INFO - COD VALUE
            if (!editTextCODAmount.text.isNullOrEmpty()) {
                details?.cod_value = editTextCODAmount.text.toString().trim()
            }

            // DELIVERY DETAIL INFO - VOICE NOTE URL
            viewModel.deliveryDetails.value?.details?.voice_note?.let {
                //IF VOICE NOTE URL IS ALREADY PRESENT
                if (voiceNoteUploadUrl.isNotEmpty() && it != voiceNoteUploadUrl) {
                    //IF VOICE NOTE IS UPLOADED
                    details?.voice_note = voiceNoteUploadUrl
                } else if (audioPlayRL.visibility == View.VISIBLE) {
                    //IF VOICE NOTE IS SAME
                    details?.voice_note = it
                }
            } ?: run {
                //IF VOICE NOTE URL IS NOT PRESENT
                if (voiceNoteUploadUrl.isNotEmpty()) {
                    details?.voice_note = voiceNoteUploadUrl
                }
            }
        }
    }

    /**
     * hide voice message bhejen text hint
     */
    private fun hideSendVoiceMsgHintText() {
        if (audioSendVoiceMsgHintTV.visibility == View.VISIBLE)
            audioSendVoiceMsgHintTV.visibility = View.GONE
    }

    /**
     * hide voice message bhejen text hint
     */
    private fun showSendVoiceMsgHintText() {
        audioSendVoiceMsgHintTV.visibility = View.VISIBLE
    }

    /**
     * show audio play layout e.g play pause seekbar time and delete voice button
     */
    private fun showPlayAudioLayout() {
        setPlayIcon(true)
        hideSendVoiceMsgHintText()
        audioRecordingRL.visibility = View.GONE
        audioPlayRL.visibility = View.VISIBLE
        audioSeekTimeTV.text = getFormattedTime(recordedAudioTime)
    }

    /**
     * show audio Record layout e.g voice mic button
     */
    private fun showRecordAudioLayout() {
        showSendVoiceMsgHintText()
        audioRecordingRL.visibility = View.VISIBLE
        audioPlayRL.visibility = View.GONE
        setPlayIcon(true)
    }

    /**
     * start voice recording
     */
    @Synchronized
    private fun startRecording() {
        if (!isRecordingAudio) {

            val audioFile = createAudioFile()

            //setting up media recorder
            if (mRecorder == null) {
                mRecorder = MediaRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setAudioChannels(Constants.Audio.AUDIO_CHANNELS)
                    setAudioEncodingBitRate(Constants.Audio.BIT_RATES)
                    setAudioSamplingRate(Constants.Audio.SAMPLE_RATES)
                    setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
                    setOutputFile(audioFile.path)
                    setMaxDuration(Constants.Audio.AUDIO_MAX_DURATION_IN_MILLIS) //60s
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                }
            }
            mRecorder?.apply {
                prepare()
                start()
                startAudioRecordTimer()
                hideSendVoiceMsgHintText()
            }
            isRecordingAudio = true
        }
    }

    /**
     * stop voice recording
     */
    @Synchronized
    private fun stopRecording() {
        if (mRecorder != null)
            mRecorder?.release()
        mRecorder = null
        isRecordingAudio = false
    }

    /**
     * toggle play pause icon
     * @param showPlayIcon toggle play or pause icon
     */
    private fun setPlayIcon(showPlayIcon: Boolean) {
        if (showPlayIcon) audioPlayPauseIV.setImageResource(R.drawable.icon_play) else audioPlayPauseIV.setImageResource(R.drawable.icon_pause)
    }

    /**
     * start playing recorded voice
     */
    private fun startPlaying() {
        if (!mMediaPlayerHolder.isPlaying) {
            if (isFileUploadToAmazonRequired) {
                mMediaPlayerHolder.loadUri(createAudioFile().path)
                mMediaPlayerHolder.play()
            } else {
                viewModel.deliveryDetails.value?.details?.voice_note?.let {
                    if (isFileDownloadFromAmazon) {
                        mMediaPlayerHolder.play()
                    } else {
                        retrieveAudioFileFromAmazon(it)
                    }
                } ?: run {
                    mMediaPlayerHolder.loadUri(createAudioFile().path)
                    mMediaPlayerHolder.play()
                }
            }

            setPlayIcon(false)
        }
    }

    private fun retrieveAudioFileFromAmazon(url: String, isShowOrHideLoader: Boolean = true) {
        if (isShowOrHideLoader) {
            Dialogs.INSTANCE.showLoader(this@AddEditDeliveryDetailsActivity)
        }
        BykeaAmazonClient.getFileObject(url, object : Callback<File> {
            override fun success(obj: File) {
                mMediaPlayerHolder.loadFile(obj)
                mMediaPlayerHolder.mMediaPlayer?.duration?.let { duration ->
                    recordedAudioTime = TimeUnit.MILLISECONDS.toSeconds(duration.toLong()).toInt()
                    audioSeekTimeTV.text = getFormattedTime(recordedAudioTime)
                }
                isFileDownloadFromAmazon = true
                if (isShowOrHideLoader) {
                    Dialogs.INSTANCE.dismissDialog()
                    mMediaPlayerHolder.play()
                }
            }

            override fun fail(errorCode: Int, errorMsg: String) {
                if (isShowOrHideLoader) {
                    Dialogs.INSTANCE.dismissDialog()
                    Dialogs.INSTANCE.showToast(getString(R.string.no_voice_note_available))
                }
            }
        })
    }

    /**
     * pause currenctly playing recorded voice
     */
    fun pausePlaying() {
        mMediaPlayerHolder.pause()
        setPlayIcon(true)
    }

    /**
     * record audio recording time to display when playing recorded audio
     */
    private fun startAudioRecordTimer() {
        isFileUploadToAmazonRequired = true
        recordedAudioTime = NEGATIVE_DIGIT_ONE
        audioTimer = Timer(false)
        audioTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                recordedAudioTime++
                if (recordedAudioTime >= Constants.Audio.AUDIO_MAX_DURATION) {
                    stopRecording()
                    stopAudioTimer()
                    audioRecordTimerHandler.post {
                        /*UI event call from thread*/
                        onFinishRecording()
                    }
                    return
                }
            }
        }, Constants.DIGIT_ZERO.toLong(), Constants.DIGIT_THOUSAND.toLong())
    }

    /**
     * stop timer for audio recording
     */
    private fun stopAudioTimer() {
        if (audioTimer != null) {
            audioTimer?.cancel()
            audioTimer?.purge() //remove all cancelled tasks from this timer's task queue.
        }
    }

    /**
     * adding UI listeners
     */
    private fun addListeners() {
        audioDeleteIV.setOnClickListener {
            isFileUploadToAmazonRequired = true
            mMediaPlayerHolder.reset()
            mMediaPlayerHolder.release()
            setPlayIcon(true)
            audioSeekbar.progress = DIGIT_ZERO
            Utils.deleteFile(getAudioFile())
            showRecordAudioLayout()
        }

        audioPlayPauseIV.setOnClickListener {
            if (mMediaPlayerHolder.isPlaying) pausePlaying() else startPlaying()
        }

        audioSeekbar.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    var userSelectedPosition = DIGIT_ZERO

                    override fun onStartTrackingTouch(seekBar: SeekBar) {
                        mUserIsSeeking = true
                    }

                    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                        if (fromUser) {
                            userSelectedPosition = progress
                        }
                        audioSeekTimeTV.text = getFormattedTime(Math.ceil(progress.toDouble() / Constants.DIGIT_THOUSAND.toDouble()).toInt())
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        mUserIsSeeking = false
                        mMediaPlayerHolder.seekTo(userSelectedPosition)
                    }
                })

        registerOnRecordingListener()
    }

    /**
     * register voice recording listener
     */
    private fun registerOnRecordingListener() {
        audioRecordView.setOnRecordListener(onRecordListener)
    }

    val onRecordListener = object : OnRecordListener {
        override fun onStart() {
            isAudioReleased = false
            if (checkClickTime()) {
                Handler().postDelayed({
                    if (!isAudioReleased) {
                        startRecording()
                    }
                }, Constants.MINIMUM_VOICE_RECORDING.toLong())
            } else {
                startRecording()
            }
        }

        override fun onFinish(recordTime: Long) {
            onFinishRecording()
        }

        override fun onCancel() {
            onCancelRecording()
        }

        override fun onLessThanSecond() {
            onLessThanSecondRecording()
        }
    }

    /**
     * check if audio and storage permission is required
     */
    private fun checkIfRecordPermissionRequired() {
        if (!Permissions.hasAudioPermissions()) {
            audioRecordButton.isListenForRecord = false
            audioRecordButton.setOnRecordClickListener {
                ActivityCompat.requestPermissions(this@AddEditDeliveryDetailsActivity, arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        RequestCode.REQUEST_AUDIO_PERMISSION)
            }
        }
    }

    /**
     * cancel recording voice
     */
    @Synchronized
    private fun onCancelRecording() {
        showSendVoiceMsgHintText()
        stopRecording()
        stopAudioTimer()
    }

    /**
     * finish/complete recording voice
     */
    @Synchronized
    private fun onFinishRecording() {
        showPlayAudioLayout()
        stopRecording()
        stopAudioTimer()
    }

    /**
     * when recording voice is less than 1 sec
     */
    @Synchronized
    private fun onLessThanSecondRecording() {
        showSendVoiceMsgHintText()
        stopRecording()
        stopAudioTimer()
        isAudioReleased = true
    }

    /**
     * formatting recording or playing time to be displayed as given in UI
     * @param time time is secs to display in UI
     */
    private fun getFormattedTime(time: Int): String {
        return if (time < DIGIT_TEN) "0:0$time" else "0:$time"
    }

    /**
     * permission callback
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[DIGIT_ZERO] == PackageManager.PERMISSION_GRANTED
                && grantResults[DIGIT_ONE] == PackageManager.PERMISSION_GRANTED) {
            audioRecordButton.isListenForRecord = true
            registerOnRecordingListener()
        } else {
            onPermissionDenied()
        }
    }

    /**
     * Handles user denied Permissions cases
     */
    private fun onPermissionDenied() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)
                    && shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Dialogs.INSTANCE.showAlertDialogNew(this@AddEditDeliveryDetailsActivity,
                        getString(R.string.permissions_required), getString(R.string.permission_msg_audio)) {
                    Dialogs.INSTANCE.dismissDialog()
                    ActivityCompat.requestPermissions(this@AddEditDeliveryDetailsActivity, arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            RequestCode.REQUEST_AUDIO_PERMISSION)
                }
            } else {
                Dialogs.INSTANCE.showPermissionSettings(this@AddEditDeliveryDetailsActivity,
                        Constants.RequestCode.PERMISSIONS_REQUEST_FOR_AUDIO_RECORDING_FROM_SETTINGS, getString(R.string.permissions_required),
                        getString(R.string.permission_msg_audio_settings))
            }
        }
    }

    /**
     * Getting method for voice recording file stored in local storage it will create new file if
     * file doesn't exist already
     *
     * @return Audio File existing or new one
     */
    fun createAudioFile(): File {
        val file = File(getFilePath(), Constants.Audio.AUDIO_FILE_NAME)
        if (!file.exists()) {
            file.parentFile.mkdirs()
        }
        return file
    }

    /**
     * Getting method for voice recording file stored in local storage
     * @return Audio File
     */
    private fun getAudioFile(): File {
        return File(getFilePath(), Constants.Audio.AUDIO_FILE_NAME)
    }

    /**
     * getting voice recording file path stored in local storage
     */
    private fun getFilePath(): String {
        return "${Environment.getExternalStorageDirectory()}${File.separator}${Constants.Audio.AUDIO_FOLDER_NAME}"
    }

    /**
     * media player listener for tracking current position of playing audio
     */
    inner class PlaybackListener : PlaybackInfoListener() {

        override fun onDurationChanged(duration: Int) {
            audioSeekbar.max = duration
        }

        override fun onPositionChanged(position: Int) {
            if (!mUserIsSeeking) {
                audioSeekbar.progress = position
            }
        }

        override fun onStateChanged(@State state: Int) {

        }

        override fun onPlaybackCompleted() {
            audioSeekTimeTV.text = getFormattedTime(recordedAudioTime)
            pausePlaying()
        }
    }

    /**
     * Set The DropOff Address
     */
    private fun setCallForETA() {
        textViewGPSAddress.text = mDropOffResult?.name
    }

    /**
     * On Activity Result For
     * 1) Places Search Result
     * 2) Audio Recording Permission
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == CONFIRM_DROPOFF_REQUEST_CODE && data != null) {
            mDropOffResult = data.getParcelableExtra(Constants.CONFIRM_DROPOFF_ADDRESS_RESULT)
            if (mDropOffResult != null) {
                setCallForETA()
            } else {
                Utils.appToast(getString(R.string.error_try_again))
            }
        } else {
            if (Permissions.hasAudioPermissions()) {
                audioRecordButton.isListenForRecord = true
                registerOnRecordingListener()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
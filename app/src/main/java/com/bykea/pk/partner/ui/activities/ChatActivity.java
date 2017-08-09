package com.bykea.pk.partner.ui.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bykea.pk.partner.Notifications;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.ui.helpers.adapters.ChatAdapter;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.communication.socket.WebIORequestHandler;
import com.bykea.pk.partner.models.ChatMessage;
import com.bykea.pk.partner.models.ReceivedMessage;
import com.bykea.pk.partner.models.Sender;
import com.bykea.pk.partner.models.response.ConversationChatResponse;
import com.bykea.pk.partner.models.response.GetConversationIdResponse;
import com.bykea.pk.partner.models.response.SendMessageResponse;
import com.bykea.pk.partner.models.response.UpdateConversationStatusResponse;
import com.bykea.pk.partner.models.response.UploadAudioFile;
import com.bykea.pk.partner.repositories.IUserDataHandler;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.Permissions;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontEditText;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity {

    @Bind(R.id.messageEdit)
    FontEditText messageEdit;
    @Bind(R.id.chronometer)
    Chronometer chronometer;
    @Bind(R.id.voiceMsgLayout)
    FrameLayout voiceMsgLayout;
    @Bind(R.id.chatSendButton)
    ImageView chatSendButton;
    @Bind(R.id.messagesContainer)
    RecyclerView messagesContainer;
    @Bind(R.id.backBtn)
    ImageView backBtn;
    @Bind(R.id.titleTv)
    FontTextView titleTv;
    @Bind(R.id.textLayout)
    LinearLayout textLayout;
    @Bind(R.id.container)
    RelativeLayout container;
    @Bind(R.id.loader)
    ProgressBar loader;

    private ChatAdapter chatAdapter;
    private ArrayList<ChatMessage> messageList;

    @SuppressLint("InlinedApi")
    private int output_formats[] = {MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP};
    private int currentFormat = 0;
    private MediaRecorder recorder = null;
    private String filePath;
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";//2016-05-20T12:37:58.508Z
    private static final String REQUIRED_FORMAT = "EEEE, MMM dd, hh:mm aa";//"EEEE, MMM dd, hh:mm a"
    //AUDIO MESSAGE MEMBER VARIABLES
    private boolean isRecording = false;
    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private String file_exts[] = {AUDIO_RECORDER_FILE_EXT_MP4,
            AUDIO_RECORDER_FILE_EXT_3GP};

    private ChatActivity mCurrentActivity;
    public static boolean isChatActiviyRunning = false;
    public static boolean isFromNotification = false;
    private String mCoversationId;
    private String mReceiversId;

    private UserRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        mCurrentActivity = this;
        ButterKnife.bind(this);
        init();
        setListener();
        if (!Permissions.hasMicPermission(mCurrentActivity)) {
            Permissions.getMicPermission(mCurrentActivity);
        }
    }

    private void init() {
        messageList = new ArrayList<>();
        NormalCallData callData = AppPreferences.getCallData(mCurrentActivity);
        String details = callData.getDetails();
        if (StringUtils.isNotBlank(details)) {
            ChatMessage detailsMsg = new ChatMessage();
            detailsMsg.setMessageType("text");
            detailsMsg.setMessage("Details: " + details);
            detailsMsg.setTime(Utils.getFormattedDate(DATE_FORMAT, callData.getSentTime()));
            detailsMsg.setSenderId(callData.getPassId());
            messageList.add(detailsMsg);
        }
        chatAdapter = new ChatAdapter(mCurrentActivity, messageList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mCurrentActivity);
        linearLayoutManager.setStackFromEnd(true);
        messagesContainer.setLayoutManager(linearLayoutManager);
        messagesContainer.setAdapter(chatAdapter);

        repository = new UserRepository();
        if (AppPreferences.isOnTrip(mCurrentActivity)) {
            mReceiversId = AppPreferences.getCallData(mCurrentActivity).getPassId();
//            setToolbarTitle(AppPreferences.getCallData(mCurrentActivity).getPassName());
            titleTv.setText(AppPreferences.getCallData(mCurrentActivity).getPassName());
        }
        if (null != getIntent() && StringUtils.isNotBlank(getIntent().getStringExtra(Keys.CHAT_CONVERSATION_ID))) {
            mCoversationId = getIntent().getStringExtra(Keys.CHAT_CONVERSATION_ID);
            isFromNotification = getIntent().getBooleanExtra("fromNotification", false);
            if (!AppPreferences.isOnTrip(mCurrentActivity)) {
//                setToolbarTitle(getIntent().getStringExtra("title"));
                titleTv.setText(getIntent().getStringExtra("title"));
            }
//            Dialogs.INSTANCE.showLoader(mCurrentActivity);
            loader.setVisibility(View.VISIBLE);
            repository.getConversationChat(mCurrentActivity, chatHandler, mCoversationId);
        } else {
            isFromNotification = getIntent().getBooleanExtra("fromNotification", false);
//            Dialogs.INSTANCE.showLoader(mCurrentActivity);
//            setToolbarTitle(AppPreferences.getCallData(mCurrentActivity).getPassName());
            titleTv.setText(AppPreferences.getCallData(mCurrentActivity).getPassName());
            loader.setVisibility(View.VISIBLE);
            repository.getConversationId(mCurrentActivity, chatHandler,
                    AppPreferences.getCallData(mCurrentActivity).getPassId(), AppPreferences.getCallData(mCurrentActivity).getTripId());

        }
    }


    private IUserDataHandler chatHandler = new UserDataHandler() {

        @Override
        public void onGetConversationChat(final ConversationChatResponse response) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Dialogs.INSTANCE.dismissDialog();
                    loader.setVisibility(View.INVISIBLE);
                    if (response.isSuccess() &&
                            null != response.getData() && response.getData().size() > 0) {
                        messageList.clear();
                        messageList.addAll(response.getData());
                        chatAdapter.notifyDataSetChanged();
                        scrollDown();
                    } else {
                        Dialogs.INSTANCE.showError(mCurrentActivity, messageEdit, response.getMessage());
                    }
                }
            });

        }

        @Override
        public void onGetConversationId(final GetConversationIdResponse response) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Dialogs.INSTANCE.dismissDialog();
                    loader.setVisibility(View.INVISIBLE);
                    if (response.isSuccess() && StringUtils.isNotBlank(response.getConversationId())) {
                        loader.setVisibility(View.VISIBLE);
                        mCoversationId = response.getConversationId();
                        repository.getConversationChat(mCurrentActivity, chatHandler,
                                response.getConversationId());

                    }
                }
            });
        }

        @Override
        public void onSendMessage(final SendMessageResponse response) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!response.isSuccess()) {
                        Dialogs.INSTANCE.showError(mCurrentActivity, messageEdit, response.getMessage());
                    }
                }
            });

        }

        @Override
        public void onUpdateConversationStatus(UpdateConversationStatusResponse response) {
            if (null != response && response.isSuccess()) {

            }
        }


        @Override
        public void onUploadAudioFile(final UploadAudioFile uploadAudioFile) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Dialogs.INSTANCE.dismissDialog();
                    if (uploadAudioFile.isSuccess()) {
                        messageList.add(makeMsg(uploadAudioFile.getImagePath(), Keys.CHAT_TYPE_VOICE
                                , AppPreferences.getDriverId(mCurrentActivity),
                                AppPreferences.getPilotData(mCurrentActivity).getFullName(),
                                AppPreferences.getPilotData(mCurrentActivity).getPilotImage(), false));
                        chatAdapter.notifyDataSetChanged();
                        scrollDown();
                        repository.sendMessage(mCurrentActivity, chatHandler,
                                uploadAudioFile.getImagePath(), mCoversationId, mReceiversId, Keys.CHAT_TYPE_VOICE,
                                AppPreferences.getCallData(mCurrentActivity).getTripId());
                    } else {
                        Dialogs.INSTANCE.showError(mCurrentActivity, voiceMsgLayout, uploadAudioFile.getMessage());
                    }
                }
            });

        }

        @Override
        public void onError(final int errorCode, final String error) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Dialogs.INSTANCE.dismissDialog();
                    loader.setVisibility(View.INVISIBLE);
                    Dialogs.INSTANCE.showError(mCurrentActivity, voiceMsgLayout, error);

                }
            });
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        setChatRunning(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        setChatRunning(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppPreferences.setChatActivityOnForeground(mCurrentActivity, true);
        WebIORequestHandler.getInstance().setContext(mCurrentActivity);
        WebIORequestHandler.getInstance().registerChatListener();
        registerReceiver(messageReceiver, new IntentFilter(Keys.BROADCAST_MESSAGE_RECEIVE));
        Notifications.removeAllNotifications(mCurrentActivity);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppPreferences.setChatActivityOnForeground(mCurrentActivity, false);
        if (chatAdapter != null) {
            chatAdapter.stopPlayingAudio();
        }
    }

    @Override
    protected void onDestroy() {
        if (messageReceiver != null) {
            unregisterReceiver(messageReceiver);
        }
        AppPreferences.setChatActivityOnForeground(mCurrentActivity, false);
        if (chatAdapter != null) {
            chatAdapter.stopPlayingAudio();
        }
        ActivityStackManager.getInstance(mCurrentActivity).stopLocationService("ChatActivity");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (isFromNotification) {
            ActivityStackManager.getInstance(mCurrentActivity).startHomeActivity(false);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void setListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        messageEdit.addTextChangedListener(watcher);
        chatSendButton.setOnTouchListener(new View.OnTouchListener() {
                                              private long startTime;

                                              @Override
                                              public boolean onTouch(View v, MotionEvent event) {
                                                  if (Permissions.hasMicPermission(mCurrentActivity)) {
                                                      switch (event.getAction()) {
                                                          case MotionEvent.ACTION_DOWN:
                                                              startTime = System.currentTimeMillis();

                                                              break;
                                                          case MotionEvent.ACTION_UP:
                                                              if (messageEdit.getText().length() > 0) {
                                                                  sendMessage();
                                                                  break;
                                                              } else {
                                                                  voiceMsgLayout.setVisibility(View.GONE);
                                                                  messageEdit.setVisibility(View.VISIBLE);
                                                                  try {
                                                                      if (isRecording && event.getRawX() > 400) {
                                                                          stopRecording();
                                                                          repository.uploadAudioFile(mCurrentActivity,
                                                                                  chatHandler, new File(filePath));
                                                                      } else {
                                                                          stopRecording();
                                                                      }
                                                                  } catch (Exception e) {
                                                                      stopRecording();
                                                                      e.printStackTrace();
                                                                  }
                                                              }
                                                              break;
                                                          case MotionEvent.ACTION_MOVE:
                                                              if (!isRecording && System.currentTimeMillis() - 200 >= startTime
                                                                      && messageEdit.getText().toString().length() == 0) {
                                                                  Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                                                  vibrator.vibrate(100);
                                                                  voiceMsgLayout.setVisibility(View.VISIBLE);
                                                                  messageEdit.setVisibility(View.GONE);
                                                                  try {
                                                                      startRecording();
                                                                  } catch (Exception e) {
                                                                      e.printStackTrace();
                                                                  }
                                                              }
                                                              break;

                                                      }
                                                      return true;
                                                  } else {
                                                      Permissions.getMicPermission(mCurrentActivity);
                                                      return true;
                                                  }
                                              }
                                          }

        );
    }

    private void sendMessage() {
        String lastMsg = messageEdit.getText().toString();
        if (TextUtils.isEmpty(lastMsg)) {
            return;
        }
        messageEdit.setText("");
        repository.sendMessage(mCurrentActivity, chatHandler, lastMsg, mCoversationId,
                mReceiversId, Keys.CHAT_TYPE_TEXT, AppPreferences.getCallData(mCurrentActivity).getTripId());

        messageList.add(makeMsg(lastMsg, Keys.CHAT_TYPE_TEXT,
                AppPreferences.getDriverId(mCurrentActivity),
                AppPreferences.getPilotData(mCurrentActivity).getFullName(),
                AppPreferences.getPilotData(mCurrentActivity).getPilotImage(), false));
        chatAdapter.notifyDataSetChanged();
        scrollDown();
    }

    public static boolean isChatRunning() {
        return isChatActiviyRunning;
    }

    public void setChatRunning(boolean isRunning) {
        isChatActiviyRunning = isRunning;
    }

    private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }

        filePath = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat]);
        return filePath;
    }


    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
//            Toast.makeText(mCurrentActivity, "Error: " + what + ", " +
//                    extra, Toast.LENGTH_SHORT).show();
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
//            Toast.makeText(mCurrentActivity, "Warning: " + what + ", " +
//                    extra, Toast.LENGTH_SHORT).show();
        }
    };

    @SuppressLint("InlinedApi")
    private void startRecording() {
        chronometer.setBase(SystemClock.elapsedRealtime());

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(output_formats[currentFormat]);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(getFilename());
        recorder.setOnErrorListener(errorListener);
        recorder.setOnInfoListener(infoListener);

        try {
            recorder.prepare();
            recorder.start();
            chronometer.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isRecording = true;
    }

    private void stopRecording() {

        try {

            if (isRecording && null != recorder) {
                chronometer.stop();
                recorder.stop();
                recorder.release();
                recorder = null;
            } else {
                if (recorder != null) {
                    recorder.release();
                }
                recorder = null;
            }
            isRecording = false;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } finally {
            isRecording = false;
            if (recorder != null) {
                recorder.release();
            }
            recorder = null;
        }
    }

    private void scrollDown() {
        if (messageList.size() > 0)
            messagesContainer.scrollToPosition(messageList.size() - 1);
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent && null != intent.getSerializableExtra("msg")) {
                ReceivedMessage chatMessage = (ReceivedMessage) (intent.getSerializableExtra("msg"));
                messageList.add(makeMsg(chatMessage.getData().getMessage(), chatMessage.getData().getMessageType(),
                        chatMessage.getData().getSender(), "", "", true));
                chatAdapter.notifyDataSetChanged();
                scrollDown();
            }
        }
    };

    private ChatMessage makeMsg(String msg, String type, String senderid, String senderName,
                                String senderImage, boolean isReceived) {
        ChatMessage chatMessage = new ChatMessage();
        Sender sender = new Sender();
        if (isReceived) {
            sender.setImage(AppPreferences.getCallData(mCurrentActivity).getPassImage());
            sender.setSenderId(AppPreferences.getCallData(mCurrentActivity).getPassId());
            sender.setUsername(AppPreferences.getCallData(mCurrentActivity).getPassName());
            chatMessage.setSenderId(AppPreferences.getCallData(mCurrentActivity).getPassId());
        } else {
            sender.setImage(senderImage);
            sender.setSenderId(senderid);
            sender.setUsername(senderName);
            chatMessage.setSenderId(senderid);
        }

        chatMessage.setSender(sender);
        chatMessage.setMessage(msg);
        chatMessage.setMessageType(type);
        chatMessage.setTime(Utils.getFormattedDate(DATE_FORMAT));
        return chatMessage;
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 0) {
                showMic();
            } else {
                showSendText();
            }
        }

        private void showMic() {

            chatSendButton.setImageResource(R.drawable.chat_voice_btn_selector);
        }

        private void showSendText() {
            chatSendButton.setImageResource(R.drawable.chat_text_btn_selector);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @OnClick(R.id.backBtn)
    public void onClick() {
    }
}

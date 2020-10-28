package com.bykea.pk.partner.ui.helpers.adapters;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.ChatMessage;
import com.bykea.pk.partner.models.data.ChatMessagesTranslated;
import com.bykea.pk.partner.models.response.DownloadAudioFileResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.ChatActivityNew;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.StringCallBack;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.utils.audio.MediaPlayerHolder;
import com.bykea.pk.partner.utils.audio.PlaybackInfoListener;
import com.bykea.pk.partner.widgets.FontTextView;
import com.bykea.pk.partner.widgets.FontUtils;
import com.bykea.pk.partner.widgets.Fonts;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.bykea.pk.partner.utils.Constants.TRANSALATION_SEPERATOR;

public class ChatAdapterNewDF extends RecyclerView.Adapter<ChatAdapterNewDF.ViewHolder> {

    private final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";//2016-05-20T12:37:58.508Z
    private final List<ChatMessage> chatMessages;
    private Context context;
    private boolean isMsgPlaying;
    private ViewHolder prevViewHolder;
    private MediaPlayerHolder mPlayer; // Opus Players
    private String selectedUrl = StringUtils.EMPTY;
    private StringCallBack onStopCallBack;
    PlaybackInfoListener playbackHandler = new PlaybackInfoListener()
    {
        @Override
        public void onDurationChanged(int duration) {
            super.onDurationChanged(duration);

            prevViewHolder.audioLength.setText((duration)/Constants.DIGIT_THOUSAND + " sec");
            Utils.redLog("AUDIO FILE LENGTH: ", (duration / Constants.DIGIT_THOUSAND) + " / " +
                    (duration/ Constants.DIGIT_THOUSAND));
            prevViewHolder.seekBar.setMax(duration);
        }

        @Override
        public void onPlaybackCompleted() {
            super.onPlaybackCompleted();

            updateUIOnStop();
            mPlayer.release();
            if (onStopCallBack != null) {
                onStopCallBack.onCallBack("Successfully Stopped");
            }
            Utils.redLog("Playback event", "EVENT_COMPLETED");
        }

        @Override
        public void onStateChanged(int state) {
            super.onStateChanged(state);
        }

        @Override
        public void onPositionChanged(int position) {
            super.onPositionChanged(position);
            if (isMsgPlaying) {
                prevViewHolder.seekBar.setProgress(position);
            }
        }
    };

    @SuppressWarnings("deprecation")
    public ChatAdapterNewDF(Context context, List<ChatMessage> chatMessages, ArrayList<ChatMessagesTranslated> chatMessagesTranslatedArrayList) {
        this.context = context;
        this.chatMessages = chatMessages;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (viewType == 0) {
            View itemLayout = layoutInflater.inflate(R.layout.chat_message_in, parent, false);
            return new ViewHolder(itemLayout, viewType, context);
        } else if (viewType == 1) {
            View itemHeader = layoutInflater.inflate(R.layout.chat_message_out, parent, false);
            return new ViewHolder(itemHeader, viewType, context);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        viewHolder.txtDate.setVisibility(View.VISIBLE);
        viewHolder.txtDate.setText(Utils.getTimeDifference(chatMessages.get(position).getTime(), DATE_FORMAT));


        if (position != 0) {
            if (Utils.getTimeDifference(chatMessages.get(position).getTime(), DATE_FORMAT).equalsIgnoreCase(
                    Utils.getTimeDifference(chatMessages.get(position - 1).getTime(), DATE_FORMAT))) {

                viewHolder.txtDate.setVisibility(View.GONE);

                if (getItemViewType(position) == getItemViewType(position - 1)) {
                    viewHolder.imgPessanger.setVisibility(View.INVISIBLE);
                } else {
                    viewHolder.imgPessanger.setVisibility(View.VISIBLE);
                }

            } else {
                viewHolder.imgPessanger.setVisibility(View.VISIBLE);
                viewHolder.txtDate.setVisibility(View.VISIBLE);
            }
        }


        if (chatMessages.get(position).getMessageType().equalsIgnoreCase("text")) {
            if (getItemViewType(position) == 1) {
                viewHolder.contentLayout.setBackgroundResource(R.drawable.white_chat_box);
            } else {
                viewHolder.contentLayout.setBackgroundResource(R.drawable.green_chat_box);
            }

            viewHolder.txtMessage.setVisibility(View.GONE);
            viewHolder.viewSeperator.setVisibility(View.GONE);
            viewHolder.txtMessageSecond.setVisibility(View.VISIBLE);

            viewHolder.audioLayout.setVisibility(View.GONE);
            viewHolder.image.setVisibility(View.GONE);
            viewHolder.txtMessageVoice.setVisibility(View.GONE);

            if (chatMessages.get(position).getMessage().contains(TRANSALATION_SEPERATOR)) {
                try {
                    String[] strings = chatMessages.get(position).getMessage().split(TRANSALATION_SEPERATOR);
                    if (strings.length == 2 && strings[Constants.DIGIT_ZERO] != null && strings[Constants.DIGIT_ONE] != null &&
                            StringUtils.isNotEmpty(strings[Constants.DIGIT_ZERO]) && StringUtils.isNotEmpty(strings[Constants.DIGIT_ONE])) {
                        viewHolder.txtMessage.setText(new SpannableStringBuilder(StringUtils.SPACE)
                                .append(FontUtils.getStyledTitle(context, strings[Constants.DIGIT_ZERO], Fonts.Jameel_Noori_Nastaleeq.getName()))
                                .append(StringUtils.SPACE));
                        viewHolder.txtMessage.setVisibility(View.VISIBLE);
                        viewHolder.viewSeperator.setVisibility(View.VISIBLE);
                        viewHolder.txtMessageSecond.setVisibility(View.VISIBLE);
                        viewHolder.txtMessageSecond.setText(strings[Constants.DIGIT_ONE]);
                    } else if (strings.length <= 2 && StringUtils.isNotEmpty(strings[Constants.DIGIT_ZERO])) {
                        viewHolder.txtMessageSecond.setText(strings[Constants.DIGIT_ZERO]);
                    } else if (strings.length <= 2 && StringUtils.isNotEmpty(strings[Constants.DIGIT_ONE])) {
                        viewHolder.txtMessageSecond.setText(strings[Constants.DIGIT_ONE]);
                    } else {
                        viewHolder.txtMessageSecond.setText(chatMessages.get(position).getMessage());
                    }
                } catch (Exception e) {
                    viewHolder.txtMessage.setVisibility(View.GONE);
                    viewHolder.viewSeperator.setVisibility(View.GONE);
                    viewHolder.txtMessageSecond.setText(chatMessages.get(position).getMessage());
                }
            } else {
                viewHolder.txtMessage.setVisibility(View.GONE);
                viewHolder.viewSeperator.setVisibility(View.GONE);
                viewHolder.txtMessageSecond.setText(chatMessages.get(position).getMessage());
            }
        } else if (chatMessages.get(position).getMessageType().equalsIgnoreCase("Image")) {
            viewHolder.audioLayout.setVisibility(View.GONE);
            viewHolder.txtMessage.setVisibility(View.GONE);
            viewHolder.viewSeperator.setVisibility(View.GONE);
            viewHolder.txtMessageSecond.setVisibility(View.GONE);
            viewHolder.txtMessageVoice.setVisibility(View.GONE);
            viewHolder.image.setVisibility(View.VISIBLE);
            final String url = Utils.getFileLink(chatMessages.get(position)
                    .getMessage());
            Picasso.get().load(url).into(viewHolder.image);

            if (getItemViewType(position) == 1) {
                viewHolder.contentLayout.setBackgroundResource(R.drawable.white_chat_box);
            } else {
                viewHolder.contentLayout.setBackgroundResource(R.drawable.green_chat_box);
            }

        } else {
            viewHolder.image.setVisibility(View.GONE);
            viewHolder.contentLayout.setBackgroundResource(R.color.transparent);
            viewHolder.audioLayout.setVisibility(View.VISIBLE);
            viewHolder.txtMessage.setVisibility(View.GONE);
            viewHolder.viewSeperator.setVisibility(View.GONE);
            viewHolder.txtMessageSecond.setVisibility(View.GONE);
            viewHolder.txtMessageVoice.setVisibility(View.VISIBLE);

            viewHolder.seekBar.setClickable(false);


            viewHolder.txtMessageVoice.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (chatMessages.get(position).getMessageType().equalsIgnoreCase(Keys.CHAT_TYPE_VOICE)) {
                        if (!isMsgPlaying) {
                            startPlayingAudio();
                        } else {
                            stopPlayingAudio(new StringCallBack() {
                                @Override
                                public void onCallBack(String msg) {
                                    if (prevViewHolder != viewHolder) {
                                        startPlayingAudio();
                                    }
                                }
                            });

                        }
                    }

                }

                private synchronized void startPlayingAudio() {

                    isMsgPlaying = true;
                    prevViewHolder = viewHolder;
                    prevViewHolder.loader.setVisibility(View.VISIBLE);
                    prevViewHolder.txtMessageVoice.setVisibility(View.INVISIBLE);
                    setPlayIcon(prevViewHolder.txtMessageVoice, true);
                    final String url = Utils.getFileLink(chatMessages.get(position)
                            .getMessage());
                    mPlayer = new MediaPlayerHolder(context);
//                    mPlayer.setEventSender(new OpusEvent(context));
//                    ((ChatActivityNew) context).setCallBack(playbackHandler);
                    selectedUrl = url;
                    new UserRepository().downloadAudioFile(context, url, new UserDataHandler() {
                        @Override
                        public void onDownloadAudio(DownloadAudioFileResponse response) {
                            if (selectedUrl.equalsIgnoreCase(response.getLink())
                                    && ((ChatActivityNew) context).isInFront()) {
                                onStateReady();
                                mPlayer.loadUri(response.getPath());
                                mPlayer.play();

                            }
                        }
                    });
                }


                private synchronized void onPlayerError() {
                    stopPlayingAudio();
                }

                private synchronized void onStateReady() {
                    setPlayIcon(prevViewHolder.txtMessageVoice, true);
                    mPlayer.setPlaybackInfoListener(playbackHandler);
                    prevViewHolder.loader.setVisibility(View.GONE);
                    prevViewHolder.txtMessageVoice.setVisibility(View.VISIBLE);
                    prevViewHolder.audioLength.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void updateUIOnStop() {
        prevViewHolder.seekBar.setProgress(0);
        isMsgPlaying = false;
        prevViewHolder.loader.setVisibility(View.GONE);
        prevViewHolder.audioLength.setVisibility(View.INVISIBLE);
        prevViewHolder.txtMessageVoice.setVisibility(View.VISIBLE);
        setPlayIcon(prevViewHolder.txtMessageVoice, false);
    }


    public synchronized void stopPlayingAudio() {
        if (mPlayer != null) {
            onStopCallBack = null;
            mPlayer.pause();
        }
    }

    public synchronized void stopPlayingAudio(StringCallBack callBack) {
        if (mPlayer != null) {
            mPlayer.pause();
            onStopCallBack = callBack;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).getSenderId()
                .equalsIgnoreCase(AppPreferences.getDriverId())) {
            return 1;
        } else {
            return 0;
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (chatMessages != null) {
            return chatMessages.size();
        } else {
            return 0;
        }
    }

    private void setPlayIcon(TextView view, boolean isPlaying) {
        if (isPlaying) {
            view.setBackgroundResource(R.drawable.pause_bar);
        } else {
            view.setBackgroundResource(R.drawable.play_icon);
        }
    }

    public void add(ChatMessage message) {
        chatMessages.add(message);
    }

    public void add(List<ChatMessage> messages) {
        chatMessages.addAll(messages);

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public FontTextView txtMessage;
        public TextView txtMessageVoice;
        public ProgressBar loader;
        public SeekBar seekBar;
        public FontTextView audioLength;
        public TextView txtDate;
        public ImageView imgPessanger;
        public LinearLayout audioLayout;
        public FrameLayout contentLayout;

        public View viewSeperator;
        public FontTextView txtMessageSecond;

        ImageView image;
        Context context;


        public ViewHolder(View itemView, int itemType, Context context) {
            super(itemView);
            this.context = context;
            image = itemView.findViewById(R.id.image);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtMessageVoice = itemView.findViewById(R.id.txtMessageVoice);
            loader = itemView.findViewById(R.id.loader);
            contentLayout = itemView.findViewById(R.id.content);
            audioLayout = itemView.findViewById(R.id.audioLayout);
            seekBar = itemView.findViewById(R.id.seekbar);
            audioLength = itemView.findViewById(R.id.audioLength);
            imgPessanger = itemView.findViewById(R.id.imgPessenger);
            viewSeperator = itemView.findViewById(R.id.viewSeperator);
            txtMessageSecond = itemView.findViewById(R.id.txtMessageSecond);
        }
    }
}

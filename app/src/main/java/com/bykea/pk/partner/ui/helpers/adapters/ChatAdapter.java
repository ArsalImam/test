package com.bykea.pk.partner.ui.helpers.adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.ChatMessage;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;
import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.Util;

import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";//2016-05-20T12:37:58.508Z
    private static final String REQUIRED_FORMAT = "EEEE, MMM dd, hh:mm aa";
    private final List<ChatMessage> chatMessages;
    private Context context;
    private String image;
    private boolean isMsgPlaying;
    private ViewHolder prevViewHolder;
    private Handler prevHandler;
    private Runnable prevUpdateSongTime;
    //    private MediaPlayer mPlayer;
    private ExoPlayer mPlayer;
    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;

    public static final int TYPE_OUT = 0;
    public static final int TYPE_IN = 1;

    @SuppressWarnings("deprecation")
    public ChatAdapter(Context context, List<ChatMessage> chatMessages) {
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


        if (position != 0
                && chatMessages.get(position).getTime().equalsIgnoreCase(
                chatMessages.get(position - 1).getTime())) {

            viewHolder.txtDate.setVisibility(View.GONE);

        }

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
                /*if (StringUtils.isNotBlank(chatMessages.get(position).getSender().getImage())) {
                    Picasso.get().load(Utils.getImageLink(chatMessages.get(position).getSender().getImage()))
                            .placeholder(R.drawable.profile_pic).into(viewHolder.imgPessanger);
                }*/
                viewHolder.contentLayout.setBackgroundResource(R.drawable.white_chat_box);
            } else {
               /* viewHolder.imgPessanger.setImageDrawable(ContextCompat.getDrawable(context,
                        R.drawable.chat_smiley_icon));*/

                viewHolder.contentLayout.setBackgroundResource(R.drawable.green_chat_box);
            }
            viewHolder.audioLayout.setVisibility(View.GONE);
            viewHolder.txtMessage.setText(chatMessages.get(position).getMessage());
            viewHolder.txtMessage.setVisibility(View.VISIBLE);
            viewHolder.txtMessageVoice.setVisibility(View.GONE);
        } else {
            /*if (getItemViewType(position) == 1) {
                if (StringUtils.isNotBlank(chatMessages.get(position).getSender().getImage())) {
                    Picasso.get().load(Utils.getImageLink(chatMessages.get(position).getSender().getImage()))
                            .placeholder(R.drawable.profile_pic).into(viewHolder.imgPessanger);
                }
            } else {
                viewHolder.imgPessanger.setImageDrawable(ContextCompat.getDrawable(context,
                        R.drawable.chat_smiley_icon));
            }*/
            viewHolder.contentLayout.setBackgroundResource(R.color.transparent);
            viewHolder.audioLayout.setVisibility(View.VISIBLE);
            viewHolder.txtMessage.setVisibility(View.GONE);
            viewHolder.txtMessageVoice.setVisibility(View.VISIBLE);

            viewHolder.seekBar.setClickable(false);


            viewHolder.txtMessageVoice.setOnClickListener(new View.OnClickListener() {
                private Handler mHandler = new Handler();
                final Runnable updateSongTime = new Runnable() {
                    public void run() {
                        if (isMsgPlaying) {
                            viewHolder.seekBar.setProgress((int) mPlayer.getCurrentPosition());
                            mHandler.postDelayed(this, 100);
                        }
                    }
                };

                @Override
                public void onClick(View v) {
//                        onClickPlayItem(v, viewHolder.seekBar, viewHolder.audioLength, position);
                    if (chatMessages.get(position).getMessageType().equalsIgnoreCase(Keys.CHAT_TYPE_VOICE)) {
                        if (!isMsgPlaying) {
                            startPlayingAudioWithExoPlayer();
                        } else {
                            if (mPlayer != null && prevViewHolder != null) {
                                prevHandler.removeCallbacks(prevUpdateSongTime);
                                prevViewHolder.seekBar.setProgress(0);
                                mPlayer.stop();
                                mPlayer.release();
                                mPlayer = null;
                                isMsgPlaying = false;
                                prevViewHolder.loader.setVisibility(View.GONE);
                                prevViewHolder.txtMessageVoice.setVisibility(View.VISIBLE);
                                setPlayIcon(prevViewHolder.txtMessageVoice, false, true);
                                if (prevViewHolder != viewHolder) {
                                    startPlayingAudioWithExoPlayer();
                                }
                            }
                        }

                    }

                }

                private void startPlayingAudioWithExoPlayer() {

                    isMsgPlaying = true;
                    prevViewHolder = viewHolder;
                    prevHandler = mHandler;
                    prevUpdateSongTime = updateSongTime;
                    prevViewHolder.loader.setVisibility(View.VISIBLE);
                    prevViewHolder.txtMessageVoice.setVisibility(View.INVISIBLE);
                    setPlayIcon(viewHolder.txtMessageVoice, true, true);
                    mPlayer = ExoPlayer.Factory.newInstance(1);
                    HttpsURLConnection.setDefaultSSLSocketFactory(Utils.getSSLContext(context).getSocketFactory());
                    String url = Utils.getFileLink(chatMessages.get(position)
                            .getMessage());
                    Uri radioUri = Uri.parse(url);
// Settings for exoPlayer
                    Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
                    String userAgent = Util.getUserAgent(context, "ExoPlayerDemo");
                    DataSource dataSource = new DefaultUriDataSource(context, null, userAgent);
                    ExtractorSampleSource sampleSource = new ExtractorSampleSource(
                            radioUri, dataSource, allocator, BUFFER_SEGMENT_SIZE * BUFFER_SEGMENT_COUNT);
                    MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource);
// Prepare ExoPlayer
                    mPlayer.prepare(audioRenderer);
                    mPlayer.setPlayWhenReady(true);
                    mPlayer.addListener(new ExoPlayer.Listener() {
                        @Override
                        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                            if (playbackState == ExoPlayer.STATE_READY) {
                                mHandler.postDelayed(updateSongTime, 100);
                                setPlayIcon(viewHolder.txtMessageVoice, true, true);
                                Utils.redLog("AUDIO FILE LENGTH: ", (mPlayer.getDuration() / 1000) + " / " +
                                        (mPlayer.getDuration() / 1000));
                                prevViewHolder.loader.setVisibility(View.GONE);
                                prevViewHolder.txtMessageVoice.setVisibility(View.VISIBLE);
                                viewHolder.audioLength.setVisibility(View.VISIBLE);
                                viewHolder.audioLength.setText((mPlayer.getDuration() / 1000) + " sec");
                                viewHolder.seekBar.setMax((int) mPlayer.getDuration());
                            } else if (playbackState == ExoPlayer.STATE_ENDED) {
                                if (mPlayer != null) {
                                    viewHolder.seekBar.setProgress(0);
                                    mPlayer.stop();
                                    mPlayer.release();
                                    mPlayer = null;
                                    isMsgPlaying = false;
                                    prevViewHolder.loader.setVisibility(View.GONE);
                                    prevViewHolder.txtMessageVoice.setVisibility(View.VISIBLE);
                                    setPlayIcon(viewHolder.txtMessageVoice, false, true);
                                }
                            }

                        }

                        @Override
                        public void onPlayWhenReadyCommitted() {
                        }

                        @Override
                        public void onPlayerError(ExoPlaybackException error) {
                            if (mPlayer != null) {
                                viewHolder.seekBar.setProgress(0);
                                mPlayer.stop();
                                mPlayer.release();
                                mPlayer = null;
                                isMsgPlaying = false;
                                prevViewHolder.loader.setVisibility(View.GONE);
                                prevViewHolder.txtMessageVoice.setVisibility(View.VISIBLE);
                                setPlayIcon(viewHolder.txtMessageVoice, false, true);
                            }
                        }
                    });
                }

            });


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

    public void setPlayIcon(TextView view, boolean isPlaying, boolean isGreen) {
        if (isGreen) {
            if (isPlaying) {
                view.setBackgroundResource(R.drawable.pause_bar);
            } else {
                view.setBackgroundResource(R.drawable.play_icon);
            }
        } else {
            if (isPlaying) {
                view.setBackgroundResource(R.drawable.pause_bar);
            } else {
                view.setBackgroundResource(R.drawable.play_icon);
            }
        }
    }

    public void add(ChatMessage message) {
        chatMessages.add(message);
    }

    public void add(List<ChatMessage> messages) {
        chatMessages.addAll(messages);

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtMessage;
        public TextView txtMessageVoice;
        public ProgressBar loader;
        public SeekBar seekBar;
        public FontTextView audioLength;
        public TextView txtDate;
        public ImageView imgPessanger;
        public LinearLayout audioLayout;
        public FrameLayout contentLayout;
        Context context;


        public ViewHolder(View itemView, int itemType, Context context) {
            super(itemView);
            this.context = context;

            txtDate = (TextView) itemView.findViewById(R.id.txtDate);
            txtMessage = (TextView) itemView.findViewById(R.id.txtMessage);
            txtMessageVoice = (TextView) itemView.findViewById(R.id.txtMessageVoice);
            loader = (ProgressBar) itemView.findViewById(R.id.loader);
            contentLayout = (FrameLayout) itemView.findViewById(R.id.content);
            audioLayout = (LinearLayout) itemView.findViewById(R.id.audioLayout);
            seekBar = (SeekBar) itemView.findViewById(R.id.seekbar);
            audioLength = (FontTextView) itemView.findViewById(R.id.audioLength);
            imgPessanger = (ImageView) itemView.findViewById(R.id.imgPessenger);
        }
    }

    public void setListener(OnItemClickListener listener) {
        OnItemClickListener listener1 = listener;
    }

    public interface OnItemClickListener {

        void onClickItem(View view, int position);

    }


    public void stopPlayingAudio() {
        if (mPlayer != null && prevHandler != null && prevUpdateSongTime != null) {
            prevHandler.removeCallbacks(prevUpdateSongTime);
            prevViewHolder.seekBar.setProgress(0);
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            isMsgPlaying = false;
            prevViewHolder.loader.setVisibility(View.GONE);
            prevViewHolder.txtMessageVoice.setVisibility(View.VISIBLE);
            setPlayIcon(prevViewHolder.txtMessageVoice, false, true);
        }
    }

}
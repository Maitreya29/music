package com.hardcodecoder.pulsemusic.playback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.PulseController;
import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.io.IOException;

public class LocalPlayback implements
        Playback,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        AudioManager.OnAudioFocusChangeListener {

    private final IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private final Context mContext;
    private final AudioManager mAudioManager;
    private final PulseController.QueueManager mQueueManager;
    private Playback.Callback mPlaybackCallback;
    private final BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                mPlaybackCallback.onFocusChanged(false);
            }
        }
    };
    private MediaPlayer mp;
    private TelephonyManager mTelephonyManager;
    private AudioFocusRequest mAudioFocusRequest = null;
    private PhoneStateListener mPhoneStateListener;
    private Handler mHandler;
    private int mPlaybackState = PlaybackState.STATE_NONE;
    private int mCurrentState;
    private int mMediaId = -99;
    private int mResumePosition = -1;
    private boolean isBecomingNoisyReceiverRegistered = false;
    private boolean mDelayedPlayback = false;
    private boolean mStartPlaybackWhenReady = true;

    public LocalPlayback(@NonNull Context context, Handler handler) {
        Context applicationContext = context.getApplicationContext();
        mContext = applicationContext;
        mAudioManager = (AudioManager) applicationContext.getSystemService(Context.AUDIO_SERVICE);
        mHandler = handler;
        if (mHandler == null) mHandler = new Handler();
        mQueueManager = PulseController.getInstance().getQueueManager();
    }

    @Override
    public void setCallback(Callback callback) {
        mPlaybackCallback = callback;
    }

    private void initMediaPlayer(@NonNull MusicModel md) {
        mp = new MediaPlayer();
        mp.setOnPreparedListener(this);
        mp.setOnCompletionListener(this);
        mp.reset();
        try {
            mp.setDataSource(mContext, Uri.parse(md.getTrackPath()));
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            //Stop playback if data source failed
            Toast.makeText(mContext, "Music file not found, playing next song in queue", Toast.LENGTH_LONG).show();
            mPlaybackCallback.onPlaybackCompletion();
        }
    }

    @Override
    public void onPlay(int startPosition, boolean startPlaying) {
        final MusicModel trackItem = mQueueManager.getActiveQueueItem();
        if (tryGetAudioFocus()) {
            mDelayedPlayback = false;
            if (trackItem.getId() == mMediaId) {
                // Track item has not changed
                if (mPlaybackState == PlaybackState.STATE_PAUSED) {
                    // We were previously paused
                    // resume playback
                    if (null == mp) {
                        // if media player becomes null when trying to resume playback
                        // initialize a new media player object
                        initMediaPlayer(trackItem);
                    } else {
                        // Start playback with the resume position
                        play(mResumePosition);
                    }
                } else if (mQueueManager.isCurrentTrackInRepeatMode()) {
                    // We are in repeat mode (infinite lop), release media player
                    // and let it re-init with same track
                    releaseMediaPlayer();
                    mPlaybackCallback.onTrackConfigured(trackItem);
                    initMediaPlayer(trackItem);
                }
            } else {
                // Track item changed, release old Media player
                releaseMediaPlayer();
                // We need playback to start from the given start Position
                mResumePosition = startPosition;
                // Whether we should start playback when resource is ready
                mStartPlaybackWhenReady = startPlaying;
                // Initialize new Media player
                initMediaPlayer(trackItem);
                // Notify Playback Manager of track change
                mPlaybackCallback.onTrackConfigured(trackItem);
                // Update current Media id
                mMediaId = trackItem.getId();
            }
            // Register listeners
            registerBecomingNoisyReceiver();
            callStateListener();
        } else if (mDelayedPlayback) mMediaId = trackItem.getId();
    }

    private void play(int startPosition) {
        if (mStartPlaybackWhenReady) {
            if (startPosition > 0 && startPosition < mp.getDuration()) mp.seekTo(startPosition);
            mp.start();
            mPlaybackState = PlaybackState.STATE_PLAYING;
        } else {
            // We only skip the playback once when resource is ready
            // Further call to play should actually start playing
            mPlaybackState = PlaybackState.STATE_PAUSED;
            mStartPlaybackWhenReady = true;
        }
        mPlaybackCallback.onPlaybackStateChanged(mPlaybackState);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        play(mResumePosition);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        onStop(false);
        mPlaybackCallback.onPlaybackCompletion();
    }

    @Override
    public void onPause() {
        if (mp != null) {
            mp.pause();
            mResumePosition = mp.getCurrentPosition() - 250; // Offset playback position by 250
        }
        mPlaybackState = PlaybackState.STATE_PAUSED;
        mPlaybackCallback.onPlaybackStateChanged(mPlaybackState);
    }

    @Override
    public void onSeekTo(int position) {
        if (mp != null) {
            mResumePosition = position;
            mp.seekTo(mResumePosition);
            mPlaybackCallback.onPlaybackStateChanged(mPlaybackState);
        }
    }

    @Override
    public void onStop(boolean abandonAudioFocus) {
        if (abandonAudioFocus) abandonAudioFocus();

        releaseMediaPlayer();
        mMediaId = -999;

        if (isBecomingNoisyReceiverRegistered) {
            mContext.unregisterReceiver(becomingNoisyReceiver);
            isBecomingNoisyReceiverRegistered = false;
        }
        if (mPhoneStateListener != null) {
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        if (abandonAudioFocus) {
            mPlaybackState = PlaybackState.STATE_STOPPED;
            mPlaybackCallback.onPlaybackStateChanged(mPlaybackState);
        }
    }

    private void releaseMediaPlayer() {
        mResumePosition = -1;
        if (mp != null) {
            mp.stop();
            mp.reset();
            mp.release();
            mp = null;
        }
    }

    @Override
    public int getActiveMediaId() {
        return mMediaId;
    }

    @Override
    public long getCurrentStreamingPosition() {
        if (null == mp) return 0;
        return mp.getCurrentPosition();
    }

    private boolean tryGetAudioFocus() {
        int r;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (null == mAudioFocusRequest) {
                AudioAttributes mPlaybackAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();
                mAudioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                        .setAudioAttributes(mPlaybackAttributes)
                        .setAcceptsDelayedFocusGain(true)
                        .setOnAudioFocusChangeListener(LocalPlayback.this, mHandler)
                        .build();
            }
            r = mAudioManager.requestAudioFocus(mAudioFocusRequest);
            if (r == AudioManager.AUDIOFOCUS_REQUEST_DELAYED) mDelayedPlayback = true;
        } else {
            r = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
        return r == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    private void abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mAudioFocusRequest != null)
            mAudioManager.abandonAudioFocusRequest(mAudioFocusRequest);
        else {
            mAudioManager.abandonAudioFocus(this);
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                mCurrentState = AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                mCurrentState = AudioManager.AUDIOFOCUS_LOSS;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                mCurrentState = AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                mCurrentState = AudioManager.AUDIOFOCUS_GAIN;
                break;
        }
        if (mp != null) configurePlayerState();
        else if (mDelayedPlayback) mPlaybackCallback.onFocusChanged(true);
    }

    private void configurePlayerState() {
        if (mCurrentState == AudioManager.AUDIOFOCUS_LOSS || mCurrentState == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            mPlaybackCallback.onFocusChanged(false);
        } else if (mCurrentState == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK)
            mp.setVolume(0.2f, 0.2f);
        else if (mCurrentState == AudioManager.AUDIOFOCUS_GAIN) {
            mp.setVolume(1.0f, 1.0f);
            mPlaybackCallback.onFocusChanged(true);
        }
    }

    private void registerBecomingNoisyReceiver() {
        //Register after getting audio focus
        if (!isBecomingNoisyReceiverRegistered) {
            mContext.registerReceiver(becomingNoisyReceiver, filter);
            isBecomingNoisyReceiverRegistered = true;
        }
    }

    private void callStateListener() {
        mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        //Starting listening for PhoneState changes
        mPhoneStateListener = new PhoneStateListener() {
            boolean wasRinging = false;

            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    //if at least one call exists or the phone is ringing
                    //pause the MediaPlayer
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        wasRinging = true;
                        mPlaybackCallback.onFocusChanged(false);
                        break;

                    case TelephonyManager.CALL_STATE_IDLE:
                        // this should be the last piece of code before the break
                        if (wasRinging) mPlaybackCallback.onFocusChanged(true);
                        wasRinging = false;
                        break;
                }
            }
        };
        // Register the listener with the telephony manager
        // Listen for changes to the device call state.
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }
}
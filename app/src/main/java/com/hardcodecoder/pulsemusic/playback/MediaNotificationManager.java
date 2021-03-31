package com.hardcodecoder.pulsemusic.playback;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.main.MainContentActivity;

public class MediaNotificationManager {

    public static final String TAG = MediaNotificationManager.class.getSimpleName();
    private static final String ACTION_PAUSE = "com.hardcodeCoder.playback.pause";
    private static final String ACTION_PLAY = "com.hardcodeCoder.playback.play";
    private static final String ACTION_PREV = "com.hardcodeCoder.playback.prev";
    private static final String ACTION_NEXT = "com.hardcodeCoder.playback.next";
    private static final String ACTION_STOP = "com.hardcodeCoder.playback.delete";
    private static final String CHANNEL_ID = "com.hardcodecoder.pulsemusic.MUSIC_CHANNEL_ID";
    private static final int NOTIFICATION_ID = 412;
    private static final int REQUEST_CODE = 100;
    private final NotificationManager mNotificationManager;
    private final Context mContext;
    private final NotificationCallback mCallback;
    private final MediaController mController;
    private final MediaController.TransportControls mTransportControls;
    private final BroadcastReceiver controlsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(@NonNull Context context, @NonNull Intent intent) {
            final String action = intent.getAction();
            if (action != null && mTransportControls != null) {
                switch (action) {
                    case ACTION_PLAY:
                        mTransportControls.play();
                        break;
                    case ACTION_PAUSE:
                        mTransportControls.pause();
                        break;
                    case ACTION_STOP:
                        mTransportControls.stop();
                        break;
                    case ACTION_NEXT:
                        mTransportControls.skipToNext();
                        break;
                    case ACTION_PREV:
                        mTransportControls.skipToPrevious();
                        break;
                }
            }
        }
    };
    private final MediaController.Callback mCb = new MediaController.Callback() {
        @Override
        public void onPlaybackStateChanged(@Nullable PlaybackState state) {
            if (null != state) {
                int mState = state.getState();
                if (mPlaybackState.getState() != mState && mState != PlaybackState.STATE_STOPPED) {
                    mPlaybackState = state;
                    updateNotification();
                }
                mPlaybackState = state;
            }
        }

        @Override
        public void onMetadataChanged(@Nullable MediaMetadata metadata) {
            if (null != metadata) {
                mMetadata = metadata;
                updateNotification();
            }
        }
    };
    private PendingIntent mPlayIntent;
    private PendingIntent mPauseIntent;
    private PendingIntent mPreviousIntent;
    private PendingIntent mNextIntent;
    private PendingIntent mStopIntent;
    private PendingIntent mOpenNowPlaying;
    private boolean mStarted;
    private boolean mInitialised = false;
    private PlaybackState mPlaybackState;
    private MediaMetadata mMetadata;

    public MediaNotificationManager(@NonNull Context context, @NonNull MediaController controller, @NonNull NotificationCallback notificationCallback) {
        mContext = context;
        mController = controller;
        mTransportControls = controller.getTransportControls();
        mCallback = notificationCallback;
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (null != mNotificationManager) mNotificationManager.cancelAll();
    }

    private void updateNotification() {
        Notification notification = createNotification();
        if (mPlaybackState.getState() == PlaybackState.STATE_PLAYING) {
            mCallback.onNotificationStarted(NOTIFICATION_ID, notification);
        } else if (mPlaybackState.getState() == PlaybackState.STATE_PAUSED) {
            mCallback.onNotificationStopped(false);
        }
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void initialisePendingIntents() {
        String pkg = mContext.getPackageName();
        mPlayIntent = PendingIntent.getBroadcast(mContext, REQUEST_CODE, new Intent(ACTION_PLAY).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mPauseIntent = PendingIntent.getBroadcast(mContext, REQUEST_CODE, new Intent(ACTION_PAUSE).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mStopIntent = PendingIntent.getBroadcast(mContext, REQUEST_CODE, new Intent(ACTION_STOP).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mNextIntent = PendingIntent.getBroadcast(mContext, REQUEST_CODE, new Intent(ACTION_NEXT).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mPreviousIntent = PendingIntent.getBroadcast(mContext, REQUEST_CODE, new Intent(ACTION_PREV).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        if (mNotificationManager != null) mNotificationManager.cancelAll();
        Intent intent = new Intent(mContext, MainContentActivity.class);
        intent.setAction(MainContentActivity.ACTION_OPEN_NOW_PLAYING);
        mOpenNowPlaying = PendingIntent.getActivity(mContext, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mInitialised = true;
    }

    public void startNotification() {
        if (!mInitialised) {
            initialisePendingIntents();
        }
        if (!mStarted) {
            mMetadata = mController.getMetadata();
            mPlaybackState = mController.getPlaybackState();
            // The notification must be updated after setting started to true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                createNotificationChannel();
            Notification notification = createNotification();
            if (notification != null) {
                mController.registerCallback(mCb);
                IntentFilter filter = new IntentFilter();
                filter.addAction(ACTION_NEXT);
                filter.addAction(ACTION_PAUSE);
                filter.addAction(ACTION_PLAY);
                filter.addAction(ACTION_PREV);
                filter.addAction(ACTION_STOP);
                mCallback.registerControlsReceiver(controlsReceiver, filter);
                mCallback.onNotificationStarted(NOTIFICATION_ID, notification);
                mStarted = true;
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        if (mNotificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    mContext.getString(R.string.notification_playback_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("no sound");
            notificationChannel.enableVibration(false);
            notificationChannel.setSound(null, null);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @Nullable
    private Notification createNotification() {
        if (mMetadata != null) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID);
            notificationBuilder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(MediaSessionCompat.Token.fromToken(mCallback.getMediaSessionToken()))
                    .setCancelButtonIntent(mStopIntent)
                    .setShowCancelButton(true)
                    .setShowActionsInCompactView(0, 1, 2))
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(mMetadata.getString(MediaMetadata.METADATA_KEY_TITLE))
                    .setContentText(mMetadata.getString(MediaMetadata.METADATA_KEY_ARTIST))
                    .setLargeIcon(mMetadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART))
                    .addAction(R.drawable.ic_round_skip_previous_noti, "Skip prev", mPreviousIntent)
                    .addAction(getPlayOrPauseButton(), "Play Pause Button", getPlayOrPauseAction())
                    .addAction(R.drawable.ic_round_skip_next_noti, "Skip Next", mNextIntent)
                    .addAction(R.drawable.ic_close_noti, "Stop Self", mStopIntent)
                    .setContentIntent(mOpenNowPlaying)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(false)
                    .setDeleteIntent(mStopIntent)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            return notificationBuilder.build();
        }
        return null;
    }

    @DrawableRes
    private int getPlayOrPauseButton() {
        return mPlaybackState.getState() == PlaybackState.STATE_PLAYING ?
                R.drawable.ic_round_pause_noti : R.drawable.ic_round_play_noti;
    }

    @NonNull
    private PendingIntent getPlayOrPauseAction() {
        return mPlaybackState.getState() == PlaybackState.STATE_PLAYING ?
                mPauseIntent : mPlayIntent;
    }

    public void stopNotification() {
        if (mStarted) {
            mStarted = false;
            mController.unregisterCallback(mCb);
            try {
                mNotificationManager.cancel(NOTIFICATION_ID);
                mCallback.unregisterControlsReceiver(controlsReceiver);
            } catch (IllegalArgumentException ex) {
                // ignore if the receiver is not registered.
            }
            mCallback.onNotificationStopped(true);
        }
    }

    public void unregisterControlsReceiver() {
        mCallback.unregisterControlsReceiver(controlsReceiver);
    }

    public interface NotificationCallback {
        MediaSession.Token getMediaSessionToken();

        void onNotificationStarted(int notificationId, Notification notification);

        void onNotificationStopped(boolean removeNotification);

        void registerControlsReceiver(@NonNull BroadcastReceiver controlsReceiver, @NonNull IntentFilter filter);

        void unregisterControlsReceiver(@NonNull BroadcastReceiver controlsReceiver);
    }
}
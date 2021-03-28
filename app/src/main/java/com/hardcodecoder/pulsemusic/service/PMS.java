package com.hardcodecoder.pulsemusic.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.session.MediaButtonReceiver;

import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.PulseController;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.loaders.LoaderManager;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.playback.LocalPlayback;
import com.hardcodecoder.pulsemusic.playback.MediaNotificationManager;
import com.hardcodecoder.pulsemusic.playback.PlaybackManager;
import com.hardcodecoder.pulsemusic.providers.ProviderManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PMS extends Service implements PlaybackManager.PlaybackServiceCallback,
        MediaNotificationManager.NotificationCallback,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String ACTION_DEFAULT_PLAY = "com.hardcodecoder.pulsemusic.service.PMS.ACTION_DEFAULT_PLAY";
    public static final String ACTION_PLAY_CONTINUE = "com.hardcodecoder.pulsemusic.service.PMS.ACTION_PLAY_CONTINUE";
    public static final String KEY_DEFAULT_PLAY = "com.hardcodecoder.pulsemusic.service.PMS.KEY_DEFAULT_PLAY";
    public static final String KEY_PLAY_CONTINUE = "com.hardcodecoder.pulsemusic.service.PMS.KEY_BLUETOOTH_AUTO_PLAY";
    public static final int DEFAULT_ACTION_PLAY_NONE = -1;
    public static final int DEFAULT_ACTION_PLAY_SHUFFLE = Preferences.ACTION_PLAY_SHUFFLE;
    public static final int DEFAULT_ACTION_PLAY_LATEST = Preferences.ACTION_PLAY_LATEST;
    public static final int DEFAULT_ACTION_PLAY_SUGGESTED = Preferences.ACTION_PLAY_SUGGESTED;
    public static final int DEFAULT_ACTION_CONTINUE_PLAYLIST = Preferences.ACTION_PLAY_CONTINUE;
    private static final String TAG = "PMS";
    private final IBinder mBinder = new ServiceBinder();
    private MediaSession mMediaSession = null;
    private MediaNotificationManager mNotificationManager = null;
    private HandlerThread mServiceThread = null;
    private Handler mWorkerHandler = null;
    private PlaybackManager mPlaybackManager;
    private PulseController mPulseController;
    private boolean isReceiverRegistered = false;
    private boolean mCanServiceStopSelf = true;

    @Override
    public void onCreate() {
        ProviderManager.init(this);
        mServiceThread = new HandlerThread("PMS Thread", Thread.NORM_PRIORITY);
        mServiceThread.start();
        mWorkerHandler = new Handler(mServiceThread.getLooper());

        LocalPlayback playback = new LocalPlayback(this, mWorkerHandler);
        mPlaybackManager = new PlaybackManager(getApplicationContext(), playback, this, mWorkerHandler);

        mMediaSession = new MediaSession(getApplicationContext(), TAG);
        mMediaSession.setCallback(mPlaybackManager.getSessionCallbacks(), mWorkerHandler);

        mWorkerHandler.post(this::initializeAsync);
    }

    private void initializeAsync() {
        mPulseController = PulseController.getInstance();
        mPulseController.setController(mMediaSession.getController());

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(getApplicationContext(), MediaButtonReceiver.class);
        PendingIntent mbrIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
        mMediaSession.setMediaButtonReceiver(mbrIntent);

        mNotificationManager = new MediaNotificationManager(this, mMediaSession.getController(), this);

        final boolean rememberPlaylist = getSharedPreferences(Preferences.GENERAL_SETTINGS_PREF, MODE_PRIVATE)
                .getBoolean(Preferences.REMEMBER_PREVIOUS_PLAYLIST, false);

        mPlaybackManager.setRememberPlaylist(rememberPlaylist);
        mPulseController.setRememberPlaylist(rememberPlaylist, false);

        final boolean sleepTimerEnabled = getSharedPreferences(Preferences.GENERAL_SETTINGS_PREF, MODE_PRIVATE)
                .getBoolean(Preferences.SLEEP_TIMER, Preferences.SLEEP_TIMER_DEFAULT);
        mPlaybackManager.configureTimer(sleepTimerEnabled, false);

        getSharedPreferences(Preferences.GENERAL_SETTINGS_PREF, MODE_PRIVATE)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mWorkerHandler.post(() -> MediaButtonReceiver.handleIntent(MediaSessionCompat.fromMediaSession(this, mMediaSession), intent));
        mWorkerHandler.post(() -> handleIntent(intent));
        return START_NOT_STICKY;
    }

    private void handleIntent(@Nullable Intent intent) {
        String actionCode;
        if (null == intent || (actionCode = intent.getAction()) == null) return;
        switch (actionCode) {
            case ACTION_PLAY_CONTINUE:
                PlaybackState state = mMediaSession.getController().getPlaybackState();
                if (null == state) {
                    if (intent.hasExtra(KEY_PLAY_CONTINUE)) {
                        int playContinueAction = intent.getIntExtra(KEY_PLAY_CONTINUE, DEFAULT_ACTION_PLAY_NONE);
                        loadAction(playContinueAction);
                    }
                } else if (state.getState() == PlaybackState.STATE_PAUSED || state.getState() == PlaybackState.STATE_STOPPED) {
                    mMediaSession.getController().getTransportControls().play();
                }
                break;
            case ACTION_DEFAULT_PLAY:
                if (intent.hasExtra(KEY_DEFAULT_PLAY)) {
                    int defaultPlayAction = intent.getIntExtra(KEY_DEFAULT_PLAY, DEFAULT_ACTION_PLAY_NONE);
                    loadAction(defaultPlayAction);
                }
                break;
            default:
                Log.e(TAG, "Unknown action");
        }
    }

    private void loadAction(int action) {
        if (LoaderManager.isMasterListEmpty()) {
            LoaderManager.loadMaster(this, success -> {
                if (null == success || !success)
                    Toast.makeText(this, getString(R.string.message_empty_recent), Toast.LENGTH_SHORT).show();
                else mWorkerHandler.post(() -> handleDefaultActions(action));
            });
        } else handleDefaultActions(action);
    }

    private void handleDefaultActions(int action) {
        switch (action) {
            case DEFAULT_ACTION_PLAY_SHUFFLE:
                playShuffle();
                break;
            case DEFAULT_ACTION_PLAY_LATEST:
                playLatest();
                break;
            case DEFAULT_ACTION_PLAY_SUGGESTED:
                playSuggested();
                break;
            case DEFAULT_ACTION_CONTINUE_PLAYLIST:
                playContinuePlaylist();
                break;
        }
    }

    private void playLatest() {
        LoaderManager.getLatestTracksList(this::playPlaylist);
    }

    private void playSuggested() {
        LoaderManager.getSuggestionsList(this::playPlaylist);
    }

    private void playShuffle() {
        List<MusicModel> master = LoaderManager.getCachedMasterList();
        if (null == master || master.isEmpty()) return;
        List<MusicModel> listToShuffle = new ArrayList<>(master);
        Collections.shuffle(listToShuffle);
        playPlaylist(listToShuffle);
    }

    private void playContinuePlaylist() {
        MediaController controller = mMediaSession.getController();
        Bundle extras = new Bundle();
        extras.putBoolean(PlaybackManager.START_PLAYBACK, true);
        controller.getTransportControls().sendCustomAction(
                PlaybackManager.ACTION_LOAD_LAST_PLAYLIST,
                extras);
    }

    private void playPlaylist(@Nullable List<MusicModel> playlist) {
        if (null != playlist && !playlist.isEmpty()) {
            mPulseController.setPlaylist(playlist);
            mMediaSession.getController().getTransportControls().play();
        } else
            Toast.makeText(this, getString(R.string.message_empty_recent), Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mCanServiceStopSelf = false;
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        mCanServiceStopSelf = false;
        super.onRebind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        mCanServiceStopSelf = true;
        if (mMediaSession != null && !mMediaSession.isActive()) stopSelf();
    }

    @Override
    public void onDestroy() {
        mPulseController.releaseController();
        if (mCanServiceStopSelf) {
            // Clear track cache only when service is free to stop itself
            // not during a restart or unbind-rebind event
            LoaderManager.clearCache();
        }
        getSharedPreferences(Preferences.GENERAL_SETTINGS_PREF, MODE_PRIVATE)
                .unregisterOnSharedPreferenceChangeListener(this);
        if (isReceiverRegistered) mNotificationManager.unregisterControlsReceiver();
        if (mMediaSession != null) mMediaSession.release();
        mWorkerHandler.removeCallbacksAndMessages(null);
        mServiceThread.quit();
        super.onDestroy();
    }

    @Override
    public void onPlaybackStart() {
        mMediaSession.setActive(true);
    }

    @Override
    public void onPlaybackStopped() {
        mMediaSession.setActive(false);
    }

    @Override
    public void onStartNotification() {
        mNotificationManager.startNotification();
    }

    @Override
    public void onStopNotification() {
        mNotificationManager.stopNotification();
    }

    @Override
    public void onPlaybackStateChanged(PlaybackState newState) {
        mMediaSession.setPlaybackState(newState);
    }

    @Override
    public void onMetaDataChanged(MediaMetadata newMetaData) {
        mMediaSession.setMetadata(newMetaData);
    }

    @Override
    public MediaSession.Token getMediaSessionToken() {
        return mMediaSession.getSessionToken();
    }

    @Override
    public void onNotificationStarted(int notificationId, Notification notification) {
        startForeground(notificationId, notification);
    }

    @Override
    public void onNotificationStopped(boolean removeNotification) {
        stopForeground(removeNotification);
        if (removeNotification && mCanServiceStopSelf) stopSelf();
    }

    @Override
    public void registerControlsReceiver(BroadcastReceiver controlsReceiver, IntentFilter filter) {
        registerReceiver(controlsReceiver, filter);
        isReceiverRegistered = true;
    }

    @Override
    public void unregisterControlsReceiver(BroadcastReceiver controlsReceiver) {
        unregisterReceiver(controlsReceiver);
        isReceiverRegistered = false;
    }

    @Override
    public void onSharedPreferenceChanged(@NonNull SharedPreferences sharedPreferences, @NonNull String key) {
        mWorkerHandler.post(() -> handleSharedPreferenceChange(sharedPreferences, key));
    }

    private void handleSharedPreferenceChange(@NonNull SharedPreferences sharedPreferences, @NonNull String key) {
        switch (key) {
            case Preferences.REMEMBER_PREVIOUS_PLAYLIST:
                boolean remember = sharedPreferences.getBoolean(Preferences.REMEMBER_PREVIOUS_PLAYLIST, false);
                mPlaybackManager.setRememberPlaylist(remember);
                mPulseController.setRememberPlaylist(remember, true);
                break;
            case Preferences.SLEEP_TIMER:
            case Preferences.SLEEP_TIMER_DURATION:
                mPlaybackManager.configureTimer(sharedPreferences.getBoolean(Preferences.SLEEP_TIMER, Preferences.SLEEP_TIMER_DEFAULT),
                        mMediaSession.isActive());
                break;
        }
    }

    public class ServiceBinder extends Binder {
        public MediaController getMediaController() {
            return mMediaSession.getController();
        }
    }
}
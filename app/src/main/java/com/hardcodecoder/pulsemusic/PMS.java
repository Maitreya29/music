package com.hardcodecoder.pulsemusic;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaMetadata;
import android.media.audiofx.AudioEffect;
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

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.media.session.MediaButtonReceiver;

import com.hardcodecoder.pulsemusic.loaders.LoaderCache;
import com.hardcodecoder.pulsemusic.loaders.LoaderHelper;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.playback.LocalPlayback;
import com.hardcodecoder.pulsemusic.playback.MediaNotificationManager;
import com.hardcodecoder.pulsemusic.playback.PlaybackManager;
import com.hardcodecoder.pulsemusic.providers.ProviderManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PMS extends Service implements PlaybackManager.PlaybackServiceCallback, MediaNotificationManager.NotificationCallback {

    public static final String ACTION_DEFAULT_PLAY = "com.hardcodecoder.pulsemusic.PMS.ACTION_DEFAULT_PLAY";
    public static final String ACTION_PLAY_CONTINUE = "com.hardcodecoder.pulsemusic.PMS.ACTION_PLAY_CONTINUE";
    public static final String KEY_DEFAULT_PLAY = "com.hardcodecoder.pulsemusic.PMS.KEY_DEFAULT_PLAY";
    public static final String KEY_PLAY_CONTINUE = "com.hardcodecoder.pulsemusic.PMS.KEY_BLUETOOTH_AUTO_PLAY";
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
    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;
    private boolean isServiceRunning = false;
    private boolean isReceiverRegistered = false;

    @Override
    public void onCreate() {
        super.onCreate();
        ProviderManager.init(this);
        mServiceThread = new HandlerThread("PMS Thread", Thread.NORM_PRIORITY);
        mServiceThread.start();
        mWorkerHandler = new Handler(mServiceThread.getLooper());
        mWorkerHandler.post(this::runOnServiceThread);
    }

    private void runOnServiceThread() {
        final boolean rememberPlaylist = getSharedPreferences(Preferences.GENERAL_SETTINGS_PREF, MODE_PRIVATE)
                .getBoolean(Preferences.REMEMBER_PREVIOUS_PLAYLIST, false);

        LocalPlayback playback = new LocalPlayback(this, mWorkerHandler);
        PlaybackManager playbackManager = new PlaybackManager(getApplicationContext(), playback, this);
        playbackManager.setRememberPlaylist(rememberPlaylist);

        mMediaSession = new MediaSession(getApplicationContext(), TAG);
        mMediaSession.setCallback(playbackManager.getSessionCallbacks(), mWorkerHandler);
        mMediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
        PulseController pulseController = PulseController.getInstance();
        pulseController.setController(mMediaSession.getController());
        pulseController.setRememberPlaylist(rememberPlaylist, false);

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(getApplicationContext(), MediaButtonReceiver.class);
        PendingIntent mbrIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
        mMediaSession.setMediaButtonReceiver(mbrIntent);
        mNotificationManager = new MediaNotificationManager(this, this);

        // Start audio fx
        final Intent intent = new Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION);
        intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, mMediaSession.getSessionToken());
        intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
        intent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
        sendBroadcast(intent);

        sharedPreferenceChangeListener = (sharedPreferences, key) -> {
            if (key.equals(Preferences.REMEMBER_PREVIOUS_PLAYLIST)) {
                boolean remember = sharedPreferences.getBoolean(Preferences.REMEMBER_PREVIOUS_PLAYLIST, false);
                playbackManager.setRememberPlaylist(remember);
                pulseController.setRememberPlaylist(remember, true);
            }
        };
        getSharedPreferences(Preferences.GENERAL_SETTINGS_PREF, MODE_PRIVATE)
                .registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    /* Never use START_STICKY here
     * The use case was -
     * --> open the app
     * --> pLay a song
     * --> remove the app from recent
     * --> relaunch the app by clicking on notification
     * --> pause the playback
     * --> remove the app from recent
     * --> Notification was started again and the service runs in the background, tapping on notification causes the app to crash
     * Reason --> The system was recreating the service {verified by logs in on create and onStartCommand)
     * and thus notification was getting posted with the old metadata
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(MediaSessionCompat.fromMediaSession(this, mMediaSession), intent);
        isServiceRunning = true;
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
        if (null == LoaderCache.getAllTracksList()) {
            LoaderHelper.loadAllTracks(this, result -> {
                if (null != result)
                    mWorkerHandler.post(() -> handleDefaultActions(action));
                else
                    Toast.makeText(this, getString(R.string.message_empty_recent), Toast.LENGTH_SHORT).show();
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
        LoaderHelper.loadLatestTracks(this::playPlaylist);
    }

    private void playSuggested() {
        LoaderHelper.loadSuggestionsList(this::playPlaylist);
    }

    private void playShuffle() {
        List<MusicModel> master = LoaderCache.getAllTracksList();
        if (null == master) return;
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
            PulseController.getInstance().setPlaylist(playlist);
            mMediaSession.getController().getTransportControls().play();
        } else
            Toast.makeText(this, getString(R.string.message_empty_recent), Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (mMediaSession != null && !mMediaSession.isActive()) {
            PulseController.getInstance().getQueueManager().resetQueue();
            stopSelf();
        }
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        if (null != sharedPreferenceChangeListener)
            getSharedPreferences(Preferences.GENERAL_SETTINGS_PREF, MODE_PRIVATE)
                    .unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        if (isReceiverRegistered)
            mNotificationManager.unregisterControlsReceiver();
        if (mMediaSession != null)
            mMediaSession.release();
        mServiceThread.quit();
        super.onDestroy();
    }

    @Override
    public void onPlaybackStart() {
        mMediaSession.setActive(true);
        if (!isServiceRunning)
            ContextCompat.startForegroundService(this, new Intent(this.getApplicationContext(), PMS.class));
    }

    @Override
    public void onPlaybackStopped() {
        mMediaSession.setActive(false);
        isServiceRunning = false;
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
        if (removeNotification) stopSelf();
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

    public class ServiceBinder extends Binder {
        public MediaController getMediaController() {
            return mMediaSession.getController();
        }
    }
}
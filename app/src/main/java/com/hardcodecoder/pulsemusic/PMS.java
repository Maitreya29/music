package com.hardcodecoder.pulsemusic;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaMetadata;
import android.media.audiofx.AudioEffect;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.hardcodecoder.pulsemusic.utils.AppSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PMS extends Service implements PlaybackManager.PlaybackServiceCallback, MediaNotificationManager.NotificationCallback {

    public static final String PLAY_KEY = "play_key";
    public static final int PLAY_SHUFFLE = 100;
    public static final int PLAY_LATEST = 101;
    public static final int PLAY_SUGGESTED = 102;
    public static final int PLAY_CONTINUE = 103;

    private static final String TAG = "PMS";
    private final IBinder mBinder = new ServiceBinder();
    private MediaSession mMediaSession = null;
    private MediaNotificationManager mNotificationManager = null;
    private HandlerThread mServiceThread = null;
    private Handler mWorkerHandler = null;
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
        LocalPlayback playback = new LocalPlayback(this, mWorkerHandler);
        PlaybackManager mPlaybackManager = new PlaybackManager(this.getApplicationContext(), playback, this);

        mMediaSession = new MediaSession(this.getApplicationContext(), TAG);
        mMediaSession.setCallback(mPlaybackManager.getSessionCallbacks(), mWorkerHandler);
        mMediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
        PulseController.getInstance().setController(mMediaSession.getController());

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
        mWorkerHandler.post(() -> handleStartCommand(intent));
        return START_NOT_STICKY;
    }

    private void handleStartCommand(Intent intent) {
        int startCode;
        if (null != intent && (startCode = intent.getIntExtra(PLAY_KEY, -1)) != -1) {
            switch (startCode) {
                case PLAY_SHUFFLE:
                    playShuffle();
                    break;
                case PLAY_LATEST:
                    playLatest();
                    break;
                case PLAY_SUGGESTED:
                    playSuggested();
                    break;
                case PLAY_CONTINUE:
                    PlaybackState state = mMediaSession.getController().getPlaybackState();
                    if (state != null) {
                        if (state.getState() == PlaybackState.STATE_PAUSED || state.getState() == PlaybackState.STATE_STOPPED)
                            mMediaSession.getController().getTransportControls().play();
                    } else {
                        int currentAction = AppSettings.getBluetoothDeviceDetectionAction(getApplicationContext());
                        switch (currentAction) {
                            case Preferences.DEVICE_ACTION_PLAY_LATEST:
                                playLatest();
                                break;
                            case Preferences.DEVICE_ACTION_PLAY_SUGGESTED:
                                playSuggested();
                                break;
                            case Preferences.DEVICE_ACTION_PLAY_SHUFFLE:
                            default:
                                playShuffle();
                        }
                    }
                    break;
                default:
                    Log.e(TAG, "Unknown start command");
            }
        }
    }

    private void playLatest() {
        if (LoaderCache.getAllTracksList() == null) {
            LoaderHelper.loadAllTracks(this, result -> LoaderHelper.loadLatestTracks(this::playPlaylist));
        } else {
            LoaderHelper.loadLatestTracks(this::playPlaylist);
        }
    }

    private void playSuggested() {
        if (LoaderCache.getAllTracksList() == null) {
            LoaderHelper.loadAllTracks(this, result -> LoaderHelper.loadSuggestionsList(this::playPlaylist));
        } else {
            LoaderHelper.loadSuggestionsList(this::playPlaylist);
        }
    }

    private void playShuffle() {
        if (LoaderCache.getAllTracksList() == null) {
            LoaderHelper.loadAllTracks(this, result -> {
                if (result != null) shuffleTracks(result);
                else playPlaylist(null);
            });
        } else {
            shuffleTracks(LoaderCache.getAllTracksList());
        }
    }

    private void shuffleTracks(@NonNull List<MusicModel> tracks) {
        List<MusicModel> listToShuffle = new ArrayList<>(tracks);
        Collections.shuffle(listToShuffle);
        playPlaylist(listToShuffle);
    }

    private void playPlaylist(List<MusicModel> playlist) {
        if (null != playlist && playlist.size() > 0) {
            PulseController.getInstance().setPlaylist(playlist);
            mMediaSession.getController().getTransportControls().play();
        } else
            Toast.makeText(this, getString(R.string.no_playlist_tracks_found), Toast.LENGTH_SHORT).show();
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
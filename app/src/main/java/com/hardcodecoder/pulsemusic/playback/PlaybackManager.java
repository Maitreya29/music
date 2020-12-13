package com.hardcodecoder.pulsemusic.playback;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadata;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.PulseController;
import com.hardcodecoder.pulsemusic.helper.MediaArtHelper;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.providers.ProviderManager;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

import java.io.InputStream;

public class PlaybackManager implements Playback.Callback {

    public static final String ACTION_LOAD_LAST_TRACK = "LoadLastTrack";
    public static final String TRACK_ITEM = "TrackItem";
    public static final String PLAYBACK_POSITION = "PlaybackPosition";
    public static final short ACTION_PLAY_NEXT = 1;
    public static final short ACTION_PLAY_PREV = -1;
    private final PlaybackState.Builder mStateBuilder = new PlaybackState.Builder();
    private final Playback mPlayback;
    private final PlaybackServiceCallback mServiceCallback;
    private final PulseController.QueueManager mQueueManager;
    private final Context mContext;
    private boolean mManualPause;
    private final MediaSession.Callback mMediaSessionCallback = new MediaSession.Callback() {
        @Override
        public void onPlay() {
            handlePlayRequest();
            mManualPause = false;
        }

        @Override
        public void onPause() {
            handlePauseRequest();
            mManualPause = true;
        }

        @Override
        public void onSkipToNext() {
            handleSkipRequest(ACTION_PLAY_NEXT, true);
        }

        @Override
        public void onSkipToPrevious() {
            handleSkipRequest(ACTION_PLAY_PREV, true);
        }

        @Override
        public void onStop() {
            handleStopRequest();
        }

        @Override
        public void onSeekTo(long pos) {
            mPlayback.onSeekTo((int) pos);
        }

        @Override
        public void onCustomAction(@NonNull String action, @Nullable Bundle extras) {
            if (action.equals(ACTION_LOAD_LAST_TRACK) && extras != null)
                handleLoadLastTrack(extras);
        }
    };

    public PlaybackManager(@NonNull Context context, @NonNull Playback playback, @NonNull PlaybackServiceCallback serviceCallback) {
        mContext = context;
        mPlayback = playback;
        mServiceCallback = serviceCallback;
        mPlayback.setCallback(this);
        mQueueManager = PulseController.getInstance().getQueueManager();
    }

    public MediaSession.Callback getSessionCallbacks() {
        return mMediaSessionCallback;
    }

    private void handlePlayRequest() {
        mServiceCallback.onPlaybackStart();
        mPlayback.onPlay(0, true);
    }

    private void handlePauseRequest() {
        saveTrackAndPosition();
        mServiceCallback.onPlaybackStopped();
        mPlayback.onPause();
    }

    private void handleStopRequest() {
        saveTrackAndPosition();
        mServiceCallback.onPlaybackStopped();
        mPlayback.onStop(true);
    }

    private void handleSkipRequest(short di, boolean manualSkip) {
        // User manually skipped the track, so we stop repeat
        if (manualSkip) mQueueManager.repeatCurrentTrack(false);
        if (mQueueManager.canSkipTrack(di)) handlePlayRequest();
        else handlePauseRequest();
    }

    private void handleLoadLastTrack(@NonNull Bundle bundle) {
        final MusicModel trackItem = (MusicModel) bundle.getSerializable(TRACK_ITEM);
        if (trackItem == null) return;
        final int resumePosition = bundle.getInt(PLAYBACK_POSITION);
        PulseController.getInstance().addToQueue(trackItem);
        mQueueManager.setActiveIndex(0);
        mPlayback.onPlay(resumePosition, false);
    }

    private void updatePlaybackState(int currentState) {
        mStateBuilder.setState(currentState, mPlayback.getCurrentStreamingPosition(), currentState == PlaybackState.STATE_PLAYING ? 1 : 0);
        mStateBuilder.setActions(getActions(currentState));
        mServiceCallback.onPlaybackStateChanged(mStateBuilder.build());

        if (currentState == PlaybackState.STATE_PLAYING) {
            mServiceCallback.onStartNotification();
        } else if (currentState == PlaybackState.STATE_STOPPED) {
            mServiceCallback.onStopNotification();
        }
    }

    private Bitmap loadAlbumArt(String path, int albumId) {
        // We know that manually selected tracks have negative album id
        if (albumId < 0)
            return MediaArtHelper.getDefaultAlbumArtBitmap(mContext, albumId);
        try {
            Uri uri = Uri.parse(path);
            InputStream is = mContext.getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            return MediaArtHelper.getDefaultAlbumArtBitmap(mContext, albumId);
        }
    }

    private long getActions(int state) {
        long actions;
        if (state == PlaybackState.STATE_PLAYING) {
            actions = PlaybackState.ACTION_PAUSE;
        } else {
            actions = PlaybackState.ACTION_PLAY;
        }
        return PlaybackState.ACTION_SKIP_TO_PREVIOUS | PlaybackState.ACTION_SKIP_TO_NEXT | actions | PlaybackState.ACTION_SEEK_TO;
    }

    @Override
    public void onFocusChanged(boolean resumePlayback) {
        if (mManualPause) return;
        if (resumePlayback) handlePlayRequest();
        else handlePauseRequest();
    }

    @Override
    public void onPlaybackCompletion() {
        handleSkipRequest(ACTION_PLAY_NEXT, false);
    }

    @Override
    public void onPlaybackStateChanged(int state) {
        updatePlaybackState(state);
    }

    @Override
    public void onTrackConfigured(@NonNull MusicModel trackItem) {
        if (mPlayback.getActiveMediaId() != trackItem.getId()) {
            // Track has changed, we need to update metadata
            MediaMetadata.Builder metadataBuilder = new MediaMetadata.Builder();
            metadataBuilder.putLong(MediaMetadata.METADATA_KEY_DURATION, trackItem.getTrackDuration());
            metadataBuilder.putString(MediaMetadata.METADATA_KEY_TITLE, trackItem.getTrackName());
            metadataBuilder.putString(MediaMetadata.METADATA_KEY_ARTIST, trackItem.getArtist());
            metadataBuilder.putString(MediaMetadata.METADATA_KEY_ALBUM, trackItem.getAlbum());
            metadataBuilder.putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, loadAlbumArt(trackItem.getAlbumArtUrl(), trackItem.getAlbumId()));
            mServiceCallback.onMetaDataChanged(metadataBuilder.build());
        }
        // Do not save any media that was picked by user
        // All data might not available to work with such tracks when building
        // HistoryRecords and or TopAlbums/TopArtist
        if (trackItem.getAlbumId() >= 0)
            ProviderManager.getHistoryProvider().addToHistory(trackItem);
    }

    private void saveTrackAndPosition() {
        final int trackId = mPlayback.getActiveMediaId();
        final long position = mPlayback.getCurrentStreamingPosition();
        if (trackId >= 0 && position > 0) {
            // Remember last played track even if option is disabled in the settings
            // We will only display last track if settings is enabled
            // Store current duration and track id
            AppSettings.saveTrackAndPosition(mContext, mPlayback.getActiveMediaId(), mPlayback.getCurrentStreamingPosition());
        }
    }

    public interface PlaybackServiceCallback {
        void onPlaybackStart();

        void onPlaybackStopped();

        void onStartNotification();

        void onStopNotification();

        void onPlaybackStateChanged(PlaybackState newState);

        void onMetaDataChanged(MediaMetadata newMetaData);
    }
}
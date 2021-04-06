package com.nezukoos.music.service;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.nezukoos.music.Preferences;
import com.nezukoos.music.R;
import com.nezukoos.music.playback.PulseController;
import com.nezukoos.music.utils.AppSettings;

@RequiresApi(api = Build.VERSION_CODES.N)
public class PulseTileService extends TileService implements PulseController.OnControllerReadyListener {

    public static final String TAG = PulseTileService.class.getSimpleName();
    private MediaController mController;
    private final MediaController.Callback mCallback = new MediaController.Callback() {
        @Override
        public void onPlaybackStateChanged(@Nullable PlaybackState state) {
            updateTileState(state);
        }

        @Override
        public void onMetadataChanged(@Nullable MediaMetadata metadata) {
            updateTileData(metadata);
        }

        @Override
        public void onSessionDestroyed() {
            if (null != mController)
                mController.unregisterCallback(mCallback);
            mController = null;
            updateTileData(null);
            updateTileState(null);
        }
    };

    @Override
    public void onStartListening() {
        super.onStartListening();
        PulseController pulseController = PulseController.getInstance();
        pulseController.addConnectionCallback(this);
        mController = pulseController.getController();
        if (null != mController) {
            mController.registerCallback(mCallback);
            updateTileState(mController.getPlaybackState());
            updateTileData(mController.getMetadata());
        } else {
            updateTileState(null);
            updateTileData(null);
        }
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        if (null != mController) mController.unregisterCallback(mCallback);
        PulseController.getInstance().removeConnectionCallback(this);
    }

    @Override
    public void onClick() {
        super.onClick();
        PlaybackState state = null;
        if (null != mController) state = mController.getPlaybackState();
        if (null == mController || null == state || state.getState() == PlaybackState.STATE_STOPPED) {
            final int action = AppSettings.getAutoPlayAction(this, Preferences.KEY_QS_TILE_ACTION);
            if (action == Preferences.ACTION_PLAY_CONTINUE && !AppSettings.isRememberPlaylistEnabled(this))
                return;
            Intent intent = new Intent(this, PMS.class);
            intent.setAction(PMS.ACTION_PLAY_CONTINUE);
            intent.putExtra(PMS.KEY_PLAY_CONTINUE, action);
            ContextCompat.startForegroundService(this, intent);
        } else {
            PulseController.PulseRemote remote = PulseController.getInstance().getRemote();
            if (state.getState() == PlaybackState.STATE_PLAYING) remote.pause();
            else remote.play();
        }
    }

    @Override
    public void onControllerReady(@NonNull MediaController controller) {
        mController = controller;
        mController.registerCallback(mCallback);
    }

    private void updateTileState(@Nullable PlaybackState state) {
        if (null != state && state.getState() == PlaybackState.STATE_SKIPPING_TO_QUEUE_ITEM) {
            // Do not notify about skipping event
            // We use this state to fix seek bar issue in notification
            return;
        }
        Tile tile = getQsTile();
        tile.setState((null == state || state.getState() == PlaybackState.STATE_STOPPED) ?
                Tile.STATE_INACTIVE : Tile.STATE_ACTIVE);

        if (state == null || state.getState() != PlaybackState.STATE_PLAYING) {
            tile.setIcon(Icon.createWithResource(this, R.drawable.ic_round_play));
        } else {
            tile.setIcon(Icon.createWithResource(this, R.drawable.ic_round_pause));
        }
        tile.updateTile();
    }

    private void updateTileData(@Nullable MediaMetadata mediaMetadata) {
        Tile tile = getQsTile();
        String title = getString(R.string.play);
        if (null != mediaMetadata) {
            title = mediaMetadata.getString(MediaMetadata.METADATA_KEY_TITLE);
        }
        tile.setLabel(title);
        tile.setContentDescription(title);
        tile.updateTile();
    }
}
package com.hardcodecoder.pulsemusic.activities;

import android.content.Intent;
import android.media.session.MediaController;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.base.AdvancePlaylist;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;

import java.util.ArrayList;

public class CurrentPlaylistActivity extends AdvancePlaylist {

    public static final String TRACK_CHANGED = "HasTracksChanged";
    public static final String TRANSITION_STYLE_KEY = "TransitionStyle";
    public static final short TRANSITION_STYLE_DEFAULT = 0;
    public static final short TRANSITION_STYLE_SLIDE = 1;
    public static final int REQUEST_UPDATE_TRACK = 40; // Calling activity get's notified if the tracks changes
    private MediaController mController;
    private short mTransitionStyle;
    private boolean mHasTracksChanged = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mTransitionStyle = getIntent().getShortExtra(TRANSITION_STYLE_KEY, TRANSITION_STYLE_DEFAULT);
        if (mTransitionStyle == TRANSITION_STYLE_SLIDE)
            overridePendingTransition(R.anim.slide_in_bottom, R.anim.activity_open_exit);

        super.onCreate(savedInstanceState);
        setUpToolbar(getString(R.string.playlist_current_queue), v -> {
            Intent i = new Intent();
            i.putExtra(TRACK_CHANGED, mHasTracksChanged);
            setResult(RESULT_OK, i);
            finishActivity(REQUEST_UPDATE_TRACK);
            finish();
        });
        setShuffleButtonAction(v -> {
            if (null == mPlaylistTracks) return;
            shuffleTrackAndPlay(mPlaylistTracks);
            mAdapter.updatePlaylist(TrackManager.getInstance().getActiveQueue());
        });
        setUpData(mTrackManager.getActiveQueue(), mTrackManager.getActiveIndex());
    }

    @Override
    protected void onReceiveData(ArrayList<MusicModel> receivedData) {
        if (null == mAdapter) {
            setUpData(receivedData, mTrackManager.getActiveIndex());
            mTrackManager.buildDataList(receivedData, 0);
        } else {
            mAdapter.addItems(receivedData);
            mTrackManager.buildDataList(mPlaylistTracks, mTrackManager.getActiveIndex());
        }
        mHasTracksChanged = true;
    }

    @Override
    public void onItemClick(int position) {
        super.onItemClick(position);
        mHasTracksChanged = true;
    }

    @Override
    public void onItemDismissed(final int position) {
        mTrackManager.removeItemFromActiveQueue(position);
        Snackbar sb = Snackbar.make(findViewById(R.id.playlist_data_root_view), R.string.item_removed, Snackbar.LENGTH_SHORT);
        sb.setAction(getString(R.string.snack_bar_action_undo), v -> {
            mAdapter.restoreItem();
            mTrackManager.restoreItem(position, mPlaylistTracks.get(position));
            if (mTrackManager.getActiveIndex() == position)
                playMedia();
        });
        sb.show();
        if (position == mTrackManager.getActiveIndex()) {
            if (mTrackManager.getActiveQueue().size() > position) {
                playMedia();
            } else {
                // Active and last item in the playlist was removed
                // Stop playback immediately
                mController.getTransportControls().stop();
            }
        }
        mHasTracksChanged = true;
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        mTrackManager.updateActiveQueue(fromPosition, toPosition);
        mHasTracksChanged = true;
    }

    @Override
    public void onMediaServiceConnected(MediaController controller) {
        super.onMediaServiceConnected(controller);
        mController = controller;
    }

    private void applyExitTransition() {
        if (mTransitionStyle == TRANSITION_STYLE_SLIDE)
            overridePendingTransition(R.anim.activity_close_enter, R.anim.slide_out_bottom);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        applyExitTransition();
    }

    @Override
    public void finish() {
        super.finish();
        applyExitTransition();
    }
}
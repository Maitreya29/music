package com.hardcodecoder.pulsemusic.activities;

import android.media.session.MediaController;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.base.AdvancePlaylist;
import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.util.ArrayList;
import java.util.List;

public class CurrentPlaylistActivity extends AdvancePlaylist {

    private MediaController mController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpToolbar(getString(R.string.playlist_current_queue));
        setShuffleButtonAction(v -> {
            if (null == mPlaylistTracks) return;
            List<MusicModel> oldList = new ArrayList<>(mPlaylistTracks);
            shuffleTrackAndPlay(mPlaylistTracks);
            mAdapter.onListShuffled(oldList);
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
    }

    @Override
    public void onItemDismissed(int position) {
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
                // Active item was removed
                // Stop playback immediately
                mController.getTransportControls().stop();
            }
        }
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        mTrackManager.updateActiveQueue(fromPosition, toPosition);
    }

    @Override
    public void onMediaServiceConnected(MediaController controller) {
        super.onMediaServiceConnected(controller);
        mController = controller;
    }
}

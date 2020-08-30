package com.hardcodecoder.pulsemusic.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.base.AdvancePlaylist;
import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.util.ArrayList;
import java.util.List;

public class CurrentPlaylistActivity extends AdvancePlaylist {

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
        if (mTrackManager.canRemoveItem(position)) {
            mTrackManager.removeItemFromActiveQueue(position);

            Snackbar sb = Snackbar.make(findViewById(R.id.playlist_data_root_view), R.string.item_removed, Snackbar.LENGTH_SHORT);
            sb.setAction(getString(R.string.snack_bar_action_undo), v -> {
                mAdapter.restoreItem();
                mTrackManager.restoreItem(position, mPlaylistTracks.get(position));
            });
            sb.show();
        } else
            Toast.makeText(this, getString(R.string.cannot_remove_active_item), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        mTrackManager.updateActiveQueue(fromPosition, toPosition);
    }
}

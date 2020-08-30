package com.hardcodecoder.pulsemusic.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.base.AdvancePlaylist;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.storage.AppFileManager;

import java.util.ArrayList;

public class UserPlaylistTracksActivity extends AdvancePlaylist {

    public static final String KEY_TITLE = "playlist name";
    private String playListTitle = "";
    private boolean isPlaylistModified = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null)
            playListTitle = getIntent().getExtras().getString(KEY_TITLE);

        setUpToolbar(playListTitle);

        if (playListTitle != null)
            AppFileManager.getPlaylistTracks(playListTitle, result -> setUpData(result, 0));
    }

    @Override
    protected void onReceiveData(ArrayList<MusicModel> receivedData) {
        AppFileManager.addItemsToPlaylist(playListTitle, receivedData, true, result -> {
            if (result) {
                if (null == mAdapter) setUpData(receivedData, 0);
                else mAdapter.addItems(receivedData);
            }
        });
    }

    @Override
    public void onItemDismissed(int position) {
        Snackbar sb = Snackbar.make(findViewById(R.id.playlist_data_root_view), R.string.item_removed, Snackbar.LENGTH_SHORT);
        sb.setAction(getString(R.string.snack_bar_action_undo), v -> mAdapter.restoreItem());
        isPlaylistModified = true;
        sb.show();
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        isPlaylistModified = true;
    }

    @Override
    protected void onDestroy() {
        if (isPlaylistModified)
            AppFileManager.updatePlaylistItems(playListTitle, mPlaylistTracks);
        super.onDestroy();
    }
}
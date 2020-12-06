package com.hardcodecoder.pulsemusic.activities;

import android.content.Intent;
import android.media.session.MediaController;
import android.media.session.PlaybackState;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.base.PlaylistActivity;
import com.hardcodecoder.pulsemusic.adapters.playlist.CustomizablePlaylistAdapter;
import com.hardcodecoder.pulsemusic.helper.RecyclerViewGestureHelper;
import com.hardcodecoder.pulsemusic.interfaces.ItemGestureCallback;
import com.hardcodecoder.pulsemusic.interfaces.PlaylistItemListener;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;

import java.util.List;

public class CurrentQueuePlaylist extends PlaylistActivity implements PlaylistItemListener, ItemGestureCallback<MusicModel> {

    public static final String TRACK_CHANGED = "HasTracksChanged";
    private MediaController mController;
    private TrackManager mTrackManager;
    private CustomizablePlaylistAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private boolean mHasTracksChanged = false;

    @Override
    protected void loadContent() {
        mTrackManager = TrackManager.getInstance();
        setUpContent(mTrackManager.getActiveQueue());
        setPlaylistTitle(getString(R.string.playlist_current_queue));
    }

    @Override
    protected void loadRecyclerList(@NonNull RecyclerView recyclerView, @NonNull List<MusicModel> list) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new CustomizablePlaylistAdapter(getLayoutInflater(), list, this, this);
        recyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback itemTouchHelperCallback = new RecyclerViewGestureHelper(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onInitializeDynamicButtons() {
        setPlaylistDynamicFabButton(R.drawable.ic_shuffle, v -> {
            // Shuffle and play
            if (mAdapter != null) {
                shuffleTrackAndPlay(mAdapter.getDataList());
                mAdapter.updatePlaylist(mTrackManager.getActiveQueue());
            }
        });

        setPlaylistDynamicButton1(getString(R.string.playlist_play), R.drawable.ic_round_play, v ->
                onItemClick(0));

        setPlaylistDynamicButton2(getString(R.string.playlist_add_more), R.drawable.ic_playlist_add, v ->
                openTrackPicker());
    }

    @Override
    public void onItemClick(int position) {
        if (null == mAdapter) return;
        mTrackManager.setActiveIndex(position);
        playMedia();
        mHasTracksChanged = true;
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder holder) {
        mItemTouchHelper.startDrag(holder);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        mTrackManager.updateActiveQueue(fromPosition, toPosition);
        mHasTracksChanged = true;
    }

    @Override
    public void onItemDismissed(@NonNull MusicModel dismissedItem, int itemPosition) {
        mTrackManager.removeItemFromActiveQueue(itemPosition);
        Snackbar sb = Snackbar.make(findViewById(R.id.playlist_layout_root), R.string.item_removed, Snackbar.LENGTH_SHORT);
        sb.setAction(getString(R.string.snack_bar_action_undo), v -> {
            mAdapter.restoreItem();
            mTrackManager.restoreItem(itemPosition, dismissedItem);
            if (mTrackManager.getActiveIndex() == itemPosition)
                playMedia();
            updateTracksInfo(mAdapter.getItemCount(), getTotalPlaylistDuration() + dismissedItem.getTrackDuration());
        });
        sb.show();
        if (itemPosition == mTrackManager.getActiveIndex()) {
            if (mTrackManager.getActiveQueue().size() > itemPosition) {
                if (mController.getPlaybackState() != null && mController.getPlaybackState().getState() == PlaybackState.STATE_PLAYING)
                    playMedia();
            } else {
                // Active and last item in the playlist was removed
                // Stop playback immediately
                mController.getTransportControls().stop();
            }
        }
        updateTracksInfo(mAdapter.getItemCount(),
                getTotalPlaylistDuration() - dismissedItem.getTrackDuration());
        mHasTracksChanged = true;
    }

    @Override
    protected void onReceivedTracks(@NonNull List<MusicModel> list) {
        if (null == mAdapter) {
            setUpContent(list);
            mTrackManager.buildDataList(list, 0);
        } else {
            mAdapter.addItems(list);
            updateTracksInfo(mAdapter.getItemCount(), getTotalPlaylistDuration() + calculatePlaylistDuration(list));
            mTrackManager.buildDataList(mAdapter.getDataList(), mTrackManager.getActiveIndex());
        }
        mHasTracksChanged = true;
    }

    @Override
    public void onMediaServiceConnected(MediaController controller) {
        mController = controller;
    }

    @Override
    public void finish() {
        Intent i = new Intent();
        i.putExtra(TRACK_CHANGED, mHasTracksChanged);
        setResult(RESULT_OK, i);
        super.finish();
    }
}
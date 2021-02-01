package com.hardcodecoder.pulsemusic.activities.playlist;

import android.media.session.MediaController;
import android.media.session.PlaybackState;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.hardcodecoder.pulsemusic.PulseController;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.playlist.base.PlaylistActivity;
import com.hardcodecoder.pulsemusic.adapters.playlist.CustomizablePlaylistAdapter;
import com.hardcodecoder.pulsemusic.helper.RecyclerViewGestureHelper;
import com.hardcodecoder.pulsemusic.interfaces.ItemGestureCallback;
import com.hardcodecoder.pulsemusic.interfaces.PlaylistItemListener;
import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.util.List;

public class CurrentQueuePlaylist extends PlaylistActivity implements PlaylistItemListener, ItemGestureCallback<MusicModel> {

    private CustomizablePlaylistAdapter mAdapter;
    private PulseController.QueueManager mQueueManager;
    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void loadContent() {
        mQueueManager = mPulseController.getQueueManager();
        setUpContent(mQueueManager.getQueue());
        setPlaylistTitle(getString(R.string.playlist_play_queue));
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
                mAdapter.updatePlaylist(mQueueManager.getQueue());
            }
        });

        setPlaylistDynamicButton1(getString(R.string.play), R.drawable.ic_round_play, v ->
                onItemClick(0));

        setPlaylistDynamicButton2(getString(R.string.add), R.drawable.ic_playlist_add, v ->
                openTrackPicker());
    }

    @Override
    public void onItemClick(int position) {
        if (null == mAdapter) return;
        mQueueManager.setActiveIndex(position);
        mRemote.play();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder holder) {
        mItemTouchHelper.startDrag(holder);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        mPulseController.moveQueueItem(fromPosition, toPosition);
    }

    @Override
    public void onItemDismissed(@NonNull MusicModel dismissedItem, int itemPosition) {
        mPulseController.removeItemFromQueue(itemPosition);
        Snackbar sb = Snackbar.make(findViewById(R.id.playlist_layout_root), R.string.track_removed_from_queue, Snackbar.LENGTH_SHORT);
        sb.setAction(getString(R.string.undo), v -> {
            mAdapter.restoreItem();
            mPulseController.addToQueue(dismissedItem, itemPosition);
            if (mQueueManager.getActiveIndex() == itemPosition)
                mRemote.play();
            updateTracksInfo(mAdapter.getItemCount(), getTotalPlaylistDuration() + dismissedItem.getTrackDuration());
        });
        sb.show();
        if (itemPosition == mQueueManager.getActiveIndex()) {
            if (mQueueManager.getQueue().size() > itemPosition) {
                MediaController controller = mPulseController.getController();
                if (controller != null && controller.getPlaybackState() != null && controller.getPlaybackState().getState() == PlaybackState.STATE_PLAYING)
                    mRemote.play();
            } else {
                // Active and last item in the playlist was removed
                // Stop playback immediately
                mRemote.stop();
            }
        }
        updateTracksInfo(mAdapter.getItemCount(),
                getTotalPlaylistDuration() - dismissedItem.getTrackDuration());
    }

    @Override
    protected void onReceivedTracks(@NonNull List<MusicModel> list) {
        if (null == mAdapter) {
            setUpContent(list);
            mPulseController.setPlaylist(list);
        } else {
            mAdapter.addItems(list);
            updateTracksInfo(mAdapter.getItemCount(), getTotalPlaylistDuration() + calculatePlaylistDuration(list));
            mPulseController.setPlaylist(mAdapter.getDataList(), mQueueManager.getActiveIndex());
        }
    }
}
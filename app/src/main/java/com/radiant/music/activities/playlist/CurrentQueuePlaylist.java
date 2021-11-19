package com.radiant.music.activities.playlist;

import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.radiant.music.R;
import com.radiant.music.activities.playlist.base.PlaylistActivity;
import com.radiant.music.adapters.playlist.CustomizablePlaylistAdapter;
import com.radiant.music.helper.RecyclerViewGestureHelper;
import com.radiant.music.interfaces.ItemGestureCallback;
import com.radiant.music.interfaces.PlaylistItemListener;
import com.radiant.music.model.MusicModel;
import com.radiant.music.playback.QueueManager;

import java.util.List;

public class CurrentQueuePlaylist extends PlaylistActivity implements PlaylistItemListener, ItemGestureCallback<MusicModel> {

    private CustomizablePlaylistAdapter mAdapter;
    private QueueManager mQueueManager;
    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void loadContent() {
        mQueueManager = mRadiantController.getQueueManager();
        setUpContent(mQueueManager.getPlaylist());
        setPlaylistTitle(getString(R.string.playlist_play_queue));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.playlist_options, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.removeItem(R.id.menu_action_clear_duplicates);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_action_clear_all)
            onClearQueue();
        return true;
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
                mAdapter.updatePlaylist(mQueueManager.getPlaylist());
            }
        });

        setPlaylistDynamicButton1(getString(R.string.play), R.drawable.ic_round_play, v ->
                onItemClick(0));

        setPlaylistDynamicButton2(getString(R.string.add), R.drawable.ic_playlist_add, v ->
                openTrackPicker());
    }

    @Override
    public void onItemClick(int position) {
        if (null == mAdapter || mAdapter.getItemCount() == 0) return;
        mQueueManager.setActiveIndex(position);
        mRemote.play();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder holder) {
        mItemTouchHelper.startDrag(holder);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        mQueueManager.swapPlaylistItem(fromPosition, toPosition);
    }

    @Override
    public void onItemDismissed(@NonNull MusicModel dismissedItem, int itemPosition) {
        mQueueManager.deletePlaylistItem(dismissedItem, itemPosition);
        Snackbar sb = Snackbar.make(
                findViewById(R.id.playlist_layout_root),
                R.string.track_removed_from_queue,
                Snackbar.LENGTH_SHORT);

        sb.setAction(getString(R.string.undo), v -> {
            mAdapter.restoreItem();
            mQueueManager.addToPlaylist(dismissedItem, itemPosition);
            if (mQueueManager.getActiveIndex() == itemPosition)
                mRemote.play();
            updateTracksInfo(mAdapter.getItemCount(),
                    getCurrentPlaylistDuration() + dismissedItem.getTrackDuration());
        });

        sb.show();

        if (itemPosition == mQueueManager.getActiveIndex()) {
            if (mQueueManager.getPlaylist().size() > itemPosition) {
                if (mRadiantController.isPlaying()) mRemote.play();
            } else {
                // Active and last item in the playlist was removed
                // Stop playback immediately
                mRemote.stop();
            }
        }

        updateTracksInfo(mAdapter.getItemCount(),
                getCurrentPlaylistDuration() - dismissedItem.getTrackDuration());
    }

    private void onClearQueue() {
        if (null == mAdapter) return;
        mAdapter.clearAll();

        showEmptyListUI(true);
        mAdapter = null;

        mRemote.stop();
        mQueueManager.resetPlaylist();
    }

    @Override
    protected void onReceivedTracks(@NonNull List<MusicModel> list) {
        if (null == mAdapter) {
            setUpContent(list);
            mQueueManager.setPlaylist(list);
        } else {
            mAdapter.addItems(list);
            updateTracksInfo(mAdapter.getItemCount(),
                    getCurrentPlaylistDuration() + getPlaylistDurationFor(list));
            mQueueManager.setPlaylist(mAdapter.getDataList(), mQueueManager.getActiveIndex());
        }
    }
}
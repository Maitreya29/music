package com.hardcodecoder.pulsemusic.activities;

import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.base.PlaylistActivity;
import com.hardcodecoder.pulsemusic.adapters.PlaylistDataAdapter;
import com.hardcodecoder.pulsemusic.helper.RecyclerViewGestureHelper;
import com.hardcodecoder.pulsemusic.interfaces.ItemGestureCallback;
import com.hardcodecoder.pulsemusic.interfaces.PlaylistItemListener;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.providers.ProviderManager;

import java.util.List;

public class CustomizablePlaylist extends PlaylistActivity implements PlaylistItemListener, ItemGestureCallback<MusicModel> {

    private PlaylistDataAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private boolean mPlaylistModified = false;

    @Override
    protected void loadContent() {
        if (null == getPlaylistTitle()) showEmptyListUI(true);
        else
            ProviderManager.getPlaylistProvider().getTrackForPlaylist(getPlaylistTitle(), this::setUpContent);
    }

    @Override
    protected void loadRecyclerList(@NonNull RecyclerView recyclerView, @NonNull List<MusicModel> list) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new PlaylistDataAdapter(list, getLayoutInflater(), this, this);
        recyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback itemTouchHelperCallback = new RecyclerViewGestureHelper(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onInitializeDynamicButtons() {
        setPlaylistDynamicFabButton(R.drawable.ic_shuffle, v ->
                shuffleTrackAndPlay(mAdapter == null ? null : mAdapter.getPlaylistTracks()));

        setPlaylistDynamicButton1(getString(R.string.playlist_play), R.drawable.ic_round_play, v ->
                onItemClick(0));

        setPlaylistDynamicButton2(getString(R.string.playlist_add_more), R.drawable.ic_playlist_add, v ->
                openTrackPicker());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_advance_playllist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_action_clear_duplicates) {
            if (null == mAdapter || getPlaylistTitle() == null) return false;
            ProviderManager.getPlaylistProvider().deleteAllDuplicatesInPlaylist(
                    getPlaylistTitle(),
                    mAdapter.getPlaylistTracks(),
                    result -> {
                        if (null != result) mAdapter.updatePlaylist(result);
                    });
            return true;
        }
        return false;
    }

    @Override
    public void onItemClick(int position) {
        if (mAdapter != null) setTrackAndPlay(mAdapter.getPlaylistTracks(), position);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder holder) {
        mItemTouchHelper.startDrag(holder);
    }

    @Override
    public void onItemDismissed(@NonNull MusicModel dismissedItem, int itemPosition) {
        Snackbar sb = Snackbar.make(findViewById(R.id.playlist_layout_root), R.string.item_removed, Snackbar.LENGTH_SHORT);
        sb.setAction(getString(R.string.snack_bar_action_undo), v -> {
            mAdapter.restoreItem();
            updateTracksInfo(mAdapter.getItemCount(), getTotalPlaylistDuration() + dismissedItem.getTrackDuration());
        });
        sb.show();
        updateTracksInfo(mAdapter.getItemCount(), getTotalPlaylistDuration() - dismissedItem.getTrackDuration());
        mPlaylistModified = true;
        // Do something when playlist is empty
        // We need to take into account that after removing last item, it can be restored.
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        mPlaylistModified = true;
    }

    @Override
    protected void onReceivedTracks(@NonNull List<MusicModel> list) {
        if (getPlaylistTitle() == null) return;
        ProviderManager.getPlaylistProvider().addTracksToPlaylist(list, getPlaylistTitle(), true);
        if (null == mAdapter) setUpContent(list);
        else {
            mAdapter.addItems(list);
            updateTracksInfo(mAdapter.getItemCount(), getTotalPlaylistDuration() + calculatePlaylistDuration(list));
        }
    }

    @Override
    protected void onDestroy() {
        if (mPlaylistModified && null != mAdapter && null != getPlaylistTitle())
            ProviderManager.getPlaylistProvider().updatePlaylistTracks(getPlaylistTitle(), mAdapter.getPlaylistTracks());
        super.onDestroy();
    }
}
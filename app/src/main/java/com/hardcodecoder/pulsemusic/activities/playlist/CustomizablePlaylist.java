package com.hardcodecoder.pulsemusic.activities.playlist;

import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.playlist.base.PlaylistActivity;
import com.hardcodecoder.pulsemusic.adapters.playlist.CustomizablePlaylistAdapter;
import com.hardcodecoder.pulsemusic.helper.RecyclerViewGestureHelper;
import com.hardcodecoder.pulsemusic.interfaces.ItemGestureCallback;
import com.hardcodecoder.pulsemusic.interfaces.PlaylistItemListener;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.providers.ProviderManager;

import java.util.ArrayList;
import java.util.List;

public class CustomizablePlaylist extends PlaylistActivity implements PlaylistItemListener, ItemGestureCallback<MusicModel> {

    private CustomizablePlaylistAdapter mAdapter;
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
        mAdapter = new CustomizablePlaylistAdapter(getLayoutInflater(), list, this, this);
        recyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback itemTouchHelperCallback = new RecyclerViewGestureHelper(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onInitializeDynamicButtons() {
        setPlaylistDynamicFabButton(R.drawable.ic_shuffle, v ->
                shuffleTrackAndPlay(mAdapter == null ? null : mAdapter.getDataList()));

        setPlaylistDynamicButton1(getString(R.string.play), R.drawable.ic_round_play, v ->
                onItemClick(0));

        setPlaylistDynamicButton2(getString(R.string.add), R.drawable.ic_playlist_add, v ->
                openTrackPicker());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.playlist_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.menu_action_clear_duplicates) removeDuplicates();
        else if (id == R.id.menu_action_clear_all) clearPlaylist();
        return true;
    }

    private void removeDuplicates() {
        if (null == mAdapter || mAdapter.getItemCount() == 0 || getPlaylistTitle() == null) return;
        ProviderManager.getPlaylistProvider().deleteAllDuplicatesInPlaylist(
                getPlaylistTitle(),
                mAdapter.getDataList(),
                result -> {
                    if (null != result) {
                        mAdapter.updatePlaylist(result);
                        updateTracksInfo(result.size(), calculatePlaylistDuration(result));
                    }
                });
    }

    private void clearPlaylist() {
        if (null == mAdapter) return;
        mAdapter.clearAll();

        showEmptyListUI(true);
        mAdapter = null;

        if (null != getPlaylistTitle())
            ProviderManager.getPlaylistProvider().updatePlaylistTracks(getPlaylistTitle(), new ArrayList<>());
    }

    @Override
    public void onItemClick(int position) {
        if (mAdapter == null || mAdapter.getItemCount() == 0) return;
        setTrackAndPlay(mAdapter.getDataList(), position);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder holder) {
        mItemTouchHelper.startDrag(holder);
    }

    @Override
    public void onItemDismissed(@NonNull MusicModel dismissedItem, int itemPosition) {
        Snackbar sb = Snackbar.make(findViewById(R.id.playlist_layout_root), R.string.track_removed_from_queue, Snackbar.LENGTH_SHORT);
        sb.setAction(getString(R.string.undo), v -> {
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
            ProviderManager.getPlaylistProvider().updatePlaylistTracks(getPlaylistTitle(), mAdapter.getDataList());
        super.onDestroy();
    }
}
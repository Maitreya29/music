package com.radiant.music.activities.playlist.base;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.radiant.music.R;
import com.radiant.music.adapters.main.TracksAdapter;
import com.radiant.music.helper.DialogHelper;
import com.radiant.music.interfaces.SimpleItemClickListener;
import com.radiant.music.model.MusicModel;

import java.util.List;

public abstract class StandardPlaylist extends PlaylistActivity implements SimpleItemClickListener {

    protected TracksAdapter mAdapter;

    @Override
    protected void loadRecyclerList(@NonNull RecyclerView recyclerView, @NonNull List<MusicModel> list) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), RecyclerView.VERTICAL, false));
        mAdapter = new TracksAdapter(getLayoutInflater(), list, this, null, null);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onInitializeDynamicButtons() {
        setPlaylistDynamicFabButton(R.drawable.ic_shuffle, v ->
                shuffleTrackAndPlay(null == mAdapter ? null : mAdapter.getDataList()));

        setPlaylistDynamicButton1(getString(R.string.play), R.drawable.ic_round_play, v ->
                onItemClick(0));

        setPlaylistDynamicButton2(getString(R.string.clear_all), R.drawable.ic_clear_all, v ->
                v.postDelayed(this::clearAllTracks, 200));
    }

    protected void clearAllTracks() {
        if (null == mAdapter) return;
        List<MusicModel> currentDataList = mAdapter.getDataList();
        int size = currentDataList.size();
        currentDataList.clear();
        mAdapter.notifyItemRangeRemoved(0, size);
        showEmptyListUI(true);
        onTracksCleared();
        mAdapter = null;
    }

    @Override
    public void onItemClick(int position) {
        if (mAdapter == null || mAdapter.getItemCount() == 0) return;
        setTrackAndPlay(mAdapter.getDataList(), position);
    }

    @Override
    public void onOptionsClick(int position) {
        DialogHelper.showMenuForLibraryTracks(this, mAdapter.getDataList().get(position));
    }

    protected abstract void onTracksCleared();
}
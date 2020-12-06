package com.hardcodecoder.pulsemusic.activities.base;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.adapters.main.TracksAdapter;
import com.hardcodecoder.pulsemusic.helper.UIHelper;
import com.hardcodecoder.pulsemusic.interfaces.SimpleItemClickListener;
import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.util.List;

public abstract class StandardPlaylist extends PlaylistActivity implements SimpleItemClickListener {

    protected TracksAdapter mAdapter;

    @Override
    protected void loadRecyclerList(@NonNull RecyclerView recyclerView, @NonNull List<MusicModel> list) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), RecyclerView.VERTICAL, false));
        mAdapter = new TracksAdapter(getLayoutInflater(), list, this, null, null, true);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onInitializeDynamicButtons() {
        setPlaylistDynamicFabButton(R.drawable.ic_shuffle, v ->
                shuffleTrackAndPlay(null == mAdapter ? null : mAdapter.getDataList()));

        setPlaylistDynamicButton1(getString(R.string.playlist_play), R.drawable.ic_round_play, v ->
                onItemClick(0));

        setPlaylistDynamicButton2(getString(R.string.playlist_clear_all), R.drawable.ic_clear_all, v ->
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
        if (mAdapter != null) setTrackAndPlay(mAdapter.getDataList(), position);
    }

    @Override
    public void onOptionsClick(int position) {
        UIHelper.showMenuForLibraryTracks(this, getSupportFragmentManager(), mAdapter.getDataList().get(position));
    }

    protected abstract void onTracksCleared();
}
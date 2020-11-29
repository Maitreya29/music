package com.hardcodecoder.pulsemusic.activities.base;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.adapters.SimplePlaylistAdapter;
import com.hardcodecoder.pulsemusic.helper.UIHelper;
import com.hardcodecoder.pulsemusic.interfaces.SimpleItemClickListener;
import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.util.List;

public abstract class StandardPlaylist extends PlaylistActivity implements SimpleItemClickListener {

    protected SimplePlaylistAdapter mAdapter;

    @Override
    protected void loadRecyclerList(@NonNull RecyclerView recyclerView, @NonNull List<MusicModel> list) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), RecyclerView.VERTICAL, false));
        mAdapter = new SimplePlaylistAdapter(list, getLayoutInflater(), this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onInitializeDynamicButtons() {
        setPlaylistDynamicFabButton(R.drawable.ic_shuffle, v ->
                shuffleTrackAndPlay(null == mAdapter ? null : mAdapter.getPlaylistTracks()));

        setPlaylistDynamicButton1(getString(R.string.playlist_play), R.drawable.ic_round_play, v ->
                onItemClick(0));

        setPlaylistDynamicButton2(getString(R.string.playlist_clear_all), R.drawable.ic_clear_all, v ->
                clearAllTracks());
    }

    protected void clearAllTracks() {
        if (null == mAdapter) return;
        mAdapter.clearAll();
        showEmptyListUI(true);
        onTracksCleared();
    }

    @Override
    public void onItemClick(int position) {
        if (mAdapter != null) setTrackAndPlay(mAdapter.getPlaylistTracks(), position);
    }

    @Override
    public void onOptionsClick(int position) {
        UIHelper.showMenuForLibraryTracks(this, getSupportFragmentManager(), mAdapter.getPlaylistTracks().get(position));
    }

    protected abstract void onTracksCleared();
}
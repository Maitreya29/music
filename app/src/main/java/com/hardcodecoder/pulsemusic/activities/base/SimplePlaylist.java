package com.hardcodecoder.pulsemusic.activities.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewStub;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.adapters.SimplePlaylistAdapter;
import com.hardcodecoder.pulsemusic.helper.UIHelper;
import com.hardcodecoder.pulsemusic.interfaces.SimpleItemClickListener;
import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.util.ArrayList;
import java.util.List;

public class SimplePlaylist extends BasePlaylistActivity implements SimpleItemClickListener {

    private SimplePlaylistAdapter mAdapter;
    private List<MusicModel> mPlaylistTracks;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setShuffleButtonAction(v -> shuffleTrackAndPlay(mPlaylistTracks));
    }

    protected void setUpData(List<MusicModel> dataList) {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (null != dataList && dataList.size() > 0) {
                findViewById(R.id.no_tracks_found).setVisibility(View.GONE);
                mPlaylistTracks = new ArrayList<>(dataList);
                RecyclerView rv = (RecyclerView) ((ViewStub) findViewById(R.id.stub_playlist_tracks_rv)).inflate();
                rv.setHasFixedSize(true);
                rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), RecyclerView.VERTICAL, false));
                mAdapter = new SimplePlaylistAdapter(mPlaylistTracks, getLayoutInflater(), this);
                rv.setAdapter(mAdapter);
            } else showEmptyListText();
        });
    }

    private void showEmptyListText() {
        MaterialTextView tv = findViewById(R.id.no_tracks_found);
        tv.setVisibility(View.VISIBLE);
        tv.setText(getEmptyListStyledText());
    }

    protected void clearAllTracks() {
        if (null == mPlaylistTracks)
            return;
        mAdapter.clearAll();
        showEmptyListText();
    }

    @Override
    public void onItemClick(int position) {
        setTrackAndPlay(mPlaylistTracks, position);
    }

    @Override
    public void onOptionsClick(int position) {
        UIHelper.buildAndShowOptionsMenu(this, getSupportFragmentManager(), mPlaylistTracks.get(position));
    }
}

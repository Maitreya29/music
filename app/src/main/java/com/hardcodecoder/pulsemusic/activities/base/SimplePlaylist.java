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

import java.util.List;

public class SimplePlaylist extends BasePlaylistActivity implements SimpleItemClickListener {

    private SimplePlaylistAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setShuffleButtonAction(v -> {
            if (mAdapter != null) shuffleTrackAndPlay(mAdapter.getPlaylistTracks());
        });
    }

    protected void setUpData(@Nullable List<MusicModel> dataList) {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (null == dataList || dataList.isEmpty()) showEmptyListText();
            else {
                findViewById(R.id.no_tracks_found).setVisibility(View.GONE);
                RecyclerView rv = (RecyclerView) ((ViewStub) findViewById(R.id.stub_playlist_tracks_rv)).inflate();
                rv.setHasFixedSize(true);
                rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), RecyclerView.VERTICAL, false));
                mAdapter = new SimplePlaylistAdapter(dataList, getLayoutInflater(), this);
                rv.setAdapter(mAdapter);
            }
        });
    }

    private void showEmptyListText() {
        MaterialTextView tv = findViewById(R.id.no_tracks_found);
        tv.setVisibility(View.VISIBLE);
        tv.setText(getEmptyListStyledText());
    }

    protected void clearAllTracks() {
        if (null == mAdapter) return;
        mAdapter.clearAll();
        showEmptyListText();
    }

    @Override
    public void onItemClick(int position) {
        setTrackAndPlay(mAdapter.getPlaylistTracks(), position);
    }

    @Override
    public void onOptionsClick(int position) {
        UIHelper.showMenuForLibraryTracks(this, getSupportFragmentManager(), mAdapter.getPlaylistTracks().get(position));
    }
}
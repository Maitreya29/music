package com.hardcodecoder.pulsemusic.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.base.SimplePlaylist;
import com.hardcodecoder.pulsemusic.loaders.LoaderHelper;

public class RecentActivity extends SimplePlaylist {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpToolbar(getString(R.string.recent));
        LoaderHelper.loadRecentTracks(this::setUpData);
    }
}

package com.hardcodecoder.pulsemusic.activities;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.base.StandardPlaylist;
import com.hardcodecoder.pulsemusic.loaders.LoaderHelper;
import com.hardcodecoder.pulsemusic.providers.ProviderManager;
import com.hardcodecoder.pulsemusic.utils.DimensionsUtil;

public class RecentActivity extends StandardPlaylist {

    @Override
    protected void loadContent() {
        LoaderHelper.loadRecentTracks(this::setUpContent);
        setPlaylistTitle(getString(R.string.history_playlist_title));
    }

    @Override
    protected void onTracksCleared() {
        ProviderManager.getHistoryProvider().deleteHistoryFiles(0, null);
    }

    @Override
    protected SpannableString getEmptyPlaylistText() {
        String text = getString(R.string.no_history_tracks);
        SpannableString spannableString = new SpannableString(text);
        int len = text.length();
        spannableString.setSpan(new AbsoluteSizeSpan(
                        DimensionsUtil.getDimensionPixelSize(this, 36)),
                len - 1,
                len,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }
}
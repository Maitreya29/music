package com.nezukoos.music.activities.playlist;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;

import com.nezukoos.music.R;
import com.nezukoos.music.activities.playlist.base.StandardPlaylist;
import com.nezukoos.music.loaders.LoaderManager;
import com.nezukoos.music.providers.ProviderManager;
import com.nezukoos.music.utils.DimensionsUtil;

public class RecentActivity extends StandardPlaylist {

    @Override
    protected void loadContent() {
        LoaderManager.loadRecentTracks(this::setUpContent);
        setPlaylistTitle(getString(R.string.recent_playlist_title));
    }

    @Override
    protected void onTracksCleared() {
        ProviderManager.getHistoryProvider().deleteHistoryFiles(0, null);
    }

    @Override
    protected SpannableString getEmptyPlaylistText() {
        String text = getString(R.string.message_empty_recent);
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
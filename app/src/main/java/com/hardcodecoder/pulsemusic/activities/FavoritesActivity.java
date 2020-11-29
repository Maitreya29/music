package com.hardcodecoder.pulsemusic.activities;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.base.StandardPlaylist;
import com.hardcodecoder.pulsemusic.providers.ProviderManager;

public class FavoritesActivity extends StandardPlaylist {

    @Override
    protected void loadContent() {
        ProviderManager.getFavoritesProvider().getFavoriteTracks(this::setUpContent);
        setPlaylistTitle(getString(R.string.favorites_playlist_title));
    }

    @Override
    protected void onTracksCleared() {
        ProviderManager.getFavoritesProvider().clearAllFavorites();
    }

    @Override
    protected SpannableString getEmptyPlaylistText() {
        String text = getString(R.string.no_favorites_tracks);
        int len = text.length();
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new ForegroundColorSpan(Color.RED),
                len - 1,
                len,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }
}
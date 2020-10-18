package com.hardcodecoder.pulsemusic.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.base.SimplePlaylist;
import com.hardcodecoder.pulsemusic.providers.ProviderManager;

public class FavoritesActivity extends SimplePlaylist {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpToolbar(getString(R.string.favorites));
        ProviderManager.getFavoritesProvider().getFavoriteTracks(this::setUpData);
        setUpDynamicButton(R.string.playlist_clear_all, R.drawable.ic_clear_all, v -> {
            ProviderManager.getFavoritesProvider().clearAllFavorites();
            clearAllTracks();
        });
    }

    @Override
    protected SpannableStringBuilder getEmptyListStyledText() {
        String text = getString(R.string.no_favorites_tracks);
        int len = text.length();
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
        stringBuilder.setSpan(new ForegroundColorSpan(Color.RED),
                len - 1,
                len,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return stringBuilder;
    }
}
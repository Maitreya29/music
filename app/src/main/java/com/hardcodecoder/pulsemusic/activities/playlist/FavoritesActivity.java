package com.hardcodecoder.pulsemusic.activities.playlist;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.playlist.base.StandardPlaylist;
import com.hardcodecoder.pulsemusic.helper.UIHelper;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.providers.FavoritesProvider;
import com.hardcodecoder.pulsemusic.providers.ProviderManager;
import com.hardcodecoder.pulsemusic.themes.ThemeColors;
import com.hardcodecoder.pulsemusic.utils.DimensionsUtil;

public class FavoritesActivity extends StandardPlaylist implements FavoritesProvider.FavoritesProviderCallback {

    @Override
    protected void loadContent() {
        FavoritesProvider provider = ProviderManager.getFavoritesProvider();
        provider.getFavoriteTracks(this::setUpContent);
        provider.addCallback(this);
        setPlaylistTitle(getString(R.string.your_favorites));
    }

    @Override
    protected void onTracksCleared() {
        ProviderManager.getFavoritesProvider().clearAllFavorites();
    }

    @Override
    protected SpannableString getEmptyPlaylistText() {
        String text = getString(R.string.message_empty_favorites);
        int len = text.length();
        SpannableString spannableString = new SpannableString(text);
        int lineEnd = text.indexOf("\n");
        spannableString.setSpan(new ForegroundColorSpan(ThemeColors.getCurrentPrimaryTextColor()),
                0,
                lineEnd,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(DimensionsUtil.getDimensionPixelSize(this, 20)),
                0,
                lineEnd,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.RED),
                len - 1,
                len,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(DimensionsUtil.getDimensionPixelSize(this, 20)),
                len - 1,
                len,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    @Override
    public void onOptionsClick(int position) {
        UIHelper.showMenuForLibraryTracks(this, mAdapter.getDataList().get(position));
    }

    @Override
    public void onFavoriteAdded(@NonNull MusicModel item) {
    }

    @Override
    public void onFavoriteRemoved(@NonNull MusicModel item) {
        if (null == mAdapter || mAdapter.getItemCount() == 0) return;
        int position = mAdapter.getDataList().indexOf(item);
        mAdapter.getDataList().remove(position);
        mAdapter.notifyItemRemoved(position);
        if (mAdapter.getItemCount() == 0) showEmptyListUI(true);
    }

    @Override
    public void onFavoritesCleared() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ProviderManager.getFavoritesProvider().removeCallback(this);
    }
}
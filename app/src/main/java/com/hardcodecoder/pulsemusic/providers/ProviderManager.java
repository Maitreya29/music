package com.hardcodecoder.pulsemusic.providers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

public class ProviderManager {

    private static final Handler sHandler = new Handler(Looper.getMainLooper());
    private static HistoryProvider mHistoryProvider = null;
    private static FavoritesProvider mFavoritesProvider = null;
    private static PlaylistProvider mPlaylistProvider = null;
    private static IgnoreListProvider mIgnoredListProvider = null;
    private static PreviousPlaylistProvider mPreviousPlaylistProvider = null;
    private static String mBaseFilesDir;

    public static void init(@NonNull Context context) {
        mBaseFilesDir = context.getFilesDir().getAbsolutePath();
    }

    @NonNull
    public static HistoryProvider getHistoryProvider() {
        if (null == mHistoryProvider)
            mHistoryProvider = new HistoryProvider(mBaseFilesDir, sHandler);
        return mHistoryProvider;
    }

    @NonNull
    public static FavoritesProvider getFavoritesProvider() {
        if (null == mFavoritesProvider)
            mFavoritesProvider = new FavoritesProvider(mBaseFilesDir, sHandler);
        return mFavoritesProvider;
    }

    @NonNull
    public static PlaylistProvider getPlaylistProvider() {
        if (null == mPlaylistProvider)
            mPlaylistProvider = new PlaylistProvider(mBaseFilesDir, sHandler);
        return mPlaylistProvider;
    }

    @NonNull
    public static IgnoreListProvider getIgnoredListProvider() {
        if (null == mIgnoredListProvider)
            mIgnoredListProvider = new IgnoreListProvider(mBaseFilesDir, sHandler);
        return mIgnoredListProvider;
    }

    @NonNull
    public static PreviousPlaylistProvider getPreviousPlaylistProvider() {
        if (null == mPreviousPlaylistProvider)
            mPreviousPlaylistProvider = new PreviousPlaylistProvider(mBaseFilesDir, sHandler);
        return mPreviousPlaylistProvider;
    }
}
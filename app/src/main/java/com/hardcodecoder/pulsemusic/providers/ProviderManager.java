package com.hardcodecoder.pulsemusic.providers;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.TaskRunner;

public class ProviderManager {

    private static Handler mHandler = null;
    private static HistoryProvider mHistoryProvider = null;
    private static FavoritesProvider mFavoritesProvider = null;
    private static PlaylistProvider mPlaylistProvider = null;
    private static IgnoreListProvider mIgnoredListProvider = null;
    private static PreviousPlaylistProvider mPreviousPlaylistProvider = null;
    private static String mBaseFilesDir;

    public static void init(@NonNull Context context) {
        mHandler = TaskRunner.getMainHandler();
        mBaseFilesDir = context.getFilesDir().getAbsolutePath();
    }

    @NonNull
    public static synchronized HistoryProvider getHistoryProvider() {
        if (null == mHistoryProvider)
            mHistoryProvider = new HistoryProvider(mBaseFilesDir, mHandler);
        return mHistoryProvider;
    }

    @NonNull
    public static synchronized FavoritesProvider getFavoritesProvider() {
        if (null == mFavoritesProvider)
            mFavoritesProvider = new FavoritesProvider(mBaseFilesDir, mHandler);
        return mFavoritesProvider;
    }

    @NonNull
    public static synchronized PlaylistProvider getPlaylistProvider() {
        if (null == mPlaylistProvider)
            mPlaylistProvider = new PlaylistProvider(mBaseFilesDir, mHandler);
        return mPlaylistProvider;
    }

    @NonNull
    public static synchronized IgnoreListProvider getIgnoredListProvider() {
        if (null == mIgnoredListProvider)
            mIgnoredListProvider = new IgnoreListProvider(mBaseFilesDir, mHandler);
        return mIgnoredListProvider;
    }

    @NonNull
    public static synchronized PreviousPlaylistProvider getPreviousPlaylistProvider() {
        if (null == mPreviousPlaylistProvider)
            mPreviousPlaylistProvider = new PreviousPlaylistProvider(mBaseFilesDir, mHandler);
        return mPreviousPlaylistProvider;
    }

    public static void release() {
        mHistoryProvider = null;
        mFavoritesProvider = null;
        mPlaylistProvider = null;
        mIgnoredListProvider = null;
        mPreviousPlaylistProvider = null;
        mBaseFilesDir = null;
        mHandler = null;
    }
}
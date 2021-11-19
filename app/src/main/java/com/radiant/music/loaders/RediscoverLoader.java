package com.radiant.music.loaders;

import androidx.annotation.Nullable;

import com.radiant.music.BuildConfig;
import com.radiant.music.model.MusicModel;
import com.radiant.music.providers.ProviderManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public class RediscoverLoader implements Callable<List<MusicModel>> {

    @Nullable
    private final List<MusicModel> mExcludeTracks;
    private final int MIN_COUNT = BuildConfig.DEBUG ? 0 : 10;

    /**
     * Default constructor, that accepts a list, which is considered
     * while generating rediscover list. If this list is not null and not empty
     * rediscover list will not contain any element that is present in this list
     *
     * @param excludeTracks an optional parameter, can be null
     */
    public RediscoverLoader(@Nullable List<MusicModel> excludeTracks) {
        mExcludeTracks = excludeTracks;
    }

    @Override
    public List<MusicModel> call() {
        List<MusicModel> master = LoaderManager.getCachedMasterList();
        if (master == null || master.isEmpty()) {
            // Return null if we don't have tracks to work on
            return null;
        }

        List<MusicModel> historyTracks = ProviderManager.getHistoryProvider().getHistoryTracks();
        if (historyTracks == null || historyTracks.isEmpty() || historyTracks.size() <= MIN_COUNT) {
            // Rediscover playlist is generated only when user
            // has played few songs earlier in the app
            return null;
        }

        List<MusicModel> rediscoverList = new ArrayList<>(master);

        // Remove tracks user has already played
        rediscoverList.removeAll(historyTracks);

        // Remove specified tracks
        if (null != mExcludeTracks)
            rediscoverList.removeAll(mExcludeTracks);

        // Shuffle leftover tracks
        Collections.shuffle(rediscoverList);

        // Consider 30 or 20% of master#size() whichever is smaller
        final int rediscoverCount = Math.min((int) (0.2 * rediscoverList.size()), 30);
        return rediscoverList.subList(0, rediscoverCount);
    }
}
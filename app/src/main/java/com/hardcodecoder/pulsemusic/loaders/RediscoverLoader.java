package com.hardcodecoder.pulsemusic.loaders;

import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.BuildConfig;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.providers.ProviderManager;

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
        List<MusicModel> master = LoaderCache.getAllTracksList();
        if (master == null || master.isEmpty()) {
            // Return null if we don't have tracks to work on
            return null;
        }

        master = new ArrayList<>(LoaderCache.getAllTracksList());

        List<MusicModel> historyTracks = ProviderManager.getHistoryProvider().getHistoryTracks();
        if (historyTracks == null || historyTracks.isEmpty() || historyTracks.size() <= MIN_COUNT) {
            // Rediscover playlist is generated only when user
            // has played few songs earlier in the app
            return null;
        }

        // Remove tracks user has already played
        master.removeAll(historyTracks);

        // Remove specified tracks
        if (null != mExcludeTracks)
            master.removeAll(mExcludeTracks);

        // Shuffle leftover tracks
        Collections.shuffle(master);

        // Consider 30 or 20% of master#size() whichever is smaller
        final int rediscoverCount = Math.min((int) (0.2 * master.size()), 30);
        List<MusicModel> rediscover = new ArrayList<>(rediscoverCount);

        rediscover.addAll(master.subList(0, rediscoverCount));
        LoaderCache.setRediscoverList(rediscover);
        rediscover.clear();
        return LoaderCache.getRediscoverList();
    }
}
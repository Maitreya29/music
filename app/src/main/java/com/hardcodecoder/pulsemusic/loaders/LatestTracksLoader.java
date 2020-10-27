package com.hardcodecoder.pulsemusic.loaders;

import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.utils.SortUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class LatestTracksLoader implements Callable<List<MusicModel>> {

    @Override
    public List<MusicModel> call() {
        List<MusicModel> master = LoaderCache.getAllTracksList();
        if (master == null || master.isEmpty()) return null;

        List<MusicModel> latestTracks = new ArrayList<>(master);
        SortUtil.sortLibraryList(latestTracks, SortOrder.DATE_MODIFIED_DESC);
        int listSize = latestTracks.size();
        // Take min 20% of list or entire list
        latestTracks = latestTracks.subList(0, Math.min((int) (0.2 * listSize), listSize));

        LoaderCache.setLatestTracks(latestTracks);
        latestTracks.clear();

        return LoaderCache.getLatestTracks();
    }
}
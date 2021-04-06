package com.nezukoos.music.loaders;

import com.nezukoos.music.model.MusicModel;
import com.nezukoos.music.utils.SortUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class LatestTracksLoader implements Callable<List<MusicModel>> {

    @Override
    public List<MusicModel> call() {
        List<MusicModel> master = LoaderManager.getCachedMasterList();
        if (master == null || master.isEmpty()) return null;

        List<MusicModel> latestTracks = new ArrayList<>(master);
        SortUtil.sortLibraryList(latestTracks, SortOrder.DATE_MODIFIED_DESC);
        int listSize = latestTracks.size();
        // Take min 20% of list or entire list
        return latestTracks.subList(0, Math.min((int) (0.2 * listSize), listSize));
    }
}
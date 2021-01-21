package com.hardcodecoder.pulsemusic.loaders;

import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.utils.SortUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class AlbumTracksLoader implements Callable<List<MusicModel>> {

    private final SortOrder mSortOrder;
    private final long mAlbumId;

    public AlbumTracksLoader(long albumId, SortOrder sortOrder) {
        mAlbumId = albumId;
        mSortOrder = sortOrder;
    }

    @Override
    public List<MusicModel> call() {
        List<MusicModel> masterList = LoaderCache.getAllTracksList();
        if (masterList == null || masterList.isEmpty() || mAlbumId < 0)
            return null;

        List<MusicModel> albumTracks = new ArrayList<>();
        for (MusicModel md : masterList) {
            if (md.getAlbumId() == mAlbumId) albumTracks.add(md);
        }
        SortUtil.sortLibraryList(albumTracks, mSortOrder);
        return albumTracks;
    }
}
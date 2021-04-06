package com.nezukoos.music.loaders;

import com.nezukoos.music.model.MusicModel;
import com.nezukoos.music.utils.SortUtil;

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
        List<MusicModel> masterList = LoaderManager.getCachedMasterList();
        if (masterList == null || masterList.isEmpty()) return null;

        List<MusicModel> albumTracks = new ArrayList<>();
        for (MusicModel md : masterList) {
            if (md.getAlbumId() == mAlbumId) albumTracks.add(md);
        }
        SortUtil.sortLibraryList(albumTracks, mSortOrder);
        return albumTracks;
    }
}
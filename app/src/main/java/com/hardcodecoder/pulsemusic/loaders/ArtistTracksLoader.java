package com.hardcodecoder.pulsemusic.loaders;

import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.utils.SortUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ArtistTracksLoader implements Callable<List<MusicModel>> {

    private String mArtistName;
    private SortOrder mSortOrder;

    public ArtistTracksLoader(String artistName, SortOrder sortOrder) {
        this.mArtistName = artistName;
        this.mSortOrder = sortOrder;
    }

    @Override
    public List<MusicModel> call() {
        List<MusicModel> masterList = LoaderCache.getAllTracksList();
        List<MusicModel> listToReturn = null;
        if (null != masterList && masterList.size() > 0) {
            listToReturn = new ArrayList<>();
            for (MusicModel md : masterList)
                if (md.getArtist().equals(mArtistName))
                    listToReturn.add(md);
        }
        if (null != listToReturn) SortUtil.sortLibraryList(listToReturn, mSortOrder);
        return listToReturn;
    }
}

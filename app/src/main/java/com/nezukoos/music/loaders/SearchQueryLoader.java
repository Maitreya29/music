package com.nezukoos.music.loaders;

import com.nezukoos.music.model.MusicModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class SearchQueryLoader implements Callable<List<MusicModel>> {

    private String mSearchQuery;

    public SearchQueryLoader(String searchQuery) {
        this.mSearchQuery = searchQuery;
    }

    @Override
    public List<MusicModel> call() {
        final List<MusicModel> searchList = LoaderManager.getCachedMasterList();
        List<MusicModel> searchResult;

        if (searchList == null || searchList.isEmpty()) return null;

        searchResult = new ArrayList<>();
        mSearchQuery = mSearchQuery.toLowerCase();
        for (MusicModel musicModel : searchList) {
            if (musicModel.getTrackName().toLowerCase().contains(mSearchQuery) ||
                    musicModel.getAlbum().toLowerCase().contains(mSearchQuery) ||
                    musicModel.getArtist().toLowerCase().contains(mSearchQuery)) {
                searchResult.add(musicModel);
            }
        }
        return searchResult;
    }
}
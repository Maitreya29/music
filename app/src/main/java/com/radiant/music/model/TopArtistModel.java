package com.radiant.music.model;

import androidx.annotation.NonNull;

public class TopArtistModel {

    private final String mArtistName;
    private final int mNumOfPlays;

    public TopArtistModel(@NonNull String artistName, int numOfPlays) {
        mArtistName = artistName;
        mNumOfPlays = numOfPlays;
    }

    @NonNull
    public String getArtistName() {
        return mArtistName;
    }

    public int getNumOfPlays() {
        return mNumOfPlays;
    }
}
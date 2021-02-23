package com.hardcodecoder.pulsemusic.model;

import androidx.annotation.NonNull;

public class ArtistModel {

    private final int mArtistId;
    private final int mNumOfAlbums;
    private final int mNumOfTracks;
    private final String mArtistName;

    public ArtistModel(int artistId, @NonNull String artistName, int numOfAlbums, int numOfTracks) {
        mArtistId = artistId;
        mNumOfAlbums = numOfAlbums;
        mNumOfTracks = numOfTracks;
        mArtistName = artistName;
    }

    public int getArtistId() {
        return mArtistId;
    }

    public int getNumOfAlbums() {
        return mNumOfAlbums;
    }

    public int getNumOfTracks() {
        return mNumOfTracks;
    }

    @NonNull
    public String getArtistName() {
        return mArtistName;
    }
}
package com.nezukoos.music.model;

import androidx.annotation.NonNull;

public class ArtistModel {

    private final long mArtistId;
    private final int mNumOfAlbums;
    private final int mNumOfTracks;
    private final String mArtistName;

    public ArtistModel(long artistId, @NonNull String artistName, int numOfAlbums, int numOfTracks) {
        mArtistId = artistId;
        mNumOfAlbums = numOfAlbums;
        mNumOfTracks = numOfTracks;
        mArtistName = artistName;
    }

    public long getArtistId() {
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
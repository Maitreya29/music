package com.hardcodecoder.pulsemusic.model;

public class ArtistModel {

    private int mArtistId;
    private int mNumOfAlbums;
    private int mNumOfTracks;
    private String mArtistName;

    public ArtistModel(int artistId, String artistName, int numOfAlbums, int numOfTracks) {
        this.mArtistId = artistId;
        this.mNumOfAlbums = numOfAlbums;
        this.mNumOfTracks = numOfTracks;
        this.mArtistName = artistName;
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

    public String getArtistName() {
        return mArtistName;
    }
}
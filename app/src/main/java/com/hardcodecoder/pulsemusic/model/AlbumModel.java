package com.hardcodecoder.pulsemusic.model;

import androidx.annotation.NonNull;

public class AlbumModel {

    private final String mAlbumName;
    private final String mAlbumArtist;
    private final String mAlbumArt;
    private final long mAlbumId;
    private final int mSongsCount;
    private final int mFirstYear;
    private final int mLastYear;

    public AlbumModel(@NonNull String albumName,
                      long albumId,
                      @NonNull String albumArtist,
                      int songsCount,
                      int firstYear,
                      int lastYear,
                      @NonNull String albumArt) {
        mAlbumName = albumName;
        mAlbumId = albumId;
        mAlbumArtist = albumArtist;
        mSongsCount = songsCount;
        mFirstYear = firstYear;
        mLastYear = lastYear;
        mAlbumArt = albumArt;
    }

    @NonNull
    public String getAlbumName() {
        return mAlbumName;
    }

    public long getAlbumId() {
        return mAlbumId;
    }

    @NonNull
    public String getAlbumArtist() {
        return mAlbumArtist;
    }

    public int getSongsCount() {
        return mSongsCount;
    }

    public int getFirstYear() {
        return mFirstYear;
    }

    public int getLastYear() {
        return mLastYear;
    }

    @NonNull
    public String getAlbumArt() {
        return mAlbumArt;
    }
}
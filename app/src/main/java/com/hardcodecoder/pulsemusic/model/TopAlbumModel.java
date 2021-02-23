package com.hardcodecoder.pulsemusic.model;

import androidx.annotation.NonNull;

public class TopAlbumModel {

    private final String mAlbumName;
    private final String mAlbumArt;
    private final int mAlbumId;
    private final int mPlayCount;

    public TopAlbumModel(@NonNull String albumName, @NonNull String albumArt, int albumId, int playCount) {
        mAlbumName = albumName;
        mAlbumArt = albumArt;
        mAlbumId = albumId;
        mPlayCount = playCount;
    }

    public int getAlbumId() {
        return mAlbumId;
    }

    @NonNull
    public String getAlbumName() {
        return mAlbumName;
    }

    @NonNull
    public String getAlbumArt() {
        return mAlbumArt;
    }

    public int getPlayCount() {
        return mPlayCount;
    }
}
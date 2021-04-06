package com.nezukoos.music.model;

import androidx.annotation.NonNull;

public class TopAlbumModel {

    private final String mAlbumName;
    private final String mAlbumArt;
    private final long mAlbumId;
    private final int mPlayCount;

    public TopAlbumModel(@NonNull String albumName, @NonNull String albumArt, long albumId, int playCount) {
        mAlbumName = albumName;
        mAlbumArt = albumArt;
        mAlbumId = albumId;
        mPlayCount = playCount;
    }

    public long getAlbumId() {
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
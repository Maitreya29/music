package com.nezukoos.music.model;

import androidx.annotation.NonNull;

public class HistoryRecord {

    private final String mTitle;
    private final String mAlbum;
    private final String mArtist;
    private final long mAlbumId;
    private final long mLastModified;
    private final short mPlayCount;

    public HistoryRecord(@NonNull String title,
                         @NonNull String album,
                         @NonNull String artist,
                         long albumId,
                         short playCount,
                         long lastModified) {
        mTitle = title;
        mAlbum = album;
        mArtist = artist;
        mAlbumId = albumId;
        mPlayCount = playCount;
        mLastModified = lastModified;
    }

    @NonNull
    public String getTitle() {
        return mTitle;
    }

    @NonNull
    public String getAlbum() {
        return mAlbum;
    }

    @NonNull
    public String getArtist() {
        return mArtist;
    }

    public long getAlbumId() {
        return mAlbumId;
    }

    public long getLastModified() {
        return mLastModified;
    }

    public short getPlayCount() {
        return mPlayCount;
    }
}
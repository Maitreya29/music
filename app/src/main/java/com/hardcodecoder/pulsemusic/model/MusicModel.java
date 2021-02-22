package com.hardcodecoder.pulsemusic.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;

public class MusicModel implements Serializable {

    private final String mTrackName;
    private final String mTrackPath;
    private final String mAlbum;
    private final String mArtist;
    private final String mAlbumArtUrl;
    // Unix Time: Elapsed seconds since 1 January 1970
    private final long mDateAdded;
    // Unix Time: Elapsed seconds since 1 January 1970
    private final long mDateModified;
    private final int mId;
    private final int mAlbumId;
    private final int mDiscNumber;
    private final int mTrackNumber;
    // Duration in milli seconds
    private final int mTrackDuration;

    public MusicModel(int id,
                      @NonNull String trackName,
                      @NonNull String album,
                      int albumId,
                      @NonNull String artist,
                      @NonNull String trackPath,
                      @Nullable String albumArtUrl,
                      long dateAdded,
                      long dateModified,
                      int discNumber,
                      int trackNumber,
                      int trackDuration) {
        mId = id;
        mTrackName = trackName;
        mTrackPath = trackPath;
        mAlbum = album;
        mArtist = artist;
        mAlbumArtUrl = albumArtUrl;
        mAlbumId = albumId;
        mDateAdded = dateAdded;
        mDateModified = dateModified;
        mDiscNumber = discNumber;
        mTrackNumber = trackNumber;
        mTrackDuration = trackDuration;
    }

    public int getId() {
        return mId;
    }

    public String getTrackName() {
        return mTrackName;
    }

    public String getTrackPath() {
        return mTrackPath;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public String getArtist() {
        return mArtist;
    }

    public String getAlbumArtUrl() {
        return mAlbumArtUrl;
    }

    public int getAlbumId() {
        return mAlbumId;
    }

    public int getDiscNumber() {
        return mDiscNumber;
    }

    public int getTrackNumber() {
        return mTrackNumber;
    }

    public long getDateAdded() {
        return mDateAdded;
    }

    public long getDateModified() {
        return mDateModified;
    }

    public int getTrackDuration() {
        return mTrackDuration;
    }

    @Override
    public int hashCode() {
        // The id is unique for each track
        return mId;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof MusicModel && mId == ((MusicModel) obj).getId();
    }
}
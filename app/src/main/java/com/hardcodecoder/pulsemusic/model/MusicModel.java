package com.hardcodecoder.pulsemusic.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;

public class MusicModel implements Serializable {

    private String mTrackName, mTrackPath, mAlbum, mArtist, mAlbumArtUrl;
    private long mAlbumId, mDateAdded, mDateModified;
    private int mId, mTrackNumber, mTrackDuration;

    @Override
    public int hashCode() {
        return mTrackPath.hashCode();
    }

    public MusicModel(int mId,
                      @NonNull String trackName,
                      @NonNull String trackPath,
                      @NonNull String album,
                      @NonNull String artist,
                      @Nullable String albumArtUrl,
                      long albumId,
                      long dateAdded,
                      long dateModified,
                      int trackNumber,
                      int trackDuration) {
        this.mId = mId;
        this.mTrackName = trackName;
        this.mTrackPath = trackPath;
        this.mAlbum = album;
        this.mArtist = artist;
        this.mAlbumArtUrl = albumArtUrl;
        this.mAlbumId = albumId;
        this.mDateAdded = dateAdded;
        this.mDateModified = dateModified;
        this.mTrackNumber = trackNumber;
        this.mTrackDuration = trackDuration;
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

    public long getAlbumId() {
        return mAlbumId;
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
}

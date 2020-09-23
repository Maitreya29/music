package com.hardcodecoder.pulsemusic.model;

public class AlbumModel {

    private String mAlbumName;
    private String mAlbumArtist;
    private String mAlbumArt;
    private long mAlbumId;
    private int mSongsCount;
    private int mId;
    private int mFirstYear;
    private int mLastYear;

    public AlbumModel(int id, String albumName, long albumId, String albumArtist, int songsCount, int firstYear, int lastYear, String albumArt) {
        this.mId = id;
        this.mAlbumName = albumName;
        this.mAlbumId = albumId;
        this.mAlbumArtist = albumArtist;
        this.mSongsCount = songsCount;
        this.mFirstYear = firstYear;
        this.mLastYear = lastYear;
        this.mAlbumArt = albumArt;
    }

    public int getId() {
        return mId;
    }

    public String getAlbumName() {
        return mAlbumName;
    }

    public long getAlbumId() {
        return mAlbumId;
    }

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

    public String getAlbumArt() {
        return mAlbumArt;
    }
}
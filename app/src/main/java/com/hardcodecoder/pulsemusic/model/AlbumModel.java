package com.hardcodecoder.pulsemusic.model;

public class AlbumModel {

    private int mId;
    private int mSongsCount;
    private long mAlbumId;
    private String mAlbumName;
    private String mAlbumArtist;
    private String mAlbumArt;

    public AlbumModel(int id, int songsCount, long albumId, String albumName, String albumArtist, String albumArt) {
        this.mId = id;
        this.mSongsCount = songsCount;
        this.mAlbumId = albumId;
        this.mAlbumName = albumName;
        this.mAlbumArtist = albumArtist;
        this.mAlbumArt = albumArt;
    }

    public int getId() {
        return mId;
    }

    public long getAlbumId() {
        return mAlbumId;
    }

    public int getSongsCount() {
        return mSongsCount;
    }

    public String getAlbumName() {
        return mAlbumName;
    }

    public String getAlbumArtist() {
        return mAlbumArtist;
    }

    public String getAlbumArt() {
        return mAlbumArt;
    }
}

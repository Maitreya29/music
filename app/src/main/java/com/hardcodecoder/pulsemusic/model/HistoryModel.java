package com.hardcodecoder.pulsemusic.model;

public class HistoryModel {

    private String title;
    private String album;
    private String artist;
    private int albumId;
    private long lastModified;
    private short playCount;

    public HistoryModel(String title, String album, String artist, int albumId, long lastModified, short playCount) {
        this.title = title;
        this.album = album;
        this.albumId = albumId;
        this.artist = artist;
        this.lastModified = lastModified;
        this.playCount = playCount;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public int getAlbumId() {
        return albumId;
    }

    public long getLastModified() {
        return lastModified;
    }

    public short getPlayCount() {
        return playCount;
    }
}

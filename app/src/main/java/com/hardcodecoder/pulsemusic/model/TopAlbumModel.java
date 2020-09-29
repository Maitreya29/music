package com.hardcodecoder.pulsemusic.model;

public class TopAlbumModel {

    private String albumName;
    private String albumArt;
    private int albumId;
    private int playCount;

    public TopAlbumModel(String albumName, String albumArt, int albumId, int playCount) {
        this.albumName = albumName;
        this.albumArt = albumArt;
        this.albumId = albumId;
        this.playCount = playCount;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public int getAlbumId() {
        return albumId;
    }

    public int getPlayCount() {
        return playCount;
    }
}

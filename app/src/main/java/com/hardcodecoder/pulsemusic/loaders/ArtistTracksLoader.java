package com.hardcodecoder.pulsemusic.loaders;

import android.content.ContentResolver;
import android.provider.MediaStore;

import com.hardcodecoder.pulsemusic.model.AlbumModel;

import java.util.List;
import java.util.concurrent.Callable;

public class ArtistTracksLoader implements Callable<List<AlbumModel>> {

    private ContentResolver mContentResolver;
    private String mArtistName;
    private SortOrder.ALBUMS mSortOrder;

    public ArtistTracksLoader(ContentResolver contentResolver, String artistName, SortOrder.ALBUMS sortOrder) {
        this.mContentResolver = contentResolver;
        this.mArtistName = artistName;
        this.mSortOrder = sortOrder;
    }

    @Override
    public List<AlbumModel> call() {
        return new AlbumsLoader(mContentResolver, mSortOrder, MediaStore.Audio.Albums.ARTIST + " = \"" + mArtistName + "\"").call();
    }
}

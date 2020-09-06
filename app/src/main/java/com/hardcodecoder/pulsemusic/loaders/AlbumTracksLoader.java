package com.hardcodecoder.pulsemusic.loaders;

import android.content.ContentResolver;
import android.provider.MediaStore;

import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.util.List;
import java.util.concurrent.Callable;

public class AlbumTracksLoader implements Callable<List<MusicModel>> {

    private ContentResolver mContentResolver;
    private SortOrder mSortOrder;
    private long mAlbumId;

    public AlbumTracksLoader(ContentResolver contentResolver, SortOrder sortOrder, long albumId) {
        this.mContentResolver = contentResolver;
        this.mSortOrder = sortOrder;
        this.mAlbumId = albumId;
    }

    @Override
    public List<MusicModel> call() {
        return new LibraryLoader(mContentResolver, mSortOrder, MediaStore.Audio.Media.ALBUM_ID + " = " + mAlbumId).call();
    }
}

package com.hardcodecoder.pulsemusic.loaders;

import android.content.Context;
import android.os.Build;
import android.provider.MediaStore;

import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.util.List;
import java.util.concurrent.Callable;

public class AlbumTracksLoader implements Callable<List<MusicModel>> {

    private final Context mContext;
    private final SortOrder mSortOrder;
    private final long mAlbumId;

    public AlbumTracksLoader(Context context, SortOrder sortOrder, long albumId) {
        mContext = context;
        mSortOrder = sortOrder;
        mAlbumId = albumId;
    }

    @Override
    public List<MusicModel> call() {
        String selection = MediaStore.Audio.Media.ALBUM_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(mAlbumId)};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            return new LibraryLoaderQ(mContext, mSortOrder, selection, selectionArgs).call();
        else
            return new LibraryLoader(mContext, mSortOrder, selection, selectionArgs).call();
    }
}
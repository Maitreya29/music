package com.hardcodecoder.pulsemusic.loaders;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.model.AlbumModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class AlbumsLoader implements Callable<List<AlbumModel>> {

    private ContentResolver mContentResolver;
    private String mSortOrder;
    private String mSelection;

    AlbumsLoader(ContentResolver contentResolver, SortOrder.ALBUMS sortOrder) {
        this(contentResolver, sortOrder, null);
    }

    AlbumsLoader(ContentResolver mContentResolver, SortOrder.ALBUMS sortOrder, @Nullable String selection) {
        this.mContentResolver = mContentResolver;
        mSortOrder = MediaStoreHelper.getSortOrderFor(sortOrder);
        this.mSelection = selection;
    }

    @Override
    public List<AlbumModel> call() {
        List<AlbumModel> albumsList = new ArrayList<>();
        String[] col = {MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ALBUM_ID,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS};
        final Cursor cursor = mContentResolver.query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                col,
                mSelection,
                null,
                mSortOrder);

        if (cursor != null && cursor.moveToFirst()) {
            int idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID);
            int albumColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM);
            int albumIdColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ID);
            int albumArtistColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST);
            int songCountColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS);
            final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            do {
                String album = cursor.getString(albumColumnIndex);
                if (null != album) {
                    int id = cursor.getInt(idColumnIndex);
                    int num = cursor.getInt(songCountColumnIndex);
                    long albumId = cursor.getLong(albumIdColumnIndex);
                    String albumArtist = cursor.getString(albumArtistColumnIndex);
                    String albumArt = ContentUris.withAppendedId(sArtworkUri, albumId).toString();
                    albumsList.add(new AlbumModel(id, num, albumId, album, albumArtist, albumArt));
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return albumsList;
    }
}

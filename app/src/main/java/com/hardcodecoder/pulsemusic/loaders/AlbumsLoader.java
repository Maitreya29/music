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

    private final ContentResolver mContentResolver;
    private final String mSortOrder;
    private final String mSelection;

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
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS,
                MediaStore.Audio.Albums.FIRST_YEAR,
                MediaStore.Audio.Albums.LAST_YEAR};

        final Cursor cursor = mContentResolver.query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                col,
                mSelection,
                null,
                mSortOrder);

        if (cursor != null && cursor.moveToFirst()) {
            int albumIdColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID);
            int albumColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM);
            int albumArtistColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST);
            int songCountColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS);
            int albumFirstYearColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.FIRST_YEAR);
            int albumLastYearColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.LAST_YEAR);

            final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

            do {
                String album = cursor.getString(albumColumnIndex);
                if (null != album) {
                    int albumId = cursor.getInt(albumIdColumnIndex);
                    int num = cursor.getInt(songCountColumnIndex);
                    int firstYear = cursor.getInt(albumFirstYearColumnIndex);
                    int lastYear = cursor.getInt(albumLastYearColumnIndex);
                    String albumArtist = cursor.getString(albumArtistColumnIndex);
                    String albumArt = ContentUris.withAppendedId(sArtworkUri, albumId).toString();
                    albumsList.add(new AlbumModel(album, albumId, albumArtist, num, firstYear, lastYear, albumArt));
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return albumsList;
    }
}
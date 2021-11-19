package com.radiant.music.loaders;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio.Artists;
import android.provider.MediaStore.Audio.Artists.Albums;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.radiant.music.model.AlbumModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ArtistAlbumsLoader implements Callable<List<AlbumModel>> {

    private final ContentResolver mContentResolver;
    private final String mArtistName;
    private final String mSortOrder;
    private long mArtistId;

    public ArtistAlbumsLoader(@NonNull ContentResolver contentResolver,
                              long artistId,
                              @NonNull String artistName,
                              @Nullable SortOrder.ALBUMS sortOrder) {
        mContentResolver = contentResolver;
        mArtistId = artistId;
        mArtistName = artistName;
        mSortOrder = MediaStoreHelper.getSortOrderFor(sortOrder);
    }

    @Override
    public List<AlbumModel> call() {
        if (mArtistId == -1) mArtistId = getArtistIdFromName();
        if (mArtistId == -1) return null;

        String albumIdColName;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) albumIdColName = BaseColumns._ID;
        else albumIdColName = Albums.ALBUM_ID;

        String[] col = {
                albumIdColName,                         // 0
                Albums.ALBUM,                           // 1
                Albums.ARTIST,                          // 2
                Albums.NUMBER_OF_SONGS_FOR_ARTIST,      // 3
                Albums.FIRST_YEAR,                      // 4
                Albums.LAST_YEAR                        // 5
        };

        final Cursor cursor = mContentResolver.query(
                Albums.getContentUri("external", mArtistId),
                col,
                null,
                null,
                mSortOrder);

        List<AlbumModel> albumsList = null;
        if (cursor != null && cursor.moveToFirst()) {
            final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            albumsList = new ArrayList<>();
            do {
                long albumId = cursor.getLong(0);
                String album = cursor.getString(1);
                String albumArtist = cursor.getString(2);
                int num = cursor.getInt(3);
                int firstYear = cursor.getInt(4);
                int lastYear = cursor.getInt(5);
                String albumArt = ContentUris.withAppendedId(sArtworkUri, albumId).toString();
                albumsList.add(new AlbumModel(album, albumId, albumArtist, num, firstYear, lastYear, albumArt));

            } while (cursor.moveToNext());
            cursor.close();
        }
        return albumsList;
    }

    private long getArtistIdFromName() {
        String[] cols = {Artists._ID};
        final Cursor artistIdCursor = mContentResolver.query(
                Artists.EXTERNAL_CONTENT_URI,
                cols,
                Artists.ARTIST + "=?",
                new String[]{mArtistName},
                null);

        if (null != artistIdCursor && artistIdCursor.moveToFirst()) {
            int colId = artistIdCursor.getColumnIndexOrThrow(Artists._ID);
            long artistId = artistIdCursor.getLong(colId);
            artistIdCursor.close();
            return artistId;
        }
        return -1;
    }
}
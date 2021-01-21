package com.hardcodecoder.pulsemusic.loaders;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.model.AlbumModel;
import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

public class AlbumsLoader implements Callable<List<AlbumModel>> {

    private final ContentResolver mContentResolver;
    private final String mSortOrder;
    private final String mSelection;
    private final String[] mArgs;

    AlbumsLoader(@NonNull ContentResolver contentResolver, @Nullable SortOrder.ALBUMS sortOrder) {
        this(contentResolver, sortOrder, null, null);
    }

    AlbumsLoader(@NonNull ContentResolver contentResolver, @Nullable SortOrder.ALBUMS sortOrder, @Nullable String selection, @Nullable String[] args) {
        mContentResolver = contentResolver;
        mSortOrder = MediaStoreHelper.getSortOrderFor(sortOrder);
        mSelection = selection;
        mArgs = args;
    }

    @Override
    public List<AlbumModel> call() {
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
                mArgs,
                mSortOrder);

        List<AlbumModel> sanitizedAlbumsList = null;

        if (cursor != null && cursor.moveToFirst()) {
            int albumIdColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID);
            int albumColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM);
            int albumArtistColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST);
            int songCountColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS);
            int albumFirstYearColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.FIRST_YEAR);
            int albumLastYearColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.LAST_YEAR);

            final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

            List<AlbumModel> albumsList = new ArrayList<>();
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

            // Make sure no album is returned that is present in the ignored folders list
            if (null != LoaderCache.getAllTracksList()) {
                Set<Integer> albumsSet = new HashSet<>();
                for (MusicModel md : LoaderCache.getAllTracksList()) {
                    albumsSet.add(md.getAlbumId());
                }

                sanitizedAlbumsList = new ArrayList<>();
                for (AlbumModel am : albumsList) {
                    if (albumsSet.contains(am.getAlbumId()))
                        sanitizedAlbumsList.add(am);
                }
            }
        }
        return sanitizedAlbumsList;
    }
}
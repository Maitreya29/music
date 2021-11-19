package com.radiant.music.loaders;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Artists;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.radiant.music.model.ArtistModel;
import com.radiant.music.model.MusicModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

public class ArtistsLoader implements Callable<List<ArtistModel>> {

    private final ContentResolver mContentResolver;
    private final String mSortOrder;

    ArtistsLoader(@NonNull ContentResolver contentResolver, @Nullable SortOrder.ARTIST sortOrder) {
        mContentResolver = contentResolver;
        mSortOrder = MediaStoreHelper.getSortOrderFor(sortOrder);
    }

    @Override
    public List<ArtistModel> call() {
        String[] col = {
                Artists._ID,                // 0
                Artists.ARTIST,             // 1
                Artists.NUMBER_OF_ALBUMS,   // 2
                Artists.NUMBER_OF_TRACKS    // 3
        };

        final Cursor cursor = mContentResolver.query(
                Artists.EXTERNAL_CONTENT_URI,
                col,
                null,
                null,
                mSortOrder);

        List<ArtistModel> sanitizedArtistLList = null;

        if (cursor != null && cursor.moveToFirst()) {
            List<ArtistModel> artistList = new ArrayList<>();
            do {
                long artistId = cursor.getLong(0);
                String artist = cursor.getString(1);
                int num_albums = cursor.getInt(2);
                int num_tracks = cursor.getInt(3);

                artistList.add(new ArtistModel(artistId, artist, num_albums, num_tracks));
            } while (cursor.moveToNext());
            cursor.close();

            // Make sure no artist is returned that is present in ignored folders list
            List<MusicModel> masterList = LoaderManager.getCachedMasterList();
            if (null != masterList && !masterList.isEmpty()) {
                sanitizedArtistLList = new ArrayList<>();
                Set<String> set = new HashSet<>();
                for (MusicModel md : masterList)
                    set.add(md.getArtist());

                for (ArtistModel am : artistList) {
                    if (set.contains(am.getArtistName()))
                        sanitizedArtistLList.add(am);
                }
            }
        }
        return sanitizedArtistLList;
    }
}
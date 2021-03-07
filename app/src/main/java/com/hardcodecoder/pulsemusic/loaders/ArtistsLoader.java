package com.hardcodecoder.pulsemusic.loaders;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.model.ArtistModel;
import com.hardcodecoder.pulsemusic.model.MusicModel;

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
        String[] col = {MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS};

        final Cursor cursor = mContentResolver.query(
                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                col,
                null,
                null,
                mSortOrder);

        List<ArtistModel> sanitizedArtistLList = null;

        if (cursor != null && cursor.moveToFirst()) {
            int artistIdColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID);
            int artistColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST);
            int albumCountColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS);
            int trackCountColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS);

            List<ArtistModel> artistList = new ArrayList<>();
            do {
                int artistId = cursor.getInt(artistIdColumnIndex);
                String artist = cursor.getString(artistColumnIndex);
                int num_albums = cursor.getInt(albumCountColumnIndex);
                int num_tracks = cursor.getInt(trackCountColumnIndex);
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
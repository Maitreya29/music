package com.hardcodecoder.pulsemusic.loaders;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Audio.Albums;

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

    AlbumsLoader(@NonNull ContentResolver contentResolver,
                 @Nullable SortOrder.ALBUMS sortOrder,
                 @Nullable String selection,
                 @Nullable String[] args) {
        mContentResolver = contentResolver;
        mSortOrder = MediaStoreHelper.getSortOrderFor(sortOrder);
        mSelection = selection;
        mArgs = args;
    }

    @Override
    public List<AlbumModel> call() {
        String[] col = getColumns();

        final Cursor cursor = mContentResolver.query(
                Albums.EXTERNAL_CONTENT_URI,
                col,
                mSelection,
                mArgs,
                mSortOrder);

        List<AlbumModel> albumsList = null;

        if (cursor != null && cursor.moveToFirst()) {

            final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            // Make sure no album is returned that is present in the ignored folders list
            Set<Long> albumIdsToAccept = new HashSet<>();
            List<MusicModel> master = LoaderManager.getCachedMasterList();
            if (master != null && !master.isEmpty())
                for (MusicModel md : master) albumIdsToAccept.add(md.getAlbumId());

            albumsList = new ArrayList<>();
            do {
                long albumId = cursor.getLong(0);
                String album = cursor.getString(1);
                if (null == album || !albumIdsToAccept.contains(albumId)) continue;

                String albumArtist = cursor.getString(2);
                int numTracks = cursor.getInt(3);
                int firstYear = cursor.getInt(4);
                int lastYear = cursor.getInt(5);

                String albumArt = ContentUris.withAppendedId(sArtworkUri, albumId).toString();

                albumsList.add(new AlbumModel(
                        album,
                        albumId,
                        albumArtist == null ? "" : albumArtist,
                        numTracks,
                        firstYear,
                        lastYear,
                        albumArt));

            } while (cursor.moveToNext());
            cursor.close();
        }
        return albumsList;
    }

    @NonNull
    private String[] getColumns() {
        return new String[]{
                Albums._ID,                 // 0
                Albums.ALBUM,               // 1
                Albums.ARTIST,              // 2
                Albums.NUMBER_OF_SONGS,     // 3
                Albums.FIRST_YEAR,          // 4
                Albums.LAST_YEAR,           // 5
        };
    }
}
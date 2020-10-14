package com.hardcodecoder.pulsemusic.loaders;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import com.hardcodecoder.pulsemusic.model.AlbumModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ArtistTracksLoader implements Callable<List<AlbumModel>> {

    private final ContentResolver mContentResolver;
    private final String mSortOrder;
    private final long mArtistId;

    public ArtistTracksLoader(ContentResolver contentResolver, String artistName, SortOrder.ALBUMS sortOrder) {
        this.mContentResolver = contentResolver;
        this.mArtistId = getArtistIdFromName(artistName);
        mSortOrder = MediaStoreHelper.getSortOrderFor(sortOrder);
    }

    @Override
    public List<AlbumModel> call() {
        if (mArtistId == -1)
            return null;
        List<AlbumModel> albumsList = new ArrayList<>();
        String albumIdColName;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
            albumIdColName = BaseColumns._ID;
        else albumIdColName = MediaStore.Audio.Artists.Albums.ALBUM_ID;
        String[] col = {
                MediaStore.Audio.Artists.Albums.ALBUM,
                albumIdColName,
                MediaStore.Audio.Artists.Albums.ARTIST,
                MediaStore.Audio.Artists.Albums.NUMBER_OF_SONGS_FOR_ARTIST,
                MediaStore.Audio.Artists.Albums.FIRST_YEAR,
                MediaStore.Audio.Artists.Albums.LAST_YEAR};

        final Cursor cursor = mContentResolver.query(
                MediaStore.Audio.Artists.Albums.getContentUri("external", mArtistId),
                col,
                null,
                null,
                mSortOrder);

        if (cursor != null && cursor.moveToFirst()) {
            int albumIdColumnIndex = cursor.getColumnIndexOrThrow(albumIdColName);
            int albumColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.Albums.ALBUM);
            int albumCountColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.Albums.NUMBER_OF_SONGS_FOR_ARTIST);
            int albumFirstYearColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.Albums.FIRST_YEAR);
            int albumLastYearColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.Albums.LAST_YEAR);
            final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            do {
                int albumId = cursor.getInt(albumIdColumnIndex);
                String album = cursor.getString(albumColumnIndex);
                int num = cursor.getInt(albumCountColumnIndex);
                int firstYear = cursor.getInt(albumFirstYearColumnIndex);
                int lastYear = cursor.getInt(albumLastYearColumnIndex);
                String albumArtist = cursor.getString(albumIdColumnIndex);
                String albumArt = ContentUris.withAppendedId(sArtworkUri, albumId).toString();
                albumsList.add(new AlbumModel(album, albumId, albumArtist, num, firstYear, lastYear, albumArt));

            } while (cursor.moveToNext());
            cursor.close();
        }
        return albumsList;
    }

    private long getArtistIdFromName(String artist) {
        String[] cols = {MediaStore.Audio.Artists._ID};
        final Cursor artistIdCursor = mContentResolver.query(
                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                cols,
                MediaStore.Audio.Artists.ARTIST + "=?",
                new String[]{artist},
                null);

        long artistId = -1;
        if (null != artistIdCursor && artistIdCursor.moveToFirst()) {
            int colId = artistIdCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID);
            artistId = artistIdCursor.getLong(colId);
            artistIdCursor.close();
        }
        return artistId;
    }
}
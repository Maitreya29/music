package com.hardcodecoder.pulsemusic.loaders;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.providers.ProviderManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class LibraryLoader implements Callable<List<MusicModel>> {

    private final ContentResolver mContentResolver;
    private final String mSortOrder;
    private final String mSelectionString;
    private final String[] mSelectionArgs;

    LibraryLoader(ContentResolver contentResolver, SortOrder sortOrder) {
        this(contentResolver, sortOrder, null, null);
    }

    LibraryLoader(ContentResolver contentResolver, SortOrder sortOrder, @Nullable String selectionString, @Nullable String[] selectionArgs) {
        mContentResolver = contentResolver;
        mSortOrder = MediaStoreHelper.getSortOrderFor(sortOrder);
        List<String> ignoredFoldersList = ProviderManager.getIgnoredListProvider().getIgnoredList();
        if (null != ignoredFoldersList && !ignoredFoldersList.isEmpty()) {
            mSelectionString = getSelection(selectionString, ignoredFoldersList.size());
            mSelectionArgs = getSelectionArgs(selectionArgs, ignoredFoldersList);
        } else {
            mSelectionString = selectionString;
            mSelectionArgs = selectionArgs;
        }
    }

    @SuppressLint("InlinedApi")
    @Override
    public List<MusicModel> call() {
        List<MusicModel> libraryList = new ArrayList<>();
        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String[] cursor_cols = {
                MediaStore.Audio.Media._ID,             // 0
                MediaStore.Audio.Media.TITLE,           // 1
                MediaStore.Audio.Media.ALBUM,           // 2
                MediaStore.Audio.Media.ALBUM_ID,        // 3
                MediaStore.Audio.Media.ARTIST,          // 4
                MediaStore.Audio.Media.TRACK,           // 5
                MediaStore.Audio.Media.DATE_ADDED,      // 6
                MediaStore.Audio.Media.DATE_MODIFIED,   // 7
                MediaStore.Audio.AudioColumns.DURATION, // 8
        };

        final Cursor cursor = mContentResolver.query(
                uri,
                cursor_cols,
                mSelectionString,
                mSelectionArgs,
                mSortOrder);

        if (cursor != null && cursor.moveToFirst()) {
            final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

            do {
                int _id = cursor.getInt(0);
                String songName = cursor.getString(1);
                String album = cursor.getString(2);
                int albumId = cursor.getInt(3);
                String artist = cursor.getString(4);
                int trackNum = cursor.getInt(5);
                long dateAdded = cursor.getLong(6);
                long dateModified = cursor.getLong(7);
                int duration = cursor.getInt(8);

                String songPath = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, _id).toString();
                String albumArt = ContentUris.withAppendedId(sArtworkUri, albumId).toString();

                libraryList.add(new MusicModel(
                        _id,
                        songName,
                        album == null ? "" : album,
                        albumId,
                        artist == null ? "" : artist,
                        songPath,
                        albumArt,
                        dateAdded,
                        dateModified,
                        trackNum,
                        duration));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return libraryList;
    }

    private String getSelection(String selection, int numIgnoredFolders) {
        StringBuilder newSelection = new StringBuilder(
                selection == null || selection.trim().equals("") ? "" : selection + " AND ");
        newSelection.append(MediaStore.Audio.AudioColumns.DATA + " NOT LIKE ?");
        for (int i = 0; i < numIgnoredFolders - 1; i++)
            newSelection.append(" AND " + MediaStore.Audio.AudioColumns.DATA + " NOT LIKE ?");

        return newSelection.toString();
    }

    private String[] getSelectionArgs(String[] selectionArgs, List<String> ignoredFolders) {
        if (selectionArgs == null) selectionArgs = new String[0];
        String[] newSelectionValues = new String[selectionArgs.length + ignoredFolders.size()];
        System.arraycopy(selectionArgs, 0, newSelectionValues, 0, selectionArgs.length);
        for (int i = selectionArgs.length; i < newSelectionValues.length; i++)
            newSelectionValues[i] = ignoredFolders.get(i - selectionArgs.length) + "%";

        return newSelectionValues;
    }
}
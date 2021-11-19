package com.radiant.music.loaders;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Audio.Media;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.radiant.music.model.MusicModel;
import com.radiant.music.providers.ProviderManager;
import com.radiant.music.utils.AppSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class LibraryLoader implements Callable<List<MusicModel>> {

    private final ContentResolver mContentResolver;
    private final String mSortOrder;
    private String mSelectionString;
    private String[] mSelectionArgs;

    LibraryLoader(@NonNull Context context, @Nullable SortOrder sortOrder) {
        this(context, sortOrder, null, null);
    }

    LibraryLoader(@NonNull Context context, @Nullable SortOrder sortOrder, @Nullable String selectionString, @Nullable String[] selectionArgs) {
        mContentResolver = context.getContentResolver();
        mSortOrder = MediaStoreHelper.getSortOrderFor(sortOrder);
        prepareSelection(context, selectionString, selectionArgs);
    }

    @SuppressLint("InlinedApi")
    @Override
    public List<MusicModel> call() {
        List<MusicModel> libraryList = null;
        final Uri uri = Media.EXTERNAL_CONTENT_URI;
        final String[] cursor_cols = {
                Media._ID,             // 0
                Media.TITLE,           // 1
                Media.ALBUM,           // 2
                Media.ALBUM_ID,        // 3
                Media.ARTIST,          // 4
                Media.TRACK,           // 5
                Media.DATE_ADDED,      // 6
                Media.DATE_MODIFIED,   // 7
                Media.DURATION,        // 8
        };

        final Cursor cursor = mContentResolver.query(
                uri,
                cursor_cols,
                mSelectionString,
                mSelectionArgs,
                mSortOrder);

        if (cursor != null && cursor.moveToFirst()) {
            libraryList = new ArrayList<>();
            final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            do {
                int id = cursor.getInt(0);
                String songName = cursor.getString(1);
                String album = cursor.getString(2);
                long albumId = cursor.getLong(3);
                String artist = cursor.getString(4);
                int track = cursor.getInt(5);
                long dateAdded = cursor.getLong(6);
                long dateModified = cursor.getLong(7);
                int duration = cursor.getInt(8);


                String trackUri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, id).toString();
                String albumArt = ContentUris.withAppendedId(sArtworkUri, albumId).toString();

                libraryList.add(new MusicModel(
                        id,
                        songName,
                        album == null ? "" : album,
                        albumId,
                        artist == null ? "" : artist,
                        trackUri,
                        albumArt,
                        dateAdded,
                        dateModified,
                        track / 1000,
                        track % 1000,
                        duration));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return libraryList;
    }

    @SuppressLint("InlinedApi")
    private void prepareSelection(Context context, @Nullable String selection, @Nullable String[] selectionArgs) {
        StringBuilder completeSelection = new StringBuilder(
                selection == null || selection.trim().equals("") ? "" : selection + " AND ");

        // Append duration filter
        completeSelection.append(Media.DURATION).append(" >= ?");

        // Append ignored folders selection
        List<String> ignoredFoldersList = ProviderManager.getIgnoredListProvider().getIgnoredList();
        int ignoredListSize = (ignoredFoldersList == null) ? 0 : ignoredFoldersList.size();

        if (ignoredListSize > 0) {
            String ignoredFolderSelection = getIgnoreFolderSelection(ignoredListSize);
            completeSelection.append(ignoredFolderSelection);
        }

        if (selectionArgs == null) selectionArgs = new String[0];
        final int completeArgsLength = selectionArgs.length + 1 + ignoredListSize;
        String[] completeSelectionArgs = new String[completeArgsLength];

        // Copy the existing selection args
        System.arraycopy(selectionArgs, 0, completeSelectionArgs, 0, selectionArgs.length);

        // Copy args for duration filter
        int durationFilter = AppSettings.getFilterDuration(context);
        completeSelectionArgs[selectionArgs.length] = String.valueOf(durationFilter * 1000 /*Must be in mills*/);

        if (ignoredFoldersList != null && ignoredListSize > 0) {
            // Copy args for ignore folders
            String[] ignoredFolderArgs = getIgnoreFolderSelectionArgs(ignoredFoldersList);
            System.arraycopy(ignoredFolderArgs, 0, completeSelectionArgs, selectionArgs.length + 1, ignoredFolderArgs.length);
        }

        mSelectionString = completeSelection.toString();
        mSelectionArgs = completeSelectionArgs;
    }

    @NonNull
    private String getIgnoreFolderSelection(int size) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            builder.append(" AND ").append(Media.DATA).append(" NOT LIKE ?");
        }
        return builder.toString();
    }

    @NonNull
    private String[] getIgnoreFolderSelectionArgs(@NonNull List<String> list) {
        String[] selectionArgs = new String[list.size()];
        for (int i = 0; i < selectionArgs.length; i++)
            selectionArgs[i] = "%".concat(list.get(i)).concat("%");
        return selectionArgs;
    }
}
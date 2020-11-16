package com.hardcodecoder.pulsemusic.loaders;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.providers.ProviderManager;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class LibraryLoaderQ implements Callable<List<MusicModel>> {

    private final Context mContext;
    private final String mSortOrder;
    private final String mSelection;
    private final String[] mSelectionArgs;

    public LibraryLoaderQ(@NonNull Context context, @Nullable SortOrder sortOrder) {
        this(context, sortOrder, null, null);
    }

    public LibraryLoaderQ(@NonNull Context context, @Nullable SortOrder sortOrder, @Nullable String selection, @Nullable String[] selectionArgs) {
        mContext = context;
        mSortOrder = MediaStoreHelper.getSortOrderFor(sortOrder);
        mSelection = selection;
        mSelectionArgs = selectionArgs;
    }

    @Override
    public List<MusicModel> call() {
        List<MusicModel> libraryList = null;
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

        final Cursor cursor = mContext.getContentResolver().query(
                uri,
                cursor_cols,
                getInclusionSelection(),
                getInclusionSelectionArgs(),
                mSortOrder);

        if (cursor != null && cursor.moveToFirst()) {
            Set<Integer> excludeSet = getItemsToExclude();
            libraryList = new ArrayList<>();
            final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            do {
                int _id = cursor.getInt(0);

                // If exclude set contains this _id we will not include this in out list
                if (excludeSet != null && excludeSet.contains(_id)) continue;

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

    @NonNull
    private String getInclusionSelection() {
        // Append duration filter
        return (mSelection == null || mSelection.trim().equals("") ? "" : mSelection + " AND ")
                + MediaStore.Audio.Media.DURATION + " >= ?";
    }

    @NonNull
    private String[] getInclusionSelectionArgs() {
        final int completeArgsLength = (mSelectionArgs == null ? 0 : mSelectionArgs.length) + 1;
        String[] completeSelectionArgs = new String[completeArgsLength];

        if (mSelectionArgs != null) {
            // Copy the existing selection args
            System.arraycopy(mSelectionArgs, 0, completeSelectionArgs, 0, mSelectionArgs.length);
        }

        // Copy args for duration filter
        int durationFilter = AppSettings.getFilterDuration(mContext);
        completeSelectionArgs[completeArgsLength - 1] = String.valueOf(durationFilter * 1000 /*Must be in mills*/);
        return completeSelectionArgs;
    }

    @Nullable
    private Set<Integer> getItemsToExclude() {
        // Append ignored folders selection
        List<String> ignoredFoldersList = ProviderManager.getIgnoredListProvider().getIgnoredList();
        int ignoredListSize = (ignoredFoldersList == null) ? 0 : ignoredFoldersList.size();

        String excludeSelection = getExcludeSelection(ignoredListSize);
        String[] excludeSelectionArgs = null;
        if (ignoredFoldersList != null && ignoredListSize > 0)
            excludeSelectionArgs = getExcludeSelectionARgs(ignoredFoldersList);

        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String[] cursor_cols = {MediaStore.Audio.Media._ID};
        final Cursor cursor = mContext.getContentResolver().query(
                uri,
                cursor_cols,
                excludeSelection,
                excludeSelectionArgs,
                null);

        Set<Integer> excludeTracksId = null;

        if (cursor != null && cursor.moveToFirst()) {
            excludeTracksId = new HashSet<>();
            do {
                excludeTracksId.add(cursor.getInt(0));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return excludeTracksId;
    }

    @NonNull
    private String getExcludeSelection(int ignoredListSize) {
        // Prepare selection
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("((")
                .append(MediaStore.Audio.Media.VOLUME_NAME).append(" LIKE ?")
                .append(" AND ")
                .append(MediaStore.Audio.Media.RELATIVE_PATH).append(" LIKE ?")
                .append(")");
        for (int i = 0; i < ignoredListSize - 1; i++) {
            stringBuilder
                    .append(" OR ")
                    .append("(")
                    .append(MediaStore.Audio.Media.VOLUME_NAME).append(" LIKE ?")
                    .append(" AND ")
                    .append(MediaStore.Audio.Media.RELATIVE_PATH).append(" LIKE ?")
                    .append(")");
        }
        return stringBuilder.append(")").toString();
    }

    @NonNull
    private String[] getExcludeSelectionARgs(@NonNull List<String> ignoredFolders) {
        String[] selectionArgs = new String[2 * ignoredFolders.size()];
        int section = 0;
        for (int i = 0; i < ignoredFolders.size(); i++) {
            String path = ignoredFolders.get(i);
            String[] segments = path.split(":");                            // complete path is in the form primary_external:Dir/Dir/Dir/
            selectionArgs[section++] = segments[0];                              // Volume name
            // We ignore every sub directory inside
            // the directory this relative path points to
            selectionArgs[section++] = "%".concat(segments[1]).concat("%");      // Relative path
        }
        return selectionArgs;
    }
}
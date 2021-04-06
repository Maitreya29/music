package com.nezukoos.music.loaders;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore.Audio.Media;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.nezukoos.music.model.MusicModel;
import com.nezukoos.music.providers.ProviderManager;
import com.nezukoos.music.utils.AppSettings;

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
    private final boolean mIsAndroidQ;

    public LibraryLoaderQ(@NonNull Context context, @Nullable SortOrder sortOrder) {
        this(context, sortOrder, null, null);
    }

    public LibraryLoaderQ(@NonNull Context context, @Nullable SortOrder sortOrder, @Nullable String selection, @Nullable String[] selectionArgs) {
        mContext = context;
        mSortOrder = MediaStoreHelper.getSortOrderFor(sortOrder);
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mIsAndroidQ = Build.VERSION.SDK_INT == Build.VERSION_CODES.Q;
    }

    @Override
    public List<MusicModel> call() {
        List<MusicModel> libraryList = null;
        final Uri uri = Media.EXTERNAL_CONTENT_URI;
        final String[] cursor_cols = getCursorColumns();

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
                int id = cursor.getInt(0);

                // If exclude set contains this id, we will not include this in out list
                if (excludeSet != null && excludeSet.contains(id)) continue;

                String songName;
                String album;
                String artist;
                long albumId;
                long dateAdded;
                long dateModified;
                int[] discTrackNumber;
                int duration;

                if (mIsAndroidQ) {
                    songName = cursor.getString(1);
                    album = cursor.getString(2);
                    albumId = cursor.getLong(3);
                    artist = cursor.getString(4);
                    int track = cursor.getInt(5);
                    discTrackNumber = new int[]{track / 1000 /*Disc number*/, track % 1000 /*Track number*/};
                    dateAdded = cursor.getLong(6);
                    dateModified = cursor.getLong(7);
                    duration = cursor.getInt(8);
                } else {
                    songName = cursor.getString(1);
                    album = cursor.getString(2);
                    albumId = cursor.getLong(3);
                    artist = cursor.getString(4);
                    discTrackNumber = new int[]{cursor.getInt(5), cursor.getInt(6)};
                    dateAdded = cursor.getLong(7);
                    dateModified = cursor.getLong(8);
                    duration = cursor.getInt(9);
                }

                String trackUri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, id).toString();
                String albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId).toString();

                libraryList.add(new MusicModel(
                        id,
                        songName,
                        album == null ? "" : album,
                        albumId,
                        artist == null ? "" : artist,
                        trackUri,
                        albumArtUri,
                        dateAdded,
                        dateModified,
                        discTrackNumber[0],
                        discTrackNumber[1],
                        duration));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return libraryList;
    }

    @SuppressLint("InlinedApi")
    @NonNull
    private String[] getCursorColumns() {
        if (mIsAndroidQ) {
            return new String[]{
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
        } else {
            return new String[]{
                    Media._ID,             // 0
                    Media.TITLE,           // 1
                    Media.ALBUM,           // 2
                    Media.ALBUM_ID,        // 3
                    Media.ARTIST,          // 4
                    Media.DISC_NUMBER,     // 5
                    Media.CD_TRACK_NUMBER, // 6
                    Media.DATE_ADDED,      // 7
                    Media.DATE_MODIFIED,   // 8
                    Media.DURATION,        // 9
            };
        }
    }

    @NonNull
    private String getInclusionSelection() {
        // Append duration filter
        return (mSelection == null || mSelection.trim().equals("") ? "" : mSelection + " AND ")
                + Media.DURATION + " >= ?";
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

        final Uri uri = Media.EXTERNAL_CONTENT_URI;
        final String[] cursor_cols = {Media._ID};
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
                .append(Media.VOLUME_NAME).append(" LIKE ?")
                .append(" AND ")
                .append(Media.RELATIVE_PATH).append(" LIKE ?")
                .append(")");
        for (int i = 0; i < ignoredListSize - 1; i++) {
            stringBuilder
                    .append(" OR ")
                    .append("(")
                    .append(Media.VOLUME_NAME).append(" LIKE ?")
                    .append(" AND ")
                    .append(Media.RELATIVE_PATH).append(" LIKE ?")
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
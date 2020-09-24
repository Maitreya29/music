package com.hardcodecoder.pulsemusic.loaders;

import android.annotation.SuppressLint;
import android.provider.MediaStore;

public class MediaStoreHelper {

    // MediaStore.Audio.Media.DURATION existed well before APi 29
    // Suppress lint
    @SuppressLint("InlinedApi")
    public static String getSortOrderFor(SortOrder sortOrder) {
        switch (sortOrder) {
            case TITLE_ASC:
                return MediaStore.Audio.Media.TITLE + " COLLATE NOCASE ASC";
            case TITLE_DESC:
                return MediaStore.Audio.Media.TITLE + " COLLATE NOCASE DESC";
            case DURATION_ASC:
                return MediaStore.Audio.Media.DURATION + " ASC";
            case DURATION_DESC:
                return MediaStore.Audio.Media.DURATION + " DESC";
            case DATE_MODIFIED_ASC:
                return MediaStore.Audio.Media.DATE_MODIFIED + " ASC";
            case DATE_MODIFIED_DESC:
                return MediaStore.Audio.Media.DATE_MODIFIED + " DESC";
            case TRACK_NUMBER_ASC:
                return MediaStore.Audio.Media.TRACK + " ASC";
            case TRACK_NUMBER_DESC:
                return MediaStore.Audio.Media.TRACK + " DESC";
            default:
                return null;
        }
    }

    public static String getSortOrderFor(SortOrder.ALBUMS sortOrder) {
        switch (sortOrder) {
            case TITLE_ASC:
                return MediaStore.Audio.Albums.ALBUM + " COLLATE NOCASE ASC";
            case TITLE_DESC:
                return MediaStore.Audio.Albums.ALBUM + " COLLATE NOCASE DESC";
            case ARTIST_ASC:
                return MediaStore.Audio.Albums.ARTIST + " COLLATE NOCASE ASC";
            case ARTIST_DESC:
                return MediaStore.Audio.Albums.ARTIST + " COLLATE NOCASE DESC";
            case ALBUM_DATE_FIRST_YEAR_ASC:
                return MediaStore.Audio.Albums.FIRST_YEAR + " ASC, " + MediaStore.Audio.Albums.ALBUM + " COLLATE NOCASE ASC";
            case ALBUM_DATE_FIRST_YEAR_DESC:
                return MediaStore.Audio.Albums.FIRST_YEAR + " DESC, " + MediaStore.Audio.Albums.ALBUM + " COLLATE NOCASE ASC";
            case ALBUM_DATE_LAST_YEAR_ASC:
                return MediaStore.Audio.Albums.LAST_YEAR + " ASC";
            case ALBUM_DATE_LAST_YEAR_DESC:
                return MediaStore.Audio.Albums.LAST_YEAR + " DESC";
            default:
                return null;
        }
    }

    public static String getSortOrderFor(SortOrder.ARTIST sortOrder) {
        switch (sortOrder) {
            case TITLE_ASC:
                return MediaStore.Audio.Artists.ARTIST + " COLLATE NOCASE ASC";
            case TITLE_DESC:
                return MediaStore.Audio.Artists.ARTIST + " COLLATE NOCASE DESC";
            case NUM_OF_TRACKS_ASC:
                return MediaStore.Audio.Artists.NUMBER_OF_TRACKS + " ASC";
            case NUM_OF_TRACKS_DESC:
                return MediaStore.Audio.Artists.NUMBER_OF_TRACKS + " DESC";
            default:
                return null;
        }
    }
}
package com.radiant.music.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.radiant.music.loaders.SortOrder;
import com.radiant.music.loaders.SortOrder.ALBUMS;
import com.radiant.music.loaders.SortOrder.ARTIST;
import com.radiant.music.model.AlbumModel;
import com.radiant.music.model.ArtistModel;
import com.radiant.music.model.MusicModel;

import java.util.Collections;
import java.util.List;

public class SortUtil {

    public static void sortLibraryList(@NonNull List<MusicModel> list, @Nullable SortOrder sortOrder) {
        if (null == sortOrder) sortOrder = SortOrder.TITLE_ASC;
        switch (sortOrder) {
            case TITLE_DESC:
                Collections.sort(list, (o1, o2) -> o2.getTrackName().compareToIgnoreCase(o1.getTrackName()));
                break;
            case DURATION_ASC:
                Collections.sort(list, (o1, o2) -> Integer.compare(o1.getTrackDuration(), o2.getTrackDuration()));
                break;
            case DURATION_DESC:
                Collections.sort(list, (o1, o2) -> Integer.compare(o2.getTrackDuration(), o1.getTrackDuration()));
                break;
            case DATE_ADDED_ASC:
                Collections.sort(list, (o1, o2) -> Long.compare(o1.getDateAdded(), o2.getDateAdded()));
                break;
            case DATE_ADDED_DESC:
                Collections.sort(list, (o1, o2) -> Long.compare(o2.getDateAdded(), o1.getDateAdded()));
                break;
            case DATE_MODIFIED_ASC:
                Collections.sort(list, (o1, o2) -> Long.compare(o1.getDateModified(), o2.getDateModified()));
                break;
            case DATE_MODIFIED_DESC:
                Collections.sort(list, (o1, o2) -> Long.compare(o2.getDateModified(), o1.getDateModified()));
                break;
            case TRACK_NUMBER_ASC:
                Collections.sort(list, (o1, o2) -> {
                    int disc = Integer.compare(o1.getDiscNumber(), o2.getDiscNumber());
                    if (disc == 0) return Integer.compare(o1.getTrackNumber(), o2.getTrackNumber());
                    return disc;
                });
                break;
            case TRACK_NUMBER_DESC:
                Collections.sort(list, (o1, o2) -> {
                    int disc = Integer.compare(o2.getDiscNumber(), o1.getDiscNumber());

                    if (disc == 0) return Integer.compare(o2.getTrackNumber(), o1.getTrackNumber());
                    return disc;
                });
                break;
            case TITLE_ASC:
            default:
                Collections.sort(list, (o1, o2) -> o1.getTrackName().compareToIgnoreCase(o2.getTrackName()));
                break;
        }
    }

    public static void sortArtistList(@NonNull List<ArtistModel> list, @Nullable ARTIST sortOrder) {
        if (null == sortOrder) sortOrder = ARTIST.TITLE_ASC;
        switch (sortOrder) {
            case TITLE_DESC:
                Collections.sort(list, (o1, o2) -> o2.getArtistName().compareToIgnoreCase(o1.getArtistName()));
                break;
            case NUM_OF_TRACKS_ASC:
                Collections.sort(list, (o1, o2) -> o1.getNumOfTracks() - o2.getNumOfTracks());
                break;
            case NUM_OF_TRACKS_DESC:
                Collections.sort(list, (o1, o2) -> o2.getNumOfTracks() - o1.getNumOfTracks());
                break;
            case TITLE_ASC:
            default:
                Collections.sort(list, (o1, o2) -> o1.getArtistName().compareToIgnoreCase(o2.getArtistName()));
                break;
        }
    }

    public static void sortAlbumList(@NonNull List<AlbumModel> list, @Nullable ALBUMS sortOrder) {
        if (null == sortOrder) sortOrder = ALBUMS.TITLE_ASC;
        switch (sortOrder) {
            case TITLE_DESC:
                Collections.sort(list, (o1, o2) -> o2.getAlbumName().compareToIgnoreCase(o1.getAlbumName()));
                break;
            case ARTIST_ASC:
                Collections.sort(list, (o1, o2) -> o1.getAlbumArtist().compareToIgnoreCase(o2.getAlbumArtist()));
                break;
            case ARTIST_DESC:
                Collections.sort(list, (o1, o2) -> o2.getAlbumArtist().compareToIgnoreCase(o1.getAlbumArtist()));
                break;
            case ALBUM_DATE_FIRST_YEAR_ASC:
                Collections.sort(list, (o1, o2) -> {
                    int diff = Integer.compare(o1.getFirstYear(), o2.getFirstYear());
                    // If album year is the same, sort alphabetically
                    if (diff == 0)
                        return o1.getAlbumName().compareToIgnoreCase(o2.getAlbumName());
                    return diff;
                });
                break;
            case ALBUM_DATE_FIRST_YEAR_DESC:
                Collections.sort(list, (o1, o2) -> {
                    int diff = Integer.compare(o2.getFirstYear(), o1.getFirstYear());
                    // If album year is the same, sort alphabetically
                    if (diff == 0)
                        return o1.getAlbumName().compareToIgnoreCase(o2.getAlbumName());
                    return diff;
                });
                break;
            case TITLE_ASC:
            default:
                Collections.sort(list, (o1, o2) -> o1.getAlbumName().compareToIgnoreCase(o2.getAlbumName()));
                break;
        }
    }
}
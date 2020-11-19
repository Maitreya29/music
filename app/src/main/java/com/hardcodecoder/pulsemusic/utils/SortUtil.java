package com.hardcodecoder.pulsemusic.utils;

import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.loaders.SortOrder;
import com.hardcodecoder.pulsemusic.loaders.SortOrder.ALBUMS;
import com.hardcodecoder.pulsemusic.loaders.SortOrder.ARTIST;
import com.hardcodecoder.pulsemusic.model.AlbumModel;
import com.hardcodecoder.pulsemusic.model.ArtistModel;
import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.util.Collections;
import java.util.List;

public class SortUtil {

    public static void sortLibraryList(@NonNull List<MusicModel> list, @NonNull SortOrder sortOrder) {
        switch (sortOrder) {
            case TITLE_ASC:
                Collections.sort(list, (o1, o2) -> o1.getTrackName().compareToIgnoreCase(o2.getTrackName()));
                break;
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
                    int[] discTrack1 = getDiscTrackNumber(o1.getTrackNumber());
                    int[] discTrack2 = getDiscTrackNumber(o2.getTrackNumber());

                    int disc = Integer.compare(discTrack1[0], discTrack2[0]);

                    if (disc == 0) return Integer.compare(discTrack1[1], discTrack2[1]);
                    return disc;
                });
                break;
            case TRACK_NUMBER_DESC:
                Collections.sort(list, (o1, o2) -> {
                    int[] discTrack1 = getDiscTrackNumber(o1.getTrackNumber());
                    int[] discTrack2 = getDiscTrackNumber(o2.getTrackNumber());

                    int disc = Integer.compare(discTrack2[0], discTrack1[0]);

                    if (disc == 0) return Integer.compare(discTrack2[1], discTrack1[1]);
                    return disc;
                });
                break;
        }
    }

    @NonNull
    private static int[] getDiscTrackNumber(int track) {
        String trackString = String.valueOf(track);
        int[] discTrackNumber = new int[]{0, track}; // Disc number, track number
        if (trackString != null && trackString.length() == 4) {
            // For multi-disc sets, track will be 1xxx for tracks on the first disc,
            // 2xxx for tracks on the second disc, etc.
            discTrackNumber[0] = Character.getNumericValue(trackString.charAt(0));
            // Track number is the number xxx
            discTrackNumber[1] = Integer.parseInt(trackString.substring(1));
        }
        return discTrackNumber;
    }

    public static void sortArtistList(List<ArtistModel> list, ARTIST sortOrder) {
        if (sortOrder == ARTIST.TITLE_ASC)
            Collections.sort(list, (o1, o2) -> o1.getArtistName().compareToIgnoreCase(o2.getArtistName()));
        else if (sortOrder == ARTIST.TITLE_DESC) {
            Collections.sort(list, (o1, o2) -> o2.getArtistName().compareToIgnoreCase(o1.getArtistName()));
        } else if (sortOrder == ARTIST.NUM_OF_TRACKS_ASC) {
            Collections.sort(list, (o1, o2) -> o1.getNumOfTracks() - o2.getNumOfTracks());
        } else if (sortOrder == ARTIST.NUM_OF_TRACKS_DESC) {
            Collections.sort(list, (o1, o2) -> o2.getNumOfTracks() - o1.getNumOfTracks());
        }
    }

    public static void sortAlbumList(List<AlbumModel> list, @NonNull ALBUMS sortOrder) {
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
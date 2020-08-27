package com.hardcodecoder.pulsemusic.utils;

import android.util.Log;

import com.hardcodecoder.pulsemusic.loaders.SortOrder;
import com.hardcodecoder.pulsemusic.loaders.SortOrder.ALBUMS;
import com.hardcodecoder.pulsemusic.loaders.SortOrder.ARTIST;
import com.hardcodecoder.pulsemusic.model.AlbumModel;
import com.hardcodecoder.pulsemusic.model.ArtistModel;
import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.util.Collections;
import java.util.List;

public class SortUtil {

    public static List<MusicModel> sortLibraryList(List<MusicModel> list, SortOrder sortOrder) {
        Log.e("NEW SORT ORDER", sortOrder.toString());
        if (sortOrder == SortOrder.TITLE_ASC)
            Collections.sort(list, (o1, o2) -> o1.getTrackName().compareToIgnoreCase(o2.getTrackName()));
        else if (sortOrder == SortOrder.TITLE_DESC)
            Collections.sort(list, (o1, o2) -> o2.getTrackName().compareToIgnoreCase(o1.getTrackName()));
        else if (sortOrder == SortOrder.DURATION_ASC)
            Collections.sort(list, (o1, o2) -> Integer.compare(o1.getTrackDuration(), o2.getTrackDuration()));
        else if (sortOrder == SortOrder.DURATION_DESC)
            Collections.sort(list, (o1, o2) -> Integer.compare(o2.getTrackDuration(), o1.getTrackDuration()));
        return list;
    }

    public static List<ArtistModel> sortArtistList(List<ArtistModel> list, ARTIST sortOrder) {
        if (sortOrder == ARTIST.TITLE_ASC)
            Collections.sort(list, (o1, o2) -> o1.getArtistName().compareToIgnoreCase(o2.getArtistName()));
        else if (sortOrder == ARTIST.TITLE_DESC) {
            Collections.sort(list, (o1, o2) -> o2.getArtistName().compareToIgnoreCase(o1.getArtistName()));
        } else if (sortOrder == ARTIST.NUM_OF_TRACKS_ASC) {
            Collections.sort(list, (o1, o2) -> o1.getNumOfTracks() - o2.getNumOfTracks());
        } else if (sortOrder == ARTIST.NUM_OF_TRACKS_DESC) {
            Collections.sort(list, (o1, o2) -> o2.getNumOfTracks() - o1.getNumOfTracks());
        }
        return list;
    }

    public static List<AlbumModel> sortAlbumList(List<AlbumModel> list, ALBUMS sortOrder) {
        if (sortOrder == ALBUMS.TITLE_ASC)
            Collections.sort(list, (o1, o2) -> o1.getAlbumName().compareToIgnoreCase(o2.getAlbumName()));
        else if (sortOrder == ALBUMS.TITLE_DESC) {
            Collections.sort(list, (o1, o2) -> o2.getAlbumName().compareToIgnoreCase(o1.getAlbumName()));
        }
        return list;
    }
}

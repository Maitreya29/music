package com.nezukoos.music.loaders;

public enum SortOrder {

    TITLE_ASC,
    TITLE_DESC,
    DURATION_ASC,
    DURATION_DESC,
    DATE_ADDED_ASC,
    DATE_ADDED_DESC,
    DATE_MODIFIED_ASC,
    DATE_MODIFIED_DESC,
    TRACK_NUMBER_ASC,
    TRACK_NUMBER_DESC;

    public enum ALBUMS {
        TITLE_ASC,
        TITLE_DESC,
        ARTIST_ASC,
        ARTIST_DESC,
        ALBUM_DATE_FIRST_YEAR_ASC,
        ALBUM_DATE_FIRST_YEAR_DESC,
        ALBUM_DATE_LAST_YEAR_ASC,
        ALBUM_DATE_LAST_YEAR_DESC,
    }

    public enum ARTIST {
        TITLE_ASC,
        TITLE_DESC,
        NUM_OF_TRACKS_ASC,
        NUM_OF_TRACKS_DESC,
    }
}

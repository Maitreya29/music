package com.hardcodecoder.pulsemusic;

public class Preferences {

    public static final String FIRST_RUN = "FirstRun";

    /* Constants for maintaining sort order in all possible places */
    public static final String SORT_ORDER_PREFS_KEY = "SortOrder";
    public static final String SORT_ORDER_LIBRARY_KEY = "LibrarySortOrder";
    public static final String SORT_ORDER_ALBUMS_KEY = "AlbumsSortOrder";
    public static final String SORT_ORDER_ALBUM_DETAILS_KEY = "AlbumDetailsSortOrder";
    public static final String SORT_ORDER_ARTIST_DETAILS_KEY = "ArtistDetailsSortOrder";
    public static final String SORT_ORDER_ARTIST_KEY = "ArtistsSortOrder";

    // Id's to identify the sort by menu to show for a given screen
    public static final int MENU_GROUP_TYPE_SORT = 2500;
    public static final short SORT_ORDER_GROUP_LIBRARY = 2501;
    public static final short SORT_ORDER_GROUP_ALBUMS = 2502;
    public static final short SORT_ORDER_GROUP_ARTISTS = 2503;
    public static final short SORT_ORDER_GROUP_ALBUMS_DETAILS = 2504;
    public static final short SORT_ORDER_GROUP_ARTISTS_DETAILS = 2505;

    // Short order id's (In range 3000 - 3100)
    public static final int SORT_ORDER_ASC = 3000;
    public static final int SORT_ORDER_DESC = 3001;
    public static final int SORT_ORDER_DURATION_ASC = 3002;
    public static final int SORT_ORDER_DURATION_DESC = 3003;
    public static final int SORT_ORDER_DATE_ADDED_ASC = 3004;
    public static final int SORT_ORDER_DATE_ADDED_DESC = 3005;
    public static final int SORT_ORDER_DATE_MODIFIED_ASC = 3006;
    public static final int SORT_ORDER_DATE_MODIFIED_DESC = 3007;
    public static final int SORT_ORDER_ALBUM_TRACK_NUMBER_ASC = 3008;
    public static final int SORT_ORDER_ALBUM_TRACK_NUMBER_DESC = 3009;
    public static final int SORT_ORDER_ALBUM_ARTIST_ASC = 3010;
    public static final int SORT_ORDER_ALBUM_ARTIST_DESC = 3011;
    public static final int SORT_ORDER_ALBUM_FIRST_YEAR_ASC = 3012;
    public static final int SORT_ORDER_ALBUM_FIRST_YEAR_DESC = 3013;

    /* Constants for maintaining column count in all possible places */
    public static final String COLUMN_COUNT = "ColumnCount";
    public static final String COLUMN_COUNT_LIBRARY_PORTRAIT_KEY = "ColumnCountLibraryPortrait";
    public static final String COLUMN_COUNT_LIBRARY_LANDSCAPE_KEY = "ColumnCountLibraryLandscape";
    public static final String COLUMN_COUNT_ALBUMS_PORTRAIT_KEY = "ColumnCountAlbumsPortrait";
    public static final String COLUMN_COUNT_ALBUMS_LANDSCAPE_KEY = "ColumnCountAlbumsLandscape";
    public static final String COLUMN_COUNT_ARTISTS_PORTRAIT_KEY = "ColumnCountArtistsPortrait";
    public static final String COLUMN_COUNT_ARTISTS_LANDSCAPE_KEY = "ColumnCountArtistsLandscape";

    // Id's to identify the column count menu to show for a given screen
    public static final int MENU_GROUP_TYPE_COLUMN_COUNT = 3500;
    public static final short COLUMN_COUNT_GROUP_LIBRARY = 3501;
    public static final short COLUMN_COUNT_GROUP_ALBUMS = 3502;
    public static final short COLUMN_COUNT_GROUP_ARTISTS = 3503;

    // Column count number (1-6 is currently used, 6 being the max columns supported)
    public static final short COLUMN_COUNT_ONE = 1;
    public static final short COLUMN_COUNT_TWO = 2;
    public static final short COLUMN_COUNT_THREE = 3;
    public static final short COLUMN_COUNT_FOUR = 4;
    public static final short COLUMN_COUNT_FIVE = 5;
    public static final short COLUMN_COUNT_SIX = 6;

    public static final int MENU_GROUP_TYPE_CREATE_PLAYLIST = 4000;


    /* Constants for maintaining themes and accents */
    public static final String PULSE_THEMES_PREFS = "PulseThemes";
    public static final String UI_MODE_AUTO_KEY = "AutoThemeEnabled";
    public static final String UI_THEME_DARK_KEY = "DarkModeEnabled";

    public static final short LIGHT_THEME = 515;

    public static final String DARK_THEME_CATEGORY_KEY = "DarkThemeId";
    public static final short DARK_THEME_GRAY = 616;
    public static final short DARK_THEME_KINDA = 626;
    public static final short DARK_THEME_PURE_BLACK = 636;

    public static final String ACCENTS_MODE_USING_PRESET_KEY = "UsingPresetColors";
    public static final String ACCENTS_COLOR_PRESET_KEY = "AccentsColorPresetId";
    public static final String ACCENTS_COLOR_CUSTOM_KEY = "AccentsColorCustom";
    public static final String ACCENTS_COLOR_DESATURATED_KEY = "AccentsColorDesaturated";

    public static final short ACCENT_SLATE_BLUE = 700;
    public static final short ACCENT_AZURE_RADIANCE = 701;
    public static final short ACCENT_PERSIAN_MINT = 702;
    public static final short ACCENT_VALENCIA = 703;
    public static final short ACCENT_MILLBROOK = 704;
    public static final short ACCENT_CORAL = 705;
    public static final short ACCENT_SUNKIST = 706;
    public static final short ACCENT_CORNFLOUR_BLUE = 707;

    // In range 5000 - 5100
    public static final String NOW_PLAYING_SCREEN_STYLE_KEY = "NowPlayingScreenStyleKey";
    public static final int NOW_PLAYING_SCREEN_MODERN = 5000;
    public static final int NOW_PLAYING_SCREEN_STYLISH = 5001;
    public static final int NOW_PLAYING_SCREEN_EDGE = 5002;

    public static final String NOW_PLAYING_ALBUM_COVER_CORNER_RADIUS = "NowPlayingAlbumCoverCornerRadius";
    public static final String NOW_PLAYING_ALBUM_COVER_RADIUS_TL = "RadiusTopLeft";
    public static final String NOW_PLAYING_ALBUM_COVER_RADIUS_TR = "RadiusTopRight";
    public static final String NOW_PLAYING_ALBUM_COVER_RADIUS_BL = "RadiusBottomLeft";
    public static final String NOW_PLAYING_ALBUM_COVER_RADIUS_BR = "RadiusBottomRight";
    public static final int NOW_PLAYING_ALBUM_COVER_RADIUS_DEF = 16;

    public static final String NOW_PLAYING_CONTROLS = "NowPlayingControls";
    public static final String NOW_PLAYING_CONTROLS_SEEK_ENABLED = "SeekButtonsEnabled";
    public static final boolean NOW_PLAYING_CONTROLS_SEEK_ENABLED_DEF = false;
    public static final String NOW_PLAYING_SEEK_DURATION_FORWARD = "SeekForward";
    public static final String NOW_PLAYING_SEEK_DURATION_BACKWARD = "SeekBackward";
    public static final int NOW_PLAYING_SEEK_DURATION_DEF = 10; // 10 seconds


    public static final String AUDIO_DEVICE_AUTO_PLAY = "AudioDeviceAutoPlay";
    public static final String BLUETOOTH_DEVICE_DETECTION_KEY = "BluetoothDetectionEnabled";
    public static final String BLUETOOTH_DEVICE_ACTION_KEY = "BluetoothDeviceAction";

    public static final short ACTION_PLAY_SHUFFLE = 6000;
    public static final short ACTION_PLAY_SUGGESTED = 6001;
    public static final short ACTION_PLAY_LATEST = 6002;

    public static final String APP_SHORTCUT_THEME = "AppShortCutThemeMode";
    public static final String APP_SHORTCUT_THEME_MODE = "DarkThemeInUse";

    public static final String GENERAL_SETTINGS_PREF = "GeneralSettings";
    public static final String FILTER_DURATION = "FilteredDuration";
    public static final String REMEMBER_LAST_TRACK = "RememberLastTrack";
    public static final String LAST_TRACK_ID = "LastTrackId";
    public static final String LAST_TRACK_POSITION = "LastTrackPosition";

    public static final String HOME_PLAYLIST_SECTIONS = "HomePlaylistSections";
    public static final String HOME_PLAYLIST_TOP_ALBUMS = "HomePlaylistTopAlbums";
    public static final String HOME_PLAYLIST_FOR_YOU = "HomePlaylistForYou";
    public static final String HOME_PLAYLIST_REDISCOVER = "HomePlaylistRediscover";
    public static final String HOME_PLAYLIST_NEW_IN_LIBRARY = "HomePlaylistNewInLibrary";
    public static final String HOME_PLAYLIST_TOP_ARTIST = "HomePlaylistTopArtist";
}
package com.hardcodecoder.pulsemusic;

public final class Preferences {

    // Global Constants for auto play actions
    public static final short ACTION_PLAY_SHUFFLE = 6000;
    public static final short ACTION_PLAY_SUGGESTED = 6001;
    public static final short ACTION_PLAY_LATEST = 6002;
    public static final short ACTION_PLAY_CONTINUE = 6003;

    /* ***********************************************************
     * **************** General UI preferences *******************
     * *********************************************************** */

    // Shared Preference for storing first run
    public static final String PREF_FIRST_RUN = "FirstRun";
    public static final String KEY_FIRST_RUN = "IsFirstRun";

    // Shared Preference for sort order preferences
    public static final String PREF_SORT_ORDER = "SortOrder";
    // Keys for sort order preferences
    public static final String SORT_ORDER_LIBRARY_KEY = "LibrarySortOrder";
    public static final String SORT_ORDER_ALBUMS_KEY = "AlbumsSortOrder";
    public static final String SORT_ORDER_ARTIST_KEY = "ArtistsSortOrder";
    public static final String SORT_ORDER_ALBUM_DETAILS_KEY = "AlbumDetailsSortOrder";
    public static final String SORT_ORDER_ARTIST_DETAILS_KEY = "ArtistDetailsSortOrder";

    // Shared Preference for column count preferences
    public static final String PREF_COLUMN_COUNT = "ColumnCount";
    // Keys for column count preferences
    public static final String COLUMN_COUNT_LIBRARY_PORTRAIT_KEY = "ColumnCountLibraryPortrait";
    public static final String COLUMN_COUNT_LIBRARY_LANDSCAPE_KEY = "ColumnCountLibraryLandscape";
    public static final String COLUMN_COUNT_ALBUMS_PORTRAIT_KEY = "ColumnCountAlbumsPortrait";
    public static final String COLUMN_COUNT_ALBUMS_LANDSCAPE_KEY = "ColumnCountAlbumsLandscape";
    public static final String COLUMN_COUNT_ARTISTS_PORTRAIT_KEY = "ColumnCountArtistsPortrait";
    public static final String COLUMN_COUNT_ARTISTS_LANDSCAPE_KEY = "ColumnCountArtistsLandscape";

    // Flag to indicate that this menu supplies column count
    public static final int MENU_GROUP_TYPE_SORT = 2500;
    public static final int MENU_GROUP_TYPE_COLUMN_COUNT = 3500;
    public static final int MENU_GROUP_TYPE_CREATE_PLAYLIST = 4000;

    // Id's to identify the "sort by" menu group
    public static final short SORT_ORDER_GROUP_LIBRARY = 2501;
    public static final short SORT_ORDER_GROUP_ALBUMS = 2502;
    public static final short SORT_ORDER_GROUP_ARTISTS = 2503;
    public static final short SORT_ORDER_GROUP_ALBUMS_DETAILS = 2504;
    public static final short SORT_ORDER_GROUP_ARTISTS_DETAILS = 2505;

    // Id's to identify the "column count" menu group
    public static final short COLUMN_COUNT_GROUP_LIBRARY = 3501;
    public static final short COLUMN_COUNT_GROUP_ALBUMS = 3502;
    public static final short COLUMN_COUNT_GROUP_ARTISTS = 3503;

    // Global constants for storing sort order (Keep in range 3000 - 3100)
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

    // Global values for storing column count
    // (1 - 6 is currently used, 6 being the max columns supported)
    public static final short COLUMN_COUNT_ONE = 1;
    public static final short COLUMN_COUNT_TWO = 2;
    public static final short COLUMN_COUNT_THREE = 3;
    public static final short COLUMN_COUNT_FOUR = 4;
    public static final short COLUMN_COUNT_FIVE = 5;
    public static final short COLUMN_COUNT_SIX = 6;



    /* ***********************************************************
     * ******* Preferences specific to the Settings Page *********
     * *********************************************************** */

    /* *********************** GENERAL SECTION ********************* */
    // Shared preference for general section
    public static final String PREF_GENERAL = "GeneralSettings";
    // Key for storing filter duration
    public static final String KEY_FILTER_DURATION = "FilterDuration";
    // Key to check whether remember playlist is enabled
    public static final String KEY_REMEMBER_PREVIOUS_PLAYLIST = "RememberPreviousPlaylist";
    // Key for storing the active track index in the playlist
    public static final String KEY_PREVIOUS_PLAYLIST_TRACK_INDEX = "PlaylistTrackIndex";
    // Key for storing track position
    public static final String KEY_PREVIOUS_PLAYLIST_TRACK_POSITION = "PlaylistTrackPosition";
    // Shared preference for playlist sections in home page
    public static final String PREF_HOME_PLAYLIST_SECTIONS = "HomePlaylistSections";
    // Keys that determines whether the following section is enabled
    public static final String KEY_HOME_PLAYLIST_TOP_ALBUMS = "HomePlaylistTopAlbums";
    public static final String KEY_HOME_PLAYLIST_FOR_YOU = "HomePlaylistForYou";
    public static final String KEY_HOME_PLAYLIST_REDISCOVER = "HomePlaylistRediscover";
    public static final String KEY_HOME_PLAYLIST_NEW_IN_LIBRARY = "HomePlaylistNewInLibrary";
    public static final String KEY_HOME_PLAYLIST_TOP_ARTIST = "HomePlaylistTopArtist";


    /* *********************** THEME SECTION ********************* */
    // Shared Preference for theme section
    public static final String PREFS_PULSE_THEMES = "PulseThemes";
    // Key to check whether "Automatically set theme" is enables
    public static final String KEY_UI_MODE_AUTO = "AutoThemeEnabled";
    // Key to determine whether dark mode is enabled by the user
    public static final String KEY_UI_THEME_DARK = "DarkModeEnabled";
    // Id for light theme
    public static final short LIGHT_THEME = 515;
    // Key for storing the dark theme to apply
    public static final String DARK_THEME_CATEGORY_KEY = "DarkThemeId";
    // Constants for each of the three dark theme that Pulse supports
    public static final short DARK_THEME_GRAY = 616;
    public static final short DARK_THEME_KINDA = 626;
    public static final short DARK_THEME_PURE_BLACK = 636;
    // Key to check whether preset accents is used
    public static final String KEY_ACCENTS_USING_PRESET = "UsingPresetColors";
    // Key for preset accent color
    public static final String KEY_ACCENTS_COLOR_PRESET = "AccentsColorPresetId";
    // Key to check whether custom accent is used
    public static final String KEY_ACCENTS_COLOR_CUSTOM = "AccentsColorCustom";
    // Key to check whether "Desaturated color" is enabled
    public static final String KEY_ACCENTS_COLOR_DESATURATED = "AccentsColorDesaturated";
    // Constants for preset accent colors
    public static final short ACCENT_SLATE_BLUE = 700;
    public static final short ACCENT_AZURE_RADIANCE = 701;
    public static final short ACCENT_PERSIAN_MINT = 702;
    public static final short ACCENT_VALENCIA = 703;
    public static final short ACCENT_MILLBROOK = 704;
    public static final short ACCENT_CORAL = 705;
    public static final short ACCENT_SUNKIST = 706;
    public static final short ACCENT_CORNFLOUR_BLUE = 707;
    // Key to check whether app shortcut is using back theme
    public static final String KEY_SHORTCUT_THEME_DARK = "AppShortcutsDarkTheme";


    /* *********************** NOW PLAYING SECTION ********************* */
    // Shared Preference for now playing section
    public static final String PREF_NOW_PLAYING = "NowPlaying";
    public static final String KEY_NOW_PLAYING_STYLE = "Style";
    // Constants for now playing ui styles (Keep in range 5000 - 5100)
    public static final int NOW_PLAYING_SCREEN_MODERN = 5000;
    public static final int NOW_PLAYING_SCREEN_STYLISH = 5001;
    public static final int NOW_PLAYING_SCREEN_EDGE = 5002;
    // Key to check whether seek controls is enabled (Disabled by default)
    public static final String KEY_NOW_PLAYING_CONTROLS_SEEK_ENABLED = "SeekButtonsEnabled";
    // Keys for storing seek duration
    public static final String KEY_NOW_PLAYING_SEEK_DURATION_FORWARD = "SeekForward";
    public static final String KEY_NOW_PLAYING_SEEK_DURATION_BACKWARD = "SeekBackward";
    // Default seek duration 10 seconds
    public static final int DEF_NOW_PLAYING_SEEK_DURATION = 10;
    // Keys for storing corner radius in now playing screen
    public static final String KEY_NOW_PLAYING_ALBUM_CARD_RADIUS_TL = "RadiusTopLeft";
    public static final String KEY_NOW_PLAYING_ALBUM_CARD_RADIUS_TR = "RadiusTopRight";
    public static final String KEY_NOW_PLAYING_ALBUM_CARD_RADIUS_BL = "RadiusBottomLeft";
    public static final String KEY_NOW_PLAYING_ALBUM_CARD_RADIUS_BR = "RadiusBottomRight";
    // Default corner radius value (16dp)
    public static final int DEF_NOW_PLAYING_ALBUM_CARD_RADIUS = 16;


    /* *********************** AUDIO SECTION ********************* */
    // Shared preference for audio
    public static final String PREF_AUDIO = "Audio";
    // Key to check whether bluetooth auto play is enabled
    public static final String KEY_BLUETOOTH_AUTO_PLAY = "BluetoothAutoPlay";
    // Key for storing the bluetooth auto play action
    public static final String KEY_BLUETOOTH_AUTO_PLAY_ACTION = "BluetoothAutoPlayAction";
    // Key for storing QS tile action
    public static final String KEY_QS_TILE_ACTION = "QSTileAction";
    // Key to check whether sleep timer is enabled
    public static final String KEY_SLEEP_TIMER = "SleepTimer";
    // Default value for sleep timer (Disabled by default)
    public static final boolean DEF_SLEEP_TIMER_DISABLED = false;
    // Key for storing sleep timer duration (Default 20 minutes)
    public static final String KEY_SLEEP_TIMER_DURATION = "SleepTimerDuration";
    // Key to check whether sleep timer should be repeated for next session
    public static final String KEY_REPEATING_TIMER = "RepeatingSleepTimer";


    /* *********************** WIDGET SECTION ********************* */
    // Shared Preference for Widgets
    public static final String PREF_WIDGETS = "Widgets";
    // Key to check whether widgets are enabled (Disabled by default)
    public static final String KEY_WIDGET_ENABLED = "WidgetEnabled";
    // Key for storing the widget's play action
    public static final String KEY_WIDGET_PLAY_ACTION = "PlayAction";
}
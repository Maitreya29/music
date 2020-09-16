package com.hardcodecoder.pulsemusic;

public class Preferences {

    public static final String SORT_ORDER_PREFS_KEY = "SortOrder";
    public static final String SORT_ORDER_LIBRARY_KEY = "LibrarySortOrder";
    public static final String SORT_ORDER_ALBUMS_KEY = "AlbumsSortOrder";
    public static final String SORT_ORDER_ALBUM_DETAILS_KEY = "AlbumDetailsSortOrder";
    public static final String SORT_ORDER_ARTIST_DETAILS_KEY = "ArtistDetailsSortOrder";
    public static final String SORT_ORDER_ARTIST_KEY = "ArtistsSortOrder";
    // In range 3000 - 3100
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

    public static final String SPAN_COUNT = "GridSpanCount";

    public static final String LIBRARY_SPAN_COUNT_PORTRAIT_KEY = "LibraryPortraitGridCount";
    public static final String LIBRARY_SPAN_COUNT_LANDSCAPE_KEY = "LibraryLandscapeGridCount";

    public static final int SPAN_COUNT_LIBRARY_PORTRAIT_DEF_VALUE = 1;
    public static final int SPAN_COUNT_LIBRARY_LANDSCAPE_DEF_VALUE = 2;

    public static final String ALBUMS_SPAN_COUNT_PORTRAIT_KEY = "AlbumsPortraitGridCount";
    public static final String ALBUMS_SPAN_COUNT_LANDSCAPE_KEY = "AlbumsLandscapeGridCount";

    public static final String ARTIST_SPAN_COUNT_PORTRAIT_KEY = "ArtistsPortraitGridCount";
    public static final String ARTIST_SPAN_COUNT_LANDSCAPE_KEY = "ArtistsLandscapeGridCount";

    public static final int SPAN_COUNT_PORTRAIT_DEF_VALUE = 2;
    public static final int SPAN_COUNT_LANDSCAPE_DEF_VALUE = 4;

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

    public static final short ACCENT_EXODUS_FRUIT = 700;
    public static final short ACCENT_ELECTRON_BLUE = 701;
    public static final short ACCENT_MINT_LEAF = 702;
    public static final short ACCENT_CHI_GONG = 703;
    public static final short ACCENT_SEI_BAR = 704;
    public static final short ACCENT_CORAL = 705;
    public static final short ACCENT_SUNKIST = 706;
    public static final short ACCENT_BLUE_PINK = 707;

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

    public static final String BLUETOOTH_DEVICE_DETECTION_KEY = "BluetoothDeviceDetection";
    public static final String BLUETOOTH_DEVICE_DETECTION_ACTION_KEY = "BluetoothDeviceDetectionAction";

    public static final short BLUETOOTH_ACTION_PLAY_SHUFFLE = 6000;
    public static final short BLUETOOTH_ACTION_PLAY_SUGGESTED = 6001;
    public static final short BLUETOOTH_ACTION_PLAY_LATEST = 6002;
}

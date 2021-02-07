package com.hardcodecoder.pulsemusic.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.themes.PresetColors;

public class AppSettings {

    public static boolean isFirstRun(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.FIRST_RUN, Context.MODE_PRIVATE)
                .getBoolean(Preferences.FIRST_RUN, true);
    }

    public static void setFirstRun(@NonNull Context context, boolean firstRun) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.FIRST_RUN, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Preferences.FIRST_RUN, firstRun);
        editor.apply();
    }

    public static void savePortraitModeColumnCount(@NonNull Context context, String prefId, int count) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.COLUMN_COUNT, Context.MODE_PRIVATE).edit();
        editor.putInt(prefId, count);
        editor.apply();
    }

    public static int getPortraitModeColumnCount(@NonNull Context context, String prefId, int defValue) {
        return context.getSharedPreferences(Preferences.COLUMN_COUNT, Context.MODE_PRIVATE)
                .getInt(prefId, defValue);
    }

    public static void saveLandscapeModeColumnCount(@NonNull Context context, String prefId, int count) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.COLUMN_COUNT, Context.MODE_PRIVATE).edit();
        editor.putInt(prefId, count);
        editor.apply();
    }

    public static int getLandscapeModeColumnCount(@NonNull Context context, String prefId, int defValue) {
        return context.getSharedPreferences(Preferences.COLUMN_COUNT, Context.MODE_PRIVATE)
                .getInt(prefId, defValue);
    }

    public static void saveSortOrder(@NonNull Context context, String prefId, int sortOrder) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.SORT_ORDER_PREFS_KEY, Context.MODE_PRIVATE).edit();
        editor.putInt(prefId, sortOrder);
        editor.apply();
    }

    public static int getSortOrder(@NonNull Context context, String prefId) {
        return context.getSharedPreferences(Preferences.SORT_ORDER_PREFS_KEY, Context.MODE_PRIVATE)
                .getInt(prefId, Preferences.SORT_ORDER_ASC);
    }

    public static void enableDarkMode(@NonNull Context context, boolean state) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PULSE_THEMES_PREFS, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Preferences.UI_THEME_DARK_KEY, state);
        editor.apply();
    }

    public static void enableAutoTheme(@NonNull Context context, boolean state) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PULSE_THEMES_PREFS, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Preferences.UI_MODE_AUTO_KEY, state);
        editor.apply();
    }

    public static boolean isDarkModeEnabled(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PULSE_THEMES_PREFS, Context.MODE_PRIVATE)
                .getBoolean(Preferences.UI_THEME_DARK_KEY, false);
    }

    public static boolean isAutoThemeEnabled(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PULSE_THEMES_PREFS, Context.MODE_PRIVATE)
                .getBoolean(Preferences.UI_MODE_AUTO_KEY, false);
    }

    public static void saveSelectedDarkTheme(@NonNull Context context, int id) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PULSE_THEMES_PREFS, Context.MODE_PRIVATE).edit();
        editor.putInt(Preferences.DARK_THEME_CATEGORY_KEY, id);
        editor.apply();
    }

    public static int getSelectedDarkTheme(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PULSE_THEMES_PREFS, Context.MODE_PRIVATE)
                .getInt(Preferences.DARK_THEME_CATEGORY_KEY, Preferences.DARK_THEME_GRAY);
    }

    public static void saveSelectedAccentId(@NonNull Context context, int accentId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PULSE_THEMES_PREFS, Context.MODE_PRIVATE).edit();
        editor.putInt(Preferences.ACCENTS_COLOR_PRESET_KEY, accentId);
        editor.putBoolean(Preferences.ACCENTS_MODE_USING_PRESET_KEY, true);
        editor.apply();
    }

    public static int getSelectedAccentId(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PULSE_THEMES_PREFS, Context.MODE_PRIVATE)
                .getInt(Preferences.ACCENTS_COLOR_PRESET_KEY, Preferences.ACCENT_SLATE_BLUE);
    }

    public static void saveCustomAccentColor(@NonNull Context context, @ColorInt int color) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PULSE_THEMES_PREFS, Context.MODE_PRIVATE).edit();
        editor.putInt(Preferences.ACCENTS_COLOR_CUSTOM_KEY, color);
        editor.putBoolean(Preferences.ACCENTS_MODE_USING_PRESET_KEY, false);
        editor.apply();
    }

    public static int getCustomAccentColor(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PULSE_THEMES_PREFS, Context.MODE_PRIVATE)
                .getInt(Preferences.ACCENTS_COLOR_CUSTOM_KEY, PresetColors.SLATE_BLUE);
    }

    public static boolean getPresetAccentModeEnabled(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PULSE_THEMES_PREFS, Context.MODE_PRIVATE)
                .getBoolean(Preferences.ACCENTS_MODE_USING_PRESET_KEY, true /*Use preset colors by default*/);
    }

    public static void saveAccentDesaturatedColor(@NonNull Context context, boolean enabled) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PULSE_THEMES_PREFS, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Preferences.ACCENTS_COLOR_DESATURATED_KEY, enabled);
        editor.apply();
    }

    public static boolean getAccentDesaturatedColor(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PULSE_THEMES_PREFS, Context.MODE_PRIVATE)
                .getBoolean(Preferences.ACCENTS_COLOR_DESATURATED_KEY, false);
    }

    public static void setNowPlayingScreenStyle(@NonNull Context context, int id) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.NOW_PLAYING_SCREEN_STYLE_KEY, Context.MODE_PRIVATE).edit();
        editor.putInt(Preferences.NOW_PLAYING_SCREEN_STYLE_KEY, id);
        editor.apply();
    }

    public static int getNowPlayingScreenStyle(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.NOW_PLAYING_SCREEN_STYLE_KEY, Context.MODE_PRIVATE)
                .getInt(Preferences.NOW_PLAYING_SCREEN_STYLE_KEY, Preferences.NOW_PLAYING_SCREEN_MODERN);
    }

    public static void saveBluetoothDeviceDetection(@NonNull Context context, boolean enabled) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.AUDIO_DEVICE_AUTO_PLAY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Preferences.BLUETOOTH_DEVICE_DETECTION_KEY, enabled);
        editor.apply();
    }

    public static boolean isBluetoothDeviceDetectionEnabled(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.AUDIO_DEVICE_AUTO_PLAY, Context.MODE_PRIVATE)
                .getBoolean(Preferences.BLUETOOTH_DEVICE_DETECTION_KEY, false);
    }

    public static void saveBluetoothDeviceDetectionAction(@NonNull Context context, int action) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.AUDIO_DEVICE_AUTO_PLAY, Context.MODE_PRIVATE).edit();
        editor.putInt(Preferences.BLUETOOTH_DEVICE_ACTION_KEY, action);
        editor.apply();
    }

    public static int getBluetoothDeviceDetectionAction(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.AUDIO_DEVICE_AUTO_PLAY, Context.MODE_PRIVATE)
                .getInt(Preferences.BLUETOOTH_DEVICE_ACTION_KEY, Preferences.DEVICE_ACTION_PLAY_SHUFFLE);
    }

    public static void saveNowPlayingAlbumCoverCornerRadius(@NonNull Context context, int tl, int tr, int bl, int br) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.NOW_PLAYING_ALBUM_COVER_CORNER_RADIUS, Context.MODE_PRIVATE).edit();
        editor.putInt(Preferences.NOW_PLAYING_ALBUM_COVER_RADIUS_TL, tl);
        editor.putInt(Preferences.NOW_PLAYING_ALBUM_COVER_RADIUS_TR, tr);
        editor.putInt(Preferences.NOW_PLAYING_ALBUM_COVER_RADIUS_BL, bl);
        editor.putInt(Preferences.NOW_PLAYING_ALBUM_COVER_RADIUS_BR, br);
        editor.apply();
    }

    @NonNull
    public static int[] getNowPlayingAlbumCoverCornerRadius(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Preferences.NOW_PLAYING_ALBUM_COVER_CORNER_RADIUS, Context.MODE_PRIVATE);
        int tl = sharedPreferences.getInt(Preferences.NOW_PLAYING_ALBUM_COVER_RADIUS_TL, Preferences.NOW_PLAYING_ALBUM_COVER_RADIUS_DEF);
        int tr = sharedPreferences.getInt(Preferences.NOW_PLAYING_ALBUM_COVER_RADIUS_TR, Preferences.NOW_PLAYING_ALBUM_COVER_RADIUS_DEF);
        int bl = sharedPreferences.getInt(Preferences.NOW_PLAYING_ALBUM_COVER_RADIUS_BL, Preferences.NOW_PLAYING_ALBUM_COVER_RADIUS_DEF);
        int br = sharedPreferences.getInt(Preferences.NOW_PLAYING_ALBUM_COVER_RADIUS_BR, Preferences.NOW_PLAYING_ALBUM_COVER_RADIUS_DEF);
        return new int[]{tl, tr, bl, br};
    }

    public static void setAppShortcutThemeMode(@NonNull Context context, boolean darkModeOn) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.APP_SHORTCUT_THEME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Preferences.APP_SHORTCUT_THEME_MODE, darkModeOn);
        editor.apply();
    }

    public static boolean getAppShortcutThemeMode(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.APP_SHORTCUT_THEME, Context.MODE_PRIVATE)
                .getBoolean(Preferences.APP_SHORTCUT_THEME_MODE, false);
    }

    public static void setFilterDuration(@NonNull Context context, int duration) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.GENERAL_SETTINGS_PREF, Context.MODE_PRIVATE).edit();
        editor.putInt(Preferences.FILTER_DURATION, duration);
        editor.apply();
    }

    public static int getFilterDuration(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.GENERAL_SETTINGS_PREF, Context.MODE_PRIVATE)
                .getInt(Preferences.FILTER_DURATION, 30); // By default filter duration is 30 sec
    }

    public static void setRememberLastTrack(@NonNull Context context, boolean enabled) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.GENERAL_SETTINGS_PREF, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Preferences.REMEMBER_LAST_TRACK, enabled);
        editor.apply();
    }

    public static boolean isRememberLastTrack(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.GENERAL_SETTINGS_PREF, Context.MODE_PRIVATE)
                .getBoolean(Preferences.REMEMBER_LAST_TRACK, false); // By default do not remember last track
    }

    public static void saveTrackAndPosition(@NonNull Context context, int trackId, long position) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.GENERAL_SETTINGS_PREF, Context.MODE_PRIVATE).edit();
        editor.putInt(Preferences.LAST_TRACK_ID, trackId);
        editor.putLong(Preferences.LAST_TRACK_POSITION, position);
        editor.apply();
    }

    public static int getLastTrackId(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.GENERAL_SETTINGS_PREF, Context.MODE_PRIVATE)
                .getInt(Preferences.LAST_TRACK_ID, -1);
    }

    public static long getLastTrackPosition(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.GENERAL_SETTINGS_PREF, Context.MODE_PRIVATE)
                .getLong(Preferences.LAST_TRACK_POSITION, 0); // By default do not remember last track
    }

    public static boolean isPlaylistSectionEnabled(@NonNull Context context, @NonNull String playlistSection) {
        return context.getSharedPreferences(Preferences.HOME_PLAYLIST_SECTIONS, Context.MODE_PRIVATE)
                .getBoolean(playlistSection, false);
    }

    public static void setPlaylistSectionEnabled(@NonNull Context context, @NonNull String playlistSection, boolean enable) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.HOME_PLAYLIST_SECTIONS, Context.MODE_PRIVATE).edit();
        editor.putBoolean(playlistSection, enable);
        editor.apply();
    }

    public static void setSeekButtonDuration(@NonNull Context context, @NonNull String key, int duration) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.NOW_PLAYING_CONTROLS, Context.MODE_PRIVATE).edit();
        editor.putInt(key, duration);
        editor.apply();
    }

    public static int getSeekButtonDuration(@NonNull Context context, @NonNull String key) {
        return context.getSharedPreferences(Preferences.NOW_PLAYING_CONTROLS, Context.MODE_PRIVATE)
                .getInt(key, Preferences.NOW_PLAYING_SEEK_DURATION_DEF);
    }

    public static void setSeekButtonsEnabled(@NonNull Context context, boolean enable) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.NOW_PLAYING_CONTROLS, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Preferences.NOW_PLAYING_CONTROLS_SEEK_ENABLED, enable);
        editor.apply();
    }

    public static boolean isSeekButtonsEnabled(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.NOW_PLAYING_CONTROLS, Context.MODE_PRIVATE)
                .getBoolean(Preferences.NOW_PLAYING_CONTROLS_SEEK_ENABLED, Preferences.NOW_PLAYING_CONTROLS_SEEK_ENABLED_DEF);
    }
}
package com.hardcodecoder.pulsemusic.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.themes.PresetColors;

public class AppSettings {

    public static boolean isFirstRun(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PREF_FIRST_RUN, Context.MODE_PRIVATE)
                .getBoolean(Preferences.KEY_FIRST_RUN, true);
    }

    public static void setFirstRun(@NonNull Context context, boolean firstRun) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PREF_FIRST_RUN, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Preferences.KEY_FIRST_RUN, firstRun);
        editor.apply();
    }

    public static void saveSortOrder(@NonNull Context context, String prefId, int sortOrder) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PREF_SORT_ORDER, Context.MODE_PRIVATE).edit();
        editor.putInt(prefId, sortOrder);
        editor.apply();
    }

    public static int getSortOrder(@NonNull Context context, String prefId) {
        return context.getSharedPreferences(Preferences.PREF_SORT_ORDER, Context.MODE_PRIVATE)
                .getInt(prefId, Preferences.SORT_ORDER_ASC);
    }

    public static void savePortraitModeColumnCount(@NonNull Context context, String prefId, int count) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PREF_COLUMN_COUNT, Context.MODE_PRIVATE).edit();
        editor.putInt(prefId, count);
        editor.apply();
    }

    public static int getPortraitModeColumnCount(@NonNull Context context, String prefId, int defValue) {
        return context.getSharedPreferences(Preferences.PREF_COLUMN_COUNT, Context.MODE_PRIVATE)
                .getInt(prefId, defValue);
    }

    public static void saveLandscapeModeColumnCount(@NonNull Context context, String prefId, int count) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PREF_COLUMN_COUNT, Context.MODE_PRIVATE).edit();
        editor.putInt(prefId, count);
        editor.apply();
    }

    public static int getLandscapeModeColumnCount(@NonNull Context context, String prefId, int defValue) {
        return context.getSharedPreferences(Preferences.PREF_COLUMN_COUNT, Context.MODE_PRIVATE)
                .getInt(prefId, defValue);
    }

    public static void setFilterDuration(@NonNull Context context, int duration) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PREF_GENERAL, Context.MODE_PRIVATE).edit();
        editor.putInt(Preferences.KEY_FILTER_DURATION, duration);
        editor.apply();
    }

    public static int getFilterDuration(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PREF_GENERAL, Context.MODE_PRIVATE)
                .getInt(Preferences.KEY_FILTER_DURATION, 30); // By default filter duration is 30 sec
    }

    public static void setRememberPlaylist(@NonNull Context context, boolean enabled) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PREF_GENERAL, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Preferences.KEY_REMEMBER_PREVIOUS_PLAYLIST, enabled);
        editor.apply();
    }

    public static boolean isRememberPlaylistEnabled(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PREF_GENERAL, Context.MODE_PRIVATE)
                .getBoolean(Preferences.KEY_REMEMBER_PREVIOUS_PLAYLIST, false); // By default do not remember last track
    }

    public static void savePlaylistTrackAndPosition(@NonNull Context context, int trackIndex, int position) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PREF_GENERAL, Context.MODE_PRIVATE).edit();
        editor.putInt(Preferences.KEY_PREVIOUS_PLAYLIST_TRACK_INDEX, trackIndex);
        editor.putInt(Preferences.KEY_PREVIOUS_PLAYLIST_TRACK_POSITION, position);
        editor.apply();
    }

    public static int getLastTrackIndex(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PREF_GENERAL, Context.MODE_PRIVATE)
                .getInt(Preferences.KEY_PREVIOUS_PLAYLIST_TRACK_INDEX, -1);
    }

    public static int getLastTrackPosition(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PREF_GENERAL, Context.MODE_PRIVATE)
                .getInt(Preferences.KEY_PREVIOUS_PLAYLIST_TRACK_POSITION, 0);
    }

    public static void setPlaylistSectionEnabled(@NonNull Context context, @NonNull String playlistSection, boolean enable) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PREF_HOME_PLAYLIST_SECTIONS, Context.MODE_PRIVATE).edit();
        editor.putBoolean(playlistSection, enable);
        editor.apply();
    }

    public static boolean isPlaylistSectionEnabled(@NonNull Context context, @NonNull String playlistSection) {
        return context.getSharedPreferences(Preferences.PREF_HOME_PLAYLIST_SECTIONS, Context.MODE_PRIVATE)
                .getBoolean(playlistSection, false);
    }

    public static void saveSelectedDarkTheme(@NonNull Context context, int id) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PREFS_PULSE_THEMES, Context.MODE_PRIVATE).edit();
        editor.putInt(Preferences.DARK_THEME_CATEGORY_KEY, id);
        editor.apply();
    }

    public static int getSelectedDarkTheme(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PREFS_PULSE_THEMES, Context.MODE_PRIVATE)
                .getInt(Preferences.DARK_THEME_CATEGORY_KEY, Preferences.DARK_THEME_GRAY);
    }

    public static void enableDarkMode(@NonNull Context context, boolean state) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PREFS_PULSE_THEMES, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Preferences.KEY_UI_THEME_DARK, state);
        editor.apply();
    }

    public static boolean isDarkModeEnabled(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PREFS_PULSE_THEMES, Context.MODE_PRIVATE)
                .getBoolean(Preferences.KEY_UI_THEME_DARK, false);
    }

    public static void enableAutoTheme(@NonNull Context context, boolean state) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PREFS_PULSE_THEMES, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Preferences.KEY_UI_MODE_AUTO, state);
        editor.apply();
    }

    public static boolean isAutoThemeEnabled(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PREFS_PULSE_THEMES, Context.MODE_PRIVATE)
                .getBoolean(Preferences.KEY_UI_MODE_AUTO, false);
    }

    public static void setAccentDesaturated(@NonNull Context context, boolean isDesaturated) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PREFS_PULSE_THEMES, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Preferences.KEY_ACCENTS_COLOR_DESATURATED, isDesaturated);
        editor.apply();
    }

    public static boolean isAccentDesaturated(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PREFS_PULSE_THEMES, Context.MODE_PRIVATE)
                .getBoolean(Preferences.KEY_ACCENTS_COLOR_DESATURATED, false);
    }

    public static void saveSelectedAccentId(@NonNull Context context, int accentId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PREFS_PULSE_THEMES, Context.MODE_PRIVATE).edit();
        editor.putInt(Preferences.KEY_ACCENTS_COLOR_PRESET, accentId);
        editor.putBoolean(Preferences.KEY_ACCENTS_USING_PRESET, true);
        editor.apply();
    }

    public static int getSelectedAccentId(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PREFS_PULSE_THEMES, Context.MODE_PRIVATE)
                .getInt(Preferences.KEY_ACCENTS_COLOR_PRESET, Preferences.ACCENT_SLATE_BLUE);
    }

    public static boolean isPresetAccentModeEnabled(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PREFS_PULSE_THEMES, Context.MODE_PRIVATE)
                .getBoolean(Preferences.KEY_ACCENTS_USING_PRESET, true /*Use preset colors by default*/);
    }

    public static void saveCustomAccentColor(@NonNull Context context, @ColorInt int color) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PREFS_PULSE_THEMES, Context.MODE_PRIVATE).edit();
        editor.putInt(Preferences.KEY_ACCENTS_COLOR_CUSTOM, color);
        editor.putBoolean(Preferences.KEY_ACCENTS_USING_PRESET, false);
        editor.apply();
    }

    public static int getCustomAccentColor(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PREFS_PULSE_THEMES, Context.MODE_PRIVATE)
                .getInt(Preferences.KEY_ACCENTS_COLOR_CUSTOM, PresetColors.SLATE_BLUE);
    }

    public static void setAppShortcutUsingDarkTheme(@NonNull Context context, boolean darkModeOn) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PREFS_PULSE_THEMES, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Preferences.KEY_SHORTCUT_THEME_DARK, darkModeOn);
        editor.apply();
    }

    public static boolean isAppShortcutUsingThemeDark(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PREFS_PULSE_THEMES, Context.MODE_PRIVATE)
                .getBoolean(Preferences.KEY_SHORTCUT_THEME_DARK, false);
    }

    public static void setNowPlayingScreenStyle(@NonNull Context context, int id) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PREF_NOW_PLAYING, Context.MODE_PRIVATE).edit();
        editor.putInt(Preferences.KEY_NOW_PLAYING_STYLE, id);
        editor.apply();
    }

    public static int getNowPlayingScreenStyle(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PREF_NOW_PLAYING, Context.MODE_PRIVATE)
                .getInt(Preferences.KEY_NOW_PLAYING_STYLE, Preferences.NOW_PLAYING_SCREEN_MODERN);
    }

    public static void setNowPlayingAlbumCardCornerRadius(@NonNull Context context, int tl, int tr, int bl, int br) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PREF_NOW_PLAYING, Context.MODE_PRIVATE).edit();
        editor.putInt(Preferences.KEY_NOW_PLAYING_ALBUM_CARD_RADIUS_TL, tl);
        editor.putInt(Preferences.KEY_NOW_PLAYING_ALBUM_CARD_RADIUS_TR, tr);
        editor.putInt(Preferences.KEY_NOW_PLAYING_ALBUM_CARD_RADIUS_BL, bl);
        editor.putInt(Preferences.KEY_NOW_PLAYING_ALBUM_CARD_RADIUS_BR, br);
        editor.apply();
    }

    @NonNull
    public static int[] getNowPlayingAlbumCardCornerRadius(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Preferences.PREF_NOW_PLAYING, Context.MODE_PRIVATE);
        int tl = sharedPreferences.getInt(Preferences.KEY_NOW_PLAYING_ALBUM_CARD_RADIUS_TL, Preferences.DEF_NOW_PLAYING_ALBUM_CARD_RADIUS);
        int tr = sharedPreferences.getInt(Preferences.KEY_NOW_PLAYING_ALBUM_CARD_RADIUS_TR, Preferences.DEF_NOW_PLAYING_ALBUM_CARD_RADIUS);
        int bl = sharedPreferences.getInt(Preferences.KEY_NOW_PLAYING_ALBUM_CARD_RADIUS_BL, Preferences.DEF_NOW_PLAYING_ALBUM_CARD_RADIUS);
        int br = sharedPreferences.getInt(Preferences.KEY_NOW_PLAYING_ALBUM_CARD_RADIUS_BR, Preferences.DEF_NOW_PLAYING_ALBUM_CARD_RADIUS);
        return new int[]{tl, tr, bl, br};
    }

    public static void setSeekButtonsEnabled(@NonNull Context context, boolean enable) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PREF_NOW_PLAYING, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Preferences.KEY_NOW_PLAYING_CONTROLS_SEEK_ENABLED, enable);
        editor.apply();
    }

    public static boolean isSeekButtonsEnabled(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PREF_NOW_PLAYING, Context.MODE_PRIVATE)
                .getBoolean(Preferences.KEY_NOW_PLAYING_CONTROLS_SEEK_ENABLED, false);
    }

    public static void setSeekButtonDuration(@NonNull Context context, @NonNull String key, int duration) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PREF_NOW_PLAYING, Context.MODE_PRIVATE).edit();
        editor.putInt(key, duration);
        editor.apply();
    }

    public static int getSeekButtonDuration(@NonNull Context context, @NonNull String key) {
        return context.getSharedPreferences(Preferences.PREF_NOW_PLAYING, Context.MODE_PRIVATE)
                .getInt(key, Preferences.DEF_NOW_PLAYING_SEEK_DURATION);
    }

    public static void enableBluetoothAutoPlay(@NonNull Context context, boolean enabled) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PREF_AUDIO, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Preferences.KEY_BLUETOOTH_AUTO_PLAY, enabled);
        editor.apply();
    }

    public static boolean isBluetoothAutoPlayEnabled(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PREF_AUDIO, Context.MODE_PRIVATE)
                .getBoolean(Preferences.KEY_BLUETOOTH_AUTO_PLAY, false);
    }

    public static void saveAutoPlayAction(@NonNull Context context, @NonNull String key, int action) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PREF_AUDIO, Context.MODE_PRIVATE).edit();
        editor.putInt(key, action);
        editor.apply();
    }

    public static int getAutoPlayAction(@NonNull Context context, @NonNull String key) {
        return context.getSharedPreferences(Preferences.PREF_AUDIO, Context.MODE_PRIVATE)
                .getInt(key, Preferences.ACTION_PLAY_SHUFFLE);
    }

    public static void setSleepTimer(@NonNull Context context, boolean enable) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PREF_AUDIO, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Preferences.KEY_SLEEP_TIMER, enable);
        editor.apply();
    }

    public static boolean isSleepTimerEnabled(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PREF_AUDIO, Context.MODE_PRIVATE)
                .getBoolean(Preferences.KEY_SLEEP_TIMER, Preferences.DEF_SLEEP_TIMER_DISABLED);
    }

    public static void setSleepTimerDurationMinutes(@NonNull Context context, int durationMinutes) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PREF_AUDIO, Context.MODE_PRIVATE).edit();
        editor.putInt(Preferences.KEY_SLEEP_TIMER_DURATION, durationMinutes);
        editor.apply();
    }

    public static int getSleepTimerDurationMinutes(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PREF_AUDIO, Context.MODE_PRIVATE)
                .getInt(Preferences.KEY_SLEEP_TIMER_DURATION, 20);
    }

    public static void setRepeatingTimer(@NonNull Context context, boolean enable) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PREF_AUDIO, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Preferences.KEY_REPEATING_TIMER, enable);
        editor.apply();
    }

    public static boolean isRepeatingTimerEnabled(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PREF_AUDIO, Context.MODE_PRIVATE)
                .getBoolean(Preferences.KEY_REPEATING_TIMER, Preferences.DEF_SLEEP_TIMER_DISABLED);
    }

    public static void setWidgetEnabled(@NonNull Context context, boolean enabled) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PREF_WIDGETS, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Preferences.KEY_WIDGET_ENABLED, enabled);
        editor.apply();
    }

    public static boolean isWidgetEnable(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PREF_WIDGETS, Context.MODE_PRIVATE)
                .getBoolean(Preferences.KEY_WIDGET_ENABLED, false);
    }

    public static void setWidgetPlayAction(@NonNull Context context, int action) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PREF_WIDGETS, Context.MODE_PRIVATE).edit();
        editor.putInt(Preferences.KEY_WIDGET_PLAY_ACTION, action);
        editor.apply();
    }

    public static int getWidgetPlayAction(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PREF_WIDGETS, Context.MODE_PRIVATE)
                .getInt(Preferences.KEY_WIDGET_PLAY_ACTION, Preferences.ACTION_PLAY_SUGGESTED);
    }

    public static void setWidgetBackgroundAlpha(@NonNull Context context, int alphaPercent) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Preferences.PREF_WIDGETS, Context.MODE_PRIVATE).edit();
        editor.putInt(Preferences.KEY_WIDGET_BACKGROUND_ALPHA, alphaPercent);
        editor.apply();
    }

    public static int getWidgetBackgroundAlpha(@NonNull Context context) {
        return context.getSharedPreferences(Preferences.PREF_WIDGETS, Context.MODE_PRIVATE)
                .getInt(Preferences.KEY_WIDGET_BACKGROUND_ALPHA, 60);
    }
}
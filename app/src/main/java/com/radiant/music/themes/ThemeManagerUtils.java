package com.radiant.music.themes;

import android.content.Context;

import androidx.annotation.ColorInt;
import androidx.annotation.StyleRes;

import com.radiant.music.Preferences;
import com.radiant.music.utils.AppSettings;
import com.radiant.music.utils.DayTimeUtils;

public class ThemeManagerUtils {

    private static boolean mAutoMode = false;
    private static boolean mDarkMode = false;
    private static int mThemeId = Preferences.LIGHT_THEME;
    private static int mPresetsAccentsId;
    private static int mStoredAccentColor = PresetColors.RADIANT_PINK;
    private static boolean mUsingPresetColors = true;
    private static boolean mDesaturatedAccents = false;
    private static boolean mInitialized = false;

    public static void init(Context context, boolean forceInitialize) {
        if (!forceInitialize && mInitialized) return;

        mAutoMode = AppSettings.isAutoThemeEnabled(context);
        if ((mUsingPresetColors = AppSettings.isPresetAccentModeEnabled(context))) {
            mPresetsAccentsId = AppSettings.getSelectedAccentId(context);
            mStoredAccentColor = PresetColors.getPresetAccentColorById(mPresetsAccentsId);
        } else
            mStoredAccentColor = AppSettings.getCustomAccentColor(context);

        if (mAutoMode) mDarkMode = (DayTimeUtils.getTimeOfDay() == DayTimeUtils.DayTime.NIGHT);
        else mDarkMode = AppSettings.isDarkModeEnabled(context);

        if (mDarkMode) mThemeId = AppSettings.getSelectedDarkTheme(context);
        else mThemeId = Preferences.LIGHT_THEME;

        mDesaturatedAccents = AppSettings.isAccentDesaturated(context) && mDarkMode;

        // we have initialized theme, older colors may no longer represent the current theme
        // Reset theme colors, will be re initialized on PMB#onCreate
        ThemeColors.reset();
        mInitialized = true;
    }

    public static boolean toggleDarkTheme(Context context, boolean enabled) {
        AppSettings.enableDarkMode(context, enabled);
        //return true if activity needs to restart because theme needs to be changed
        return (!mDarkMode && enabled) || (mDarkMode && !enabled);
    }

    public static boolean toggleAutoTheme(Context context, boolean enabled) {
        AppSettings.enableAutoTheme(context, enabled);
        // User want auto theme based on time of day
        // If its night/day and current theme is light/dark respectively
        // them restart to apply new theme
        return (enabled && needToChangeTheme());

    }

    public static void setSelectedDarkTheme(Context context, int id) {
        AppSettings.saveSelectedDarkTheme(context, id);
    }

    public static boolean setSelectedPresetAccentColor(Context context, int accentId) {
        AppSettings.saveSelectedAccentId(context, accentId);
        return !mUsingPresetColors || mPresetsAccentsId != accentId;
    }

    public static boolean setSelectedCustomAccentColor(Context context, @ColorInt int color) {
        AppSettings.saveCustomAccentColor(context, color);
        return mStoredAccentColor != color;
    }

    public static boolean needToApplyNewDarkTheme() {
        //Returns true to restart activity in order to apply new dark theme
        return (mAutoMode && isNight()) || mDarkMode;
    }

    public static boolean isDarkModeEnabled() {
        return mDarkMode;
    }

    @StyleRes
    public static int getThemeToApply() {
        return ThemeStore.getThemeById(mThemeId);
    }

    @StyleRes
    public static int getBottomSheetThemeToApply() {
        return ThemeStore.getBottomSheetThemeById(mThemeId);
    }

    @ColorInt
    public static int getStoredAccentColor() {
        return mStoredAccentColor;
    }

    public static boolean isUsingPresetColors() {
        return mUsingPresetColors;
    }

    public static boolean isAccentsDesaturated() {
        return mDesaturatedAccents;
    }

    private static boolean needToChangeTheme() {
        return ((isNight() && !mDarkMode) || (!isNight() && mDarkMode));
    }

    private static boolean isNight() {
        return (DayTimeUtils.getTimeOfDay() == DayTimeUtils.DayTime.NIGHT);
    }
}
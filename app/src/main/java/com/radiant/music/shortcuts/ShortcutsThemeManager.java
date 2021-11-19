package com.radiant.music.shortcuts;

import android.content.Context;
import android.content.res.Configuration;

import com.radiant.music.themes.ColorUtil;
import com.radiant.music.themes.PresetColors;
import com.radiant.music.utils.AppSettings;

class ShortcutsThemeManager {

    private static int mAccentColorForIcons;
    private static int mBackgroundColorForIcons;
    private static boolean mRequiredRecreate;

    static void init(Context context) {
        boolean currentAppliedIsDarkMode = AppSettings.isAppShortcutUsingThemeDark(context);
        int systemThemeMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        mRequiredRecreate = currentAppliedIsDarkMode && systemThemeMode == Configuration.UI_MODE_NIGHT_NO ||
                !currentAppliedIsDarkMode && systemThemeMode == Configuration.UI_MODE_NIGHT_YES;
        mAccentColorForIcons = getAccentColor(context);
        switch (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                mBackgroundColorForIcons = ColorUtil.mixColors(0x121212, 0xFFFFFF, 0.84f);
                if (AppSettings.isAccentDesaturated(context))
                    mAccentColorForIcons = ColorUtil.makeColorDesaturated(mAccentColorForIcons);
                AppSettings.setAppShortcutUsingDarkTheme(context, true);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
            default:
                mBackgroundColorForIcons = ColorUtil.mixColors(0x000000, 0xFFFFFF, 0.02f);
                AppSettings.setAppShortcutUsingDarkTheme(context, false);
                break;
        }
    }

    static int getAccentColorForIcons() {
        return mAccentColorForIcons;
    }

    static int getBackgroundColorForIcons() {
        return mBackgroundColorForIcons;
    }

    static boolean isRequiredRecreate() {
        return mRequiredRecreate;
    }

    // Special method to retrieve stored accent color from shared prefs
    // since ShortcutManagers do not have access to activity theme
    // ThemeManagerUtils and ThemeColors are not initialized
    // when generating icons
    private static int getAccentColor(Context context) {
        if ((AppSettings.isPresetAccentModeEnabled(context))) {
            int presetsAccentsId = AppSettings.getSelectedAccentId(context);
            return PresetColors.getPresetAccentColorById(presetsAccentsId);
        } else
            return AppSettings.getCustomAccentColor(context);
    }
}
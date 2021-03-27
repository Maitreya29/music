package com.hardcodecoder.pulsemusic.shortcuts;

import android.content.Context;
import android.content.res.Configuration;

import com.hardcodecoder.pulsemusic.themes.ColorUtil;
import com.hardcodecoder.pulsemusic.themes.PresetColors;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

class ShortcutsThemeManager {

    private static int mAccentColorForIcons;
    private static int mBackgroundColorForIcons;
    private static boolean mRequiredRecreate;

    static void init(Context context) {
        boolean currentAppliedIsDarkMode = AppSettings.getAppShortcutThemeMode(context);
        int systemThemeMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        mRequiredRecreate = currentAppliedIsDarkMode && systemThemeMode == Configuration.UI_MODE_NIGHT_NO ||
                !currentAppliedIsDarkMode && systemThemeMode == Configuration.UI_MODE_NIGHT_YES;
        mAccentColorForIcons = getAccentColor(context);
        switch (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                mBackgroundColorForIcons = ColorUtil.mixColors(0x121212, 0xFFFFFF, 0.84f);
                if (AppSettings.getAccentDesaturatedColor(context))
                    mAccentColorForIcons = ColorUtil.makeColorDesaturated(mAccentColorForIcons);
                AppSettings.setAppShortcutThemeMode(context, true);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
            default:
                mBackgroundColorForIcons = ColorUtil.mixColors(0x000000, 0xFFFFFF, 0.02f);
                AppSettings.setAppShortcutThemeMode(context, false);
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
        if ((AppSettings.getPresetAccentModeEnabled(context))) {
            int presetsAccentsId = AppSettings.getSelectedAccentId(context);
            return PresetColors.getPresetAccentColorById(presetsAccentsId);
        } else
            return AppSettings.getCustomAccentColor(context);
    }
}
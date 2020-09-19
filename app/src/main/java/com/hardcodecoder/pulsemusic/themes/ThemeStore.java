package com.hardcodecoder.pulsemusic.themes;

import androidx.annotation.StyleRes;

import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;

class ThemeStore {

    @StyleRes
    static int getThemeById(int id) {
        switch (id) {
            case Preferences.DARK_THEME_GRAY:
                return R.style.ActivityThemeDark;
            case Preferences.DARK_THEME_KINDA:
                return R.style.ActivityThemeKindaDark;
            case Preferences.DARK_THEME_PURE_BLACK:
                return R.style.ActivityThemeBlack;
            case Preferences.LIGHT_THEME:
            default:
                return R.style.ActivityThemeLight;
        }
    }
}
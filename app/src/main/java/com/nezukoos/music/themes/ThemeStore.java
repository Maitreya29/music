package com.nezukoos.music.themes;

import androidx.annotation.StyleRes;

import com.nezukoos.music.Preferences;
import com.nezukoos.music.R;

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

    @StyleRes
    static int getBottomSheetThemeById(int activityThemeId) {
        switch (activityThemeId) {
            case Preferences.DARK_THEME_GRAY:
                return R.style.RoundedBottomSheet_Dark;
            case Preferences.DARK_THEME_KINDA:
                return R.style.RoundedBottomSheet_Signature;
            case Preferences.DARK_THEME_PURE_BLACK:
                return R.style.RoundedBottomSheet_Black;
            case Preferences.LIGHT_THEME:
            default:
                return R.style.RoundedBottomSheet_Light;
        }
    }
}
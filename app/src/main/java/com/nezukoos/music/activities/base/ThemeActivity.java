package com.nezukoos.music.activities.base;

import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatActivity;

import com.nezukoos.music.themes.ThemeColors;
import com.nezukoos.music.themes.ThemeManagerUtils;

public class ThemeActivity extends AppCompatActivity {

    @StyleRes
    private int mCurrentTheme;
    @ColorInt
    private int mCurrentAccent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManagerUtils.init(getApplicationContext(), false);
        setTheme(ThemeManagerUtils.getThemeToApply());
        // Initialize ThemeColors only after applying theme to the calling activity
        ThemeColors.initColors(this);
        mCurrentTheme = ThemeManagerUtils.getThemeToApply();
        mCurrentAccent = ThemeColors.getCurrentColorPrimary();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        if (mCurrentTheme != ThemeManagerUtils.getThemeToApply() ||
                mCurrentAccent != ThemeColors.getCurrentColorPrimary()) {
            recreate();
        }
        super.onStart();
    }
}
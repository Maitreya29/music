package com.hardcodecoder.pulsemusic.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.hardcodecoder.pulsemusic.themes.ThemeColors;

public class AccentColorRadioButton extends MaterialRadioButton {

    public AccentColorRadioButton(@NonNull Context context) {
        super(context);
        applyStyles();
    }

    public AccentColorRadioButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        applyStyles();
    }

    public AccentColorRadioButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyStyles();
    }

    private void applyStyles() {
        ColorStateList stateList = ColorStateList.valueOf(ThemeColors.getAccentColorForCurrentTheme());
        setButtonTintList(stateList);
    }
}
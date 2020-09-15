package com.hardcodecoder.pulsemusic.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.slider.Slider;
import com.hardcodecoder.pulsemusic.themes.ThemeColors;

public class AccentColorSlider extends Slider {

    public AccentColorSlider(@NonNull Context context) {
        super(context);
        applyColors();
    }

    public AccentColorSlider(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        applyColors();
    }

    public AccentColorSlider(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyColors();
    }

    private void applyColors() {
        ColorStateList inactiveTrackColor = ColorStateList.valueOf(ThemeColors.getCurrentColorOverlay());
        ColorStateList activeTrackColor = ThemeColors.getAccentColorStateList();

        setTrackInactiveTintList(inactiveTrackColor);
        setTrackActiveTintList(activeTrackColor);
        setThumbTintList(activeTrackColor);
    }
}

package com.nezukoos.music.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.slider.Slider;
import com.nezukoos.music.themes.ThemeColors;

public class AccentColorSlider extends Slider {

    public AccentColorSlider(@NonNull Context context) {
        this(context, null);
    }

    public AccentColorSlider(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AccentColorSlider(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        ColorStateList inactiveTrackColor = ColorStateList.valueOf(ThemeColors.getCurrentColorControlHighlight());
        ColorStateList activeTrackColor = ThemeColors.getPrimaryColorStateList();

        setTrackInactiveTintList(inactiveTrackColor);
        setTrackActiveTintList(activeTrackColor);
        setThumbTintList(activeTrackColor);
    }
}
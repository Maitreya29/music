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
        this(context, null);
    }

    public AccentColorRadioButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AccentColorRadioButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ColorStateList stateList = ColorStateList.valueOf(ThemeColors.getCurrentColorPrimary());
        setButtonTintList(stateList);
    }
}
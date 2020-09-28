package com.hardcodecoder.pulsemusic.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.hardcodecoder.pulsemusic.themes.ThemeColors;

public class MaterialTextButton extends MaterialButton {

    public MaterialTextButton(@NonNull Context context) {
        this(context, null);
    }

    public MaterialTextButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MaterialTextButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        setTextColor(ThemeColors.getAccentColorForCurrentTheme());
        setRippleColor(ColorStateList.valueOf(ThemeColors.getCurrentColorRipple()));
    }
}
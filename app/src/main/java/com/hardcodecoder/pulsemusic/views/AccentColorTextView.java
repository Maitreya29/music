package com.hardcodecoder.pulsemusic.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.themes.ThemeColors;

public class AccentColorTextView extends MaterialTextView {

    public AccentColorTextView(@NonNull Context context) {
        super(context);
        applyColor();
    }

    public AccentColorTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        applyColor();
    }

    public AccentColorTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyColor();
    }

    private void applyColor() {
        setTextColor(ThemeColors.getAccentColorForCurrentTheme());
    }
}
package com.radiant.music.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;
import com.radiant.music.themes.ThemeColors;

public class AccentColorTextView extends MaterialTextView {

    public AccentColorTextView(@NonNull Context context) {
        this(context, null);
    }

    public AccentColorTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AccentColorTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setTextColor(ThemeColors.getCurrentColorPrimary());
    }
}
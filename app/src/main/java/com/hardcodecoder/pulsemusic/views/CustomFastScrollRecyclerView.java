package com.hardcodecoder.pulsemusic.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.themes.ThemeColors;
import com.l4digital.fastscroll.FastScrollRecyclerView;

public class CustomFastScrollRecyclerView extends FastScrollRecyclerView {

    public CustomFastScrollRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public CustomFastScrollRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomFastScrollRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        int accentColor = ThemeColors.getAccentColorForCurrentTheme();
        setBubbleColor(accentColor);
        setHandleColor(accentColor);
        setTrackColor(ThemeColors.getCurrentColorOverlay());
    }
}
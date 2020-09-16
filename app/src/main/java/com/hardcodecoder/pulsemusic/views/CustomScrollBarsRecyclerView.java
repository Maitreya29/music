package com.hardcodecoder.pulsemusic.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.hardcodecoder.pulsemusic.themes.ThemeColors;

public class CustomScrollBarsRecyclerView extends RecyclerView {

    @ColorInt
    private int scrollBarColor;

    public CustomScrollBarsRecyclerView(@NonNull Context context) {
        super(context);
        setScrollBarColor();
    }

    public CustomScrollBarsRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setScrollBarColor();
    }

    public CustomScrollBarsRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setScrollBarColor();
    }

    public void setScrollBarColor() {
        this.scrollBarColor = ThemeColors.getAccentColorForCurrentTheme();
    }

    /**
     * Called by Android {@link android.view.View#onDrawScrollBars(Canvas)}
     **/
    protected void onDrawHorizontalScrollBar(Canvas canvas, Drawable scrollBar, int l, int t, int r, int b) {
        scrollBar.setColorFilter(scrollBarColor, PorterDuff.Mode.SRC_ATOP);
        scrollBar.setBounds(l, t, r, b);
        scrollBar.draw(canvas);
    }

    /**
     * Called by Android {@link android.view.View#onDrawScrollBars(Canvas)}
     **/
    protected void onDrawVerticalScrollBar(Canvas canvas, Drawable scrollBar, int l, int t, int r, int b) {
        scrollBar.setColorFilter(scrollBarColor, PorterDuff.Mode.SRC_ATOP);
        scrollBar.setBounds(l, t, r, b);
        scrollBar.draw(canvas);
    }
}
package com.nezukoos.music.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.EdgeEffect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.nezukoos.music.themes.ColorUtil;
import com.nezukoos.music.themes.ThemeColors;

public class CustomRecyclerView extends RecyclerView {

    public CustomRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public CustomRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Called by Android {@link android.view.View#onDrawScrollBars(Canvas)}
     **/
    protected void onDrawHorizontalScrollBar(Canvas canvas, Drawable scrollBar, int l, int t, int r, int b) {
        scrollBar.setColorFilter(ThemeColors.getCurrentColorPrimary(), PorterDuff.Mode.SRC);
        scrollBar.setBounds(l, t, r, b);
        scrollBar.draw(canvas);
    }

    /**
     * Called by Android {@link android.view.View#onDrawScrollBars(Canvas)}
     **/
    protected void onDrawVerticalScrollBar(Canvas canvas, Drawable scrollBar, int l, int t, int r, int b) {
        scrollBar.setColorFilter(ThemeColors.getCurrentColorPrimary(), PorterDuff.Mode.SRC);
        scrollBar.setBounds(l, t, r, b);
        scrollBar.draw(canvas);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setEdgeEffectFactory(new EdgeEffectFactory() {
            @NonNull
            @Override
            protected EdgeEffect createEdgeEffect(@NonNull RecyclerView view, int direction) {
                EdgeEffect edgeEffect = new EdgeEffect(view.getContext());
                edgeEffect.setColor(ColorUtil.changeAlphaComponentTo(ThemeColors.getCurrentColorPrimary(), 0.04f));
                return edgeEffect;
            }
        });
    }
}
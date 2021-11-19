package com.radiant.music.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EdgeEffect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.radiant.music.themes.ColorUtil;
import com.radiant.music.themes.ThemeColors;


public class CustomFastScrollRecyclerView extends RecyclerView {

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
        int accentColor = ThemeColors.getCurrentColorPrimary();
        setEdgeEffectFactory(new EdgeEffectFactory() {
            @NonNull
            @Override
            protected EdgeEffect createEdgeEffect(@NonNull RecyclerView view, int direction) {
                EdgeEffect edgeEffect = new EdgeEffect(view.getContext());
                edgeEffect.setColor(ColorUtil.changeAlphaComponentTo(accentColor, 0.04f));
                return edgeEffect;
            }
        });

        new RadiantScrollerBuilder(this).useRadiantPopupStyle().build();
    }
}
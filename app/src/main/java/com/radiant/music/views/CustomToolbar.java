package com.radiant.music.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;

import com.radiant.music.themes.ThemeColors;

public class CustomToolbar extends Toolbar {

    public CustomToolbar(@NonNull Context context) {
        super(context);
    }

    public CustomToolbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomToolbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setNavigationIcon(@Nullable Drawable icon) {
        super.setNavigationIcon(applyTintToIcon(icon));
    }

    private Drawable applyTintToIcon(Drawable drawable) {
        if (drawable != null) {
            Drawable wrappedNavigationIcon = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(wrappedNavigationIcon, ThemeColors.getCurrentColorControlNormal());
            return wrappedNavigationIcon;
        }
        return null;
    }
}
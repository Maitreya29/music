package com.radiant.music.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.textview.MaterialTextView;
import com.radiant.music.R;
import com.radiant.music.themes.ColorUtil;
import com.radiant.music.themes.ThemeManagerUtils;
import com.radiant.music.utils.DimensionsUtil;

public class ButtonShortcut extends LinearLayout {

    public ButtonShortcut(Context context) {
        this(context, null);
    }

    public ButtonShortcut(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ButtonShortcut(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(@NonNull Context context, @Nullable AttributeSet attrs) {
        final int paddingVertical = DimensionsUtil.getDimensionPixelSize(context, 12);
        setPadding(0, paddingVertical, 0, paddingVertical);
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setMinimumHeight(DimensionsUtil.getDimensionPixelSize(context, 48));

        View contents = View.inflate(context, R.layout.button_shortcut, this);
        ImageView icon = contents.findViewById(R.id.shortcut_icon);
        MaterialTextView title = contents.findViewById(R.id.shortcut_title);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ButtonShortcut);
        if (typedArray.hasValue(R.styleable.ButtonShortcut_shortcutTitle))
            title.setText(typedArray.getString(R.styleable.ButtonShortcut_shortcutTitle));

        if (typedArray.hasValue(R.styleable.ButtonShortcut_shortcutIcon)) {
            icon.setImageDrawable(typedArray.getDrawable(R.styleable.ButtonShortcut_shortcutIcon));
            int iconColor = typedArray.getColor(R.styleable.ButtonShortcut_shortcutIconColor, 0);
            int backgroundTint = iconColor;

            boolean desaturated = ThemeManagerUtils.isAccentsDesaturated()
                    && typedArray.getBoolean(R.styleable.ButtonShortcut_colorsDesaturatedInDarkMode, true);

            if (desaturated) {
                iconColor = context.getResources().getColor(R.color.darkColorBackground);
                backgroundTint = ColorUtil.mixColors(backgroundTint, Color.WHITE, 0.4f);
            } else
                backgroundTint = ColorUtil.changeColorAlphaTo20(backgroundTint);

            Drawable background = ContextCompat.getDrawable(context, R.drawable.shape_rounded_rectangle);

            if (background != null) {
                background.mutate();
                background.setTint(backgroundTint);
            }

            setBackground(background);
            icon.setImageTintList(ColorStateList.valueOf(iconColor));
            title.setTextColor(iconColor);
        }
        typedArray.recycle();
    }
}
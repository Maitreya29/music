package com.nezukoos.music.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.nezukoos.music.R;
import com.nezukoos.music.themes.ColorUtil;
import com.nezukoos.music.themes.ThemeManagerUtils;

public class ColoredIconView extends AppCompatImageView {

    public ColoredIconView(Context context) {
        this(context, null);
    }

    public ColoredIconView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initialize(context, attrs);
    }

    public ColoredIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ColoredIconView);

        if (typedArray.getInt(R.styleable.ColoredIconView_iconBackgroundShape, 0) == 0)
            setBackground(ContextCompat.getDrawable(context, R.drawable.plain_circle));
        else setBackground(ContextCompat.getDrawable(context, R.drawable.shape_rounded_rectangle));

        int paddingPixels = typedArray.getDimensionPixelSize(R.styleable.ColoredIconView_iconPadding, context.getResources().getDimensionPixelSize(R.dimen.icon_padding));
        setPadding(paddingPixels, paddingPixels, paddingPixels, paddingPixels);

        setImageResource(typedArray.getResourceId(R.styleable.ColoredIconView_icon, R.drawable.def_colored_icon_view_icon));

        int iconColor = typedArray.getColor(R.styleable.ColoredIconView_iconColor, Color.BLUE);
        int backgroundColor = iconColor;

        boolean desaturated = ThemeManagerUtils.isAccentsDesaturated()
                && typedArray.getBoolean(R.styleable.ColoredIconView_desaturatedColorInDarkMode, true);

        if (desaturated) {
            iconColor = context.getResources().getColor(R.color.darkColorBackground);
            backgroundColor = ColorUtil.mixColors(backgroundColor, Color.WHITE, 0.4f);
        } else {
            float alpha = typedArray.getFloat(R.styleable.ColoredIconView_backgroundColorAlpha, 0.2f);
            backgroundColor = ColorUtil.changeAlphaComponentTo(backgroundColor, alpha);
        }

        setImageTintList(ColorStateList.valueOf(iconColor));
        setBackgroundTintList(ColorStateList.valueOf(backgroundColor));

        typedArray.recycle();
    }
}
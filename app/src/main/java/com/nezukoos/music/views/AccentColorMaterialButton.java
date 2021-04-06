package com.nezukoos.music.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.nezukoos.music.R;
import com.nezukoos.music.themes.TintHelper;

public class AccentColorMaterialButton extends MaterialButton {

    private final int buttonStyle;

    public AccentColorMaterialButton(@NonNull Context context) {
        this(context, null);
    }

    public AccentColorMaterialButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.materialButtonStyle);
    }

    public AccentColorMaterialButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AccentColorMaterialButton);
        buttonStyle = typedArray.getInt(R.styleable.AccentColorMaterialButton_accentButtonStyle, 0);
        typedArray.recycle();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (buttonStyle == 0 /* Text style button */) {
            TintHelper.setAccentColorToMaterialTextButton(this, true);
        } else if (buttonStyle == 1 /* Filled style button */) {
            TintHelper.setAccentTintToMaterialButton(this, true);
        } else if (buttonStyle == 2 /* Outline style button */) {
            TintHelper.setAccentTintToMaterialOutlineButton(this, true);
        }
    }
}
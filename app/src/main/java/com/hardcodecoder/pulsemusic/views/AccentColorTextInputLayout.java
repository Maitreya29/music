package com.hardcodecoder.pulsemusic.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.themes.ColorUtil;
import com.hardcodecoder.pulsemusic.themes.ThemeColors;

import java.lang.reflect.Field;

public class AccentColorTextInputLayout extends TextInputLayout {

    public AccentColorTextInputLayout(@NonNull Context context) {
        this(context, null);
    }

    public AccentColorTextInputLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AccentColorTextInputLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getEditText() == null) return;

        int colorOnSurface = ThemeColors.getCurrentColorOnSurface();

        int[][] states = new int[][]{
                new int[]{android.R.attr.state_focused}, // [0]
                new int[]{android.R.attr.state_hovered}, // [1]
                new int[]{-android.R.attr.state_enabled}, // [2]
                new int[]{}, // [3]
        };
        int[] colors = new int[states.length];

        colors[0] = ThemeColors.getAccentColorForCurrentTheme();
        colors[1] = ColorUtil.changeAlphaComponentTo(colorOnSurface, 0.42f);
        colors[2] = ColorUtil.changeAlphaComponentTo(colorOnSurface, 0.38f);
        colors[3] = ColorUtil.changeAlphaComponentTo(colorOnSurface, 0.42f);

        setDefaultHintTextColor(ColorStateList.valueOf(colorOnSurface));
        setHintTextColor(ThemeColors.getAccentColorStateList());
        setBoxStrokeColorStateList(new ColorStateList(states, colors));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Use setTextCursorDrawable to set custom tinted drawable
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.edit_text_cursor);
            if (null != drawable)
                drawable.setTint(ThemeColors.getAccentColorForCurrentTheme());
            getEditText().setTextCursorDrawable(drawable);
        } else {
            // Use reflection to try to change the cursor drawable color
            try {
                Field cursorField = TextView.class.getDeclaredField("mCursorDrawable");
                cursorField.setAccessible(true);
                TextView editText = getEditText();
                Drawable drawable = (Drawable) cursorField.get(editText);
                if (null != drawable)
                    drawable.setTint(ThemeColors.getAccentColorForCurrentTheme());
                cursorField.set(editText, drawable);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
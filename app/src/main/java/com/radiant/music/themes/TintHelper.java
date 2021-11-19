package com.radiant.music.themes;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.radiant.music.R;
import com.radiant.music.utils.DimensionsUtil;

import java.lang.reflect.Field;

public final class TintHelper {

    public static void setAccentTintTo(@NonNull ImageView imageView) {
        imageView.setImageTintList(ColorStateList.valueOf(ThemeColors.getCurrentColorPrimary()));
    }

    @NonNull
    public static Drawable setAccentTintTo(@NonNull Drawable drawable, boolean mutate) {
        return setTintTo(drawable, ThemeColors.getCurrentColorPrimary(), mutate);
    }

    @NonNull
    public static Drawable setTintTo(@NonNull Drawable drawable, @ColorInt int color, boolean mutate) {
        if (mutate) drawable.mutate();
        drawable.setTint(color);
        return drawable;
    }

    public static void setAccentTintTo(@NonNull FloatingActionButton fab) {
        fab.setBackgroundTintList(ThemeColors.getPrimaryColorStateList());
    }

    public static void setAccentTintToMaterialButton(@NonNull MaterialButton materialButton, boolean tintIcon) {
        materialButton.setBackgroundTintList(ThemeColors.getPrimaryColorStateList());
        materialButton.setRippleColor(ThemeColors.getMaterialButtonRippleColor());
        ColorStateList foregroundTint = ColorStateList.valueOf(ThemeColors.getCurrentColorOnPrimary());
        materialButton.setTextColor(foregroundTint);
        if (tintIcon) materialButton.setIconTint(foregroundTint);
    }

    public static void setAccentColorToMaterialTextButton(@NonNull MaterialButton materialTextButton, boolean tintIcon) {
        final ColorStateList accentColorStateList = ThemeColors.getPrimaryColorStateList();
        materialTextButton.setTextColor(accentColorStateList);
        if (tintIcon) materialTextButton.setIconTint(accentColorStateList);
        materialTextButton.setBackgroundTintList(ColorStateList.valueOf(0));
        materialTextButton.setRippleColor(ThemeColors.getMaterialButtonTextRippleColor());
        materialTextButton.setStateListAnimator(null);
        materialTextButton.setElevation(0);
    }

    public static void setAccentTintToMaterialOutlineButton(@NonNull MaterialButton materialOutlineButton, boolean tintIcon) {
        setAccentColorToMaterialTextButton(materialOutlineButton, tintIcon);
        materialOutlineButton.setStrokeWidth(
                DimensionsUtil.getDimensionPixelSize(materialOutlineButton.getContext(), 1));
        materialOutlineButton.setStrokeColor(ThemeColors.getMaterialOutlineColorSelector());
    }

    public static void setAccentTintToCursor(@NonNull EditText editText) {
        if (Build.VERSION.SDK_INT == 28) {
            // Cannot be applied using reflection
            // mDrawableForCursor in Editor class set's the drawable for cursor
            // but is blacklisted can cannot be accessed using reflection
            // back luck for api = 28 :(
            return;
        }

        int color = ThemeColors.getCurrentColorPrimary();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Use TextView#setTextCursorDrawable to set custom tinted drawable
            Drawable drawable = ContextCompat.getDrawable(editText.getContext(), R.drawable.edit_text_cursor);
            if (null != drawable)
                drawable.setTint(color);
            editText.setTextCursorDrawable(drawable);
        } else {
            // Use reflection to set for devices api < 28
            try {
                // Get the cursor resource id
                /*Field field = TextView.class.getDeclaredField("mCursorDrawableRes");
                field.setAccessible(true);
                int drawableResId = field.getInt(editText);*/

                // Get the editor
                Field field = TextView.class.getDeclaredField("mEditor");
                field.setAccessible(true);
                Object editor = field.get(editText);

                // Get the drawable and set a color filter
                //Drawable drawable = ContextCompat.getDrawable(editText.getContext(), drawableResId);
                Drawable drawable = ContextCompat.getDrawable(editText.getContext(), R.drawable.edit_text_cursor);
                assert drawable != null;
                //drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                drawable.setTint(color);

                // Set the drawables
                /*if (Build.VERSION.SDK_INT == 28) {//set differently in Android P (API 28)
                    assert editor != null;
                    field = editor.getClass().getDeclaredField("mDrawableForCursor");
                    field.setAccessible(true);
                    field.set(editor, drawable);
                } else {*/
                Drawable[] drawables = {drawable, drawable};
                assert editor != null;
                field = editor.getClass().getDeclaredField("mCursorDrawable");
                field.setAccessible(true);
                field.set(editor, drawables);
                //}

                // set the "selection handle" color too
                // colorHandles(editText); // Result is not satisfactory
            } catch (Exception exception) {
                Log.w("TintHelper", "Failed to change edit text cursor drawable tint");
            }
        }
    }

    /*
     * Set the color of the handles when you select text in a
     * {@link android.widget.EditText} or other view that extends {@link TextView}.
     *
     * @param view  The {@link TextView} or a {@link android.view.View} that extends {@link TextView}.
     * @see <a href="https://gist.github.com/jaredrummler/2317620559d10ac39b8218a1152ec9d4">External reference</a>

    public static void colorHandles(TextView view) {
        int color = ThemeColors.getCurrentAccentColor();
        try {
            Field editorField = TextView.class.getDeclaredField("mEditor");
            if (!editorField.isAccessible()) {
                editorField.setAccessible(true);
            }

            Object editor = editorField.get(view);
            assert editor != null;
            Class<?> editorClass = editor.getClass();

            String[] handleNames = {"mSelectHandleLeft", "mSelectHandleRight", "mSelectHandleCenter"};
            String[] resNames = {"mTextSelectHandleLeftRes", "mTextSelectHandleRightRes", "mTextSelectHandleRes"};

            for (int i = 0; i < handleNames.length; i++) {
                Field handleField = editorClass.getDeclaredField(handleNames[i]);
                if (!handleField.isAccessible())
                    handleField.setAccessible(true);

                Drawable handleDrawable = (Drawable) handleField.get(editor);

                if (handleDrawable == null) {
                    Field resField = TextView.class.getDeclaredField(resNames[i]);
                    if (!resField.isAccessible()) {
                        resField.setAccessible(true);
                    }
                    int resId = resField.getInt(view);
                    handleDrawable = ResourcesCompat.getDrawable(view.getResources(), resId, view.getContext().getTheme());
                }

                if (handleDrawable != null) {
                    //Drawable drawable = handleDrawable.mutate();
                    //drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    handleDrawable.setTint(color);
                    handleField.set(editor, handleDrawable);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
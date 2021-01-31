package com.hardcodecoder.pulsemusic.views;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.themes.ThemeColors;
import com.hardcodecoder.pulsemusic.utils.DimensionsUtil;

public class PulseToolbar extends LinearLayout {

    private final ImageView mNavigationIcon;
    private final MaterialTextView mTitle;
    private final ImageView mOptionsIcons;

    public PulseToolbar(Context context) {
        this(context, null, 0);
    }

    public PulseToolbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PulseToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, DimensionsUtil.getDimensionPixelSize(context, 56));
        setLayoutParams(params);

        setGravity(Gravity.CENTER_VERTICAL);
        setOrientation(LinearLayout.HORIZONTAL);

        View contents = View.inflate(context, R.layout.pulse_toolbar, this);
        mNavigationIcon = contents.findViewById(R.id.pt_navigation);
        mTitle = contents.findViewById(R.id.pt_title);
        mOptionsIcons = contents.findViewById(R.id.pt_option);
    }

    public void setTitle(@Nullable String title, boolean accentFirstLetter) {
        if (null == title) mTitle.setText(null);
        else if (accentFirstLetter) {
            SpannableString spannableString = new SpannableString(title);
            spannableString.setSpan(
                    new StyleSpan(Typeface.BOLD),
                    0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(
                    new ForegroundColorSpan(ThemeColors.getCurrentColorPrimary()),
                    0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mTitle.setText(spannableString);
        } else mTitle.setText(title);
    }

    public void setTitle(@StringRes int resId, boolean accentFirstLetter) {
        setTitle(getContext().getString(resId), accentFirstLetter);
    }

    public void setNavigationIcon(@DrawableRes int drawableRes) {
        mNavigationIcon.setImageResource(drawableRes);
    }

    public void setNavigationIcon(@Nullable Drawable drawable) {
        mNavigationIcon.setImageDrawable(drawable);
    }

    public void setNavigationIconOnClickListener(@Nullable OnClickListener listener) {
        mNavigationIcon.setOnClickListener(listener);
    }

    public void setOptionsIcon(@DrawableRes int drawableRes) {
        mOptionsIcons.setImageResource(drawableRes);
    }

    public void setOptionsIcon(@Nullable Drawable drawable) {
        mOptionsIcons.setImageDrawable(drawable);
    }

    public void setOptionIconOnClickListener(@Nullable OnClickListener listener) {
        mOptionsIcons.setOnClickListener(listener);
    }
}
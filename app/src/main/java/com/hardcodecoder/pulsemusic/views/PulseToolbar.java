package com.hardcodecoder.pulsemusic.views;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.themes.ThemeColors;
import com.hardcodecoder.pulsemusic.utils.DimensionsUtil;

public class PulseToolbar extends RelativeLayout {

    private final ImageView mNavigationIcon;
    private final MaterialTextView mTitle;
    private final ImageView mQuickActionIcon;
    private final ImageView mOverflowIcon;

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

        View contents = View.inflate(context, R.layout.pulse_toolbar, this);
        mNavigationIcon = contents.findViewById(R.id.pt_navigation);
        mTitle = contents.findViewById(R.id.pt_title);
        mQuickActionIcon = contents.findViewById(R.id.pt_option_always_visible);
        mOverflowIcon = contents.findViewById(R.id.pt_options);
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

    public void setQuickActionIcon(@DrawableRes int drawableRes) {
        mQuickActionIcon.setImageResource(drawableRes);
    }

    public void setQuickActionIcon(@Nullable Drawable drawable) {
        mQuickActionIcon.setImageDrawable(drawable);
    }

    public void setQuickActionIconOnClickListener(@Nullable OnClickListener listener) {
        mQuickActionIcon.setOnClickListener(listener);
    }

    public void setOverflowIcon(@DrawableRes int drawableRes) {
        mOverflowIcon.setImageResource(drawableRes);
    }

    public void setOverflowIcon(@Nullable Drawable drawable) {
        mOverflowIcon.setImageDrawable(drawable);
    }

    public void setOverflowIconOnClickListener(@Nullable OnClickListener listener) {
        mOverflowIcon.setOnClickListener(listener);
    }

    public void showOverflowIcon(boolean show) {
        mOverflowIcon.setVisibility(show ? VISIBLE : GONE);
    }
}
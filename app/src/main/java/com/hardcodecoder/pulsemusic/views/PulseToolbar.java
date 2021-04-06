package com.hardcodecoder.pulsemusic.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.themes.ThemeColors;
import com.hardcodecoder.pulsemusic.utils.DimensionsUtil;

public class PulseToolbar extends RelativeLayout {

    private final Context mContext;
    private final int mSelectableItemBackgroundBorderless;
    private ImageView mNavigationIcon;
    private MaterialTextView mTitle;
    private ImageView mQuickActionIcon;
    private ImageView mOverflowIcon;
    private boolean mAccentFirstLetter;

    public PulseToolbar(Context context) {
        this(context, null, 0);
    }

    public PulseToolbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PulseToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
        mContext = getContext();

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, DimensionsUtil.getDimensionPixelSize(context, 56));
        setLayoutParams(params);

        TypedValue outValue = new TypedValue();
        mContext.getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
        mSelectableItemBackgroundBorderless = outValue.resourceId;

        inflateTitle();

        TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.PulseToolbar);

        if (array.hasValue(R.styleable.PulseToolbar_toolbarNavigationIcon)) {
            inflateNavigation();
            setNavigationIcon(array.getDrawable(R.styleable.PulseToolbar_toolbarNavigationIcon));
        }
        if (array.hasValue(R.styleable.PulseToolbar_toolbarOverflowIcon)) {
            inflateOverFlow();
            setOverflowIcon(array.getDrawable(R.styleable.PulseToolbar_toolbarOverflowIcon));
        }
        if (array.hasValue(R.styleable.PulseToolbar_toolbarQuickActionIcon)) {
            inflateQuickAction();
            setQuickActionIcon(array.getDrawable(R.styleable.PulseToolbar_toolbarQuickActionIcon));
        }

        accentFirstLetterOfTitle(array.getBoolean(R.styleable.PulseToolbar_toolbarAccentTitle, true));
        array.recycle();
    }

    private void inflateTitle() {
        mTitle = new MaterialTextView(mContext);
        mTitle.setId(View.generateViewId());

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        int margin = DimensionsUtil.getDimensionPixelSize(mContext, 84);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.setMarginStart(margin);
        params.setMarginEnd(margin);
        mTitle.setLayoutParams(params);

        mTitle.setGravity(Gravity.CENTER);
        mTitle.setTextAppearance(mContext, R.style.Appearance_Text_Base_Headline6);

        addView(mTitle);
    }

    private void inflateNavigation() {
        mNavigationIcon = new ImageView(mContext);
        mNavigationIcon.setId(View.generateViewId());

        final int side = DimensionsUtil.getDimensionPixelSize(getContext(), 36);
        LayoutParams params = new LayoutParams(side, side);
        params.addRule(RelativeLayout.ALIGN_PARENT_START);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.setMarginStart(DimensionsUtil.getDimensionPixelSize(mContext, 10));
        mNavigationIcon.setLayoutParams(params);

        mNavigationIcon.setBackgroundResource(mSelectableItemBackgroundBorderless);
        mNavigationIcon.setContentDescription(mContext.getString(R.string.desc_action_btn));

        final int padding = DimensionsUtil.getDimensionPixelSize(getContext(), 6);
        mNavigationIcon.setPadding(padding, padding, padding, padding);

        mNavigationIcon.setImageTintList(ThemeColors.getColorControlNormalTintList());

        addView(mNavigationIcon);
    }

    private void inflateQuickAction() {
        mQuickActionIcon = new ImageView(mContext);
        mQuickActionIcon.setId(View.generateViewId());

        final int side = DimensionsUtil.getDimensionPixelSize(getContext(), 36);
        LayoutParams params = new LayoutParams(side, side);
        if (null != mOverflowIcon) {
            params.addRule(RelativeLayout.START_OF, mOverflowIcon.getId());
            params.alignWithParent = true;
        } else params.addRule(RelativeLayout.ALIGN_PARENT_END);

        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.setMarginEnd(DimensionsUtil.getDimensionPixelSize(mContext, 10));
        mQuickActionIcon.setLayoutParams(params);

        mQuickActionIcon.setBackgroundResource(mSelectableItemBackgroundBorderless);
        mQuickActionIcon.setContentDescription(mContext.getString(R.string.desc_action_btn));

        final int padding = DimensionsUtil.getDimensionPixelSize(getContext(), 6);
        mQuickActionIcon.setPadding(padding, padding, padding, padding);

        mQuickActionIcon.setImageTintList(ThemeColors.getColorControlNormalTintList());

        addView(mQuickActionIcon);
    }

    private void inflateOverFlow() {
        mOverflowIcon = new ImageView(mContext);
        mOverflowIcon.setId(View.generateViewId());

        final int side = DimensionsUtil.getDimensionPixelSize(getContext(), 36);
        LayoutParams params = new LayoutParams(side, side);
        params.addRule(RelativeLayout.ALIGN_PARENT_END);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.setMarginEnd(DimensionsUtil.getDimensionPixelSize(mContext, 10));
        mOverflowIcon.setLayoutParams(params);

        mOverflowIcon.setBackgroundResource(mSelectableItemBackgroundBorderless);
        mOverflowIcon.setContentDescription(mContext.getString(R.string.desc_action_btn));

        final int padding = DimensionsUtil.getDimensionPixelSize(getContext(), 6);
        mOverflowIcon.setPadding(padding, padding, padding, padding);

        mOverflowIcon.setImageTintList(ThemeColors.getColorControlNormalTintList());

        addView(mOverflowIcon);
    }

    public void accentFirstLetterOfTitle(boolean accented) {
        mAccentFirstLetter = accented;
    }

    public void setTitle(@Nullable String title) {
        if (null == title) mTitle.setText(null);
        else mTitle.setText(title);
    }

    public void setNavigationIcon(@DrawableRes int drawableRes) {
        setNavigationIcon(ContextCompat.getDrawable(mContext, drawableRes));
    }

    public void setNavigationIcon(@Nullable Drawable drawable) {
        mNavigationIcon.setImageDrawable(drawable);
    }

    public void setNavigationIconOnClickListener(@Nullable OnClickListener listener) {
        if (null == mNavigationIcon)
            throw new IllegalStateException("Cannot set listener on a null view");
        mNavigationIcon.setOnClickListener(listener);
    }

    public void setQuickActionIcon(@DrawableRes int drawableRes) {
        setQuickActionIcon(ContextCompat.getDrawable(mContext, drawableRes));
    }

    public void setQuickActionIcon(@Nullable Drawable drawable) {
        mQuickActionIcon.setImageDrawable(drawable);
    }

    public void setQuickActionIconOnClickListener(@Nullable OnClickListener listener) {
        if (null == mQuickActionIcon)
            throw new IllegalStateException("Cannot set listener on a null view");
        mQuickActionIcon.setOnClickListener(listener);
    }

    public void setOverflowIcon(@DrawableRes int drawableRes) {
        setOverflowIcon(ContextCompat.getDrawable(mContext, drawableRes));
    }

    public void setOverflowIcon(@Nullable Drawable drawable) {
        mOverflowIcon.setImageDrawable(drawable);
    }

    public void setOverflowIconOnClickListener(@Nullable OnClickListener listener) {
        if (null == mOverflowIcon)
            throw new IllegalStateException("Cannot set listener on a null view");
        mOverflowIcon.setOnClickListener(listener);
    }

    public void showOverflowIcon(boolean show) {
        if (null == mOverflowIcon) inflateOverFlow();
        mOverflowIcon.setVisibility(show ? VISIBLE : GONE);
    }
}
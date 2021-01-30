package com.hardcodecoder.pulsemusic.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.themes.ColorUtil;
import com.hardcodecoder.pulsemusic.themes.ThemeManagerUtils;
import com.hardcodecoder.pulsemusic.utils.DimensionsUtil;

public class SettingsCategoryItemView extends RelativeLayout {

    private final MaterialTextView mTitle;
    private final MaterialTextView mText;
    private ImageView mIcon;

    public SettingsCategoryItemView(@NonNull Context context) {
        this(context, null, 0);
    }

    public SettingsCategoryItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingsCategoryItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Update this root layout dimensions
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int marginHorizontal = DimensionsUtil.getDimensionPixelSize(getContext(), 2);
        params.setMarginStart(marginHorizontal);
        params.setMarginEnd(marginHorizontal);
        setLayoutParams(params);

        final int paddingDef = DimensionsUtil.getDimensionPixelSize(context, 16);
        setPadding(DimensionsUtil.getDimensionPixelSize(context, 8), paddingDef, paddingDef, paddingDef);

        TypedArray array = context.obtainStyledAttributes(ThemeManagerUtils.getThemeToApply(), new int[]{android.R.attr.selectableItemBackground});
        setBackground(array.getDrawable(0));
        array.recycle();

        View view = View.inflate(context, R.layout.settings_category_list_item, this);
        mTitle = view.findViewById(R.id.settings_list_item_title);
        mText = view.findViewById(R.id.settings_list_item_text);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingsCategoryItemView);

        if (mTitle.getTypeface() != null && typedArray.hasValue(R.styleable.SettingsCategoryItemView_android_textStyle)) {
            mTitle.setTypeface(mTitle.getTypeface(), typedArray.getInteger(R.styleable.SettingsCategoryItemView_android_textStyle, Typeface.NORMAL));
        }

        if (typedArray.hasValue(R.styleable.SettingsCategoryItemView_settingItemIcon)) {
            mIcon = view.findViewById(R.id.settings_list_item_icon);
            mIcon.setImageDrawable(typedArray.getDrawable(R.styleable.SettingsCategoryItemView_settingItemIcon));

            int iconColor = typedArray.getColor(R.styleable.SettingsCategoryItemView_settingItemIconColor, Color.BLUE);
            int iconBackgroundColor = typedArray.getColor(R.styleable.SettingsCategoryItemView_settingItemIconBackgroundColor, iconColor);

            boolean desaturated = ThemeManagerUtils.isAccentsDesaturated()
                    && typedArray.getBoolean(R.styleable.SettingsCategoryItemView_settingItemDesaturatedColorsInDarkMode, true);

            if (desaturated) {
                iconColor = context.getResources().getColor(R.color.darkColorBackground);
                iconBackgroundColor = ColorUtil.mixColors(iconBackgroundColor, Color.WHITE, 0.4f);
            } else
                iconBackgroundColor = ColorUtil.changeColorAlphaTo20(iconBackgroundColor);

            mIcon.setBackgroundTintList(ColorStateList.valueOf(iconBackgroundColor));
            mIcon.setImageTintList(ColorStateList.valueOf(iconColor));
        }

        mTitle.setText(typedArray.getText(R.styleable.SettingsCategoryItemView_settingItemTitle));
        mText.setText(typedArray.getText(R.styleable.SettingsCategoryItemView_settingItemText));

        typedArray.recycle();
    }

    @Override
    public void setEnabled(boolean enabled) {
        mTitle.setEnabled(enabled);
        mText.setEnabled(enabled);
        if (null != mIcon) mIcon.setEnabled(enabled);
        super.setEnabled(enabled);
    }
}
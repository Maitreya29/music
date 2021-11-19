package com.radiant.music.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;
import com.radiant.music.R;
import com.radiant.music.themes.ColorUtil;
import com.radiant.music.themes.ThemeColors;
import com.radiant.music.themes.ThemeManagerUtils;
import com.radiant.music.utils.DimensionsUtil;

public class SettingsCategoryItem extends RelativeLayout {

    private final MaterialTextView mTitle;
    private final MaterialTextView mText;
    private ImageView mIcon;

    public SettingsCategoryItem(@NonNull Context context) {
        this(context, null, 0);
    }

    public SettingsCategoryItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingsCategoryItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final int paddingDef = DimensionsUtil.getDimensionPixelSize(context, 12);
        setPadding(DimensionsUtil.getDimensionPixelSize(context, 8), paddingDef, paddingDef, paddingDef);

        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        setBackgroundResource(outValue.resourceId);

        View view = View.inflate(context, R.layout.settings_category_list_item, this);
        mTitle = view.findViewById(R.id.settings_list_item_title);
        mText = view.findViewById(R.id.settings_list_item_text);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingsCategoryItem);

        if (mTitle.getTypeface() != null && typedArray.hasValue(R.styleable.SettingsCategoryItem_android_textStyle)) {
            mTitle.setTypeface(mTitle.getTypeface(), typedArray.getInteger(R.styleable.SettingsCategoryItem_android_textStyle, Typeface.NORMAL));
        }

        if (typedArray.hasValue(R.styleable.SettingsCategoryItem_settingItemIcon)) {
            mIcon = view.findViewById(R.id.settings_list_item_icon);
            mIcon.setImageDrawable(typedArray.getDrawable(R.styleable.SettingsCategoryItem_settingItemIcon));

            int iconColor = typedArray.getColor(R.styleable.SettingsCategoryItem_settingItemIconColor,
                    ThemeColors.getCurrentColorControlNormal());

            boolean isColoredIcon = typedArray.getBoolean(R.styleable.SettingsCategoryItem_settingItemColoredIcon, false);
            int iconBackgroundColor = isColoredIcon ? iconColor : ThemeColors.getCurrentColorBackgroundHighlight();

            if (isColoredIcon) {
                boolean desaturated = ThemeManagerUtils.isAccentsDesaturated();
                if (desaturated) {
                    iconColor = context.getResources().getColor(R.color.darkColorBackground);
                    iconBackgroundColor = ColorUtil.mixColors(iconBackgroundColor, Color.WHITE, 0.4f);
                } else {
                    iconBackgroundColor = ColorUtil.changeAlphaComponentTo(iconBackgroundColor,
                            typedArray.getFloat(R.styleable.SettingsCategoryItem_settingItemBackgroundAlpha, 0.2f));
                }
            }

            mIcon.setBackgroundTintList(ColorStateList.valueOf(iconBackgroundColor));
            mIcon.setImageTintList(ColorStateList.valueOf(iconColor));
        }

        mTitle.setText(typedArray.getText(R.styleable.SettingsCategoryItem_settingItemTitle));
        mText.setText(typedArray.getText(R.styleable.SettingsCategoryItem_settingItemText));

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
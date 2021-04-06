package com.nezukoos.music.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textview.MaterialTextView;
import com.nezukoos.music.R;
import com.nezukoos.music.themes.ThemeColors;
import com.nezukoos.music.utils.DimensionsUtil;

public class SettingsToggleableItem extends RelativeLayout {

    private final MaterialTextView mTitle;
    private final MaterialTextView mText;
    private final SwitchCompat mSwitchButton;
    private ImageView mIcon;

    public SettingsToggleableItem(@NonNull Context context) {
        this(context, null, 0);
    }

    public SettingsToggleableItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingsToggleableItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final int paddingDef = DimensionsUtil.getDimensionPixelSize(context, 16);
        setPadding(DimensionsUtil.getDimensionPixelSize(context, 8), paddingDef, paddingDef, paddingDef);

        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        setBackgroundResource(outValue.resourceId);

        View view = View.inflate(context, R.layout.settings_toggleable_item_layout, this);
        mTitle = view.findViewById(R.id.setting_toggleable_item_title);
        mText = view.findViewById(R.id.setting_toggleable_item_text);
        mSwitchButton = view.findViewById(R.id.setting_toggleable_item_switch);
        //Do not save state
        mSwitchButton.setSaveEnabled(false);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingsToggleableItem);
        mTitle.setText(typedArray.getText(R.styleable.SettingsToggleableItem_settingToggleableItemTitle));
        mText.setText(typedArray.getText(R.styleable.SettingsToggleableItem_settingToggleableItemText));

        if (typedArray.hasValue(R.styleable.SettingsToggleableItem_settingToggleableItemIcon)) {
            inflateIcon(typedArray.getDrawable(R.styleable.SettingsToggleableItem_settingToggleableItemIcon));
            int iconColor = ThemeColors.getCurrentColorControlNormal();
            int iconBackgroundColor = ThemeColors.getCurrentColorBackgroundHighlight();
            mIcon.setBackgroundTintList(ColorStateList.valueOf(iconBackgroundColor));
            mIcon.setImageTintList(ColorStateList.valueOf(iconColor));
        }

        typedArray.recycle();

        // Added onClick listener to toggle switch state
        setOnClickListener(v -> mSwitchButton.setChecked(!mSwitchButton.isChecked()));
    }

    private void inflateIcon(@Nullable Drawable icon) {
        Context context = getContext();
        mIcon = new ImageView(context);
        mIcon.setId(View.generateViewId());
        int side = context.getResources().getDimensionPixelSize(R.dimen.colored_icon_side);
        LayoutParams params = new LayoutParams(side, side);
        params.addRule(ALIGN_PARENT_START);
        params.addRule(CENTER_VERTICAL);
        mIcon.setLayoutParams(params);

        int padding = context.getResources().getDimensionPixelSize(R.dimen.colored_icon_padding);
        mIcon.setPadding(padding, padding, padding, padding);
        mIcon.setBackground(ContextCompat.getDrawable(context, R.drawable.plain_circle));
        mIcon.setImageDrawable(icon);

        addView(mIcon);
    }

    @Override
    public void setEnabled(boolean enabled) {
        mTitle.setEnabled(enabled);
        mText.setEnabled(enabled);
        mSwitchButton.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    public boolean isSwitchChecked() {
        return mSwitchButton.isChecked();
    }

    public void setSwitchChecked(boolean checked) {
        mSwitchButton.setChecked(checked);
    }

    public void setOnSwitchCheckedChangedListener(SwitchCompat.OnCheckedChangeListener checkedListener) {
        mSwitchButton.setOnCheckedChangeListener(checkedListener);
    }
}
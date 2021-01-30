package com.hardcodecoder.pulsemusic.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.themes.ThemeManagerUtils;
import com.hardcodecoder.pulsemusic.utils.DimensionsUtil;

public class SettingsToggleableItem extends RelativeLayout {

    private final MaterialTextView mTitle;
    private final MaterialTextView mText;
    private final SwitchCompat mSwitchButton;

    public SettingsToggleableItem(@NonNull Context context) {
        this(context, null, 0);
    }

    public SettingsToggleableItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingsToggleableItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Update this root layout dimensions
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int marginHorizontal = DimensionsUtil.getDimensionPixelSize(getContext(), 2);
        params.setMarginStart(marginHorizontal);
        params.setMarginEnd(marginHorizontal);
        setLayoutParams(params);

        final int paddingDef = DimensionsUtil.getDimensionPixelSize(context, 16);
        setPadding(DimensionsUtil.getDimensionPixelSize(context, 70), paddingDef, paddingDef, paddingDef);

        TypedArray array = context.obtainStyledAttributes(ThemeManagerUtils.getThemeToApply(), new int[]{android.R.attr.selectableItemBackground});
        setBackground(array.getDrawable(0));
        array.recycle();

        View view = View.inflate(context, R.layout.settings_toggleable_item_layout, this);
        mTitle = view.findViewById(R.id.setting_toggleable_item_title);
        mText = view.findViewById(R.id.setting_toggleable_item_text);
        mSwitchButton = view.findViewById(R.id.setting_toggleable_item_switch);
        //Do not save state
        mSwitchButton.setSaveEnabled(false);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingsToggleableItem);
        mTitle.setText(typedArray.getText(R.styleable.SettingsToggleableItem_settingToggleableItemTitle));
        mText.setText(typedArray.getText(R.styleable.SettingsToggleableItem_settingToggleableItemText));

        typedArray.recycle();

        // Added onClick listener to toggle switch state
        setOnClickListener(v -> mSwitchButton.setChecked(!mSwitchButton.isChecked()));
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
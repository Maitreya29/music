package com.hardcodecoder.pulsemusic.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;

public class SettingsToggleableItem extends FrameLayout {

    private MaterialTextView title;
    private MaterialTextView text;
    private SwitchCompat switchButton;

    public SettingsToggleableItem(@NonNull Context context) {
        this(context, null, 0);
    }

    public SettingsToggleableItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingsToggleableItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = View.inflate(context, R.layout.settings_toggleable_item_layout, this);
        title = view.findViewById(R.id.setting_toggleable_item_title);
        text = view.findViewById(R.id.setting_toggleable_item_text);
        switchButton = view.findViewById(R.id.setting_toggleable_item_switch);
        //Do not save state
        switchButton.setSaveEnabled(false);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingsToggleableItem);
        title.setText(typedArray.getText(R.styleable.SettingsToggleableItem_settingToggleableItemTitle));
        text.setText(typedArray.getText(R.styleable.SettingsToggleableItem_settingToggleableItemText));

        typedArray.recycle();

        // Added onClick listener to toggle switch state
        setOnClickListener(v -> switchButton.setChecked(!switchButton.isChecked()));
    }

    @Override
    public void setEnabled(boolean enabled) {
        title.setEnabled(enabled);
        text.setEnabled(enabled);
        switchButton.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    public boolean isSwitchChecked() {
        return switchButton.isChecked();
    }

    public void setSwitchChecked(boolean checked) {
        switchButton.setChecked(checked);
    }

    public void setOnSwitchCheckedChangedListener(SwitchCompat.OnCheckedChangeListener checkedListener) {
        switchButton.setOnCheckedChangeListener(checkedListener);
    }
}
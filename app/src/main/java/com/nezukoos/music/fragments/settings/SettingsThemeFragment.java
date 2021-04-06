package com.nezukoos.music.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nezukoos.music.R;
import com.nezukoos.music.dialog.DarkThemeChooser;
import com.nezukoos.music.dialog.PresetAccentsChooser;
import com.nezukoos.music.fragments.settings.base.SettingsBaseFragment;
import com.nezukoos.music.themes.ThemeManagerUtils;
import com.nezukoos.music.utils.AppSettings;
import com.nezukoos.music.views.SettingsToggleableItem;

public class SettingsThemeFragment extends SettingsBaseFragment {

    public static final String TAG = SettingsThemeFragment.class.getSimpleName();

    @NonNull
    public static SettingsThemeFragment getInstance() {
        return new SettingsThemeFragment();
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public int getToolbarTitleForFragment() {
        return R.string.theme;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_theme, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateThemeSection(view);

        SettingsToggleableItem desaturatedAccentItem = view.findViewById(R.id.laf_enable_desaturated);

        boolean desaturatedAccents;
        desaturatedAccents = AppSettings.isAccentDesaturated(requireContext());

        desaturatedAccentItem.setSwitchChecked(desaturatedAccents);
        desaturatedAccentItem.setOnSwitchCheckedChangedListener((buttonView, isChecked) -> {
            AppSettings.setAccentDesaturated(requireActivity(), isChecked);
            if (ThemeManagerUtils.isDarkModeEnabled())
                applyTheme();
        });

        view.findViewById(R.id.laf_select_accent_color).setOnClickListener(v -> {
            PresetAccentsChooser presetAccentsChooser = PresetAccentsChooser.getInstance();
            presetAccentsChooser.show(requireFragmentManager(), PresetAccentsChooser.TAG);
        });
    }

    private void updateThemeSection(@NonNull View view) {
        view.findViewById(R.id.laf_select_dark_theme).setOnClickListener(v -> {
            DarkThemeChooser darkThemeChooser = DarkThemeChooser.getInstance();
            darkThemeChooser.show(requireFragmentManager(), DarkThemeChooser.TAG);
        });

        // Get custom views
        SettingsToggleableItem enableDarkThemeItem = view.findViewById(R.id.laf_enable_dark_mode);
        SettingsToggleableItem enableAutoThemeItem = view.findViewById(R.id.laf_enable_auto_mode);

        // Configure state of views based on saved settings
        boolean darkModeEnable = AppSettings.isDarkModeEnabled(requireContext());
        enableDarkThemeItem.setSwitchChecked(darkModeEnable);

        boolean autoModeEnable = AppSettings.isAutoThemeEnabled(requireContext());
        enableAutoThemeItem.setSwitchChecked(autoModeEnable);
        enableDarkThemeItem.setEnabled(!autoModeEnable);

        //Add listeners to switch views
        enableDarkThemeItem.setOnSwitchCheckedChangedListener((buttonView, isChecked) -> {
            if (ThemeManagerUtils.toggleDarkTheme(requireContext(), isChecked))
                applyTheme();
        });

        enableAutoThemeItem.setOnSwitchCheckedChangedListener((buttonView, isChecked) -> {
            enableDarkThemeItem.setEnabled(!isChecked);
            if (ThemeManagerUtils.toggleAutoTheme(requireContext(), isChecked))
                applyTheme();
            else {
                // User does not want auto theme based on time of day
                // Revert to theme selected via darkThemeToggle
                if (enableDarkThemeItem.isSwitchChecked() && !ThemeManagerUtils.isDarkModeEnabled()) {
                    // User previously select dark theme so when auto theme is
                    // disabled apply dark theme if not already applied
                    applyTheme();
                } else if (!enableDarkThemeItem.isSwitchChecked() && ThemeManagerUtils.isDarkModeEnabled()) {
                    // User previously select light theme so when auto theme is
                    // disabled apply light theme if not already applied
                    applyTheme();
                }
            }
        });
    }

    private void applyTheme() {
        ThemeManagerUtils.init(requireActivity(), true);
        requestActivityRestart();
    }
}
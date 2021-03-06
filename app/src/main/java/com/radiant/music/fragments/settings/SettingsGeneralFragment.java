package com.radiant.music.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.radiant.music.R;
import com.radiant.music.dialog.ConfigurePlaylistSection;
import com.radiant.music.dialog.IgnoreFolderChooser;
import com.radiant.music.fragments.settings.base.SettingsBaseFragment;
import com.radiant.music.utils.AppSettings;
import com.radiant.music.views.SettingsCategoryItem;
import com.radiant.music.views.SettingsToggleableItem;
import com.radiant.music.views.ValueSlider;

public class SettingsGeneralFragment extends SettingsBaseFragment {

    public static final String TAG = SettingsGeneralFragment.class.getSimpleName();
    private ValueSlider mDurationFilter;
    private int mCurrentFilterDuration;

    @NonNull
    public static SettingsGeneralFragment getInstance() {
        return new SettingsGeneralFragment();
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public int getToolbarTitleForFragment() {
        return R.string.general;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_general, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.ignore_folder_picker).setOnClickListener(v -> {
            IgnoreFolderChooser ignoreFolderChooser = IgnoreFolderChooser.getInstance(hasChanged -> {
                if (hasChanged) requiresApplicationRestart(true);
            });
            ignoreFolderChooser.show(requireFragmentManager(), IgnoreFolderChooser.TAG);
        });

        mDurationFilter = view.findViewById(R.id.duration_filter_slider);
        mCurrentFilterDuration = AppSettings.getFilterDuration(requireContext());
        mDurationFilter.setSliderValue(mCurrentFilterDuration);

        boolean remember = AppSettings.isRememberPlaylistEnabled(requireContext());
        SettingsToggleableItem rememberOption = view.findViewById(R.id.remember_playlist_option);
        rememberOption.setSwitchChecked(remember);
        rememberOption.setOnSwitchCheckedChangedListener((buttonView, isChecked) ->
                AppSettings.setRememberPlaylist(requireContext(), isChecked));

        SettingsCategoryItem editPlaylist = view.findViewById(R.id.home_section_playlist_selector);
        editPlaylist.setOnClickListener(v -> {
            if (null != getFragmentManager()) {
                ConfigurePlaylistSection configurePlaylistSection = ConfigurePlaylistSection.getInstance(needsRestartOnDismiss -> {
                    if (needsRestartOnDismiss) requiresApplicationRestart(false);
                });
                configurePlaylistSection.show(getFragmentManager(), ConfigurePlaylistSection.TAG);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        int newFilterDuration = mDurationFilter.getSliderValue();
        if (mCurrentFilterDuration != newFilterDuration) {
            AppSettings.setFilterDuration(requireContext(), newFilterDuration);
            requiresApplicationRestart(true);
        }
    }
}
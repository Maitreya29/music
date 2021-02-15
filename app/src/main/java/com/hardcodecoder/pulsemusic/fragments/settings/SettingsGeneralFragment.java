package com.hardcodecoder.pulsemusic.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.dialog.IgnoreFolderChooser;
import com.hardcodecoder.pulsemusic.dialog.PlaylistSectionSelector;
import com.hardcodecoder.pulsemusic.fragments.settings.base.SettingsBaseFragment;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.views.SettingsCategoryItemView;
import com.hardcodecoder.pulsemusic.views.SettingsToggleableItem;
import com.hardcodecoder.pulsemusic.views.ValueSlider;

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

        boolean remember = AppSettings.rememberPlaylistEnabled(requireContext());
        SettingsToggleableItem rememberOption = view.findViewById(R.id.remember_playlist_option);
        rememberOption.setSwitchChecked(remember);
        rememberOption.setOnSwitchCheckedChangedListener((buttonView, isChecked) ->
                AppSettings.setRememberPlaylist(requireContext(), isChecked));

        SettingsCategoryItemView editPlaylist = view.findViewById(R.id.home_section_playlist_selector);
        editPlaylist.setOnClickListener(v -> {
            if (null != getFragmentManager()) {
                PlaylistSectionSelector playlistSectionSelector = PlaylistSectionSelector.getInstance(needsRestartOnDismiss -> {
                    if (needsRestartOnDismiss) requiresApplicationRestart(false);
                });
                playlistSectionSelector.show(getFragmentManager(), PlaylistSectionSelector.TAG);
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
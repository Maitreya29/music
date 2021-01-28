package com.hardcodecoder.pulsemusic.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.dialog.CornerRadiusChangeDialogFragment;
import com.hardcodecoder.pulsemusic.dialog.NowPlayingStyleChooser;
import com.hardcodecoder.pulsemusic.dialog.SeekDurationSelector;
import com.hardcodecoder.pulsemusic.fragments.settings.base.SettingsBaseFragment;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.views.SettingsCategoryItemView;
import com.hardcodecoder.pulsemusic.views.SettingsToggleableItem;

public class SettingsNowPlayingFragment extends SettingsBaseFragment {

    public static final String TAG = SettingsNowPlayingFragment.class.getSimpleName();

    @NonNull
    public static SettingsNowPlayingFragment getInstance() {
        return new SettingsNowPlayingFragment();
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public int getToolbarTitleForFragment() {
        return R.string.now_playing_title;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_now_playing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.now_playing_screen_style).setOnClickListener(v -> {
            NowPlayingStyleChooser dialog = NowPlayingStyleChooser.getInstance();
            dialog.show(requireFragmentManager(), NowPlayingStyleChooser.TAG);
        });

        view.findViewById(R.id.now_playing_album_cover_corner_radius).setOnClickListener(v -> {
            CornerRadiusChangeDialogFragment dialog = CornerRadiusChangeDialogFragment.getInstance();
            dialog.show(requireFragmentManager(), CornerRadiusChangeDialogFragment.TAG);
        });

        SettingsToggleableItem seekButtonsEnabler = view.findViewById(R.id.np_enable_seek_buttons);
        SettingsCategoryItemView seekDurationSelector = view.findViewById(R.id.now_playing_seek_duration_selector);

        final boolean seekEnabled = AppSettings.isSeekButtonsEnabled(requireContext());
        seekButtonsEnabler.setSwitchChecked(seekEnabled);
        seekDurationSelector.setEnabled(seekEnabled);

        seekButtonsEnabler.setOnSwitchCheckedChangedListener((buttonView, isChecked) -> {
            seekDurationSelector.setEnabled(isChecked);
            AppSettings.setSeekButtonsEnabled(requireContext(), isChecked);
        });

        seekDurationSelector.setOnClickListener(v -> {
            SeekDurationSelector seekDurationSelectorDialog = SeekDurationSelector.getInstance();
            seekDurationSelectorDialog.show(requireFragmentManager(), SeekDurationSelector.TAG);
        });
    }
}
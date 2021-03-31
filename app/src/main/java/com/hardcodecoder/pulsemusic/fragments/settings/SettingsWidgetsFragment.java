package com.hardcodecoder.pulsemusic.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.dialog.AutoPlayActionChooser;
import com.hardcodecoder.pulsemusic.dialog.WidgetBackgroundAlphaChooser;
import com.hardcodecoder.pulsemusic.fragments.settings.base.SettingsBaseFragment;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

public class SettingsWidgetsFragment extends SettingsBaseFragment {

    public static final String TAG = SettingsBaseFragment.class.getSimpleName();

    @NonNull
    public static SettingsWidgetsFragment getInstance() {
        return new SettingsWidgetsFragment();
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public int getToolbarTitleForFragment() {
        return R.string.widgets;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_widgets, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.widget_play_action_picker).setOnClickListener(v -> {
            AutoPlayActionChooser dialog = new AutoPlayActionChooser(
                    getString(R.string.widget_play_button_action_chooser_title),
                    action -> AppSettings.setWidgetPlayAction(requireContext(), action),
                    AppSettings.getWidgetPlayAction(requireContext()));
            dialog.show(requireFragmentManager(), AutoPlayActionChooser.TAG);
        });

        view.findViewById(R.id.widget_background_alpha_chooser).setOnClickListener(v -> {
            WidgetBackgroundAlphaChooser alphaChooser = WidgetBackgroundAlphaChooser.getInstance();
            alphaChooser.show(requireFragmentManager(), WidgetBackgroundAlphaChooser.TAG);
        });
    }
}
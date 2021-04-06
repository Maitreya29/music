package com.nezukoos.music.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nezukoos.music.Preferences;
import com.nezukoos.music.R;
import com.nezukoos.music.dialog.base.RoundedCustomBottomSheetFragment;
import com.nezukoos.music.utils.AppSettings;
import com.nezukoos.music.views.ValueSlider;

public class ConfigureSeekDuration extends RoundedCustomBottomSheetFragment {

    public static final String TAG = ConfigureSeekDuration.class.getSimpleName();

    @NonNull
    public static ConfigureSeekDuration getInstance() {
        return new ConfigureSeekDuration();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bsd_configure_seek_duration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ValueSlider forward = view.findViewById(R.id.forward_seek_duration);
        ValueSlider backward = view.findViewById(R.id.backwards_seek_duration);

        final int currentForwardDuration = AppSettings.getSeekButtonDuration(requireContext(), Preferences.KEY_NOW_PLAYING_SEEK_DURATION_FORWARD);
        final int currentBackwardDuration = AppSettings.getSeekButtonDuration(requireContext(), Preferences.KEY_NOW_PLAYING_SEEK_DURATION_BACKWARD);

        forward.setSliderValue(currentForwardDuration);
        backward.setSliderValue(currentBackwardDuration);

        view.findViewById(R.id.seek_duration_confirm_btn).setOnClickListener(v -> {
            int newForwardDuration = forward.getSliderValue();
            int newBackwardDuration = backward.getSliderValue();
            if (newForwardDuration != currentForwardDuration)
                AppSettings.setSeekButtonDuration(requireContext(), Preferences.KEY_NOW_PLAYING_SEEK_DURATION_FORWARD, newForwardDuration);
            if (newBackwardDuration != currentBackwardDuration)
                AppSettings.setSeekButtonDuration(requireContext(), Preferences.KEY_NOW_PLAYING_SEEK_DURATION_BACKWARD, newBackwardDuration);
            dismiss();
        });
    }
}
package com.nezukoos.music.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nezukoos.music.Preferences;
import com.nezukoos.music.R;
import com.nezukoos.music.dialog.base.RoundedCustomBottomSheetFragment;
import com.nezukoos.music.utils.AppSettings;

public class NowPlayingStyleChooser extends RoundedCustomBottomSheetFragment {

    public static final String TAG = NowPlayingStyleChooser.class.getSimpleName();
    private boolean mOptionChanged = false;

    @NonNull
    public static NowPlayingStyleChooser getInstance() {
        return new NowPlayingStyleChooser();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bsd_choose_now_playing_style, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RadioGroup radioGroup = view.findViewById(R.id.selector_radio_button_group);
        int currentStyle = AppSettings.getNowPlayingScreenStyle(requireContext());

        switch (currentStyle) {
            case Preferences.NOW_PLAYING_SCREEN_MODERN:
                ((RadioButton) radioGroup.findViewById(R.id.selector_now_playing_screen_modern)).setChecked(true);
                break;
            case Preferences.NOW_PLAYING_SCREEN_STYLISH:
                ((RadioButton) radioGroup.findViewById(R.id.selector_now_playing_screen_stylish)).setChecked(true);
                break;
            case Preferences.NOW_PLAYING_SCREEN_EDGE:
                ((RadioButton) radioGroup.findViewById(R.id.selector_now_playing_screen_edge)).setChecked(true);
                break;
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> mOptionChanged = true);

        view.findViewById(R.id.selector_set_btn).setOnClickListener(v1 -> {
            if (mOptionChanged) {
                final int id = radioGroup.getCheckedRadioButtonId();
                if (id == R.id.selector_now_playing_screen_modern)
                    AppSettings.setNowPlayingScreenStyle(requireContext(), Preferences.NOW_PLAYING_SCREEN_MODERN);
                else if (id == R.id.selector_now_playing_screen_stylish)
                    AppSettings.setNowPlayingScreenStyle(requireContext(), Preferences.NOW_PLAYING_SCREEN_STYLISH);
                else if (id == R.id.selector_now_playing_screen_edge)
                    AppSettings.setNowPlayingScreenStyle(requireContext(), Preferences.NOW_PLAYING_SCREEN_EDGE);
            }
            dismiss();
        });
        view.findViewById(R.id.selector_cancel_btn).setOnClickListener(v -> dismiss());
    }
}
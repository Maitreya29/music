package com.radiant.music.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.radiant.music.Preferences;
import com.radiant.music.R;
import com.radiant.music.dialog.base.RoundedCustomBottomSheetFragment;
import com.radiant.music.themes.ThemeManagerUtils;
import com.radiant.music.utils.AppSettings;

public class DarkThemeChooser extends RoundedCustomBottomSheetFragment {

    public static final String TAG = DarkThemeChooser.class.getSimpleName();
    private boolean mOptionChanged = false;

    @NonNull
    public static DarkThemeChooser getInstance() {
        return new DarkThemeChooser();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bsd_chose_dark_theme, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RadioGroup radioGroup = view.findViewById(R.id.radio_button_group);
        int currentTheme = AppSettings.getSelectedDarkTheme(requireContext());

        switch (currentTheme) {
            case Preferences.DARK_THEME_GRAY:
                ((RadioButton) radioGroup.findViewById(R.id.radio_btn_dark_theme_gray)).setChecked(true);
                break;
            case Preferences.DARK_THEME_KINDA:
                ((RadioButton) radioGroup.findViewById(R.id.radio_btn_dark_theme_kinda)).setChecked(true);
                break;
            case Preferences.DARK_THEME_PURE_BLACK:
                ((RadioButton) radioGroup.findViewById(R.id.radio_btn_dark_theme_pure_black)).setChecked(true);
                break;
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> mOptionChanged = true);

        view.findViewById(R.id.choose_theme_set_btn).setOnClickListener(v1 -> {
            if (mOptionChanged) {
                final int id = radioGroup.getCheckedRadioButtonId();
                if (id == R.id.radio_btn_dark_theme_gray)
                    ThemeManagerUtils.setSelectedDarkTheme(requireContext(), Preferences.DARK_THEME_GRAY);
                else if (id == R.id.radio_btn_dark_theme_kinda)
                    ThemeManagerUtils.setSelectedDarkTheme(requireContext(), Preferences.DARK_THEME_KINDA);
                else if (id == R.id.radio_btn_dark_theme_pure_black)
                    ThemeManagerUtils.setSelectedDarkTheme(requireContext(), Preferences.DARK_THEME_PURE_BLACK);

                if (ThemeManagerUtils.needToApplyNewDarkTheme()) {
                    //Theme need to be updated
                    if (null != getActivity()) {
                        ThemeManagerUtils.init(getActivity(), true);
                        getActivity().recreate();
                    }
                }
            }
            dismiss();
        });

        view.findViewById(R.id.choose_theme_cancel_btn).setOnClickListener(v -> dismiss());
    }
}
package com.hardcodecoder.pulsemusic.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.dialog.IgnoreFolderChooser;
import com.hardcodecoder.pulsemusic.fragments.settings.base.SettingsBaseFragment;

import java.util.Objects;

public class SettingsGeneralFragment extends SettingsBaseFragment {

    public static final String TAG = SettingsGeneralFragment.class.getSimpleName();

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
            IgnoreFolderChooser ignoreFolderChooser = IgnoreFolderChooser.getInstance();
            ignoreFolderChooser.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), IgnoreFolderChooser.TAG);
        });
    }
}
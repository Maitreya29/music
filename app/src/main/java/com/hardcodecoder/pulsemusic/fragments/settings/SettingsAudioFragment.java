package com.hardcodecoder.pulsemusic.fragments.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.AudioDeviceService;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.dialog.AutoPlayActionChooserDialogFragment;
import com.hardcodecoder.pulsemusic.fragments.settings.base.SettingsBaseFragment;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.views.SettingsToggleableItem;

public class SettingsAudioFragment extends SettingsBaseFragment {

    public static final String TAG = SettingsAudioFragment.class.getSimpleName();

    @NonNull
    public static SettingsAudioFragment getInstance() {
        return new SettingsAudioFragment();
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public int getToolbarTitleForFragment() {
        return R.string.audio;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_audio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SettingsToggleableItem bluetoothDeviceDetectionItem = view.findViewById(R.id.audio_device_bluetooth_toggle);
        boolean bluetoothDeviceDetection = AppSettings.isBluetoothDeviceDetectionEnabled(requireContext());

        bluetoothDeviceDetectionItem.setSwitchChecked(bluetoothDeviceDetection);
        bluetoothDeviceDetectionItem.setOnSwitchCheckedChangedListener((buttonView, isChecked) -> {
            AppSettings.saveBluetoothDeviceDetection(requireContext(), isChecked);
            Intent bluetoothServiceIntent = new Intent(requireContext().getApplicationContext(), AudioDeviceService.class);
            if (isChecked) requireContext().startService(bluetoothServiceIntent);
            else requireContext().stopService(bluetoothServiceIntent);
        });

        view.findViewById(R.id.audio_device_bluetooth_action_picker).setOnClickListener(v -> {
            AutoPlayActionChooserDialogFragment dialog = AutoPlayActionChooserDialogFragment.getInstance();
            dialog.show(getFragmentManager(), AutoPlayActionChooserDialogFragment.TAG);
        });
    }
}
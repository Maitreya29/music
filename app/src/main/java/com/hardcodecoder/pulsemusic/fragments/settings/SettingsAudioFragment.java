package com.hardcodecoder.pulsemusic.fragments.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.hardcodecoder.pulsemusic.BDS;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.dialog.BluetoothActionChooserBottomSheetDialogFragment;
import com.hardcodecoder.pulsemusic.fragments.settings.base.SettingsBaseFragment;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.views.SettingsToggleableItem;

import java.util.Objects;

public class SettingsAudioFragment extends SettingsBaseFragment {

    public static final String TAG = SettingsAudioFragment.class.getSimpleName();

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

        SettingsToggleableItem bluetoothDeviceDetectionLayout = view.findViewById(R.id.audio_enable_bluetooth_start);
        SwitchCompat bluetoothDeviceDetectionSwitch = bluetoothDeviceDetectionLayout.findViewById(R.id.setting_toggleable_item_switch);

        if (null != getContext()) {
            boolean bluetoothDeviceDetection = AppSettings.isBluetoothDeviceDetectionEnabled(getContext());

            bluetoothDeviceDetectionSwitch.setChecked(bluetoothDeviceDetection);
            bluetoothDeviceDetectionSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                AppSettings.saveBluetoothDeviceDetection(buttonView.getContext(), isChecked);
                Intent bluetoothServiceIntent = new Intent(getContext().getApplicationContext(), BDS.class);
                if (isChecked) getContext().startService(bluetoothServiceIntent);
                else getContext().stopService(bluetoothServiceIntent);
            });
            bluetoothDeviceDetectionLayout.setOnClickListener(v ->
                    bluetoothDeviceDetectionSwitch.setChecked(!bluetoothDeviceDetectionSwitch.isChecked()));

            view.findViewById(R.id.audio_select_bluetooth_action).setOnClickListener(v -> {
                BluetoothActionChooserBottomSheetDialogFragment dialog = BluetoothActionChooserBottomSheetDialogFragment.getInstance();
                dialog.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), BluetoothActionChooserBottomSheetDialogFragment.TAG);
            });
        }
    }
}
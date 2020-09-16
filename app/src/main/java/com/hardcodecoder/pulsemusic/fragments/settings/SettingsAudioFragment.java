package com.hardcodecoder.pulsemusic.fragments.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.hardcodecoder.pulsemusic.BDS;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.dialog.BluetoothActionChooserBottomSheetDialogFragment;
import com.hardcodecoder.pulsemusic.fragments.settings.base.SettingsBaseFragment;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.views.SettingsToggleableItem;

import java.util.Objects;

public class SettingsAudioFragment extends SettingsBaseFragment {

    public static final String TAG = SettingsAudioFragment.class.getSimpleName();
    private Context mContext;

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
        mContext = getActivity();

        SettingsToggleableItem bluetoothDeviceDetectionLayout = view.findViewById(R.id.audio_enable_bluetooth_start);
        SwitchMaterial bluetoothDeviceDetectionSwitch = bluetoothDeviceDetectionLayout.findViewById(R.id.setting_toggleable_item_switch);

        boolean bluetoothDeviceDetection = false;
        if (null != getContext()) {
            bluetoothDeviceDetection = AppSettings.isBluetoothDeviceDetection(getContext());
        }

        bluetoothDeviceDetectionSwitch.setChecked(bluetoothDeviceDetection);
        bluetoothDeviceDetectionSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppSettings.saveBluetoothDeviceDetection(buttonView.getContext(), isChecked);
            if (isChecked){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mContext.startForegroundService(new Intent(mContext, BDS.class));
                }
            }
            else{
                mContext.stopService(new Intent(mContext.getApplicationContext(), BDS.class));
            }

        });
        bluetoothDeviceDetectionLayout.setOnClickListener(v -> bluetoothDeviceDetectionSwitch.setChecked(!bluetoothDeviceDetectionSwitch.isChecked()));

        view.findViewById(R.id.audio_select_bluetooth_action).setOnClickListener(v -> {
            BluetoothActionChooserBottomSheetDialogFragment dialog = BluetoothActionChooserBottomSheetDialogFragment.getInstance();
            dialog.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), BluetoothActionChooserBottomSheetDialogFragment.TAG);
        });

    }

}

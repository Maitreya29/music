package com.hardcodecoder.pulsemusic.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.themes.ThemeManagerUtils;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

public class BluetoothActionChooserBottomSheetDialogFragment extends RoundedBottomSheetDialogFragment {

    public static final String TAG = "BluetoothActionChooserBottomSheetDialogFragment";
    private boolean mOptionChanged = false;
    private Context mContext;

    public static BluetoothActionChooserBottomSheetDialogFragment getInstance() {
        return new BluetoothActionChooserBottomSheetDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_choose_bluetooth_action, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        RadioGroup radioGroup = view.findViewById(R.id.radio_button_group);
        int currentAction = AppSettings.getBluetoothDeviceDetectionAction(mContext);

        switch (currentAction) {
            case Preferences.BLUETOOTH_ACTION_PLAY_SHUFFLE:
                ((RadioButton) radioGroup.findViewById(R.id.radio_btn_shuffle)).setChecked(true);
                break;
            case Preferences.BLUETOOTH_ACTION_PLAY_SUGGESTED:
                ((RadioButton) radioGroup.findViewById(R.id.radio_btn_suggested)).setChecked(true);
                break;
            case Preferences.BLUETOOTH_ACTION_PLAY_LATEST:
                ((RadioButton) radioGroup.findViewById(R.id.radio_btn_recent)).setChecked(true);
                break;
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> mOptionChanged = true);

        view.findViewById(R.id.choose_bluetooth_action_set_btn).setOnClickListener(v1 -> {
            if (mOptionChanged) {
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.radio_btn_shuffle:
                        AppSettings.saveBluetoothDeviceDetectionAction(mContext, Preferences.BLUETOOTH_ACTION_PLAY_SHUFFLE);
                        break;
                    case R.id.radio_btn_suggested:
                        AppSettings.saveBluetoothDeviceDetectionAction(mContext, Preferences.BLUETOOTH_ACTION_PLAY_SUGGESTED);
                        break;
                    case R.id.radio_btn_recent:
                        AppSettings.saveBluetoothDeviceDetectionAction(mContext, Preferences.BLUETOOTH_ACTION_PLAY_LATEST);
                        break;
                }

            }
            dismiss();
        });

        view.findViewById(R.id.choose_bluetooth_action_cancel_btn).setOnClickListener(v -> dismiss());
    }
}

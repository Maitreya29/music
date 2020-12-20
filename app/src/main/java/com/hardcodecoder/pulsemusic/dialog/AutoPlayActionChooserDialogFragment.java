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
import com.hardcodecoder.pulsemusic.dialog.base.RoundedBottomSheetFragment;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

public class AutoPlayActionChooserDialogFragment extends RoundedBottomSheetFragment {

    public static final String TAG = "AutoPlayActionChooserDialogFragment";
    private Context mContext;
    private boolean mOptionChanged = false;

    @NonNull
    public static AutoPlayActionChooserDialogFragment getInstance() {
        return new AutoPlayActionChooserDialogFragment();
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
            case Preferences.DEVICE_ACTION_PLAY_SHUFFLE:
                ((RadioButton) radioGroup.findViewById(R.id.radio_btn_shuffle)).setChecked(true);
                break;
            case Preferences.DEVICE_ACTION_PLAY_SUGGESTED:
                ((RadioButton) radioGroup.findViewById(R.id.radio_btn_suggested)).setChecked(true);
                break;
            case Preferences.DEVICE_ACTION_PLAY_LATEST:
                ((RadioButton) radioGroup.findViewById(R.id.radio_btn_latest)).setChecked(true);
                break;
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> mOptionChanged = true);

        view.findViewById(R.id.choose_bluetooth_action_set_btn).setOnClickListener(v1 -> {
            if (mOptionChanged) {
                final int id = radioGroup.getCheckedRadioButtonId();
                if (id == R.id.radio_btn_shuffle)
                    AppSettings.saveBluetoothDeviceDetectionAction(mContext, Preferences.DEVICE_ACTION_PLAY_SHUFFLE);
                else if (id == R.id.radio_btn_suggested)
                    AppSettings.saveBluetoothDeviceDetectionAction(mContext, Preferences.DEVICE_ACTION_PLAY_SUGGESTED);
                else if (id == R.id.radio_btn_latest)
                    AppSettings.saveBluetoothDeviceDetectionAction(mContext, Preferences.DEVICE_ACTION_PLAY_LATEST);
            }
            dismiss();
        });

        view.findViewById(R.id.choose_bluetooth_action_cancel_btn).setOnClickListener(v -> dismiss());
    }
}
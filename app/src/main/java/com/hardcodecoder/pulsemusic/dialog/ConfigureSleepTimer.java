package com.hardcodecoder.pulsemusic.dialog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.dialog.base.RoundedCustomBottomSheetFragment;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

public class ConfigureSleepTimer extends RoundedCustomBottomSheetFragment {

    public static final String TAG = ConfigureSleepTimer.class.getSimpleName();
    private boolean isValidDuration = true;

    @NonNull
    public static ConfigureSleepTimer getInstance() {
        return new ConfigureSleepTimer();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_configure_sleep_timer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextInputLayout editTextLayout = view.findViewById(R.id.edit_text_container);
        TextInputEditText editText = view.findViewById(R.id.text_input_field);
        int durationMinutes = AppSettings.getSleepTimerDurationMinutes(requireContext());

        editTextLayout.setSuffixText(getResources().getQuantityString(R.plurals.duration_minutes, durationMinutes));
        editText.setText(String.valueOf(durationMinutes));

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (null != editText.getText()) {
                    String duration = editText.getText().toString();
                    if (duration.length() > 0 && duration.matches("[0-9]+") && Integer.parseInt(duration) > 0) {
                        isValidDuration = true;
                        editTextLayout.setError(null);
                        editTextLayout.setSuffixText(getResources()
                                .getQuantityString(
                                        R.plurals.duration_minutes,
                                        Integer.parseInt(duration)));
                        return;
                    }
                }
                editTextLayout.setError(getString(R.string.error_invalid_duration));
                isValidDuration = false;
            }
        });

        view.findViewById(R.id.confirm_btn).setOnClickListener(v -> {
            if (!isValidDuration || null == editText.getText()) return;
            String duration = editText.getText().toString();
            int newDurationMinutes = Integer.parseInt(duration);
            if (durationMinutes == newDurationMinutes) {
                dismiss();
                return;
            }
            AppSettings.setSleepTimerDurationMinutes(requireContext(), newDurationMinutes);
            dismiss();
        });

        view.findViewById(R.id.cancel_btn).setOnClickListener(v -> dismiss());
    }
}
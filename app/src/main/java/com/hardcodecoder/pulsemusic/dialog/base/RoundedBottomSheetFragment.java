package com.hardcodecoder.pulsemusic.dialog.base;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.themes.ThemeManagerUtils;

public class RoundedBottomSheetFragment extends CustomBottomSheetFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new CustomBottomSheet(
                getContext(),
                ThemeManagerUtils.isDarkModeEnabled() ? R.style.RoundedBottomSheet : R.style.RoundedBottomSheetLight,
                this);
    }
}
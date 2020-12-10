package com.hardcodecoder.pulsemusic.dialog;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.dialog.base.CustomBottomSheet;
import com.hardcodecoder.pulsemusic.themes.ThemeManagerUtils;

public class RoundedBottomSheetDialog extends CustomBottomSheet {

    public RoundedBottomSheetDialog(@NonNull Context context) {
        super(context, ThemeManagerUtils.isDarkModeEnabled() ? R.style.RoundedBottomSheet : R.style.RoundedBottomSheetLight, behavior -> {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setSkipCollapsed(true);
        });
    }
}
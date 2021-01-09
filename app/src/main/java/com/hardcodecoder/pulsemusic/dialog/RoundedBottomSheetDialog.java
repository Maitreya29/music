package com.hardcodecoder.pulsemusic.dialog;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.dialog.base.CustomBottomSheet;
import com.hardcodecoder.pulsemusic.themes.ThemeManagerUtils;

public class RoundedBottomSheetDialog extends CustomBottomSheet {

    public RoundedBottomSheetDialog(@NonNull Context context) {
        this(context, null);
    }

    public RoundedBottomSheetDialog(@NonNull Context context, @Nullable BehaviourCallback callback) {
        super(context, ThemeManagerUtils.isDarkModeEnabled() ? R.style.RoundedBottomSheet : R.style.RoundedBottomSheetLight, callback);
    }
}
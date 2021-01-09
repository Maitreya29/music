package com.hardcodecoder.pulsemusic.dialog.base;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.dialog.RoundedBottomSheetDialog;

public class RoundedBottomSheetFragment extends CustomBottomSheetFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new RoundedBottomSheetDialog(requireContext(), this);
    }
}
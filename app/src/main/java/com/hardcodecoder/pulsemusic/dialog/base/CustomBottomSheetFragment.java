package com.hardcodecoder.pulsemusic.dialog.base;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CustomBottomSheetFragment extends BottomSheetDialogFragment implements CustomBottomSheet.BehaviourCallback {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new CustomBottomSheet(requireContext(), this);
    }

    @Override
    public void onBehaviourReady(@NonNull BottomSheetBehavior<FrameLayout> behavior) {
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setSkipCollapsed(true);
    }
}
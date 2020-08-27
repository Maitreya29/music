package com.hardcodecoder.pulsemusic.dialog;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hardcodecoder.pulsemusic.R;

public class RoundedBottomSheetDialog extends BottomSheetDialog {

    public RoundedBottomSheetDialog(@NonNull Context context) {
        super(context, R.style.BaseBottomSheetDialog);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            BottomSheetDialog dialog = RoundedBottomSheetDialog.this;
            FrameLayout bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
            if (null != bottomSheet) {
                BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setPeekHeight(0);
            }
        });
    }
}

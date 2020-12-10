package com.hardcodecoder.pulsemusic.dialog.base;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.themes.ThemeManagerUtils;

public class CustomBottomSheet extends BottomSheetDialog {

    private final BehaviourCallback mCallback;

    protected CustomBottomSheet(@NonNull Context context, @Nullable BehaviourCallback callback) {
        this(context, ThemeManagerUtils.isDarkModeEnabled() ? R.style.CustomBottomSheet : R.style.CustomBottomSheetLight, callback);
    }

    protected CustomBottomSheet(@NonNull Context context, int theme, @Nullable BehaviourCallback callback) {
        super(context, theme);
        mCallback = callback;
    }

    protected CustomBottomSheet(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener, @Nullable BehaviourCallback callback) {
        super(context, cancelable, cancelListener);
        mCallback = callback;
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        setDismissWithAnimation(true);
        BottomSheetBehavior<FrameLayout> behavior = getBehavior();
        if (null != mCallback) mCallback.onBehaviourReady(behavior);
    }

    public interface BehaviourCallback {
        void onBehaviourReady(@NonNull BottomSheetBehavior<FrameLayout> behavior);
    }
}
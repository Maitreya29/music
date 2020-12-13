package com.hardcodecoder.pulsemusic.activities.base;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.PulseController;

public class ControllerActivity extends ThemeActivity {

    protected PulseController mPulseController;
    protected PulseController.PulseRemote mRemote;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPulseController = PulseController.getInstance();
        mRemote = mPulseController.getRemote();
    }
}
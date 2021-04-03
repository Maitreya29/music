package com.hardcodecoder.pulsemusic.activities.base;

import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.playback.PulseController;

public class ControllerActivity extends ThemeActivity {

    protected PulseController mPulseController;
    protected PulseController.PulseRemote mRemote;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPulseController = PulseController.getInstance();
        mRemote = mPulseController.getRemote();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean mediaKey = null != mPulseController.getController() && mPulseController.getController().dispatchMediaButtonEvent(event);
        return mediaKey || super.onKeyDown(keyCode, event);
    }
}
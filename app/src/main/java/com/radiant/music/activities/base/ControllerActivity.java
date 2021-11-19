package com.radiant.music.activities.base;

import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.Nullable;

import com.radiant.music.playback.RadiantController;

public class ControllerActivity extends ThemeActivity {

    protected RadiantController mRadiantController;
    protected RadiantController.RadiantRemote mRemote;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRadiantController = RadiantController.getInstance();
        mRemote = mRadiantController.getRemote();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean mediaKey = null != mRadiantController.getController() && mRadiantController.getController().dispatchMediaButtonEvent(event);
        return mediaKey || super.onKeyDown(keyCode, event);
    }
}
package com.hardcodecoder.pulsemusic.widgets;

import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.widgets.base.PulseWidgetNormal;

public class PulseWidgetNormalLight extends PulseWidgetNormal {

    public static final String TAG = PulseWidgetNormalLight.class.getSimpleName();
    private static PulseWidgetNormalLight mInstance;

    public PulseWidgetNormalLight() {
        mInstance = this;
    }

    @NonNull
    public static PulseWidgetNormalLight getInstance() {
        if (null == mInstance) new PulseWidgetNormalLight();
        return mInstance;
    }

    @Override
    public int getLayoutId() {
        return R.layout.pulse_widget_normal_light;
    }
}
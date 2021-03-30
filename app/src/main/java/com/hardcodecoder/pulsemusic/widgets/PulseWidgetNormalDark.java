package com.hardcodecoder.pulsemusic.widgets;

import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.widgets.base.PulseWidgetNormal;

public class PulseWidgetNormalDark extends PulseWidgetNormal {

    public static final String TAG = PulseWidgetNormalDark.class.getSimpleName();
    private static PulseWidgetNormalDark mInstance;

    public PulseWidgetNormalDark() {
        mInstance = this;
    }

    @NonNull
    public static PulseWidgetNormalDark getInstance() {
        if (null == mInstance) new PulseWidgetNormalDark();
        return mInstance;
    }

    @Override
    public int getLayoutId() {
        return R.layout.pulse_widget_normal_dark;
    }
}
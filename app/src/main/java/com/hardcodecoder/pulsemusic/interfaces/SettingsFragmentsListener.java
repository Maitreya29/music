package com.hardcodecoder.pulsemusic.interfaces;

import androidx.annotation.StringRes;

import com.hardcodecoder.pulsemusic.fragments.settings.base.SettingsBaseFragment;

public interface SettingsFragmentsListener {

    void changeFragment(SettingsBaseFragment fragment);

    void setToolbarTitle(@StringRes int titleId);

    void requiresActivityRestart();

    void requiresApplicationRestart();
}
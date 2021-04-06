package com.nezukoos.music.interfaces;

import androidx.annotation.StringRes;

import com.nezukoos.music.fragments.settings.base.SettingsBaseFragment;

public interface SettingsFragmentsListener {

    void changeFragment(SettingsBaseFragment fragment);

    void setToolbarTitle(@StringRes int titleId);

    void requiresActivityRestart();

    void requiresApplicationRestart(boolean shouldStopPlayback);
}
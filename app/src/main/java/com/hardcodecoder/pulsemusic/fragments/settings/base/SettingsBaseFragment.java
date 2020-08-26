package com.hardcodecoder.pulsemusic.fragments.settings.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.hardcodecoder.pulsemusic.activities.SettingsActivity;
import com.hardcodecoder.pulsemusic.interfaces.SettingsFragmentsListener;

public abstract class SettingsBaseFragment extends Fragment {

    private SettingsFragmentsListener mListener;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mListener = (SettingsFragmentsListener) getActivity();
        if (mListener instanceof SettingsActivity)
            mListener.setToolbarTitle(getToolbarTitleForFragment());
    }

    protected void requestActivityRestart() {
        if (mListener instanceof SettingsActivity)
            mListener.onRequestRestart();
    }

    public abstract String getFragmentTag();

    @StringRes
    public abstract int getToolbarTitleForFragment();
}

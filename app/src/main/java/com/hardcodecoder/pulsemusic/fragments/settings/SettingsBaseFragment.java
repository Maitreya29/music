package com.hardcodecoder.pulsemusic.fragments.settings;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.hardcodecoder.pulsemusic.activities.SettingsActivity;
import com.hardcodecoder.pulsemusic.interfaces.SettingsFragmentsListener;

public abstract class SettingsBaseFragment extends Fragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SettingsFragmentsListener mListener = (SettingsFragmentsListener) getActivity();
        if (mListener instanceof SettingsActivity)
            mListener.setToolbarTitle(getToolbarTitleForFragment());
    }

    public abstract String getFragmentTag();

    @StringRes
    public abstract int getToolbarTitleForFragment();
}

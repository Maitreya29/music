package com.radiant.music.fragments.settings.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.radiant.music.activities.main.SettingsActivity;
import com.radiant.music.interfaces.SettingsFragmentsListener;

public abstract class SettingsBaseFragment extends Fragment {

    protected SettingsFragmentsListener mListener;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mListener = (SettingsFragmentsListener) getActivity();
        if (mListener instanceof SettingsActivity)
            mListener.setToolbarTitle(getToolbarTitleForFragment());
    }

    protected void requestActivityRestart() {
        if (mListener instanceof SettingsActivity)
            mListener.requiresActivityRestart();
    }

    protected void requiresApplicationRestart(boolean shouldStopPlayback) {
        if (mListener instanceof SettingsActivity)
            mListener.requiresApplicationRestart(shouldStopPlayback);
    }

    public abstract String getFragmentTag();

    @StringRes
    public abstract int getToolbarTitleForFragment();
}
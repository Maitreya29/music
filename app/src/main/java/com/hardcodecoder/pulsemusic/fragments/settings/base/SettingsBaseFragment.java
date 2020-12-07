package com.hardcodecoder.pulsemusic.fragments.settings.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.google.android.material.transition.MaterialSharedAxis;
import com.hardcodecoder.pulsemusic.activities.SettingsActivity;
import com.hardcodecoder.pulsemusic.interfaces.SettingsFragmentsListener;

public abstract class SettingsBaseFragment extends Fragment {

    private SettingsFragmentsListener mListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
        setExitTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));

        setReenterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
        setReturnTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
    }

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

    protected void requiresApplicationRestart() {
        if (mListener instanceof SettingsActivity)
            mListener.requiresApplicationRestart();
    }

    public abstract String getFragmentTag();

    @StringRes
    public abstract int getToolbarTitleForFragment();
}

package com.hardcodecoder.pulsemusic.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.SettingsActivity;
import com.hardcodecoder.pulsemusic.fragments.settings.base.SettingsBaseFragment;
import com.hardcodecoder.pulsemusic.interfaces.SettingsFragmentsListener;


public class SettingsMainFragment extends SettingsBaseFragment {

    public static final String TAG = SettingsMainFragment.class.getSimpleName();
    private SettingsFragmentsListener mListener;

    public static SettingsMainFragment getInstance() {
        return new SettingsMainFragment();
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public int getToolbarTitleForFragment() {
        return R.string.settings_title;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListener = (SettingsFragmentsListener) getActivity();
        view.findViewById(R.id.themeSettings).setOnClickListener(v -> openSettingsFragment(SettingsThemeFragment.getInstance()));
        view.findViewById(R.id.nowPlayingSettings).setOnClickListener(v -> openSettingsFragment(SettingsNowPlayingFragment.getInstance()));
        view.findViewById(R.id.contributorsSettings).setOnClickListener(v -> openSettingsFragment(SettingsContributorsFragment.getInstance()));
        view.findViewById(R.id.donationSettings).setOnClickListener(v -> openSettingsFragment(SettingsDonationFragment.getInstance()));
        view.findViewById(R.id.aboutSettings).setOnClickListener(v -> openSettingsFragment(SettingsAboutFragment.getInstance()));
    }

    private void openSettingsFragment(SettingsBaseFragment fragment) {
        if (mListener instanceof SettingsActivity) {
            mListener.changeFragment(fragment);
        }
    }
}

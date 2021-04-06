package com.nezukoos.music.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nezukoos.music.R;
import com.nezukoos.music.activities.main.SettingsActivity;
import com.nezukoos.music.fragments.settings.base.SettingsBaseFragment;

public class SettingsMainFragment extends SettingsBaseFragment {

    public static final String TAG = SettingsMainFragment.class.getSimpleName();

    @NonNull
    public static SettingsMainFragment getInstance() {
        return new SettingsMainFragment();
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public int getToolbarTitleForFragment() {
        return R.string.settings;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.generalSettings).setOnClickListener(v ->
                v.postOnAnimation(() -> openSettingsFragment(SettingsGeneralFragment.getInstance())));
        view.findViewById(R.id.themeSettings).setOnClickListener(v ->
                v.postOnAnimation(() -> openSettingsFragment(SettingsThemeFragment.getInstance())));
        view.findViewById(R.id.nowPlayingSettings).setOnClickListener(v ->
                v.postOnAnimation(() -> openSettingsFragment(SettingsNowPlayingFragment.getInstance())));
        view.findViewById(R.id.audioSettings).setOnClickListener(v ->
                v.postOnAnimation(() -> openSettingsFragment(SettingsAudioFragment.getInstance())));
        view.findViewById(R.id.widgetSettings).setOnClickListener(v ->
                v.postOnAnimation(() -> openSettingsFragment(SettingsWidgetsFragment.getInstance())));
        view.findViewById(R.id.contributorsSettings).setOnClickListener(v ->
                v.postOnAnimation(() -> openSettingsFragment(SettingsContributorsFragment.getInstance())));
        view.findViewById(R.id.donationSettings).setOnClickListener(v ->
                v.postOnAnimation(() -> openSettingsFragment(SettingsDonationFragment.getInstance())));
        view.findViewById(R.id.aboutSettings).setOnClickListener(v ->
                v.postOnAnimation(() -> openSettingsFragment(SettingsAboutFragment.getInstance())));
    }

    private void openSettingsFragment(SettingsBaseFragment fragment) {
        if (mListener instanceof SettingsActivity) {
            mListener.changeFragment(fragment);
        }
    }
}
package com.nezukoos.music.fragments.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;
import com.nezukoos.music.BuildConfig;
import com.nezukoos.music.R;
import com.nezukoos.music.fragments.settings.base.SettingsBaseFragment;

public class SettingsAboutFragment extends SettingsBaseFragment {

    public static final String TAG = SettingsAboutFragment.class.getSimpleName();

    @NonNull
    public static SettingsAboutFragment getInstance() {
        return new SettingsAboutFragment();
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public int getToolbarTitleForFragment() {
        return R.string.about;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialTextView tempTextView = view.findViewById(R.id.app_version);
        tempTextView.setText(BuildConfig.VERSION_NAME);

        tempTextView = view.findViewById(R.id.app_release_date);
        tempTextView.setText(BuildConfig.BUILD_DATE);

        tempTextView = view.findViewById(R.id.about_app_build_code);
        tempTextView.setText(String.valueOf(BuildConfig.VERSION_CODE));

        tempTextView = view.findViewById(R.id.about_app_build_type);
        tempTextView.setText(BuildConfig.BUILD_TYPE);

        view.findViewById(R.id.app_link_twitter).setOnClickListener(v -> openLink("https://www.twitter.com/hardcodecoder"));

        view.findViewById(R.id.app_link_telegram).setOnClickListener(v -> openLink("https://t.me/PulseMusicDiscussion"));

        view.findViewById(R.id.app_link_github).setOnClickListener(v -> openLink("https://github.com/HardcodeCoder/PulseMusic"));

        view.findViewById(R.id.app_link_crowdin).setOnClickListener(v -> openLink("https://crwd.in/pulse-music"));
    }

    private void openLink(@NonNull String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
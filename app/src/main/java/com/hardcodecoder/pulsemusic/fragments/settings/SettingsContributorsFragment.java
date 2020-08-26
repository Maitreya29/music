package com.hardcodecoder.pulsemusic.fragments.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.hardcodecoder.pulsemusic.GlideApp;
import com.hardcodecoder.pulsemusic.GlideConstantArtifacts;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.fragments.settings.base.SettingsBaseFragment;

public class SettingsContributorsFragment extends SettingsBaseFragment {


    public static final String TAG = SettingsContributorsFragment.class.getSimpleName();

    public static SettingsContributorsFragment getInstance() {
        return new SettingsContributorsFragment();
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public int getToolbarTitleForFragment() {
        return R.string.contributors;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_contributors, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.github_logo).setOnClickListener(v -> openLink(R.string.github_link));
        view.findViewById(R.id.facebook_logo).setOnClickListener(v -> openLink(R.string.facebook_link));
        view.findViewById(R.id.twitter_logo).setOnClickListener(v -> openLink(R.string.twitter_link));
        view.findViewById(R.id.telegram_logo).setOnClickListener(v -> openLink(R.string.telegram_link));

        GlideApp.with(view)
                .load(getString(R.string.profile_icon_link))
                .error(R.drawable.def_avatar)
                .transform(GlideConstantArtifacts.getCircleCrop())
                .into((ImageView) view.findViewById(R.id.lead_developer_profile_icon));
    }

    private void openLink(@StringRes int linkId) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(getString(linkId)));
        startActivity(i);
    }
}

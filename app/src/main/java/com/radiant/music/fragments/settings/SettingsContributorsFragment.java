package com.radiant.music.fragments.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.radiant.music.activities.main.SettingsActivity;

import com.radiant.music.R;
import com.radiant.music.fragments.settings.base.SettingsBaseFragment;
import com.radiant.music.glide.GlideApp;

public class SettingsContributorsFragment extends SettingsBaseFragment {

    public static final String TAG = SettingsContributorsFragment.class.getSimpleName();

    @NonNull
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

        view.findViewById(R.id.github_logo).setOnClickListener(v -> openLink("https://github.com/HardcodeCoder"));
        view.findViewById(R.id.gh_logo).setOnClickListener(v -> openLink("https://github.com/Maitreya29"));
        view.findViewById(R.id.tg_logo).setOnClickListener(v -> openLink("https://t.me/Maitreya29"));
        view.findViewById(R.id.tw_logo).setOnClickListener(v -> openLink("https://instagram.com/maitreyapatni"));
        view.findViewById(R.id.twitter_logo).setOnClickListener(v -> openLink("https://www.twitter.com/hardcodecoder"));
        view.findViewById(R.id.telegram_logo).setOnClickListener(v ->
                v.postOnAnimation(() -> openSettingsFragment(SettingsDonationFragment.getInstance())));
 
        GlideApp.with(view)
                .load(R.drawable.def_avatar)
                .circleCrop()
                .into((ImageView) view.findViewById(R.id.lead_developer_avatar));
        GlideApp.with(view)
                .load(R.drawable.maitreya)
                .circleCrop()
                .into((ImageView) view.findViewById(R.id.d_avatar));
    }

    private void openSettingsFragment(SettingsBaseFragment fragment) {
        if (mListener instanceof SettingsActivity) {
            mListener.changeFragment(fragment);
        }
    }

    private void openLink(@NonNull String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
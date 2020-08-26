package com.hardcodecoder.pulsemusic.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.fragments.settings.SettingsMainFragment;
import com.hardcodecoder.pulsemusic.fragments.settings.base.SettingsBaseFragment;
import com.hardcodecoder.pulsemusic.interfaces.SettingsFragmentsListener;

public class SettingsActivity extends PMBActivity implements SettingsFragmentsListener {

    private MaterialToolbar mToolbar;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mFragmentManager = getSupportFragmentManager();

        //Setting up toolbar
        mToolbar = findViewById(R.id.material_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());

        if (null == savedInstanceState) {
            //Set up the main fragment when activity is first created
            mFragmentManager.beginTransaction()
                    .replace(R.id.settings_content_container, SettingsMainFragment.getInstance())
                    .commit();
        }
    }

    @Override
    public void changeFragment(SettingsBaseFragment fragment) {
        mFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.settings_content_container, fragment, fragment.getFragmentTag())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void setToolbarTitle(@StringRes int titleId) {
        mToolbar.setTitle(titleId);
    }

    @Override
    public void onRequestRestart() {
        recreate();
    }

    @Override
    public void onBackPressed() {
        if (mFragmentManager.getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            mToolbar.setTitle(R.string.settings_title);
            mFragmentManager.popBackStack();
        }
    }
}

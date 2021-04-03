package com.hardcodecoder.pulsemusic.activities.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.base.ControllerActivity;
import com.hardcodecoder.pulsemusic.fragments.settings.SettingsMainFragment;
import com.hardcodecoder.pulsemusic.fragments.settings.base.SettingsBaseFragment;
import com.hardcodecoder.pulsemusic.interfaces.SettingsFragmentsListener;
import com.hardcodecoder.pulsemusic.shortcuts.AppShortcutsManager;
import com.hardcodecoder.pulsemusic.views.PulseToolbar;

public class SettingsActivity extends ControllerActivity implements SettingsFragmentsListener {

    private PulseToolbar mPulseToolbar;
    private FragmentManager mFragmentManager;
    private AlertDialog mRestartDialog;
    private SharedPreferences.OnSharedPreferenceChangeListener mAccentsChangedListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mFragmentManager = getSupportFragmentManager();

        mPulseToolbar = findViewById(R.id.settings_toolbar);
        setToolbarTitle(R.string.settings);
        mPulseToolbar.setNavigationIconOnClickListener(v -> onBackPressed());

        if (null == savedInstanceState) {
            //Set up the main fragment when activity is first created
            mFragmentManager.beginTransaction()
                    .replace(R.id.settings_content_container, SettingsMainFragment.getInstance(), SettingsMainFragment.TAG)
                    .commit();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            mAccentsChangedListener = (sharedPreferences, key) -> {
                if (key.equals(Preferences.KEY_ACCENTS_COLOR_PRESET) ||
                        key.equals(Preferences.KEY_ACCENTS_COLOR_CUSTOM) ||
                        key.equals(Preferences.KEY_ACCENTS_COLOR_DESATURATED) ||
                        key.equals(Preferences.KEY_ACCENTS_USING_PRESET)) {
                    AppShortcutsManager manager = new AppShortcutsManager(getApplicationContext());
                    manager.initDynamicShortcuts(true);
                }
            };
            getSharedPreferences(Preferences.PREFS_PULSE_THEMES, MODE_PRIVATE)
                    .registerOnSharedPreferenceChangeListener(mAccentsChangedListener);
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
        mPulseToolbar.setTitle(getString(titleId));
    }

    @Override
    public void requiresActivityRestart() {
        recreate();
    }

    @Override
    public void requiresApplicationRestart(boolean shouldStopPlayback) {
        View layout = View.inflate(this, R.layout.alert_dialog_view, null);
        MaterialTextView title = layout.findViewById(R.id.alert_dialog_title);
        MaterialTextView msg = layout.findViewById(R.id.alert_dialog_message);

        MaterialButton negativeBtn = layout.findViewById(R.id.alert_dialog_negative_btn);
        MaterialButton positiveBtn = layout.findViewById(R.id.alert_dialog_positive_btn);

        title.setText(R.string.restart_app);
        msg.setText(R.string.restart_app_dialog_desc);

        mRestartDialog = new MaterialAlertDialogBuilder(this)
                .setView(layout).create();

        positiveBtn.setText(R.string.restart);
        positiveBtn.setOnClickListener(positive -> {
            Intent restartIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
            if (null != restartIntent) {
                restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(restartIntent);
                if (shouldStopPlayback) {
                    mRemote.stop();
                    mPulseController.getQueueManager().resetPlaylist();
                    mRestartDialog.dismiss();
                }
                finish();
            }
        });

        negativeBtn.setText(R.string.do_it_later);
        negativeBtn.setOnClickListener(negative -> mRestartDialog.dismiss());
        mRestartDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (mFragmentManager.getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            mPulseToolbar.setTitle(getString(R.string.settings));
            mFragmentManager.popBackStack();
        }
    }

    @Override
    protected void onDestroy() {
        if (null != mAccentsChangedListener)
            getSharedPreferences(Preferences.PREFS_PULSE_THEMES, MODE_PRIVATE)
                    .unregisterOnSharedPreferenceChangeListener(mAccentsChangedListener);
        if (null != mRestartDialog) mRestartDialog.dismiss();
        super.onDestroy();
    }
}
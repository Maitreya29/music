package com.hardcodecoder.pulsemusic.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.session.MediaController;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.fragments.settings.SettingsMainFragment;
import com.hardcodecoder.pulsemusic.fragments.settings.base.SettingsBaseFragment;
import com.hardcodecoder.pulsemusic.interfaces.SettingsFragmentsListener;
import com.hardcodecoder.pulsemusic.shortcuts.AppShortcutsManager;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;

public class SettingsActivity extends MediaSessionActivity implements SettingsFragmentsListener {

    private Toolbar mToolbar;
    private FragmentManager mFragmentManager;
    private AlertDialog mRestartDialog;
    private SharedPreferences.OnSharedPreferenceChangeListener mAccentsChangedListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mFragmentManager = getSupportFragmentManager();

        //Setting up toolbar
        mToolbar = findViewById(R.id.material_toolbar);
        setToolbarTitle(R.string.settings_title);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());

        if (null == savedInstanceState) {
            //Set up the main fragment when activity is first created
            mFragmentManager.beginTransaction()
                    .replace(R.id.settings_content_container, SettingsMainFragment.getInstance())
                    .commit();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            mAccentsChangedListener = (sharedPreferences, key) -> {
                if (key.equals(Preferences.ACCENTS_COLOR_PRESET_KEY) ||
                        key.equals(Preferences.ACCENTS_COLOR_CUSTOM_KEY) ||
                        key.equals(Preferences.ACCENTS_COLOR_DESATURATED_KEY) ||
                        key.equals(Preferences.ACCENTS_MODE_USING_PRESET_KEY)) {
                    AppShortcutsManager manager = new AppShortcutsManager(getApplicationContext());
                    manager.initDynamicShortcuts(true);
                }
            };
            getSharedPreferences(Preferences.PULSE_THEMES_PREFS, MODE_PRIVATE)
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
        mToolbar.setTitle(titleId);
    }

    @Override
    public void requiresActivityRestart() {
        recreate();
    }

    @Override
    public void requiresApplicationRestart() {
        View layout = View.inflate(this, R.layout.alert_dialog_view, null);
        MaterialTextView title = layout.findViewById(R.id.alert_dialog_title);
        MaterialTextView msg = layout.findViewById(R.id.alert_dialog_message);

        MaterialButton negativeBtn = layout.findViewById(R.id.alert_dialog_negative_btn);
        MaterialButton positiveBtn = layout.findViewById(R.id.alert_dialog_positive_btn);

        title.setText(R.string.restart_dialog_title);
        msg.setText(R.string.restart_dialog_desc);

        mRestartDialog = new MaterialAlertDialogBuilder(this)
                .setView(layout).create();

        positiveBtn.setText(R.string.restart_dialog_positive_btn_title);
        positiveBtn.setOnClickListener(positive -> {
            Intent restartIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
            if (null != restartIntent) {
                restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(restartIntent);
                MediaController controller = getMediaController();
                if (controller != null) controller.getTransportControls().stop();
                TrackManager.getInstance().resetTrackManager();
                mRestartDialog.dismiss();
                finish();
            }
        });

        negativeBtn.setText(R.string.restart_dialog_negative_btn_title);
        negativeBtn.setOnClickListener(negative -> mRestartDialog.dismiss());
        mRestartDialog.show();
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

    @Override
    protected void onDestroy() {
        if (null != mAccentsChangedListener)
            getSharedPreferences(Preferences.PULSE_THEMES_PREFS, MODE_PRIVATE)
                    .unregisterOnSharedPreferenceChangeListener(mAccentsChangedListener);
        if (null != mRestartDialog) mRestartDialog.dismiss();
        super.onDestroy();
    }

    @Override
    public void onMediaServiceConnected(MediaController controller) {
    }
}
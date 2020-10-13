package com.hardcodecoder.pulsemusic.fragments.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.dialog.IgnoreFolderChooser;
import com.hardcodecoder.pulsemusic.fragments.settings.base.SettingsBaseFragment;

import java.util.Objects;

public class SettingsGeneralFragment extends SettingsBaseFragment {

    public static final String TAG = SettingsGeneralFragment.class.getSimpleName();

    public static SettingsGeneralFragment getInstance() {
        return new SettingsGeneralFragment();
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public int getToolbarTitleForFragment() {
        return R.string.general;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_general, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.ignore_folder_picker).setOnClickListener(v -> {
            if (null == getActivity()) return;

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            IgnoreFolderChooser ignoreFolderChooser = IgnoreFolderChooser.getInstance();
            ignoreFolderChooser.show(fragmentManager, IgnoreFolderChooser.TAG);
            fragmentManager.executePendingTransactions();

            if (null == ignoreFolderChooser.getDialog()) return;

            ignoreFolderChooser.getDialog().setOnDismissListener(dialog -> {
                AlertDialog dialog2 = new MaterialAlertDialogBuilder(Objects.requireNonNull(getContext()))
                        .setTitle(R.string.restart_dialog_title)
                        .setMessage(R.string.restart_dialog_desc)
                        .setPositiveButton(R.string.restart_dialog_positive_btn_title, (dialog1, which) -> {
                            Intent restartIntent = getActivity().getPackageManager()
                                    .getLaunchIntentForPackage(getActivity().getPackageName());
                            if (null != restartIntent) {
                                restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(restartIntent);
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton(R.string.restart_dialog_negative_btn_title, (dialog12, which) ->
                                dialog12.dismiss()).create();
                dialog2.show();
            });
        });
    }
}
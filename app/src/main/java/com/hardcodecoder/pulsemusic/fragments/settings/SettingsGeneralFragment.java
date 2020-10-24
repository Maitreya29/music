package com.hardcodecoder.pulsemusic.fragments.settings;

import android.content.Intent;
import android.media.session.MediaController;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
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

            IgnoreFolderChooser ignoreFolderChooser = IgnoreFolderChooser.getInstance(hasChanged -> {
                if (hasChanged) showRestartDialog(view);
            });
            ignoreFolderChooser.show(getFragmentManager(), IgnoreFolderChooser.TAG);
        });
    }

    private void showRestartDialog(View view) {
        View layout = View.inflate(view.getContext(), R.layout.alert_dialog_view, null);
        MaterialTextView title = layout.findViewById(R.id.alert_dialog_title);
        MaterialTextView msg = layout.findViewById(R.id.alert_dialog_message);

        MaterialButton negativeBtn = layout.findViewById(R.id.alert_dialog_negative_btn);
        MaterialButton positiveBtn = layout.findViewById(R.id.alert_dialog_positive_btn);

        title.setText(R.string.restart_dialog_title);
        msg.setText(R.string.restart_dialog_desc);

        AlertDialog restartDialog = new MaterialAlertDialogBuilder(Objects.requireNonNull(getContext()))
                .setView(layout).create();

        positiveBtn.setText(R.string.restart_dialog_positive_btn_title);
        positiveBtn.setOnClickListener(positive -> {
            Intent restartIntent = getActivity().getPackageManager()
                    .getLaunchIntentForPackage(getActivity().getPackageName());
            if (null != restartIntent) {
                restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(restartIntent);
                MediaController controller = getActivity().getMediaController();
                if (controller != null) controller.getTransportControls().stop();
                restartDialog.dismiss();
                getActivity().finish();
            }
        });

        negativeBtn.setText(R.string.restart_dialog_negative_btn_title);
        negativeBtn.setOnClickListener(negative -> restartDialog.dismiss());
        restartDialog.show();
    }
}
package com.radiant.music.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;
import com.radiant.music.Preferences;
import com.radiant.music.R;
import com.radiant.music.dialog.base.RoundedCustomBottomSheetFragment;
import com.radiant.music.utils.AppSettings;

public class AutoPlayActionChooser extends RoundedCustomBottomSheetFragment {

    public static final String TAG = AutoPlayActionChooser.class.getSimpleName();
    private final String mDialogTitle;
    private final DialogActionListener mListener;
    private final int mActiveOption;
    private boolean mOptionChanged = false;

    public AutoPlayActionChooser(@NonNull String dialogTitle, @NonNull DialogActionListener listener, int activeOption) {
        mDialogTitle = dialogTitle;
        mListener = listener;
        mActiveOption = activeOption;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bsd_choose_auto_play_action, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MaterialTextView textView = view.findViewById(R.id.auto_play_title);
        textView.setText(mDialogTitle);

        RadioGroup radioGroup = view.findViewById(R.id.radio_button_group);

        boolean continueWhereYouLeftEnabled = AppSettings.isRememberPlaylistEnabled(requireContext());
        view.findViewById(R.id.radio_btn_continue).setEnabled(continueWhereYouLeftEnabled);

        switch (mActiveOption) {
            case Preferences.ACTION_PLAY_SHUFFLE:
                ((RadioButton) radioGroup.findViewById(R.id.radio_btn_shuffle)).setChecked(true);
                break;
            case Preferences.ACTION_PLAY_SUGGESTED:
                ((RadioButton) radioGroup.findViewById(R.id.radio_btn_suggested)).setChecked(true);
                break;
            case Preferences.ACTION_PLAY_LATEST:
                ((RadioButton) radioGroup.findViewById(R.id.radio_btn_latest)).setChecked(true);
                break;
            case Preferences.ACTION_PLAY_CONTINUE:
                ((RadioButton) radioGroup.findViewById(R.id.radio_btn_continue)).setChecked(true);
                break;
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> mOptionChanged = true);

        view.findViewById(R.id.set_btn).setOnClickListener(v1 -> {
            if (mOptionChanged) {
                final int id = radioGroup.getCheckedRadioButtonId();
                int action;
                if (id == R.id.radio_btn_suggested)
                    action = Preferences.ACTION_PLAY_SUGGESTED;
                else if (id == R.id.radio_btn_latest)
                    action = Preferences.ACTION_PLAY_LATEST;
                else if (id == R.id.radio_btn_continue)
                    action = Preferences.ACTION_PLAY_CONTINUE;
                else
                    action = Preferences.ACTION_PLAY_SHUFFLE;

                mListener.onActionSelected(action);
            }
            dismiss();
        });

        view.findViewById(R.id.cancel_btn).setOnClickListener(v -> dismiss());
    }

    public interface DialogActionListener {
        void onActionSelected(int action);
    }
}
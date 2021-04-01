package com.hardcodecoder.pulsemusic.dialog;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.signature.ObjectKey;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.PulseController;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.main.SettingsActivity;
import com.hardcodecoder.pulsemusic.dialog.base.RoundedCustomBottomSheet;
import com.hardcodecoder.pulsemusic.dialog.base.RoundedCustomBottomSheetFragment;
import com.hardcodecoder.pulsemusic.glide.GlideApp;
import com.hardcodecoder.pulsemusic.utils.LogUtils;
import com.hardcodecoder.pulsemusic.utils.UserInfo;

import java.io.File;

public class MainActivityMenu extends RoundedCustomBottomSheetFragment {

    public static final String TAG = MainActivityMenu.class.getSimpleName();
    private static final int REQUEST_CODE_OPEN_EQUALIZER = 599;
    private static final int PICK_AVATAR = 1500;
    private ImageView mUserPic;
    private MaterialTextView mUserName;

    @NonNull
    public static MainActivityMenu getInstance() {
        return new MainActivityMenu();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bsd_main_activity_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUserPic = view.findViewById(R.id.drawer_user_logo);
        loadProfilePic(UserInfo.getUserProfilePic(requireContext()));
        mUserName = view.findViewById(R.id.drawer_user_name);
        updateUserName(UserInfo.getUserName(requireContext()));

        mUserPic.setOnClickListener(v -> pickPhoto());
        mUserName.setOnClickListener(v1 -> addUserName());
        view.findViewById(R.id.drawer_option_settings).setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), SettingsActivity.class));
            view.postOnAnimation(this::dismiss);
        });

        view.findViewById(R.id.drawer_option_equalizer).setOnClickListener(v -> {
            openStockEqualizer();
            view.postOnAnimation(this::dismiss);
        });
    }

    private void openStockEqualizer() {
        final int sessionId = PulseController.getInstance().getAudioSessionId();
        if (sessionId == AudioEffect.ERROR_BAD_VALUE) {
            Toast.makeText(requireContext(), getString(R.string.toast_equalizer_not_found), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            final Intent audioEffectIntent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
            audioEffectIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, sessionId);
            audioEffectIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
            requireActivity().startActivityForResult(audioEffectIntent, REQUEST_CODE_OPEN_EQUALIZER);
        } catch (ActivityNotFoundException notFoundException) {
            LogUtils.logException(LogUtils.Type.GENERAL, TAG, "Error opening Equalizer", notFoundException);
            Toast.makeText(requireContext(), getString(R.string.toast_equalizer_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProfilePic(@NonNull File profilePicFile) {
        GlideApp.with(this)
                .load(profilePicFile)
                .signature(new ObjectKey(profilePicFile.lastModified()))
                .error(R.drawable.def_avatar)
                .circleCrop()
                .into(mUserPic);
    }

    private void pickPhoto() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, getString(R.string.select_image));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_AVATAR);
    }

    private void updateUserName(String title) {
        mUserName.setText(title);
    }

    private void addUserName() {
        View layout = View.inflate(requireContext(), R.layout.bsd_edit_text, null);
        BottomSheetDialog sheetDialog = new RoundedCustomBottomSheet(layout.getContext(), RoundedCustomBottomSheet::setDefaultBehaviour);
        sheetDialog.setContentView(layout);
        sheetDialog.show();

        ((MaterialTextView) layout.findViewById(R.id.header)).setText(getResources().getString(R.string.enter_your_name));
        ((TextInputLayout) layout.findViewById(R.id.edit_text_container)).setHint(getResources().getString(R.string.enter_your_name));
        TextInputEditText et = layout.findViewById(R.id.text_input_field);

        layout.findViewById(R.id.confirm_btn).setOnClickListener(v -> {
            if (et.getText() != null && et.getText().toString().length() > 0) {
                String name = et.getText().toString();
                UserInfo.saveUserName(requireContext(), name);
                if (sheetDialog.isShowing()) sheetDialog.dismiss();
                updateUserName(name);
            } else
                Toast.makeText(requireContext(), getString(R.string.toast_enter_your_name), Toast.LENGTH_SHORT).show();
        });

        layout.findViewById(R.id.cancel_btn).setOnClickListener(v -> {
            if (sheetDialog.isShowing())
                sheetDialog.dismiss();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_AVATAR) {
            if (null == data || data.getData() == null) {
                Toast.makeText(requireContext(), getString(R.string.toast_select_image_error), Toast.LENGTH_SHORT).show();
                return;
            }
            UserInfo.saveUserProfilePic(requireContext(), data.getData(), this::loadProfilePic);
        }
    }
}
package com.radiant.music.fragments.main;

import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textview.MaterialTextView;
import com.radiant.music.R;
import com.radiant.music.playback.RadiantController;

public class ControlsFragment extends Fragment implements RadiantController.OnControllerReadyListener {

    public static final String TAG = ControlsFragment.class.getSimpleName();
    private MaterialTextView tv1;
    private ImageView playPause;
    private RadiantController mRadiantController;
    private final MediaController.Callback mCallback = new MediaController.Callback() {
        @Override
        public void onPlaybackStateChanged(@Nullable PlaybackState state) {
            if (null != state && state.getState() == PlaybackState.STATE_SKIPPING_TO_QUEUE_ITEM) {
                // Do not notify about skipping event
                // We use this state to fix seek bar issue in notification
                return;
            }
            updateControls();
        }

        @Override
        public void onMetadataChanged(@Nullable MediaMetadata metadata) {
            updateMetadata(metadata);
        }
    };

    @NonNull
    public static ControlsFragment getInstance() {
        return new ControlsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_controls, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        mRadiantController = RadiantController.getInstance();

        tv1 = v.findViewById(R.id.song_name);
        tv1.setSelected(true);

        playPause = v.findViewById(R.id.cf_play_pause_btn);

        RadiantController.RadiantRemote remote = mRadiantController.getRemote();

        ImageView skipNext = v.findViewById(R.id.cf_skip_next_btn);
        ImageView skipPrev = v.findViewById(R.id.cf_skip_prev_btn);

        playPause.setOnClickListener(v1 -> {
            if (mRadiantController.isPlaying()) remote.pause();
            else remote.play();
        });

        skipNext.setOnClickListener(v1 -> remote.skipToNextTrack());
        skipPrev.setOnClickListener(v1 -> remote.skipToPreviousTrack());

        mRadiantController.addConnectionCallback(this);
    }

    @Override
    public void onControllerReady(@NonNull MediaController controller) {
        controller.registerCallback(mCallback);
        updateControls();
        updateMetadata(controller.getMetadata());
    }

    private void updateControls() {
        if (mRadiantController.isPlaying()) playPause.setImageResource(R.drawable.ic_round_pause);
        else playPause.setImageResource(R.drawable.ic_round_play);
    }

    private void updateMetadata(@Nullable MediaMetadata metadata) {
        if (metadata == null) return;
        tv1.setText(metadata.getText(MediaMetadata.METADATA_KEY_TITLE));
    }

    @Override
    public void onDestroy() {
        mRadiantController.removeConnectionCallback(this);
        if (mRadiantController.getController() != null)
            mRadiantController.getController().unregisterCallback(mCallback);
        super.onDestroy();
    }
}
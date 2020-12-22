package com.hardcodecoder.pulsemusic.fragments.main;

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
import com.hardcodecoder.pulsemusic.PulseController;
import com.hardcodecoder.pulsemusic.R;

public class ControlsFragment extends Fragment {

    public static final String TAG = ControlsFragment.class.getSimpleName();
    private MaterialTextView tv1;
    private ImageView playPause;
    private PulseController mPulseController;
    private PulseController.PulseRemote mRemote;
    private PlaybackState mState;
    private final MediaController.Callback mCallback = new MediaController.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackState state) {
            mState = state;
            updateControls();
        }

        @Override
        public void onMetadataChanged(MediaMetadata metadata) {
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
    public void onStart() {
        updateController();
        super.onStart();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        mPulseController = PulseController.getInstance();
        mRemote = mPulseController.getRemote();

        tv1 = v.findViewById(R.id.song_name);
        tv1.setSelected(true);
        playPause = v.findViewById(R.id.cf_play_pause_btn);
        ImageView skipNext = v.findViewById(R.id.cf_skip_next_btn);
        ImageView skipPrev = v.findViewById(R.id.cf_skip_prev_btn);

        playPause.setOnClickListener(v1 -> {
            if (mState.getState() == PlaybackState.STATE_PLAYING)
                mRemote.pause();
            else
                mRemote.play();
        });

        skipNext.setOnClickListener(v1 -> mRemote.skipToNextTrack());
        skipPrev.setOnClickListener(v1 -> mRemote.skipToPreviousTrack());
    }

    private void updateControls() {
        if (null == mState)
            return;
        if (mState.getState() == PlaybackState.STATE_PLAYING)
            playPause.setImageResource(R.drawable.ic_round_pause);
        else
            playPause.setImageResource(R.drawable.ic_round_play);
    }

    private void updateMetadata(MediaMetadata metadata) {
        if (metadata != null)
            tv1.setText(metadata.getText(MediaMetadata.METADATA_KEY_TITLE));
    }

    private void updateController() {
        MediaController controller = mPulseController.getController();
        if (null == controller) return;
        controller.registerCallback(mCallback);
        mState = controller.getPlaybackState();
        updateMetadata(controller.getMetadata());
        updateControls();
    }

    @Override
    public void onStop() {
        if (mPulseController.getController() != null)
            mPulseController.getController().unregisterCallback(mCallback);
        super.onStop();
    }
}
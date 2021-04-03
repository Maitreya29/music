package com.hardcodecoder.pulsemusic.dialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.dialog.base.RoundedCustomBottomSheetFragment;
import com.hardcodecoder.pulsemusic.helper.UIHelper;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.playback.PulseController;
import com.hardcodecoder.pulsemusic.playback.QueueManager;
import com.hardcodecoder.pulsemusic.providers.ProviderManager;
import com.hardcodecoder.pulsemusic.utils.NavigationUtil;
import com.hardcodecoder.pulsemusic.views.MediaArtImageView;

public class TracksContentMenuDialog extends RoundedCustomBottomSheetFragment {

    public static final String TAG = TracksContentMenuDialog.class.getSimpleName();
    private final MusicModel mTrackModel;
    private boolean mShowGoToAlbums = false;
    private boolean mShowGoToArtists = false;
    private boolean mIsItemFavorite = false;

    public TracksContentMenuDialog() {
        mTrackModel = null;
    }

    public TracksContentMenuDialog(@NonNull MusicModel trackModel) {
        mTrackModel = trackModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.library_item_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (mTrackModel == null) {
            dismiss();
            return;
        }

        MediaArtImageView trackAlbumArt = view.findViewById(R.id.track_album_art);
        trackAlbumArt.setTransitionName("song_info_transition_" + mTrackModel.getId());
        MaterialTextView trackTitle = view.findViewById(R.id.track_title);
        MaterialTextView trackSubTitle = view.findViewById(R.id.track_sub_title);
        MaterialTextView updateFavorite = view.findViewById(R.id.update_favorite);

        trackAlbumArt.loadAlbumArt(mTrackModel.getAlbumArtUrl(), mTrackModel.getAlbumId());
        trackTitle.setText(mTrackModel.getTrackName());
        trackTitle.setSelected(true);
        trackSubTitle.setText(mTrackModel.getArtist());

        ProviderManager.getFavoritesProvider().isTemFavorite(mTrackModel, result -> {
            mIsItemFavorite = null != result && result;

            updateFavorite.setText(getString(mIsItemFavorite ? R.string.remove_from_favorite : R.string.add_to_favorite));

            updateFavorite.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    mIsItemFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border,
                    0, 0, 0);
        });

        updateFavorite.setOnClickListener(v -> {
            if (mIsItemFavorite) {
                ProviderManager.getFavoritesProvider().removeFromFavorite(mTrackModel);
                Toast.makeText(requireContext(), getString(R.string.toast_removed_from_favorites), Toast.LENGTH_SHORT).show();
            } else {
                if (mTrackModel.getId() < 0)
                    Toast.makeText(requireContext(), getString(R.string.toast_cannot_add_to_favorites), Toast.LENGTH_SHORT).show();
                else {
                    ProviderManager.getFavoritesProvider().addToFavorites(mTrackModel);
                    Toast.makeText(requireContext(), getString(R.string.toast_added_to_favorites), Toast.LENGTH_SHORT).show();
                }
            }
            dismiss();
        });

        QueueManager queueManager = PulseController.getInstance().getQueueManager();
        view.findViewById(R.id.track_play_next).setOnClickListener(v -> {
            queueManager.playNext(mTrackModel);
            Toast.makeText(v.getContext(), getString(R.string.toast_added_to_play_next), Toast.LENGTH_SHORT).show();
            dismiss();
        });

        view.findViewById(R.id.add_to_queue).setOnClickListener(v -> {
            queueManager.addToPlaylist(mTrackModel);
            Toast.makeText(requireContext(), getString(R.string.toast_added_to_queue), Toast.LENGTH_SHORT).show();
            dismiss();
        });

        view.findViewById(R.id.song_info).setOnClickListener(v -> {
            UIHelper.buildSongInfoDialog(requireActivity(), mTrackModel);
            dismiss();
        });

        view.findViewById(R.id.share).setOnClickListener(v -> {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            Uri trackPath = Uri.parse(mTrackModel.getTrackPath());
            sharingIntent.setType("audio/*");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, trackPath);
            startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_intent)));
        });

        if (mShowGoToAlbums) {
            MaterialTextView goToAlbums = view.findViewById(R.id.go_to_album);
            goToAlbums.setVisibility(View.VISIBLE);
            goToAlbums.setOnClickListener(v ->
                    NavigationUtil.goToAlbum(requireActivity(),
                            trackAlbumArt,
                            mTrackModel.getAlbum(),
                            mTrackModel.getAlbumId(),
                            mTrackModel.getAlbumArtUrl()));
        }

        if (mShowGoToArtists) {
            MaterialTextView goToArtists = view.findViewById(R.id.go_to_artist);
            goToArtists.setVisibility(View.VISIBLE);
            goToArtists.setOnClickListener(v ->
                    NavigationUtil.goToArtist(requireActivity(), mTrackModel.getArtist()));
        }

        view.findViewById(R.id.add_to_playlist).setOnClickListener(v -> {
            UIHelper.openAddToPlaylistDialog(requireFragmentManager(), mTrackModel);
            dismiss();
        });
    }

    public void setShowGoToAlbums(boolean show) {
        mShowGoToAlbums = show;
    }

    public void setShowGoToArtists(boolean show) {
        mShowGoToArtists = show;
    }
}
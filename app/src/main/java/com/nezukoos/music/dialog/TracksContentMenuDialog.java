package com.nezukoos.music.dialog;

import android.app.RecoverableSecurityException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.nezukoos.music.R;
import com.nezukoos.music.TaskRunner;
import com.nezukoos.music.dialog.base.RoundedCustomBottomSheetFragment;
import com.nezukoos.music.helper.DialogHelper;
import com.nezukoos.music.helper.MasterListUpdater;
import com.nezukoos.music.model.MusicModel;
import com.nezukoos.music.playback.PulseController;
import com.nezukoos.music.playback.QueueManager;
import com.nezukoos.music.providers.ProviderManager;
import com.nezukoos.music.utils.LogUtils;
import com.nezukoos.music.utils.NavigationUtil;
import com.nezukoos.music.views.MediaArtImageView;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class TracksContentMenuDialog extends RoundedCustomBottomSheetFragment {

    public static final String TAG = TracksContentMenuDialog.class.getSimpleName();
    private static final int DELETE_REQ_CODE = 45;
    private final MusicModel mTrackModel;
    private AlertDialog mDeleteConfirmDialog;
    private boolean mShowGoToAlbums = false;
    private boolean mShowGoToArtists = false;
    private boolean mIsItemFavorite = false;

    public TracksContentMenuDialog(@NonNull MusicModel trackModel) {
        mTrackModel = trackModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.library_item_menu, container, false);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Additional null check even though we will not accept null values for mTrackModel
        // Just in case at runtime mTrack happens to be null Don't show this dialog
        if (mTrackModel == null || mTrackModel.getId() < 0) {
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

        view.findViewById(R.id.delete).setOnClickListener(v -> showDeleteConfirmationDialog());

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
            DialogHelper.buildSongInfoDialog(requireActivity(), mTrackModel);
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
            DialogHelper.openAddToPlaylistDialog(requireFragmentManager(), mTrackModel);
            dismiss();
        });
    }

    public void setShowGoToAlbums(boolean show) {
        mShowGoToAlbums = show;
    }

    public void setShowGoToArtists(boolean show) {
        mShowGoToArtists = show;
    }

    private void showDeleteConfirmationDialog() {
        View layout = View.inflate(requireContext(), R.layout.alert_dialog_view, null);
        MaterialTextView title = layout.findViewById(R.id.alert_dialog_title);
        MaterialTextView msg = layout.findViewById(R.id.alert_dialog_message);

        MaterialButton deleteBtn = layout.findViewById(R.id.alert_dialog_positive_btn);
        MaterialButton cancelBtn = layout.findViewById(R.id.alert_dialog_negative_btn);

        title.setText(R.string.delete_confirmation_dialog_title);
        msg.setText(R.string.delete_confirmation_dialog_desc);

        mDeleteConfirmDialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(layout)
                .create();

        deleteBtn.setText(R.string.delete);
        deleteBtn.setOnClickListener(positive -> {
            deleteTrack();
            mDeleteConfirmDialog.dismiss();
        });

        cancelBtn.setText(R.string.cancel);
        cancelBtn.setOnClickListener(negative -> mDeleteConfirmDialog.dismiss());
        mDeleteConfirmDialog.show();
    }

    public void deleteTrack() {
        TaskRunner.executeAsync(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) performDeleteTrackQ();
            else performDeleteTrackPreQ();
        });
    }

    private void performDeleteTrackPreQ() {
        try {
            Cursor cursor = requireActivity().getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Audio.Media.DATA},
                    MediaStore.Audio.Media._ID + " = ?",
                    new String[]{String.valueOf(mTrackModel.getId())},
                    null);

            if (null == cursor || !cursor.moveToFirst()) return;

            String fullPath = cursor.getString(0);
            File file = new File(fullPath);
            if (file.delete()) {
                int deleted = deleteFromMediaStore();
                if (deleted < 1) {
                    // unable to delete from media store
                    // log info
                    LogUtils.logInfo(TAG, "Unable to delete track from MediaStore: " + mTrackModel.getTrackPath());
                }
                // Since the track has been deleted from the file system
                // We notify the ui even if deleting from media store fails
                onPostDeleteAction(true);
            } else LogUtils.logInfo(TAG, "Unable to delete track : " + mTrackModel.getTrackPath());
            cursor.close();
        } catch (Exception exception) {
            LogUtils.logException(LogUtils.Type.IO, TAG, "at: performDeleteTrackPreQ()", exception);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void performDeleteTrackQ() {
        try {
            int deleted = deleteFromMediaStore();
            onPostDeleteAction(deleted > 0);
        } catch (SecurityException securityException) {
            if (!(securityException instanceof RecoverableSecurityException)) return;
            RecoverableSecurityException rsException = (RecoverableSecurityException) securityException;
            try {
                IntentSender intentSender = rsException.getUserAction().getActionIntent().getIntentSender();
                startIntentSenderForResult(intentSender, DELETE_REQ_CODE,
                        null,
                        0,
                        0,
                        0,
                        null);
            } catch (IntentSender.SendIntentException intentException) {
                LogUtils.logException(LogUtils.Type.IO, TAG, "at: performDeleteTrackQ()", intentException);
            }
        }
    }

    private int deleteFromMediaStore() {
        return requireActivity()
                .getContentResolver()
                .delete(Uri.parse(mTrackModel.getTrackPath()),
                        MediaStore.Audio.Media._ID + " = ?",
                        new String[]{String.valueOf(mTrackModel.getId())});
    }

    private void onPostDeleteAction(boolean deleted) {
        if (deleted) {
            // Notify master list updater that this item has been deleted
            MasterListUpdater.getInstance().removeDeletedTrack(mTrackModel);
            // Remove this item from Favorite
            ProviderManager.getFavoritesProvider().removeFromFavorite(mTrackModel);
            // Notify user that track has been deleted
            Toast.makeText(requireContext(), getString(R.string.toast_delete_from_device_success), Toast.LENGTH_LONG).show();
            // Dismiss dialog as this item info is no longer valid
            dismiss();
        } else
            Toast.makeText(requireContext(), getString(R.string.toast_delete_from_device_failed), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DELETE_REQ_CODE && resultCode == RESULT_OK) {
            // We have been granted the permission to delete the item
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) performDeleteTrackQ();
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (null != mDeleteConfirmDialog) mDeleteConfirmDialog.dismiss();
        super.onDismiss(dialog);
    }
}
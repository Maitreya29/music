package com.nezukoos.music.helper;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.nezukoos.music.R;
import com.nezukoos.music.dialog.AddToPlaylistDialog;
import com.nezukoos.music.dialog.TracksContentMenuDialog;
import com.nezukoos.music.dialog.base.RoundedCustomBottomSheet;
import com.nezukoos.music.interfaces.CreatePlaylist;
import com.nezukoos.music.model.MusicModel;
import com.nezukoos.music.providers.ProviderManager;
import com.nezukoos.music.utils.DataUtils;

public class DialogHelper {

    public static void showMenuForLibraryTracks(@NonNull FragmentActivity activity, @NonNull MusicModel data) {
        TracksContentMenuDialog tracksContentDialog = new TracksContentMenuDialog(data);
        tracksContentDialog.setShowGoToAlbums(true);
        tracksContentDialog.setShowGoToArtists(true);
        tracksContentDialog.show(activity.getSupportFragmentManager(), TracksContentMenuDialog.TAG);
    }

    public static void showMenuForAlbumDetails(@NonNull FragmentActivity activity, @NonNull MusicModel data) {
        TracksContentMenuDialog tracksContentDialog = new TracksContentMenuDialog(data);
        tracksContentDialog.setShowGoToAlbums(false);
        tracksContentDialog.setShowGoToArtists(true);
        tracksContentDialog.show(activity.getSupportFragmentManager(), TracksContentMenuDialog.TAG);
    }

    public static void buildCreatePlaylistDialog(@NonNull Context context, @NonNull CreatePlaylist callback) {
        BottomSheetDialog sheetDialog = new RoundedCustomBottomSheet(context, RoundedCustomBottomSheet::setDefaultBehaviour);
        View layout = View.inflate(context, R.layout.bsd_edit_text, null);
        sheetDialog.setContentView(layout);

        TextView header = layout.findViewById(R.id.header);
        header.setText(context.getString(R.string.create_playlist));

        TextInputLayout til = layout.findViewById(R.id.edit_text_container);
        til.setHint(context.getString(R.string.hint_create_playlist));

        TextInputEditText et = layout.findViewById(R.id.text_input_field);

        layout.findViewById(R.id.confirm_btn).setOnClickListener(v -> {
            if (et.getText() != null) {
                String text = et.getText().toString().trim();
                if (text.length() == 0 || text.charAt(0) == ' ') {
                    Toast.makeText(context, context.getString(R.string.hint_create_playlist), Toast.LENGTH_SHORT).show();
                    return;
                }
                String playlistName = et.getText().toString();
                ProviderManager.getPlaylistProvider().addPlaylistItem(playlistName);
                callback.onPlaylistCreated(playlistName);
            } else {
                Toast.makeText(context, context.getString(R.string.hint_create_playlist), Toast.LENGTH_SHORT).show();
                return;
            }
            dismiss(sheetDialog);
        });

        layout.findViewById(R.id.cancel_btn).setOnClickListener(v -> dismiss(sheetDialog));
        sheetDialog.show();
    }

    public static void buildSongInfoDialog(@NonNull Context context, @NonNull final MusicModel musicModel) {
        BottomSheetDialog bottomSheetDialog = new RoundedCustomBottomSheet(context, RoundedCustomBottomSheet::setDefaultBehaviour);
        final View view = View.inflate(context, R.layout.bsd_track_info, null);
        bottomSheetDialog.setContentView(view);
        view.findViewById(R.id.dialog_ok).setOnClickListener(v -> dismiss(bottomSheetDialog));
        // Reference view fields which needs to be filled with data
        MaterialTextView displayTextView = view.findViewById(R.id.dialog_display_name);
        MaterialTextView trackTitle = view.findViewById(R.id.dialog_track_title);
        MaterialTextView trackAlbum = view.findViewById(R.id.dialog_track_album);
        MaterialTextView trackArtist = view.findViewById(R.id.dialog_track_artist);
        MaterialTextView trackFileSize = view.findViewById(R.id.dialog_file_size);
        MaterialTextView trackFileType = view.findViewById(R.id.dialog_file_type);
        MaterialTextView trackBitRate = view.findViewById(R.id.dialog_bitrate);
        MaterialTextView trackSampleRate = view.findViewById(R.id.dialog_sample_rate);
        MaterialTextView trackChannelCount = view.findViewById(R.id.dialog_channel_count);
        bottomSheetDialog.show();
        DataModelHelper.getTrackInfo(view.getContext(), musicModel, infoModel -> {
            if (null != infoModel) {
                view.postOnAnimation(() -> {
                    final StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
                    displayTextView.setText(infoModel.getDisplayName());
                    displayTextView.setSelected(true);
                    setInfo(trackTitle, context.getString(R.string.track), musicModel.getTrackName(), styleSpan);
                    setInfo(trackAlbum, context.getString(R.string.nav_home), musicModel.getAlbum(), styleSpan);
                    setInfo(trackArtist, context.getString(R.string.nav_artists), musicModel.getArtist(), styleSpan);
                    setInfo(trackFileSize, context.getString(R.string.file_size), DataUtils.getFormattedFileSize(infoModel.getFileSize()), styleSpan);
                    setInfo(trackFileType, context.getString(R.string.file_type), infoModel.getFileType(), styleSpan);
                    setInfo(trackBitRate, context.getString(R.string.bitrate), DataUtils.getFormattedBitRate(infoModel.getBitRate()), styleSpan);
                    setInfo(trackSampleRate, context.getString(R.string.sample_rate), DataUtils.getFormattedSampleRate(infoModel.getSampleRate()), styleSpan);
                    setInfo(trackChannelCount, context.getString(R.string.channel_count), String.valueOf(infoModel.getChannelCount()), styleSpan);
                    bottomSheetDialog.show();
                });
            }
        });
    }

    private static void setInfo(@NonNull MaterialTextView textView, @NonNull String head, @NonNull String info, @NonNull StyleSpan styleSpan) {
        String text = String.format("%s: %s", head, info);
        SpannableString sub = new SpannableString(text);
        sub.setSpan(styleSpan, 0, head.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        textView.setText(sub);
    }

    public static void openAddToPlaylistDialog(@NonNull FragmentManager fragmentManager, @NonNull final MusicModel itemToAdd) {
        AddToPlaylistDialog dialog = AddToPlaylistDialog.getInstance();
        Bundle b = new Bundle();
        b.putSerializable(AddToPlaylistDialog.MUSIC_MODEL_KEY, itemToAdd);
        dialog.setArguments(b);
        dialog.show(fragmentManager, AddToPlaylistDialog.TAG);
    }

    private static void dismiss(@NonNull BottomSheetDialog dialog) {
        if (dialog.isShowing())
            dialog.dismiss();
    }
}
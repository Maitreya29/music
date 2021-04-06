package com.nezukoos.music.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.nezukoos.music.R;
import com.nezukoos.music.adapters.playlist.PlaylistItemAdapter;
import com.nezukoos.music.dialog.base.RoundedCustomBottomSheetFragment;
import com.nezukoos.music.helper.DialogHelper;
import com.nezukoos.music.model.MusicModel;
import com.nezukoos.music.providers.ProviderManager;

import java.util.ArrayList;
import java.util.List;

public class AddToPlaylistDialog extends RoundedCustomBottomSheetFragment {

    public static final String TAG = AddToPlaylistDialog.class.getSimpleName();
    public static final String MUSIC_MODEL_KEY = "data";
    private PlaylistItemAdapter mAdapter;
    private MaterialTextView mEmptyListText;
    private List<String> mPlaylistNames;
    private MusicModel mItemToAdd = null;

    @NonNull
    public static AddToPlaylistDialog getInstance() {
        return new AddToPlaylistDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null)
            mItemToAdd = (MusicModel) getArguments().getSerializable(MUSIC_MODEL_KEY);
        return inflater.inflate(R.layout.bsd_item_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setUpRecyclerView(view);
        MaterialTextView title = view.findViewById(R.id.bottom_dialog_picker_title);
        title.setText(R.string.add_track_to);
        ImageView addBtn = view.findViewById(R.id.bottom_dialog_picker_add_btn);
        addBtn.setImageResource(R.drawable.ic_playlist_add);
        addBtn.setOnClickListener(v -> DialogHelper.buildCreatePlaylistDialog(requireActivity(), playlistName -> {
            if (null == mAdapter) setUpRecyclerView(view);
            else view.post(() -> mAdapter.addItem(playlistName));
        }));
    }

    private void setUpRecyclerView(View view) {
        ProviderManager.getPlaylistProvider().getAllPlaylistItem(result -> {
            if (null == result || result.size() <= 0) {
                mEmptyListText = (MaterialTextView) ((ViewStub) view.findViewById(R.id.stub_empty_list_text)).inflate();
                mEmptyListText.setText(getString(R.string.message_no_playlist_found));
                return;
            }
            mPlaylistNames = new ArrayList<>(result);
            view.post(() -> {
                if (null != mEmptyListText) mEmptyListText.setVisibility(View.GONE);
                RecyclerView recyclerView = (RecyclerView) ((ViewStub) view.findViewById(R.id.bottom_dialog_picker_stub_rv)).inflate();
                recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), RecyclerView.VERTICAL, false));
                mAdapter = new PlaylistItemAdapter(
                        getLayoutInflater(),
                        mPlaylistNames,
                        position -> {
                            if (null == mItemToAdd) {
                                Toast.makeText(requireContext(), getString(R.string.toast_add_to_playlist_failed), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            List<MusicModel> tracks = new ArrayList<>();
                            tracks.add(mItemToAdd);
                            ProviderManager.getPlaylistProvider().addTracksToPlaylist(tracks, mPlaylistNames.get(position), true);
                            Toast.makeText(requireContext(), getString(R.string.toast_added_to_playlist), Toast.LENGTH_SHORT).show();
                            if (isVisible())
                                dismiss();
                        });
                recyclerView.setAdapter(mAdapter);
            });
        });
    }
}
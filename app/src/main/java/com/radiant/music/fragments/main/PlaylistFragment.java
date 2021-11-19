package com.radiant.music.fragments.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.FileObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.radiant.music.Preferences;
import com.radiant.music.R;
import com.radiant.music.activities.playlist.CurrentQueuePlaylist;
import com.radiant.music.activities.playlist.CustomizablePlaylist;
import com.radiant.music.adapters.main.PlaylistsAdapter;
import com.radiant.music.dialog.ToolbarContextMenuDialog;
import com.radiant.music.dialog.base.RoundedCustomBottomSheet;
import com.radiant.music.fragments.main.base.RadiantFragment;
import com.radiant.music.helper.DialogHelper;
import com.radiant.music.helper.RecyclerViewGestureHelper;
import com.radiant.music.interfaces.ItemGestureCallback;
import com.radiant.music.interfaces.PlaylistCardListener;
import com.radiant.music.providers.ProviderManager;

import java.util.ArrayList;
import java.util.List;

public class PlaylistFragment extends RadiantFragment implements PlaylistCardListener, ItemGestureCallback<String> {

    public static final String TAG = "Playlist";
    private FileObserver mObserver;
    private PlaylistsAdapter mAdapter;
    private List<String> mPlaylistNames;
    private String mFragmentTitle = null;

    @NonNull
    public static PlaylistFragment getInstance() {
        return new PlaylistFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlist_cards, container, false);
    }

    @Override
    public void setUpContent(@NonNull View view) {
        mPlaylistNames = new ArrayList<>();
        mPlaylistNames.add(getString(R.string.playlist_play_queue));
        ProviderManager.getPlaylistProvider().getAllPlaylistItem(result -> {
            if (null != result) mPlaylistNames.addAll(result);
            loadPlaylistCards(view);
        });

        mObserver = new FileObserver(ProviderManager.getPlaylistProvider().getPlaylistParentFolder().getAbsolutePath(), FileObserver.CREATE) {
            @Override
            public void onEvent(int event, @Nullable String path) {
                if (null != path) view.post(() -> mAdapter.addPlaylist(path));
            }
        };
        mObserver.startWatching();
    }

    @Override
    public String getFragmentTitle(@NonNull Context context) {
        if (null == mFragmentTitle) mFragmentTitle = context.getString(R.string.nav_playlist);
        return mFragmentTitle;
    }

    @Override
    public void showOptionsMenu() {
        ToolbarContextMenuDialog.Builder builder = new ToolbarContextMenuDialog.Builder();
        builder.addGroup(Preferences.MENU_GROUP_TYPE_CREATE_PLAYLIST, getString(R.string.create_playlist), R.drawable.ic_playlist_add);
        builder.setMenuSelectedListener(groupItem ->
                DialogHelper.buildCreatePlaylistDialog(requireContext(), playlistName -> {
                    // File observer monitors creation of new playlists
                    // No need to call PlaylistAdapter#addPlaylist();
                })
        );
        ToolbarContextMenuDialog contextMenuDialog = builder.build();
        contextMenuDialog.show(requireFragmentManager(), ToolbarContextMenuDialog.TAG);
    }

    @Override
    public void onItemClick(int pos) {
        if (pos == 0)
            startActivity(new Intent(requireContext(), CurrentQueuePlaylist.class));
        else {
            Intent i = new Intent(requireContext(), CustomizablePlaylist.class);
            i.putExtra(CustomizablePlaylist.PLAYLIST_TITLE_KEY, mPlaylistNames.get(pos));
            requireActivity().startActivityFromFragment(this, i, 100);
        }
    }

    @Override
    public void onItemEdit(int pos) {
        showEditPlaylistDialog(pos);
    }

    @Override
    public void onItemDismissed(@NonNull String dismissedItem, int itemPosition) {
        if (itemPosition == 0) {
            Toast.makeText(requireContext(), getString(R.string.cannot_delete_default_playlist), Toast.LENGTH_SHORT).show();
            mAdapter.notifyItemChanged(itemPosition);
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext())
                    .setMessage(getString(R.string.playlist_delete_dialog_title))
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                        ProviderManager.getPlaylistProvider().deletePlaylistItem(mPlaylistNames.get(itemPosition));
                        Toast.makeText(requireContext(), getString(R.string.toast_playlist_deleted_success), Toast.LENGTH_SHORT).show();
                        mAdapter.removePlaylist(itemPosition);
                    }).setNegativeButton(getString(R.string.cancel), (dialog, which) -> mAdapter.notifyItemChanged(itemPosition));
            alertDialog.create().show();
        }
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
    }

    @Override
    public void onDestroyView() {
        mObserver.stopWatching();
        super.onDestroyView();
    }

    private void loadPlaylistCards(@NonNull View view) {
        view.postOnAnimation(() -> {
            RecyclerView recyclerView = (RecyclerView) ((ViewStub) view.findViewById(R.id.stub_playlist_cards_rv)).inflate();
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), RecyclerView.VERTICAL, false));
            mAdapter = new PlaylistsAdapter(getLayoutInflater(), mPlaylistNames, this, this);
            recyclerView.setAdapter(mAdapter);

            ItemTouchHelper.Callback itemTouchHelperCallback = new RecyclerViewGestureHelper(mAdapter);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        });
    }

    private void showEditPlaylistDialog(int pos) {
        View layout = View.inflate(requireContext(), R.layout.bsd_edit_text, null);
        BottomSheetDialog sheetDialog = new RoundedCustomBottomSheet(layout.getContext(), RoundedCustomBottomSheet::setDefaultBehaviour);
        sheetDialog.setContentView(layout);
        sheetDialog.show();

        TextView header = layout.findViewById(R.id.header);
        header.setText(getResources().getString(R.string.edit_user_playlist_name));

        TextInputLayout til = layout.findViewById(R.id.edit_text_container);
        til.setHint(getResources().getString(R.string.hint_create_playlist));

        TextInputEditText et = layout.findViewById(R.id.text_input_field);

        Button create = layout.findViewById(R.id.confirm_btn);
        create.setOnClickListener(v -> {
            if (et.getText() != null) {
                String text = et.getText().toString().trim();
                if (text.length() == 0 || text.charAt(0) == ' ') {
                    Toast.makeText(requireContext(), getString(R.string.hint_create_playlist), Toast.LENGTH_SHORT).show();
                    return;
                }
                String oldName = mPlaylistNames.remove(pos);
                String newName = et.getText().toString();
                if (ProviderManager.getPlaylistProvider().renamePlaylistItem(oldName, newName)) {
                    mPlaylistNames.add(pos, newName);
                    mAdapter.notifyItemChanged(pos);
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.hint_create_playlist), Toast.LENGTH_SHORT).show();
                return;
            }
            if (sheetDialog.isShowing())
                sheetDialog.dismiss();
        });

        Button cancel = layout.findViewById(R.id.cancel_btn);
        cancel.setOnClickListener(v -> {
            if (sheetDialog.isShowing())
                sheetDialog.dismiss();
        });
    }
}
package com.radiant.music.dialog;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.radiant.music.R;
import com.radiant.music.adapters.playlist.CustomizablePlaylistAdapter;
import com.radiant.music.dialog.base.RoundedCustomBottomSheetFragment;
import com.radiant.music.helper.RecyclerViewGestureHelper;
import com.radiant.music.interfaces.ItemGestureCallback;
import com.radiant.music.interfaces.PlaylistItemListener;
import com.radiant.music.model.MusicModel;
import com.radiant.music.playback.RadiantController;
import com.radiant.music.playback.QueueManager;
import com.radiant.music.themes.ThemeColors;
import com.radiant.music.utils.PlaylistUtil;
import com.radiant.music.views.MediaArtImageView;

import java.util.List;

public class CurrentQueueBottomSheet extends RoundedCustomBottomSheetFragment implements
        PlaylistItemListener, ItemGestureCallback<MusicModel>, QueueManager.OnQueueChangedListener {

    public static final String TAG = CurrentQueueBottomSheet.class.getSimpleName();
    private View rootView;
    private MediaArtImageView mUpNextTrackAlbumArt;
    private MaterialTextView mUpNextTrackTitle;
    private MaterialTextView mUpNextTrackArtist;
    private CustomizablePlaylistAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private RadiantController mRadiantController;
    private QueueManager mQueueManager;
    private final MediaController.Callback mCallback = new MediaController.Callback() {
        @Override
        public void onMetadataChanged(@Nullable MediaMetadata metadata) {
            updateUpNextCard();
        }
    };
    private RadiantController.RadiantRemote mRemote;

    @NonNull
    public static CurrentQueueBottomSheet getInstance() {
        return new CurrentQueueBottomSheet();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bsd_current_queue, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRadiantController = RadiantController.getInstance();
        mQueueManager = mRadiantController.getQueueManager();
        mRemote = mRadiantController.getRemote();
        if (null != mRadiantController.getController())
            mRadiantController.getController().registerCallback(mCallback);
        mQueueManager.addQueueChangedListener(this);

        rootView = view;

        mUpNextTrackAlbumArt = view.findViewById(R.id.up_next_track_album_art);
        mUpNextTrackTitle = view.findViewById(R.id.up_next_track_title);
        mUpNextTrackArtist = view.findViewById(R.id.up_next_track_sub_title);

        MaterialCardView upNextCard = view.findViewById(R.id.up_next_card);
        upNextCard.setStrokeColor(R.color.card);
        updateUpNextCard();

        List<MusicModel> list = mQueueManager.getPlaylist();

        PlaylistUtil.loadPlaylistArtInto(view.findViewById(R.id.current_queue_playlist_art), list);

        RecyclerView recyclerView = (RecyclerView) ((ViewStub) view.findViewById(R.id.current_queue_stub_queue_list)).inflate();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new CustomizablePlaylistAdapter(getLayoutInflater(), list, this, this);
        recyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback itemTouchHelperCallback = new RecyclerViewGestureHelper(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onBehaviourReady(@NonNull BottomSheetBehavior<FrameLayout> behavior) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setSkipCollapsed(true);
        }
    }

    @Override
    public void onItemDismissed(@NonNull MusicModel dismissedItem, int itemPosition) {
        mQueueManager.deletePlaylistItem(dismissedItem, itemPosition);
        Snackbar sb = Snackbar.make(rootView, R.string.track_removed_from_queue, Snackbar.LENGTH_SHORT);
        sb.setAction(getString(R.string.undo), v -> {
            mAdapter.restoreItem();
            mQueueManager.addToPlaylist(dismissedItem, itemPosition);
            if (mQueueManager.getActiveIndex() == itemPosition)
                mRemote.play();
        });
        sb.show();
        if (itemPosition == mQueueManager.getActiveIndex()) {
            if (mQueueManager.getPlaylist().size() > itemPosition) {
                if (mRadiantController.isPlaying()) mRemote.play();
            } else {
                // Active and last item in the playlist was removed
                // Stop playback immediately
                mRemote.stop();
                dismiss();
            }
        }
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        mQueueManager.swapPlaylistItem(fromPosition, toPosition);
    }

    @Override
    public void onItemClick(int position) {
        mQueueManager.setActiveIndex(position);
        mRemote.play();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder holder) {
        mItemTouchHelper.startDrag(holder);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (null != mRadiantController.getController())
            mRadiantController.getController().registerCallback(mCallback);
        mQueueManager.removeQueueChangedListener(this);
        super.onDismiss(dialog);
    }

    @Override
    public void onPlaylistChanged(@NonNull List<MusicModel> newPlaylist) {
    }

    @Override
    public void onPlaylistItemAdded(@NonNull MusicModel newItem, int index) {
        int upNextPosition = mQueueManager.getActiveIndex() + 1;
        if (index == upNextPosition)
            updateUpNextCard();
    }

    @Override
    public void onPlaylistItemDeleted(@NonNull MusicModel deletedItem, int index) {
        int upNextPosition = mQueueManager.getActiveIndex() + 1;
        if (index == upNextPosition)
            updateUpNextCard();
    }

    @Override
    public void onPlaylistItemSwapped(int from, int to) {
        int upNextPosition = mQueueManager.getActiveIndex() + 1;
        if (from <= upNextPosition || to <= upNextPosition)
            updateUpNextCard();
    }

    private void updateUpNextCard() {
        MusicModel data = mQueueManager.getNextQueueItem();
        if (null == data) {
            String completed = getString(R.string.playlist_completed);
            mUpNextTrackAlbumArt.loadAlbumArt(null, -1);
            mUpNextTrackTitle.setText(completed);
            mUpNextTrackArtist.setText(completed);
        } else {
            mUpNextTrackAlbumArt.loadAlbumArt(data.getAlbumArtUrl(), data.getAlbumId());
            mUpNextTrackTitle.setText(data.getTrackName());
            mUpNextTrackArtist.setText(data.getArtist());
        }
    }
}
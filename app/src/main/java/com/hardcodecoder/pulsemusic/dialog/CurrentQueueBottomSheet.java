package com.hardcodecoder.pulsemusic.dialog;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.PulseController;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.adapters.playlist.CustomizablePlaylistAdapter;
import com.hardcodecoder.pulsemusic.dialog.base.RoundedCustomBottomSheetFragment;
import com.hardcodecoder.pulsemusic.helper.RecyclerViewGestureHelper;
import com.hardcodecoder.pulsemusic.interfaces.ItemGestureCallback;
import com.hardcodecoder.pulsemusic.interfaces.PlaylistItemListener;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.views.MediaArtImageView;

import java.util.List;

public class CurrentQueueBottomSheet extends RoundedCustomBottomSheetFragment implements PlaylistItemListener, ItemGestureCallback<MusicModel> {

    public static final String TAG = CurrentQueueBottomSheet.class.getSimpleName();
    private View rootView;
    private MediaArtImageView mUpNextTrackAlbumArt;
    private MaterialTextView mUpNextTrackTitle;
    private MaterialTextView mUpNextTrackArtist;
    private CustomizablePlaylistAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private PulseController mPulseController;
    private PulseController.QueueManager mQueueManager;
    private final PulseController.Callback mCallback = new PulseController.Callback() {

        @Override
        public void onTrackItemAdded(@NonNull MusicModel trackItem, int position) {
            int upNextPosition = mQueueManager.getActiveIndex() + 1;
            if (position == upNextPosition)
                updateUpNextCard();
        }

        @Override
        public void onTrackItemRemoved(int position) {
            int upNextPosition = mQueueManager.getActiveIndex() + 1;
            if (position == upNextPosition)
                updateUpNextCard();
        }

        @Override
        public void onTrackItemMoved(int from, int to) {
            int upNextPosition = mQueueManager.getActiveIndex() + 1;
            if (from <= upNextPosition || to <= upNextPosition)
                updateUpNextCard();
        }

        @Override
        public void onMetadataChanged(@Nullable MediaMetadata metadata) {
            updateUpNextCard();
        }
    };
    private PulseController.PulseRemote mRemote;

    @NonNull
    public static CurrentQueueBottomSheet getInstance() {
        return new CurrentQueueBottomSheet();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_current_queue, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mPulseController = PulseController.getInstance();
        mQueueManager = mPulseController.getQueueManager();
        mRemote = mPulseController.getRemote();
        mPulseController.registerCallback(mCallback);

        rootView = view;

        mUpNextTrackAlbumArt = view.findViewById(R.id.up_next_track_album_art);
        mUpNextTrackTitle = view.findViewById(R.id.up_next_track_title);
        mUpNextTrackArtist = view.findViewById(R.id.up_next_track_sub_title);
        updateUpNextCard();

        RecyclerView recyclerView = (RecyclerView) ((ViewStub) view.findViewById(R.id.current_queue_stub_queue_list)).inflate();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        List<MusicModel> list = mQueueManager.getQueue();
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
        mPulseController.removeItemFromQueue(itemPosition);
        Snackbar sb = Snackbar.make(rootView, R.string.track_removed_from_queue, Snackbar.LENGTH_SHORT);
        sb.setAction(getString(R.string.undo), v -> {
            mAdapter.restoreItem();
            mPulseController.addToQueue(dismissedItem, itemPosition);
            if (mQueueManager.getActiveIndex() == itemPosition)
                mRemote.play();
        });
        sb.show();
        if (itemPosition == mQueueManager.getActiveIndex()) {
            if (mQueueManager.getQueue().size() > itemPosition) {
                MediaController controller = mPulseController.getController();
                if (controller != null && controller.getPlaybackState() != null &&
                        controller.getPlaybackState().getState() == PlaybackState.STATE_PLAYING)
                    mRemote.play();
            } else {
                // Active and last item in the playlist was removed
                // Stop playback immediately
                mRemote.stop();
            }
        }
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        mPulseController.moveQueueItem(fromPosition, toPosition);
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
        mPulseController.unregisterCallback(mCallback);
        super.onDismiss(dialog);
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
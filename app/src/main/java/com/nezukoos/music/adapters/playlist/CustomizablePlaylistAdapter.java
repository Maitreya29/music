package com.nezukoos.music.adapters.playlist;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;

import com.google.android.material.textview.MaterialTextView;
import com.nezukoos.music.R;
import com.nezukoos.music.TaskRunner;
import com.nezukoos.music.adapters.base.EfficientRecyclerViewAdapter;
import com.nezukoos.music.helper.DiffCb;
import com.nezukoos.music.interfaces.ItemGestureCallback;
import com.nezukoos.music.interfaces.ItemTouchHelperAdapter;
import com.nezukoos.music.interfaces.ItemTouchHelperViewHolder;
import com.nezukoos.music.interfaces.PlaylistItemListener;
import com.nezukoos.music.model.MusicModel;
import com.nezukoos.music.utils.ImageUtil;
import com.nezukoos.music.views.MediaArtImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomizablePlaylistAdapter extends EfficientRecyclerViewAdapter<MusicModel, CustomizablePlaylistAdapter.PlaylistTrackItemHolder>
        implements ItemTouchHelperAdapter {

    private final LayoutInflater mInflater;
    private final List<MusicModel> mPlaylistTracks;
    private final ItemGestureCallback<MusicModel> mItemGestureCallback;
    private final PlaylistItemListener mListener;
    private MusicModel deletedItem;
    private int deletedIndex;

    public CustomizablePlaylistAdapter(@NonNull LayoutInflater inflater,
                                       @NonNull List<MusicModel> playlistTracks,
                                       @NonNull PlaylistItemListener listener,
                                       @NonNull ItemGestureCallback<MusicModel> callback) {
        // This playlist can be modified
        // To prevent modification of original list, create a new list
        super(new ArrayList<>(playlistTracks));
        mInflater = inflater;
        mPlaylistTracks = getDataList();
        mListener = listener;
        mItemGestureCallback = callback;
    }

    @NonNull
    @Override
    public PlaylistTrackItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaylistTrackItemHolder(mInflater.inflate(R.layout.list_item_with_drag_handle, parent, false), mListener);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mPlaylistTracks, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        if (null != mItemGestureCallback) mItemGestureCallback.onItemMove(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        deletedItem = mPlaylistTracks.remove(position);
        deletedIndex = position;
        notifyItemRemoved(position);
        if (null != mItemGestureCallback)
            mItemGestureCallback.onItemDismissed(deletedItem, position);
    }

    public void addItems(final List<MusicModel> list) {
        int startIndex = mPlaylistTracks.size();
        mPlaylistTracks.addAll(list);
        notifyItemRangeInserted(startIndex, list.size());
    }

    public void updatePlaylist(@NonNull List<MusicModel> newList) {
        final Handler handler = TaskRunner.getMainHandler();
        TaskRunner.executeAsync(() -> {
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCb(mPlaylistTracks, newList));
            handler.post(() -> {
                diffResult.dispatchUpdatesTo(CustomizablePlaylistAdapter.this);
                mPlaylistTracks.clear();
                mPlaylistTracks.addAll(newList);
            });
        });
    }

    public void restoreItem() {
        mPlaylistTracks.add(deletedIndex, deletedItem);
        notifyItemInserted(deletedIndex);
    }

    public void clearAll() {
        int size = mPlaylistTracks.size();
        mPlaylistTracks.clear();
        notifyItemRangeRemoved(0, size);
    }

    static class PlaylistTrackItemHolder extends EfficientRecyclerViewAdapter.SmartViewHolder<MusicModel>
            implements ItemTouchHelperViewHolder {

        private final MaterialTextView mTrackTitle;
        private final MaterialTextView mSubTitle;
        private final MediaArtImageView mAlbumArt;

        public PlaylistTrackItemHolder(@NonNull View itemView, @NonNull PlaylistItemListener listener) {
            super(itemView);
            mTrackTitle = itemView.findViewById(R.id.list_item_title);
            mSubTitle = itemView.findViewById(R.id.list_item_sub_title);
            mAlbumArt = itemView.findViewById(R.id.list_item_drag_album_art);
            itemView.setOnClickListener(v ->
                    listener.onItemClick(getAdapterPosition()));
            //noinspection AndroidLintClickableViewAccessibility
            itemView.findViewById(R.id.list_item_drag_drag_handle)
                    .setOnTouchListener((v, event) -> {
                        if (event.getActionMasked() == MotionEvent.ACTION_DOWN || event.getActionMasked() == MotionEvent.ACTION_UP)
                            listener.onStartDrag(this);
                        return true;
                    });
        }

        @Override
        public void bindData(@NonNull MusicModel data) {
            mTrackTitle.setText(data.getTrackName());
            mSubTitle.setText(data.getArtist());
            mAlbumArt.loadAlbumArt(data.getAlbumArtUrl(), data.getAlbumId());
        }

        @Override
        public void unbindData() {
            mAlbumArt.clearLoadedArt();
            mAlbumArt.setImageDrawable(null);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackground(ImageUtil.getSelectedItemDrawable(itemView.getContext()));
        }

        @Override
        public void onItemClear() {
            itemView.setBackground(ContextCompat.getDrawable(itemView.getContext(), android.R.color.transparent));
        }
    }
}
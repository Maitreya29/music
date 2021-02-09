package com.hardcodecoder.pulsemusic.adapters.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.adapters.base.EfficientRecyclerViewAdapter;
import com.hardcodecoder.pulsemusic.interfaces.ItemGestureCallback;
import com.hardcodecoder.pulsemusic.interfaces.ItemTouchHelperAdapter;
import com.hardcodecoder.pulsemusic.interfaces.ItemTouchHelperViewHolder;
import com.hardcodecoder.pulsemusic.interfaces.PlaylistCardListener;
import com.hardcodecoder.pulsemusic.utils.ImageUtil;

import java.util.List;

public class PlaylistsAdapter extends EfficientRecyclerViewAdapter<String, PlaylistsAdapter.PlaylistItemHolder> implements ItemTouchHelperAdapter {

    private final LayoutInflater mInflater;
    private final PlaylistCardListener mListener;
    private final ItemGestureCallback<String> mCallback;

    public PlaylistsAdapter(@NonNull LayoutInflater inflater,
                            @NonNull List<String> playlistNames,
                            @NonNull PlaylistCardListener listener,
                            @Nullable ItemGestureCallback<String> callback) {
        super(playlistNames);
        mInflater = inflater;
        mListener = listener;
        mCallback = callback;
    }

    @NonNull
    @Override
    public PlaylistItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaylistItemHolder(mInflater.inflate(R.layout.rv_playlist_card_item, parent, false), mListener);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        if (null != mCallback)
            mCallback.onItemDismissed(getDataList().get(position), position);
    }

    public void addPlaylist(String playlistName) {
        getDataList().add(1, playlistName);
        notifyItemInserted(1);
    }

    public void removePlaylist(int position) {
        getDataList().remove(position);
        notifyItemRemoved(position);
    }

    static class PlaylistItemHolder extends EfficientRecyclerViewAdapter.SmartViewHolder<String>
            implements ItemTouchHelperViewHolder {

        private final TextView mPlaylistTitle;
        private final ImageView mEditButton;

        public PlaylistItemHolder(@NonNull View itemView, @NonNull PlaylistCardListener listener) {
            super(itemView);
            itemView.setBackground(ImageUtil.getHighlightedItemBackground(itemView.getContext()));
            itemView.setOnClickListener(v ->
                    listener.onItemClick(getAdapterPosition()));

            mPlaylistTitle = itemView.findViewById(R.id.playlist_title);
            mEditButton = itemView.findViewById(R.id.edit_btn);
            mEditButton.setOnClickListener(v ->
                    listener.onItemEdit(getAdapterPosition()));
        }

        @Override
        public void bindData(@NonNull String data) {
            mPlaylistTitle.setText(data);
            if (getAdapterPosition() == 0)
                mEditButton.setVisibility(View.GONE);
        }

        @Override
        public void unbindData() {
        }

        @Override
        public void onItemSelected() {
            itemView.setBackground(ImageUtil.getSelectedItemDrawable(itemView.getContext()));
        }

        @Override
        public void onItemClear() {
            itemView.setBackground(ImageUtil.getHighlightedItemBackground(itemView.getContext()));
        }
    }
}
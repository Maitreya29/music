package com.hardcodecoder.pulsemusic.adapters;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.helper.DiffCb;
import com.hardcodecoder.pulsemusic.interfaces.ItemTouchHelperAdapter;
import com.hardcodecoder.pulsemusic.interfaces.ItemTouchHelperViewHolder;
import com.hardcodecoder.pulsemusic.interfaces.PlaylistItemListener;
import com.hardcodecoder.pulsemusic.interfaces.SimpleGestureCallback;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.utils.ImageUtil;
import com.hardcodecoder.pulsemusic.views.MediaArtImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlaylistDataAdapter extends RecyclerView.Adapter<PlaylistDataAdapter.PlaylistDataSVH>
        implements ItemTouchHelperAdapter {

    private final LayoutInflater mInflater;
    private final List<MusicModel> mPlaylistTracks = new ArrayList<>();
    private final SimpleGestureCallback mCallback;
    private final PlaylistItemListener mListener;
    private int lastPosition = -1;
    private MusicModel deletedItem;
    private int deletedIndex;

    public PlaylistDataAdapter(@NonNull List<MusicModel> playlistTracks,
                               @NonNull LayoutInflater inflater,
                               @NonNull PlaylistItemListener listener,
                               @Nullable SimpleGestureCallback callback) {
        mPlaylistTracks.addAll(playlistTracks);
        mInflater = inflater;
        mListener = listener;
        mCallback = callback;
    }

    @NonNull
    public List<MusicModel> getPlaylistTracks() {
        return mPlaylistTracks;
    }

    public void addItems(final List<MusicModel> list) {
        int startIndex = mPlaylistTracks.size();
        mPlaylistTracks.addAll(list);
        notifyItemRangeInserted(startIndex, list.size());
    }

    public void updatePlaylist(@NonNull List<MusicModel> newList) {
        final Handler handler = new Handler();
        TaskRunner.executeAsync(() -> {
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCb(mPlaylistTracks, newList) {
                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return mPlaylistTracks.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
                }
            });
            handler.post(() -> {
                diffResult.dispatchUpdatesTo(PlaylistDataAdapter.this);
                mPlaylistTracks.clear();
                mPlaylistTracks.addAll(newList);
            });
        });
    }

    public void restoreItem() {
        mPlaylistTracks.add(deletedIndex, deletedItem);
        notifyItemInserted(deletedIndex);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mPlaylistTracks, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        if (null != mCallback) mCallback.onItemMove(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        deletedItem = mPlaylistTracks.remove(position);
        deletedIndex = position;
        notifyItemRemoved(position);
        if (null != mCallback)
            mCallback.onItemDismissed(position);
    }

    @NonNull
    @Override
    public PlaylistDataSVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaylistDataSVH(mInflater.inflate(R.layout.list_item_with_drag_handle, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistDataSVH holder, int position) {
        holder.updateView(mPlaylistTracks.get(position));
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(),
                (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top));
        lastPosition = position;
    }

    @Override
    public int getItemCount() {
        return mPlaylistTracks.size();
    }

    static class PlaylistDataSVH extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        private final MaterialTextView title;
        private final MaterialTextView subTitle;
        private final MediaArtImageView albumArt;

        PlaylistDataSVH(@NonNull View itemView, PlaylistItemListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.list_item_title);
            subTitle = itemView.findViewById(R.id.list_item_sub_title);
            albumArt = itemView.findViewById(R.id.list_item_drag_album_art);
            itemView.setOnClickListener(v -> listener.onItemClick(getAdapterPosition()));
            //noinspection AndroidLintClickableViewAccessibility
            itemView.findViewById(R.id.list_item_drag_drag_handle)
                    .setOnTouchListener((v, event) -> {
                        if (event.getActionMasked() == MotionEvent.ACTION_DOWN || event.getActionMasked() == MotionEvent.ACTION_UP)
                            listener.onStartDrag(this);
                        return true;
                    });
        }

        void updateView(MusicModel md) {
            title.setText(md.getTrackName());
            subTitle.setText(md.getArtist());
            albumArt.loadAlbumArt(md.getAlbumArtUrl(), md.getAlbumId());
        }

        @Override
        public void onItemSelected() {
            itemView.setBackground(ImageUtil.getAccentTintedSelectedItemBackground(itemView.getContext()));
        }

        @Override
        public void onItemClear() {
            itemView.setBackground(ContextCompat.getDrawable(itemView.getContext(), android.R.color.transparent));
        }
    }
}
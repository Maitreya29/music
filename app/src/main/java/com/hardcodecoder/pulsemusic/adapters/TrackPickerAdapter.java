package com.hardcodecoder.pulsemusic.adapters;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.helper.DiffCb;
import com.hardcodecoder.pulsemusic.interfaces.ItemSelectorAdapterCallback;
import com.hardcodecoder.pulsemusic.interfaces.ItemSelectorListener;
import com.hardcodecoder.pulsemusic.interfaces.ItemTouchHelperViewHolder;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.utils.ImageUtil;
import com.hardcodecoder.pulsemusic.views.MediaArtImageView;
import com.l4digital.fastscroll.FastScroller;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class TrackPickerAdapter extends RecyclerView.Adapter<TrackPickerAdapter.TrackPickerSVH>
        implements ItemSelectorAdapterCallback, FastScroller.SectionIndexer {

    private final Handler mMainHandler = new Handler();
    private final Set<MusicModel> mSelectedTracks = new LinkedHashSet<>();
    private final Deque<List<MusicModel>> pendingUpdates = new ArrayDeque<>();
    private final List<MusicModel> mList;
    private final LayoutInflater mInflater;
    private final ItemSelectorListener mListener;

    public TrackPickerAdapter(List<MusicModel> list, LayoutInflater inflater, ItemSelectorListener listener) {
        mList = new ArrayList<>(list);
        mInflater = inflater;
        mListener = listener;
    }

    @NonNull
    @Override
    public TrackPickerSVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TrackPickerSVH(mInflater.inflate(R.layout.list_item, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackPickerSVH holder, int position) {
        boolean isSelected = mSelectedTracks.contains(mList.get(position));
        holder.updateViewData(mList.get(position), isSelected);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public CharSequence getSectionText(int position) {
        return mList.get(position).getTrackName().substring(0, 1);
    }

    @Override
    public void onItemSelected(int position) {
        mSelectedTracks.add(mList.get(position));
    }

    @Override
    public void onItemUnselected(int position) {
        mSelectedTracks.remove(mList.get(position));
    }

    public void updateItems(final List<MusicModel> newItems) {
        pendingUpdates.push(newItems);
        if (pendingUpdates.size() > 1) {
            return;
        }
        updateItemsInternal(newItems);
    }

    private void updateItemsInternal(final List<MusicModel> newItems) {
        TaskRunner.executeAsync(() -> {
            final List<MusicModel> oldItems = new ArrayList<>(this.mList);
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCb(oldItems, newItems));
            mMainHandler.post(() -> applyDiffResult(newItems, diffResult));
        });
    }

    private void applyDiffResult(List<MusicModel> newItems, DiffUtil.DiffResult diffResult) {
        pendingUpdates.remove(newItems);
        dispatchUpdates(newItems, diffResult);
        if (pendingUpdates.size() > 0) {
            List<MusicModel> latest = pendingUpdates.pop();
            pendingUpdates.clear();
            updateItemsInternal(latest);
        }
    }

    private void dispatchUpdates(List<MusicModel> newItems, @NonNull DiffUtil.DiffResult diffResult) {
        diffResult.dispatchUpdatesTo(this);
        mList.clear();
        mList.addAll(newItems);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        mSelectedTracks.clear();
        pendingUpdates.clear();
        mList.clear();
        super.onDetachedFromRecyclerView(recyclerView);
    }

    public Set<MusicModel> getSelectedTracks() {
        return mSelectedTracks;
    }

    static class TrackPickerSVH extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        private final MediaArtImageView albumArt;
        private final MaterialTextView title;
        private final MaterialTextView artist;
        private boolean mItemSelected = false;

        TrackPickerSVH(@NonNull View itemView, ItemSelectorListener listener) {
            super(itemView);
            albumArt = itemView.findViewById(R.id.list_item_album_art);
            title = itemView.findViewById(R.id.list_item_title);
            artist = itemView.findViewById(R.id.list_item_sub_title);
            itemView.setOnClickListener(v ->
                    listener.onItemClick(this, getAdapterPosition(), !mItemSelected));
        }

        void updateViewData(@NonNull MusicModel md, boolean selected) {
            title.setText(md.getTrackName());
            artist.setText(md.getArtist());
            albumArt.loadAlbumArt(md.getAlbumArtUrl(), md.getAlbumId());
            if (mItemSelected == selected) return;
            if (selected) onItemSelected();
            else onItemClear();
        }

        @Override
        public void onItemSelected() {
            itemView.setBackground(ImageUtil.getAccentTintedSelectedItemBackground(itemView.getContext()));
            mItemSelected = true;
        }

        @Override
        public void onItemClear() {
            itemView.setBackground(ContextCompat.getDrawable(itemView.getContext(), android.R.color.transparent));
            mItemSelected = false;
        }
    }
}
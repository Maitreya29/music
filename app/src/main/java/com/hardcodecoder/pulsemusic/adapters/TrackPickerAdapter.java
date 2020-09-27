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
import com.hardcodecoder.pulsemusic.interfaces.ItemTouchHelperViewHolder;
import com.hardcodecoder.pulsemusic.interfaces.TrackPickerCallbackAdapter;
import com.hardcodecoder.pulsemusic.interfaces.TrackPickerListener;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.utils.ImageUtil;
import com.hardcodecoder.pulsemusic.views.MediaArtImageView;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class TrackPickerAdapter extends RecyclerView.Adapter<TrackPickerAdapter.TrackPickerSVH> implements TrackPickerCallbackAdapter {

    private final Handler mMainHandler = new Handler();
    private final Set<MusicModel> mSelectedTracks = new LinkedHashSet<>();
    private Deque<List<MusicModel>> pendingUpdates = new ArrayDeque<>();
    private List<MusicModel> mList;
    private LayoutInflater mInflater;
    private TrackPickerListener mListener;

    public TrackPickerAdapter(List<MusicModel> list, LayoutInflater mInflater, TrackPickerListener listener) {
        this.mList = new ArrayList<>(list);
        this.mInflater = mInflater;
        this.mListener = listener;
    }

    @Override
    public void onItemSelected(int position) {
        mSelectedTracks.add(mList.get(position));
    }

    @Override
    public void onItemUnselected(int position) {
        mSelectedTracks.remove(mList.get(position));
    }

    @NonNull
    @Override
    public TrackPickerSVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TrackPickerSVH(mInflater.inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TrackPickerSVH holder, int position) {
        boolean isSelected = mSelectedTracks.contains(mList.get(position));
        holder.itemView.setOnClickListener(v ->
                mListener.onItemClick(holder, holder.getAdapterPosition(),
                        mSelectedTracks.contains(mList.get(holder.getAdapterPosition()))));

        if (isSelected)
            holder.itemView.setBackground(ImageUtil.getAccentTintedSelectedItemBackground(holder.itemView.getContext()));
        else
            holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), android.R.color.transparent));

        holder.updateViewData(mList.get(position));
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull TrackPickerSVH holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    @Override
    public int getItemCount() {
        if (mList != null) return mList.size();
        return 0;
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

    private void dispatchUpdates(List<MusicModel> newItems, DiffUtil.DiffResult diffResult) {
        diffResult.dispatchUpdatesTo(this);
        mList.clear();
        mList.addAll(newItems);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        pendingUpdates.clear();
        mList.clear();
        super.onDetachedFromRecyclerView(recyclerView);
    }

    public Set<MusicModel> getSelectedTracks() {
        return mSelectedTracks;
    }

    static class TrackPickerSVH extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        private MediaArtImageView albumArt;
        private MaterialTextView title, artist;

        TrackPickerSVH(@NonNull View itemView) {
            super(itemView);
            albumArt = itemView.findViewById(R.id.list_item_album_art);
            title = itemView.findViewById(R.id.list_item_title);
            artist = itemView.findViewById(R.id.list_item_sub_title);
        }

        void updateViewData(MusicModel md) {
            title.setText(md.getTrackName());
            artist.setText(md.getArtist());
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
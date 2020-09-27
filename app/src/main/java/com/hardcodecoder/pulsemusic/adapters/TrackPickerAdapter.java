package com.hardcodecoder.pulsemusic.adapters;

import android.os.Handler;
import android.util.SparseBooleanArray;
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
import java.util.HashSet;
import java.util.List;

public class TrackPickerAdapter extends RecyclerView.Adapter<TrackPickerAdapter.TrackPickerSVH> implements TrackPickerCallbackAdapter {

    private final SparseBooleanArray mSelectedItemState = new SparseBooleanArray();
    private List<MusicModel> mList;
    private LayoutInflater mInflater;
    private TrackPickerListener mListener;
    private Deque<List<MusicModel>> pendingUpdates = new ArrayDeque<>();
    private Handler mMainHandler = new Handler();
    private HashSet<MusicModel> mSelectedTracks = new HashSet<>();


    public TrackPickerAdapter(List<MusicModel> mList, LayoutInflater mInflater, TrackPickerListener listener) {
        this.mList = mList;
        this.mInflater = mInflater;
        this.mListener = listener;
    }

    @Override
    public void onItemSelected(int position) {
        mSelectedItemState.put(position, true);
    }

    @Override
    public void onItemUnselected(int position) {
        mSelectedItemState.put(position, false);
    }

    @NonNull
    @Override
    public TrackPickerSVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TrackPickerSVH(mInflater.inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TrackPickerSVH holder, int position) {
        boolean isSelected = mSelectedItemState.get(position, false);
        holder.itemView.setOnClickListener(v ->
                mListener.onItemClick(holder, holder.getAdapterPosition(), mSelectedItemState.get(holder.getAdapterPosition(), false)));

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
        else return 0;
    }

    public void updateItems(final List<MusicModel> newItems) {
        pendingUpdates.push(newItems);
        if (pendingUpdates.size() > 1) {
            return;
        }
        updateItemsInternal(newItems);
    }

    public void updateSelection() {
        mSelectedItemState.clear();
        for (int i = 0; i < mList.size(); i++) {
            MusicModel model = mList.get(i);
            if (mSelectedTracks.contains(model)) {
                mSelectedItemState.put(i, true);
            }
        }
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
        updateSelection();
        diffResult.dispatchUpdatesTo(this);
        mList.clear();
        mList.addAll(newItems);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        pendingUpdates.clear();
        mList.clear();
    }

    public HashSet<MusicModel> getSelectedTracks() {
        return mSelectedTracks;
    }

    public void removeSelection(MusicModel musicModel) {
        mSelectedTracks.remove(musicModel);
    }

    public void addSelection(MusicModel musicModel) {
        mSelectedTracks.add(musicModel);
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

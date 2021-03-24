package com.hardcodecoder.pulsemusic.adapters.main;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.adapters.base.SelectableItemAdapter;
import com.hardcodecoder.pulsemusic.helper.DiffCb;
import com.hardcodecoder.pulsemusic.interfaces.ItemSelectorListener;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.views.MediaArtImageView;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class TracksSelectorAdapter extends SelectableItemAdapter<MusicModel, TracksSelectorAdapter.TrackItemHolder> {

    private final Deque<List<MusicModel>> pendingUpdates = new ArrayDeque<>();
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());
    private LayoutInflater mInflater;
    private ItemSelectorListener mListener;

    public TracksSelectorAdapter(@NonNull LayoutInflater inflater,
                                 @NonNull List<MusicModel> list,
                                 @NonNull ItemSelectorListener listener) {
        super(new ArrayList<>(list));
        mInflater = inflater;
        mListener = listener;
    }

    @NonNull
    @Override
    public TrackItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TrackItemHolder(mInflater.inflate(R.layout.list_item, parent, false), mListener);
    }

    @NonNull
    @Override
    protected String getSectionText(@NonNull MusicModel data) {
        return data.getTrackName().substring(0, 1);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mInflater = null;
        mListener = null;
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
            final List<MusicModel> oldItems = new ArrayList<>(getDataList());
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
        getDataList().clear();
        getDataList().addAll(newItems);
    }

    static class TrackItemHolder extends SelectableItemAdapter.SelectableItemHolder<MusicModel> {

        private final MediaArtImageView mTrackArt;
        private final MaterialTextView mTrackTitle;
        private final MaterialTextView mTrackArtist;

        public TrackItemHolder(@NonNull View itemView, @NonNull ItemSelectorListener listener) {
            super(itemView);
            mTrackArt = itemView.findViewById(R.id.list_item_album_art);
            mTrackTitle = itemView.findViewById(R.id.list_item_title);
            mTrackArtist = itemView.findViewById(R.id.list_item_sub_title);
            itemView.setOnClickListener(v ->
                    listener.onItemClick(this, getAdapterPosition(), shouldEnableSelection()));
        }

        @Override
        public void bindData(@NonNull MusicModel data, boolean selectThisItem) {
            mTrackArt.loadAlbumArt(data.getAlbumArtUrl(), data.getAlbumId());
            mTrackTitle.setText(data.getTrackName());
            mTrackArtist.setText(data.getArtist());
            super.bindData(data, selectThisItem);
        }

        @Override
        public void unBindData() {
            mTrackArt.clearLoadedArt();
            mTrackArt.setImageDrawable(null);
            mTrackTitle.setText(null);
            mTrackArtist.setText(null);
        }
    }
}
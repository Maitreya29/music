package com.hardcodecoder.pulsemusic.adapters.main;

import android.content.res.Configuration;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.adapters.base.EfficientRecyclerViewAdapter;
import com.hardcodecoder.pulsemusic.helper.PMBGridAdapterDiffCallback;
import com.hardcodecoder.pulsemusic.interfaces.GridAdapterCallback;
import com.hardcodecoder.pulsemusic.interfaces.SimpleTransitionClickListener;
import com.hardcodecoder.pulsemusic.loaders.SortOrder;
import com.hardcodecoder.pulsemusic.model.ArtistModel;
import com.hardcodecoder.pulsemusic.themes.TintHelper;
import com.hardcodecoder.pulsemusic.utils.ImageUtil;
import com.hardcodecoder.pulsemusic.utils.SortUtil;

import java.util.ArrayList;
import java.util.List;

public class ArtistAdapter extends EfficientRecyclerViewAdapter<ArtistModel, ArtistAdapter.ArtistItemHolder> {

    private final LayoutInflater mInflater;
    private final SimpleTransitionClickListener mListener;
    private final GridAdapterCallback mCallback;
    private int mOrientation;
    private int mCurrentSpanCount;
    private int mLayoutId;

    public ArtistAdapter(@NonNull LayoutInflater inflater,
                         @NonNull List<ArtistModel> artistList,
                         @NonNull SimpleTransitionClickListener listener,
                         @Nullable GridAdapterCallback callback,
                         int orientation,
                         int spanCount) {
        super(artistList);
        mInflater = inflater;
        mListener = listener;
        mCallback = callback;
        mOrientation = orientation;
        mCurrentSpanCount = spanCount;
        mLayoutId = getLayoutId();
    }

    public void updateSortOrder(SortOrder.ARTIST sortOrder) {
        final Handler handler = new Handler();
        TaskRunner.executeAsync(() -> {
            List<ArtistModel> currentDataList = getDataList();
            List<ArtistModel> oldSortedTracks = new ArrayList<>(currentDataList);
            SortUtil.sortArtistList(currentDataList, sortOrder);
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PMBGridAdapterDiffCallback(oldSortedTracks, currentDataList) {
                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return oldSortedTracks.get(oldItemPosition).getArtistName().equals(currentDataList.get(newItemPosition).getArtistName());
                }
            });
            handler.post(() -> {
                diffResult.dispatchUpdatesTo(ArtistAdapter.this);
                mCallback.onSortUpdateComplete();
            });
        });
    }

    public void updateSpanCount(int orientation, int newSpanCount) {
        mOrientation = orientation;
        mCurrentSpanCount = newSpanCount;
        mLayoutId = getLayoutId();
    }

    private int getLayoutId() {
        if (mOrientation == Configuration.ORIENTATION_PORTRAIT && mCurrentSpanCount >= 3 ||
                mOrientation == Configuration.ORIENTATION_LANDSCAPE && mCurrentSpanCount >= 5) {
            return R.layout.rv_grid_item_artist_small;
        }
        return R.layout.rv_grid_item_artist;
    }

    @Override
    protected CharSequence getSectionText(@NonNull ArtistModel data) {
        return data.getArtistName().substring(0, 1);
    }

    @NonNull
    @Override
    public ArtistItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArtistItemHolder(mInflater.inflate(mLayoutId, parent, false), mListener);
    }

    static class ArtistItemHolder extends EfficientRecyclerViewAdapter.SmartViewHolder<ArtistModel> {

        private final ImageView mArtistArt;
        private final TextView mArtistTitle;

        public ArtistItemHolder(@NonNull View itemView, @NonNull SimpleTransitionClickListener listener) {
            super(itemView);
            itemView.setBackground(ImageUtil.getHighlightedItemBackground(itemView.getContext()));
            mArtistArt = itemView.findViewById(R.id.grid_item_artist_iv);
            TintHelper.setAccentTintTo(mArtistArt);
            mArtistTitle = itemView.findViewById(R.id.grid_item_artist_tv);
            itemView.setOnClickListener(v ->
                    listener.onItemClick(mArtistArt, getAdapterPosition()));
        }

        @Override
        public void bindData(@NonNull ArtistModel data) {
            mArtistArt.setTransitionName("shared_transition_artist_iv_" + getAdapterPosition());
            mArtistTitle.setText(data.getArtistName());
        }

        @Override
        public void unbindData() {
        }
    }
}
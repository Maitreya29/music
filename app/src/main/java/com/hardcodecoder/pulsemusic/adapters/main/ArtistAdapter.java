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

    private static final int VIEW_TYPE_SMALL = R.layout.rv_grid_item_artist_small;
    private static final int VIEW_TYPE_NORMAL = R.layout.rv_grid_item_artist;
    private final LayoutInflater mInflater;
    private final SimpleTransitionClickListener mListener;
    private final GridAdapterCallback mCallback;
    private boolean mNeedUseSmallLayout = false;

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
        updateColumnCount(orientation, spanCount);
    }

    public void updateSortOrder(SortOrder.ARTIST sortOrder) {
        final Handler handler = TaskRunner.getMainHandler();
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
                if (null != mCallback) mCallback.onSortUpdateComplete();
            });
        });
    }

    public void updateColumnCount(int orientation, int newSpanCount) {
        mNeedUseSmallLayout = (orientation == Configuration.ORIENTATION_PORTRAIT && newSpanCount > 2) ||
                (orientation == Configuration.ORIENTATION_LANDSCAPE && newSpanCount > 4);
    }

    @NonNull
    @Override
    protected String getSectionText(@NonNull ArtistModel data) {
        return data.getArtistName().substring(0, 1);
    }

    @NonNull
    @Override
    public ArtistItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArtistItemHolder(mInflater.inflate(viewType, parent, false), mListener);
    }

    @Override
    public int getItemViewType(int position) {
        return mNeedUseSmallLayout ? VIEW_TYPE_SMALL : VIEW_TYPE_NORMAL;
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
package com.radiant.music.adapters.main;

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

import com.radiant.music.R;
import com.radiant.music.TaskRunner;
import com.radiant.music.adapters.base.EfficientRecyclerViewAdapter;
import com.radiant.music.helper.PMBGridAdapterDiffCallback;
import com.radiant.music.interfaces.GridAdapterCallback;
import com.radiant.music.interfaces.SimpleTransitionClickListener;
import com.radiant.music.loaders.SortOrder;
import com.radiant.music.model.ArtistModel;
import com.radiant.music.themes.TintHelper;
import com.radiant.music.utils.ImageUtil;
import com.radiant.music.utils.SortUtil;

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
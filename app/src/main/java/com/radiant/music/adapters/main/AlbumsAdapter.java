package com.radiant.music.adapters.main;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.radiant.music.model.AlbumModel;
import com.radiant.music.utils.SortUtil;
import com.radiant.music.views.MediaArtImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AlbumsAdapter extends EfficientRecyclerViewAdapter<AlbumModel, AlbumsAdapter.AlbumsItemHolder> {

    private final LayoutInflater mInflater;
    private final SimpleTransitionClickListener mListener;
    private final GridAdapterCallback mCallback;
    private SimpleDateFormat mDateFormatter = null;
    private SortOrder.ALBUMS mSortOrder;

    public AlbumsAdapter(@NonNull LayoutInflater inflater,
                         @NonNull List<AlbumModel> albumsList,
                         @NonNull SimpleTransitionClickListener listener,
                         @Nullable GridAdapterCallback callback,
                         @NonNull SortOrder.ALBUMS sortOrder) {
        super(albumsList);
        mInflater = inflater;
        mSortOrder = sortOrder;
        mListener = listener;
        mCallback = callback;
    }

    @NonNull
    @Override
    public AlbumsItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlbumsItemHolder(mInflater.inflate(R.layout.rv_big_album_item, parent, false), mListener);
    }

    public void updateSortOrder(SortOrder.ALBUMS sortOrder) {
        mSortOrder = sortOrder;
        final Handler handler = TaskRunner.getMainHandler();
        TaskRunner.executeAsync(() -> {
            List<AlbumModel> currentDataList = getDataList();
            List<AlbumModel> oldSortedTracks = new ArrayList<>(currentDataList);
            SortUtil.sortAlbumList(currentDataList, sortOrder);
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PMBGridAdapterDiffCallback(oldSortedTracks, currentDataList) {
                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return oldSortedTracks.get(oldItemPosition).getAlbumName().equals(currentDataList.get(newItemPosition).getAlbumName());
                }
            });
            handler.post(() -> {
                diffResult.dispatchUpdatesTo(AlbumsAdapter.this);
                if (null != mCallback) mCallback.onSortUpdateComplete();
            });
        });
    }

    @NonNull
    @Override
    protected String getSectionText(@NonNull AlbumModel data) {
        switch (mSortOrder) {
            case ARTIST_ASC:
            case ARTIST_DESC:
                return data.getAlbumArtist().substring(0, 1);
            case ALBUM_DATE_FIRST_YEAR_ASC:
            case ALBUM_DATE_FIRST_YEAR_DESC:
                return getDate(data.getFirstYear());
            case ALBUM_DATE_LAST_YEAR_ASC:
            case ALBUM_DATE_LAST_YEAR_DESC:
                return getDate(data.getLastYear());
            case TITLE_ASC:
            case TITLE_DESC:
            default:
                return data.getAlbumName().substring(0, 1);
        }
    }

    @NonNull
    private String getDate(long seconds) {
        if (null == mDateFormatter)
            mDateFormatter = new SimpleDateFormat("yyyy", Locale.getDefault());
        return mDateFormatter.format(new Date(seconds * 1000));
    }

    static class AlbumsItemHolder extends EfficientRecyclerViewAdapter.SmartViewHolder<AlbumModel> {

        private final MediaArtImageView mAlbumArt;
        private final TextView mAlbumTitle;

        public AlbumsItemHolder(@NonNull View itemView, @NonNull SimpleTransitionClickListener listener) {
            super(itemView);
            mAlbumArt = itemView.findViewById(R.id.big_album_item_art);
            mAlbumTitle = itemView.findViewById(R.id.big_album_item_title);
            itemView.setOnClickListener(v ->
                    listener.onItemClick(mAlbumArt, getAdapterPosition()));
        }

        @Override
        public void bindData(@NonNull AlbumModel data) {
            mAlbumTitle.setText(data.getAlbumName());
            mAlbumArt.setTransitionName("shared_transition_album_iv_" + getAdapterPosition());
            mAlbumArt.loadAlbumArt(data.getAlbumArt(), data.getAlbumId());
        }

        @Override
        public void unbindData() {
            mAlbumArt.clearLoadedArt();
            mAlbumArt.setImageDrawable(null);
            mAlbumTitle.setText(null);
        }
    }
}
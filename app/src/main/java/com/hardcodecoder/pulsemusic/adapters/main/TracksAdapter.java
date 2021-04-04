package com.hardcodecoder.pulsemusic.adapters.main;

import android.os.Handler;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.adapters.base.EfficientRecyclerViewAdapter;
import com.hardcodecoder.pulsemusic.helper.DiffCb;
import com.hardcodecoder.pulsemusic.interfaces.GridAdapterCallback;
import com.hardcodecoder.pulsemusic.interfaces.SimpleItemClickListener;
import com.hardcodecoder.pulsemusic.loaders.SortOrder;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.utils.SortUtil;
import com.hardcodecoder.pulsemusic.views.MediaArtImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TracksAdapter extends EfficientRecyclerViewAdapter<MusicModel, TracksAdapter.LibraryItemHolder> {

    private final LayoutInflater mInflater;
    private final SimpleItemClickListener mListener;
    private final GridAdapterCallback mCallback;
    private SimpleDateFormat mDateFormatter = null;
    private SortOrder mSortOrder;

    public TracksAdapter(@NonNull LayoutInflater inflater,
                         @NonNull List<MusicModel> tracksList,
                         @NonNull SimpleItemClickListener listener,
                         @Nullable GridAdapterCallback callback,
                         @Nullable SortOrder sortOrder) {
        super(tracksList);
        mInflater = inflater;
        mListener = listener;
        mCallback = callback;
        mSortOrder = sortOrder;
    }

    @NonNull
    @Override
    public LibraryItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LibraryItemHolder(mInflater.inflate(R.layout.list_item_with_options, parent, false), mListener);
    }

    public void updateSortOrder(@Nullable SortOrder sortOrder) {
        mSortOrder = sortOrder;
        final Handler handler = TaskRunner.getMainHandler();
        TaskRunner.executeAsync(() -> {
            List<MusicModel> currentDataList = getDataList();
            List<MusicModel> oldSortedTracks = new ArrayList<>(currentDataList);
            SortUtil.sortLibraryList(currentDataList, sortOrder);
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCb(oldSortedTracks, currentDataList));
            handler.post(() -> {
                diffResult.dispatchUpdatesTo(TracksAdapter.this);
                if (null != mCallback)
                    mCallback.onSortUpdateComplete();
            });
        });
    }

    @NonNull
    @Override
    protected String getSectionText(@NonNull MusicModel data) {
        switch (mSortOrder) {
            case DURATION_ASC:
            case DURATION_DESC:
                return DateUtils.formatElapsedTime(data.getTrackDuration() / 1000);
            case DATE_ADDED_ASC:
            case DATE_ADDED_DESC:
                return getDate(data.getDateAdded());
            case DATE_MODIFIED_ASC:
            case DATE_MODIFIED_DESC:
                return getDate(data.getDateModified());
            case TRACK_NUMBER_ASC:
            case TRACK_NUMBER_DESC:
                return String.valueOf(data.getTrackNumber());
            case TITLE_ASC:
            case TITLE_DESC:
            default:
                return data.getTrackName().substring(0, 1);
        }
    }

    @NonNull
    private String getDate(long seconds) {
        if (null == mDateFormatter)
            mDateFormatter = new SimpleDateFormat("MMM dd, yy", Locale.getDefault());
        return mDateFormatter.format(new Date(seconds * 1000));
    }

    static class LibraryItemHolder extends EfficientRecyclerViewAdapter.SmartViewHolder<MusicModel> {

        private final MaterialTextView mTrackTitle;
        private final MaterialTextView mTrackArtist;
        private final MediaArtImageView mTrackArt;

        public LibraryItemHolder(@NonNull View itemView, @NonNull SimpleItemClickListener listener) {
            super(itemView);
            mTrackTitle = itemView.findViewById(R.id.list_item_title);
            mTrackArtist = itemView.findViewById(R.id.list_item_sub_title);
            mTrackArt = itemView.findViewById(R.id.list_item_option_album_art);

            itemView.setOnClickListener(v ->
                    listener.onItemClick(getAdapterPosition()));

            itemView.findViewById(R.id.list_item_option_options_btn).setOnClickListener(v ->
                    listener.onOptionsClick(getAdapterPosition()));
        }

        @Override
        public void bindData(@NonNull MusicModel data) {
            mTrackTitle.setText(data.getTrackName());
            mTrackArtist.setText(data.getArtist());
            mTrackArt.loadAlbumArt(data.getAlbumArtUrl(), data.getAlbumId());
        }

        @Override
        public void unbindData() {
            mTrackArt.clearLoadedArt();
            mTrackArt.setImageDrawable(null);
        }
    }
}
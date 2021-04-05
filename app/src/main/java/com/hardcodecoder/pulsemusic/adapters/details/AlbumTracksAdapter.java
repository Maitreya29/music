package com.hardcodecoder.pulsemusic.adapters.details;

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
import com.hardcodecoder.pulsemusic.helper.DataModelHelper;
import com.hardcodecoder.pulsemusic.helper.DiffCb;
import com.hardcodecoder.pulsemusic.helper.MasterListUpdater;
import com.hardcodecoder.pulsemusic.interfaces.SimpleItemClickListener;
import com.hardcodecoder.pulsemusic.loaders.SortOrder;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.utils.SortUtil;
import com.hardcodecoder.pulsemusic.views.MediaArtImageView;

import java.util.ArrayList;
import java.util.List;

public class AlbumTracksAdapter extends EfficientRecyclerViewAdapter<MusicModel, AlbumTracksAdapter.AlbumTracksAdapterSVH>
        implements MasterListUpdater.OnMasterListUpdateListener {

    private final LayoutInflater mInflater;
    private final SimpleItemClickListener mListener;
    private SortOrder mSortOrder;

    public AlbumTracksAdapter(@NonNull LayoutInflater inflater,
                              @NonNull List<MusicModel> tracksList,
                              @NonNull SimpleItemClickListener listener,
                              @Nullable SortOrder sortOrder) {
        super(tracksList);
        mInflater = inflater;
        mListener = listener;
        mSortOrder = sortOrder;
    }

    public void updateSortOrder(@Nullable SortOrder sortOrder) {
        mSortOrder = sortOrder;
        final Handler handler = TaskRunner.getMainHandler();
        TaskRunner.executeAsync(() -> {
            List<MusicModel> currentDataList = getDataList();
            List<MusicModel> oldSortedTracks = new ArrayList<>(currentDataList);
            SortUtil.sortLibraryList(currentDataList, sortOrder);
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCb(oldSortedTracks, currentDataList));
            handler.post(() -> diffResult.dispatchUpdatesTo(AlbumTracksAdapter.this));
        });
    }

    @Override
    public void onItemDeleted(@NonNull MusicModel item) {
        DataModelHelper.getItemIndexInPlaylist(getDataList(), item, index -> {
            if (null == index || index == -1) return;
            getDataList().remove(index.intValue());
            notifyItemRemoved(index);
        });
    }

    @NonNull
    @Override
    protected String getSectionText(@NonNull MusicModel data) {
        switch (mSortOrder) {
            case DURATION_ASC:
            case DURATION_DESC:
                return DateUtils.formatElapsedTime(data.getTrackDuration() / 1000);
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
    @Override
    public AlbumTracksAdapterSVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlbumTracksAdapterSVH(mInflater.inflate(R.layout.list_item_with_options, parent, false), mListener);
    }

    static class AlbumTracksAdapterSVH extends EfficientRecyclerViewAdapter.SmartViewHolder<MusicModel> {

        private final MaterialTextView mTrackTitle;
        private final MaterialTextView mSubText;
        private final MediaArtImageView mTrackArt;

        public AlbumTracksAdapterSVH(@NonNull View itemView, @NonNull SimpleItemClickListener listener) {
            super(itemView);
            mTrackTitle = itemView.findViewById(R.id.list_item_title);
            mSubText = itemView.findViewById(R.id.list_item_sub_title);
            mTrackArt = itemView.findViewById(R.id.list_item_option_album_art);

            itemView.setOnClickListener(v ->
                    listener.onItemClick(getAdapterPosition()));

            itemView.findViewById(R.id.list_item_option_options_btn).setOnClickListener(v ->
                    listener.onOptionsClick(getAdapterPosition()));
        }

        @Override
        public void bindData(@NonNull MusicModel data) {
            mTrackTitle.setText(data.getTrackName());
            String subText = "● "
                    + itemView.getContext().getString(R.string.track_number_info, data.getDiscNumber(), data.getTrackNumber())
                    + "  ● "
                    + data.getArtist();
            mSubText.setText(subText);
            mTrackArt.loadAlbumArt(data.getAlbumArtUrl(), data.getAlbumId());
        }

        @Override
        public void unbindData() {
            mTrackArt.clearLoadedArt();
            mTrackArt.setImageDrawable(null);
        }
    }
}
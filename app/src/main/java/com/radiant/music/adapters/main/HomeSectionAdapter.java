package com.radiant.music.adapters.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.textview.MaterialTextView;
import com.radiant.music.R;
import com.radiant.music.adapters.base.EfficientRecyclerViewAdapter;
import com.radiant.music.helper.DataModelHelper;
import com.radiant.music.helper.MasterListUpdater;
import com.radiant.music.interfaces.SimpleItemClickListener;
import com.radiant.music.model.MusicModel;
import com.radiant.music.views.MediaArtImageView;

import java.util.List;

public class HomeSectionAdapter extends EfficientRecyclerViewAdapter<MusicModel, HomeSectionAdapter.HomeSectionItemHolder>
        implements MasterListUpdater.OnMasterListUpdateListener {

    private final LayoutInflater mInflater;
    private final SimpleItemClickListener mListener;

    public HomeSectionAdapter(
            @NonNull LayoutInflater inflater,
            @NonNull List<MusicModel> tracksList,
            @NonNull SimpleItemClickListener listener) {
        super(tracksList);
        mInflater = inflater;
        mListener = listener;
    }

    @NonNull
    @Override
    public HomeSectionItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeSectionItemHolder(mInflater.inflate(R.layout.rv_home_item_sq, parent, false), mListener);
    }

    @Override
    public void onItemDeleted(@NonNull MusicModel item) {
        DataModelHelper.getItemIndexInPlaylist(getDataList(), item, index -> {
            if (null == index || index == -1) return;
            getDataList().remove(index.intValue());
            notifyItemRemoved(index);
        });
    }

    static class HomeSectionItemHolder extends EfficientRecyclerViewAdapter.SmartViewHolder<MusicModel> {

        private final MaterialTextView trackTitle;
        private final MaterialTextView trackArtist;
        private final MediaArtImageView trackArt;

        public HomeSectionItemHolder(@NonNull View itemView, @NonNull SimpleItemClickListener listener) {
            super(itemView);

            trackTitle = itemView.findViewById(R.id.home_rv_list_item_title);
            trackArtist = itemView.findViewById(R.id.home_rv_list_item_text);
            trackArt = itemView.findViewById(R.id.home_rv_list_item_album_art);

            itemView.setOnLongClickListener(v -> {
                listener.onOptionsClick(getAdapterPosition());
                return true;
            });
            itemView.setOnClickListener(v ->
                    listener.onItemClick(getAdapterPosition()));
        }

        @Override
        public void bindData(@NonNull MusicModel data) {
            trackTitle.setText(data.getTrackName());
            trackArtist.setText(data.getArtist());
            trackArt.loadAlbumArt(data.getAlbumArtUrl(), data.getAlbumId());
        }

        @Override
        public void unbindData() {
            trackArt.clearLoadedArt();
            trackArt.setImageDrawable(null);
        }
    }
}
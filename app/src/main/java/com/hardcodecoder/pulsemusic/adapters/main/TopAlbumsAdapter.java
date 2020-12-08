package com.hardcodecoder.pulsemusic.adapters.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.adapters.base.EfficientRecyclerViewAdapter;
import com.hardcodecoder.pulsemusic.interfaces.SimpleTransitionClickListener;
import com.hardcodecoder.pulsemusic.model.TopAlbumModel;
import com.hardcodecoder.pulsemusic.views.MediaArtImageView;

import java.util.List;

public class TopAlbumsAdapter extends EfficientRecyclerViewAdapter<TopAlbumModel, TopAlbumsAdapter.HomeSectionAlbumItemHolder> {

    private final LayoutInflater mInflater;
    private final SimpleTransitionClickListener mListener;

    public TopAlbumsAdapter(@NonNull LayoutInflater inflater,
                            @NonNull List<TopAlbumModel> topAlbumsList,
                            @NonNull SimpleTransitionClickListener listener) {
        super(topAlbumsList);
        mInflater = inflater;
        mListener = listener;
    }

    @NonNull
    @Override
    public HomeSectionAlbumItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeSectionAlbumItemHolder(mInflater.inflate(R.layout.rv_top_albums_item, parent, false), mListener);
    }

    static class HomeSectionAlbumItemHolder extends EfficientRecyclerViewAdapter.SmartViewHolder<TopAlbumModel> {

        private final MediaArtImageView mAlbumArt;
        private final MaterialTextView mAlbumTitle;

        public HomeSectionAlbumItemHolder(@NonNull View itemView, @NonNull SimpleTransitionClickListener listener) {
            super(itemView);
            mAlbumTitle = itemView.findViewById(R.id.home_rv_album_album_name);
            mAlbumArt = itemView.findViewById(R.id.home_rv_album_album_art);
            itemView.setOnClickListener(v ->
                    listener.onItemClick(mAlbumArt, getAdapterPosition()));
        }

        @Override
        public void bindData(@NonNull TopAlbumModel data) {
            mAlbumTitle.setText(data.getAlbumName());
            mAlbumArt.setTransitionName("shared_transition_album_iv_" + getAdapterPosition());
            mAlbumArt.loadAlbumArt(data.getAlbumArt(), data.getAlbumId());
        }

        @Override
        public void unbindData() {
            mAlbumArt.clearLoadedArt();
            mAlbumArt.setImageDrawable(null);
        }
    }
}
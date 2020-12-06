package com.hardcodecoder.pulsemusic.adapters.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.adapters.base.EfficientRecyclerViewAdapter;
import com.hardcodecoder.pulsemusic.interfaces.SimpleTransitionClickListener;
import com.hardcodecoder.pulsemusic.model.TopArtistModel;
import com.hardcodecoder.pulsemusic.themes.TintHelper;
import com.hardcodecoder.pulsemusic.utils.ImageUtil;

import java.util.List;
import java.util.Locale;

public class TopArtistsAdapter extends EfficientRecyclerViewAdapter<TopArtistModel, TopArtistsAdapter.HomeSectionArtistsItemHolder> {

    private final LayoutInflater mInflater;
    private final SimpleTransitionClickListener mListener;

    public TopArtistsAdapter(@NonNull LayoutInflater inflater,
                             @NonNull List<TopArtistModel> topArtistList,
                             @NonNull SimpleTransitionClickListener listener) {
        super(topArtistList);
        mInflater = inflater;
        mListener = listener;
    }

    @NonNull
    @Override
    public HomeSectionArtistsItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeSectionArtistsItemHolder(mInflater.inflate(R.layout.rv_home_item_artist, parent, false), mListener);
    }

    static class HomeSectionArtistsItemHolder extends EfficientRecyclerViewAdapter.SmartViewHolder<TopArtistModel> {

        private final ImageView mArtistArt;
        private final MaterialTextView mArtistTitle;
        private final MaterialTextView mPlayCount;

        public HomeSectionArtistsItemHolder(@NonNull View itemView, @NonNull SimpleTransitionClickListener listener) {
            super(itemView);
            itemView.setBackground(ImageUtil.getHighlightedItemBackground(itemView.getContext()));

            mArtistArt = itemView.findViewById(R.id.home_rv_artist_list_item_art);
            TintHelper.setAccentTintTo(mArtistArt);

            mArtistTitle = itemView.findViewById(R.id.home_rv_list_item_title);
            mPlayCount = itemView.findViewById(R.id.home_rv_list_item_text);
            itemView.setOnClickListener(v ->
                    listener.onItemClick(mArtistArt, getAdapterPosition()));
        }

        @Override
        public void bindData(@NonNull TopArtistModel data) {
            mArtistArt.setTransitionName("shared_transition_artist_iv_" + getAdapterPosition());
            mArtistTitle.setText(data.getArtistName());
            mPlayCount.setText(String.format(Locale.getDefault(), "%s %d",
                    itemView.getResources().getString(R.string.tracks_plays),
                    data.getNumOfPlays()));

        }

        @Override
        public void unbindData() {
            mArtistTitle.setText(null);
            mPlayCount.setText(null);
        }
    }
}
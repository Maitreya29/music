package com.hardcodecoder.pulsemusic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.interfaces.SimpleTransitionClickListener;
import com.hardcodecoder.pulsemusic.model.TopAlbumModel;
import com.hardcodecoder.pulsemusic.views.MediaArtImageView;

import java.util.List;

public class HomeAdapterAlbum extends RecyclerView.Adapter<HomeAdapterAlbum.AdapterSVH> {

    private List<TopAlbumModel> mList;
    private LayoutInflater mInflater;
    private SimpleTransitionClickListener mListener;

    public HomeAdapterAlbum(List<TopAlbumModel> list, LayoutInflater inflater, SimpleTransitionClickListener listener) {
        this.mList = list;
        this.mInflater = inflater;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public HomeAdapterAlbum.AdapterSVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeAdapterAlbum.AdapterSVH(mInflater.inflate(R.layout.rv_album_card_item, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapterAlbum.AdapterSVH holder, int position) {
        holder.updateData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        if (null != mList)
            return mList.size();
        return 0;
    }

    static class AdapterSVH extends RecyclerView.ViewHolder {

        private MediaArtImageView albumArt;
        private MaterialTextView title;

        AdapterSVH(@NonNull View itemView, SimpleTransitionClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.home_rv_album_album_name);
            albumArt = itemView.findViewById(R.id.home_rv_album_album_art);
            itemView.setOnClickListener(v -> listener.onItemClick(albumArt, getAdapterPosition()));
        }

        void updateData(TopAlbumModel albumModel) {
            title.setText(albumModel.getAlbumName());
            albumArt.setTransitionName("shared_transition_album_iv_" + getAdapterPosition());
            albumArt.loadAlbumArt(albumModel.getAlbumArt(), albumModel.getAlbumId());
        }
    }
}

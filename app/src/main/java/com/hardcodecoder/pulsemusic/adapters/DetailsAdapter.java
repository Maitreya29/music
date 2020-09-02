package com.hardcodecoder.pulsemusic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.interfaces.SimpleItemClickListener;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.views.MediaArtImageView;

import java.util.List;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.DetailsSVH> {

    private List<MusicModel> mList;
    private SimpleItemClickListener listener;
    private LayoutInflater mInflater;

    public DetailsAdapter(List<MusicModel> mList, SimpleItemClickListener listener, LayoutInflater mInflater) {
        this.mList = mList;
        this.listener = listener;
        this.mInflater = mInflater;
    }

    @NonNull
    @Override
    public DetailsSVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DetailsSVH(mInflater.inflate(R.layout.list_item_with_options, parent, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsSVH holder, int position) {
        holder.updateData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        if (null != mList)
            return mList.size();
        return 0;
    }

    static class DetailsSVH extends RecyclerView.ViewHolder {

        private MediaArtImageView albumArt;
        private MaterialTextView title, subTitle;

        DetailsSVH(@NonNull View itemView, SimpleItemClickListener mListener) {
            super(itemView);
            albumArt = itemView.findViewById(R.id.list_item_option_album_art);
            title = itemView.findViewById(R.id.list_item_title);
            subTitle = itemView.findViewById(R.id.list_item_sub_title);
            itemView.findViewById(R.id.list_item_option_options_btn).setOnClickListener(v -> mListener.onOptionsClick(getAdapterPosition()));
            itemView.setOnClickListener(v -> mListener.onItemClick(getAdapterPosition()));
        }

        void updateData(MusicModel md) {
            title.setText(md.getTrackName());
            subTitle.setText(md.getArtist());
            albumArt.loadAlbumArt(md.getAlbumArtUrl(), md.getAlbumId());
        }
    }
}

package com.hardcodecoder.pulsemusic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.interfaces.SimpleItemClickListener;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.views.MediaArtImageView;

import java.util.List;

public class SimplePlaylistAdapter extends RecyclerView.Adapter<SimplePlaylistAdapter.SimpleViewHolder> {

    private List<MusicModel> mList;
    private SimpleItemClickListener mListener;
    private LayoutInflater mInflater;
    private int lastPosition = -1;

    public SimplePlaylistAdapter(List<MusicModel> list, LayoutInflater inflater, SimpleItemClickListener listener) {
        this.mList = list;
        this.mListener = listener;
        this.mInflater = inflater;
    }

    public void clearAll() {
        int size = mList.size();
        mList.clear();
        notifyItemRangeRemoved(0, size);
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SimpleViewHolder(mInflater.inflate(R.layout.list_item_with_options, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleViewHolder holder, int position) {
        holder.setItemData(mList.get(position));
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(),
                (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top));
        lastPosition = position;
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull SimpleViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    @Override
    public int getItemCount() {
        if (mList != null)
            return mList.size();
        return 0;
    }

    /*
     * Custom View holder class
     */
    static class SimpleViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView songName, artist;
        private MediaArtImageView albumArt;

        SimpleViewHolder(View itemView, SimpleItemClickListener listener) {
            super(itemView);
            songName = itemView.findViewById(R.id.list_item_title);
            artist = itemView.findViewById(R.id.list_item_sub_title);
            albumArt = itemView.findViewById(R.id.list_item_option_album_art);

            itemView.setOnClickListener(v -> listener.onItemClick(getAdapterPosition()));

            itemView.findViewById(R.id.list_item_option_options_btn).setOnClickListener(v -> v.post(() -> listener.onOptionsClick(getAdapterPosition())));
        }

        void setItemData(MusicModel md) {
            songName.setText(md.getTrackName());
            artist.setText(md.getArtist());
            albumArt.loadAlbumArt(md.getAlbumArtUrl(), md.getAlbumId());
        }
    }
}
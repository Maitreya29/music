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

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

    private final List<MusicModel> mList;
    private final SimpleItemClickListener mListener;
    private final LayoutInflater mInflater;

    public HomeAdapter(LayoutInflater inflater, List<MusicModel> list, SimpleItemClickListener clickListener) {
        this.mListener = clickListener;
        this.mList = list;
        this.mInflater = inflater;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(mInflater.inflate(R.layout.rv_home_item_sq, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setItemData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mList != null)
            return mList.size();
        return 0;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private final MaterialTextView title;
        private final MaterialTextView text;
        private final MediaArtImageView albumArt;

        MyViewHolder(View itemView, SimpleItemClickListener listener) {
            super(itemView);

            title = itemView.findViewById(R.id.home_rv_list_item_title);
            text = itemView.findViewById(R.id.home_rv_list_item_text);
            albumArt = itemView.findViewById(R.id.home_rv_list_item_album_art);

            itemView.setOnLongClickListener(v -> {
                listener.onOptionsClick(getAdapterPosition());
                return true;
            });
            itemView.setOnClickListener(v -> listener.onItemClick(getAdapterPosition()));
        }

        void setItemData(MusicModel md) {
            title.setText(md.getTrackName());
            text.setText(md.getArtist());
            albumArt.loadAlbumArt(md.getAlbumArtUrl(), md.getAlbumId());
        }
    }
}
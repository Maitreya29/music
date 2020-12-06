package com.hardcodecoder.pulsemusic.adapters.playlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.interfaces.SingleClickListener;

import java.util.List;

public class PlaylistItemAdapter extends RecyclerView.Adapter<PlaylistItemAdapter.PlaylistItemHolder> {

    private final LayoutInflater mInflater;
    private final List<String> mPlaylistNames;
    private final SingleClickListener mListener;

    public PlaylistItemAdapter(@NonNull LayoutInflater inflater,
                               @NonNull List<String> playlistNames,
                               @NonNull SingleClickListener listener) {
        mInflater = inflater;
        mPlaylistNames = playlistNames;
        mListener = listener;
    }

    public void addItem(String name) {
        mPlaylistNames.add(0, name);
        notifyItemInserted(0);
    }

    @NonNull
    @Override
    public PlaylistItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaylistItemHolder(mInflater.inflate(R.layout.rv_item_playlist_names, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistItemHolder holder, int position) {
        holder.setData(mPlaylistNames.get(position));
    }

    @Override
    public int getItemCount() {
        return mPlaylistNames.size();
    }

    static class PlaylistItemHolder extends RecyclerView.ViewHolder {

        private final MaterialTextView mPlaylistName;

        PlaylistItemHolder(@NonNull View itemView, SingleClickListener listener) {
            super(itemView);
            mPlaylistName = itemView.findViewById(R.id.rv_item_playlist_name);
            itemView.setOnClickListener(v -> listener.onItemCLick(getAdapterPosition()));
        }

        void setData(String name) {
            mPlaylistName.setText(name);
        }
    }
}
package com.hardcodecoder.pulsemusic.adapters.bottomsheet;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.interfaces.SingleClickListener;
import com.hardcodecoder.pulsemusic.model.AccentsModel;
import com.hardcodecoder.pulsemusic.themes.ThemeColors;
import com.hardcodecoder.pulsemusic.utils.DimensionsUtil;
import com.hardcodecoder.pulsemusic.views.ColorView;

public class AccentAdapter extends RecyclerView.Adapter<AccentAdapter.AccentAdapterSVH> {

    private final LayoutInflater mInflater;
    private final SingleClickListener mListener;
    private final AccentsModel[] mAccentsList;
    private final int mSelectedAccentId;

    public AccentAdapter(AccentsModel[] accentsArray, LayoutInflater inflater, int selectedAccentId, SingleClickListener listener) {
        this.mAccentsList = accentsArray;
        this.mInflater = inflater;
        this.mSelectedAccentId = selectedAccentId;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public AccentAdapterSVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AccentAdapterSVH(mInflater.inflate(R.layout.rv_accent_item, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AccentAdapterSVH holder, int position) {
        holder.setData(mAccentsList[position], mSelectedAccentId);
    }

    @Override
    public int getItemCount() {
        return null == mAccentsList ? 0 : mAccentsList.length;
    }

    static class AccentAdapterSVH extends RecyclerView.ViewHolder {

        private final ColorView mColorView;
        private final MaterialTextView mTitle;

        AccentAdapterSVH(@NonNull View itemView, SingleClickListener listener) {
            super(itemView);
            mColorView = itemView.findViewById(R.id.accent_color);
            mTitle = itemView.findViewById(R.id.accent_title);
            itemView.setOnClickListener(v -> {
                listener.onItemCLick(getAdapterPosition());
                markCurrentItemSelected();
            });
        }

        void setData(@NonNull AccentsModel accentsModel, int selectedAccentId) {
            if (accentsModel.getId() == selectedAccentId)
                markCurrentItemSelected();
            mColorView.setBackgroundColor(accentsModel.getColor());
            mTitle.setText(accentsModel.getTitle());
            mTitle.setTextColor(accentsModel.getColor());
        }

        private void markCurrentItemSelected() {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(DimensionsUtil.getDimension(itemView.getContext(), 8));
            drawable.setStroke(
                    DimensionsUtil.getDimensionPixelSize(itemView.getContext(), 2),
                    ThemeColors.getCurrentColorPrimary());
            itemView.setBackground(drawable);
        }
    }
}
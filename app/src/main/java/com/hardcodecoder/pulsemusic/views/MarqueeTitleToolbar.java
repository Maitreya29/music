package com.hardcodecoder.pulsemusic.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;

public class MarqueeTitleToolbar extends FrameLayout {

    private CustomToolbar mToolbar;
    private MaterialTextView mTitleTextView;

    public MarqueeTitleToolbar(@NonNull Context context) {
        this(context, null);
    }

    public MarqueeTitleToolbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeTitleToolbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    public void setTitle(CharSequence title) {
        mTitleTextView.setText(title);
        mTitleTextView.setSelected(true);
    }

    public void setNavigationOnClickListener(OnClickListener listener) {
        mToolbar.setNavigationOnClickListener(listener);
    }

    public CustomToolbar getToolbar() {
        return mToolbar;
    }

    private void initialize(@NonNull Context context) {
        View layout = View.inflate(context, R.layout.marquee_toolbar_layout, this);
        mToolbar = layout.findViewById(R.id.marquee_toolbar);
        mTitleTextView = layout.findViewById(R.id.marque_toolbar_title);
    }
}
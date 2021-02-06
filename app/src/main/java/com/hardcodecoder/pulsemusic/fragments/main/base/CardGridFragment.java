package com.hardcodecoder.pulsemusic.fragments.main.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;

public abstract class CardGridFragment extends PMBGridFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initialize();
        return inflater.inflate(R.layout.fragment_grid, container, false);
    }

    @Override
    public int getSortOrder() {
        return Preferences.SORT_ORDER_ASC;
    }

    @Override
    public int getPortraitModeSpanCount() {
        return Preferences.SPAN_COUNT_PORTRAIT_DEF_VALUE;
    }

    @Override
    public int getLandscapeModeSpanCount() {
        return Preferences.SPAN_COUNT_LANDSCAPE_DEF_VALUE;
    }
}
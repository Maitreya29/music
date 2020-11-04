package com.hardcodecoder.pulsemusic.fragments.main.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;

public class CardGridFragment extends PMBGridFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        initialize();
        return inflater.inflate(R.layout.fragment_grid, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(getMenuRes(getCurrentOrientation()), menu);
    }

    @Override
    public int getSortOrder() {
        return Preferences.SORT_ORDER_ASC;
    }

    @Override
    public int getMenuRes(int screenOrientation) {
        return 0;
    }

    @Override
    public int getPortraitModeSpanCount() {
        return Preferences.SPAN_COUNT_PORTRAIT_DEF_VALUE;
    }

    @Override
    public int getLandscapeModeSpanCount() {
        return Preferences.SPAN_COUNT_LANDSCAPE_DEF_VALUE;
    }

    @Override
    public void saveNewSpanCount(int configId, int spanCount) {
    }

    @Override
    public void onSortOrderChanged(int newSortOrder) {
    }

    @Override
    public void onLayoutSpanCountChanged(int currentOrientation, int spanCount) {
    }
}
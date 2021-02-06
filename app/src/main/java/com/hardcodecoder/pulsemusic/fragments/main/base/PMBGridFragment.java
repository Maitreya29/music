package com.hardcodecoder.pulsemusic.fragments.main.base;

import android.content.res.Configuration;

import androidx.annotation.NonNull;

public abstract class PMBGridFragment extends PulseFragment {

    private int mCurrentOrientation;
    private int mCurrentSpanCount;
    private int mCurrentSortOrder;

    void initialize() {
        mCurrentOrientation = getResources().getConfiguration().orientation;
        mCurrentSpanCount = mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT ?
                getPortraitModeSpanCount() : getLandscapeModeSpanCount();
        mCurrentSortOrder = getSortOrder();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        final int newOrientation = newConfig.orientation;
        if (newOrientation != mCurrentOrientation) {
            mCurrentOrientation = newOrientation;
            if (newOrientation == Configuration.ORIENTATION_LANDSCAPE)
                mCurrentSpanCount = getLandscapeModeSpanCount();
            else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
                mCurrentSpanCount = getPortraitModeSpanCount();

            onLayoutSpanCountChanged(mCurrentOrientation, mCurrentSpanCount);
        }
        if (null != getActivity())
            getActivity().invalidateOptionsMenu();
    }

    protected int getCurrentOrientation() {
        return mCurrentOrientation;
    }

    protected int getCurrentSortOrder() {
        return mCurrentSortOrder;
    }

    protected int getCurrentSpanCount() {
        return mCurrentSpanCount;
    }

    protected void changeSortOrder(int newSortOrder) {
        if (mCurrentSortOrder != newSortOrder) {
            mCurrentSortOrder = newSortOrder;
            onSortOrderChanged(newSortOrder);
        }
    }

    protected void updateGridSpanCount(int id, int spanCount) {
        onLayoutSpanCountChanged(id, spanCount);
    }

    public abstract int getSortOrder();

    public abstract int getPortraitModeSpanCount();

    public abstract int getLandscapeModeSpanCount();

    public abstract void onSortOrderChanged(int newSortOrder);

    public abstract void onLayoutSpanCountChanged(int currentOrientation, int spanCount);
}
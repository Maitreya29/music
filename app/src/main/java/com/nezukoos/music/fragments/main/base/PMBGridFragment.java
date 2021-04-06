package com.nezukoos.music.fragments.main.base;

import android.content.res.Configuration;

import androidx.annotation.NonNull;

public abstract class PMBGridFragment extends PulseFragment {

    private int mCurrentOrientation;
    private int mCurrentSpanCount;
    private int mCurrentSortOrder;

    void initialize() {
        mCurrentOrientation = getResources().getConfiguration().orientation;
        mCurrentSpanCount = mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT ?
                getPortraitModeColumnCount() : getLandscapeModeColumnCount();
        mCurrentSortOrder = getSortOrder();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        final int newOrientation = newConfig.orientation;
        if (newOrientation != mCurrentOrientation) {
            mCurrentOrientation = newOrientation;
            if (newOrientation == Configuration.ORIENTATION_LANDSCAPE)
                mCurrentSpanCount = getLandscapeModeColumnCount();
            else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
                mCurrentSpanCount = getPortraitModeColumnCount();

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

    protected int getCurrentColumnCount() {
        return mCurrentSpanCount;
    }

    protected void changeSortOrder(int newSortOrder) {
        if (mCurrentSortOrder != newSortOrder) {
            mCurrentSortOrder = newSortOrder;
            onSortOrderChanged(newSortOrder);
        }
    }

    protected void changeColumnCount(int id, int spanCount) {
        onLayoutSpanCountChanged(id, spanCount);
    }

    public abstract int getSortOrder();

    public abstract int getPortraitModeColumnCount();

    public abstract int getLandscapeModeColumnCount();

    public abstract void onSortOrderChanged(int newSortOrder);

    public abstract void onLayoutSpanCountChanged(int currentOrientation, int spanCount);
}
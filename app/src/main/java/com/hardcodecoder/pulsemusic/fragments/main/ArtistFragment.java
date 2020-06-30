package com.hardcodecoder.pulsemusic.fragments.main;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.adapters.ArtistAdapter;
import com.hardcodecoder.pulsemusic.loaders.LoaderHelper;
import com.hardcodecoder.pulsemusic.loaders.SortOrder;
import com.hardcodecoder.pulsemusic.model.ArtistModel;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.utils.NavigationUtil;

import java.util.List;

public class ArtistFragment extends PMBGridFragment {

    private GridLayoutManager mLayoutManager;
    private ArtistAdapter mAdapter;

    public static ArtistFragment getInstance() {
        return new ArtistFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final int sortOrder = getCurrentSortOrder();
        SortOrder.ARTIST artistSortOrder = (sortOrder == Preferences.SORT_ORDER_ASC) ?
                SortOrder.ARTIST.TITLE_ASC : SortOrder.ARTIST.TITLE_DESC;
        LoaderHelper.loadArtistsList(view.getContext().getContentResolver(),
                artistSortOrder,
                result -> loadArtistsList(view, result));
    }

    private void loadArtistsList(View view, List<ArtistModel> list) {
        if (null != list && list.size() > 0) {
            RecyclerView rv = (RecyclerView) ((ViewStub) view.findViewById(R.id.stub_grid_rv)).inflate();
            mLayoutManager = new GridLayoutManager(rv.getContext(), getCurrentSpanCount());
            rv.setLayoutManager(mLayoutManager);
            rv.setHasFixedSize(true);
            mAdapter = new ArtistAdapter(list, getLayoutInflater(), (sharedView, position) -> {
                if (null != getActivity())
                    NavigationUtil.goToArtist(getActivity(), sharedView, list.get(position).getArtistName());
            });
            rv.setAdapter(mAdapter);
        } else {
            MaterialTextView noTracksText = (MaterialTextView) ((ViewStub) view.findViewById(R.id.stub_no_tracks_found)).inflate();
            noTracksText.setText(getString(R.string.tracks_not_found));
        }
    }

    @Override
    public int getSortOrder() {
        if (null == getContext())
            return Preferences.SORT_ORDER_ASC;
        return AppSettings.getSortOrder(getContext(), Preferences.SORT_ORDER_ARTIST_KEY);
    }

    @Override
    public void onSortOrderChanged(int newSortOrder) {
        mAdapter.updateSortOrder();
        if (null != getContext())
            AppSettings.saveSortOrder(getContext(), Preferences.SORT_ORDER_ARTIST_KEY, newSortOrder);
    }

    @Override
    public int getPortraitModeSpanCount() {
        if (null == getContext())
            return Preferences.SPAN_COUNT_PORTRAIT_DEF_VALUE;
        return AppSettings.getPortraitModeGridSpanCount(getContext(), Preferences.ARTIST_SPAN_COUNT_PORTRAIT_KEY);
    }

    @Override
    public int getLandscapeModeSpanCount() {
        if (null == getContext())
            return Preferences.SPAN_COUNT_LANDSCAPE_DEF_VALUE;
        return AppSettings.getLandscapeModeGridSpanCount(getContext(), Preferences.ARTIST_SPAN_COUNT_LANDSCAPE_KEY);
    }

    @Override
    public void saveNewSpanCount(int configId, int spanCount) {
        if (null != getContext()) {
            if (configId == Configuration.ORIENTATION_PORTRAIT)
                AppSettings.savePortraitModeGridSpanCount(getContext(), Preferences.ARTIST_SPAN_COUNT_PORTRAIT_KEY, spanCount);
            else if (configId == Configuration.ORIENTATION_LANDSCAPE)
                AppSettings.saveLandscapeModeGridSpanCount(getContext(), Preferences.ARTIST_SPAN_COUNT_LANDSCAPE_KEY, spanCount);
        }
    }

    @Override
    public void onLayoutSpanCountChanged(int spanCount) {
        mLayoutManager.setSpanCount(spanCount);
    }
}
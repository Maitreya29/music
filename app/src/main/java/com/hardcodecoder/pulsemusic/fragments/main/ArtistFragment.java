package com.hardcodecoder.pulsemusic.fragments.main;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.adapters.ArtistAdapter;
import com.hardcodecoder.pulsemusic.fragments.main.base.CardGridFragment;
import com.hardcodecoder.pulsemusic.loaders.LoaderHelper;
import com.hardcodecoder.pulsemusic.loaders.SortOrder.ARTIST;
import com.hardcodecoder.pulsemusic.model.ArtistModel;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.utils.NavigationUtil;

import java.util.List;

public class ArtistFragment extends CardGridFragment {

    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private ArtistAdapter mAdapter;
    private int mFirstVisibleItemPosition;

    public static ArtistFragment getInstance() {
        return new ArtistFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final int sortOrder = getCurrentSortOrder();
        ARTIST artistSortOrder = (sortOrder == Preferences.SORT_ORDER_ASC) ?
                ARTIST.TITLE_ASC : ARTIST.TITLE_DESC;
        LoaderHelper.loadArtistsList(view.getContext().getContentResolver(),
                artistSortOrder,
                result -> loadArtistsList(view, result));
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem sortItem = null;
        final int sortOrder = getCurrentSortOrder();

        if (sortOrder == Preferences.SORT_ORDER_ASC)
            sortItem = menu.findItem(R.id.menu_action_sort_artist_title_asc);
        else if (sortOrder == Preferences.SORT_ORDER_DESC)
            sortItem = menu.findItem(R.id.menu_action_sort_artist_title_desc);


        MenuItem spanItem = null;
        final int spanCount = getCurrentSpanCount();

        if (spanCount == 2)
            spanItem = menu.findItem(R.id.artist_two);
        else if (spanCount == 3)
            spanItem = menu.findItem(R.id.artist_three);
        else if (spanCount == 4)
            spanItem = menu.findItem(R.id.artist_four);
        else if (spanCount == 5)
            spanItem = menu.findItem(R.id.artist_five);
        else if (spanCount == 6)
            spanItem = menu.findItem(R.id.artist_six);

        if (sortItem != null)
            sortItem.setChecked(true);
        if (spanItem != null)
            spanItem.setChecked(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int groupId = item.getGroupId();
        if (groupId == R.id.group_artist_sort) {
            final int id = item.getItemId();
            int sortOrder;

            if (id == R.id.menu_action_sort_artist_title_desc)
                sortOrder = Preferences.SORT_ORDER_DESC;
            else sortOrder = Preferences.SORT_ORDER_ASC;

            changeSortOrder(sortOrder);

        } else if (groupId == R.id.group_artist_grid) {
            final int id = item.getItemId();
            int spanCount;

            if (id == R.id.artist_two)
                spanCount = 2;
            else if (id == R.id.artist_three)
                spanCount = 3;
            else if (id == R.id.artist_four)
                spanCount = 4;
            else if (id == R.id.artist_five)
                spanCount = 5;
            else if (id == R.id.artist_six)
                spanCount = 6;
            else spanCount = 2;

            updateGridSpanCount(getCurrentOrientation(), spanCount);
        }

        item.setChecked(true);
        return true;
    }

    private void loadArtistsList(View view, List<ArtistModel> list) {
        if (list == null || list.isEmpty()) {
            MaterialTextView noTracksText = (MaterialTextView) ((ViewStub) view.findViewById(R.id.stub_no_tracks_found)).inflate();
            noTracksText.setText(getString(R.string.tracks_not_found));
        } else {
            mRecyclerView = (RecyclerView) ((ViewStub) view.findViewById(R.id.stub_grid_rv)).inflate();
            mLayoutManager = new GridLayoutManager(mRecyclerView.getContext(), getCurrentSpanCount());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setHasFixedSize(true);
            mAdapter = new ArtistAdapter(list,
                    getLayoutInflater(),
                    (sharedView, position) -> {
                        if (null != getActivity())
                            NavigationUtil.goToArtist(getActivity(), sharedView, list.get(position).getArtistName());
                    },
                    () -> mLayoutManager.scrollToPosition(mFirstVisibleItemPosition),
                    getCurrentOrientation(),
                    getCurrentSpanCount());
            LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(mRecyclerView.getContext(), R.anim.item_enter_slide_up);
            mRecyclerView.setLayoutAnimation(controller);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private ARTIST resolveSortOrder(int sortOrder) {
        if (sortOrder == Preferences.SORT_ORDER_ASC)
            return ARTIST.TITLE_ASC;
        return ARTIST.TITLE_DESC;
    }

    @Override
    public int getMenuRes(int screenOrientation) {
        if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE)
            return R.menu.menu_artist_land;
        return R.menu.menu_artist;
    }

    @Override
    public int getSortOrder() {
        if (null == getContext())
            return super.getSortOrder();
        return AppSettings.getSortOrder(getContext(), Preferences.SORT_ORDER_ARTIST_KEY);
    }

    @Override
    public void onSortOrderChanged(int newSortOrder) {
        if (mAdapter == null) return;
        mFirstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
        mAdapter.updateSortOrder(resolveSortOrder(newSortOrder));
        if (null != getContext())
            AppSettings.saveSortOrder(getContext(), Preferences.SORT_ORDER_ARTIST_KEY, newSortOrder);
    }

    @Override
    public int getPortraitModeSpanCount() {
        if (null == getContext())
            return super.getPortraitModeSpanCount();
        return AppSettings.getPortraitModeGridSpanCount(
                getContext(),
                Preferences.ARTIST_SPAN_COUNT_PORTRAIT_KEY,
                Preferences.SPAN_COUNT_PORTRAIT_DEF_VALUE);
    }

    @Override
    public int getLandscapeModeSpanCount() {
        if (null == getContext())
            return super.getLandscapeModeSpanCount();
        return AppSettings.getLandscapeModeGridSpanCount(
                getContext(),
                Preferences.ARTIST_SPAN_COUNT_LANDSCAPE_KEY,
                Preferences.SPAN_COUNT_LANDSCAPE_DEF_VALUE);
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
    public void onLayoutSpanCountChanged(int currentOrientation, int spanCount) {
        if (mLayoutManager == null) return;
        mFirstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
        mAdapter.updateSpanCount(currentOrientation, spanCount);
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager.setSpanCount(spanCount);
        mLayoutManager.scrollToPosition(mFirstVisibleItemPosition);
    }
}
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
import com.hardcodecoder.pulsemusic.adapters.main.AlbumsAdapter;
import com.hardcodecoder.pulsemusic.fragments.main.base.CardGridFragment;
import com.hardcodecoder.pulsemusic.loaders.LoaderHelper;
import com.hardcodecoder.pulsemusic.loaders.SortOrder.ALBUMS;
import com.hardcodecoder.pulsemusic.model.AlbumModel;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.utils.NavigationUtil;

import java.util.List;

public class AlbumsFragment extends CardGridFragment {

    private GridLayoutManager mLayoutManager;
    private AlbumsAdapter mAdapter;
    private int mFirstVisibleItemPosition;

    @NonNull
    public static AlbumsFragment getInstance() {
        return new AlbumsFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final int sortOrder = getCurrentSortOrder();
        LoaderHelper.loadAlbumsList(view.getContext().getContentResolver(),
                resolveSortOrder(sortOrder),
                result -> loadAlbumsList(view, result));
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem sortItem = null;
        final int sortOrder = getCurrentSortOrder();

        if (sortOrder == Preferences.SORT_ORDER_ASC)
            sortItem = menu.findItem(R.id.menu_action_sort_album_title_asc);
        else if (sortOrder == Preferences.SORT_ORDER_DESC)
            sortItem = menu.findItem(R.id.menu_action_sort_album_title_desc);
        else if (sortOrder == Preferences.SORT_ORDER_ALBUM_ARTIST_ASC)
            sortItem = menu.findItem(R.id.menu_action_sort_album_artist_asc);
        else if (sortOrder == Preferences.SORT_ORDER_ALBUM_ARTIST_DESC)
            sortItem = menu.findItem(R.id.menu_action_sort_album_artist_desc);
        else if (sortOrder == Preferences.SORT_ORDER_ALBUM_FIRST_YEAR_ASC)
            sortItem = menu.findItem(R.id.menu_action_sort_album_first_year_asc);
        else if (sortOrder == Preferences.SORT_ORDER_ALBUM_FIRST_YEAR_DESC)
            sortItem = menu.findItem(R.id.menu_action_sort_album_first_year_desc);


        MenuItem spanItem = null;
        final int spanCount = getCurrentSpanCount();

        if (spanCount == 2)
            spanItem = menu.findItem(R.id.album_two);
        else if (spanCount == 3)
            spanItem = menu.findItem(R.id.album_three);
        else if (spanCount == 4)
            spanItem = menu.findItem(R.id.album_four);
        else if (spanCount == 5)
            spanItem = menu.findItem(R.id.album_five);
        else if (spanCount == 6)
            spanItem = menu.findItem(R.id.album_six);

        if (sortItem != null)
            sortItem.setChecked(true);
        if (spanItem != null)
            spanItem.setChecked(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int groupId = item.getGroupId();
        if (groupId == R.id.group_album_sort) {
            final int id = item.getItemId();
            int sortOrder;

            if (id == R.id.menu_action_sort_album_title_asc)
                sortOrder = Preferences.SORT_ORDER_ASC;
            else if (id == R.id.menu_action_sort_album_title_desc)
                sortOrder = Preferences.SORT_ORDER_DESC;
            else if (id == R.id.menu_action_sort_album_artist_asc)
                sortOrder = Preferences.SORT_ORDER_ALBUM_ARTIST_ASC;
            else if (id == R.id.menu_action_sort_album_artist_desc)
                sortOrder = Preferences.SORT_ORDER_ALBUM_ARTIST_DESC;
            else if (id == R.id.menu_action_sort_album_first_year_asc)
                sortOrder = Preferences.SORT_ORDER_ALBUM_FIRST_YEAR_ASC;
            else if (id == R.id.menu_action_sort_album_first_year_desc)
                sortOrder = Preferences.SORT_ORDER_ALBUM_FIRST_YEAR_DESC;
            else sortOrder = Preferences.SORT_ORDER_ASC;

            changeSortOrder(sortOrder);

        } else if (groupId == R.id.group_album_grid) {
            final int id = item.getItemId();
            int spanCount;

            if (id == R.id.album_two)
                spanCount = 2;
            else if (id == R.id.album_three)
                spanCount = 3;
            else if (id == R.id.album_four)
                spanCount = 4;
            else if (id == R.id.album_five)
                spanCount = 5;
            else if (id == R.id.album_six)
                spanCount = 6;
            else spanCount = 2;

            updateGridSpanCount(getCurrentOrientation(), spanCount);
        }

        item.setChecked(true);
        return true;
    }

    private ALBUMS resolveSortOrder(int sortOrder) {
        switch (sortOrder) {
            case Preferences.SORT_ORDER_DESC:
                return ALBUMS.TITLE_DESC;
            case Preferences.SORT_ORDER_ALBUM_ARTIST_ASC:
                return ALBUMS.ARTIST_ASC;
            case Preferences.SORT_ORDER_ALBUM_ARTIST_DESC:
                return ALBUMS.ARTIST_DESC;
            case Preferences.SORT_ORDER_ALBUM_FIRST_YEAR_ASC:
                return ALBUMS.ALBUM_DATE_FIRST_YEAR_ASC;
            case Preferences.SORT_ORDER_ALBUM_FIRST_YEAR_DESC:
                return ALBUMS.ALBUM_DATE_FIRST_YEAR_DESC;
            case Preferences.SORT_ORDER_ASC:
            default:
                return ALBUMS.TITLE_ASC;
        }
    }

    @Override
    public int getSortOrder() {
        if (null == getContext())
            return super.getSortOrder();
        return AppSettings.getSortOrder(getContext(), Preferences.SORT_ORDER_ALBUMS_KEY);
    }

    @Override
    public int getMenuRes(int screenOrientation) {
        if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE)
            return R.menu.menu_album_land;
        return R.menu.menu_album;
    }

    @Override
    public void onSortOrderChanged(int newSortOrder) {
        if (mAdapter == null) return;
        mFirstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
        mAdapter.updateSortOrder(resolveSortOrder(newSortOrder));
        if (null != getContext())
            AppSettings.saveSortOrder(getContext(), Preferences.SORT_ORDER_ALBUMS_KEY, newSortOrder);
    }

    @Override
    public int getPortraitModeSpanCount() {
        if (null == getContext())
            return super.getPortraitModeSpanCount();
        return AppSettings.getPortraitModeGridSpanCount(
                getContext(),
                Preferences.ALBUMS_SPAN_COUNT_PORTRAIT_KEY,
                Preferences.SPAN_COUNT_PORTRAIT_DEF_VALUE);
    }

    @Override
    public int getLandscapeModeSpanCount() {
        if (null == getContext())
            return super.getLandscapeModeSpanCount();
        return AppSettings.getLandscapeModeGridSpanCount(
                getContext(),
                Preferences.ALBUMS_SPAN_COUNT_LANDSCAPE_KEY,
                Preferences.SPAN_COUNT_LANDSCAPE_DEF_VALUE);
    }

    @Override
    public void saveNewSpanCount(int configId, int spanCount) {
        if (null != getContext()) {
            if (configId == Configuration.ORIENTATION_PORTRAIT)
                AppSettings.savePortraitModeGridSpanCount(getContext(), Preferences.ALBUMS_SPAN_COUNT_PORTRAIT_KEY, spanCount);
            else if (configId == Configuration.ORIENTATION_LANDSCAPE)
                AppSettings.saveLandscapeModeGridSpanCount(getContext(), Preferences.ALBUMS_SPAN_COUNT_LANDSCAPE_KEY, spanCount);
        }
    }

    @Override
    public void onLayoutSpanCountChanged(int currentOrientation, int spanCount) {
        if (mLayoutManager == null) return;
        mLayoutManager.setSpanCount(spanCount);
    }

    private void loadAlbumsList(@NonNull View view, @Nullable List<AlbumModel> list) {
        if (list == null || list.isEmpty()) {
            MaterialTextView noTracksText = (MaterialTextView) ((ViewStub) view.findViewById(R.id.stub_no_tracks_found)).inflate();
            noTracksText.setText(getString(R.string.tracks_not_found));
        } else {
            RecyclerView rv = (RecyclerView) ((ViewStub) view.findViewById(R.id.stub_grid_rv)).inflate();
            mLayoutManager = new GridLayoutManager(rv.getContext(), getCurrentSpanCount());
            rv.setLayoutManager(mLayoutManager);
            rv.setHasFixedSize(true);

            mAdapter = new AlbumsAdapter(
                    getLayoutInflater(),
                    list,
                    ((sharedView, position) -> {
                        if (null != getActivity()) {
                            AlbumModel albumModel = list.get(position);
                            NavigationUtil.goToAlbum(getActivity(), sharedView, albumModel.getAlbumName(), albumModel.getAlbumId(), albumModel.getAlbumArt());
                        }
                    }),
                    () -> mLayoutManager.scrollToPosition(mFirstVisibleItemPosition),
                    resolveSortOrder(getCurrentSortOrder()));

            LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(rv.getContext(), R.anim.item_enter_slide_up);
            rv.setLayoutAnimation(controller);
            rv.setAdapter(mAdapter);
        }
    }
}
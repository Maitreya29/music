package com.hardcodecoder.pulsemusic.fragments.main;

import android.content.Context;
import android.content.res.Configuration;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.PulseController;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.adapters.main.TracksAdapter;
import com.hardcodecoder.pulsemusic.fragments.main.base.ListGridFragment;
import com.hardcodecoder.pulsemusic.helper.UIHelper;
import com.hardcodecoder.pulsemusic.interfaces.SimpleItemClickListener;
import com.hardcodecoder.pulsemusic.loaders.LoaderCache;
import com.hardcodecoder.pulsemusic.loaders.SortOrder;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.utils.SortUtil;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends ListGridFragment implements SimpleItemClickListener {

    public static final String TAG = "Library";
    private GridLayoutManager mLayoutManager;
    private TracksAdapter mAdapter;
    private SortOrder mSortOrder;
    private String mFragmentTitle = null;
    private int mFirstVisibleItemPosition;

    @NonNull
    public static LibraryFragment getInstance() {
        return new LibraryFragment();
    }

    @Override
    public void setUpContent(@NonNull View view) {
        List<MusicModel> list = LoaderCache.getAllTracksList();
        if (list == null || list.isEmpty()) {
            MaterialTextView noTracksText = (MaterialTextView) ((ViewStub) view.findViewById(R.id.stub_no_tracks_found)).inflate();
            noTracksText.setText(getString(R.string.tracks_not_found));
        } else {
            mSortOrder = resolveSortOrder(getCurrentSortOrder());
            TaskRunner.executeAsync(() -> {
                List<MusicModel> libraryTracks = new ArrayList<>(list);
                SortUtil.sortLibraryList(libraryTracks, mSortOrder);
                view.post(() -> loadLibrary(view, libraryTracks));
            });
        }
    }

    @Override
    public String getFragmentTitle(@NonNull Context context) {
        if (null == mFragmentTitle) mFragmentTitle = context.getString(R.string.nav_library);
        return mFragmentTitle;
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem sortItem = null;
        final int sortOrder = getCurrentSortOrder();

        if (sortOrder == Preferences.SORT_ORDER_ASC)
            sortItem = menu.findItem(R.id.menu_action_sort_title_asc);
        else if (sortOrder == Preferences.SORT_ORDER_DESC)
            sortItem = menu.findItem(R.id.menu_action_sort_title_desc);
        else if (sortOrder == Preferences.SORT_ORDER_DURATION_ASC)
            sortItem = menu.findItem(R.id.menu_action_sort_duration_asc);
        else if (sortOrder == Preferences.SORT_ORDER_DURATION_DESC)
            sortItem = menu.findItem(R.id.menu_action_sort_duration_desc);
        else if (sortOrder == Preferences.SORT_ORDER_DATE_MODIFIED_ASC)
            sortItem = menu.findItem(R.id.menu_action_sort_date_modified_asc);
        else if (sortOrder == Preferences.SORT_ORDER_DATE_MODIFIED_DESC)
            sortItem = menu.findItem(R.id.menu_action_sort_date_modified_desc);
        else if (sortOrder == Preferences.SORT_ORDER_DATE_ADDED_ASC)
            sortItem = menu.findItem(R.id.menu_action_sort_date_added_asc);
        else if (sortOrder == Preferences.SORT_ORDER_DATE_ADDED_DESC)
            sortItem = menu.findItem(R.id.menu_action_sort_date_added_desc);


        MenuItem spanItem = null;
        final int spanCount = getCurrentSpanCount();

        if (spanCount == 1)
            spanItem = menu.findItem(R.id.library_one);
        else if (spanCount == 2)
            spanItem = menu.findItem(R.id.library_two);
        else if (spanCount == 3)
            spanItem = menu.findItem(R.id.library_three);
        else if (spanCount == 4)
            spanItem = menu.findItem(R.id.library_four);

        if (sortItem != null)
            sortItem.setChecked(true);
        if (spanItem != null)
            spanItem.setChecked(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int groupId = item.getGroupId();
        if (groupId == R.id.group_library_sort) {
            final int id = item.getItemId();
            int sortOrder;

            if (id == R.id.menu_action_sort_title_asc)
                sortOrder = Preferences.SORT_ORDER_ASC;
            else if (id == R.id.menu_action_sort_title_desc)
                sortOrder = Preferences.SORT_ORDER_DESC;
            else if (id == R.id.menu_action_sort_duration_asc)
                sortOrder = Preferences.SORT_ORDER_DURATION_ASC;
            else if (id == R.id.menu_action_sort_duration_desc)
                sortOrder = Preferences.SORT_ORDER_DURATION_DESC;
            else if (id == R.id.menu_action_sort_date_added_asc)
                sortOrder = Preferences.SORT_ORDER_DATE_ADDED_ASC;
            else if (id == R.id.menu_action_sort_date_added_desc)
                sortOrder = Preferences.SORT_ORDER_DATE_ADDED_DESC;
            else if (id == R.id.menu_action_sort_date_modified_asc)
                sortOrder = Preferences.SORT_ORDER_DATE_MODIFIED_ASC;
            else if (id == R.id.menu_action_sort_date_modified_desc)
                sortOrder = Preferences.SORT_ORDER_DATE_MODIFIED_DESC;
            else
                sortOrder = Preferences.SORT_ORDER_ASC;

            changeSortOrder(sortOrder);

        } else if (groupId == R.id.group_library_grid) {
            final int id = item.getItemId();
            int spanCount;

            if (id == R.id.library_one)
                spanCount = 1;
            else if (id == R.id.library_two)
                spanCount = 2;
            else if (id == R.id.library_three)
                spanCount = 3;
            else if (id == R.id.library_four)
                spanCount = 4;
            else spanCount = 1;

            updateGridSpanCount(getCurrentOrientation(), spanCount);
        }

        item.setChecked(true);
        return true;
    }

    @Override
    public void onItemClick(int position) {
        PulseController controller = PulseController.getInstance();
        controller.setPlaylist(mAdapter.getDataList(), position);
        controller.getRemote().play();
    }

    @Override
    public void onOptionsClick(int position) {
        UIHelper.showMenuForLibraryTracks(requireActivity(), requireFragmentManager(), mAdapter.getDataList().get(position));
    }

    public void onSortUpdateComplete() {
        mLayoutManager.scrollToPosition(mFirstVisibleItemPosition);
    }

    @Override
    public int getMenuRes(int screenOrientation) {
        if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE)
            return R.menu.menu_library_land;
        return R.menu.menu_library;
    }

    private SortOrder resolveSortOrder(int sortOrder) {
        switch (sortOrder) {
            case Preferences.SORT_ORDER_ASC:
                return SortOrder.TITLE_ASC;
            case Preferences.SORT_ORDER_DESC:
                return SortOrder.TITLE_DESC;
            case Preferences.SORT_ORDER_DURATION_ASC:
                return SortOrder.DURATION_ASC;
            case Preferences.SORT_ORDER_DURATION_DESC:
                return SortOrder.DURATION_DESC;
            case Preferences.SORT_ORDER_DATE_ADDED_ASC:
                return SortOrder.DATE_ADDED_ASC;
            case Preferences.SORT_ORDER_DATE_ADDED_DESC:
                return SortOrder.DATE_ADDED_DESC;
            case Preferences.SORT_ORDER_DATE_MODIFIED_ASC:
                return SortOrder.DATE_MODIFIED_ASC;
            case Preferences.SORT_ORDER_DATE_MODIFIED_DESC:
                return SortOrder.DATE_MODIFIED_DESC;
        }
        return SortOrder.TITLE_DESC;
    }

    @Override
    public int getSortOrder() {
        if (null == getContext())
            return Preferences.SORT_ORDER_ASC;
        return AppSettings.getSortOrder(getContext(), Preferences.SORT_ORDER_LIBRARY_KEY);
    }

    @Override
    public void onSortOrderChanged(int newSortOrder) {
        if (null == mAdapter) return;
        mFirstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
        mAdapter.updateSortOrder(resolveSortOrder(newSortOrder));
        AppSettings.saveSortOrder(requireContext(), Preferences.SORT_ORDER_LIBRARY_KEY, newSortOrder);
    }

    @Override
    public int getPortraitModeSpanCount() {
        if (null == getContext())
            return Preferences.SPAN_COUNT_LIBRARY_PORTRAIT_DEF_VALUE;
        return AppSettings.getPortraitModeGridSpanCount(
                getContext(),
                Preferences.LIBRARY_SPAN_COUNT_PORTRAIT_KEY,
                Preferences.SPAN_COUNT_LIBRARY_PORTRAIT_DEF_VALUE);
    }

    @Override
    public int getLandscapeModeSpanCount() {
        if (null == getContext())
            return Preferences.SPAN_COUNT_LIBRARY_LANDSCAPE_DEF_VALUE;
        return AppSettings.getLandscapeModeGridSpanCount(
                getContext(),
                Preferences.LIBRARY_SPAN_COUNT_LANDSCAPE_KEY,
                Preferences.SPAN_COUNT_LIBRARY_LANDSCAPE_DEF_VALUE);
    }

    @Override
    public void saveNewSpanCount(int configId, int spanCount) {
        if (configId == Configuration.ORIENTATION_PORTRAIT)
            AppSettings.savePortraitModeGridSpanCount(requireContext(), Preferences.LIBRARY_SPAN_COUNT_PORTRAIT_KEY, spanCount);
        else if (configId == Configuration.ORIENTATION_LANDSCAPE)
            AppSettings.saveLandscapeModeGridSpanCount(requireContext(), Preferences.LIBRARY_SPAN_COUNT_LANDSCAPE_KEY, spanCount);
    }

    @Override
    public void onLayoutSpanCountChanged(int currentOrientation, int spanCount) {
        if (null == mLayoutManager) return;
        mLayoutManager.setSpanCount(spanCount);
    }

    private void loadLibrary(@NonNull View view, @NonNull List<MusicModel> list) {
        view.postOnAnimation(() -> {
            RecyclerView recyclerView = (RecyclerView) ((ViewStub) view.findViewById(R.id.stub_library_fragment_rv)).inflate();
            mLayoutManager = new GridLayoutManager(recyclerView.getContext(), getCurrentSpanCount());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setHasFixedSize(true);

            mAdapter = new TracksAdapter(
                    getLayoutInflater(),
                    list,
                    this,
                    this::onSortUpdateComplete,
                    mSortOrder,
                    true);
            recyclerView.setAdapter(mAdapter);
        });
    }
}
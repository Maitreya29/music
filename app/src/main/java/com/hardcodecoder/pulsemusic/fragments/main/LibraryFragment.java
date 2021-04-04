package com.hardcodecoder.pulsemusic.fragments.main;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewStub;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.adapters.main.TracksAdapter;
import com.hardcodecoder.pulsemusic.dialog.MenuDetailsDialog;
import com.hardcodecoder.pulsemusic.dialog.ToolbarContextMenuDialog;
import com.hardcodecoder.pulsemusic.fragments.main.base.ListGridFragment;
import com.hardcodecoder.pulsemusic.helper.DialogHelper;
import com.hardcodecoder.pulsemusic.interfaces.OptionsMenuListener;
import com.hardcodecoder.pulsemusic.interfaces.SimpleItemClickListener;
import com.hardcodecoder.pulsemusic.loaders.LoaderManager;
import com.hardcodecoder.pulsemusic.loaders.SortOrder;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.playback.PulseController;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.utils.SortUtil;
import com.hardcodecoder.pulsemusic.utils.ToolbarMenuBuilder;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends ListGridFragment implements SimpleItemClickListener, OptionsMenuListener {

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
        List<MusicModel> masterList = LoaderManager.getCachedMasterList();
        if (null == masterList || masterList.isEmpty()) {
            MaterialTextView noTracksText = (MaterialTextView) ((ViewStub) view.findViewById(R.id.stub_no_tracks_found)).inflate();
            noTracksText.setText(getString(R.string.tracks_not_found));
            return;
        }
        mSortOrder = resolveSortOrder(getCurrentSortOrder());
        TaskRunner.executeAsync(() -> {
            List<MusicModel> libraryTracks = new ArrayList<>(masterList);
            SortUtil.sortLibraryList(libraryTracks, mSortOrder);
            view.post(() -> loadLibrary(view, libraryTracks));
        });
    }

    @Override
    public void showOptionsMenu() {
        ToolbarContextMenuDialog toolbarMenuDialog = ToolbarMenuBuilder.buildDefaultOptionsMenu(
                requireActivity(),
                this,
                Preferences.SORT_ORDER_GROUP_LIBRARY,
                Preferences.COLUMN_COUNT_GROUP_LIBRARY);
        toolbarMenuDialog.show(requireFragmentManager(), ToolbarContextMenuDialog.TAG);
    }

    @Override
    public void onMenuDetailsDialogCreated(int groupId, @NonNull MenuDetailsDialog detailsDialog) {
        if (groupId == Preferences.SORT_ORDER_GROUP_LIBRARY)
            detailsDialog.setSelectedItemId(getCurrentSortOrder());
        else if (groupId == Preferences.COLUMN_COUNT_GROUP_LIBRARY)
            detailsDialog.setSelectedItemId(getCurrentColumnCount());
    }

    @Override
    public void onItemSelected(int groupId, int selectedItemId) {
        if (groupId == Preferences.SORT_ORDER_GROUP_LIBRARY)
            changeSortOrder(selectedItemId);
        else if (groupId == Preferences.COLUMN_COUNT_GROUP_LIBRARY)
            changeColumnCount(getCurrentOrientation(), selectedItemId);
    }

    @Override
    public String getFragmentTitle(@NonNull Context context) {
        if (null == mFragmentTitle) mFragmentTitle = context.getString(R.string.nav_library);
        return mFragmentTitle;
    }

    @Override
    public void onItemClick(int position) {
        PulseController pulseController = PulseController.getInstance();
        pulseController.getQueueManager().setPlaylist(mAdapter.getDataList(), position);
        pulseController.getRemote().play();
    }

    @Override
    public void onOptionsClick(int position) {
        DialogHelper.showMenuForLibraryTracks(requireActivity(), mAdapter.getDataList().get(position));
    }

    public void onSortUpdateComplete() {
        mLayoutManager.scrollToPosition(mFirstVisibleItemPosition);
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
    public int getPortraitModeColumnCount() {
        if (null == getContext())
            return Preferences.COLUMN_COUNT_ONE;
        return AppSettings.getPortraitModeColumnCount(
                getContext(),
                Preferences.COLUMN_COUNT_LIBRARY_PORTRAIT_KEY,
                Preferences.COLUMN_COUNT_ONE);
    }

    @Override
    public int getLandscapeModeColumnCount() {
        if (null == getContext())
            return Preferences.COLUMN_COUNT_TWO;
        return AppSettings.getLandscapeModeColumnCount(
                getContext(),
                Preferences.COLUMN_COUNT_LIBRARY_LANDSCAPE_KEY,
                Preferences.COLUMN_COUNT_TWO);
    }

    @Override
    public void onLayoutSpanCountChanged(int currentOrientation, int spanCount) {
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT)
            AppSettings.savePortraitModeColumnCount(requireContext(), Preferences.COLUMN_COUNT_LIBRARY_PORTRAIT_KEY, spanCount);
        else if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE)
            AppSettings.saveLandscapeModeColumnCount(requireContext(), Preferences.COLUMN_COUNT_LIBRARY_LANDSCAPE_KEY, spanCount);
        if (null == mLayoutManager) return;
        mLayoutManager.setSpanCount(spanCount);
    }

    private void loadLibrary(@NonNull View view, @NonNull List<MusicModel> list) {
        view.postOnAnimation(() -> {
            RecyclerView recyclerView = (RecyclerView) ((ViewStub) view.findViewById(R.id.stub_library_fragment_rv)).inflate();
            mLayoutManager = new GridLayoutManager(recyclerView.getContext(), getCurrentColumnCount());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setHasFixedSize(true);

            mAdapter = new TracksAdapter(
                    getLayoutInflater(),
                    list,
                    this,
                    this::onSortUpdateComplete,
                    mSortOrder);
            recyclerView.setAdapter(mAdapter);
        });
    }
}